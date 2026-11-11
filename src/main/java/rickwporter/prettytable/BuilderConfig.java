package rickwporter.prettytable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Labels, truncation limits, and column selection for {@link TableBuilder} and {@link DisplayBuilder}.
 * Each setter returns this config so calls can be chained. String setters reject {@code null}.
 * List setters store a copy of the given list.
 */
public final class BuilderConfig {
    static final int DEFAULT_KEY_MAX_LEN = 35;
    static final int DEFAULT_VALUE_MAX_LEN = 50;
    static final int DEFAULT_URL_MAX_LEN = 100;

    private String itemsLabel = "Items";
    private String propertyLabel = "Property";
    private String propertiesLabel = "Properties";
    private String valueLabel = "Value";
    private String valuesLabel = "Values";
    private String unknownLabel = "Unknown";
    private String nullValue = "None";
    private String trueValue = "True";
    private String falseValue = "False";
    private String itemsCaption = "Found {} items";
    private String ellipsis = "...";
    private String wildcardHeader = "Properties";
    private List<String> urlPrefixes = new ArrayList<String>(Arrays.asList("http://", "https://", "ftp://"));
    private int urlMaxLen = DEFAULT_URL_MAX_LEN;
    private List<String> keyFields = new ArrayList<String>(Arrays.asList("name", "id"));
    private int keyMaxLen = DEFAULT_KEY_MAX_LEN;
    private int valueMaxLen = DEFAULT_VALUE_MAX_LEN;
    private OutputFormat format = OutputFormat.RICH;
    private List<String> columns = null;

    /**
     * Creates a config with the default labels, limits, and format.
     */
    public BuilderConfig() {
    }

    /**
     * Returns the header for a list of strings, numbers, or booleans.
     *
     * @return the header, {@code Items} by default
     */
    public String getItemsLabel() {
        return this.itemsLabel;
    }

    /**
     * Sets the header for a list of strings, numbers, or booleans.
     *
     * @param label column header
     * @return this config
     * @throws NullPointerException if {@code label} is null
     */
    public BuilderConfig setItemsLabel(String label) {
        this.itemsLabel = Objects.requireNonNull(label);
        return this;
    }

    /**
     * Returns the left header of a map table.
     *
     * @return the header, {@code Property} by default
     */
    public String getPropertyLabel() {
        return this.propertyLabel;
    }

    /**
     * Sets the left header of a map table.
     *
     * @param label column header
     * @return this config
     * @throws NullPointerException if {@code label} is null
     */
    public BuilderConfig setPropertyLabel(String label) {
        this.propertyLabel = Objects.requireNonNull(label);
        return this;
    }

    /**
     * Returns the header used for the remaining fields of a named list item.
     *
     * @return the header, {@code Properties} by default
     */
    public String getPropertiesLabel() {
        return this.propertiesLabel;
    }

    /**
     * Sets the header used for the remaining fields of a named list item.
     *
     * @param label column header
     * @return this config
     * @throws NullPointerException if {@code label} is null
     */
    public BuilderConfig setPropertiesLabel(String label) {
        this.propertiesLabel = Objects.requireNonNull(label);
        return this;
    }

    /**
     * Returns the right header of a map table.
     *
     * @return the header, {@code Value} by default
     */
    public String getValueLabel() {
        return this.valueLabel;
    }

    /**
     * Sets the right header of a map table.
     *
     * @param label column header
     * @return this config
     * @throws NullPointerException if {@code label} is null
     */
    public BuilderConfig setValueLabel(String label) {
        this.valueLabel = Objects.requireNonNull(label);
        return this;
    }

    /**
     * Returns the header used when list items have no name field.
     *
     * @return the header, {@code Values} by default
     */
    public String getValuesLabel() {
        return this.valuesLabel;
    }

    /**
     * Sets the header used when list items have no name field.
     *
     * @param label column header
     * @return this config
     * @throws NullPointerException if {@code label} is null
     */
    public BuilderConfig setValuesLabel(String label) {
        this.valuesLabel = Objects.requireNonNull(label);
        return this;
    }

    /**
     * Returns the text used when a later row has no name field.
     *
     * @return the label, {@code Unknown} by default
     */
    public String getUnknownLabel() {
        return this.unknownLabel;
    }

    /**
     * Sets the text used when a later row has no name field.
     *
     * @param label replacement text
     * @return this config
     * @throws NullPointerException if {@code label} is null
     */
    public BuilderConfig setUnknownLabel(String label) {
        this.unknownLabel = Objects.requireNonNull(label);
        return this;
    }

    /**
     * Returns the text used for a null value.
     *
     * @return the text, {@code None} by default
     */
    public String getNullValue() {
        return this.nullValue;
    }

    /**
     * Sets the text used for a null value.
     *
     * @param text replacement text
     * @return this config
     * @throws NullPointerException if {@code text} is null
     */
    public BuilderConfig setNullValue(String text) {
        this.nullValue = Objects.requireNonNull(text);
        return this;
    }

    /**
     * Returns the text used for {@code true}.
     *
     * @return the text, {@code True} by default
     */
    public String getTrueValue() {
        return this.trueValue;
    }

    /**
     * Sets the text used for {@code true}.
     *
     * @param text replacement text
     * @return this config
     * @throws NullPointerException if {@code text} is null
     */
    public BuilderConfig setTrueValue(String text) {
        this.trueValue = Objects.requireNonNull(text);
        return this;
    }

    /**
     * Returns the text used for {@code false}.
     *
     * @return the text, {@code False} by default
     */
    public String getFalseValue() {
        return this.falseValue;
    }

    /**
     * Sets the text used for {@code false}.
     *
     * @param text replacement text
     * @return this config
     * @throws NullPointerException if {@code text} is null
     */
    public BuilderConfig setFalseValue(String text) {
        this.falseValue = Objects.requireNonNull(text);
        return this;
    }

    /**
     * Returns the caption template for a list table.
     * <code>{}</code>, {@code %d}, and {@code %s} are replaced with the item count.
     *
     * @return the template, <code>Found {} items</code> by default
     */
    public String getItemsCaption() {
        return this.itemsCaption;
    }

    /**
     * Sets the caption template for a list table.
     * <code>{}</code>, {@code %d}, and {@code %s} are replaced with the item count. A template with
     * none of those markers is printed unchanged.
     *
     * @param template caption template
     * @return this config
     * @throws NullPointerException if {@code template} is null
     */
    public BuilderConfig setItemsCaption(String template) {
        this.itemsCaption = Objects.requireNonNull(template);
        return this;
    }

    /**
     * Returns the marker appended when a key, value, or URL is cut off.
     *
     * @return the marker, {@code ...} by default
     */
    public String getEllipsis() {
        return this.ellipsis;
    }

    /**
     * Sets the marker appended when a key, value, or URL is cut off.
     *
     * @param marker truncation marker
     * @return this config
     * @throws NullPointerException if {@code marker} is null
     */
    public BuilderConfig setEllipsis(String marker) {
        this.ellipsis = Objects.requireNonNull(marker);
        return this;
    }

    /**
     * Returns the header used for a {@code *} column.
     *
     * @return the header, {@code Properties} by default
     */
    public String getWildcardHeader() {
        return this.wildcardHeader;
    }

    /**
     * Sets the header used for a {@code *} column.
     *
     * @param header column header
     * @return this config
     * @throws NullPointerException if {@code header} is null
     */
    public BuilderConfig setWildcardHeader(String header) {
        this.wildcardHeader = Objects.requireNonNull(header);
        return this;
    }

    /**
     * Returns the prefixes that mark a value as a URL.
     * URL values use {@link #getUrlMaxLen()} instead of {@link #getValueMaxLen()}.
     *
     * @return the prefixes, {@code http://}, {@code https://}, and {@code ftp://} by default
     */
    public List<String> getUrlPrefixes() {
        return this.urlPrefixes;
    }

    /**
     * Sets the prefixes that mark a value as a URL.
     *
     * @param prefixes prefixes to match at the start of a value
     * @return this config
     * @throws NullPointerException if {@code prefixes} is null
     */
    public BuilderConfig setUrlPrefixes(List<String> prefixes) {
        this.urlPrefixes = new ArrayList<String>(Objects.requireNonNull(prefixes));
        return this;
    }

    /**
     * Returns the maximum length of a URL value before the ellipsis is appended.
     *
     * @return the maximum length, {@code 100} by default
     */
    public int getUrlMaxLen() {
        return this.urlMaxLen;
    }

    /**
     * Sets the maximum length of a URL value before the ellipsis is appended.
     *
     * @param maxLen maximum length in characters
     * @return this config
     */
    public BuilderConfig setUrlMaxLen(int maxLen) {
        this.urlMaxLen = maxLen;
        return this;
    }

    /**
     * Returns the fields tried, in order, as the row name of a list of maps.
     *
     * @return the field names, {@code name} then {@code id} by default
     */
    public List<String> getKeyFields() {
        return this.keyFields;
    }

    /**
     * Sets the fields tried, in order, as the row name of a list of maps.
     *
     * @param fields field names, most preferred first
     * @return this config
     * @throws NullPointerException if {@code fields} is null
     */
    public BuilderConfig setKeyFields(List<String> fields) {
        this.keyFields = new ArrayList<String>(Objects.requireNonNull(fields));
        return this;
    }

    /**
     * Returns the maximum length of a key before the ellipsis is appended.
     *
     * @return the maximum length, {@code 35} by default
     */
    public int getKeyMaxLen() {
        return this.keyMaxLen;
    }

    /**
     * Sets the maximum length of a key before the ellipsis is appended.
     *
     * @param maxLen maximum length in characters
     * @return this config
     */
    public BuilderConfig setKeyMaxLen(int maxLen) {
        this.keyMaxLen = maxLen;
        return this;
    }

    /**
     * Returns the maximum length of a non-URL value before the ellipsis is appended.
     *
     * @return the maximum length, {@code 50} by default
     */
    public int getValueMaxLen() {
        return this.valueMaxLen;
    }

    /**
     * Sets the maximum length of a non-URL value before the ellipsis is appended.
     *
     * @param maxLen maximum length in characters
     * @return this config
     */
    public BuilderConfig setValueMaxLen(int maxLen) {
        this.valueMaxLen = maxLen;
        return this;
    }

    /**
     * Returns the format stored on tables this config builds.
     *
     * @return the format, {@link OutputFormat#RICH} by default
     */
    public OutputFormat getFormat() {
        return this.format;
    }

    /**
     * Sets the format stored on tables this config builds.
     *
     * @param outputFormat output format
     * @return this config
     * @throws NullPointerException if {@code outputFormat} is null
     */
    public BuilderConfig setFormat(OutputFormat outputFormat) {
        this.format = Objects.requireNonNull(outputFormat);
        return this;
    }

    /**
     * Returns the columns selected for a list of maps.
     * A {@code null} or empty selection leaves the default list layout in place.
     *
     * @return the column names, or {@code null} when none are selected
     */
    public List<String> getColumns() {
        return this.columns;
    }

    /**
     * Selects columns for a list of maps.
     * A {@code *} entry collects every field that is not named in {@code columnNames}, under
     * {@link #getWildcardHeader()}. {@code null} or an empty list leaves the default list layout
     * in place. Column selection does not apply to a map.
     *
     * @param columnNames column names in display order, or {@code null}
     * @return this config
     */
    public BuilderConfig setColumns(List<String> columnNames) {
        if (columnNames == null) {
            this.columns = null;
            return this;
        }
        this.columns = new ArrayList<String>(columnNames);
        return this;
    }
}
