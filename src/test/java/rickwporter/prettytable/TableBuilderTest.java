package rickwporter.prettytable;

import rickwporter.prettytable.PrettyTable.HorizontalAlign;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TableBuilderTest {
    private static final String LONG_KEY = "really looooooooooooooooooonnnng key value";
    private static final String LONG_URL =
            "https://typer.tiangolo.com/virtual-environments/#install-packages-directly";
    private static final String LONG_FTP =
            "ftp://typer.tiangolo.com/virtual-environments/#install-packages-directly?"
            + "12345678901234568901234567890123";

    private final String loadFileContent(String filename) {
        URL resource = getClass().getClassLoader().getResource(filename);
        if (resource == null) {
            throw new IllegalStateException("Missing resource " + filename);
        }
        try {
            return Files.readString(Paths.get(resource.toURI()));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to read " + filename, ex);
        }
    }

    private static Map<Object, Object> mapOf(Object... pairs) {
        if (pairs.length % 2 != 0) {
            throw new IllegalArgumentException("Expected key/value pairs");
        }
        Map<Object, Object> result = new LinkedHashMap<Object, Object>();
        for (int i = 0; i < pairs.length; i += 2) {
            result.put(pairs[i], pairs[i + 1]);
        }
        return result;
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

    private List<Object> summaryList() {
        List<Object> items = new ArrayList<Object>();
        items.add(mapOf("name", "sna", "data", 1));
        items.add(mapOf("name", "foo", "data", mapOf("a", 1, "b", 2)));
        items.add(mapOf("name", "bar", "data", null));
        return items;
    }

    private Map<Object, Object> innerList() {
        List<Object> items = new ArrayList<Object>();
        items.add(mapOf("name", "sna", "abc", "def", "ghi", true));
        items.add(mapOf("name", "foo", "abc", "def", "ghi", null));
        items.add(mapOf("name", "bar", "abc", "def", "ghi", 1.2345d));
        items.add(mapOf("abc", "def", "ghi", "blah"));
        return mapOf("prop 1", "simple", "prOp B", items, "Prop III", null);
    }

    private List<Object> readmeList() {
        List<Object> items = new ArrayList<Object>();
        items.add(mapOf("name", "sna", "prop1", 1, "prop B", null, "blah", "zay"));
        items.add(mapOf("name", "foo", "prop2", 2, "prop B", true));
        items.add(mapOf("name", "bar", 1, "inverse"));
        return items;
    }

    private List<Object> simpleList() {
        return Arrays.asList("str value", "3", 4, false, null, "foo");
    }

    private Map<Object, Object> longValues() {
        StringBuilder repeated = new StringBuilder();
        for (int i = 0; i < 75; i++) {
            repeated.append("a");
        }
        return mapOf(
                "mid-url", LONG_URL,
                LONG_KEY, "sna",
                "long value", repeated.toString(),
                "long", LONG_FTP);
    }

    private List<Object> columnList() {
        List<Object> items = new ArrayList<Object>();
        items.add(mapOf("name", "a", "id", 1, "help", "hello", "service", "army"));
        items.add(mapOf("name", "b", "id", 2, "help", "world", "service", "navy"));
        items.add(mapOf("name", "c", "id", 3, "help", "jazzy", "service", "air-force"));
        items.add(mapOf("name", "d", "id", 4, "help", "buzzy", "service", "marines"));
        items.add(mapOf("name", "e", "id", 5, "help", "fuzzy", "service", "space-force"));
        items.add(mapOf("name", "f", "id", 6, "help", "jiggy", "service", "coast guard"));
        return items;
    }

    private void assertRendered(String filename, Object data) {
        this.assertRendered(filename, data, null);
    }

    private void assertRendered(String filename, Object data, BuilderConfig options) {
        String expected = this.loadFileContent(filename);
        String actual = new TableBuilder(options).build(data).toString();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testHeaderize() {
        TableBuilder obj = new TableBuilder();
        Assertions.assertEquals("Properties", obj.headerize("*"));
        Assertions.assertEquals("", obj.headerize(""));
        Assertions.assertEquals("Hello", obj.headerize("hello"));

        TableBuilder custom = new TableBuilder(new BuilderConfig().setWildcardHeader("Rest"));
        Assertions.assertEquals("Rest", custom.headerize("*"));
        Assertions.assertEquals("Hello", custom.headerize("hello"));
    }

    @Test 
    public void testStringify() {
        Assertions.assertEquals("None", TableBuilder.stringify(null));
        Assertions.assertEquals("True", TableBuilder.stringify(true));
        Assertions.assertEquals("False", TableBuilder.stringify(false));
        Assertions.assertEquals("My string", TableBuilder.stringify("My string"));
        Assertions.assertEquals("3", TableBuilder.stringify(3));
    }

    @Test
    public void testTruncate() {
        TableBuilder obj = new TableBuilder();
        Assertions.assertEquals("This is it", obj.truncate("This is it", 10));
        Assertions.assertEquals("This...", obj.truncate("This is it", 7));
        // corner case with nothing
        Assertions.assertEquals("...", obj.truncate("This is it", 2));

        TableBuilder custom = new TableBuilder(new BuilderConfig().setEllipsis("~"));
        Assertions.assertEquals("This i~", custom.truncate("This is it", 7));
        Assertions.assertEquals("~", custom.truncate("This is it", 0));
    }

    @Test 
    public void testFormatCaption() {
        Assertions.assertEquals("Found 23 items", TableBuilder.formatCaption("Found {} items", 23));
        Assertions.assertEquals("Found 99 items", TableBuilder.formatCaption("Found %d items", 99));
        Assertions.assertEquals("Found 12 items", TableBuilder.formatCaption("Found %s items", 12));

        // odd behaviors (may want to change)
        Assertions.assertEquals("Found 23 23 items", TableBuilder.formatCaption("Found {} {} items", 23));
    }

    @Test
    public void testSimpleDict() {
        PrettyTable table = new TableBuilder().build(this.simpleDict());
        this.assertRendered("TableBuilder_simple_dict.text", this.simpleDict());
        Assertions.assertTrue(table.isShowHeader());
        Assertions.assertTrue(table.isShowLines());
        Assertions.assertFalse(table.isRemoveRedundant());
        Assertions.assertNull(table.getCaption());
        Assertions.assertEquals(HorizontalAlign.LEFT, table.getHorizAligns().get(0));
        Assertions.assertEquals(Arrays.asList("Property", "Value"), table.getHeaders());
        Assertions.assertEquals(6, table.getRowCount());
        Assertions.assertEquals("abc", table.getCell(0, 0));
        Assertions.assertEquals("def", table.getCell(0, 1));
        Assertions.assertEquals("False", table.getCell(1, 1));
        Assertions.assertEquals("mno, pqr, stu", table.getCell(2, 1));
        Assertions.assertEquals("1, 2, 4", table.getCell(3, 1));
        Assertions.assertEquals("2", table.getCell(4, 0));
        Assertions.assertEquals("3", table.getCell(4, 1));
        Assertions.assertEquals("None", table.getCell(5, 1));
    }

    @Test
    public void testSummaryList() {
        PrettyTable table = new TableBuilder().build(this.summaryList());
        this.assertRendered("TableBuilder_summary.text", this.summaryList());
        Assertions.assertEquals(Arrays.asList("Name", "Data"), table.getHeaders());
        Assertions.assertTrue(table.isShowLines());
        Assertions.assertEquals("Found 3 items", table.getCaption());
        PrettyTable nested = (PrettyTable) table.getCell(1, 1);
        Assertions.assertFalse(nested.isShowHeader());
        Assertions.assertEquals("a", nested.getCell(0, 0));
        Assertions.assertEquals("1", nested.getCell(0, 1));
        Assertions.assertEquals("b", nested.getCell(1, 0));
    }

    @Test
    public void testInnerList() {
        PrettyTable table = new TableBuilder().build(this.innerList());
        this.assertRendered("TableBuilder_inner.text", this.innerList());
        PrettyTable inner = (PrettyTable) table.getCell(1, 1);
        Assertions.assertFalse(inner.isShowHeader());
        Assertions.assertEquals(4, inner.getRowCount());
        Assertions.assertEquals("sna", inner.getCell(0, 0));
        Assertions.assertEquals("Unknown", inner.getCell(3, 0));
        PrettyTable props = (PrettyTable) inner.getCell(0, 1);
        Assertions.assertEquals("abc", props.getCell(0, 0));
        Assertions.assertEquals("def", props.getCell(0, 1));
        Assertions.assertEquals("True", props.getCell(1, 1));
    }

    @Test
    public void testReadmeList() {
        this.assertRendered("TableBuilder_readme.text", this.readmeList());
    }

    @Test
    public void testNamelessList() {
        List<Object> items = new ArrayList<Object>();
        items.add(this.simpleDict());
        items.add(mapOf("foo", "bar"));
        this.assertRendered("TableBuilder_nameless.text", items);
    }

    @Test
    public void testSimpleList() {
        this.assertRendered("TableBuilder_simple_list.text", this.simpleList());
        PrettyTable table = new TableBuilder().build(this.simpleList());
        Assertions.assertEquals(Arrays.asList("Items"), table.getHeaders());
        Assertions.assertEquals("Found 6 items", table.getCaption());
        Assertions.assertEquals("False", table.getCell(3, 0));
        Assertions.assertEquals("None", table.getCell(4, 0));
    }

    @Test
    public void testTruncated() {
        PrettyTable table = new TableBuilder().build(this.longValues());
        this.assertRendered("TableBuilder_truncated.text", this.longValues());
        Assertions.assertEquals("mid-url", table.getCell(0, 0));
        Assertions.assertEquals(LONG_URL, table.getCell(0, 1));
        Assertions.assertEquals("really looooooooooooooooooonnnng...", table.getCell(1, 0));
        Assertions.assertEquals("sna", table.getCell(1, 1));
        StringBuilder repeated = new StringBuilder();
        for (int i = 0; i < 47; i++) {
            repeated.append("a");
        }
        repeated.append("...");
        Assertions.assertEquals(repeated.toString(), table.getCell(2, 1));
    }

    @Test
    public void testConfigTruncated() {
        BuilderConfig options = new BuilderConfig()
                .setUrlMaxLen(16)
                .setValueMaxLen(20)
                .setKeyMaxLen(4);
        this.assertRendered("TableBuilder_config_trunc.text", this.longValues(), options);
    }

    @Test
    public void testConfigLabels() {
        BuilderConfig options = new BuilderConfig()
                .setUrlMaxLen(16)
                .setValueMaxLen(50)
                .setKeyMaxLen(100)
                .setUrlPrefixes(Arrays.asList("https://"))
                .setPropertyLabel("foo")
                .setValueLabel("bar");
        this.assertRendered("TableBuilder_config_labels.text", this.longValues(), options);
    }

    @Test
    public void testConfigInnerList() {
        BuilderConfig options = new BuilderConfig()
                .setKeyFields(Arrays.asList("ghi"))
                .setPropertiesLabel("Different");
        this.assertRendered("TableBuilder_config_inner.text", this.innerList(), options);
    }

    @Test
    public void testColumns() {
        this.assertRendered("TableBuilder_columns.text", this.columnList(),
                new BuilderConfig().setColumns(Arrays.asList("service", "name")));
        this.assertRendered("TableBuilder_columns_wild.text", this.columnList(),
                new BuilderConfig().setColumns(Arrays.asList("service", "*", "id")));
        this.assertRendered("TableBuilder_columns_missing.text", this.columnList(),
                new BuilderConfig().setColumns(Arrays.asList("id", "missing", "name")));
    }

    @Test
    public void testEmptyColumnsUsesDefaultList() {
        List<Object> items = new ArrayList<Object>();
        items.add(mapOf("name", "a", "x", 1, "y", 2));
        PrettyTable table = new TableBuilder(new BuilderConfig().setColumns(new ArrayList<String>())).build(items);
        Assertions.assertEquals(Arrays.asList("Name", "Properties"), table.getHeaders());
    }

    @Test
    public void testEmptyListValue() {
        PrettyTable table = new TableBuilder().build(mapOf("items", new ArrayList<Object>()));
        Assertions.assertEquals("items", table.getCell(0, 0));
        Assertions.assertEquals("[]", table.getCell(0, 1));
    }

    @Test
    public void testCaptionWrap() {
        BuilderConfig options = new BuilderConfig()
                .setItemsLabel("Simple stuff")
                .setItemsCaption("Got {} simple things");
        this.assertRendered("TableBuilder_caption.text", this.simpleList(), options);
    }

    @Test
    public void testCaptionLong() {
        BuilderConfig options = new BuilderConfig()
                .setItemsCaption("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        this.assertRendered("TableBuilder_caption_long.text", Arrays.asList("a"), options);
    }

    @Test
    public void testRepeatedNamesNotDeduped() {
        List<Object> items = new ArrayList<Object>();
        items.add(mapOf("name", "a", "value", 1));
        items.add(mapOf("name", "a", "value", 2));
        PrettyTable table = new TableBuilder().build(items);
        this.assertRendered("TableBuilder_repeated.text", items);
        Assertions.assertEquals("a", table.getCell(0, 0));
        Assertions.assertEquals("a", table.getCell(1, 0));
    }

    @Test
    public void testIdKey() {
        List<Object> items = new ArrayList<Object>();
        items.add(mapOf("id", 10, "role", "x"));
        items.add(mapOf("id", 11, "role", "y"));
        PrettyTable table = new TableBuilder().build(items);
        this.assertRendered("TableBuilder_by_id.text", items);
        Assertions.assertEquals(Arrays.asList("Id", "Role"), table.getHeaders());
    }

    @Test
    public void testNothingFound() {
        Assertions.assertNull(new TableBuilder().build(null));
        Assertions.assertNull(new TableBuilder().build(new LinkedHashMap<String, Object>()));
        Assertions.assertNull(new TableBuilder().build(new ArrayList<Object>()));
    }

    @Test
    public void testDoesNotMutateInput() {
        Map<Object, Object> item = mapOf("name", "sna", "a", 1, "b", 2);
        List<Object> data = new ArrayList<Object>();
        data.add(item);
        new TableBuilder().build(data);
        Assertions.assertEquals(3, item.size());
        Assertions.assertEquals("sna", item.get("name"));
        Assertions.assertEquals(1, item.get("a"));
    }

    @Test
    public void testColumnsIgnoredForMap() {
        BuilderConfig options = new BuilderConfig()
                .setColumns(Arrays.asList("abc"));
        this.assertRendered("TableBuilder_simple_dict.text", this.simpleDict(), options);
    }

    @Test
    public void testNullConfigUsesDefaults() {
        this.assertRendered("TableBuilder_simple_dict.text", this.simpleDict(), null);
    }

    @Test
    public void testUnknownLabel() {
        BuilderConfig options = new BuilderConfig()
                .setUnknownLabel("Anonymous");
        String rendered = new TableBuilder(options).build(this.innerList()).toString();
        Assertions.assertTrue(rendered.contains("Anonymous"));
        Assertions.assertFalse(rendered.contains("Unknown"));
    }

    @Test
    public void testWildcardIgnoresPropertiesLabel() {
        BuilderConfig options = new BuilderConfig()
                .setPropertiesLabel("Different")
                .setColumns(Arrays.asList("service", "*", "id"));
        String rendered = new TableBuilder(options).build(this.columnList()).toString();
        Assertions.assertTrue(rendered.contains("Properties"));
        Assertions.assertFalse(rendered.contains("Different"));
    }

    @Test
    public void testUnsupportedType() {
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new TableBuilder().build(new Object());
        });
        Assertions.assertEquals("Unable to create table for type Object", ex.getMessage());

        ex = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new TableBuilder().build(Arrays.asList(new Object()));
        });
        Assertions.assertEquals("Unable to create table for type list", ex.getMessage());

        ex = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            List<Object> items = new ArrayList<Object>();
            items.add(mapOf("name", "a"));
            items.add("nope");
            new TableBuilder().build(items);
        });
        Assertions.assertEquals("Unable to create table for type list", ex.getMessage());
    }
}
