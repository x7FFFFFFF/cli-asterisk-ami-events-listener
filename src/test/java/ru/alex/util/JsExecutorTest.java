package ru.alex.util;


import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JsExecutorTest {

    @Test
    @Ignore
    public void testRun() {
        new JsExecutor("cmd.execute({Action:'DBPut', Family:'DND'});", new CommandExecutorWrapper(command-> assertEquals("DBPut", command.getAction())));
    }
}