package com.htp.repository;

import java.io.IOException;

public interface CrawlerDAO {
    public boolean isInt(String x) throws NumberFormatException;
    public int getPageLinks(String URL);
    public void getWord(String[] mass);
    public void writeToCsvFile(String[] x, String fileName) throws IOException;
    public boolean findTop() throws IOException;
    public void deleteInfoCsvFile(String fileName) throws IOException;
}
