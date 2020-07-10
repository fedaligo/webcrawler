package com.htp.service;

import com.htp.repository.CrawlerDAO;
import com.htp.repository.CrawlerDAOImpl;

import java.io.IOException;
import java.util.Scanner;

/**
 * Class with a full application loop.
 * @autor Igor Fedonenkov
 * @version 1.0
 */
public class CrawlerService {

    /**
     * Method for starting a full application loop
     */
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
        crawler.findLinksOnPage("https://en.wikipedia.org/");
        crawler.deleteInfoCsvFile("file.csv");
        crawler.deleteInfoCsvFile("top10.csv");
        crawler.getWord(mass);
        crawler.findTop();
    }
}
