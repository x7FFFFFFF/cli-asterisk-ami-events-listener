package ru.alex.util;

import junit.framework.TestCase;
import org.junit.Test;
import ru.alex.model.Command;
import ru.alex.model.Field;

import java.io.InputStream;
import java.util.List;

import static ru.alex.model.Message.Constants.*;

public class MacrosTest extends TestCase {
    @Test
    public void testRender() {
        final InputStream resource = MacrosTest.class.getResourceAsStream(Macros.DEFAULT);
        assertNotNull(resource);
        final Macros macros = new Macros(resource);
        {
            final List<Command> tested = macros.render("*78", "9001");
            assertEquals(1, tested.size());
            assertEquals("Action: DBPut\r\n" +
                    "Family: DND\r\n" +
                    "Key: 9001\r\n" +
                    "Val: YES\r\n\r\n", tested.get(0).toString()
                    .replaceAll("(?m)^ActionID.*\\r\\n", ""));
        }
        {
            final List<Command> tested = macros.render("*79", "9001");
            assertEquals(1, tested.size());
            assertEquals("Action: DBPut\r\n" +
                    "Family: DND\r\n" +
                    "Key: 9001\r\n" +
                    "Val: NO\r\n\r\n", tested.get(0).toString().replaceAll("(?m)^ActionID.*\\r\\n", ""));
        }
    }
}