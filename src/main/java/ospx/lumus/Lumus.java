package ospx.lumus;

import arc.ApplicationListener;
import arc.Core;
import arc.files.Fi;
import arc.func.Cons;
import arc.struct.Seq;
import arc.util.CommandHandler;
import arc.util.Log;
import arc.util.Time;
import mindustry.mod.*;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.luajit.LuaJit;

import static mindustry.Vars.dataDirectory;
import static mindustry.Vars.modDirectory;

public class Lumus extends Plugin {
    public static Seq<LuaMod> mods = new Seq<>();
    public static Fi MODS_DIRECTORY = dataDirectory.child("luamods");

    public static Lua mainLua;

    public static Seq<Command> clientCommands = new Seq<>();
    public static Seq<Command> serverCommands = new Seq<>();

    public static boolean commandsRegistered = false;

    public Lumus() {
        Core.app.addListener(new ApplicationListener() {
            @Override
            public void exit() {
                modEach(LuaMod::callExit);
                mods.each(LuaMod::close);
            }

            @Override
            public void dispose() {
                modEach(LuaMod::callDispose);
                mods.each(LuaMod::close);
            }
        });
        MODS_DIRECTORY.mkdirs();

        Time.mark();
        try {
            mainLua = new LuaJit();
            new LuaGlobals(mainLua).pushStandardClasses();
        } catch (LinkageError e) {
            Log.err(e);
            Log.err("LuaJIT not loaded. Exiting");
            Core.app.exit();
        }

        Log.info("Time to load luajit: @", Time.elapsed());

        Seq<Fi> dirs = Seq.with(MODS_DIRECTORY.list()).filter(d -> d.isDirectory() && d.child("main.lua").exists());
        for (Fi dir : dirs) {
            try {
                long start = Time.millis();
                LuaMod mod = new LuaMod(dir);
                Log.info("Loaded lua mod '@' in @ms", dir.name(), Time.millis() - start);
                mods.add(mod);
            } catch (Exception e) {
                Log.err("An exception occurred while loading the mod @", dir.name());
                Log.err(e);
            }
        }
    }

    public static <E extends LuaMod> void modEach(Cons<E> consumer) {
        mods.each(m -> m.lastStatus == Lua.LuaError.OK || m.lastStatus == Lua.LuaError.RUNTIME, consumer);
    }

    @Override
    public void init() {
        modEach(LuaMod::callInit);
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {
        handler.register("luamods", "List lua mods", (args) -> {
            if (!mods.isEmpty()) {
                Log.info("Lua Mods:");
                for (LuaMod mod : mods) {
                    Log.info("  @ @ Status: @", mod.config.meta.name, mod.config.meta.version, mod.lastStatus);
                }
            } else {
                Log.info("No mods found.");
            }

            Log.info("Mod directory: @", modDirectory.absolutePath());
        });

        handler.register("lua", "<code...>", "Execute lua code", (args) -> {
            Lua.LuaError status = mainLua.run(args[0]);
            Utils.handleErrors(mainLua, status, "console.lua");
        });

        serverCommands.each(c -> handler.register(c.text, c.params, c.description, c.runner));
        commandsRegistered = true;
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        clientCommands.each(c -> handler.register(c.text, c.params, c.description, c.runner));
        commandsRegistered = true;
    }
}
