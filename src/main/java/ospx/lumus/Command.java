package ospx.lumus;

import arc.util.CommandHandler;
import party.iroiro.luajava.Lua;

import java.util.Map;

public class Command {
    String text, params, description;
    CommandHandler.CommandRunner runner;
    CommandType type;

    public Command(String text, String params, String description, CommandHandler.CommandRunner runner) {
        this.text = text;
        this.params = params;
        this.description = description;
        this.runner = runner;
    }

    public Command() {}

    enum CommandType {
        CLIENT, SERVER
    }
}
