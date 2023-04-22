package ospx.lumus;

import arc.util.CommandHandler;

public class Command {
    final String text, params, description;
    final CommandHandler.CommandRunner runner;

    public Command(String text, String params, String description, CommandHandler.CommandRunner runner) {
        this.text = text;
        this.params = params;
        this.description = description;
        this.runner = runner;
    }
}
