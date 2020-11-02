package ru.alex.model;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.alex.model.Message.Constants.ACTION_ID;


public abstract class Message {


    public static final class Constants {
        public static final String ACTION_ID = "ActionID";
        public static final String ACTION = "Action";

        public static final String RESPONSE = "Response";
        public static final String MESSAGE = "Message";

        public static final String EVENT = "Event";

        public static final String DELIM = "\r\n";
        public static final Pattern DELIM_PATTERN = Pattern.compile(DELIM);
        public static final Pattern MESSAGE_END_PATTERN = Pattern.compile(DELIM + DELIM);
    }

    public enum Type {
        ACTION(Constants.ACTION_ID), RESPONSE(Constants.RESPONSE), EVENT(Constants.EVENT);

        private final String start;

        public static Type parse(String line) {
            return Stream.of(values()).filter(v -> line.startsWith(v.start))
                    .findFirst().orElseThrow(() -> new IllegalArgumentException("Cant parse " + line));
        }

        Type(String start) {
            this.start = start;
        }

        public Message create(List<Field> fields) {
            switch (this) {
                case EVENT:
                    return new Event(fields);
                case ACTION:
                    return new Command(fields);
                case RESPONSE:
                    return new Response(fields);
                default:
                    throw new IllegalArgumentException(name());
            }
        }
    }

    public static Message create(List<Field> fields) {
        assert fields.size() >= 1;
        final Type type = Type.parse(fields.get(0).toString());
        return type.create(fields);
    }

    public Message remove(Field field) {
        final LinkedHashMap<String, Field> newMap = new LinkedHashMap<>(fields);
        newMap.remove(field.getName());
        return getType().create(new ArrayList<>(newMap.values()));
    }

    private final Map<String, Field> fields;

    public Message(Field field, Field... fields) {
        final List<Field> list = new ArrayList<>(Arrays.asList(fields));
        list.add(field);
        this.fields = collectFields(list.stream());
    }

    public Message(Field... fields) {
        this.fields = collectFields(Stream.of(fields));
    }

    public Message(List<Field> fields) {
        this.fields = collectFields(fields.stream());
    }

    public Message(String row) {
        fields = collectFields(Stream.of(Constants.DELIM_PATTERN.split(row)).map(Field::new));
    }


    public abstract Type getType();

    public Optional<String> getActionId() {
        return Optional.ofNullable(getFields().get(ACTION_ID))
                .map(Field::getValue);
    }

    public boolean isResponse() {
        return getType() == Type.RESPONSE;
    }

    public boolean isEvent() {
        return getType() == Type.EVENT;
    }

    public boolean isResponseFor(Message message) {
        final Optional<String> thisActionId = getActionId();
        final Optional<String> otherActionId = message.getActionId();
        if (otherActionId.isPresent() && thisActionId.isPresent() && message.isResponse()) {
            return otherActionId.get().equals(thisActionId.get());
        }
        return false;
    }


    private Map<String, Field> collectFields(Stream<Field> stream) {
        return stream.filter(Field::isValid).collect(Collectors.toMap(Field::getName, f -> f, (u, v) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                },
                LinkedHashMap::new));
    }

    protected Field removeField(String name) {
        return Objects.requireNonNull(fields.remove(name));
    }

    protected String[] printFirst() {
        return new String[]{};
    }

    public Map<String, Field> getFields() {
        return fields;
    }

    public String getFieldValue(String name) {
        return fields.get(name).getValue();
    }

    public void print(PrintWriter out) {
        final Set<String> set = new HashSet<>();
        for (String name : printFirst()) {
            set.add(name);
            out.print(getField(name));
        }
        for (Field field : fields.values()) {
            if (set.contains(field.getName())) {
                continue;
            }
            out.print(field);
        }
        out.print(Constants.DELIM);
        out.flush();
    }

    private Field getField(String name) {
        return getFields().get(name);
    }

    @Override
    public String toString() {
        final StringWriter out = new StringWriter();
        final PrintWriter writer = new PrintWriter(out);
        print(writer);
        return out.toString();
    }


}
