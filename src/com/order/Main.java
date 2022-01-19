package com.order;


import com.order.data.Book;
import com.order.data.BookProcessor;
import com.order.data.Processor;
import com.order.data.Type;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Files.newBufferedWriter;

public class Main {
    public static void main(String[] args) {

        var book = new Book();
        Processor processor = new BookProcessor(book);

//        List<String> result = new ArrayList<>();
//        processor.process(Stream.of(
//                "First line",
//                "Second line"),
//                result::add);
//
//        // another test config:
//        StringJoiner resultSJ = new StringJoiner(System.lineSeparator());
//        processor.process("""
//                        First line
//                        Second line
//                        """.lines(),
//                resultSJ::add);

        // for file processing (stream)
//        try (var writer = newBufferedWriter(Path.of("output.txt"))) {
//            processor.process(
//                    Files.lines(Path.of("input.txt")),
//                    consume(writer));
//        } catch (IOException e) {
//            handleIoException(e);
//        }

        // for file processing (supplier)
        try (var reader = newBufferedReader(Path.of("input.txt"));
             var writer = newBufferedWriter(Path.of("output.txt"))) {
            processor.process(supply(reader), consume(writer));
        } catch (IOException e) {
            handleIoException(e);
        }
        }

    static void handleIoException(Exception e) {
        e.printStackTrace();
    }

    static Consumer<String> consume(BufferedWriter writer) {
        return s -> {
            try {
                writer.write(s);
                writer.newLine();
            } catch (IOException e) {
                handleIoException(e);
            }
        };
    }

    static Supplier<String> supply(BufferedReader reader) {
        return () -> {
            try {
                return reader.readLine();
            } catch (IOException e) {
                handleIoException(e);
            }
            return null;
        };
    }

    static Supplier<String> supply(List<String> list) {
        return new Supplier<>() {
            final Iterator<String> it = list.iterator();

            @Override
            public String get() {
                return it.hasNext() ? it.next() : null;
            }
        };
    }
}
