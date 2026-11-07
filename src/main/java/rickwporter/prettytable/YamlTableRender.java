package rickwporter.prettytable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

class YamlTableRender implements TableRenderInterface {
    private YAMLMapper yamlMapper;

    YamlTableRender() {
        this(null);
    }

    YamlTableRender(YAMLMapper mapper) {
        this.yamlMapper = (mapper == null ? yamlMapper() : mapper);
    }

    private static YAMLMapper yamlMapper() {
        YAMLMapper mapper = new YAMLMapper();
        mapper.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        mapper.disable(YAMLGenerator.Feature.SPLIT_LINES);
        mapper.enable(YAMLGenerator.Feature.LITERAL_BLOCK_STYLE);
        return mapper;
    }

    private List<Object> toData(PrettyTable table) {
        List<Object> data = new ArrayList<Object>();
        List<String> headers = table.getHeaders();
        if (!headers.isEmpty()) {
            data.add(new ArrayList<String>(headers));
        }
        for (List<Object> row : table.getRows()) {
            if (headers.isEmpty()) {
                List<Object> values = new ArrayList<Object>();
                for (Object cell : row) {
                    values.add(this.cellValue(cell));
                }
                data.add(values);
            } else {
                Map<String, Object> values = new LinkedHashMap<String, Object>();
                for (int column = 0; column < row.size(); column++) {
                    values.put(headers.get(column), this.cellValue(row.get(column)));
                }
                data.add(values);
            }
        }
        return data;
    }

    private Object cellValue(Object cell) {
        if (cell instanceof PrettyTable) {
            PrettyTable nested = (PrettyTable) cell;
            if (nested.getDefaultOutput() == OutputFormat.YAML) {
                return this.toData(nested);
            }
            return nested.toString();
        }
        if (cell instanceof Integer) {
            return cell;
        }
        String text = cell.toString();
        try {
            return Integer.valueOf(text);
        } catch (NumberFormatException ex) {
            return text;
        }
    }

    @Override
    public String render(PrettyTable table, boolean removeRedundant) {
        try {
            return this.yamlMapper.writeValueAsString(this.toData(table));
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to render table as YAML", ex);
        }
    }
}
