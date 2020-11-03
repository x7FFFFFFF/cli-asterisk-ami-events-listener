package ru.alex;

import ru.alex.model.Field;

import java.util.Map;
import java.util.regex.Pattern;

public class Filter {
    private final String name;
    private final String value;
    private final boolean nameReverse;
    private final boolean valueReverse;
    private final Pattern patternName;
    private final Pattern patternValue;

    public Filter(String name, String value) {
        nameReverse = name.startsWith("!");
        valueReverse = value.startsWith("!");
        this.value = normalize2(value);
        this.name = normalize2(name);
        patternName = compile(this.value);
        patternValue = compile(this.name);
    }

    private String normalize2(String value) {
        return value.startsWith("!") ? value.substring(1) : value;
    }

    private Pattern compile(String field) {
        return Pattern.compile("(?i)^\\s*" +
                normalize(field) +
                "\\s*");
    }

    public boolean match(Map<String, Field> fields) {
        return fields.values().stream().anyMatch(this::isMatch);
    }

    private boolean isMatch(Field field) {
        return (nameReverse != isMatches(patternName, field.getName())) &&
                (valueReverse != isMatches(patternValue, field.getValue()));
    }

    private boolean isMatches(Pattern patternField, String name) {
        return patternField.matcher(name).matches();
    }

    public Filter(Field opt) {
        this.value = normalize2(opt.getValue());
        this.name = normalize2(opt.getName());
        patternName = compile(name);
        patternValue = compile(value);
        nameReverse = opt.getName().startsWith("!");
        valueReverse = opt.getValue().startsWith("!");
    }

    private String normalize(String field) {
        return field.trim()
                .toLowerCase();
    }
}
