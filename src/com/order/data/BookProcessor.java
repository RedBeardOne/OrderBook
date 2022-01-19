package com.order.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookProcessor extends Processor {
    private Book book;

    public BookProcessor(Book book) {
        this.book = book;
    }

    @Override
    public void processOrder(String s) {
        Pattern pattern = Pattern.compile("o,(buy|sell),\\d+");
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
}
