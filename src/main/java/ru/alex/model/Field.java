package ru.alex.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static ru.alex.model.Message.Constants.*;

public class Field {
    public static final String DELIM = ":";
    private static final Pattern COLON_PATTERN = Pattern.compile(DELIM);
    public static Field EMPTY = new Field("");
    final String name;
    final String value;
    final boolean valid;

    public Field(String rowStr) {
        String[] split = COLON_PATTERN.split(rowStr);
        valid = split.length == 2;
        if (valid) {
            this.name = split[0].trim();
            this.value = split[1].trim();
        } else {
            this.name = null;
            this.value = null;
        }
    }

    public Field(String name, String value) {
        this.name = name.trim();
        this.value = value.trim();
        valid = true;
    }

    public static List<Field> read(BufferedReader reader) {
        String line;
        final List<Field> fields = new ArrayList<>(30);
        try {
            while (!(line = reader.readLine()).isEmpty()) {
                fields.add(new Field(line));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fields;
    }

    public static Field action(String name) {
        return new Field(ACTION, name);
    }

    public boolean isValid() {
        return valid;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        if (!valid) {
            return "";
        }
        return name + DELIM + " " + value + Message.Constants.DELIM;
    }


}