package ru.alex;

import ru.alex.model.Command;
import ru.alex.model.Field;
import ru.alex.model.Logoff;
import ru.alex.model.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static ru.alex.model.Command.COMMAND_END_PATTERN;

public class Client implements AutoCloseable {
    private final int serverPort;
    private final String addr;
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;

    public Client(String addr, int serverPort) throws IOException {
        this.serverPort = serverPort;
        this.addr = addr;
        socket = new Socket(addr, serverPort);
        out = new PrintWriter(socket.getOutputStream(),
                true);
        in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
    }

    public boolean check() throws IOException {
        //final String greet = in.re();
        String greet = in.readLine();
        return greet.contains("Asterisk Call Manager");
    }

    public void logoff() {
        new Logoff().print(out);
    }

    public Response send(Command command) throws IOException {
        //System.out.println(command);
        command.print(out);
        String line;
        final List<Field> fields = new ArrayList<>(30);
        while (!(line = in.readLine()).isEmpty()) {
            fields.add(new Field(line));
        }
        //System.out.println(response);
        return new Response(fields.toArray(new Field[0]));
    }

    public Response listen() {
        String line;
        final List<Field> fields = new ArrayList<>(30);
        try {
            while (!(line = in.readLine()).isEmpty()) {
                fields.add(new Field(line));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //System.out.println(response);
        return new Response(fields.toArray(new Field[0]));
    }


    @Override
    public void close() throws Exception {
        out.close();
        in.close();
        socket.close();
    }
}
