package ru.alex.model;

public class EventsCommand extends Command {
    public static EventsCommand create(String mask) {
        return new EventsCommand(Field.action("Events"), new Field("EventMask", mask));
    }

    public EventsCommand(Field... fields) {
        super(fields);
    }

    public EventsCommand(String row) {
        super(row);
    }
}
