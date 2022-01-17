package com.order.data;

public class Account {
    private int price;
    private Type type;

    public Account(int price, Type type) {
        this.price = price;
        this.type = type;
    }
    public int getPrice() {
        return price;
    }

    public Type getType() {
        return type;
    }
}
