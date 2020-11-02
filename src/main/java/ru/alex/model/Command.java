package ru.alex.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ru.alex.model.Message.Constants.*;


public class Command extends Message {
    public static final String[] FIRST = {ACTION, ACTION_ID};

    public Command(Field... fields) {
        super(getNewActionId(), fields);
    }

    public Command(List<Field> fields) {
        super(fields);
        getFields().put(ACTION_ID, getNewActionId());
    }

    public Command(String row) {
        super(row);
        getFields().put(ACTION_ID, getNewActionId());
    }

    private static Field getNewActionId() {
        return new Field(ACTION_ID, generateActionId());
    }

    private static String generateActionId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public String getAction() {
        return Optional.ofNullable(getFields().get(ACTION))
                .map(Field::getValue).orElseThrow(() -> new IllegalStateException("Action not found in command:" + this));
    }

    @Override
    protected String[] printFirst() {
        return FIRST;
    }

    @Override
    public Type getType() {
        return Type.ACTION;
    }
}
