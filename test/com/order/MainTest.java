package com.order;

import com.order.data.Book;
import com.order.data.BookProcessor;
import com.order.data.Processor;
import com.order.data.Type;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void main() {
    }


    @Test
    @DisplayName("getSizeByPrice()")
    void getSizeByPrice() {
        var book = new Book();
        Processor processor = new BookProcessor(book);
        processor.processLine("u,20,5,ask");
        processor.processLine("u,21,10,ask");
        processor.processLine("u,22,15,ask");
        processor.processLine("u,10,5,bid");
        processor.processLine("u,9,10,bid");
        processor.processLine("u,8,15,bid");

        assertEquals("5", processor.processQuery("q,size,20"));
        assertEquals("10", processor.processQuery("q,size,21"));
        assertEquals("15", processor.processQuery("q,size,22"));
        assertEquals("5", processor.processQuery("q,size,10"));
        assertEquals("10", processor.processQuery("q,size,9"));
        assertEquals("15", processor.processQuery("q,size,8"));
        assertEquals("0", processor.processQuery("q,size,888"));
    }


    @Test
    @DisplayName("best ask & bid()")
    void getBest() {
        var book = new Book();
        Processor processor = new BookProcessor(book);

        processor.processLine("u,20,5,ask");
        processor.processLine("u,21,10,ask");
        processor.processLine("u,18,15,ask");

        var res = processor.processQuery("q,best_ask");
        assertEquals("18,15", res);

        processor.processLine("u,9,10,bid");
        processor.processLine("u,8,5,bid");
        processor.processLine("u,10,15,bid");

        res = processor.processQuery("q,best_bid");
        assertEquals("10,15", res);
    }


    @Test
    @DisplayName("sell")
    void insertBidOrder(){
        var book = new Book();
        Processor processor = new BookProcessor(book);
        processor.processLine("u,22,15,ask");
        processor.processLine("u,21,10,ask");
        processor.processLine("u,20,5,ask");
        processor.processLine("u,10,5,bid");
        processor.processLine("u,9,10,bid");
        processor.processLine("u,8,15,bid");
        processor.processLine("o,sell,3");

        assertEquals("10,2", processor.processQuery("q,best_bid"));

        processor.processLine("o,sell,7");
        assertEquals("9,5", processor.processQuery("q,best_bid"));

        processor.processLine("o,buy,3");
        assertEquals("20,2", processor.processQuery("q,best_ask"));

        processor.processLine("o,buy,7");
        assertEquals("21,5", processor.processQuery("q,best_ask"));
    }


    @Test
    @DisplayName("removeAllOrders()")
    void removeAllOrders(){
        var book = new Book();
        Processor processor = new BookProcessor(book);

        processor.processLine("u,20,5,ask");
        processor.processLine("u,21,10,ask");
        processor.processLine("u,22,15,ask");
        processor.processLine("u,10,5,bid");
        processor.processLine("u,9,10,bid");
        processor.processLine("u,8,15,bid");

        processor.processLine("o,buy,100");
        processor.processLine("o,sell,100");

        assertEquals("0", processor.processQuery("q,size,100"));

    }

    @Test
    void addedThreeAsk_shouldReturnBestNonZeroAsk_processQuery(){
        var book = new Book();
        Processor processor = new BookProcessor(book);

        processor.processLine("u,99,0,ask");
        processor.processLine("u,98,50,ask");
        processor.processLine("u,97,100,ask");
        processor.processLine("u,97,0,ask");
        var res = processor.processQuery("q,best_ask");
        assertEquals("98,50", res);
    }





    @Test
    @DisplayName("insertMiddleOrder1()")
    void insertMiddleOrder1() {
        var book = new Book();
        Processor processor = new BookProcessor(book);

        processor.processLine("u,22,15,ask");
        processor.processLine("u,21,10,ask");
        processor.processLine("u,20,5,ask");

        processor.processLine("u,21,20,bid");

        assertEquals("21,20", processor.processQuery("q,best_bid"));

        processor.processLine("u,10,5,bid");
        processor.processLine("u,9,10,bid");
        processor.processLine("u,8,15,bid");

        processor.processLine("u,9,30,ask");

        assertEquals("9,30", processor.processQuery("q,best_ask"));
    }



}
