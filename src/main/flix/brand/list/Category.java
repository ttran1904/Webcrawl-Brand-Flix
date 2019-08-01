package flix.brand.list;

import org.json.JSONObject;

import java.util.ArrayList;

public class Category {

    private static ArrayList<String> header = new ArrayList<String>();

    public static String findCategory(String URL){
        if (URL.contains("List_of_")) {
            URL = URL.substring(URL.indexOf("List_of_") + 8)
                    .replace("_", " ")
                    .replace(" brands", "");
            return URL.substring(0,1).toUpperCase() + URL.substring(1);

        } else if (URL.startsWith("https://en.wikipedia.org/wiki/")) {
            URL = URL.substring(URL.indexOf("/wiki/") + 6)
                    .replace(" brand names", "")
                    .replace(" (brands)", "");
            return URL;
        }
        return URL;
    }


    public static JSONObject fillMissingFields(JSONObject json){
        if (!json.has("brand")){ json.put("brand", "NA"); }

        if (!json.has("company") && json.has("brand")) { json.put("company", json.getString("brand")); }
        else if (!json.has("company")) { json.put("company", "NA"); }

        if (!json.has("product") && json.has("category")) { json.put("product", json.getString("category")); }
        else if (!json.has("product")) { json.put("product", "NA"); }

        if (!json.has("country")){ json.put("country", "NA"); }



        return json;
    }

}
