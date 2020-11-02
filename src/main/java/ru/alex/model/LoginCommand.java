package ru.alex.model;

public class LoginCommand extends Command {
    public static final String USERNAME = "Username";
    public static final String SECRET = "Secret";
    public static final String ACTION = "Login";
    public static final Field LOGIN = Field.action(ACTION);

    public LoginCommand(String name, String pass) {
        super(LOGIN, new Field(USERNAME, name), new Field(SECRET, pass));

    }

}
