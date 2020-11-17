package ru.alex.util;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.objects.NativeRegExp;
import ru.alex.model.Field;
import ru.alex.model.Message;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;
import java.util.function.Predicate;

public class JsFilter {
    // Copied from https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/includes#Polyfill
    public static final String NASHORN_POLYFILL_STRING_PROTOTYPE_INCLUDES = "if (!String.prototype.includes) { Object.defineProperty(String.prototype, 'includes', { value: function(search, start) { if (typeof start !== 'number') { start = 0 } if (start + search.length > this.length) { return false } else { return this.indexOf(search, start) !== -1 } } }) }";
    // Copied from https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/includes#Polyfill
    public static final String NASHORN_POLYFILL_ARRAY_PROTOTYPE_INCLUDES = "if (!Array.prototype.includes) { Object.defineProperty(Array.prototype, 'includes', { value: function(valueToFind, fromIndex) { if (this == null) { throw new TypeError('\"this\" is null or not defined'); } var o = Object(this); var len = o.length >>> 0; if (len === 0) { return false; } var n = fromIndex | 0; var k = Math.max(n >= 0 ? n : len - Math.abs(n), 0); function sameValueZero(x, y) { return x === y || (typeof x === 'number' && typeof y === 'number' && isNaN(x) && isNaN(y)); } while (k < len) { if (sameValueZero(o[k], valueToFind)) { return true; } k++; } return false; } }); }";

    private final Invocable invocable;
    private final String jsSource;

    public JsFilter(String js) {
        jsSource = js;
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            engine.eval(NASHORN_POLYFILL_STRING_PROTOTYPE_INCLUDES);
            engine.eval(NASHORN_POLYFILL_ARRAY_PROTOTYPE_INCLUDES);
            engine.eval("function filter($msg,$str){ return " + js + ";}");
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
        invocable = (Invocable) engine;
    }

    Predicate<Message> predicate() {
        return msg -> {
            try {
                return (Boolean) invocable.invokeFunction("filter", createMap(msg), msg.toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    private Map<String, String> createMap(Message msg) {
        Map<String, String> map = new JsMap();
        msg.getFields().values().forEach(f -> {
            map.put(f.getName().toLowerCase(), f.getValue());
            map.put(f.getName().toUpperCase(), f.getValue());
            map.put(f.getName(), f.getValue());
        });
        return map;
    }

    public static class JsMap extends HashMap<String, String> {
        public boolean keysIncludes(Object obj) {
            return match(obj, keySet());
        }

        public boolean valuesIncludes(Object obj) {
            return match(obj, values());
        }

        private boolean match(Object obj, Collection<String> keys) {
            if (obj instanceof String) {
                return keys.contains(obj);
            } else if (obj instanceof ScriptObjectMirror) {
                final ScriptObjectMirror mirror = (ScriptObjectMirror) obj;
                if ("RegExp".equals(mirror.getClassName())) {
                    final NativeRegExp regExp = mirror.to(NativeRegExp.class);
                    return keys.stream().anyMatch(regExp::test);
                }
                throw new RuntimeException("Unknown class " + mirror.getClassName());
            }
            throw new RuntimeException("Unknown class " + obj.getClass());
        }

    }

}
