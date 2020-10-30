package ru.alex.model;

public class Events extends Command {
    public static Events create(String mask) {
        return new Events(Field.action("Events"), new Field("EventMask", mask));
    }

    public Events(Field... fields) {
        super(fields);
    }

    public Events(String row) {
        super(row);
    }
}
