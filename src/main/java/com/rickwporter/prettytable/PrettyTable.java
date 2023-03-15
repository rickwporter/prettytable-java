package com.rickwporter.prettytable;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class PrettyTable {
    private static final String HTML_TABLE_TAG = "table";
    private static final String HTML_HEADER_TAG = "thead";
    private static final String HTML_BODY_TAG = "tbody";
    private static final String HTML_ROW_TAG = "tr";
    private static final String HTML_CELL_BODY_TAG = "td";
    private static final String HTML_CELL_HEADER_TAG = "th";

    public enum CellFormat {
        CENTER,
        LEFT,
        RIGHT,
    }

    public enum OutputFormat {
        TEXT,
        CSV,
        HTML,
        JSON,
    }

    private List<String> headers = new ArrayList<>();
    private List<List<Object>> rows = new ArrayList<>();
    private List<CellFormat> formats = new ArrayList<>();
    private CellFormat defaultFormat = CellFormat.CENTER;

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

    public String getCell(int rowIndex, int columnIndex) {
        List<Object> row = this.getRow(rowIndex);
        if (row == null || row.size() <= columnIndex) {
            return null;
        }
        return row.get(columnIndex).toString();
    }

    public void setFormat(int column, CellFormat fmt) {
        for (int cIdx = this.formats.size(); cIdx <= column; cIdx++) {
            this.formats.add(this.defaultFormat);
        }
        this.formats.set(column, fmt);
    }

    public void setFormats(CellFormat... fmts) {
        this.formats.clear();
        this.formats.addAll(Arrays.asList(fmts));
    }

    private CellFormat getFormat(int column) {
        if (this.formats.size() <= column) {
            return this.defaultFormat;
        }
        return this.formats.get(column);
    }

    public void removeRedundant() {
        // Remove duplicate data from rows for more aesthetically pleasing output for text
        if (this.rows.size() < 2) {
            return;
        }
        List<Object> lastRow = new ArrayList<Object>(this.rows.get(0));
        int lastChanged = 0;
        for (int rIdx = 1; rIdx < this.rows.size(); rIdx++) {
            List<Object> row = this.rows.get(rIdx);
            List<Object> rowCopy = new ArrayList<Object>(row);
            int changed = 0;
            for (int cIdx = 0; cIdx < row.size(); cIdx++) {
                if (!row.get(cIdx).equals(lastRow.get(cIdx))) {
                    break;
                }
                row.set(cIdx, "");
                // blank out current cell
                changed += 1;
            }
            if (changed == 0 || changed <= lastChanged) {
                lastRow = rowCopy;
            }
            lastChanged = changed;
        }
    }

    private int getMaxSizeForColumn(int column) {
        int maxSize = column >= this.headers.size() ? 0 : this.headers.get(column).length();
        for (List<Object> row : this.rows) {
            int rowSize = row.get(column).toString().length();
            if (rowSize > maxSize) {
                maxSize = rowSize;
            }
        }
        return maxSize;
    }

    private int getMaxColumns() {
        // get the  # of columns from the first row when there are no headers
        return this.headers.isEmpty() ? this.rows.get(0).size() : this.headers.size();
    }

    private List<Integer> getMaxSizes() {
        List<Integer> maxSizes = new ArrayList<>();
        for (int cIdx = 0; cIdx < this.getMaxColumns(); cIdx++) {
            maxSizes.add(getMaxSizeForColumn(cIdx));
        }
        return maxSizes;
    }

    private String textRow(List<? extends Object> row, List<Integer> maxSizes) {
        StringBuilder result = new StringBuilder();
        result.append("|");
        for (int cIdx = 0; cIdx < row.size(); cIdx++) {
            String cValue = row.get(cIdx).toString();
            Integer cSize = maxSizes.get(cIdx);
            switch (getFormat(cIdx)) {
            case LEFT:
                result.append(" " + StringUtils.rightPad(cValue, cSize) + " ");
                break;
            case RIGHT:
                result.append(" " + StringUtils.leftPad(cValue, cSize) + " ");
                break;
            case CENTER:
                result.append(StringUtils.center(cValue, cSize + 2));
                break;
            }
            result.append("|");
        }
        result.append("\n");
        return result.toString();
    }

    private String textRule(List<Integer> maxSizes) {
        StringBuilder result = new StringBuilder();
        result.append("+");
        for (int i = 0; i < this.getMaxColumns(); i++) {
            for (int j = 0; j < maxSizes.get(i) + 2; j++) {
                result.append("-");
            }
            result.append("+");
        }
        result.append("\n");
        return result.toString();
    }

    String toText() {
        List<Integer> maxSizes = getMaxSizes();
        StringBuilder result = new StringBuilder();
        if (!this.headers.isEmpty()) {
            result.append(textRule(maxSizes));
            result.append(textRow(this.headers, maxSizes));
        }
        result.append(textRule(maxSizes));
        for (List<Object> row : this.rows) {
            result.append(textRow(row, maxSizes));
        }
        result.append(textRule(maxSizes));
        return result.toString();
    }

    String csvEncode(String orig) {
        if (!orig.contains(",")) {
            return orig;
        }
        // put the original string in quotes
        return "\"" + orig + "\"";
    }

    String csvRow(List<? extends Object> row) {
        return StringUtils.join(
            row.stream().map(c -> csvEncode(c.toString())).collect(Collectors.toList()), ","
        ) + "\n";
    }

    String toCsv() {
        StringBuilder result = new StringBuilder();
        if (!this.headers.isEmpty()) {
            result.append(csvRow(this.headers));
        }
        for (List<Object> row: this.rows) {
            result.append(csvRow(row));
        }
        return result.toString();
    }

    String htmlRow(List<? extends Object> row, String initIndent, String indent, String cellTag) {
        // TODO: HTML formatting
        String result = String.format("%s<%s>\n", initIndent, HTML_ROW_TAG);
        for (Object col : row) {
            result += String.format(
                "%s%s<%s>%s</%s>\n", initIndent, indent, cellTag, col.toString(), cellTag
            );
        }
        result += String.format("%s</%s>\n", initIndent, HTML_ROW_TAG);
        return result;
    }

    String toHtml() {
        StringBuilder result = new StringBuilder();
        String indent = "    ";
        result.append(String.format("<%s>\n", HTML_TABLE_TAG));
        if (!this.headers.isEmpty()) {
            result.append(String.format("%s<%s>\n", indent, HTML_HEADER_TAG));
            result.append(htmlRow(this.headers, indent + indent, indent, HTML_CELL_HEADER_TAG));
            result.append(String.format("%s</%s>\n", indent, HTML_HEADER_TAG));
        }
        result.append(String.format("%s<%s>\n", indent, HTML_BODY_TAG));
        for (List<Object> row : this.rows) {
            result.append(htmlRow(row, indent + indent, indent, HTML_CELL_BODY_TAG));
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
        switch (format) {
        case TEXT:
            this.removeRedundant();
            return this.toText();
        case CSV:
            return this.toCsv();
        case JSON:
            return this.toJson();
        case HTML:
            return this.toHtml();
        }
        return String.format("Unhandled format=%s", format);
    }
}
