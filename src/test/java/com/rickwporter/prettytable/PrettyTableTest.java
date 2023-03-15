package com.rickwporter.prettytable;

import com.rickwporter.prettytable.PrettyTable.CellFormat;
import com.rickwporter.prettytable.PrettyTable.OutputFormat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


public class PrettyTableTest {
    Map<OutputFormat, String> OUTPUT_FORMAT_EXTENSIONS = new HashMap<OutputFormat, String>() {{
        put(OutputFormat.TEXT, "text");
        put(OutputFormat.CSV, "csv");
        put(OutputFormat.HTML, "html");
        put(OutputFormat.JSON, "json");
    }};

    final String loadFileContent(String filename) {
        try {
            URL resource = getClass().getClassLoader().getResource(filename);
            return Files.readString(Paths.get(resource.toURI()));
        } catch (Exception ex) {
            System.out.println(String.format("got exception reading %s:", filename));
            System.out.println(ex);
        }
        return null;
    }

    PrettyTable createBasicTable() {
        PrettyTable table = new PrettyTable("Column A", "Column 2", "Column III");
        table.addRow("A", "B", "c");
        table.addRow(1, 1024, 65535);
        table.addRow("Washington", "Adams", "Jefferson");
        return table;
    }

    PrettyTable createFormattedTable() {
        PrettyTable table = createBasicTable();
        table.setFormat(0, CellFormat.LEFT);
        table.setFormat(2, CellFormat.RIGHT);
        return table;
    }

    @Test
    public void testBasicCsv() {
        PrettyTable table = createBasicTable();
        String result = table.toCsv();
        String expected = loadFileContent("PrettyTable_basic.csv");
        Assertions.assertEquals(expected, result);
        // same answer when going through 'formattedString()'
        result = table.formattedString(PrettyTable.OutputFormat.CSV);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testBasicHtml() {
        PrettyTable table = createBasicTable();
        String result = table.toHtml();
        String expected = loadFileContent("PrettyTable_basic.html");
        Assertions.assertEquals(expected, result);
        // same answer when going through 'formattedString()'
        result = table.formattedString(PrettyTable.OutputFormat.HTML);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testBasicJson() {
        PrettyTable table = createBasicTable();
        String result = table.toJson();
        String expected = loadFileContent("PrettyTable_basic.json");
        Assertions.assertEquals(expected, result);
        // same answer when going through 'formattedString()'
        result = table.formattedString(PrettyTable.OutputFormat.JSON);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testBasicText() {
        PrettyTable table = createBasicTable();
        String result = table.toText();
        String expected = loadFileContent("PrettyTable_basic.text");
        Assertions.assertEquals(expected, result);
        // same answer when going through 'formattedString()'
        result = table.formattedString(PrettyTable.OutputFormat.TEXT);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testFormattedText() {
        PrettyTable table = createFormattedTable();
        String result = table.toText();
        String expected = loadFileContent("PrettyTable_formatted.text");
        Assertions.assertEquals(expected, result);
        // same answer when going through 'formattedString()'
        result = table.formattedString(PrettyTable.OutputFormat.TEXT);
        Assertions.assertEquals(expected, result);
        // update the table setting all the formats at once
        table.setFormats(CellFormat.LEFT, CellFormat.CENTER, CellFormat.RIGHT);
        result = table.formattedString(PrettyTable.OutputFormat.TEXT);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testUnformatted() {
        // this checks all the "unformatted" outputs... using the same data as the basic test for easier comparison
        Map<OutputFormat, String> formats = new HashMap<OutputFormat, String>() {{
            put(OutputFormat.CSV, "csv");
            put(OutputFormat.HTML, "html");
            put(OutputFormat.JSON, "json");
        }};
        for (Map.Entry<OutputFormat, String> entry: formats.entrySet()) {
            PrettyTable table = createFormattedTable();
            String expected = loadFileContent("PrettyTable_basic." + entry.getValue());
            String result = table.formattedString(entry.getKey());
            Assertions.assertEquals(expected, result);
        }
    }


     @Test
     public void testEmpty() {
        for (Map.Entry<OutputFormat, String> entry: OUTPUT_FORMAT_EXTENSIONS.entrySet()) {
            PrettyTable table = new PrettyTable("Col1", "Col2", "Col3", "Col4");
            String expected = loadFileContent("PrettyTable_empty." + entry.getValue());
            String result = table.formattedString(entry.getKey());
         Assertions.assertEquals(expected, result);
        }
     }

     @Test
     public void testDuplicated() {
         for (Map.Entry<OutputFormat, String> entry: OUTPUT_FORMAT_EXTENSIONS.entrySet()) {
             // create the table each time, since the print may be destructive
             PrettyTable table = new PrettyTable("Col1", "Col2", "Col3", "Col4");
             table.addRow("A", "B", "C", "D");
             table.addRow("A", "B", "C", "Z");
             table.addRow("A", "B", "F", "E");
             table.addRow("A", "B", "F", "E"); // causes an empty row -- would be unusal

            String expected = loadFileContent("PrettyTable_duplicate." + entry.getValue());
            String result = table.formattedString(entry.getKey());
            Assertions.assertEquals(expected, result);
         }
     }

     @Test
     public void testQuotedCsv() {
         PrettyTable table = new PrettyTable("one", "two, with comma", "three");
         table.addRow("a,b", "c", "d,e");
         String expected = loadFileContent("PrettyTable_quotes.csv");
         String result = table.formattedString(OutputFormat.CSV);
        Assertions.assertEquals(expected, result);
     }

     @Test
     public void testGetters() {
         PrettyTable table = createFormattedTable();
         Assertions.assertEquals(3, table.getRowCount());
         int row = 0;

         Assertions.assertNotNull(table.getRow(row));
         Assertions.assertEquals(table.getCell(row, 0), "A");
         Assertions.assertEquals(table.getCell(row, 1), "B");
         Assertions.assertEquals(table.getCell(row, 2), "c");
         Assertions.assertNull(table.getCell(row, 3));

         row = 1;
         Assertions.assertNotNull(table.getRow(row));
         Assertions.assertEquals(table.getCell(row, 0), "1");
         Assertions.assertEquals(table.getCell(row, 1), "1024");
         Assertions.assertEquals(table.getCell(row, 2), "65535");
        Assertions.assertNull(table.getCell(row, 3));
 
        row = 2;
        Assertions.assertNotNull(table.getRow(row));
        Assertions.assertEquals(table.getCell(row, 0), "Washington");
        Assertions.assertEquals(table.getCell(row, 1), "Adams");
        Assertions.assertEquals(table.getCell(row, 2), "Jefferson");
        Assertions.assertNull(table.getCell(row, 3));
        
        row = 3;
        Assertions.assertNull(table.getRow(row));
        Assertions.assertNull(table.getCell(row, 0));
        Assertions.assertNull(table.getCell(row, 1));
        Assertions.assertNull(table.getCell(row, 2));
        Assertions.assertNull(table.getCell(row, 3));
     }

     @Test
     public void testNumberAsStringJson() {     
        // NOTES:
        // 1. make sure a numeric header is still string encoded
        // 2. make sure hex value is still string encoded
        PrettyTable table = new PrettyTable("Col 1", "Col 2", "3");
        table.addRow("a", "9", 11);

        String expected = loadFileContent("PrettyTable_stringnum.json");
        String result = table.formattedString(OutputFormat.JSON);
        Assertions.assertEquals(expected, result);
     }

     @Test
     public void testNoHeaders() {
        PrettyTable table = new PrettyTable();
        table.addRow("A", "B", "c");
        table.addRow(1, 1024, 65535);
        table.addRow("Washington", "Adams", "Jefferson");

        for (Map.Entry<OutputFormat, String> entry: OUTPUT_FORMAT_EXTENSIONS.entrySet()) {
           String expected = loadFileContent("PrettyTable_no_headers." + entry.getValue());
           String result = table.formattedString(entry.getKey());
           Assertions.assertEquals(expected, result);
        }

     }
}
