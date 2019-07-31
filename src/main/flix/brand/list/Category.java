package flix.brand.list;

public class Category {

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
}
