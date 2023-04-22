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

import static mindustry.Vars.dataDirectory;

public class Main extends Plugin {
    public static Seq<LuaMod> mods = new Seq<>();
    public static Fi MODS_DIRECTORY = dataDirectory.child("luamods");

    public Main() {
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
        modEach(m -> m.registerServerCommands(handler));
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        modEach(m -> m.registerClientCommands(handler));
    }
}
