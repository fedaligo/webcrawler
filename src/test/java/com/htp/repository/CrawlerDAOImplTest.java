package com.htp.repository;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class CrawlerDAOImplTest {
    private CrawlerDAO crawlerDAO = new CrawlerDAOImpl();

    @Test
    public void isInt() {
        String word = "3";
        assertTrue(crawlerDAO.isInt(word));
    }

    @Test
    public void findLinksOnPage() {
        assertNotEquals(crawlerDAO.findLinksOnPage("https://en.wikipedia.org/", "100"), 0);
    }

    @Test
    public void getWord() {

    }

    @Test
    public void saveToCsvFile() {

    }

    @Test
    public void findTop() throws IOException {
        String[] word = new String[1];
        word[0] = "the";
        crawlerDAO.findLinksOnPage("https://en.wikipedia.org/", "100");
        crawlerDAO.deleteInfoCsvFile("file.csv");
        crawlerDAO.deleteInfoCsvFile("top10.csv");
        crawlerDAO.getWord(word);
        assertTrue(crawlerDAO.findTop());
    }

    @Test
    public void deleteInfoCsvFile() throws IOException {
        String fileName = "file.csv";
        File fl = new File(fileName);
        crawlerDAO.deleteInfoCsvFile(fileName);
        assertEquals((int) fl.length(), 0);
    }
}
