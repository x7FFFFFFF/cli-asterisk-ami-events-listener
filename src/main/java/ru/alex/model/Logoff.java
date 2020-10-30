package ru.alex.model;

public class Logoff extends Command {
    public static final String ACTION = "Logoff";
    public Logoff() {
        super(Field.action(ACTION));
    }

    public Logoff(String row) {
        super(row);
    }
}
