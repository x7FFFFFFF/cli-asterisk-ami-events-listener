package ru.alex.model;

import org.junit.Assert;
import org.junit.Test;

import static ru.alex.model.Response.SUCCESS_RESP;

public class ResponseTest {
    @Test
    public void test() {
        final Response response = new Response("Success", "Authentication accepted");
        final String expected = "Response: Success\r\n" +
                "Message: Authentication accepted\r\n\r\n";
        Assert.assertEquals(expected, response.toString());
        Assert.assertEquals(expected, new Response(expected).toString());
        Assert.assertEquals("Response: Success\r\n" +
                "Message: Message\r\n\r\n", SUCCESS_RESP.toString());


    }
}