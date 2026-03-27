package rickwporter.prettytable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import rickwporter.prettytable.PrettyTable.HorizontalAlign;
import rickwporter.prettytable.PrettyTable.OutputFormat;

class HtmlTableRender implements TableRenderInterface {
    private static final String HTML_TABLE_TAG = "table";
    private static final String HTML_HEADER_TAG = "thead";
    private static final String HTML_BODY_TAG = "tbody";
    private static final String HTML_ROW_TAG = "tr";
    private static final String HTML_CELL_BODY_TAG = "td";
    private static final String HTML_CELL_HEADER_TAG = "th";

    String htmlColumnFormat(Integer column, List<HorizontalAlign> hAligns) {
        if (hAligns.isEmpty()) {
            return "";
        }
        return " style=\"" + hAligns.get(column).getHtmlStyle() + "\"";
    }

    String htmlRow(
        List<? extends Object> row,
        String initIndent,
        String indent,
        String cellTag,
        List<HorizontalAlign> hAligns
    ) {
        String result = String.format("%s<%s>\n", initIndent, HTML_ROW_TAG);
        for (int column = 0; column < row.size(); column++) {
            Object obj = row.get(column);
            String cellFormat = htmlColumnFormat(column, hAligns);
            result += String.format(
                "%s%s<%s%s>%s</%s>\n", initIndent, indent, cellTag, cellFormat, obj.toString(), cellTag
            );
        }
        result += String.format("%s</%s>\n", initIndent, HTML_ROW_TAG);
        return result;
    }

    @Override
    public String render(PrettyTable table, boolean removeRedundant) {
        StringBuilder result = new StringBuilder();
        String indent = "    ";
        List<HorizontalAlign> hAligns = table.getHorizAligns();

        result.append(String.format("<%s>\n", HTML_TABLE_TAG));
        if (!table.getHeaders().isEmpty()) {
            result.append(String.format("%s<%s>\n", indent, HTML_HEADER_TAG));
            result.append(htmlRow(table.getHeaders(), indent + indent, indent, HTML_CELL_HEADER_TAG, hAligns));
            result.append(String.format("%s</%s>\n", indent, HTML_HEADER_TAG));
        }

        result.append(String.format("%s<%s>\n", indent, HTML_BODY_TAG));
        List<String> lastRow = new ArrayList<>();
        for (List<Object> row : table.getRows()) {
            List<String> currentRow = row.stream()
                .map(c -> {
                    if (c instanceof PrettyTable)  {
                        PrettyTable t = (PrettyTable) c;
                        if (t.getDefaultOutput() == OutputFormat.HTML) {
                            String out = "\n" + t.toHtml(true);
                            out = out.replaceAll("\n", "\n" + indent + indent + indent + indent);
                            return out.substring(0, out.lastIndexOf(indent));
                        }
                        return t.toString().replace("\n", "<br/>");
                    }
                    return c.toString();
                })
                .collect(Collectors.toList());
            List<String> fullRow = new ArrayList<String>(currentRow);  // make a copy before manipulating
            if (removeRedundant) {
                for (int i = 0; i < lastRow.size(); i++) {
                    if (!lastRow.get(i).equals(currentRow.get(i))) {
                        break;
                    }
                    currentRow.set(i, "");
                }
                lastRow = fullRow;
            }
            result.append(htmlRow(currentRow, indent + indent, indent, HTML_CELL_BODY_TAG, hAligns));
        }
        result.append(String.format("%s</%s>\n", indent, HTML_BODY_TAG));
        result.append(String.format("</%s>\n", HTML_TABLE_TAG));
        return result.toString();
    }
}
