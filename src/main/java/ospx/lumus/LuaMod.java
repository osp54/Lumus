package ospx.lumus;

import arc.files.Fi;
import arc.util.CommandHandler;
import arc.util.Log;
import arc.util.Strings;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.luajit.LuaJit;
import party.iroiro.luajava.value.LuaValue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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

        LuaGlobals.init(context);
        context.push(root.absolutePath());
        context.setGlobal("__dir");

        context.push(file.absolutePath());
        context.setGlobal("__file");

        context.run(Strings.format("package.path = '@/?.lua;' .. package.path\n", root.absolutePath()));
        lastStatus = context.run(file.readString());

        if (lastStatus == Lua.LuaError.OK) {
            callFunction("configure", config);
        }

        handleErrors();
    }
    public void handleErrors() {
        handleErrors(lastStatus);
    }
    public void handleErrors(Lua.LuaError status) {
        if (status == Lua.LuaError.OK) return;

        if (context.getTop() != 0 && context.isString(-1)) {
            Log.err("An exception occurred while loading the mod @\n@", root.name(), context.toString(-1));
        }
        context.setTop(0);
        Throwable e = context.getJavaError();
        if (e != null) {
            Log.err("Last Java side exception:");
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            PrintStream print = new PrintStream(output);
            e.printStackTrace(print);
            print.flush();
            Log.err(output.toString());
            context.error((Throwable) null);
        }
    }

    public void registerServerCommands(CommandHandler handler) {
        for (Command command : config.serverCommands) {
            handler.register(command.text, command.params, command.description, command.runner);
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
