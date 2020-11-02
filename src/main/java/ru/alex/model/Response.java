package ru.alex.model;

import java.util.List;

import static ru.alex.model.Message.Constants.*;

public class Response extends Message {

    public static final String[] FIRST = {RESPONSE, MESSAGE, ACTION_ID};
    public static final String SUCCESS = "Success";

    public Response(List<Field> fields) {
        super(fields);
    }

    public Response(Field... fields) {
        super(fields);
    }

    public Response(String row) {
        super(row);
    }

    public Response(String staus, String msg, String actionId) {
        super(new Field(RESPONSE, staus), new Field[]{new Field(MESSAGE, msg), new Field(ACTION_ID, actionId)});
    }

    @Override
    protected String[] printFirst() {
        return FIRST;
    }

    @Override
    public Type getType() {
        return Type.RESPONSE;
    }

    public boolean isOk() {
        return getFieldValue(RESPONSE).equalsIgnoreCase(SUCCESS);
    }
}
