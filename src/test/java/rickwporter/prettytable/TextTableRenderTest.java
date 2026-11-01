package rickwporter.prettytable;

import org.junit.jupiter.api.Test;

import rickwporter.prettytable.PrettyTable.HorizontalAlign;
import rickwporter.prettytable.PrettyTable.OutputFormat;

import org.junit.jupiter.api.Assertions;

public class TextTableRenderTest {
    @Test 
    public void testPad() {
        TextTableRender uut = new TextTableRender();

        Assertions.assertEquals(" a     ", uut.pad("a", 5, HorizontalAlign.LEFT));
        Assertions.assertEquals("   a   ", uut.pad("a", 5, HorizontalAlign.CENTER));
        Assertions.assertEquals("     a ", uut.pad("a", 5, HorizontalAlign.RIGHT));

        Assertions.assertThrows(IllegalArgumentException.class, () -> uut.pad("a", 5, null));
        //Assertions.assertThrows(IllegalArgumentException.class, () -> uut.pad("a", 5, new Integer(3)));
    }

    @Test
    public void testShowHeader() {
        PrettyTable table = nameAgeTable();
        Assertions.assertTrue(table.isShowHeader());
        Assertions.assertEquals(borderedNameAge(), render(table));

        table.setShowHeader(false);
        Assertions.assertFalse(table.isShowHeader());
        String withoutHeader = ""
            + "+-----+----+\n"
            + "| Ada | 36 |\n"
            + "| Bob | 7  |\n"
            + "+-----+----+\n";
        Assertions.assertEquals(withoutHeader, render(table));
        Assertions.assertEquals(withoutHeader, table.toString());

        // header text is still stored, and showing it again restores the wider columns
        table.setShowHeader(true);
        Assertions.assertEquals(borderedNameAge(), render(table));

        // an empty header list is omitted even when the option is on
        PrettyTable noHeaders = new PrettyTable();
        noHeaders.addRow("Ada", 36);
        Assertions.assertTrue(noHeaders.isShowHeader());
        Assertions.assertEquals(""
            + "+-----+----+\n"
            + "| Ada | 36 |\n"
            + "+-----+----+\n", render(noHeaders));

        // hiding the header of an empty table leaves the padding-wide frame
        PrettyTable empty = new PrettyTable("Name", "Age");
        empty.setShowHeader(false);
        Assertions.assertEquals(""
            + "+--+--+\n"
            + "+--+--+\n", render(empty));
    }

    @Test
    public void testShowLines() {
        PrettyTable table = nameAgeTable();
        Assertions.assertTrue(table.isShowLines());
        Assertions.assertEquals(borderedNameAge(), render(table));

        table.setShowLines(false);
        Assertions.assertFalse(table.isShowLines());
        String plain = ""
            + " Name  Age \n"
            + " Ada   36  \n"
            + " Bob    7  \n";
        Assertions.assertEquals(plain, render(table));
        Assertions.assertEquals(plain, table.toString());

        // column alignment still applies when the border is omitted
        table.setHorizAligns(HorizontalAlign.LEFT, HorizontalAlign.RIGHT);
        Assertions.assertEquals(""
            + " Name  Age \n"
            + " Ada    36 \n"
            + " Bob     7 \n", render(table));

        // every wrapped line stays borderless
        PrettyTable wrapped = new PrettyTable();
        wrapped.setShowLines(false);
        wrapped.addRow("A\nBB", "C");
        Assertions.assertEquals(""
            + " A   C \n"
            + " BB    \n", render(wrapped));

        // repeated leading cells are still cleared, and the border stays off
        PrettyTable repeated = new PrettyTable("A", "B");
        repeated.setShowLines(false);
        repeated.setShowHeader(false);
        repeated.addRow("x", "1");
        repeated.addRow("x", "2");
        Assertions.assertEquals(""
            + " x  1 \n"
            + " x  2 \n", render(repeated, false));
        Assertions.assertEquals(""
            + " x  1 \n"
            + "    2 \n", render(repeated, true));
        Assertions.assertEquals(""
            + " x  1 \n"
            + "    2 \n", repeated.toString());
    }

    @Test
    public void testCaption() {
        PrettyTable table = nameAgeTable();
        Assertions.assertNull(table.getCaption());
        Assertions.assertEquals(borderedNameAge(), render(table));

        table.setCaption("");
        Assertions.assertEquals("", table.getCaption());
        Assertions.assertEquals(borderedNameAge(), render(table));

        table.setCaption("Friends");
        Assertions.assertEquals("Friends", table.getCaption());
        Assertions.assertEquals(borderedNameAge() + "Friends\n", render(table));
        Assertions.assertEquals(borderedNameAge() + "Friends\n", table.toString());

        // newlines are passed through
        table.setCaption("Top\n\nBottom");
        Assertions.assertEquals(borderedNameAge() + "Top\n\nBottom\n", render(table));

        // a caption longer than the table is left intact, on its own line
        table.setCaption("Longer than the table");
        Assertions.assertEquals(borderedNameAge() + "Longer than the table\n", render(table));

        table.setShowHeader(false);
        table.setShowLines(true);
        table.setCaption("Go");
        Assertions.assertEquals(""
            + "+-----+----+\n"
            + "| Ada | 36 |\n"
            + "| Bob | 7  |\n"
            + "+-----+----+\n"
            + "Go\n", render(table));

        table.setShowLines(false);
        Assertions.assertEquals(""
            + " Ada  36 \n"
            + " Bob  7  \n"
            + "Go\n", render(table));

        table.setCaption(null);
        Assertions.assertNull(table.getCaption());
        Assertions.assertEquals(""
            + " Ada  36 \n"
            + " Bob  7  \n", render(table));
    }

    @Test
    public void testRowDividers() {
        PrettyTable table = nameAgeTable();
        Assertions.assertFalse(table.isRowDividers());
        Assertions.assertEquals(borderedNameAge(), render(table, false));

        table.setRowDividers(true);
        Assertions.assertTrue(table.isRowDividers());
        // repeated cells are kept here, so each data row closes with a rule
        Assertions.assertEquals(dividedNameAge(), render(table, false));
        table.setRemoveRedundant(false);
        Assertions.assertEquals(dividedNameAge(), table.toString());

        table.setHorizAligns(HorizontalAlign.LEFT, HorizontalAlign.RIGHT);
        Assertions.assertEquals(""
            + "+------+-----+\n"
            + "| Name | Age |\n"
            + "+------+-----+\n"
            + "| Ada  |  36 |\n"
            + "+------+-----+\n"
            + "| Bob  |   7 |\n"
            + "+------+-----+\n", render(table, false));

        // one rule follows the whole wrapped row
        PrettyTable wrapped = new PrettyTable("A", "B");
        wrapped.setRowDividers(true);
        wrapped.setRemoveRedundant(false);
        wrapped.addRow("a\nbb", "c");
        wrapped.addRow("d", "ee");
        Assertions.assertEquals(""
            + "+----+----+\n"
            + "| A  | B  |\n"
            + "+----+----+\n"
            + "| a  | c  |\n"
            + "| bb |    |\n"
            + "+----+----+\n"
            + "| d  | ee |\n"
            + "+----+----+\n", render(wrapped, false));

        table.setHorizAligns(HorizontalAlign.CENTER, HorizontalAlign.CENTER);
        table.setShowHeader(false);
        Assertions.assertEquals(""
            + "+-----+----+\n"
            + "| Ada | 36 |\n"
            + "+-----+----+\n"
            + "| Bob | 7  |\n"
            + "+-----+----+\n", render(table, false));

        PrettyTable noHeaders = new PrettyTable();
        noHeaders.setRowDividers(true);
        noHeaders.addRow("Ada", 36);
        noHeaders.addRow("Bob", 7);
        Assertions.assertEquals(""
            + "+-----+----+\n"
            + "| Ada | 36 |\n"
            + "+-----+----+\n"
            + "| Bob | 7  |\n"
            + "+-----+----+\n", render(noHeaders, false));

        // an empty table keeps the rule under the header and skips the extra closing rule
        PrettyTable empty = new PrettyTable("Name", "Age");
        empty.setRowDividers(true);
        Assertions.assertEquals(""
            + "+------+-----+\n"
            + "| Name | Age |\n"
            + "+------+-----+\n", render(empty, false));

        empty.setShowHeader(false);
        Assertions.assertEquals(""
            + "+--+--+\n", render(empty, false));

        table.setShowHeader(true);
        table.setShowLines(false);
        Assertions.assertEquals(""
            + " Name  Age \n"
            + " Ada   36  \n"
            + " Bob    7  \n", render(table, false));

        table.setShowLines(true);
        table.setCaption("Friends");
        Assertions.assertEquals(dividedNameAge() + "Friends\n", render(table, false));
        Assertions.assertEquals(dividedNameAge() + "Friends\n", table.toString());

        table.setRowDividers(false);
        Assertions.assertFalse(table.isRowDividers());
        Assertions.assertEquals(borderedNameAge() + "Friends\n", table.toString());
    }

    @Test
    public void testRowDividersAndRedundant() {
        PrettyTable table = new PrettyTable("Col1", "Col2", "Col3", "Col4");
        table.addRow("A", "B", "C", "D");
        table.addRow("A", "B", "C", "Z");
        table.addRow("A", "B", "F", "E");
        table.addRow("A", "B", "F", "E");
        table.setRowDividers(true);

        // a shared leading prefix suppresses the rule under that row
        String deduped = ""
            + "+------+------+------+------+\n"
            + "| Col1 | Col2 | Col3 | Col4 |\n"
            + "+------+------+------+------+\n"
            + "|  A   |  B   |  C   |  D   |\n"
            + "+------+------+------+------+\n"
            + "|      |      |      |  Z   |\n"
            + "|      |      |  F   |  E   |\n"
            + "|      |      |      |      |\n";
        Assertions.assertEquals(deduped, render(table, true));
        Assertions.assertEquals(deduped, table.toString());
        Assertions.assertEquals(deduped, table.formattedString(OutputFormat.TEXT, true));

        String everyRow = ""
            + "+------+------+------+------+\n"
            + "| Col1 | Col2 | Col3 | Col4 |\n"
            + "+------+------+------+------+\n"
            + "|  A   |  B   |  C   |  D   |\n"
            + "+------+------+------+------+\n"
            + "|  A   |  B   |  C   |  Z   |\n"
            + "+------+------+------+------+\n"
            + "|  A   |  B   |  F   |  E   |\n"
            + "+------+------+------+------+\n"
            + "|  A   |  B   |  F   |  E   |\n"
            + "+------+------+------+------+\n";
        Assertions.assertEquals(everyRow, render(table, false));
        Assertions.assertEquals(everyRow, table.formattedString(OutputFormat.TEXT, false));
        table.setRemoveRedundant(false);
        Assertions.assertEquals(everyRow, table.toString());

        // a repeated leading cell drops the rule; a changed leading cell keeps it
        PrettyTable names = new PrettyTable("Name", "Age");
        names.setRowDividers(true);
        names.addRow("Ada", 36);
        names.addRow("Ada", 36);
        names.addRow("Bob", 7);
        Assertions.assertEquals(""
            + "+------+-----+\n"
            + "| Name | Age |\n"
            + "+------+-----+\n"
            + "| Ada  | 36  |\n"
            + "+------+-----+\n"
            + "|      |     |\n"
            + "| Bob  |  7  |\n"
            + "+------+-----+\n", render(names, true));
    }

    private String dividedNameAge() {
        return ""
            + "+------+-----+\n"
            + "| Name | Age |\n"
            + "+------+-----+\n"
            + "| Ada  | 36  |\n"
            + "+------+-----+\n"
            + "| Bob  |  7  |\n"
            + "+------+-----+\n";
    }

    private PrettyTable nameAgeTable() {
        PrettyTable table = new PrettyTable("Name", "Age");
        table.addRow("Ada", 36);
        table.addRow("Bob", 7);
        return table;
    }

    private String borderedNameAge() {
        return ""
            + "+------+-----+\n"
            + "| Name | Age |\n"
            + "+------+-----+\n"
            + "| Ada  | 36  |\n"
            + "| Bob  |  7  |\n"
            + "+------+-----+\n";
    }

    private String render(PrettyTable table) {
        return render(table, false);
    }

    private String render(PrettyTable table, boolean removeRedundant) {
        return new TextTableRender().render(table, removeRedundant);
    }
}
