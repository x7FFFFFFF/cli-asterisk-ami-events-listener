package ru.alex.util;

import ru.alex.model.Command;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.alex.model.Message.Constants.DELIM;

public class Macros {
    public static final String COMMENT = "#";
    public static final String DEFAULT = "/macros.txt";
    private Map<String, List<ITemplate>> templates = new HashMap<>();
    //private static Pattern VAR_PATTERN = Pattern.compile("\\$\\{", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

    public List<Command> render(String starCode, Object... prarams) {
        final List<ITemplate> iTemplates = templates.get(starCode);
        if (iTemplates == null) {
            throw new RuntimeException("Cant find template for " + starCode);
        }
        return iTemplates.stream().map(t -> t.render(prarams)).map(Command::new).collect(Collectors.toList());
    }

    public Macros(InputStream is) {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            String starCode = null;
            final StringBuilder command = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(COMMENT)) {
                    continue;
                }
                final String trimmedLine = clearComments(line, COMMENT);

                if (trimmedLine.startsWith("*")) {
                    starCode = trimmedLine;
                    command.setLength(0);
                    continue;
                }
                if (trimmedLine.isEmpty() && starCode != null) {
                    final ITemplate iTemplate = create(command.toString());
                    //currTempl.add(iTemplate);
                    templates.compute(starCode, (k, oldValue) -> {
                        final List<ITemplate> list = (oldValue == null) ? new ArrayList<>() : oldValue;
                        list.add(iTemplate);
                        return list;
                    });
                    command.setLength(0);
                    continue;
                }
                if (starCode != null) {
                    command.append(trimmedLine).append(DELIM);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ITemplate create(String line) {
        return line.contains("${") ? new ParamTemplate(line.replaceAll("\\$\\{", "{")) : new LiteralTemplate(line);
    }


    String clearComments(String line, String symb) {
        final int i = line.indexOf(symb);
        return (i != -1 ? line.substring(0, i) : line).trim();
    }


    interface ITemplate {
        String render(Object... params);
    }

    private static class LiteralTemplate implements ITemplate {

        private final String value;

        public LiteralTemplate(String value) {
            this.value = value;
        }

        @Override
        public String render(Object... params) {
            return value;
        }
    }

    private static class ParamTemplate implements ITemplate {
        private final MessageFormat messageFormat;

        private ParamTemplate(String messageFormat) {
            this.messageFormat = new MessageFormat(messageFormat);
        }

        @Override
        public String render(Object... params) {
            return messageFormat.format(params);
        }
    }
}
