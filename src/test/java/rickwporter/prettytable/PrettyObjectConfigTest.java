package rickwporter.prettytable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PrettyObjectConfigTest {
    @Test
    public void testDefaults() {
        PrettyObjectConfig config = new PrettyObjectConfig();
        Assertions.assertEquals("Items", config.getItemsLabel());
        Assertions.assertEquals("Property", config.getPropertyLabel());
        Assertions.assertEquals("Properties", config.getPropertiesLabel());
        Assertions.assertEquals("Value", config.getValueLabel());
        Assertions.assertEquals("Values", config.getValuesLabel());
        Assertions.assertEquals("Unknown", config.getUnknownLabel());
        Assertions.assertEquals("Found {} items", config.getItemsCaption());
        Assertions.assertEquals(Arrays.asList("http://", "https://", "ftp://"), config.getUrlPrefixes());
        Assertions.assertEquals(100, config.getUrlMaxLen());
        Assertions.assertEquals(Arrays.asList("name", "id"), config.getKeyFields());
        Assertions.assertEquals(35, config.getKeyMaxLen());
        Assertions.assertEquals(50, config.getValueMaxLen());
        Assertions.assertNull(config.getColumns());
    }

    @Test
    public void testSetters() {
        PrettyObjectConfig config = new PrettyObjectConfig();

        Assertions.assertSame(config, config.setItemsLabel("mickey"));
        Assertions.assertEquals("mickey", config.getItemsLabel());

        Assertions.assertSame(config, config.setPropertyLabel("minnie"));
        Assertions.assertEquals("minnie", config.getPropertyLabel());

        Assertions.assertSame(config, config.setPropertiesLabel("donald"));
        Assertions.assertEquals("donald", config.getPropertiesLabel());

        Assertions.assertSame(config, config.setValueLabel("daisy"));
        Assertions.assertEquals("daisy", config.getValueLabel());

        Assertions.assertSame(config, config.setValuesLabel("goofy"));
        Assertions.assertEquals("goofy", config.getValuesLabel());

        Assertions.assertSame(config, config.setUnknownLabel("pluto"));
        Assertions.assertEquals("pluto", config.getUnknownLabel());

        Assertions.assertSame(config, config.setItemsCaption("huey, dewey, louie"));
        Assertions.assertEquals("huey, dewey, louie", config.getItemsCaption());

        List<String> prefixes = new ArrayList<String>(Arrays.asList("ftp://"));
        Assertions.assertSame(config, config.setUrlPrefixes(prefixes));
        prefixes.add("http://");
        Assertions.assertEquals(Arrays.asList("ftp://"), config.getUrlPrefixes());

        Assertions.assertSame(config, config.setUrlMaxLen(1));
        Assertions.assertEquals(1, config.getUrlMaxLen());

        List<String> fields = new ArrayList<String>(Arrays.asList("title"));
        Assertions.assertSame(config, config.setKeyFields(fields));
        fields.add("slug");
        Assertions.assertEquals(Arrays.asList("title"), config.getKeyFields());

        Assertions.assertSame(config, config.setKeyMaxLen(2));
        Assertions.assertEquals(2, config.getKeyMaxLen());

        Assertions.assertSame(config, config.setValueMaxLen(3));
        Assertions.assertEquals(3, config.getValueMaxLen());

        List<String> columns = new ArrayList<String>(Arrays.asList("service", "name"));
        Assertions.assertSame(config, config.setColumns(columns));
        columns.add("id");
        Assertions.assertEquals(Arrays.asList("service", "name"), config.getColumns());

        Assertions.assertSame(config, config.setColumns(new ArrayList<String>()));
        Assertions.assertEquals(Arrays.asList(), config.getColumns());

        Assertions.assertSame(config, config.setColumns(null));
        Assertions.assertNull(config.getColumns());
    }

    @Test
    public void testSettersRejectNull() {
        PrettyObjectConfig config = new PrettyObjectConfig();

        Assertions.assertThrows(NullPointerException.class, () -> config.setItemsLabel(null));
        Assertions.assertEquals("Items", config.getItemsLabel());

        Assertions.assertThrows(NullPointerException.class, () -> config.setPropertyLabel(null));
        Assertions.assertEquals("Property", config.getPropertyLabel());

        Assertions.assertThrows(NullPointerException.class, () -> config.setPropertiesLabel(null));
        Assertions.assertEquals("Properties", config.getPropertiesLabel());

        Assertions.assertThrows(NullPointerException.class, () -> config.setValueLabel(null));
        Assertions.assertEquals("Value", config.getValueLabel());

        Assertions.assertThrows(NullPointerException.class, () -> config.setValuesLabel(null));
        Assertions.assertEquals("Values", config.getValuesLabel());

        Assertions.assertThrows(NullPointerException.class, () -> config.setUnknownLabel(null));
        Assertions.assertEquals("Unknown", config.getUnknownLabel());

        Assertions.assertThrows(NullPointerException.class, () -> config.setItemsCaption(null));
        Assertions.assertEquals("Found {} items", config.getItemsCaption());

        Assertions.assertThrows(NullPointerException.class, () -> config.setUrlPrefixes(null));
        Assertions.assertEquals(Arrays.asList("http://", "https://", "ftp://"), config.getUrlPrefixes());

        Assertions.assertThrows(NullPointerException.class, () -> config.setKeyFields(null));
        Assertions.assertEquals(Arrays.asList("name", "id"), config.getKeyFields());
    }
}
