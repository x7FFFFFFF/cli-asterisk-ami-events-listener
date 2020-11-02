package ru.alex;

import org.apache.commons.cli.*;
import ru.alex.model.*;
import ru.alex.util.CommandExecutor;
import ru.alex.util.Macros;
import ru.alex.util.Opt;

import java.io.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ru.alex.util.Opt.HOST;
import static ru.alex.util.Opt.PORT;

public class Main implements Closeable {
    private final Options options;
    private final CommandLineParser parser;
    private final String[] args;
    private final HelpFormatter helpFormatter;
    private final CommandLine parsed;
    private final CommandExecutor executor;
    private final String host;
    private final String port;
    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private final List<Command> commands;

    public Main(String[] args) throws IOException {
        this.args = args;
        options = Opt.create();
        parser = new DefaultParser();
        helpFormatter = new HelpFormatter();
        if (args.length == 0) {
            System.out.println("Console utility for Asterisk AMI protocol interaction.");
            System.out.println("Allows to execute AMI commands and subscribe to certain AMI events.");
            helpFormatter.printHelp("java -jar cliAmi.jar <options>", options);
            printDefaultMacroses();
            throw new IllegalStateException();
        }
        try {
            parsed = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println("ERROR:" + e.getMessage());
            helpFormatter.printHelp("ant", options);
            throw new IllegalArgumentException(e);
        }
        host = HOST.getFirst(parsed);
        port = PORT.getFirst(parsed);
        executor = new CommandExecutor(new Client(host, Integer.parseInt(port)), System.out);
        commands = Opt.EXECUTE_COMMANDS.getCommands(parsed);
    }

    private void printDefaultMacroses() {
        System.out.println("----------------------");
        System.out.println("Default macroses:");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream(Macros.DEFAULT)))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    enum Result {
        OK, ERROR
    }

    Result launch() throws IOException {
        if (!executor.check()) {
            System.out.printf("Wrong server: %s:%s %n", host, port);
            return Result.ERROR;
        }
        if (!executor.execute(new LoginCommand(Opt.USER.getFirst(parsed), Opt.PASSWORD.getFirst(parsed)), Response::isOk)) {
            System.out.println("Wrong login or password or network restricted in manager.conf");
            return Result.ERROR;
        }
        if (!executor.execute(EventsCommand.create(Opt.SUBSCRIBE.getFirst(parsed)), r -> r.isResponse() || r.isOk())) {
            System.out.println("Event listen error");
            return Result.ERROR;
        }
        if (commands.isEmpty()) {
            listen();
        } else {
            executor.execute(commands);
        }
        executor.execute(new LogoffCommand());
        return Result.OK;
    }

    private List<Command> getCommands() throws FileNotFoundException {
        return commands;
    }

    private void listen() throws IOException {
        List<Filter> filterList = Opt.FILTER.getAllFields(parsed).stream().map(Filter::new).collect(Collectors.toList());
        Predicate<Message> predicate = !filterList.isEmpty() ? resp -> filterList.stream().anyMatch(f -> f.match(resp.toString()))
                : resp -> true;
        while (!Thread.currentThread().isInterrupted()) {
            executor.execute(Client::listen,
                    predicate,
                    System.out::println);
        }
    }


    public static void main(String[] args) throws Exception {

        try (final Main main = new Main(args)) {
            final List<Command> commands = main.getCommands();
            if (commands.isEmpty()) {
                final ExecutorService executorService = Executors.newSingleThreadExecutor(r -> new Thread(r, "main-client-thread"));
                executorService.submit(main::launch);
                waitForExit();
                executorService.shutdownNow();
            } else {
                main.launch();
            }
        }
    }

    private static void waitForExit() {
        System.out.println("Press 'q' key to exit.");
        int c;
        do {
            try {
                c = System.in.read();
            } catch (IOException var2) {
                break;
            }
        } while ('q' != (char) c && 'Q' != (char) c);
    }


    @Override
    public void close() throws IOException {
        if (stopped.compareAndSet(false, true)) {
            executor.close();
        }
    }
}
