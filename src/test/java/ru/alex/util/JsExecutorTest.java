package ru.alex.util;


import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JsExecutorTest {

    @Test
    @Ignore
    public void testRun() {
        new JsExecutor("", "cmd.execute({Action:'DBPut', Family:'DND', Key:'9001', Val:'YES'});", command -> assertEquals("DBPut", command.getAction()));

        new JsExecutor("function dnd(ext, y){cmd.execute({Action: 'DBPut'," +
                "Family: 'DND'," +
                "Key: ext+''," +
                "Val: y?'YES':'NO'});}", "dnd(9001, true);",
                command -> {
                    assertEquals("DBPut", command.getAction());
                    assertEquals("9001", command.getFieldValue("Key"));
                    assertEquals("YES", command.getFieldValue("Val"));
                }
        );
    }
}