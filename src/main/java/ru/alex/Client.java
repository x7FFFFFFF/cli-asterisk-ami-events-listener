package ru.alex;

import ru.alex.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


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
        new LogoffCommand().print(out);
    }

    public Response send(Command command) throws IOException {
        //System.out.println(command);
        command.print(out);
        Message message;
        do {
            String line = in.readLine();
            final List<Field> fields = new ArrayList<>(30);
            Message.Type type = Message.Type.parse(line);
            do {
                fields.add(new Field(line));
            } while (!(line = in.readLine()).isEmpty());
            message = type.create(fields);
            if (message.isEvent()) {
                System.out.println(message);
            }

        } while (!message.isResponseFor(command));

        return (Response) message;
    }

    public Message listen() {
        return Message.create(Field.read(in));
    }


    @Override
    public void close() throws IOException {
        out.close();
        in.close();
        socket.close();
    }
}
