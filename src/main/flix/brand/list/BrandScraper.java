package flix.brand.list;

import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;


public class BrandScraper {


    private static final int MAX_DEPTH = 2; // Has to be 2
    private static HashSet<String> plink; // Raw URL list. To check if already seen web   (Does not include the endBrand)
    private HashSet<String> endBrand; // Filtered URL list w/ only brands
    public static Set<String> stopWords;

    private BrandScraper() {
        plink = new HashSet<String>();
        endBrand = new HashSet<String>();
    }


    public static void openStopWordsFile() throws FileNotFoundException {
        Scanner stopWordsFile = new Scanner(new File("./filter/filter.txt"));
        stopWords = new HashSet<String>();

        while (stopWordsFile.hasNext()){ stopWords.add(stopWordsFile.next().trim()); }

        stopWordsFile.close();
    }


    private HashSet<String> getPageLinks(String URL, int depth) {
        // Filter through lists of brands URL
        if (!plink.contains(URL) && (depth < MAX_DEPTH) && identifyEndBrand(URL)) { // Use identifyEndBrand method
            System.out.println(">> Depth: " + depth + " [" + URL + "]");

            try {
                plink.add(URL);
                Document doc = Jsoup.connect(URL).get();
                Elements linksOnPage = doc.select("a[href]");

                depth++;
                for (Element page : linksOnPage) {getPageLinks(page.attr("abs:href"), depth); }
            } catch (IOException e) {
                System.err.println("For '" + URL + "': " + e.getMessage());
            }

            // Extract from end brand URL
        }
        /* Ignore */
//        else if (!plink.contains(URL) && depth == MAX_DEPTH) {
//            if (identifyEndBrand(URL)) { endBrand.add(URL); System.out.println(">> Filtered:" + URL);}
//        }

        return plink;
    }


    public static boolean identifyEndBrand(String URL) {
        if (!URL.startsWith("https://en.wikipedia.org/wiki")){
            return false;
        }

        String id = URL.substring(30);

        Iterator<String> iterator = stopWords.iterator();
        while (iterator.hasNext()){
            // Filter out stop words by URL address
            if (id.contains(iterator.next()) || id.contains("#") || id.contains(":")) {
                return false;
            } else if (id.contains("List_of") && id.contains("brand")){
                return true;
            }
        }
        return true;
    }


    public static void main(String[] args) throws IOException, WriteException, BiffException {
        // Open stop words
        openStopWordsFile();

        // Get ending links
        new BrandScraper().getPageLinks("https://en.wikipedia.org/wiki/Lists_of_brands", 0);

        // Extract tables from each page, if can
        for (String link: plink) {
            Table.extractTable(link); // Auto extract list if table doesn't work
        }

        writeExcel test = new writeExcel();
        test.setOutputFile("data/testing.xls");
        test.write();
        System.out.println("Please check the output file under data/testing.xls ");
    }
}
