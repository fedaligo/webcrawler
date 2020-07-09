package com.htp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.UniqueHashCode;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class Crawler {
    private HashSet<String> links;
    private int stage = 0;
    private final String jpg = ".jpg";
    private List<String[]> write = new ArrayList<>();
    private int max = 0;

    public Crawler() {
        links = new HashSet<>();
    }

    public void getPageLinks(String URL) {
        try {
            System.out.println("I am on the LINK " + URL);
            System.out.println(links.size());
            System.out.println("-----------------------");
            //2. Fetch the HTML code
            Document document = Jsoup.connect(URL).get();
            //3. Parse the HTML to extract links to other URLs
            Elements linksOnPage = document.select("a[href]");
            for (int page = 0; page < linksOnPage.size(); page++) {
                if (stage == 0) {
                    for (Element pg : linksOnPage) {
                        if (links.size() < 100) {
                            if (pg.attr("abs:href").indexOf(jpg) != -1 | pg.attr("abs:href").trim().isEmpty() == true) {
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
                        getPageLinks(linksOnPage.get(page).attr("abs:href"));
                    }
                }
                if (links.contains(linksOnPage.get(page).attr("abs:href")) == false) {
                    if (stage < 8) {
                        if (links.size() < 100) {
                            if (linksOnPage.get(page).attr("abs:href").indexOf(jpg) != -1 | linksOnPage.get(page).attr("abs:href").trim().isEmpty() == true) {
                                System.out.println("image or empty");
                                continue;
                            } else {
                                links.add(linksOnPage.get(page).attr("abs:href"));
                                System.out.println("Link is added - " + linksOnPage.get(page).attr("abs:href"));
                                stage = stage + 1;
                                System.out.println("Going to the stage - " + stage);
                                getPageLinks(linksOnPage.get(page).attr("abs:href"));
                            }
                        } else {
                            System.out.println("Limit is 10000");
                            break;
                        }
                    } else {
                        System.out.println("Attempt to exceed the stage - 8");
                        System.out.println("Total links per page -" + linksOnPage.size() + ", I am on -" + page);
                        continue;
                    }
                } else {
                    System.out.println("Link is already exist");
                    System.out.println("Total links per page -" + linksOnPage.size() + ", I am on -" + page);
                }
                if (page == linksOnPage.size() - 1) {
                    System.out.println("Stage " + stage + " is done. Total links per page -" + linksOnPage.size() +
                            ", I am on -" + page);
                    stage = stage - 1;
                    System.out.println("Come back to the stage - " + stage);
                }
            }
            System.out.println("Exit from the method");
        } catch (IOException e) {
            System.err.println("For '" + URL + "': " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }

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
                        //System.out.println(inputLine.toLowerCase().replaceAll("[^a-zA-Zа-яёА-ЯЁ]", "")); // построчный вывод всей страницы на экран
                        // добавьте код для сравнения/поиска подстроки в строке
                    /*if (inputLine.contains(pat)) {
                        counter++;}*/
                        String[] words = inputLine.toLowerCase().replaceAll("[^a-zA-Zа-яёА-ЯЁ]", " ").split("\\s");
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
                    writeToCsvFile(info,"file.csv");
                    in.close();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }


    public void writeToCsvFile(String[] x, String fileName) throws IOException {
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

    public void findTop() throws IOException {
        List<Integer> y = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < write.size(); i++){
            if(count!=10) {
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
                    System.out.println("The word - " + write.get(i)[1] + " is discovered " + write.get(i)[2] + " times on this page");
                    writeToCsvFile(write.get(i), "top10.csv");
                    count++;
                }
            } else {
                break;
            }
        }
    }

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

    public static void main(String[] args) throws IOException {
        Crawler crawler = new Crawler();
        System.out.println("Enter the quantity of searching words");
        Scanner sc = new Scanner(System.in);
        int q = sc.nextInt();
        String[] mass = new String[q];
        sc.nextLine();
        for (int i = 0; i < mass.length; i++) {
            System.out.println("Enter the word for searching");
            mass[i] = sc.nextLine();
        }
        //1. Pick a URL from the frontier
        //crawler.getPageLinks("https://fabrika-uborki.by/");
        crawler.getPageLinks("https://en.wikipedia.org/wiki/Elon_Musk");
        //crawler.getPageLinks("https://en.wikipedia.org/");
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
