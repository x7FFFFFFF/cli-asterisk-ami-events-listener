package ru.alex;


import org.apache.commons.cli.*;
import org.junit.Ignore;
import org.junit.Test;
import ru.alex.model.Command;
import ru.alex.model.Field;
import ru.alex.model.Message;
import ru.alex.util.MessageParser;
import ru.alex.util.Opt;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.Assert.*;
import static ru.alex.util.Opt.*;

public class MainTest {

    @Test
    public void testPattern() {
      /*  assertTrue(new Filter("calleridnum", "...").match("CallerIDNum: 123"));
        assertTrue(new Filter(".*", "...").match("CallerIDNum: 123"));
        assertFalse(new Filter(".*", "[0-9]{3}").match("CallerIDNum: 1234"));
        assertTrue(new Filter(".*ID.*", "1.*").match("CallerIDNum: 1234"));
        assertTrue(new Filter(".*ID.*", "...").match("CallerIDNum: 123"));
        assertTrue(new Filter(".*ID.*", "[1-2]..").match("CallerIDNum: 123"));*/
    }

    @Test
    @Ignore
    public void testMain() throws Exception {
        // Main.main(new String[]{});
       /* Main.main(("--user admin --secret d8Frxv/T6Ha1 --port 5038 --execute *79#9001" +
                " --host 10.0.26.37 --listen call --macros C:\\Users\\root\\IdeaProjects\\cli-asterisk-ami-events-listener\\src\\main\\resources\\macros.txt").split(" "));*/
        Main.main(("--user admin --secret d8Frxv/T6Ha1 --port 5038  --host 10.0.26.37 ").split(" "));
    }

    @Test
    public void testOptions() throws ParseException, FileNotFoundException {
        final Options options = Opt.create();
        final CommandLineParser parser = new DefaultParser();

        {
            CommandLine line = parser.parse(options, ("--user user1 --secret passw1 --host 123.0.0.7 --listen call,system " +
                    " --execute *78#101;*79#101").split(" "));
            assertEquals("user1", USER.getFirst(line));
            assertEquals("passw1", PASSWORD.getFirst(line));
            assertEquals("123.0.0.7", HOST.getFirst(line));
            assertEquals("call,system", SUBSCRIBE.getFirst(line));
            final List<Command> commands = EXECUTE_COMMANDS.getCommands(line);
            assertEquals(2, commands.size());
            assertEquals("Action: DBPut\r\n" +
                    "Family: DND\r\n" +
                    "Key: 101\r\n" +
                    "Val: YES\r\n\r\n", commands.get(0).toString().replaceAll("(?m)^ActionID.*\\r\\n", ""));
            assertEquals("Action: DBPut\r\n" +
                    "Family: DND\r\n" +
                    "Key: 101\r\n" +
                    "Val: NO\r\n\r\n", commands.get(1).toString().replaceAll("(?m)^ActionID.*\\r\\n", ""));
        }
        {
            CommandLine line = parser.parse(options, " --secret passw1".split(" "));
       /*     List<Filter> filterList = FILTER.getFilterPredicate(line).stream().map(Filter::new).collect(Collectors.toList());
            assertEquals(0, filterList.size());*/
            assertEquals("admin", USER.getFirst(line));
            assertEquals("passw1", PASSWORD.getFirst(line));
            assertEquals("localhost", HOST.getFirst(line));
            assertEquals("system", SUBSCRIBE.getFirst(line));


        }

    }

    @Test
    public void testFilters() throws ParseException, IOException {
        final Options options = Opt.create();
        final CommandLineParser parser = new DefaultParser();
        CommandLine line = parser.parse(options, ("--secret passw1  " +
                "--filter $msg.keysIncludes(/^.*Num.*$/)&&$msg.valuesIncludes(/^.*9260647698.*$/)  --filter !$msg.valuesIncludes('Newexten')").split(" "));
        final Predicate<Message> filterPredicate = FILTER.getFilterPredicate(line);
        try (final MessageParser messageParser = new MessageParser(getClass().getResourceAsStream("/testEvents.txt"), filterPredicate)) {
            final List<Message> parse = messageParser.parse();
            assertEquals(8, parse.size());
            parse.forEach(System.out::println);

        }
    }

    @Test
    public void testFilterReverse() throws ParseException, IOException {
        final Options options = Opt.create();
        final CommandLineParser parser = new DefaultParser();
        {
            CommandLine line = parser.parse(options, ("--secret passw1 --filter !$msg.valuesIncludes('Newexten')").split(" "));
            final Predicate<Message> filterPredicate = FILTER.getFilterPredicate(line);
            assertFalse(filterPredicate.test(Message.create(new Field("Event: Newexten"))));
        }

        {
            CommandLine line = parser.parse(options, ("--secret passw1  --filter $msg.Event=='Newexten'").split(" "));
            final Predicate<Message> filterPredicate = FILTER.getFilterPredicate(line);
            assertTrue(filterPredicate.test(Message.create(new Field("Event: Newexten"))));
        }

    }
    @Test
    public void testFilters2() throws ParseException, IOException {
        final Options options = Opt.create();
        final CommandLineParser parser = new DefaultParser();
        CommandLine line = parser.parse(options, ("--secret passw1  " +
                "--filter $msg.valuesIncludes('9001') " +
                "--filter !$msg.valuesIncludes('Newexten') " +
                "--filter $msg.Channel.startsWith('Local')").split(" "));
        final Predicate<Message> filterPredicate = FILTER.getFilterPredicate(line);
        try (final MessageParser messageParser = new MessageParser(getClass().getResourceAsStream("/call2.txt"), filterPredicate)) {
            final List<Message> parse = messageParser.parse();
            assertEquals(16, parse.size());
            parse.forEach(System.out::println);

        }
    }
}