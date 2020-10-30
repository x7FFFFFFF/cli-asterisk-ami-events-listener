package ru.alex.model;

public class Login extends Command {
    public static final String USERNAME = "Username";
    public static final String SECRET = "Secret";
    public static final String ACTION = "Login";
    public static final Field LOGIN = Field.action(ACTION);
    private final Field username;
    private final Field secret;

    public Login(String name, String pass) {
        super(LOGIN, new Field(USERNAME, name), new Field(SECRET, pass));
        username = peekField(USERNAME);
        secret = peekField(SECRET);
    }

    public Login(String row) {
        super(row);
        username = peekField(USERNAME);
        secret = peekField(SECRET);
    }

    public Field getUsername() {
        return username;
    }

    public Field getSecret() {
        return secret;
    }
}
