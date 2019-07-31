package flix.brand.list;

import java.io.*;

public class test {


    public static void main(String[] args) throws FileNotFoundException {
        String url = "https://en.wikipedia.org/wiki/List_of_toy_soldiers_brands";
        System.out.println(Category.findCategory(url));
    }
}
