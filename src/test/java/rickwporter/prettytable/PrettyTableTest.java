package rickwporter.prettytable;

import rickwporter.prettytable.PrettyTable.CellFormat;
import rickwporter.prettytable.PrettyTable.OutputFormat;

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
        result = table.formattedString(OutputFormat.CSV);
        Assertions.assertEquals(expected, result);
        table.setOutputFormat(OutputFormat.CSV);
        result = table.toString();
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testBasicHtml() {
        PrettyTable table = createBasicTable();
        String result = table.toHtml(false);
        String expected = loadFileContent("PrettyTable_basic.html");
        Assertions.assertEquals(expected, result);
        // same answer when going through 'formattedString()'
        result = table.formattedString(OutputFormat.HTML);
        Assertions.assertEquals(expected, result);
        // again with removing redundancy
        result = table.toHtml(true);
        Assertions.assertEquals(expected, result);
        table.setOutputFormat(OutputFormat.HTML);
        result = table.toString();
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testBasicJson() {
        PrettyTable table = createBasicTable();
        String result = table.toJson();
        String expected = loadFileContent("PrettyTable_basic.json");
        Assertions.assertEquals(expected, result);
        // same answer when going through 'formattedString()'
        result = table.formattedString(OutputFormat.JSON);
        Assertions.assertEquals(expected, result);
        table.setOutputFormat(OutputFormat.JSON);
        result = table.toString();
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testBasicText() {
        PrettyTable table = createBasicTable();
        String result = table.toText(true);
        String expected = loadFileContent("PrettyTable_basic.text");
        Assertions.assertEquals(expected, result);
        // same answer when going through 'formattedString()'
        result = table.formattedString(OutputFormat.TEXT);
        Assertions.assertEquals(expected, result);
        // same answer in this case because there are no duplicates
        result = table.toText(false);
        Assertions.assertEquals(expected, result);
        // before setting the format type, see that it defaults to text
        result = table.toString();
        Assertions.assertEquals(expected, result);
        table.setOutputFormat(OutputFormat.TEXT);
        result = table.toString();
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testFormatted() {
        // this checks all the formatted outputs
        Map<OutputFormat, String> formats = new HashMap<OutputFormat, String>() {{
            put(OutputFormat.HTML, "html");
            put(OutputFormat.TEXT, "text");
        }};
        PrettyTable table = createFormattedTable();
        for (Map.Entry<OutputFormat, String> entry: formats.entrySet()) {
            String expected = loadFileContent("PrettyTable_formatted." + entry.getValue());
            String result = table.formattedString(entry.getKey());
            Assertions.assertEquals(expected, result);
        }

        // update the table setting all the formats at once, and get same answers
        table.setFormats(CellFormat.LEFT, CellFormat.CENTER, CellFormat.RIGHT);
        for (Map.Entry<OutputFormat, String> entry: formats.entrySet()) {
            String expected = loadFileContent("PrettyTable_formatted." + entry.getValue());
            String result = table.formattedString(entry.getKey());
            Assertions.assertEquals(expected, result);
        }
    }

    @Test
    public void testUnformatted() {
        // this checks all the "unformatted" outputs... using the same data as the basic test for easier comparison
        Map<OutputFormat, String> formats = new HashMap<OutputFormat, String>() {{
            put(OutputFormat.CSV, "csv");
            put(OutputFormat.JSON, "json");
        }};
        PrettyTable table = createFormattedTable();
        for (Map.Entry<OutputFormat, String> entry: formats.entrySet()) {
            String expected = loadFileContent("PrettyTable_basic." + entry.getValue());
            String result = table.formattedString(entry.getKey());
            Assertions.assertEquals(expected, result);
        }
    }

     @Test
     public void testEmpty() {
        PrettyTable table = new PrettyTable("Col1", "Col2", "Col3", "Col4");
        for (Map.Entry<OutputFormat, String> entry: OUTPUT_FORMAT_EXTENSIONS.entrySet()) {
            String expected = loadFileContent("PrettyTable_empty." + entry.getValue());
            String result = table.formattedString(entry.getKey());
            Assertions.assertEquals(expected, result);
        }
     }

     @Test
     public void testDuplicated() {
        PrettyTable table = new PrettyTable("Col1", "Col2", "Col3", "Col4");
        table.addRow("A", "B", "C", "D");
        table.addRow("A", "B", "C", "Z");
        table.addRow("A", "B", "F", "E");
        table.addRow("A", "B", "F", "E"); // causes an empty row -- would be unusal
    
        for (Map.Entry<OutputFormat, String> entry: OUTPUT_FORMAT_EXTENSIONS.entrySet()) {
            String expected = loadFileContent("PrettyTable_duplicate." + entry.getValue());
            String result = table.formattedString(entry.getKey());
            Assertions.assertEquals(expected, result);
        }

        String expected = loadFileContent("PrettyTable_duplicate_undup.text");
        String result  = table.toText(false);
        Assertions.assertEquals(expected, result);
        result = table.formattedString(OutputFormat.TEXT, false);
        Assertions.assertEquals(expected, result);
        
        expected = loadFileContent("PrettyTable_duplicate_undup.html");
        result  = table.toHtml(false);
        Assertions.assertEquals(expected, result);
        result = table.formattedString(OutputFormat.HTML, false);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testDuplicatedMixed() {
        PrettyTable table = new PrettyTable("A", "B", "C");
        table.setFormats(CellFormat.RIGHT, CellFormat.RIGHT, CellFormat.RIGHT);
        table.addRow("10", "2", "3");
        table.addRow(10, 2, 45);
        table.addRow("10", "20", "12");
        table.addRow("x", "y", "z");

        String expected = loadFileContent("PrettyTable_duplicate_mixed.text");
        Assertions.assertEquals(expected, table.formattedString(OutputFormat.TEXT));

        expected = loadFileContent("PrettyTable_duplicate_mixed.html");
        Assertions.assertEquals(expected, table.formattedString(OutputFormat.HTML));
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
         Assertions.assertEquals("A", table.getCell(row, 0));
         Assertions.assertEquals("B", table.getCell(row, 1));
         Assertions.assertEquals("c", table.getCell(row, 2));
         Assertions.assertNull(table.getCell(row, 3));

         row = 1;
         Assertions.assertNotNull(table.getRow(row));
         Assertions.assertEquals(1, table.getCell(row, 0));
         Assertions.assertEquals(1024, table.getCell(row, 1));
         Assertions.assertEquals(65535, table.getCell(row, 2));
        Assertions.assertNull(table.getCell(row, 3));
 
        row = 2;
        Assertions.assertNotNull(table.getRow(row));
        Assertions.assertEquals("Washington", table.getCell(row, 0));
        Assertions.assertEquals("Adams", table.getCell(row, 1));
        Assertions.assertEquals("Jefferson", table.getCell(row, 2));
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

     @Test
     public void testSort() {
        PrettyTable table = new PrettyTable("A", "B", "C");
        table.addRow("10", "2", "3");
        table.addRow("10", "20", "12");
        table.addRow(10, 10, 45);
        table.addRow("x", "y", "z");

        table.sortByIndex(0, 1);
        Assertions.assertEquals("2", table.getCell(0, 1));
        Assertions.assertEquals(10, table.getCell(1, 1));
        Assertions.assertEquals("20", table.getCell(2, 1));
        Assertions.assertEquals("y", table.getCell(3, 1));

        table.sortByHeader("C");
        Assertions.assertEquals("2", table.getCell(0, 1));
        Assertions.assertEquals("20", table.getCell(1, 1));
        Assertions.assertEquals(10, table.getCell(2, 1));
        Assertions.assertEquals("y", table.getCell(3, 1));

        // below comparisons use 'toString()' because order is non-deterministic
        table.sortByIndex(0);
        Assertions.assertEquals("10", table.getCell(0, 0).toString());
        Assertions.assertEquals("10", table.getCell(1, 0).toString());
        Assertions.assertEquals("10", table.getCell(2, 0).toString());
        Assertions.assertEquals("x", table.getCell(3, 0).toString());

        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> table.sortByIndex(500));
        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> table.sortByHeader("Q"));
    }
}
