package com.htp.service;

import com.htp.repository.CrawlerDAO;
import com.htp.repository.CrawlerDAOImpl;

import java.io.IOException;
import java.util.Scanner;

public class CrawlerService {
    public void start () throws IOException {
        CrawlerDAO crawler = new CrawlerDAOImpl();
        System.out.println("Enter the quantity of searching words");
        Scanner sc = new Scanner(System.in);
        String q = sc.nextLine();
        while(!crawler.isInt(q)){
            System.out.println("Enter a correct number");
            q = sc.nextLine();
        }
        String[] mass = new String[Integer.parseInt(q)];
        for (int i = 0; i < mass.length; i++) {
            System.out.println("Enter the " + (i+1) + " word for searching");
            mass[i] = sc.nextLine();
        }
        //1. Pick a URL from the frontier
        //crawler.getPageLinks("https://fabrika-uborki.by/");
        //crawler.getPageLinks("https://en.wikipedia.org/wiki/Elon_Musk");
        crawler.getPageLinks("https://en.wikipedia.org/");
        //crawler.getPageLinks("https://onliner.by/");
        //crawler.getPageLinks("https://tut.by/");
        //crawler.getPageLinks("http://vkontakte.ru/");
        //crawler.getArticles();
        crawler.deleteInfoCsvFile("file.csv");
        crawler.deleteInfoCsvFile("top10.csv");
        crawler.getWord(mass);
        crawler.findTop();
    }
}
