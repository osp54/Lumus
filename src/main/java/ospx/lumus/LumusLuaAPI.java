package ospx.lumus;

import arc.Events;
import arc.func.Boolf;
import arc.func.Boolf2;
import arc.func.Cons;
import arc.util.CommandHandler;
import arc.util.Strings;
import mindustry.game.EventType;
import mindustry.server.ServerControl;
import party.iroiro.luajava.Lua;

import static mindustry.Vars.netServer;

public class LumusLuaAPI {
    public static int command(Lua l, Command.CommandType type) {
        if (!l.isTable(1)) {
            l.error(Strings.format("bad argument #1 to 'commands.@Command' (expecting an table)",
                    type == Command.CommandType.CLIENT ? "client" : "server"));
            return 0;
        }
        Command result = new Command();

        l.getField(1, "name");
        result.text = l.toString(-1);

        l.getField(1, "params");
        String params = l.toString(-1);
        result.params = params == null ? "" : params;

        l.getField(1, "description");
        result.description = l.toString(-1);

        if (result.text == null) {
            l.error("bad argument #1 to 'commands.clientCommand' (name cannot be nil)");
        }
        result.type = type;

        Boolf2 middleware;
        l.getField(1, "middleware");
        if (!l.isNil(-1)) {
            middleware = (Boolf2) l.createProxy(new Class[] {Boolf2.class}, Lua.Conversion.SEMI);
        } else {
            middleware = null;
        }

        l.getField(1, "handler");
        result.runner = (CommandHandler.CommandRunner) l.createProxy(new Class[] {CommandHandler.CommandRunner.class}, Lua.Conversion.SEMI);
        CommandHandler.CommandRunner runner = result.runner;

        if (middleware != null) {
            result.runner = (args, player) -> {
                if (middleware.get(args, player)) runner.accept(args, player);
            };
        }

        if (Lumus.commandsRegistered) {
            switch (type) {
                case CLIENT -> netServer.clientCommands.register(result.text, result.params, result.description, result.runner);
                case SERVER -> ServerControl.instance.handler.register(result.text, result.params, result.description, result.runner);
            }
        } else {
            switch (type) {
                case CLIENT -> Lumus.clientCommands.add(result);
                case SERVER -> Lumus.serverCommands.add(result);
            }
        }

        return 1;
    }

    public static int event(Lua l) {
        Class<?> type;
        if (l.isJavaObject(1)) {
            type = (Class<?>) l.toJavaObject(1);
        } else if (l.isString(1)) {
            type = Utils.getSubclassByName(EventType.class, l.toString(1));
        } else {
            l.error("bad argument #1 to 'events.eventListener' (expecting an string or jclass)");
            return 0;
        }

        if (type == null) {
            l.error("bad argument #1 to 'events.eventListener' (event not found)");
            return 0;
        }

        if (!l.isFunction(2)) {
            l.error("bad argument #2 to 'events.eventListener' (expecting an function)");
            return 0;
        }

        if (l.getTop() > 2) {
            l.error("bad arguments to 'events.eventListener' (more than 2 arguments)");
            return 0;
        }

        Cons listener = (Cons) l.createProxy(new Class[] {Cons.class}, Lua.Conversion.SEMI);

        Events.on(type, listener);
        return 1;
    }

    public static int setOnInit(Lua l, ModConfiguration config) {
        if (!l.isFunction(1)) {
            l.error("bad argument #1 to 'events.onInit' (expecting an function)");
            return 0;
        }

        config.onInit = (Runnable) l.createProxy(new Class[] {Runnable.class}, Lua.Conversion.SEMI);
        return 1;
    }

    public static int setOnExit(Lua l, ModConfiguration config) {
        if (!l.isFunction(1)) {
            l.error("bad argument #1 to 'events.onExit' (expecting an function)");
            return 0;
        }

        config.onExit = (Runnable) l.createProxy(new Class[] {Runnable.class}, Lua.Conversion.SEMI);
        return 1;
    }

    public static int setOnDispose(Lua l, ModConfiguration config) {
        if (!l.isFunction(1)) {
            l.error("bad argument #1 to 'events.onDispose' (expecting an function)");
            return 0;
        }

        config.onDispose = (Runnable) l.createProxy(new Class[] {Runnable.class}, Lua.Conversion.SEMI);
        return 1;
    }

    public static int pluginConfiguration(Lua l, ModConfiguration config) {
        if (!l.isTable(1)) {
            l.error("bad argument #1 to 'pluginConfiguration' (expecting an table)");
            return 0;
        }

        l.getField(1, "meta");
        if (!l.isTable(-1)) {
            l.error("bad argument #1 to 'pluginConfiguration' (meta must be table)");
            return 0;
        }
        ModMetadata meta = new ModMetadata();

        l.getField(-1, "displayName");
        meta.displayName = l.toString(-1);
        l.pop(1);

        l.getField(-1, "name");
        meta.name = l.toString(-1);
        l.pop(1);

        l.getField(-1, "version");
        meta.version = l.toString(-1);
        l.pop(1);

        l.getField(-1, "description");
        meta.description = l.toString(-1);

        config.meta = meta;
        return 1;
    }
}
