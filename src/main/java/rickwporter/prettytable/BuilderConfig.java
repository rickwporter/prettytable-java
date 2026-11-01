package rickwporter.prettytable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    private String itemsCaption = "Found {} items";
    private String ellipsis = "...";
    private String wildcardHeader = "Properties";
    private List<String> urlPrefixes = new ArrayList<String>(Arrays.asList("http://", "https://", "ftp://"));
    private int urlMaxLen = DEFAULT_URL_MAX_LEN;
    private List<String> keyFields = new ArrayList<String>(Arrays.asList("name", "id"));
    private int keyMaxLen = DEFAULT_KEY_MAX_LEN;
    private int valueMaxLen = DEFAULT_VALUE_MAX_LEN;
    private List<String> columns = null;

    public String getItemsLabel() {
        return this.itemsLabel;
    }

    public BuilderConfig setItemsLabel(String label) {
        this.itemsLabel = Objects.requireNonNull(label);
        return this;
    }

    public String getPropertyLabel() {
        return this.propertyLabel;
    }

    public BuilderConfig setPropertyLabel(String label) {
        this.propertyLabel = Objects.requireNonNull(label);
        return this;
    }

    public String getPropertiesLabel() {
        return this.propertiesLabel;
    }

    public BuilderConfig setPropertiesLabel(String label) {
        this.propertiesLabel = Objects.requireNonNull(label);
        return this;
    }

    public String getValueLabel() {
        return this.valueLabel;
    }

    public BuilderConfig setValueLabel(String label) {
        this.valueLabel = Objects.requireNonNull(label);
        return this;
    }

    public String getValuesLabel() {
        return this.valuesLabel;
    }

    public BuilderConfig setValuesLabel(String label) {
        this.valuesLabel = Objects.requireNonNull(label);
        return this;
    }

    public String getUnknownLabel() {
        return this.unknownLabel;
    }

    public BuilderConfig setUnknownLabel(String label) {
        this.unknownLabel = Objects.requireNonNull(label);
        return this;
    }

    public String getItemsCaption() {
        return this.itemsCaption;
    }

    public BuilderConfig setItemsCaption(String template) {
        this.itemsCaption = Objects.requireNonNull(template);
        return this;
    }

    public String getEllipsis() {
        return this.ellipsis;
    }

    public BuilderConfig setEllipsis(String marker) {
        this.ellipsis = Objects.requireNonNull(marker);
        return this;
    }

    public String getWildcardHeader() {
        return this.wildcardHeader;
    }

    public BuilderConfig setWildcardHeader(String header) {
        this.wildcardHeader = Objects.requireNonNull(header);
        return this;
    }

    public List<String> getUrlPrefixes() {
        return this.urlPrefixes;
    }

    public BuilderConfig setUrlPrefixes(List<String> prefixes) {
        this.urlPrefixes = new ArrayList<String>(Objects.requireNonNull(prefixes));
        return this;
    }

    public int getUrlMaxLen() {
        return this.urlMaxLen;
    }

    public BuilderConfig setUrlMaxLen(int maxLen) {
        this.urlMaxLen = maxLen;
        return this;
    }

    public List<String> getKeyFields() {
        return this.keyFields;
    }

    public BuilderConfig setKeyFields(List<String> fields) {
        this.keyFields = new ArrayList<String>(Objects.requireNonNull(fields));
        return this;
    }

    public int getKeyMaxLen() {
        return this.keyMaxLen;
    }

    public BuilderConfig setKeyMaxLen(int maxLen) {
        this.keyMaxLen = maxLen;
        return this;
    }

    public int getValueMaxLen() {
        return this.valueMaxLen;
    }

    public BuilderConfig setValueMaxLen(int maxLen) {
        this.valueMaxLen = maxLen;
        return this;
    }

    public List<String> getColumns() {
        return this.columns;
    }

    public BuilderConfig setColumns(List<String> columnNames) {
        if (columnNames == null) {
            this.columns = null;
            return this;
        }
        this.columns = new ArrayList<String>(columnNames);
        return this;
    }
}
