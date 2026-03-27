package rickwporter.prettytable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;


public final class PrettyTable {
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

    public List<String> getHeaders() {
        return this.headers;
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

    public List<List<Object>> getRows() {
        return this.rows;
    }

    public Object getCell(int rowIndex, int columnIndex) {
        List<Object> row = this.getRow(rowIndex);
        if (row == null || row.size() <= columnIndex) {
            return null;
        }
        return row.get(columnIndex);
    }

    public HorizontalAlign getDefaultHorizAlign() {
        return this.defaultHorizontal;
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

    public List<HorizontalAlign> getHorizAligns() {
        return this.hAligns;
    }

    public void setOutputFormat(OutputFormat format) {
        this.defaultOutput = format;
    }

    public OutputFormat getDefaultOutput() {
        return this.defaultOutput;
    }

    public int getMaxWidthForColumn(int column) {
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

    public int getMaxColumns() {
        // get the  # of columns from the first row when there are no headers
        return this.headers.isEmpty() ? this.rows.get(0).size() : this.headers.size();
    }

    public List<Integer> getMaxWidths() {
        List<Integer> maxWidths = new ArrayList<>();
        for (int cIdx = 0; cIdx < this.getMaxColumns(); cIdx++) {
            maxWidths.add(getMaxWidthForColumn(cIdx));
        }
        return maxWidths;
    }

    String toText(boolean removeRedundant) {
        return formattedString(OutputFormat.TEXT, removeRedundant);
    }

    String toCsv() {
        return formattedString(OutputFormat.CSV, false);
    }

    String toHtml(boolean removeRedundant) {
        return formattedString(OutputFormat.HTML, removeRedundant);
    }

    String toJson() {
        return formattedString(OutputFormat.JSON, false);
    }

    public String formattedString(OutputFormat format) {
        return this.formattedString(format, true);
    }

    public String formattedString(OutputFormat format, boolean removeRedundant) {
        TableRenderInterface renderer = null;
        switch (format) {
        case TEXT:
            renderer = new TextTableRender();
            break;
        case CSV:
            renderer = new CsvTableRender();
            break;
        case JSON:
            renderer = new JsonTableRender();
            break;
        case HTML:
            renderer = new HtmlTableRender();
            break;
        }
        return render(renderer, removeRedundant);
    }

    public String render(TableRenderInterface renderer, boolean removeRedundant) {
        return renderer.render(this, removeRedundant);
    }

    public String toString() {
        return this.formattedString(this.defaultOutput);
    }
}
