package ru.alex.model;

import java.util.List;

import static ru.alex.model.Message.Constants.EVENT;

public class Event extends Message {
    public Event(List<Field> fields) {
        super(fields);
    }

    public Event(Field... fields) {
        super(fields);
    }

    public Event(String row) {
        super(row);
    }

    @Override
    protected String[] printFirst() {
        return new String[]{EVENT};
    }

    @Override
    public Type getType() {
        return Type.EVENT;
    }
}
