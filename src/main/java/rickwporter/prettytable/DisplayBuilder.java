package rickwporter.prettytable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

/**
 * Renders a value as a string.
 * {@code null} becomes an empty string. A string, number, or boolean is rendered with
 * {@link String#valueOf(Object)}. JSON and YAML write the value with Jackson. Every other format
 * builds a {@link PrettyTable} from a {@link java.util.Map} or {@link java.util.List}.
 */
public final class DisplayBuilder {
    private static final String INDENT = "    ";
    private BuilderConfig config;

    /**
     * Creates a renderer that uses {@link BuilderConfig} defaults.
     */
    public DisplayBuilder() {
        this(null);
    }

    /**
     * Creates a renderer that uses {@code options} for table output.
     * JSON and YAML ignore the config and write the value as given. A {@code null} config uses
     * {@link BuilderConfig} defaults.
     *
     * @param options labels, limits, and format settings, or {@code null}
     */
    public DisplayBuilder(BuilderConfig options) {
        this.config = options == null ? new BuilderConfig() : options;
    }

    private static ObjectMapper jsonMapper() {
        ObjectMapper mapper = new ObjectMapper();
        DefaultIndenter indenter = new DefaultIndenter(INDENT, "\n");
        DefaultPrettyPrinter printer = new DefaultPrettyPrinter()
            .withArrayIndenter(indenter)
            .withObjectIndenter(indenter);
        mapper.setDefaultPrettyPrinter(printer);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }

    private static YAMLMapper yamlMapper() {
        YAMLMapper mapper = new YAMLMapper();
        mapper.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        mapper.disable(YAMLGenerator.Feature.SPLIT_LINES);
        mapper.enable(YAMLGenerator.Feature.LITERAL_BLOCK_STYLE);
        return mapper;
    }

    String renderJson(Object object, ObjectMapper mapper) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to render object as JSON", ex);
        }
    }

    String renderYaml(Object object, YAMLMapper mapper) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to render object as YAML", ex);
        }
    }

    /**
     * Renders {@code object} in {@code format}.
     *
     * @param format output format
     * @param object value to render, or {@code null}
     * @return the rendered text, or an empty string when {@code object} is {@code null}
     * @throws IllegalArgumentException if a table format is given a value that is not a map or list
     * @throws IllegalStateException if JSON or YAML writing fails
     */
    public String render(OutputFormat format, Object object) {
        if (object == null) {
            return "";
        }
        if (object instanceof String || object instanceof Number || object instanceof Boolean) {
            return String.valueOf(object);
        }
        if (format == OutputFormat.JSON) {
            return this.renderJson(object, jsonMapper());
        }

        if (format == OutputFormat.YAML) {
            return this.renderYaml(object, yamlMapper());
        }

        PrettyTable table = new TableBuilder(this.config).build(format, object);
        return table.toString();
    }

}
