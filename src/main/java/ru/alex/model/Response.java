package ru.alex.model;

public class Response extends Command {
    public static final String RESPONSE = "Response";
    public static final String MESSAGE = "Message";
    public static final String SUCCESS = "Success";
    public static final String GOODBYE = "Goodbye";
    public static final String EVENT = "Event";
    private Field response;
    private Field message;
    public static final Response SUCCESS_RESP = new Response(SUCCESS, "Message");
    public static final Response GOODBYE_RESP = new Response(GOODBYE, "Message");

    public Response(String response, String message) {
        super(new Field(RESPONSE, response), new Field(MESSAGE, message));
    }
    public Response(Field... fields) {
        super(fields);
        response = peekField(RESPONSE);
        message = peekField(MESSAGE);
    }
    public Response(String row) {
        super(row);
        response = peekField(RESPONSE);
        message = peekField(MESSAGE);
    }

    @Override
    protected Field getField(String name) {
        return Field.EMPTY;
    }
    public boolean isEvent() {
        return getFields().containsKey(EVENT);
    }

    public boolean isOk() {
        return response != null && response.getValue().equals(SUCCESS) || isGoodbye();
    }

    public boolean isGoodbye() {
        return response != null && response.getValue().equals(GOODBYE);
    }

    public Field getResponse() {
        return response;
    }

    public void setResponse(Field response) {
        this.response = response;
    }

    public Field getMessage() {
        return message;
    }

    public void setMessage(Field message) {
        this.message = message;
    }
}
