package rickwporter.prettytable;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public final class PrettyTable {
    private static final String HTML_TABLE_TAG = "table";
    private static final String HTML_HEADER_TAG = "thead";
    private static final String HTML_BODY_TAG = "tbody";
    private static final String HTML_ROW_TAG = "tr";
    private static final String HTML_CELL_BODY_TAG = "td";
    private static final String HTML_CELL_HEADER_TAG = "th";
    private static final Pattern NEWLINE = Pattern.compile("\\R");

    public enum HorizontalAlign {
        CENTER("text-align:center"),
        LEFT("text-align:left"),
        RIGHT("text-align:right");

        private final String htmlStyle;
        HorizontalAlign(String html) {
            this.htmlStyle = html;
        }
        public String getHtmlStyle() {
            return this.htmlStyle;
        }
    }

    public enum OutputFormat {
        TEXT,
        CSV,
        HTML,
        JSON,
    }

    private List<String> headers = new ArrayList<>();
    private List<List<Object>> rows = new ArrayList<>();
    private List<HorizontalAlign> hAligns = new ArrayList<>();
    private HorizontalAlign defaultHorizontal = HorizontalAlign.CENTER;
    private OutputFormat defaultOutput = OutputFormat.TEXT;

    public PrettyTable(String... hdrs) {
        this.headers.addAll(Arrays.asList(hdrs));
    }

     public void addRow(Object... row) {
        this.rows.add(Arrays.asList(row));
    }

    public int getRowCount() {
        return this.rows.size();
    }

    public List<Object> getRow(int rowIndex) {
        if (this.rows.size() <= rowIndex) {
            return null;
        }
        return this.rows.get(rowIndex);
    }

    public Object getCell(int rowIndex, int columnIndex) {
        List<Object> row = this.getRow(rowIndex);
        if (row == null || row.size() <= columnIndex) {
            return null;
        }
        return row.get(columnIndex);
    }

    public void setHorizAlign(int column, HorizontalAlign fmt) {
        for (int cIdx = this.hAligns.size(); cIdx <= column; cIdx++) {
            this.hAligns.add(this.defaultHorizontal);
        }
        this.hAligns.set(column, fmt);
    }

    public void setHorizAligns(HorizontalAlign... fmts) {
        this.hAligns.clear();
        this.hAligns.addAll(Arrays.asList(fmts));
    }

    private HorizontalAlign getHorizAlign(int column) {
        if (this.hAligns.size() <= column) {
            return this.defaultHorizontal;
        }
        return this.hAligns.get(column);
    }

    public void setOutputFormat(OutputFormat format) {
        this.defaultOutput = format;
    }

    private int getMaxWidthForColumn(int column) {
        int maxWidth = column >= this.headers.size() ? 0 : this.headers.get(column).length();
        for (List<Object> row : this.rows) {
            String cellText = row.get(column).toString();
            List<String> cellLines = Arrays.asList(NEWLINE.split(cellText));
            int cellWidth = cellLines.stream().map(String::length).max(Integer::compare).get();
            if (cellWidth > maxWidth) {
                maxWidth = cellWidth;
            }
        }
        return maxWidth;
    }

    private void sortRows(List<Integer> indices) {
        Collections.sort(this.rows, (row1, row2) -> {
            for (Integer idx : indices) {
                String s1 = row1.get(idx).toString();
                String s2 = row2.get(idx).toString();
                int value = 0;
                try {
                    Float f1 = Float.parseFloat(s1);
                    Float f2 = Float.parseFloat(s2);
                    value = f1.compareTo(f2);
                } catch (NumberFormatException ex) {
                    value = s1.compareTo(s2);
                }
                if (value != 0) {
                    return value;
                }
            }
            return 0;
        });
    }

    public void sortByHeader(String... order) {
        List<Integer> indices = new ArrayList<>();
        for (String hdr : order) {
            indices.add(this.headers.indexOf(hdr));
        }
        this.sortRows(indices);
    }

    public void sortByIndex(Integer... indices) {
        this.sortRows(Arrays.asList(indices));
    }

    private int getMaxColumns() {
        // get the  # of columns from the first row when there are no headers
        return this.headers.isEmpty() ? this.rows.get(0).size() : this.headers.size();
    }

    private List<Integer> getMaxWidths() {
        List<Integer> maxWidths = new ArrayList<>();
        for (int cIdx = 0; cIdx < this.getMaxColumns(); cIdx++) {
            maxWidths.add(getMaxWidthForColumn(cIdx));
        }
        return maxWidths;
    }

    private String textRow(List<? extends Object> row, List<Integer> maxWidths) {
        StringBuilder result = new StringBuilder();
        List<List<String>> splits = row.stream()
            .map(c -> Arrays.asList(NEWLINE.split(c.toString())))
            .collect(Collectors.toList());
        int maxLines = splits.stream().map(c -> c.size()).max(Integer::compare).get();
        for (int idx = 0; idx < maxLines; idx++) {
            List<String> line = new ArrayList<>();
            for (List<String> column : splits) {
                line.add(idx < column.size() ? column.get(idx) : "");
            }
            result.append(this.textRowLine(line, maxWidths));
        }
        return result.toString();
    }

    private String textRowLine(List<String> row, List<Integer> maxWidths) {
        StringBuilder result = new StringBuilder();
        result.append("|");
        for (int cIdx = 0; cIdx < row.size(); cIdx++) {
            String cValue = row.get(cIdx);
            Integer cWidth = maxWidths.get(cIdx);
            switch (getHorizAlign(cIdx)) {
            case LEFT:
                result.append(" " + StringUtils.rightPad(cValue, cWidth) + " ");
                break;
            case RIGHT:
                result.append(" " + StringUtils.leftPad(cValue, cWidth) + " ");
                break;
            case CENTER:
                result.append(StringUtils.center(cValue, cWidth + 2));
                break;
            }
            result.append("|");
        }
        result.append("\n");
        return result.toString();
    }

    private String textRule(List<Integer> maxWidths) {
        StringBuilder result = new StringBuilder();
        result.append("+");
        for (int i = 0; i < this.getMaxColumns(); i++) {
            for (int j = 0; j < maxWidths.get(i) + 2; j++) {
                result.append("-");
            }
            result.append("+");
        }
        result.append("\n");
        return result.toString();
    }

    String toText(boolean removeRedundant) {
        List<Integer> maxWidths = getMaxWidths();
        StringBuilder result = new StringBuilder();
        if (!this.headers.isEmpty()) {
            result.append(textRule(maxWidths));
            result.append(textRow(this.headers, maxWidths));
        }
        result.append(textRule(maxWidths));
        List<String> lastRow = new ArrayList<>();
        for (List<Object> row : this.rows) {
            List<String> currentRow = row.stream().map(c -> c.toString()).collect(Collectors.toList());
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
            result.append(textRow(currentRow, maxWidths));
        }
        result.append(textRule(maxWidths));
        return result.toString();
    }

    String toCsv() {
        try {
            StringBuilder result = new StringBuilder();
            CSVFormat format = CSVFormat.DEFAULT.builder().setRecordSeparator("\n").build();
            CSVPrinter printer = new CSVPrinter(result, format);
            if (!this.headers.isEmpty()) {
                printer.printRecord(this.headers);
            }
            for (List<? extends Object> row : this.rows) {
                List<String> current = row.stream().map(c -> c.toString()).collect(Collectors.toList());
                printer.printRecord(current);
            }
            printer.close();
            return result.toString();
        } catch (IOException ex) {
            // nothing to do here
        }
        return null;
    }

    String htmlColumnFormat(Integer column) {
        if (this.hAligns.isEmpty()) {
            return "";
        }
        return " style=\"" + this.getHorizAlign(column).getHtmlStyle() + "\"";
    }

    String htmlRow(List<? extends Object> row, String initIndent, String indent, String cellTag) {
        String result = String.format("%s<%s>\n", initIndent, HTML_ROW_TAG);
        for (int column = 0; column < row.size(); column++) {
            Object obj = row.get(column);
            String cellFormat = htmlColumnFormat(column);
            result += String.format(
                "%s%s<%s%s>%s</%s>\n", initIndent, indent, cellTag, cellFormat, obj.toString(), cellTag
            );
        }
        result += String.format("%s</%s>\n", initIndent, HTML_ROW_TAG);
        return result;
    }

    String toHtml(boolean removeRedundant) {
        StringBuilder result = new StringBuilder();
        String indent = "    ";
        result.append(String.format("<%s>\n", HTML_TABLE_TAG));
        if (!this.headers.isEmpty()) {
            result.append(String.format("%s<%s>\n", indent, HTML_HEADER_TAG));
            result.append(htmlRow(this.headers, indent + indent, indent, HTML_CELL_HEADER_TAG));
            result.append(String.format("%s</%s>\n", indent, HTML_HEADER_TAG));
        }
        result.append(String.format("%s<%s>\n", indent, HTML_BODY_TAG));
        List<String> lastRow = new ArrayList<>();
        for (List<Object> row : this.rows) {
            List<String> currentRow = row.stream().map(c -> c.toString()).collect(Collectors.toList());
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
            result.append(htmlRow(currentRow, indent + indent, indent, HTML_CELL_BODY_TAG));
        }
        result.append(String.format("%s</%s>\n", indent, HTML_BODY_TAG));
        result.append(String.format("</%s>\n", HTML_TABLE_TAG));
        return result.toString();
    }

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
        return String.format("\"%s\"", object.toString());
    }

    String jsonRow(List<Object> row, String initIndent, String indent) {
        List<String> rowValues = new ArrayList<>();
        for (int cIdx = 0; cIdx < row.size(); cIdx++) {
            if (!this.headers.isEmpty()) {
                rowValues.add(
                    String.format("%s%s\"%s\": %s",
                        initIndent,
                        indent,
                        this.headers.get(cIdx),
                        jsonEncode(row.get(cIdx)))
                );
            } else {
                rowValues.add(
                    String.format("%s%s%s", initIndent, indent, jsonEncode(row.get(cIdx)))
                );
            }
        }
        return StringUtils.join(rowValues, ",\n");
    }

    String toJson() {
        StringBuilder result = new StringBuilder();
        String indent = "    ";
        result.append("[\n");
        String entryPrefix = "";
        if (!this.headers.isEmpty()) {
          result.append(String.format("%s[\n", indent));
            List<String> headerValues = this.headers.stream()
                .map(h -> String.format("%s%s\"%s\"", indent, indent, h))
                .collect(Collectors.toList());
            result.append(StringUtils.join(headerValues, ",\n") + "\n");
            result.append(String.format("%s]", indent));
            entryPrefix = ",\n";
        }
        String entryStart = this.headers.isEmpty() ? "[" : "{";
        String entryEnd = this.headers.isEmpty() ? "]" : "}";
        for (List<Object> row : this.rows) {
            result.append(String.format("%s%s%s\n", entryPrefix, indent, entryStart));
            result.append(jsonRow(row, indent, indent) + "\n");
            result.append(String.format("%s%s", indent, entryEnd));
            entryPrefix = ",\n";
        }
        result.append("\n]\n");
        return result.toString();
    }

    public String formattedString(OutputFormat format) {
        return this.formattedString(format, true);
    }

    public String formattedString(OutputFormat format, boolean removeRedundant) {
        switch (format) {
        case TEXT:
            return this.toText(removeRedundant);
        case CSV:
            return this.toCsv();
        case JSON:
            return this.toJson();
        case HTML:
            return this.toHtml(removeRedundant);
        }
        return String.format("Unhandled format=%s", format);
    }

    public String toString() {
        return this.formattedString(this.defaultOutput);
    }
}
