package ru.alex;

import org.apache.commons.cli.*;
import ru.alex.model.Events;
import ru.alex.model.Login;
import ru.alex.model.Logoff;
import ru.alex.model.Response;
import ru.alex.util.CommandExecutor;
import ru.alex.util.Opt;

import java.io.Closeable;
import java.io.IOException;
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

    public Main(String[] args) throws IOException {
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
        host = HOST.getFirst(parsed);
        port = PORT.getFirst(parsed);
        executor = new CommandExecutor(new Client(host, Integer.parseInt(port)), System.out);
    }
    enum Result {
        OK, ERROR
    }

    Result launch() throws IOException {
        if (!executor.check()) {
            System.out.printf("Wrong server: %s:%s %n", host, port);
            return Result.ERROR;
        }
        if (!executor.execute(new Login(Opt.USER.getFirst(parsed), Opt.PASSWORD.getFirst(parsed)), Response::isOk)) {
            System.out.println("Wrong login or password or network restricted in manager.conf");
            return Result.ERROR;
        }
        if (!executor.execute(Events.create(Opt.SUBSCRIBE.getFirst(parsed)), r -> r.isOk() || r.isEvent())) {
            System.out.println("Event listen error");
            return Result.ERROR;
        }
        List<Filter> filterList = Opt.FILTER.getAllFields(parsed).stream().map(Filter::new).collect(Collectors.toList());
        Predicate<Response> predicate = !filterList.isEmpty() ? resp -> filterList.stream().anyMatch(f -> f.match(resp.toString()))
                : resp -> true;
        while (!Thread.currentThread().isInterrupted()) {
            executor.execute(Client::listen,
                    predicate,
                    System.out::println);
        }
        executor.execute(new Logoff());
        return Result.OK;
    }


    public static void main(String[] args) throws Exception {
        System.out.println("Press 'q' key to exit.");
        final ExecutorService executorService = Executors.newSingleThreadExecutor(r -> new Thread(r, "main-client-thread"));
        try(final Main main = new Main(args)) {
            executorService.submit(main::launch);
            waitForExit();
            executorService.shutdownNow();
        }
    }

    private static void waitForExit() {
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
