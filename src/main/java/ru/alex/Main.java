package ru.alex;

import org.apache.commons.cli.*;
import ru.alex.model.*;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class Main {
    final Options options;
    final CommandLineParser parser;
    private final String[] args;
    private final HelpFormatter helpFormatter;
    private final CommandLine parsed;

    public Main(String[] args) {
        this.args = args;
        options = Opt.create();
        parser = new DefaultParser();
        helpFormatter = new HelpFormatter();
        if (args.length == 0) {
            helpFormatter.printHelp("ant", options);
            throw new IllegalArgumentException();
        }
        try {
            parsed = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println("ERROR:" + e.getMessage());
            helpFormatter.printHelp("ant", options);
            throw new IllegalArgumentException(e);
        }
    }

    static class Executor implements AutoCloseable {
        private final Client client;

        Executor(Client client) {
            this.client = client;
        }

        boolean check() throws IOException {
            return client.check();
        }

        boolean execute(Command command, Predicate<Response> predicate) throws IOException {
            System.out.println(command);
            final Response response = client.send(command);
            System.out.println(response);
            return predicate.test(response);
        }

        void execute(Function<Client, Response> command,
                     Predicate<Response> predicate, Consumer<Response> consumer) throws IOException {
            //System.out.println(command);
            final Response response = command.apply(client);
            //System.out.println(response);
            if (predicate.test(response)) {
                consumer.accept(response);
            }
        }

        @Override
        public void close() throws Exception {
            client.close();
        }
    }

    void launch() throws Exception {
        String host = Opt.HOST.getFirst(parsed);
        String port = Opt.PORT.getFirst(parsed);
        try (final Executor executor = new Executor(new Client(host, Integer.parseInt(port)))) {
            if (!executor.check()) {
                System.out.printf("Wrong server: %s:%s %n", host, port);
                return;
            }
            if (!executor.execute(new Login(Opt.USER.getFirst(parsed), Opt.PASSWORD.getFirst(parsed)), Response::isOk)) {
                System.out.println("Wrong login or password or network restricted in manager.conf");
                return;
            }
            if (!executor.execute(Events.create(Opt.SUBSCRIBE.getFirst(parsed)), r -> r.isOk() || r.isEvent())) {
                System.out.println("Event listen error");
                return;
            }
            List<Filter> filterList = Main.Opt.FILTER.getAllFields(parsed).stream().map(Filter::new).collect(Collectors.toList());
            Predicate<Response> predicate = !filterList.isEmpty() ? resp -> filterList.stream().anyMatch(f -> f.match(resp.toString()))
                    : resp -> true;
            while (true) {
                executor.execute(Client::listen,
                        predicate,
                        System.out::println);
            }
        }
    }


    public static void main(String[] args) throws Exception {
        new Main(args).launch();
    }


    enum Opt {
        USER("u", "user", "Manager username from manager.conf. Default - " + Constants.ADMIN, null, Constants.ADMIN, false),
        PASSWORD("s", "secret", "Manager password from manager.conf.", null, null, true),
        HOST("h", "host", "Ami server ip/name. Default - " + Constants.LOCALHOST, null, Constants.LOCALHOST, false),
        PORT("p", "port", "Ami server port. Default - " + Constants.PORT, null, Constants.PORT, false),
        SUBSCRIBE("l", "listen", "Subscribe on events:\n" +
                "(system,call,log,verbose,command,agent,user,config,command,dtmf,reporting,cdr,dialplan,originate,message)\n" +
                " Example: '-s call,cdr'. Default: " + Constants.SYSTEM, ',', Constants.SYSTEM, false),
        FILTER("f", "filter", "Event's header filter. Default - case insensitivity" +
                "Serach by regexp pattern. \n " +
                "Example:  -f 'event: status' -f '.*caller.*: [0-9]{3}'", ':', null, false);

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

        List<Field> getAllFields(CommandLine line) {
            Objects.requireNonNull(sep);
            return getValues(line).stream().map(Field::new).collect(toList());
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
            return builder.required(required).hasArg().desc(descr).build();
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
            public static final String PORT = "5038";
            public static final String SYSTEM = "system";
        }
    }
}
