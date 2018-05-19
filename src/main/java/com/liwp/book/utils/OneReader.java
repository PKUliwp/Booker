package com.liwp.book.utils;


import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by liwp on 2018/5/11.
 */
@Slf4j
@Component
public class OneReader {

    private String path = "/Users/liwp/Desktop/out/";

    public OneReader() {

    }

    public void getPagesByURLs(String baseURL, int base) throws Exception {
        FileWriter writer = new FileWriter(path);

        for(int i=0; i<=4; i++) {
            System.out.println(i);
            Book book = getPage(baseURL + String.valueOf(base + i) + ".html");
            writer.write("第" + String.valueOf(i+1) + "章   " + book.getTitle() + "\n\n\n");
            writer.write(book.getContent() + "\n\n\n");
        }

        writer.close();
    }

    public Book getPage(String url) throws Exception {
        Document doc = getDoc(url);
        String baseURL = url.substring(0, url.indexOf(".html"));

        Book book = new Book();
        book.setTitle(title(doc));

        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append(content(doc));
        int pageNums = countSubPages(doc);
        for(int i=1; i< pageNums; i++) {
            Document tmp = getDoc(baseURL +  '_' + String.valueOf(i+1) + ".html");
            contentBuilder.append(content(tmp));
        }

        book.setContent(contentBuilder.toString());

        return book;
    }

    public Elements getChapters(Document home) {
        Element list = home.getElementsByClass("dd").get(0);
        Elements chapters = list.getElementsByTag("a");
        return chapters;
    }

    public String getName(Document home) {
        return home.getElementsByTag("h2").get(1).text();
    }

    public String title(Document doc) {
        Element e = doc.getElementsByTag("h2").get(0);
        return e.text()
                .replaceAll("正文|【|】", "");
    }

    public String content(Document doc) {
        Elements elements = doc.getElementsByClass("Con box_con");
        //System.out.println(elements.size());
        StringBuilder builder = new StringBuilder();
        for(Element e : elements) {
            builder.append(convert(e.html()));
        }
        return builder.toString();
    }

    public int countSubPages(Document doc) {
        Elements as = doc.getElementsByTag("a");
        int count = 0;
        for(Element a : as) {
            if(a.text().indexOf("第") >= 0 && a.text().indexOf("节") >= 0) count ++;
        }
        //System.out.println(count);
        return count;
    }

    public Document getDoc(String link) throws Exception {
        URL url = new URL(link);
        URLConnection uc = url.openConnection();
        uc.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
        Document doc = new Document("");

        try {
            doc = Jsoup.parse(uc.getInputStream(), "gbk", "");
        } catch (Exception e) {
            e.printStackTrace();
            Thread.sleep(3000);
            return getDoc(link);
        }

        return doc;
    }

    public String convert(String s) {
        String res = s
                .replaceAll("<script[^<]*</script>", "\r")
                .replaceAll("&nbsp;", "")
                .replaceAll("<br>(\\s)*\n", "\r")
                .replaceAll("[^\r\\S]", "")
                .replaceAll("<br>", "")
                .replaceAll("\r", "\n\n");
        String[] lines = res.split("\n");
        //System.out.println(res);

        int count = 0;
        for(String line : lines) {
            if(line.length() > 5) count ++;
        }
        //System.out.println(count);
        if(count < 21) {
            //log.info(String.valueOf(count));
            res = res.replaceAll("　　", "\n　　");
            lines = res.split("\n");
        }

        StringBuilder builder = new StringBuilder();
        for(String line : lines) {
            //if(line.indexOf("&nbsp") > 0) continue;
            if(line.indexOf("<") >= 0) continue;
            if(line.indexOf("&brvbar;") >= 0) continue;

            builder
                    .append(line)
                    .append("\n");
        }
        res = builder.toString();
        //System.out.println(s);
        return res;
    }


    public static void main(String args[]) throws Exception {
        OneReader oner = new OneReader();
        //System.out.println(oner.getPage("http://www.diyibanzhu.xyz/7/7260/121728.html").getContent());
    }


}
