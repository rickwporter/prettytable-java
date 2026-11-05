package rickwporter.prettytable;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import rickwporter.prettytable.PrettyTable.HorizontalAlign;

public class RichTableRenderTest {
    @Test
    public void testBasicRich() {
        PrettyTable table = new PrettyTable("Column A", "Column 2", "Column III");
        table.addRow("A", "B", "c");
        table.addRow(1, 1024, 65535);
        table.addRow("Washington", "Adams", "Jefferson");

        String expected = loadFileContent("PrettyTable_basic.txt");
        Assertions.assertEquals(expected, render(table));
        Assertions.assertEquals(expected, table.formattedString(OutputFormat.RICH));
        Assertions.assertEquals(expected, table.toRich(true));

        table.setOutputFormat(OutputFormat.RICH);
        Assertions.assertEquals(OutputFormat.RICH, table.getDefaultOutput());
        Assertions.assertEquals(expected, table.toString());
    }

    @Test
    public void testAlignment() {
        PrettyTable table = new PrettyTable("Column A", "Column 2", "Column III");
        table.setHorizAligns(HorizontalAlign.LEFT, HorizontalAlign.CENTER, HorizontalAlign.RIGHT);
        table.addRow("A", "B", "c");
        table.addRow(1, 1024, 65535);
        table.addRow("Washington", "Adams", "Jefferson");

        Assertions.assertEquals(loadFileContent("PrettyTable_formatted.txt"), render(table));
    }

    @Test
    public void testHiddenHeaderUsesSquareBox() {
        PrettyTable table = nameAgeTable();
        table.setShowHeader(false);
        Assertions.assertEquals(""
            + "┌─────┬────┐\n"
            + "│ Ada │ 36 │\n"
            + "│ Bob │ 7  │\n"
            + "└─────┴────┘\n", render(table));

        PrettyTable noHeaders = new PrettyTable();
        noHeaders.addRow("Ada", 36);
        Assertions.assertEquals(""
            + "┌─────┬────┐\n"
            + "│ Ada │ 36 │\n"
            + "└─────┴────┘\n", render(noHeaders));

        PrettyTable empty = new PrettyTable("Name", "Age");
        empty.setShowHeader(false);
        Assertions.assertEquals(""
            + "┌──┬──┐\n"
            + "└──┴──┘\n", render(empty));
    }

    @Test
    public void testEmptyTableClosesTheBox() {
        PrettyTable empty = new PrettyTable("Name", "Age");
        Assertions.assertEquals(""
            + "┏━━━━━━┳━━━━━┓\n"
            + "┃ Name ┃ Age ┃\n"
            + "┡━━━━━━╇━━━━━┩\n"
            + "└──────┴─────┘\n", render(empty));

        empty.setRowDividers(true);
        Assertions.assertEquals(""
            + "┏━━━━━━┳━━━━━┓\n"
            + "┃ Name ┃ Age ┃\n"
            + "┡━━━━━━╇━━━━━┩\n"
            + "└──────┴─────┘\n", render(empty));
    }

    @Test
    public void testShowLinesFalseMatchesText() {
        PrettyTable table = nameAgeTable();
        table.setShowLines(false);
        Assertions.assertEquals(new TextTableRender().render(table, false), render(table));

        table.setHorizAligns(HorizontalAlign.LEFT, HorizontalAlign.RIGHT);
        Assertions.assertEquals(new TextTableRender().render(table, false), render(table));

        table.setShowHeader(false);
        table.setRowDividers(true);
        Assertions.assertEquals(new TextTableRender().render(table, false), render(table));
    }

    @Test
    public void testRowDividers() {
        PrettyTable table = nameAgeTable();
        table.setRowDividers(true);
        Assertions.assertEquals(""
            + "┏━━━━━━┳━━━━━┓\n"
            + "┃ Name ┃ Age ┃\n"
            + "┡━━━━━━╇━━━━━┩\n"
            + "│ Ada  │ 36  │\n"
            + "├──────┼─────┤\n"
            + "│ Bob  │  7  │\n"
            + "└──────┴─────┘\n", render(table, false));

        table.setShowHeader(false);
        Assertions.assertEquals(""
            + "┌─────┬────┐\n"
            + "│ Ada │ 36 │\n"
            + "├─────┼────┤\n"
            + "│ Bob │ 7  │\n"
            + "└─────┴────┘\n", render(table, false));

        PrettyTable wrapped = new PrettyTable("A", "B");
        wrapped.setRowDividers(true);
        wrapped.addRow("a\nbb", "c");
        wrapped.addRow("d", "ee");
        Assertions.assertEquals(""
            + "┏━━━━┳━━━━┓\n"
            + "┃ A  ┃ B  ┃\n"
            + "┡━━━━╇━━━━┩\n"
            + "│ a  │ c  │\n"
            + "│ bb │    │\n"
            + "├────┼────┤\n"
            + "│ d  │ ee │\n"
            + "└────┴────┘\n", render(wrapped, false));
    }

    @Test
    public void testSharedPrefixOmitsInnerRules() {
        PrettyTable table = new PrettyTable("Col1", "Col2", "Col3", "Col4");
        table.addRow("A", "B", "C", "D");
        table.addRow("A", "B", "C", "Z");
        table.addRow("A", "B", "F", "E");
        table.addRow("A", "B", "F", "E");
        table.setRowDividers(true);

        Assertions.assertEquals(""
            + "┏━━━━━━┳━━━━━━┳━━━━━━┳━━━━━━┓\n"
            + "┃ Col1 ┃ Col2 ┃ Col3 ┃ Col4 ┃\n"
            + "┡━━━━━━╇━━━━━━╇━━━━━━╇━━━━━━┩\n"
            + "│  A   │  B   │  C   │  D   │\n"
            + "├──────┼──────┼──────┼──────┤\n"
            + "│      │      │      │  Z   │\n"
            + "│      │      │  F   │  E   │\n"
            + "│      │      │      │      │\n"
            + "└──────┴──────┴──────┴──────┘\n", render(table, true));

        Assertions.assertEquals(""
            + "┏━━━━━━┳━━━━━━┳━━━━━━┳━━━━━━┓\n"
            + "┃ Col1 ┃ Col2 ┃ Col3 ┃ Col4 ┃\n"
            + "┡━━━━━━╇━━━━━━╇━━━━━━╇━━━━━━┩\n"
            + "│  A   │  B   │  C   │  D   │\n"
            + "├──────┼──────┼──────┼──────┤\n"
            + "│  A   │  B   │  C   │  Z   │\n"
            + "├──────┼──────┼──────┼──────┤\n"
            + "│  A   │  B   │  F   │  E   │\n"
            + "├──────┼──────┼──────┼──────┤\n"
            + "│  A   │  B   │  F   │  E   │\n"
            + "└──────┴──────┴──────┴──────┘\n", render(table, false));
    }

    @Test
    public void testCaption() {
        PrettyTable table = nameAgeTable();
        Assertions.assertEquals(borderedNameAge(), render(table));

        table.setCaption("");
        Assertions.assertEquals(borderedNameAge(), render(table));

        table.setCaption("Friends");
        Assertions.assertEquals(borderedNameAge() + "Friends\n", render(table));
    }

    @Test
    public void testNullAlignment() {
        PrettyTable table = new PrettyTable("A");
        table.addRow("x");
        table.setHorizAligns((HorizontalAlign) null);
        IllegalArgumentException ex = Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> render(table)
        );
        Assertions.assertEquals("Unsupported HorizontalAlignment: null", ex.getMessage());
    }

    @Test
    public void testNestedTableUsesItsOwnFormat() {
        PrettyTable inner = new PrettyTable("A");
        inner.addRow("hi");
        inner.setOutputFormat(OutputFormat.RICH);
        PrettyTable outer = new PrettyTable("Data");
        outer.setHorizAligns(HorizontalAlign.LEFT);
        outer.addRow(inner);

        Assertions.assertEquals(""
            + "┏━━━━━━━━┓\n"
            + "┃ Data   ┃\n"
            + "┡━━━━━━━━┩\n"
            + "│ ┏━━━━┓ │\n"
            + "│ ┃ A  ┃ │\n"
            + "│ ┡━━━━┩ │\n"
            + "│ │ hi │ │\n"
            + "│ └────┘ │\n"
            + "└────────┘\n", outer.formattedString(OutputFormat.RICH));

        inner.setOutputFormat(OutputFormat.TEXT);
        Assertions.assertTrue(outer.formattedString(OutputFormat.RICH).contains("| A  |"));
    }

    @Test
    public void testRichObjectsSamples() {
        Assertions.assertEquals(""
            + "┏━━━━━━┳━━━━━━━━━━━━━━━━┓\n"
            + "┃ Name ┃ Properties     ┃\n"
            + "┡━━━━━━╇━━━━━━━━━━━━━━━━┩\n"
            + "│ sna  │  prop1   1     │\n"
            + "│      │  prop B  None  │\n"
            + "│      │  blah    zay   │\n"
            + "├──────┼────────────────┤\n"
            + "│ foo  │  prop2   2     │\n"
            + "│      │  prop B  True  │\n"
            + "├──────┼────────────────┤\n"
            + "│ bar  │  1  inverse    │\n"
            + "└──────┴────────────────┘\n"
            + "Found 3 items\n",
            rich(readmeList()));

        Assertions.assertEquals(""
            + "┏━━━━━━━━━━┳━━━━━━━━━━━━━━━┓\n"
            + "┃ Property ┃ Value         ┃\n"
            + "┡━━━━━━━━━━╇━━━━━━━━━━━━━━━┩\n"
            + "│ abc      │ def           │\n"
            + "│ ghi      │ False         │\n"
            + "│ jkl      │ mno, pqr, stu │\n"
            + "│ vwx      │ 1, 2, 4       │\n"
            + "│ 2        │ 3             │\n"
            + "│ yxa      │ None          │\n"
            + "└──────────┴───────────────┘\n",
            rich(simpleDict()));

        Assertions.assertEquals(""
            + "┏━━━━━━━━━━━┓\n"
            + "┃ Items     ┃\n"
            + "┡━━━━━━━━━━━┩\n"
            + "│ str value │\n"
            + "├───────────┤\n"
            + "│ 3         │\n"
            + "├───────────┤\n"
            + "│ 4         │\n"
            + "├───────────┤\n"
            + "│ False     │\n"
            + "├───────────┤\n"
            + "│ None      │\n"
            + "├───────────┤\n"
            + "│ foo       │\n"
            + "└───────────┘\n"
            + "Found 6 items\n",
            rich(Arrays.asList("str value", "3", 4, false, null, "foo")));

        List<Object> summary = new ArrayList<Object>();
        summary.add(mapOf("name", "sna", "data", 1));
        summary.add(mapOf("name", "foo", "data", mapOf("a", 1, "b", 2)));
        summary.add(mapOf("name", "bar", "data", null));
        Assertions.assertEquals(""
            + "┏━━━━━━┳━━━━━━━━┓\n"
            + "┃ Name ┃ Data   ┃\n"
            + "┡━━━━━━╇━━━━━━━━┩\n"
            + "│ sna  │ 1      │\n"
            + "├──────┼────────┤\n"
            + "│ foo  │  a  1  │\n"
            + "│      │  b  2  │\n"
            + "├──────┼────────┤\n"
            + "│ bar  │ None   │\n"
            + "└──────┴────────┘\n"
            + "Found 3 items\n",
            rich(summary));

        BuilderConfig options = new BuilderConfig()
            .setItemsLabel("Simple stuff")
            .setItemsCaption("Got {} simple things");
        Assertions.assertEquals(""
            + "┏━━━━━━━━━━━━━━┓\n"
            + "┃ Simple stuff ┃\n"
            + "┡━━━━━━━━━━━━━━┩\n"
            + "│ str value    │\n"
            + "├──────────────┤\n"
            + "│ 3            │\n"
            + "├──────────────┤\n"
            + "│ 4            │\n"
            + "├──────────────┤\n"
            + "│ False        │\n"
            + "├──────────────┤\n"
            + "│ None         │\n"
            + "├──────────────┤\n"
            + "│ foo          │\n"
            + "└──────────────┘\n"
            + "Got 6 simple things\n",
            rich(Arrays.asList("str value", "3", 4, false, null, "foo"), options));
    }

    private String loadFileContent(String filename) {
        try {
            URL resource = getClass().getClassLoader().getResource(filename);
            return Files.readString(Paths.get(resource.toURI()));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to read " + filename, ex);
        }
    }

    private String borderedNameAge() {
        return ""
            + "┏━━━━━━┳━━━━━┓\n"
            + "┃ Name ┃ Age ┃\n"
            + "┡━━━━━━╇━━━━━┩\n"
            + "│ Ada  │ 36  │\n"
            + "│ Bob  │  7  │\n"
            + "└──────┴─────┘\n";
    }

    private PrettyTable nameAgeTable() {
        PrettyTable table = new PrettyTable("Name", "Age");
        table.addRow("Ada", 36);
        table.addRow("Bob", 7);
        return table;
    }

    private List<Object> readmeList() {
        List<Object> items = new ArrayList<Object>();
        items.add(mapOf("name", "sna", "prop1", 1, "prop B", null, "blah", "zay"));
        items.add(mapOf("name", "foo", "prop2", 2, "prop B", true));
        items.add(mapOf("name", "bar", 1, "inverse"));
        return items;
    }

    private Map<Object, Object> simpleDict() {
        return mapOf(
            "abc", "def",
            "ghi", false,
            "jkl", Arrays.asList("mno", "pqr", "stu"),
            "vwx", Arrays.asList(1, 2, 4),
            2, 3,
            "yxa", null);
    }

    private static Map<Object, Object> mapOf(Object... pairs) {
        Map<Object, Object> result = new LinkedHashMap<Object, Object>();
        for (int i = 0; i < pairs.length; i += 2) {
            result.put(pairs[i], pairs[i + 1]);
        }
        return result;
    }

    private String rich(Object data) {
        return rich(data, null);
    }

    private String rich(Object data, BuilderConfig options) {
        return new TableBuilder(options).build(data).formattedString(OutputFormat.RICH);
    }

    private String render(PrettyTable table) {
        return render(table, false);
    }

    private String render(PrettyTable table, boolean removeRedundant) {
        return new RichTableRender().render(table, removeRedundant);
    }
}
