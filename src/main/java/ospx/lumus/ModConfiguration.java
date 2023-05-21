package ospx.lumus;

import arc.Events;
import arc.func.*;
import arc.struct.Seq;
import arc.util.CommandHandler;
import arc.util.Log;
import mindustry.Vars;
import mindustry.core.GameState;
import mindustry.game.EventType;
import mindustry.gen.Player;
import party.iroiro.luajava.LuaException;

@SuppressWarnings("unused")
public class ModConfiguration {
    public ModMetadata meta = new ModMetadata();
    public boolean modInfoCommand = true;

    public Runnable onExit = () -> {};
    public Runnable onDispose = () -> {};
    public Runnable onInit = () -> {};

    public Seq<Command> clientCommands = new Seq<>();
    public Seq<Command> serverCommands = new Seq<>();

    public void registerClientCommand(String text, String params, String description, CommandHandler.CommandRunner<Player> runner) {
        clientCommands.add(new Command(text, params, description, runner));
    }

    public void registerClientCommand(String text, String params, String description,
                                      Boolf2<String[], Player> middleware,
                                      CommandHandler.CommandRunner<Player> runner) {
        registerClientCommand(text, params, description, (args, player) -> {
            if (middleware.get(args, player)) {
                runner.accept(args, player);
            }
        });
    }

    public void registerServerCommand(String text, String params, String description, Cons<String[]> runner) {
        serverCommands.add(new Command(text, params, description, (args, p) -> runner.get(args)));
    }

    public void registerServerCommand(String text, String params, String description, Boolf<String[]> middleware, Cons<String[]> runner) {
        registerServerCommand(text, params, description, (args) -> {
            if (middleware.get(args)) runner.get(args);
        });
    }

    public <T> void registerEventListener(String eventName, Cons<T> listener) {
        Class<T> type = Utils.getSubclassByName(EventType.class, eventName);

        if (type == null) throw new LuaException("EventType by name '" + eventName +"' not found");

        Events.on(type, listener);
    }

    public <T> void registerEventListener(Class<T> type, Cons<T> listener) {
        Events.on(type, listener);
    }

    public ModConfiguration() {}

    public void setModInfoCommand(boolean modInfoCommand) {
        this.modInfoCommand = modInfoCommand;
    }

    public Runnable getOnExit() {
        return onExit;
    }

    public void setOnExit(Runnable onExit) {
        this.onExit = onExit;
    }

    public Runnable getOnDispose() {
        return onDispose;
    }

    public void setOnDispose(Runnable onDispose) {
        this.onDispose = onDispose;
    }

    public Runnable getOnInit() {
        return onInit;
    }

    public void setOnInit(Runnable onInit) {
        this.onInit = onInit;
    }
}