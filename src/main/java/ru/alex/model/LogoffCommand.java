package ru.alex.model;

public class LogoffCommand extends Command {

    public static final String ACTION = "Logoff";

    public LogoffCommand() {
        super(Field.action(ACTION));
    }

    public LogoffCommand(String row) {
        super(row);
    }
}
