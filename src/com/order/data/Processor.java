package com.order.data;


import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class Processor {

    public void process(Supplier<String> source,
                        Consumer<String> drain) {
        String s;
        while ((s = source.get()) != null) {
            processLine(s).forEach(drain);
        }
    }

    public void process(Stream<String> source,
                        Consumer<String> drain) {
        source.flatMap(this::processLine).forEach(drain);
    }

    Stream<String> processLine(String s) {
        String res = null;
        switch (s.charAt(0)) {
            case 'q' -> res = processQuery(s);
            case 'u' -> processUpdate(s);
            case 'o' -> processOrder(s);
            default -> throw new IllegalStateException("Unexpected value: " + s.charAt(0));
        }
        return Stream.ofNullable(res);
    }

    public abstract void processOrder(String s);

    public abstract void processUpdate(String s);

    public abstract String processQuery(String s);
}