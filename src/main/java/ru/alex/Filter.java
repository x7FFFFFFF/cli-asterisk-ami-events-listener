package ru.alex;

import ru.alex.model.Field;

import java.util.regex.Pattern;

class Filter {
    private final String field;
    private final String value;
    private final Pattern pattern;
    Filter( String field, String value) {
        this.value = value;
        this.field = field;
        pattern = compile();
    }
    boolean match(String line){
        return pattern.matcher(line).matches();
    }

    Filter(Field opt) {
        this.value = opt.getValue();
        this.field = opt.getName();
        pattern = compile();
    }

    private Pattern compile() {
        String str = "(?i)^\\s*" +
                normalize(field) +
                "\\s*:\\s*" +
                normalize(value) +
                "\\s*$";
        return Pattern.compile(str);
    }


    private String normalize(String field) {
        return field.trim()
                .toLowerCase();
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }

    public Pattern getPattern() {
        return pattern;
    }
}
