package com.order.data;

import java.util.*;


public class Book {
    private Map<Account, Integer> accounts = new TreeMap<>((o1, o2) -> Integer.compare(o2.getPrice(), o1.getPrice()));

    public void update(int price, int quantity, Type type) {
        accounts.keySet().stream()
                .filter(y -> y.getPrice() == price)
                .findAny()
                .ifPresentOrElse(y -> checkType(price, type, quantity), () -> accounts.put(new Account(price, type), quantity));
    }

    public String queryBest(Type type) { ////make realization of writing
        if (type == Type.Asker) {
            return accounts.keySet().stream()
                    .filter(account -> account.getType().equals(Type.Asker))
                    .filter(account -> accounts.get(account)>0)
                    .min(Comparator.comparing(Account::getPrice))
                    .map(account -> {
                        int price = account.getPrice();
                        int quantity = accounts.get(account);
                        return String.format("%d,%d", price, quantity);
                    })
                    .orElse("0");
        } else {
            return accounts.keySet().stream()
                    .filter(account -> account.getType().equals(Type.Bider))
                    .max(Comparator.comparing(Account::getPrice))
                    .map(account -> {
                        int price = account.getPrice();
                        int quantity = accounts.get(account);
                        return String.format("%d,%d", price, quantity);
                    })
                    .orElse("0");
        }
    }

    public String queryByPrice(int price) {
        return accounts.keySet().stream()
                .filter(y -> y.getPrice() == price)
                .findAny().map(account -> {
                    int quantity = accounts.get(account);
                    return String.format("%d", quantity);
                }).orElse("0");
    }

    public void buy(int quantity) {
        accounts.keySet().stream()
                .filter(y -> y.getType().equals(Type.Asker))
                .min(Comparator.comparing(Account::getPrice))
                .ifPresent(account -> {
                    int integer = accounts.get(account) - quantity;
                    if (integer < 0) {
                        accounts.remove(account);
                        int rest = Math.abs(integer);
                        buy(rest);
                    } else if (integer == 0) {
                        accounts.remove(account);
                    } else {
                        accounts.put(account, accounts.get(account) - quantity);
                    }
                });
    }

    public void sell(int quantity) {
        accounts.keySet().stream()
                .filter(y -> y.getType().equals(Type.Bider))
                .max(Comparator.comparing(Account::getPrice))
                .ifPresent(account -> {
                    int integer = accounts.get(account) - quantity;
                    if (integer < 0) {
                        accounts.remove(account);
                        int rest = Math.abs(integer);
                        sell(rest);
                    } else if (integer == 0) {
                        accounts.remove(account);
                    } else {
                        accounts.put(account, accounts.get(account) - quantity);
                    }
                });
    }

    private void checkType(int price, Type type, int quantity) {
        accounts.keySet().stream()
                .filter(y -> y.getPrice() == price && y.getType() == type)
                .findAny()
                .ifPresentOrElse(account -> updateQuantity(quantity, account),
                        () -> checkTypeInverse(price, type, quantity));
    }

    private void checkTypeInverse(int price, Type type, int quantity) {
        accounts.keySet().stream()
                .filter(y -> y.getPrice() == price)
                .filter(y -> y.getType() != type)
                .filter(account -> account.getType().equals(Type.Asker))
                .findAny().ifPresentOrElse(y -> updateAskers(price, type, quantity), () -> updateBider(price, type, quantity));
    }

    private void updateBider(int price, Type type, int quantity) {
        Account acc = accounts.keySet().stream()   //here comes Asker
                .filter(account -> account.getType().equals(Type.Bider))
                .filter(y -> y.getPrice() == price)
                .max(Comparator.comparing(Account::getPrice))
                .orElse(null);
        if (acc == null) {
            accounts.put(new Account(price, type), quantity);
            return;
        }
        if (acc.getPrice() == price) {
            accounts.remove(acc);
            update(price, quantity, type);
        }
    }

    private void updateAskers(int price, Type type, int quantity) {
        Account acc = accounts.keySet().stream()   //here comes Bider
                .filter(account -> account.getType().equals(Type.Asker))
                .filter(y -> y.getPrice() == price)
                .min(Comparator.comparing(Account::getPrice))
                .filter(y -> y.getPrice() == price)
                .orElse(null);
        if (acc == null) {
            accounts.put(new Account(price, type), quantity);
            return;
        }
        if (acc.getPrice() == price) {
            accounts.remove(acc);
            update(price, quantity, type);
        }
    }

    private void updateQuantity(int quantity, Account account) {
        accounts.put(account, quantity);
    }

}
