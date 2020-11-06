package ru.alex.util;

import junit.framework.TestCase;
import ru.alex.model.Field;

import javax.script.ScriptException;
import java.util.function.Predicate;

public class JsFilterTest extends TestCase {

    public void testPredicate() throws ScriptException {
        final Predicate<Field> predicate = new JsFilter("$v==='9001' && $n.endsWith('Num') && /.*um/.test($n)").predicate();
        assertTrue(predicate.test(new Field("ConnectedLineNum: 9001")));
        assertTrue(predicate.test(new Field("DestCallerIDNum: 9001")));
        assertFalse(predicate.test(new Field("Dest: 9001")));
    }
}