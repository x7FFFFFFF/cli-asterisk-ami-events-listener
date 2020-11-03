package ru.alex.util;

import ru.alex.model.Field;
import ru.alex.model.Message;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class MessageParser implements Closeable {
    private final BufferedReader reader;
    private final Predicate<Message> predicate;

    public MessageParser(InputStream is, Predicate<Message> predicate) {
        reader = new BufferedReader(new InputStreamReader(is));
        this.predicate = predicate;
    }

    public List<Message> parse() throws IOException {
        String line;
        final List<Message> result = new ArrayList<>();
        final List<Field> fields = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty() && !fields.isEmpty()) {
                final Message message = Message.create(fields);
                if (predicate.test(message)) {
                    result.add(message);
                }
                fields.clear();
                continue;
            }
            if (!line.isEmpty()) {
                fields.add(new Field(line));
            }
        }
        return result;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
