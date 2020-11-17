package ru.alex.util;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import ru.alex.model.Command;
import ru.alex.model.Field;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static ru.alex.util.JsFilter.NASHORN_POLYFILL_ARRAY_PROTOTYPE_INCLUDES;
import static ru.alex.util.JsFilter.NASHORN_POLYFILL_STRING_PROTOTYPE_INCLUDES;

public class JsExecutor {
    public JsExecutor(String source, CommandConsumer commandExecutor) {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            engine.eval(NASHORN_POLYFILL_STRING_PROTOTYPE_INCLUDES);
            engine.eval(NASHORN_POLYFILL_ARRAY_PROTOTYPE_INCLUDES);
            final Bindings bindings = engine.createBindings();
            bindings.put("cmd", commandExecutor);
            engine.eval(source, bindings);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    public static class CommandConsumer {
        final Consumer<Command> executor;

        public CommandConsumer(Consumer<Command> executor) {
            this.executor = executor;
        }

        public void execute(ScriptObjectMirror mirror) {
            List<Field> list = new ArrayList<>();
            mirror.forEach((k, v) -> list.add(new Field(k, v.toString())));
            executor.accept(new Command(list));
        }
    }
}
