package ru.alex.model;

import junit.framework.TestCase;
import org.junit.Assert;

public class CommandTest extends TestCase {

    public void testPrint() {
        String row = "Action: Login\r\n" +
                "Username: admin\r\n" +
                "Secret: d8Frxv/T6Ha1\r\n\r\n";
        Command command = new Command(row);
        Assert.assertEquals(row, command.toString());



    }
}