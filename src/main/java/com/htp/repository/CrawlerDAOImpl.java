package com.htp.repository;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Class with main functions of crawler.
 * @autor Igor Fedonenkov
 * @version 1.0
 */
public class CrawlerDAOImpl implements CrawlerDAO {

    /** Collection with finded links */
    private HashSet<String> links = new HashSet<>();

    /** Number of entry stage */
    private int stage = 0;

    /** A string to check the link for mismatch of the link to the picture */
    private final String jpg = ".jpg";

    /** Collection with information about matching words */
    private List<String[]> write = new ArrayList<>();

    /** Variable for counting top 10 */
    private int max = 0;

    /**
     * Method for checking a string against a number
     * @param word - string for checking
     * @return returns boolean match or mismatch value
     */
    @Override
    public boolean isInt(String word) throws NumberFormatException {
        try {
            Integer.parseInt(word);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Method for searching all links on the current page
     * @param URL - string of link
     * @param numberOfLinks - maximum number of unique links in the array
     * @return returns quantity of unique links
     */
    @Override
    public int findLinksOnPage(String URL, String numberOfLinks) {
        try {
            System.out.println("I am on the LINK " + URL);
            System.out.println("Number of unique links in the array - " + links.size());
            System.out.println("-----------------------");
            Document document = Jsoup.connect(URL).get();
            Elements linksOnPage = document.select("a[href]");
            for (int page = 0; page < linksOnPage.size(); page++) {
                if (stage == 0) {
                    for (Element pg : linksOnPage) {
                        if (links.size() < Integer.parseInt(numberOfLinks)) {
                            if (pg.attr("abs:href").indexOf(jpg) != -1 |
                                    pg.attr("abs:href").trim().isEmpty() == true) {
                                System.out.println("image or empty");
                                continue;
                            } else {
                                if (links.add(pg.attr("abs:href"))) {
                                    System.out.println(pg.attr("abs:href"));
                                }
                            }
                        } else {
                            break;
                        }
                    }
                    stage = stage + 1;
                    System.out.println("Going to the stage - " + stage);
                    if (URL.equals(linksOnPage.get(page).attr("abs:href"))) {
                        stage = stage - 1;
                        continue;
                    } else {
                        findLinksOnPage(linksOnPage.get(page).attr("abs:href"), numberOfLinks);
                    }
                }
                if (links.contains(linksOnPage.get(page).attr("abs:href")) == false) {
                    if (stage < 8) {
                        if (links.size() < Integer.parseInt(numberOfLinks)) {
                            if (linksOnPage.get(page).attr("abs:href").indexOf(jpg) != -1 |
                                    linksOnPage.get(page).attr("abs:href").trim().isEmpty() == true) {
                                System.out.println("image or empty");
                                continue;
                            } else {
                                links.add(linksOnPage.get(page).attr("abs:href"));
                                System.out.println("Link is added - " +
                                        linksOnPage.get(page).attr("abs:href"));
                                stage = stage + 1;
                                System.out.println("Going to the stage - " + stage);
                                findLinksOnPage(linksOnPage.get(page).attr("abs:href"), numberOfLinks);
                            }
                        } else {
                            System.out.println("Limit is - " + numberOfLinks);
                            break;
                        }
                    } else {
                        System.out.println("Attempt to exceed the stage - 8");
                        System.out.println("Total links per page -" + (linksOnPage.size() - 1) + ", I am on -" + page);
                        if(page < linksOnPage.size() - 1){
                            continue;
                        }
                    }
                } else {
                    System.out.println("Link is already exist - " + (linksOnPage.get(page).attr("abs:href")));
                    System.out.println("Total links per page -" + (linksOnPage.size() - 1) + ", I am on -" + page);
                }
                if (page == linksOnPage.size() - 1) {
                    System.out.println("Stage " + stage + " is done. Total links per page -" +
                            (linksOnPage.size() - 1) + ", I am on -" + page);
                    stage = stage - 1;
                    System.out.println("Come back to the stage - " + stage);
                }
            }
            System.out.println("Exit from the method");
            return links.size();
        } catch (IOException e) {
            System.err.println("For '" + URL + "': " + e.getMessage());
            return 0;
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return 0;
        }
    }

    /**
     * Method for searching some words on the current page
     * @param mass - array of string with information about searching words
     */
    @Override
    public void getWord(String[] mass) {
        links.forEach(x -> {
            try {
                for (int z = 0; z < mass.length; z++) {
                    int counter = 0;
                    String[] info = new String[3];
                    URL oracle = new URL(x);
                    URLConnection yc = oracle.openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        String[] words = inputLine.toLowerCase().replaceAll("[^a-zA-Zа-яёА-ЯЁ]", " ")
                                .split("\\s");
                        for (int i = 0; i < words.length; i++) {
                            if (!words[i].isEmpty()) {
                                if (words[i].equals(mass[z].toLowerCase())) {
                                    counter++;
                                }
                            }
                        }
                    }
                    System.out.println(x);
                    System.out.println("The word - " + mass[z] + " is discovered " + counter + " times on this page");
                    if (counter >= max) {
                        max = counter;
                    }
                    info[0] = x;
                    info[1] = mass[z];
                    info[2] = String.valueOf(counter);
                    write.add(info);
                    saveToCsvFile(info, "file.csv");
                    in.close();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }

    /**
     * Method for recording found matches in a file
     * @param x - array of string with information about searching words
     * @param fileName - name of file where will be saved information
     */
    @Override
    public void saveToCsvFile(String[] x, String fileName) throws IOException {
        File fl = new File(fileName);
        FileWriter pw = null;
        try {
            pw = new FileWriter(fl, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedWriter bw = new BufferedWriter(pw);
        StringBuilder builder = new StringBuilder();
        String ColumnNamesList = "Link;Searching word;Result of searching";
        if (fl.length() == 0) {
            builder.append(ColumnNamesList + "\n");
        }
        builder.append(x[0] + ";");
        builder.append(x[1] + ";");
        builder.append(x[2]);
        bw.newLine();
        bw.write(builder.toString());
        bw.close();
        pw.close();
    }

    /**
     * Method for searching top 10 of the most popular matches among all
     * @return returns boolean value of successful or unsuccessful result
     */
    @Override
    public boolean findTop() throws IOException {
        int count = 0;
        if (write.size() < 10) {
            return false;
        }
        for (int i = 0; i < write.size(); i++) {
            if (count != 10) {
                if (i == write.size() - 1) {
                    i = 0;
                    int mx = 0;
                    for (int p = 0; p < write.size(); p++) {
                        if (Integer.parseInt(write.get(p)[2]) > mx && Integer.parseInt(write.get(p)[2]) < max) {
                            mx = Integer.parseInt(write.get(p)[2]);
                        }
                    }
                    max = mx;
                }
                if (Integer.parseInt(write.get(i)[2]) == max) {
                    System.out.println(write.get(i)[0]);
                    System.out.println("The word - " + write.get(i)[1] + " is discovered " + write.get(i)[2] +
                            " times on this page");
                    saveToCsvFile(write.get(i), "top10.csv");
                    count++;
                }
            } else {
                break;
            }
        }
        return true;

    }

    /**
     * Method for deleting of all information in a file
     * @param fileName - name of file where will be deleted information
     */
    @Override
    public void deleteInfoCsvFile(String fileName) throws IOException {
        File fl = new File(fileName);
        FileWriter pw = null;
        try {
            pw = new FileWriter(fl);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedWriter bw = new BufferedWriter(pw);
        bw.write("");
        bw.close();
        pw.close();
    }
}
