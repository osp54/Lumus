package ospx.lumus;

import arc.files.Fi;
import arc.util.CommandHandler;
import arc.util.Log;
import arc.util.Strings;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.luajit.LuaJit;
import party.iroiro.luajava.value.LuaValue;

import static ospx.lumus.Utils.handleErrors;

public class LuaMod {
    public Fi root;
    public Fi file;

    public Lua context;
    public ModConfiguration config = new ModConfiguration();

    public Lua.LuaError lastStatus;

    public LuaMod(Fi root) {
        this.root = root;
        this.file = root.child("main.lua");

        context = new LuaJit();
        context.openLibraries();

        new LuaGlobals(context).init();
        context.push(root.absolutePath());
        context.setGlobal("__dir");

        context.push(file.absolutePath());
        context.setGlobal("__file");

        context.run(Strings.format("package.path = '@/?.lua;' .. package.path\n", root.absolutePath()));
        lastStatus = context.run(file.readString());


        if (lastStatus == Lua.LuaError.OK) {
            callFunction("configure", config);
        }

        handleError();
    }
    public void handleError() {
        handleErrors(context, lastStatus, root.name());
    }

    public void registerServerCommands(CommandHandler handler) {
        for (Command command : config.serverCommands) {
            handler.register(command.text, command.params, command.description, command.runner);
        }

        if (!config.meta.name.equals("Unknown") && config.modInfoCommand) {
            handler.register(config.meta.name, "Plugin " + config.meta.displayName, args -> {
                Log.info("@ @", config.meta.displayName, config.meta.version);
                Log.info(config.meta.description);
                Log.info("Server Commands:");
                for (Command command : config.serverCommands) {
                    Log.info("  @ @ @", command.text, command.params, command.description);
                }
                Log.info("Client Commands:");
                for (Command command : config.clientCommands) {
                    Log.info("  @ @ @", command.text, command.params, command.description);
                }
            });
        }
    }
    public void registerClientCommands(CommandHandler handler) {
        for (Command command : config.clientCommands) {
            handler.register(command.text, command.params, command.description, command.runner);
        }
    }

    public void callFunction(String name, Object... parameters) {
        LuaValue func = context.get(name);

        if (func.type() != Lua.LuaType.FUNCTION) return;
        lastStatus = func.call(parameters) == null ? Lua.LuaError.RUNTIME : Lua.LuaError.OK;
    }

    public void callInit() {
        config.onInit.run();
    }

    public void callExit() {
        config.onExit.run();
    }

    public void callDispose() {
        config.onDispose.run();
    }

    public void close() {
        context.close();
    }
}
