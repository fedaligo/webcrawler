package com.htp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class Crawler {
    private HashSet<String> links;
    private List<List<String>> articles;
    private int stage = 0;
    private final String jpg = ".jpg";

    public Crawler() {
        links = new HashSet<>();
        articles = new ArrayList<>();
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
                        if (links.size() < 10000) {
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
                        if (links.size() < 10000) {
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

    public void getWord(String [] mass) {
        links.forEach(x -> {
            try {
                for (int z =0; z< mass.length; z++) {
                    int counter = 0;
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
                    System.out.println("The word - "+mass[z]+" is discovered " + counter + " times on this page");
                    in.close();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }

    public static void main(String[] args) {
        Crawler crawler = new Crawler();
        System.out.println("Enter the quantity of searching words");
        Scanner sc = new Scanner(System.in);
        int q = sc.nextInt();
        String [] mass = new String[q];
        sc.nextLine();
            for (int i=0; i<mass.length;i++){
                System.out.println("Enter the word for searching");
                mass[i]=sc.nextLine();
            }
        //1. Pick a URL from the frontier
        //crawler.getPageLinks("https://fabrika-uborki.by/");
        crawler.getPageLinks("https://en.wikipedia.org/wiki/Elon_Musk");
        //crawler.getPageLinks("https://en.wikipedia.org/");
        //crawler.getPageLinks("https://onliner.by/");
        //crawler.getPageLinks("https://tut.by/");
        //crawler.getPageLinks("http://vkontakte.ru/");
        //crawler.getArticles();
        crawler.getWord(mass);


    }
}
