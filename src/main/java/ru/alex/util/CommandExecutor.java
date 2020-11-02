package ru.alex.util;

import ru.alex.Client;
import ru.alex.model.Command;
import ru.alex.model.Response;

import java.io.IOException;
import java.io.PrintStream;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class CommandExecutor implements AutoCloseable {
    private final Client client;
    private final PrintStream out;

    public CommandExecutor(Client client, PrintStream out) {
        this.client = client;
        this.out = out;
    }

    public boolean check() throws IOException {
        return client.check();
    }

    public void execute(Command command) throws IOException {
        out.println(command);
        final Response response = client.send(command);
        out.println(response);
    }

    public boolean execute(Command command, Predicate<Response> predicate) throws IOException {
        out.println(command);
        final Response response = client.send(command);
        out.println(response);
        return predicate.test(response);
    }

    public void execute(Function<Client, Response> command,
                 Predicate<Response> predicate, Consumer<Response> consumer) throws IOException {
        final Response response = command.apply(client);
        if (predicate.test(response)) {
            consumer.accept(response);
        }
    }

    @Override
    public void close() throws IOException {
        client.close();
    }
}
