package com.order;


import com.order.data.Book;
import com.order.data.Processor;
import com.order.data.Type;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
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
        var processor = new Processor() {

            @Override
            public void processOrder(String s) {
                Pattern pattern = Pattern.compile("o,(byu|sell),\\d+");
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    String[] split = s.split(",");
                    int length = split.length;
                    if (matcher.group(1).equals("buy")) {
                        book.buy(Integer.parseInt(split[length - 1]));
                    } else {
                        book.sell(Integer.parseInt(split[length - 1]));
                    }
                }
            }

            @Override
            public void processUpdate(String s) {
                Pattern pattern = Pattern.compile("u,\\d+,\\d+,(ask|bid)");
                Matcher matcher = pattern.matcher(s);
                String[] array;
                if (matcher.find()) {
                    array = matcher.group(0).split(",");
                    book.update(Integer.parseInt(array[1]), Integer.parseInt(array[2]), Type.getType(array[3]));
                } else {
                    System.out.println("No such element");
                }
            }

            @Override
            public String processQuery(String s) {
                Pattern pattern = Pattern.compile("_");
                String[] split = pattern.split(s);
                String rez = "";
                for (String s1 : split) {
                    if (s1.equals("bid") || s1.equals("ask")) {
                        return book.queryBest(Type.getType(s1));
                    }
                }
                Pattern patternInt = Pattern.compile("\\d+");
                Matcher match = patternInt.matcher(s);
                if (match.find()) {
                    return book.queryByPrice(Integer.parseInt(match.group()));
                }
                return rez;
            }
        };

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
        try (var writer = newBufferedWriter(Path.of("output.txt"))) {
            processor.process(
                    Files.lines(Path.of("input.txt")),
                    consume(writer));
        } catch (IOException e) {
            handleIoException(e);
        }

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
