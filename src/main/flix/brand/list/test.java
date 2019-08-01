package flix.brand.list;

import java.io.*;
import java.util.ArrayList;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class test {


    public static void main(String[] args) throws FileNotFoundException, IOException {
        File input = new File("src/test/test.html");
        Document doc = Jsoup.parse(input, "UTF-8");

        Elements rowspans = doc.select("td[rowspan=2]");
        String text = rowspans.text();

        ArrayList<String> array = new ArrayList<String>();
        for (Element rowspan : rowspans) {
            array.add(rowspan.text());
        }

        System.out.println(text);
        System.out.println(array);
    }
}
