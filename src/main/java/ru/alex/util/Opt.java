package ru.alex.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import ru.alex.model.Command;
import ru.alex.model.Message;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public enum Opt {
    USER("u", "user", "Manager username from manager.conf. Default - " + Constants.ADMIN, null, Constants.ADMIN, false),
    PASSWORD("s", "secret", "Manager password from manager.conf.", null, null, true),
    HOST("h", "host", "Ami server ip/name. Default - " + Constants.LOCALHOST, null, Constants.LOCALHOST, false),
    PORT("p", "port", "Ami server port. Default - " + Constants.PORT, null, Constants.PORT, false),
    SUBSCRIBE("l", "listen", Constants.SUBSCRIBE_DESC, ',', Constants.ALL, false),
    MACROS("m", "macros", "File with macroses for star commands like '*79#<ext>'", ',', Constants.ALL, false),
    FILTER("f", "filter", Constants.FILTER_DESCR, ':', null, false) {
        @Override
        public Predicate<Message> getFilterPredicate(CommandLine line) {
            Objects.requireNonNull(sep);
            final List<Predicate<Message>> filters = getValues(line).stream().map(JsFilter::new).map(JsFilter::predicate).collect(toList());
            if (filters.isEmpty()) {
                return ALL_PREDICATE;
            }
            return message -> filters.stream().allMatch(f -> f.test(message));
        }
    },
    EXECUTE_COMMANDS("e", "execute", Constants.EXECUTE_DESC, ';', null, false) {
        @Override
        public List<Command> getCommands(CommandLine line) throws FileNotFoundException {
            final String s = sep.toString();
            final List<Macro> list = getValues(line).stream().flatMap(c -> Stream.of(c.trim().split(s))).map(Macro::new)
                    .collect(toList());
            if (list.isEmpty()) {
                return Collections.emptyList();
            }
            final Macros macros = getMacros(line);
            return list.stream().flatMap(m -> macros.render(m.name, m.args).stream())
                    .collect(Collectors.toList());
        }

        private Macros getMacros(CommandLine line) throws FileNotFoundException {
            final String file = getFirst(line, MACROS.shortName, null);
            if (file != null) {
                return new Macros(new FileInputStream(file));
            }
            return new Macros(getClass().getResourceAsStream(Macros.DEFAULT));
        }

        class Macro {
            final String name;
            final String[] args;

            public Macro(String str) {
                final String[] split = str.split("#");
                name = split[0];
                args = split.length == 2 ? split[1].split(",") : new String[]{};
            }
        }
    };

    public static final Predicate<Message> ALL_PREDICATE = msg -> true;
    protected final String shortName;
    private final String longName;
    private final String descr;
    protected final Character sep;
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

    public Predicate<Message> getFilterPredicate(CommandLine line) {
        throw new UnsupportedOperationException();
    }

    public List<Command> getCommands(CommandLine line) throws FileNotFoundException {
        throw new UnsupportedOperationException();
    }

    protected List<String> getValues(CommandLine line) {
        return getValues(line, shortName);
    }

    protected List<String> getValues(CommandLine line, String name) {
        return Optional.ofNullable(line.getOptionValues(name)).map(Arrays::asList).orElse(Collections.emptyList());
    }

    public List<String> getAll(CommandLine line) {
        return getValues(line);
    }

    public String getFirst(CommandLine line, String name, String defaultValue) {
        return Optional.ofNullable(line.getOptionValue(name)).orElse(defaultValue);
    }

    public String getFirst(CommandLine line) {
        return getFirst(line, shortName, defaultValue);
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
        public static final String ALL = "all";
        public static final String SUBSCRIBE_DESC = "Subscribe on events:\n" +
                "(system,call,log,verbose,command,agent,user,config,command,dtmf,reporting,cdr,dialplan,originate,message)\n" +
                " Example: '-s call,cdr'. Default: " + ALL;
        public static final String FILTER_DESCR = "Javascript boolean expression to filter events.\n" +
                "Predefined variables:\n" +
                " '$msg' - event fields as map, '$str'- event fields as string\n" +
                "Example:\n" +
                "Event: Newchannel\n" +
                "Privilege: call,all\n" +
                "Channel: SIP/4956616060-0001245a\n" +
                "ChannelState: 0'\n" +
                "For this event $msg = {Event:'Newchannel', Privilege: 'call,all', Channel:'SIP/4956616060-0001245a', ChannelState:'0'}\n" +
                "$msg also support uppercase and lowercase keys:  $msg.Event===$msg.event===$msg.EVENT\n" +
                "$msg has additional methods:  \n" +
                "$msg.keysIncludes(<String|RegExp>)\n" +
                "$msg.valuesIncludes(<String|RegExp>)\n" +
                "Example:\n" +
                "show only events containing a number 88009009001:  \n" +
                "java -jar cliAmi.jar -f \"$str.includes('88009009001')\"\n" +
                "show only HangupRequest events containing a number 88009009001: \n" +
                " java -jar cliAmi.jar -f \"$str.includes('88009009001')&&$msg.Event==='HangupRequest'\"\n" +
                "show only events containing fields that has any match the regular expression: \n" +
                "java -jar cliAmi.jar -f \"$msg.keysIncludes(/^.*IDNum.*$/)\"\n";
        public static final String EXECUTE_DESC = "Execute commands. " +
                "Example: DND for 9001 =>  -e *78#9001 ";
    }
}
