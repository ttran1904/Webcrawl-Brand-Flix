package flix.brand.list;

import java.util.*;

public class HeadFilter {

    public static ArrayList<String> brand;
    public static ArrayList<String> company;
    public static ArrayList<String> product;
    public static ArrayList<String> country;

    private static HashMap<String, ArrayList<String>> all;

    private static boolean match;
    private static String cate;

    public HeadFilter(){
        brand = new ArrayList<String>();
        brand.add("brand");
        brand.add("model");
        brand.add("name");
        brand.add("card");

        company = new ArrayList<String>();
        company.add("parent");
        company.add("company");
        company.add("companies");
        company.add("manufacture");
        company.add("owner");
        company.add("created by");

        product = new ArrayList<String>();
        product.add("product");
        product.add("note");

        country = new ArrayList<String>();
        country.add("country");
        country.add("nation");
        country.add("origin");
        country.add("headquarter");
        country.add("countries");

        match = false;
        cate = null;

        addMapping();
    }

    public static void addMapping(){
        all = new HashMap<String, ArrayList<String>>();
        all.put("brand", brand);
        all.put("company", company);
        all.put("product", product);
        all.put("country", country);
    }


    public static boolean boolMatchString (String str) {
//        // Setup mapping
//        addMapping();

        // Loop through string if contains word in the mapping set
        for (Map.Entry<String, ArrayList<String>> pair : all.entrySet()){
            for (String name : pair.getValue()) {
                if (str.contains(name)){
                    // Get what type of header belongs to and switch boolean match to true
                    cate = pair.getKey();
                    match = true;
                    return true;
                }
            }
        }
        match = false;
        return false;
    }


    public static String getMatchString() {
        // Return the header word
        return cate;
    }

//
//    public static void main(String[] args){
//        System.out.println("All filters:" + all);
//        findMatchString("brand");
//    }
}
