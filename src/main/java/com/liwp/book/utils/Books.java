package com.liwp.book.utils;

import lombok.Data;

import java.util.ArrayList;

/**
 * Created by liwp on 2018/5/13.
 */
@Data
public class Books {
    private ArrayList<Book> lists = new ArrayList<>();
    private String name = "out";

    public void addBook(Book book) {
        lists.add(book);
    }
}
