package rickwporter.prettytable;

import rickwporter.prettytable.PrettyTable.HorizontalAlign;
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
        table.setHorizAlign(0, HorizontalAlign.LEFT);
        table.setHorizAlign(2, HorizontalAlign.RIGHT);
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
        table.setHorizAligns(HorizontalAlign.LEFT, HorizontalAlign.CENTER, HorizontalAlign.RIGHT);
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
        table.setHorizAligns(HorizontalAlign.RIGHT, HorizontalAlign.RIGHT, HorizontalAlign.RIGHT);
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

    @Test
    public void testCsvEncoding() {
        // CSV encoding needs to update/encapsulate special characters
        PrettyTable table = new PrettyTable("Col1", "Col2", "Col3");
        table.addRow("contains, a commas", "boring", "contains, multi, commas");
        table.addRow("crlf\r\nline 2", "contains\nnewline", "multiple\r\nnew\nlines");
        table.addRow("single' quote", "multi 'single' quotes", "oxymoron");
        table.addRow("make it a \"double-double\"", "single \"double", "undouble");

        String expected = loadFileContent("PrettyTable_csv_encoded.csv");
        Assertions.assertEquals(expected, table.toCsv());
    }

    @Test
    public void testTextNewlines() {
        PrettyTable table = new PrettyTable("A", "B", "C", "D");
        table.setHorizAligns(HorizontalAlign.LEFT, HorizontalAlign.RIGHT, HorizontalAlign.CENTER, HorizontalAlign.LEFT);
        table.addRow("a", "12\n345\n67890\r\nabc", "testing\na\n", "\nempty\nblank");
        table.addRow("abc\n123", "123\n4567", "aaa", "bbb");

        String expected = loadFileContent("PrettyTable_newlines.text");
        Assertions.assertEquals(expected, table.formattedString(OutputFormat.TEXT));
    }

    @Test
    public void testNested() {
        PrettyTable letters = new PrettyTable("A", "B", "C");
        letters.setHorizAligns(HorizontalAlign.RIGHT, HorizontalAlign.LEFT, HorizontalAlign.CENTER);
        letters.addRow("abc", "def", "ghij");
        letters.addRow("klmn", "o", "pqr");
        letters.addRow("st", "uvx", "yz");

        PrettyTable numbers = new PrettyTable("First", "Second", "Third");
        numbers.addRow(123, 4567, 89012);
        numbers.addRow("3456", "78", 901);
        numbers.addRow(23456, 7890, 12);

        PrettyTable big = new PrettyTable("Table name", "Data");
        big.setHorizAligns(HorizontalAlign.CENTER, HorizontalAlign.LEFT);
        big.addRow("Letters", letters);
        big.addRow("Numbers", numbers);
        
        for (Map.Entry<OutputFormat, String> entry: OUTPUT_FORMAT_EXTENSIONS.entrySet()) {
            String expected = loadFileContent("PrettyTable_nested." + entry.getValue());
            big.setOutputFormat(entry.getKey());
            letters.setOutputFormat(entry.getKey());
            numbers.setOutputFormat(entry.getKey());
            String result = big.toString();
            Assertions.assertEquals(expected, result);
        }

        String expected = loadFileContent("PrettyTable_nested_mixed.html");
        letters.setOutputFormat(OutputFormat.TEXT);
        numbers.setOutputFormat(OutputFormat.CSV);
        big.setOutputFormat(OutputFormat.HTML);
        Assertions.assertEquals(expected, big.toString());

        expected = loadFileContent("PrettyTable_nested_mixed.text");
        letters.setOutputFormat(OutputFormat.JSON);
        numbers.setOutputFormat(OutputFormat.CSV);
        big.setOutputFormat(OutputFormat.TEXT);
        Assertions.assertEquals(expected, big.toString());

        expected = loadFileContent("PrettyTable_nested_mixed.json");
        letters.setOutputFormat(OutputFormat.HTML);
        numbers.setOutputFormat(OutputFormat.CSV);
        big.setOutputFormat(OutputFormat.JSON);
        Assertions.assertEquals(expected, big.toString());
    }

    @Test
    public void testOptionDefaults() {
        PrettyTable table = new PrettyTable("Column");
        Assertions.assertTrue(table.isShowHeader());
        Assertions.assertTrue(table.isShowLines());
        Assertions.assertFalse(table.isRowDividers());
        Assertions.assertTrue(table.isRemoveRedundant());
        Assertions.assertNull(table.getCaption());
    }

    @Test
    public void testShowHeaderOption() {
        PrettyTable table = new PrettyTable("LongHeader", "H");
        table.addRow("ab", "wider-cell");
        table.addRow("cdef\ngh", "z");

        Assertions.assertEquals(10, table.getMaxWidthForColumn(0));
        Assertions.assertEquals(10, table.getMaxWidthForColumn(1));
        Assertions.assertEquals(10, table.getMaxWidths().get(0).intValue());
        Assertions.assertEquals(10, table.getMaxWidths().get(1).intValue());

        table.setShowHeader(false);
        Assertions.assertFalse(table.isShowHeader());
        // header text no longer contributes; multiline cells still do
        Assertions.assertEquals(4, table.getMaxWidthForColumn(0));
        Assertions.assertEquals(10, table.getMaxWidthForColumn(1));

        table.setShowHeader(true);
        Assertions.assertTrue(table.isShowHeader());
        Assertions.assertEquals(10, table.getMaxWidthForColumn(0));

        PrettyTable empty = new PrettyTable("Header");
        Assertions.assertEquals(6, empty.getMaxWidthForColumn(0));
        empty.setShowHeader(false);
        Assertions.assertEquals(0, empty.getMaxWidthForColumn(0));

        // a column past the header list is sized from the cell either way
        PrettyTable extra = new PrettyTable("Only");
        extra.addRow("x", "second");
        Assertions.assertEquals(6, extra.getMaxWidthForColumn(1));
        extra.setShowHeader(false);
        Assertions.assertEquals(1, extra.getMaxWidthForColumn(0));
        Assertions.assertEquals(6, extra.getMaxWidthForColumn(1));
    }

    @Test
    public void testShowLinesAndCaptionOptions() {
        PrettyTable table = new PrettyTable("A");

        table.setShowLines(false);
        Assertions.assertFalse(table.isShowLines());
        table.setShowLines(true);
        Assertions.assertTrue(table.isShowLines());

        table.setCaption("Summary");
        Assertions.assertEquals("Summary", table.getCaption());
        table.setCaption("");
        Assertions.assertEquals("", table.getCaption());
        table.setCaption(null);
        Assertions.assertNull(table.getCaption());
    }

    @Test
    public void testRowDividersOption() {
        PrettyTable table = createBasicTable();
        Assertions.assertFalse(table.isRowDividers());

        table.setRowDividers(true);
        Assertions.assertTrue(table.isRowDividers());
        // these rows do not share a leading cell, so each one has a rule under it
        String everyRow = ""
            + "+------------+----------+------------+\n"
            + "|  Column A  | Column 2 | Column III |\n"
            + "+------------+----------+------------+\n"
            + "|     A      |    B     |     c      |\n"
            + "+------------+----------+------------+\n"
            + "|     1      |   1024   |   65535    |\n"
            + "+------------+----------+------------+\n"
            + "| Washington |  Adams   | Jefferson  |\n"
            + "+------------+----------+------------+\n";
        Assertions.assertEquals(everyRow, table.formattedString(OutputFormat.TEXT));
        Assertions.assertEquals(everyRow, table.toString());

        table.setRemoveRedundant(false);
        Assertions.assertEquals(everyRow, table.formattedString(OutputFormat.TEXT));
        Assertions.assertEquals(everyRow, table.toString());
        Assertions.assertEquals(everyRow, table.formattedString(OutputFormat.TEXT, true));

        // need showLines to have dividers show up
        String lineless = ""
            + "  Column A   Column 2  Column III \n"
            + "     A          B          c      \n"
            + "     1         1024      65535    \n"
            + " Washington   Adams    Jefferson  \n";
        table.setShowLines(false);
        Assertions.assertEquals(lineless, table.formattedString(OutputFormat.TEXT));
        Assertions.assertEquals(lineless, table.toString());
        Assertions.assertEquals(lineless, table.formattedString(OutputFormat.TEXT, true));
        table.setShowLines(true);

        // csv, html, and json ignore the setting
        Assertions.assertEquals(loadFileContent("PrettyTable_basic.csv"), table.formattedString(OutputFormat.CSV));
        Assertions.assertEquals(loadFileContent("PrettyTable_basic.html"), table.formattedString(OutputFormat.HTML));
        Assertions.assertEquals(loadFileContent("PrettyTable_basic.json"), table.formattedString(OutputFormat.JSON));
        table.setOutputFormat(OutputFormat.HTML);
        Assertions.assertEquals(loadFileContent("PrettyTable_basic.html"), table.toString());

        table.setRowDividers(false);
        Assertions.assertFalse(table.isRowDividers());
        table.setOutputFormat(OutputFormat.TEXT);
        table.setRemoveRedundant(true);
        Assertions.assertEquals(loadFileContent("PrettyTable_basic.text"), table.toString());

    }

    @Test
    public void testRemoveRedundantOption() {
        PrettyTable table = new PrettyTable("Col1", "Col2", "Col3", "Col4");
        table.addRow("A", "B", "C", "D");
        table.addRow("A", "B", "C", "Z");
        table.addRow("A", "B", "F", "E");
        table.addRow("A", "B", "F", "E");

        String dedupedText = loadFileContent("PrettyTable_duplicate.text");
        String dedupedHtml = loadFileContent("PrettyTable_duplicate.html");
        String fullText = loadFileContent("PrettyTable_duplicate_undup.text");
        String fullHtml = loadFileContent("PrettyTable_duplicate_undup.html");

        Assertions.assertTrue(table.isRemoveRedundant());
        Assertions.assertEquals(dedupedText, table.formattedString(OutputFormat.TEXT));
        Assertions.assertEquals(dedupedHtml, table.formattedString(OutputFormat.HTML));
        Assertions.assertEquals(dedupedText, table.toString());

        table.setRemoveRedundant(false);
        Assertions.assertFalse(table.isRemoveRedundant());
        Assertions.assertEquals(fullText, table.formattedString(OutputFormat.TEXT));
        Assertions.assertEquals(fullHtml, table.formattedString(OutputFormat.HTML));
        Assertions.assertEquals(fullText, table.toString());

        table.setOutputFormat(OutputFormat.HTML);
        Assertions.assertEquals(fullHtml, table.toString());

        // the explicit flag still overrides the option
        Assertions.assertEquals(dedupedText, table.formattedString(OutputFormat.TEXT, true));
        Assertions.assertEquals(dedupedHtml, table.formattedString(OutputFormat.HTML, true));
        table.setRemoveRedundant(true);
        Assertions.assertEquals(fullText, table.formattedString(OutputFormat.TEXT, false));
        Assertions.assertEquals(fullHtml, table.toHtml(false));

        // CSV and JSON keep every value either way
        String csv = loadFileContent("PrettyTable_duplicate.csv");
        String json = loadFileContent("PrettyTable_duplicate.json");
        Assertions.assertEquals(csv, table.formattedString(OutputFormat.CSV));
        Assertions.assertEquals(json, table.formattedString(OutputFormat.JSON));
        table.setRemoveRedundant(false);
        Assertions.assertEquals(csv, table.formattedString(OutputFormat.CSV));
        Assertions.assertEquals(json, table.formattedString(OutputFormat.JSON));
    }
}
