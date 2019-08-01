package flix.brand.list;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.*;
import java.io.*;
import java.util.*;

public class Table {

    public static ArrayList<JSONObject> ARR_TABLE = new ArrayList<JSONObject>();
    public static ArrayList<JSONObject> column_id;
    /**
     * Initialize new table_id array in every call
     */
    public Table(){
        column_id = new ArrayList<JSONObject>();
    }

    /**
     * Extract all tables from 1 page
     */
    public static void extractTable(String URL){
        try {
            Document doc = Jsoup.connect(URL).get();
            Elements tables = doc.select("table.wikitable");
            System.out.println(">> Checking Table ..." + URL);

            if (tables == null || tables.isEmpty()){
                System.out.println(">>>> Does not contain: table");
                List.getLists(URL);
            }

            int hcol_size;
            for (Element table : tables){
                System.out.println("\n>> Checking individual tables");
                Elements rows = table.select("tr");
                Elements hcols = rows.select("th");

                // Init new column_id every table
                new Table();

                // Select columns to view
                hcol_size = hcols.size();
                for (int j = 0; j < hcols.size(); j++){
                    String h = hcols.get(j).text().toLowerCase();
                    getColumnID(h, j);  // Store in column_id
                }

                // Start extracting each cells
                System.out.println(">> Extracting from cells in URL: " + URL);
                extractCell(rows, URL, hcol_size);

                /*
                * Also try extracting list if exists both list and table
                * */
                List.getLists(URL);
            }
        } catch (IOException e) { System.err.println("For '" + URL + "': " + e.getMessage()); }
    }

    /**
     * Check which column to view. Store id in table_id
     */
    public static void getColumnID(String h, int j) {
        JSONObject obj = new JSONObject();

        // Don't add to table id if id is already there
        if (column_id != null){
            for (JSONObject id : column_id){
                if (id.getInt("id") == j) { return; }
            }
        }

        HeadFilter hf = new HeadFilter();
        if (hf.boolMatchString(h) == true){ obj.put("type", hf.getMatchString());   obj.put("id", j); }

        if (obj.length() != 0 ){ column_id.add(obj); }
    }

    /**
     * Filter data with the right column_id (table_id)
     */
    public static void extractCell(Elements rows, String URL, int hcols_size) {
        for (int i = 1; i < rows.size(); i++) {
            Element row = rows.get(i);
            Elements cols = row.select("td");

            // Duplicate data if some table contains rowspan
            for (int j = 0; j < cols.size() ; j++) { // Look at each cell
                Element col = cols.get(j);
                String span = col.select("[rowspan]").attr("rowspan");

                if (span == "") { continue; }
                int rowspan = Integer.parseInt(span);

                if (rowspan > 1) {
                    for (int k = 1; k < rowspan; k++) {  // k = how many more addition rows to add
                        if ( (i+k) > rows.size() - 1 ) { continue; } // Ignore incorrect rowspan
                        Element row2 = rows.get(i + k); // Fix this num iteration
                        Elements cols2 = row2.select("td");

                        cols2.add(j, new Element("td").appendText(col.text()));
                        row2.empty().append(cols2.toString());
                    }
                }
            }

            JSONObject obj = new JSONObject();

            // Extract cell from the right column_id
            for (JSONObject c : column_id){
                if (c.getInt("id") > cols.size() - 1 ){ continue; }
                obj.put(c.getString("type"), cols.get(c.getInt("id")).text());
            }
            obj.put("category", Category.findCategory(URL));
            obj = Category.fillMissingFields(obj);

            ARR_TABLE.add(obj);
        }
    }
}



