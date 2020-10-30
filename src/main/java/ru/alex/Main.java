package ru.alex;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class Main {
    final Options options;

    public Main() {
        options = Opt.create();
    }


    public static void main(String[] args) {
        final Main main = new Main();


    }

    static class OptWithSep {
        final String field;
        final String value;

        public OptWithSep(String field, String sep) {
            String[] split = field.split(sep);
            assert split.length == 2;
            this.field = split[0];
            this.value = split[1];
        }
    }

    enum Opt {
        USER("u", "user", "Manager username from manager.conf. Default - " + Constants.ADMIN, null, Constants.ADMIN),
        PASSWORD("p", "password", "Manager password from manager.conf.", null, null),
        HOST("h", "host", "Ami server ip/name. Default - " + Constants.LOCALHOST, null, Constants.LOCALHOST),
        SUBSCRIBE("s", "subscribe", "Subscribe on events:\n" +
                "(system,call,log,verbose,command,agent,user,config,command,dtmf,reporting,cdr,dialplan,originate,message)\n" +
                " Example: '-s call,cdr'. Default: "+ Constants.SYSTEM, ',', Constants.SYSTEM),
        FILTER("f", "filter", "Event's header filter. Default - case insensitivity" +
                "Serach by regexp pattern. \n " +
                "Example:  -f 'event: status' -f '.*caller.*: [0-9]{3}'", ':', null);

        private final String shortName;
        private final String longName;
        private final String descr;
        private final Character sep;
        private final String defaultValue;

        Opt(String shortName, String longName, String descr, Character sep, String defaultValue) {
            this.shortName = shortName;
            this.longName = longName;
            this.descr = descr;
            this.sep = sep;
            this.defaultValue = defaultValue;
        }

        List<OptWithSep> getAllFields(CommandLine line) {
            Objects.requireNonNull(sep);
            return getValues(line).stream().map(v -> new OptWithSep(v, sep.toString())).collect(toList());
        }

        private List<String> getValues(CommandLine line) {
            return Optional.ofNullable(line.getOptionValues(shortName)).map(Arrays::asList).orElse(Collections.emptyList());
        }

        List<String> getAll(CommandLine line) {
            return getValues(line);
        }

        String getFirst(CommandLine line) {
            return Optional.ofNullable(line.getOptionValue(shortName)).orElse(defaultValue);
        }

        private Option option() {
            final Option.Builder builder = Option.builder(shortName).longOpt(longName);
            if (sep != null) {
                builder.valueSeparator(sep);
            }
            return builder.hasArg().desc(descr).build();
        }

        static Options create() {
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
            public static final String SYSTEM = "system";
        }
    }
}
