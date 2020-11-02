package ru.alex;

import ru.alex.model.Command;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;



public class TestServer/* extends Thread implements Closeable*/ {
  /*  private final ServerSocket soket;
    private final int port;

    public static final String WELCOME = "Asterisk Call Manager/4.0.3" + DELIM+ DELIM;

    public TestServer() throws IOException, InterruptedException {
        super("asterisk-ami-test-server-thread");
        soket = new ServerSocket(0);
        port = soket.getLocalPort();

    }

    public TestServer go() {
        super.start();
        return this;
    }


    public int getPort() {
        return port;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try (final Socket client = soket.accept();
                 final Scanner in = new Scanner(client.getInputStream(), "UTF-8");
                 final PrintWriter out = new PrintWriter(client.getOutputStream())) {
                in.useDelimiter(COMMAND_END_PATTERN);
                out.println(WELCOME);
                out.flush();
                while (!Thread.currentThread().isInterrupted()) {
                    Command command = new Command(in.next());
                    if (command.isLogoff()) {
                        return;
                    }

                    process(command, out);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected void process(Command in, PrintWriter out) {
    }


    @Override
    public void close() throws IOException {
        soket.close();
    }*/
}
