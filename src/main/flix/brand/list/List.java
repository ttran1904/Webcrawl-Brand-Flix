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

    /**
    * Get list for each category list if Table does not work
    */
    public static void getLists (String URL){
        try {
            // Get list of brands
            Document doc = Jsoup.connect(URL).get();
            Element content = doc.select("div.mw-content-ltr .mw-parser-output").get(0);
            Elements lists = content.select(":not(.navbox)");

            System.out.println(">> Checking List ..." + URL);
            if (lists == null || lists.isEmpty()){
                System.out.println(">>>> Does not contain: table, list");
                return;
            }

            for (Element list : lists){
                if (list.toString().contains("class=\"navbox")) {  // Ignore class="navbox..." prefix
                    continue;
                } else {
                    Elements li = list.select("ul li");
                    li.select("span").remove();
                    for (Element brand : li) { putArr(brand, URL); }  // Put in larger array ARR_LIST
                    break;
                }
            }
        } catch (IOException e) { System.err.println("For '" + URL + "': " + e.getMessage()); }
    }

    /**
     * Put all the data into stored ARR_LIST
     */
    public static void putArr(Element brand, String URL) throws IOException {
        JSONObject prod = getBrand(brand, URL);
        if (prod != null) {
            ARR_LIST.add(prod);
        }
    }

    /**
     * Put all the data into stored ARR_LIST
     */
    public static String getBrandName(Element brand) { return brand.text(); }


    /**
     * Get further info of brand via a[href] link attached, if possible
     */
    public static JSONObject getBrand(Element brand, String URL) throws IOException {
        JSONObject prod = null;


        if (!brand.select("a[href]").toString().isEmpty()) {   // Check if link exists
            String link = brand.select("a[href]").attr("href");

            if (!identifyLink(link)) { System.out.println(">> Incorrect link" + link); return null; }   // Filter desired links

            // Before entering redlink, put category & brand
            prod = new JSONObject();
            prod.put("category", Category.findCategory(URL));
            prod.put("brand", getBrandName(brand));

            link = "https://en.wikipedia.org" + link;
            System.out.println("Getting list link from:" + link);
            // Access link
            if (!link.contains("/w/index.php?title=") || !link.contains("&action=edit&redlink=1")) {
                Document doc = Jsoup.connect(link).get();
                Elements infoboxRow = doc.select("table.infobox tbody tr"); // Look at infobox

                // Loop through data in info box
                for (Element row : infoboxRow) {
                    String header = row.select("th").text().toLowerCase();
                    String data = row.select("td").text();

                    if (!header.isEmpty() && !data.isEmpty()){  // Only check if both header and data is not empty
                        HeadFilter hf = new HeadFilter();
                        if (hf.boolMatchString(header)){
                            prod.put(hf.getMatchString(),data);
                        }
                    }
                }
            }
        }
        if (prod != null){ prod = Category.fillMissingFields(prod); }
        return prod;
    }

    /**
     * Identify correct link "/wiki/" or "/w/index.php?=" with the correct red link specifications
     */
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
            if (url.contains(iterator.next())
                    || url.contains("#")
                    || url.contains(":")
                    || url.contains("List_of")
                    || url.contains("Lists_of_")) {
                return false;
            }
        }
        return true;
    }

    /**
     * FIX THIS!! to replace missing company with brand that already exists
     */



//    // Individual link (list) URL testing
//    public static void main(String[] args) throws IOException {
//        getLists("https://en.wikipedia.org/wiki/List_of_defunct_consumer_brands");
//
//        for (JSONObject obj: ARR_LIST) {
//            System.out.println(obj);
//        }
//    }
}
