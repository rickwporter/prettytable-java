package rickwporter.prettytable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public final class PrettyObject {
    private static final String INDENT = "    ";
    private BuilderConfig config;

    public PrettyObject() {
        this(null);
    }

    public PrettyObject(BuilderConfig options) {
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
