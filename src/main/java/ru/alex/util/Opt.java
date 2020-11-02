package ru.alex.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import ru.alex.model.Field;

import java.util.*;

import static java.util.stream.Collectors.toList;

public  enum Opt {
    USER("u", "user", "Manager username from manager.conf. Default - " + Constants.ADMIN, null, Constants.ADMIN, false),
    PASSWORD("s", "secret", "Manager password from manager.conf.", null, null, true),
    HOST("h", "host", "Ami server ip/name. Default - " + Constants.LOCALHOST, null, Constants.LOCALHOST, false),
    PORT("p", "port", "Ami server port. Default - " + Constants.PORT, null, Constants.PORT, false),
    SUBSCRIBE("l", "listen", Constants.SUBSCRIBE_DESC, ',', Constants.SYSTEM, false),
    FILTER("f", "filter", Constants.FILTER_DESCR, ':', null, false);

    private final String shortName;
    private final String longName;
    private final String descr;
    private final Character sep;
    private final String defaultValue;
    private final boolean required;

    Opt(String shortName, String longName, String descr, Character sep, String defaultValue, boolean required) {
        this.shortName = shortName;
        this.longName = longName;
        this.descr = descr;
        this.sep = sep;
        this.defaultValue = defaultValue;
        this.required = required;
    }

    public List<Field> getAllFields(CommandLine line) {
        Objects.requireNonNull(sep);
        return getValues(line).stream().map(Field::new).collect(toList());
    }

    private List<String> getValues(CommandLine line) {
        return Optional.ofNullable(line.getOptionValues(shortName)).map(Arrays::asList).orElse(Collections.emptyList());
    }

    public List<String> getAll(CommandLine line) {
        return getValues(line);
    }

    public String getFirst(CommandLine line) {
        return Optional.ofNullable(line.getOptionValue(shortName)).orElse(defaultValue);
    }

    private Option option() {
        final Option.Builder builder = Option.builder(shortName).longOpt(longName);
        if (sep != null) {
            builder.valueSeparator(sep);
        }
        return builder.required(required).hasArg().desc(descr).build();
    }

    public static Options create() {
        final Options options = new Options();
        for (Opt opt : values()) {
            options.addOption(opt.option());
        }
        return options;
    }


    @Override
    public String toString() {
        return shortName;
    }

    private static class Constants {
        public static final String ADMIN = "admin";
        public static final String LOCALHOST = "localhost";
        public static final String PORT = "5038";
        public static final String SYSTEM = "system";
        public static final String SUBSCRIBE_DESC = "Subscribe on events:\n" +
                "(system,call,log,verbose,command,agent,user,config,command,dtmf,reporting,cdr,dialplan,originate,message)\n" +
                " Example: '-s call,cdr'. Default: " + SYSTEM;
        public static final String FILTER_DESCR = "Event's header filter. Default - case insensitivity" +
                "Serach by regexp pattern. \n " +
                "Example:  -f 'event: status' -f '.*caller.*: [0-9]{3}'";
    }
}
