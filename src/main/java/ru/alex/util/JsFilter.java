package ru.alex.util;

import ru.alex.model.Field;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.function.Predicate;

public class JsFilter {
    private final Invocable invocable;
    private final String jsSource;

    public JsFilter(String js) {
        jsSource = js;
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            engine.eval("function filter($n,$v){ return " + js + ";}");
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
        invocable = (Invocable) engine;
    }

    Predicate<Field> predicate() {
        return field -> {
            try {
                return (Boolean) invocable.invokeFunction("filter", field.getName(), field.getValue());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

}
