package rickwporter.prettytable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;


/**
 * A table of cells that can be rendered as text, rich text, CSV, HTML, JSON, or YAML.
 *
 * <p>The default format is {@link OutputFormat#TEXT}. Text and rich output show the header and
 * the border, omit rules between data rows, and clear repeated leading cells. A caption is omitted
 * until one is set. Cells are centered until an alignment is set. A cell may itself be a
 * {@code PrettyTable}, and that nested table is rendered in its own format.
 */
public final class PrettyTable {
    private static final Pattern NEWLINE = Pattern.compile("\\R");

    private List<String> headers = new ArrayList<>();
    private List<List<Object>> rows = new ArrayList<>();
    private List<HorizontalAlign> hAligns = new ArrayList<>();
    private HorizontalAlign defaultHorizontal = HorizontalAlign.CENTER;
    private OutputFormat defaultOutput = OutputFormat.TEXT;
    private boolean showHeader = true;
    private boolean showLines = true;
    private boolean rowDividers = false;
    private boolean omitRedundant = true;
    private String caption = null;

    /**
     * Creates a table with the given column headers.
     * Passing no headers leaves the table without a header row.
     *
     * @param hdrs column headers, in display order
     */
    public PrettyTable(String... hdrs) {
        this.headers.addAll(Arrays.asList(hdrs));
    }

    /**
     * Returns the column headers in display order.
     *
     * @return the headers, possibly empty
     */
    public List<String> getHeaders() {
        return this.headers;
    }

    /**
     * Appends a row.
     * A nested {@code PrettyTable} cell is rendered with that table's own format.
     *
     * @param row cell values, in column order
     */
    public void addRow(Object... row) {
        this.rows.add(Arrays.asList(row));
    }

    /**
     * Returns the number of data rows.
     *
     * @return the row count
     */
    public int getRowCount() {
        return this.rows.size();
    }

    /**
     * Returns the cells in one data row.
     *
     * @param rowIndex zero-based row index
     * @return the row, or {@code null} when {@code rowIndex} is past the last row
     * @throws IndexOutOfBoundsException if {@code rowIndex} is negative
     */
    public List<Object> getRow(int rowIndex) {
        if (this.rows.size() <= rowIndex) {
            return null;
        }
        return this.rows.get(rowIndex);
    }

    /**
     * Returns the data rows in display order.
     *
     * @return the rows, possibly empty
     */
    public List<List<Object>> getRows() {
        return this.rows;
    }

    /**
     * Returns one cell.
     *
     * @param rowIndex zero-based row index
     * @param columnIndex zero-based column index
     * @return the cell, or {@code null} when either index is past the end of the table
     * @throws IndexOutOfBoundsException if {@code rowIndex} is negative, or if the row exists and
     *     {@code columnIndex} is negative
     */
    public Object getCell(int rowIndex, int columnIndex) {
        List<Object> row = this.getRow(rowIndex);
        if (row == null || row.size() <= columnIndex) {
            return null;
        }
        return row.get(columnIndex);
    }

    /**
     * Returns the alignment used for columns that have no explicit alignment.
     * The default is {@link HorizontalAlign#CENTER}.
     *
     * @return the default alignment
     */
    public HorizontalAlign getDefaultHorizAlign() {
        return this.defaultHorizontal;
    }

    /**
     * Sets the horizontal alignment of one column.
     * Columns before {@code column} that have no alignment keep the default.
     *
     * @param column zero-based column index
     * @param fmt alignment for that column
     */
    public void setHorizAlign(int column, HorizontalAlign fmt) {
        for (int cIdx = this.hAligns.size(); cIdx <= column; cIdx++) {
            this.hAligns.add(this.defaultHorizontal);
        }
        this.hAligns.set(column, fmt);
    }

    /**
     * Replaces the per-column alignments.
     * Columns past the end of {@code fmts} use {@link #getDefaultHorizAlign()}.
     *
     * @param fmts alignments, in column order
     */
    public void setHorizAligns(HorizontalAlign... fmts) {
        this.hAligns.clear();
        this.hAligns.addAll(Arrays.asList(fmts));
    }

    /**
     * Returns the per-column alignments that have been set.
     *
     * @return the alignments, possibly empty
     */
    public List<HorizontalAlign> getHorizAligns() {
        return this.hAligns;
    }

    /**
     * Sets the format used by {@link #toString()}.
     * The default is {@link OutputFormat#TEXT}.
     *
     * @param format output format
     */
    public void setOutputFormat(OutputFormat format) {
        this.defaultOutput = format;
    }

    /**
     * Returns the format used by {@link #toString()}.
     *
     * @return the output format
     */
    public OutputFormat getDefaultOutput() {
        return this.defaultOutput;
    }

    /**
     * Shows or hides the header row in text and rich output.
     * Hidden headers are also left out of the column-width calculation. The default is to show
     * the header. CSV, HTML, JSON, and YAML ignore this setting.
     *
     * @param visible {@code true} to include the header row
     */
    public void setShowHeader(boolean visible) {
        this.showHeader = visible;
    }

    /**
     * Returns whether text and rich output include the header row.
     *
     * @return {@code true} when the header is shown
     */
    public boolean isShowHeader() {
        return this.showHeader;
    }

    /**
     * Shows or hides the border in text and rich output.
     * Hiding the border also hides row dividers. Cell padding and alignment remain. The default
     * is to show the border. CSV, HTML, JSON, and YAML ignore this setting.
     *
     * @param visible {@code true} to draw rules and vertical borders
     */
    public void setShowLines(boolean visible) {
        this.showLines = visible;
    }

    /**
     * Returns whether text and rich output draw the border.
     *
     * @return {@code true} when the border is shown
     */
    public boolean isShowLines() {
        return this.showLines;
    }

    /**
     * Draws or omits a rule under data rows in text and rich output.
     * A row that shares a leading prefix with the row above has no rule under it while redundant
     * cells are being removed. {@link #setShowLines(boolean) Hiding the border} hides these rules
     * as well. The default is no row dividers. CSV, HTML, JSON, and YAML ignore this setting.
     *
     * @param visible {@code true} to draw a rule under data rows
     */
    public void setRowDividers(boolean visible) {
        this.rowDividers = visible;
    }

    /**
     * Returns whether text and rich output draw a rule under data rows.
     *
     * @return {@code true} when row dividers are drawn
     */
    public boolean isRowDividers() {
        return this.rowDividers;
    }

    /**
     * Clears or keeps repeated leading cells in text, rich, and HTML output.
     * The default is to clear them. {@link #formattedString(OutputFormat)} uses this setting.
     * {@link #formattedString(OutputFormat, boolean)} overrides it for that call. CSV, JSON, and
     * YAML keep every cell either way.
     *
     * @param remove {@code true} to clear repeated leading cells
     */
    public void setRemoveRedundant(boolean remove) {
        this.omitRedundant = remove;
    }

    /**
     * Returns whether text, rich, and HTML output clear repeated leading cells.
     *
     * @return {@code true} when repeated leading cells are cleared
     */
    public boolean isRemoveRedundant() {
        return this.omitRedundant;
    }

    /**
     * Sets the caption printed under a text or rich table.
     * A {@code null} or empty caption is skipped. Any other caption is printed unchanged, including
     * its newlines. CSV, HTML, JSON, and YAML ignore this setting.
     *
     * @param value caption text, or {@code null} to omit it
     */
    public void setCaption(String value) {
        this.caption = value;
    }

    /**
     * Returns the caption, or {@code null} when none is set.
     *
     * @return the caption
     */
    public String getCaption() {
        return this.caption;
    }

    /**
     * Returns the widest line in one column.
     * The header is included when it is shown and this column has one. Newlines split a cell into
     * separate lines.
     *
     * @param column zero-based column index
     * @return the width in characters
     */
    public int getMaxWidthForColumn(int column) {
        int maxWidth = 0;
        if (this.showHeader && column < this.headers.size()) {
            maxWidth = this.headers.get(column).length();
        }
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

    /**
     * Sorts the rows by the named headers, from the first name to the last.
     * A cell that parses as a float is compared numerically. Any other cell is compared as text.
     * Rows that compare equal keep their relative order.
     *
     * @param order header names, most significant first
     * @throws ArrayIndexOutOfBoundsException if a name is not a header
     */
    public void sortByHeader(String... order) {
        List<Integer> indices = new ArrayList<>();
        for (String hdr : order) {
            indices.add(this.headers.indexOf(hdr));
        }
        this.sortRows(indices);
    }

    /**
     * Sorts the rows by column index, from the first index to the last.
     * A cell that parses as a float is compared numerically. Any other cell is compared as text.
     * Rows that compare equal keep their relative order.
     *
     * @param indices column indexes, most significant first
     * @throws ArrayIndexOutOfBoundsException if an index is outside the row
     */
    public void sortByIndex(Integer... indices) {
        this.sortRows(Arrays.asList(indices));
    }

    /**
     * Returns the number of columns.
     * The count is the number of headers, or the length of the first row when there are no headers.
     *
     * @return the column count
     */
    public int getMaxColumns() {
        // get the  # of columns from the first row when there are no headers
        return this.headers.isEmpty() ? this.rows.get(0).size() : this.headers.size();
    }

    /**
     * Returns the widest line in each column, in column order.
     *
     * @return one width per column
     * @see #getMaxWidthForColumn(int)
     */
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

    String toRich(boolean removeRedundant) {
        return formattedString(OutputFormat.RICH, removeRedundant);
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

    String toYaml() {
        return formattedString(OutputFormat.YAML, false);
    }

    /**
     * Renders the table in {@code format}, using {@link #isRemoveRedundant()}.
     *
     * @param format output format
     * @return the rendered table
     * @throws IllegalArgumentException if {@code format} is not supported
     */
    public String formattedString(OutputFormat format) {
        return this.formattedString(format, this.omitRedundant);
    }

    /**
     * Renders the table in {@code format}.
     * {@code removeRedundant} applies to text, rich, and HTML. CSV, JSON, and YAML keep every cell.
     *
     * @param format output format
     * @param removeRedundant whether text, rich, and HTML clear repeated leading cells
     * @return the rendered table
     * @throws IllegalArgumentException if {@code format} is not supported
     */
    public String formattedString(OutputFormat format, boolean removeRedundant) {
        final TableRenderInterface renderer;
        if (format == OutputFormat.TEXT) {
            renderer = new TextTableRender();
        } else if (format == OutputFormat.RICH) {
            renderer = new RichTableRender();
        } else if (format == OutputFormat.CSV) {
            renderer = new CsvTableRender();
        } else if (format == OutputFormat.JSON) {
            renderer = new JsonTableRender();
        } else if (format == OutputFormat.YAML) {
            renderer = new YamlTableRender();
        } else if (format == OutputFormat.HTML) {
            renderer = new HtmlTableRender();
        } else {
            throw new IllegalArgumentException("Unsupported OutputFormat: " + format);
        }
        return render(renderer, removeRedundant);
    }

    /**
     * Renders the table with a caller-supplied renderer.
     *
     * @param renderer renderer to use
     * @param removeRedundant passed through to {@link TableRenderInterface#render}
     * @return the rendered table
     */
    public String render(TableRenderInterface renderer, boolean removeRedundant) {
        return renderer.render(this, removeRedundant);
    }

    /**
     * Renders the table in the format from {@link #getDefaultOutput()}.
     *
     * @return the rendered table
     */
    public String toString() {
        return this.formattedString(this.defaultOutput);
    }
}
