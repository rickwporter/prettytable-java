package rickwporter.prettytable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public class PrettyObjectTest {
    private String loadFileContent(String filename) {
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

    @Test
    public void testSimpleDict() {
        Map<OutputFormat, String> extensions = new LinkedHashMap<OutputFormat, String>();
        extensions.put(OutputFormat.TEXT, "text");
        extensions.put(OutputFormat.RICH, "txt");
        extensions.put(OutputFormat.CSV, "csv");
        extensions.put(OutputFormat.HTML, "html");
        extensions.put(OutputFormat.JSON, "json");
        extensions.put(OutputFormat.YAML, "yaml");

        PrettyObject pretty = new PrettyObject();
        Map<Object, Object> data = this.simpleDict();
        for (Map.Entry<OutputFormat, String> entry : extensions.entrySet()) {
            String filename = "PrettyObject_simple_dict." + entry.getValue();
            Assertions.assertEquals(
                    this.loadFileContent(filename),
                    pretty.render(entry.getKey(), data),
                    filename);
        }
    }

    @Test 
    public void testNullValue() {
        PrettyObject uut = new PrettyObject();

        for (OutputFormat format : OutputFormat.values()) {
            Assertions.assertEquals("", uut.render(format, null));
        }
    }

    @Test 
    public void testSimpleValues() {
        PrettyObject uut = new PrettyObject();

        for (OutputFormat format : OutputFormat.values()) {
            Assertions.assertEquals("1", uut.render(format, 1));
            Assertions.assertEquals("foo", uut.render(format, "foo"));
            Assertions.assertEquals("true", uut.render(format, true));
        }
    }

    @Test 
    public void testConfig() {
        Map<Object, Object> data = this.simpleDict();

        String label = "My label";
        PrettyObject config = new PrettyObject(new BuilderConfig().setPropertyLabel(label));
        PrettyObject unconfig = new PrettyObject();

        String labeled = config.render(OutputFormat.RICH, data);
        String unlabeled = unconfig.render(OutputFormat.RICH, data);

        Assertions.assertNotEquals(labeled, unlabeled);
        Assertions.assertTrue(labeled.contains(label));
        Assertions.assertFalse(unlabeled.contains(label));
    }

    @Test
    public void testJsonProcessingException() throws JsonProcessingException {
        JsonProcessingException failure = new JsonGenerationException("this is it", (JsonGenerator) null);
        ObjectMapper mapper = mock(ObjectMapper.class);
        when(mapper.writeValueAsString(any())).thenThrow(failure);

        PrettyObject uut = new PrettyObject();
        IllegalStateException ex = Assertions.assertThrows(
            IllegalStateException.class,
            () -> uut.renderJson(this.simpleDict(), mapper)
        );
        Assertions.assertEquals("Unable to render object as JSON", ex.getMessage());
        Assertions.assertSame(failure, ex.getCause());
    }

    @Test
    public void testYamlProcessingException() throws JsonProcessingException {
        JsonProcessingException failure = new JsonGenerationException("this is it", (JsonGenerator) null);
        YAMLMapper mapper = mock(YAMLMapper.class);
        when(mapper.writeValueAsString(any())).thenThrow(failure);

        PrettyObject uut = new PrettyObject();
        IllegalStateException ex = Assertions.assertThrows(
            IllegalStateException.class,
            () -> uut.renderYaml(this.simpleDict(), mapper)
        );
        Assertions.assertEquals("Unable to render object as YAML", ex.getMessage());
        Assertions.assertSame(failure, ex.getCause());
    }
}
