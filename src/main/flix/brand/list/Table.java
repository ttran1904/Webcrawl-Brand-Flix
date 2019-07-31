package flix.brand.list;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.*;

import java.io.*;
import java.util.*;


public class Table {

    public static ArrayList<JSONObject> ARR_TABLE = new ArrayList<JSONObject>(); // One array for all Tables!
    public static ArrayList<JSONObject> table_id; // Store ids of table columns should view


    public Table(){
        table_id = new ArrayList<JSONObject>();
    }


    // Extract tables from 1 page
    public static void extractTable(String URL){
        try {
            System.out.println(">> Checking Table ..." + URL);
            Document doc = Jsoup.connect(URL).get();

            // Get table w/ wikitable class
            Elements tables = doc.select("table.wikitable");
            if (tables == null || tables.isEmpty()){
                System.out.println(">>>>Does not contain: table");
                List.getLists(URL);
            }

            for (Element table : tables){
                Elements rows = table.select("tr"); // Rows
                Elements hcols = rows.select("th"); // Headers


                // Create table_id every new table
                table_id = new ArrayList<JSONObject>();

                // Which column to view
                for (int j = 0; j < hcols.size(); j++){
                    String h = hcols.get(j).text().toLowerCase();
                    System.out.println(hcols.get(j).text());
                    System.out.println("    Checking table ID");
                    tableId(h, j);  //Use tableID method to sort
                }

                // Then extractCells from each row, corresponing column
                System.out.println(">> Extracting from cells in URL: " + URL);
                extractCell(rows, Category.findCategory(URL), table_id);
            }
        } catch (IOException e) { System.err.println("For '" + URL + "': " + e.getMessage()); }
    }


    // Check which column to view. Store id in table_id
    public static void tableId (String h, int j) {
        JSONObject obj = new JSONObject();

        // Don't add to table id if id is already there
        if (table_id != null){
            for (JSONObject id : table_id){
                if (id.getInt("id") == j) { return; }
            }
        }

        // Filter is the header contains the right data type
        HeadFilter hf = new HeadFilter();
        if (hf.boolMatchString(h) == true){
            obj.put("type", hf.getMatchString());
            obj.put("id", j);
        }

        if (obj.length() != 0 ){ table_id.add(obj); }
        System.out.println("Filtered table id" + table_id);



//        /* Filter header words to put in table_id */
//        if (h.contains("brand") || h.contains("name") ){ //Find brand
//            obj.put("type", "brand");
//            obj.put("id", j);
//            System.out.println(">> Contains brand name in column no.: " + j);
//        } else if (h.contains("company") || h.contains("companies") || h.contains("manufacture")){ // Find company
//            obj.put("type", "company");
//            obj.put("id", j);
//            System.out.println(">> Contains company name in column no.: " + j);
//
//        }  else if (h.contains("product") || h.contains("note") ) {
//            obj.put("type", "product");
//            obj.put("id", j);
//            System.out.println(">> Contains product name in column no.: " + j);
//
//        } else if (h.contains("country") || h.contains("origin") || h.contains("countries") || h.contains("nation")) {
//            obj.put("type", "country");
//            obj.put("id", j);
//            System.out.println(">> Contains country name in column no.: " + j);
//        } else { System.out.println(">> Doesn't contain any defined header in table col.: " + j); }// Didn't find any yet
//
//        /* Don't add to table_id if doc does not have any applicable headers */

    }


    // Filter data to extract to observations
    public static void extractCell(Elements rows, String cat, ArrayList<JSONObject> col_id) {
        System.out.println("Column ids: " + col_id);
        // Loop over each row
        for (int i = 1; i < rows.size(); i++) {
            Element row = rows.get(i);
            Elements cols = row.select("td");


            // Duplicate data if some table contains rowspan
            for (Element span : row.select("td[rowspan]")) {
                int rowspan = Integer.parseInt(span.attr("rowspan"));

                if (rowspan > 1) {
                    Element row2 = rows.get(i+1); // Fix this num iteration
                    //Elements cols2 = row2.select("td");

                    //System.out.println(row2);
                    System.out.println(span.toString());

                    //row2.append("<td>" + span.toString() + "</td>");
                }
            }

            JSONObject obj = new JSONObject();

            // Extract cell from the right column id (table_id)
            for (JSONObject c : col_id){
                obj.put(c.getString("type"), cols.get(c.getInt("id")).text());
            }
            obj.put("category", cat);   // Also put the category


            ArrayList<String> ar = new ArrayList<String>();
            ArrayList<String> compare = new ArrayList<String>();
            ar.add("category");
            ar.add("brand");
            ar.add("product");
            ar.add("company");
            ar.add("country");

            for (JSONObject c : col_id){ // Copying the column id!!!! (Only the type)
                if (!compare.contains( c.getString("type")) ){ compare.add(c.getString("type")); }
            }


            if (!compare.containsAll(ar)){ //
                if (!compare.contains("brand")){ obj.put("brand", "NA"); }

                if (!compare.contains("product")){ obj.put("product", "NA"); }

                if (!compare.contains("company") && compare.contains("product")) {  // Changing this one!!
                    obj.put("company", obj.getString("brand"));
                    System.out.println("> This is the same brand-company: " + obj.getString("brand"));
                } else if (!compare.contains("company")) {
                    obj.put("company", "NA");
                }

                if (!compare.contains("country")){ obj.put("country", "NA"); }
            }

            System.out.println(obj);
            // *** Add to the ARR_TABLE ***
            new Table().ARR_TABLE.add(obj);
        }
    }


    public static void main(String[] args) {
        extractTable("https://en.wikipedia.org/wiki/List_of_smart_cards");

        for (JSONObject obj : ARR_TABLE) {
            System.out.println(obj);
        }
    }

}



