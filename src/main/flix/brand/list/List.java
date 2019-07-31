package flix.brand.list;

import java.io.*;
import java.util.*;

import org.json.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class List {

    public static ArrayList<JSONObject> ARR_LIST = new ArrayList<JSONObject>();

    public List(){  // Initialize list
    }


    // Get all Lists for each brand lists secondary URL
    public static void getLists (String URL){
        try {
            // Get list of brand names
            Document doc = Jsoup.connect(URL).get();
            //Elements lists = doc.select("div.div-col.columns.column-width");
            Element content = doc.select("div.mw-content-ltr .mw-parser-output").get(0);
            Elements lists = content.select(":not(.navbox)");


            System.out.println(">>Checking List ..." + URL);
            if (lists == null || lists.isEmpty()){
                System.out.println(">>>> Does not contain: table, list");
                return;
            }


            for (Element list : lists){
                if (list.toString().contains("class=\"navbox")) {
                    continue;
                } else {
                    Elements li = list.select("ul li");
                    for (Element brand : li){
                        putArr(brand, URL);
                    }
                    break;
                }

            }
        } catch (IOException e) { System.err.println("For '" + URL + "': " + e.getMessage()); }
    }


    // Put all the data into Array
    public static void putArr(Element brand, String URL) throws IOException {
        JSONObject prod = getBrand(brand, URL);
        if (prod != null) {
            ARR_LIST.add(prod);
        }
    }


    public static String getBrandName(Element brand) { return brand.text(); }


    // Get more info if BrandName is not enough (all the time)
    public static JSONObject getBrand(Element brand, String URL) throws IOException {
        JSONObject prod = null;  // Info to add

        // Check link exists
        if (!brand.select("a[href]").toString().isEmpty()) {
            String link = brand.select("a[href]").attr("href");

            if (!identifyLink(link)){ System.out.println(">> Incorrect link" + link); return null; }


            // Put: category, brand, company, product, country. Before entering redlink
            prod = new JSONObject();
            prod.put("category", Category.findCategory(URL));
            prod.put("brand", getBrandName(brand));

            // Access the link
            link = "https://en.wikipedia.org" + link;
            System.out.println("Getting list link from:" + link);

            if (!link.contains("/w/index.php?title=") || !link.contains("&action=edit&redlink=1")) {
                Document doc = Jsoup.connect(link).get();
                Elements infoboxRow = doc.select("table.infobox tbody tr"); // Look at infobox

                // Loop through data in info box
                for (Element row : infoboxRow) {
                    String header = row.select("th").text().toLowerCase();
                    String data = row.select("td").text();

                    // Only check if both header and data is not empty
                    if (!header.isEmpty() && !data.isEmpty()){
                        HeadFilter hf = new HeadFilter();
                        if (hf.boolMatchString(header)){   // Check boolean of each type
                            prod.put(hf.getMatchString(),data);
                        }
                    }
                }
            }
        }

        if (prod != null){
            prod = missingFields(prod);
        }
        return prod;
    }


    public static boolean identifyLink(String url) throws FileNotFoundException {
        BrandScraper.openStopWordsFile();
        Set<String> stopWords = BrandScraper.stopWords;
        stopWords.remove(".php");

        if (!url.startsWith("/wiki/")){
            if (!(url.startsWith("/w/index.php?title=") &&
                    url.contains("&action=edit&redlink=1") )) {
                return false;
            }
        }


        Iterator<String> iterator = stopWords.iterator();
        while (iterator.hasNext()){
            // Filter out stop words by URL address
            if (url.contains(iterator.next()) || url.contains("#") || url.contains(":") || url.contains("List_of")) {
                return false;
            }
        }
        return true;
    }


    public static JSONObject missingFields(JSONObject json){
        if (!json.has("brand")){ json.put("brand", "NA"); }

        if (!json.has("company")){ json.put("company", "NA"); }

        if (!json.has("product")){ json.put("product", "NA"); }

        if (!json.has("country")){ json.put("country", "NA"); }

        return json;
    }


//    // Individual link (list) URL testing
//    public static void main(String[] args) throws IOException {
//        getLists("https://en.wikipedia.org/wiki/List_of_toy_soldiers_brands");
//
//        for (JSONObject obj: ARR_LIST) {
//            System.out.println(obj);
//        }
//    }
}
