package com.htp.repository;

import java.io.IOException;

/**
 * Class with main functions of crawler.
 * @autor Igor Fedonenkov
 * @version 1.0
 */
public interface CrawlerDAO {

    /**
     * Method for checking a string against a number
     * @param word - string for checking
     * @return returns boolean match or mismatch value
     */
    public boolean isInt(String word) throws NumberFormatException;

    /**
     * Method for searching all links on the current page
     * @param URL - string of link
     * @param numberOfLinks - maximum number of unique links in the array
     * @return returns quantity of unique links
     */
    public int findLinksOnPage(String URL, String numberOfLinks);

    /**
     * Method for searching some words on the current page
     * @param mass - array of string with information about searching words
     */
    public void getWord(String[] mass);

    /**
     * Method for recording found matches in a file
     * @param x - array of string with information about searching words
     * @param fileName - name of file where will be saved information
     */
    public void saveToCsvFile(String[] x, String fileName) throws IOException;

    /**
     * Method for searching top 10 of the most popular matches among all
     * @return returns boolean value of successful or unsuccessful result
     */
    public boolean findTop() throws IOException;

    /**
     * Method for deleting of all information in a file
     * @param fileName - name of file where will be deleted information
     */
    public void deleteInfoCsvFile(String fileName) throws IOException;
}
