package com.order.data;

import java.util.NoSuchElementException;

public enum Type {
    Bider("bid"),
    Asker("ask");

    private final String type;

    Type(String type) {
        this.type = type;
    }

    public static Type getType(String s) {
        for (Type t : Type.values()) {
            if (t.type.equals(s)) {
                return t;
            }
        }
        throw new NoSuchElementException("No such type in book " + s);
    }
}
