package flix.brand.list;

import jxl.*;
import jxl.format.UnderlineStyle;
import jxl.read.biff.BiffException;
import jxl.write.*;
import jxl.write.Number;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class writeExcel {


    private static ArrayList<JSONObject> arrTable = Table.ARR_TABLE;
    private static ArrayList<JSONObject> arrList = List.ARR_LIST;


    private WritableCellFormat timesBoldUnderline;
    private WritableCellFormat times;
    private String inputFile;


    private static ArrayList<JSONObject> arr = new ArrayList<JSONObject>();

    public writeExcel(){
        arr.addAll(arrTable);
        arr.addAll(arrList);
    }


    public void setOutputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public void write() throws IOException, WriteException, BiffException {
        File file = new File(inputFile);
        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "EN"));

        System.out.println(">> Writing new file");
        WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
        workbook.createSheet("Brand", 0);
        WritableSheet excelSheet = workbook.getSheet(0);

        createLabel(excelSheet);
        createContent(excelSheet);

        workbook.write();
        workbook.close();



        //Extra testing for incremental addition

//        if(file.exists() && !file.isDirectory()) {
//            System.out.println(">> Updating file");
//            System.out.println(file);
//            Workbook workbook = Workbook.getWorkbook(new File("testing3.xls"));
//            WritableWorkbook copy = Workbook.createWorkbook(file, workbook);
//            WritableSheet excelSheet = copy.getSheet(0);
//
//            rowNum = excelSheet.getRows();
//            System.out.println(rowNum);
//            createContent(excelSheet);
//
//            workbook.close();
//            copy.write();
//            copy.close();
//
//        } else {
//            System.out.println(">> Writing new file");
//            WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
//            workbook.createSheet("Brand", 0);
//            WritableSheet excelSheet = workbook.getSheet(0);
//            rowNum = 1;
//            System.out.println(rowNum);
//
//            createLabel(excelSheet);
//            createContent(excelSheet);
//
//            workbook.write();
//            workbook.close();
//        }
    }

    private void createLabel(WritableSheet sheet) throws WriteException {
        WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10); //Times font
        times = new WritableCellFormat(times10pt); //Cell format
        times.setWrap(true); //Auto wrap cells

        WritableFont times10ptBoldUnderline = new WritableFont( // Bold font with underlines
                WritableFont.TIMES, 10, WritableFont.BOLD, false,
                UnderlineStyle.SINGLE);
        timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
        timesBoldUnderline.setWrap(true); // Auto wrap


        CellView cv = new CellView();
        cv.setFormat(times);
        cv.setFormat(timesBoldUnderline);
        cv.setAutosize(true);


        // **IMPORTANT:** Write headers
        addCaption(sheet, 0, 0, "Category");
        addCaption(sheet, 1, 0, "Brand");
        addCaption(sheet, 2, 0, "Company/Owner");
        addCaption(sheet, 3, 0, "Product");
        addCaption(sheet, 4, 0, "Country");
    }


    // Write: Header
    private void addCaption(WritableSheet sheet, int column, int row, String s)
            throws WriteException {
        Label label;
        label = new Label(column, row, s, timesBoldUnderline);
        sheet.addCell(label);
    }

    // Write: Number
    private void addNumber(WritableSheet sheet, int column, int row,
                           Integer integer) throws WriteException {
        jxl.write.Number number;
        number = new Number(column, row, integer, times);
        sheet.addCell(number);
    }

    //Write: Text
    private void addLabel(WritableSheet sheet, int column, int row, String s)
            throws WriteException {
        Label label;
        label = new Label(column, row, s, times);
        sheet.addCell(label);
    }


    private void createContent(WritableSheet sheet) throws WriteException { // Add brand contents to Excel
        for (int i = 1; i < arr.size(); i++) {  // Skip first header row. i MUST == 1
            JSONObject brand = arr.get(i-1); // Arr starts from 0
            System.out.println(brand);

            addLabel(sheet, 0, i, brand.getString("category"));
            addLabel(sheet, 1, i, brand.getString("brand"));

            if (brand.getString("company") != null) { addLabel(sheet, 2, i, brand.getString("company"));
            } else { addLabel(sheet, 2, i, null); }

            if (brand.getString("product") != null) { addLabel(sheet, 3, i, brand.getString("product"));
            } else { addLabel(sheet, 3, i, null); }

            if (brand.getString("country") != null) { addLabel(sheet, 4, i, brand.getString("country"));
            } else { addLabel(sheet, 4, i, null); }

        }
    }
}
