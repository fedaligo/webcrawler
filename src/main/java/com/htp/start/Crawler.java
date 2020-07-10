package com.htp.start;

import com.htp.service.CrawlerService;
import java.io.*;

public class Crawler {

    public static void main(String[] args) throws IOException {
        CrawlerService crawlerService = new CrawlerService();
        crawlerService.start();
    }
}
