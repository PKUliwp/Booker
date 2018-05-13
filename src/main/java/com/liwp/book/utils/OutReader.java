package com.liwp.book.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * Created by liwp on 2018/5/12.
 */
@Slf4j
public class OutReader {

    @Setter
    private String outPath = "/Users/liwp/Desktop/out/";


    public Books getBooks(String link) throws Exception {
        OneReader oner = new OneReader();
        Document home = oner.getDoc(link);
        Elements chapters = oner.getChapters(home);
        String name = oner.getName(home);

        Books books = new Books();
        books.setName(name);

        System.out.println(chapters.size());

        int count = 0;
        for(Element e : chapters) {
            String url = link + e.attr("href");
            Book book = oner.getPage(url);
            book.setLink(url);
            book.setIndex(++count);

            books.addBook(book);

            log.info(e.text() + " " + e.attr("href"));
        }

        print(books);
        return books;
    }

    public void print(Books books) throws Exception{
        File outFile = new File(outPath + books.getName() + ".txt");
        FileWriter fileWriter = new FileWriter(outFile);

        for(Book book : books.getLists()) {
            fileWriter.write(String.valueOf(book.getIndex()) + "  " + book.getTitle() + "\n\n\n");
            fileWriter.write(book.getContent() + "\n\n\n");
        }

        fileWriter.close();
    }

    public static void main(String args[]) throws Exception {
        OutReader outr = new OutReader();
        outr.getBooks("");
    }
}
