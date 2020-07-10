package com.htp.start;

import com.htp.service.CrawlerService;
import java.io.*;

/**
 * Class with main method of application.
 * @autor Igor Fedonenkov
 * @version 1.0
 */
public class Crawler {

    /**
     * Main method of application
     */
    public static void main(String[] args) throws IOException {
        CrawlerService crawlerService = new CrawlerService();
        crawlerService.start();
    }
}
