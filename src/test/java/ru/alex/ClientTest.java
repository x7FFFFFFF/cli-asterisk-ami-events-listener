package ru.alex;

import org.junit.Ignore;
import org.junit.Test;
import ru.alex.model.Command;
import ru.alex.model.LoginCommand;
import ru.alex.model.Message;
import ru.alex.model.Response;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.function.BiConsumer;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ClientTest {
    @Test
    @Ignore
    public void testRemote() throws Exception {
        try (final Client client = new Client("10.0.26.37", 5038)) {
            assertTrue(client.check());
            Response response = client.send(new LoginCommand("admin", "d8Frxv/T6Ha1"));
            assertTrue(response.isOk());
            Message listen = client.listen();
            assertTrue(listen.isEvent());
            client.logoff();

        }
    }

  /*  @Test
    public void test() throws Exception {
        BiConsumer<Command, PrintWriter> todo = (comm, out) -> {
            switch (comm.getAction().getValue()) {
                case Login.ACTION:
                    SUCCESS_RESP.print(out);
                    break;
                case Logoff.ACTION:
                    GOODBYE_RESP.print(out);
                    break;
                default:
                    fail();
            }


        };
        try (final TestServer testServer = createAndRunSrv(todo);
             final Client client = new Client("localhost", testServer.getPort())) {
            assertTrue(client.check());
            {
                Response response = client.send(new Login("user", "secret"));
                assertTrue(response.isOk());
            }
            {
                client.logoff();
            }

        }
    }*/

   /* private TestServer createAndRunSrv(BiConsumer<Command, PrintWriter> consumer) throws
            IOException, InterruptedException {
        return new TestServer() {
            @Override
            protected void process(Command in, PrintWriter out) {
                consumer.accept(in, out);
            }
        }.go();
    }*/
}