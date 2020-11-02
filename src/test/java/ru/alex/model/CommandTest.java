package ru.alex.model;

import junit.framework.TestCase;
import org.junit.Assert;

import static ru.alex.model.Message.Constants.*;

public class CommandTest extends TestCase {

    public void testPrint() {
        final String action = "Action: Login\r\n";
        final String cred = "Username: admin\r\n" +
                "Secret: d8Frxv/T6Ha1\r\n\r\n";
        String row = action + cred;
        Command command = new Command(row);
        final String actionId = ACTION_ID + ": " + command.getActionId().get() + DELIM;
        Assert.assertEquals(action + actionId + cred, command.toString());


    }
}