package ru.alex.model;

import org.junit.Assert;
import org.junit.Test;


public class ResponseTest {
    @Test
    public void test() {
        final Response response = new Response("Success", "Authentication accepted", "123");
        final String expected = "Response: Success\r\n" +
                "Message: Authentication accepted\r\n" +
                "ActionID: 123\r\n\r\n";
        Assert.assertEquals(expected, response.toString());
        Assert.assertEquals(expected, new Response(expected).toString());


    }
}