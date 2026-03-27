package rickwporter.prettytable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

import rickwporter.prettytable.PrettyTable.OutputFormat;

class JsonTableRender implements TableRenderInterface {
    String jsonEncode(Object object) {
        if (object instanceof Integer) {
            return object.toString();
        }
        try {
            Integer value = Integer.parseInt(object.toString());
            return value.toString();
        } catch (NumberFormatException ex) {
            // nothing to do here, just double-quote as below
        }
        return String.format(
            "\"%s\"",
            object.toString()
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\"", "\\\"")
            );
    }

    String jsonValue(Object cellObj, String initIndent, String indent) {
        if (cellObj instanceof PrettyTable) {
            PrettyTable t = (PrettyTable) cellObj;
            if (t.getDefaultOutput() == OutputFormat.JSON) {
                String out = t.toJson().replace("\n", "\n" + initIndent);
                return out.substring(0, out.lastIndexOf("\n" + initIndent));
            }
        }
        return jsonEncode(cellObj);
    }

    String jsonRow(List<Object> row, String initIndent, String indent, List<String> headers) {
        List<String> rowValues = new ArrayList<>();
        for (int cIdx = 0; cIdx < row.size(); cIdx++) {
            String value = this.jsonValue(row.get(cIdx), initIndent + indent, indent);
            if (!headers.isEmpty()) {
                rowValues.add(
                    String.format("%s%s\"%s\": %s",
                        initIndent,
                        indent,
                        headers.get(cIdx),
                        value)
                );
            } else {
                rowValues.add(
                    String.format("%s%s%s", initIndent, indent, value)
                );
            }
        }
        return StringUtils.join(rowValues, ",\n");
    }

    @Override
    public String render(PrettyTable table, boolean removeRedundant) {
        StringBuilder result = new StringBuilder();
        String indent = "    ";
        result.append("[\n");
        String entryPrefix = "";
        List<String> headers = table.getHeaders();
        if (!headers.isEmpty()) {
            result.append(String.format("%s[\n", indent));
            List<String> headerValues = headers.stream()
                .map(h -> String.format("%s%s\"%s\"", indent, indent, h))
                .collect(Collectors.toList());
            result.append(StringUtils.join(headerValues, ",\n") + "\n");
            result.append(String.format("%s]", indent));
            entryPrefix = ",\n";
        }
        String entryStart = table.getHeaders().isEmpty() ? "[" : "{";
        String entryEnd = table.getHeaders().isEmpty() ? "]" : "}";
        for (List<Object> row : table.getRows()) {
            result.append(String.format("%s%s%s\n", entryPrefix, indent, entryStart));
            result.append(jsonRow(row, indent, indent, headers) + "\n");
            result.append(String.format("%s%s", indent, entryEnd));
            entryPrefix = ",\n";
        }
        result.append("\n]\n");
        return result.toString();
    }
}
