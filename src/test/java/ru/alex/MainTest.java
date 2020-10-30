package ru.alex;


import org.apache.commons.cli.*;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class MainTest {

    @Test
    public void testPattern() {
        assertTrue(new Filter("calleridnum", "...").match("CallerIDNum: 123"));
        assertTrue(new Filter(".*", "...").match("CallerIDNum: 123"));
        assertFalse(new Filter(".*", "[0-9]{3}").match("CallerIDNum: 1234"));
        assertTrue(new Filter(".*ID.*", "1.*").match("CallerIDNum: 1234"));
        assertTrue(new Filter(".*ID.*", "...").match("CallerIDNum: 123"));
        assertTrue(new Filter(".*ID.*", "[1-2]..").match("CallerIDNum: 123"));
    }

    @Test
    @Ignore
    public void testMain() throws Exception {
        Main.main( "-u admin -s d8Frxv/T6Ha1 -p 5038 -h 10.0.26.37 -l call " .split(" "));
    }

    @Test
    public void testOptions() throws ParseException {
        final Options options = Main.Opt.create();
        final CommandLineParser parser = new DefaultParser();

        {
            CommandLine line = parser.parse(options, "-u user1 -p passw1 -h localhost -s call,system -f .*event.*:.*status.* -f .*callerid.*:[0-9]{3}" .split(" "));
            List<Filter> filterList = Main.Opt.FILTER.getAllFields(line).stream().map(Filter::new).collect(Collectors.toList());
            assertEquals(2, filterList.size());
            assertEquals("(?i)^\\s*.*event.*\\s*:\\s*.*status.*\\s*$", filterList.get(0).getPattern().toString());
            assertEquals("(?i)^\\s*.*callerid.*\\s*:\\s*[0-9]{3}\\s*$", filterList.get(1).getPattern().toString());
            assertEquals("user1", Main.Opt.USER.getFirst(line));
            assertEquals("passw1", Main.Opt.PASSWORD.getFirst(line));
            assertEquals("localhost", Main.Opt.HOST.getFirst(line));
            assertEquals("call,system", Main.Opt.SUBSCRIBE.getFirst(line));
        }
        {
            CommandLine line = parser.parse(options, " -p passw1" .split(" "));
            List<Filter> filterList = Main.Opt.FILTER.getAllFields(line).stream().map(Filter::new).collect(Collectors.toList());
            assertEquals(0, filterList.size());
            assertEquals("admin", Main.Opt.USER.getFirst(line));
            assertEquals("passw1", Main.Opt.PASSWORD.getFirst(line));
            assertEquals("localhost", Main.Opt.HOST.getFirst(line));
            assertEquals("system", Main.Opt.SUBSCRIBE.getFirst(line));
        }

    }


}