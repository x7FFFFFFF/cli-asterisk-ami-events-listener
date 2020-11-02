package ru.alex;


import org.apache.commons.cli.*;
import org.junit.Ignore;
import org.junit.Test;
import ru.alex.util.Opt;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static ru.alex.util.Opt.*;

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
        Main.main("--user admin --secret d8Frxv/T6Ha1 --port 5038 --host 10.0.26.37 --listen call ".split(" "));
    }

    @Test
    public void testOptions() throws ParseException {
        final Options options = Opt.create();
        final CommandLineParser parser = new DefaultParser();

        {
            CommandLine line = parser.parse(options, "--user user1 --secret passw1 --host localhost --listen call,system --filter .*event.*:.*status.* --filter .*callerid.*:[0-9]{3}".split(" "));
            List<Filter> filterList = FILTER.getAllFields(line).stream().map(Filter::new).collect(Collectors.toList());
            assertEquals(2, filterList.size());
            assertEquals("(?i)^\\s*.*event.*\\s*:\\s*.*status.*\\s*$", filterList.get(0).getPattern().toString());
            assertEquals("(?i)^\\s*.*callerid.*\\s*:\\s*[0-9]{3}\\s*$", filterList.get(1).getPattern().toString());
            assertEquals("user1", USER.getFirst(line));
            assertEquals("passw1", PASSWORD.getFirst(line));
            assertEquals("localhost", HOST.getFirst(line));
            assertEquals("call,system", SUBSCRIBE.getFirst(line));
        }
        {
            CommandLine line = parser.parse(options, " --secret passw1".split(" "));
            List<Filter> filterList = FILTER.getAllFields(line).stream().map(Filter::new).collect(Collectors.toList());
            assertEquals(0, filterList.size());
            assertEquals("admin", USER.getFirst(line));
            assertEquals("passw1", PASSWORD.getFirst(line));
            assertEquals("localhost", HOST.getFirst(line));
            assertEquals("system", SUBSCRIBE.getFirst(line));
        }

    }


}