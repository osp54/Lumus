package ospx.lumus;

import arc.func.Cons;
import arc.struct.Seq;
import arc.util.CommandHandler;
import mindustry.gen.Player;
@SuppressWarnings("unused")
public class ModConfiguration {
    public String name = "Unknown", description = "Unknown", version = "1.0";

    public Runnable onExit = () -> {};
    public Runnable onDispose = () -> {};
    public Runnable onInit = () -> {};

    public Seq<Command> clientCommands = new Seq<>();
    public Seq<Command> serverCommands = new Seq<>();

    public void registerClientCommand(String text, String params, String description, CommandHandler.CommandRunner<Player> runner) {
        clientCommands.add(new Command(text, params, description, runner));
    }

    public void registerServerCommand(String text, String params, String description, Cons<String[]> runner) {
        serverCommands.add(new Command(text, params, description, (args, p) -> runner.get(args)));
    }

    public ModConfiguration() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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