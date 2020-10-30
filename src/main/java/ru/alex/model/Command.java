package ru.alex.model;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Command {
    public static final String DELIM = "\r\n";
    public static final Pattern DELIM_PATTERN = Pattern.compile(DELIM);
    public static final Pattern COMMAND_END_PATTERN = Pattern.compile(DELIM + DELIM);

    public static final String ACTION = "Action";
    public static final String LOGOFF = "Logoff";

    private final Field action;
    private final Map<String, Field> fields;

    public Command(Field... fields) {
        this.fields = collectFields(Stream.of(fields));
        this.action = getField(ACTION);
    }

    public Command(String row) {
        fields = collectFields(Stream.of(DELIM_PATTERN.split(row)).map(Field::new));
        action = getField(ACTION);
    }


    private Map<String, Field> collectFields(Stream<Field> stream) {
        return stream.filter(Field::isValid).collect(Collectors.toMap(Field::getName, f -> f, (u, v) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                },
                LinkedHashMap::new));
    }

    protected Field getField(String name) {
        return Objects.requireNonNull(fields.remove(name));
    }

    protected Field peekField(String name) {
        return fields.get(name);
    }

    public boolean isLogoff() {
        return action.getValue().equals(LOGOFF);
    }

    public Field getAction() {
        return action;
    }

    public Map<String, Field> getFields() {
        return fields;
    }


    public void print(PrintWriter out) {
        out.print(action);
        fields.values().forEach(out::print);
        out.print(DELIM);
        out.flush();
    }

    @Override
    public String toString() {
        final StringWriter out = new StringWriter();
        final PrintWriter writer = new PrintWriter(out);
        print(writer);
        return out.toString();
    }
}
