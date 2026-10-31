package rickwporter.prettytable;

import org.junit.jupiter.api.Test;

import rickwporter.prettytable.PrettyTable.HorizontalAlign;

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
