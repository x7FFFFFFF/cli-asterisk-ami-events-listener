package ru.alex.util;

import junit.framework.TestCase;
import ru.alex.model.Field;
import ru.alex.model.Message;

import java.util.function.Predicate;

public class JsFilterTest extends TestCase {

    public void testPredicate() {
        {
            final Predicate<Message> predicate = new JsFilter("$str.includes('9001')").predicate();
            assertTrue(predicate.test(Message.create(new Field("Event: Status"), new Field("ConnectedLineNum: 9001"))));
            assertTrue(predicate.test(Message.create(new Field("Event: Status"), new Field("DestCallerIDNum: 9001"))));
            assertFalse(predicate.test(Message.create(new Field("Event: Status"), new Field("Dest: 9002"))));
        }
        {
            final Predicate<Message> predicate = new JsFilter("$str.includes('9001') && $msg.event==='Status' && $msg.ConnectedLineNum==='9001'").predicate();
            assertTrue(predicate.test(Message.create(new Field("Event: Status"), new Field("ConnectedLineNum: 9001"))));
            assertFalse(predicate.test(Message.create(new Field("Event: A"), new Field("DestCallerIDNum: 9001"))));
            assertFalse(predicate.test(Message.create(new Field("Event: B"), new Field("Dest: 9002"))));
        }
        {
            final Predicate<Message> predicate = new JsFilter("$msg.valuesIncludes(/^9.*$/)&&$msg.keysIncludes(/^Ev.*$/)").predicate();
            assertTrue(predicate.test(Message.create(new Field("Event: Status"), new Field("ConnectedLineNum: 9001"))));
            assertTrue(predicate.test(Message.create(new Field("Event: Status"), new Field("DestCallerIDNum: 9001"))));
            assertTrue(predicate.test(Message.create(new Field("Event: Status"), new Field("Dest: 9002"))));
            assertFalse(predicate.test(Message.create(new Field("Action: Action"), new Field("Dest: 8002"))));
        }
    }
}