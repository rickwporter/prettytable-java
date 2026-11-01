package rickwporter.prettytable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rickwporter.prettytable.PrettyTable.HorizontalAlign;

public final class TableBuilder {
    private static final String WILDCARD_COLUMN = "*";
    private static final Object MANY_KEYS = new Object();

    private final BuilderConfig config;

    public TableBuilder() {
        this(null);
    }

    public TableBuilder(BuilderConfig options) {
        this.config = options == null ? new BuilderConfig() : options;
    }

    public PrettyTable build(Object data) {
        if (isNothing(data)) {
            return null;
        }
        if (data instanceof Map) {
            return this.createObjectTable((Map<?, ?>) data, true);
        }
        if (data instanceof List) {
            return this.createList((List<?>) data);
        }
        throw new IllegalArgumentException("Unable to create table for type " + data.getClass().getSimpleName());
    }

    private static boolean isNothing(Object data) {
        if (data == null) {
            return true;
        }
        if (data instanceof Map) {
            return ((Map<?, ?>) data).isEmpty();
        }
        if (data instanceof List) {
            return ((List<?>) data).isEmpty();
        }
        return false;
    }

    private PrettyTable createList(List<?> items) {
        Object first = items.get(0);
        if (first instanceof Map) {
            List<String> columns = this.config.getColumns();
            if (columns != null && !columns.isEmpty()) {
                return this.createListColumnsTable(items, columns);
            }
            return this.createListTable(items, true);
        }
        if (isSimpleList(items)) {
            return this.createSimpleListTable(items);
        }
        throw new IllegalArgumentException("Unable to create table for type list");
    }

    private static boolean isSimpleList(List<?> items) {
        for (Object item : items) {
            if (item == null) {
                continue;
            }
            if (!(item instanceof String || item instanceof Number || item instanceof Boolean)) {
                return false;
            }
        }
        return true;
    }

    private PrettyTable createSimpleListTable(List<?> items) {
        PrettyTable result = new PrettyTable(this.config.getItemsLabel());
        this.configure(result, true, true, formatCaption(this.config.getItemsCaption(), items.size()));
        for (Object item : items) {
            result.addRow(this.cellValue(item));
        }
        return result;
    }

    private PrettyTable createListColumnsTable(List<?> items, List<String> columns) {
        String[] headers = new String[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            headers[i] = this.headerize(columns.get(i));
        }
        PrettyTable result = new PrettyTable(headers);
        this.configure(result, true, true, null);
        Set<String> skip = new HashSet<String>(columns);
        for (Object item : items) {
            Map<?, ?> map = asMap(item);
            Object[] values = new Object[columns.size()];
            for (int i = 0; i < columns.size(); i++) {
                String column = columns.get(i);
                if (WILDCARD_COLUMN.equals(column)) {
                    values[i] = this.cellValue(copyWithout(map, skip));
                } else {
                    values[i] = this.cellValue(map.get(column));
                }
            }
            result.addRow(values);
        }
        return result;
    }

    private PrettyTable createListTable(List<?> items, boolean outer) {
        Map<?, ?> first = asMap(items.get(0));
        String nameKey = this.findNameKey(first);
        String caption = outer ? formatCaption(this.config.getItemsCaption(), items.size()) : null;
        if (nameKey == null) {
            PrettyTable result = new PrettyTable(this.config.getValuesLabel());
            this.configure(result, outer, true, caption);
            for (Object item : items) {
                result.addRow(this.cellValue(asMap(item)));
            }
            return result;
        }

        Object other = this.singleOtherKey(first, nameKey);
        if (other != MANY_KEYS) {
            PrettyTable result = new PrettyTable(this.headerize(nameKey), this.headerize(stringify(other)));
            this.configure(result, outer, true, caption);
            for (Object item : items) {
                Map<?, ?> map = asMap(item);
                result.addRow(this.nameCell(map, nameKey), this.cellValue(map.get(other)));
            }
            return result;
        }

        PrettyTable result = new PrettyTable(this.headerize(nameKey), this.config.getPropertiesLabel());
        this.configure(result, outer, true, caption);
        for (Object item : items) {
            Map<?, ?> map = asMap(item);
            Map<Object, Object> rest = copyWithout(map, Collections.singleton(nameKey));
            result.addRow(this.nameCell(map, nameKey), this.cellValue(rest));
        }
        return result;
    }

    private PrettyTable createObjectTable(Map<?, ?> obj, boolean outer) {
        PrettyTable result = new PrettyTable(this.config.getPropertyLabel(), this.config.getValueLabel());
        this.configure(result, outer, false, null);
        for (Map.Entry<?, ?> entry : obj.entrySet()) {
            String key = this.truncate(stringify(entry.getKey()), this.config.getKeyMaxLen());
            result.addRow(key, this.cellValue(entry.getValue()));
        }
        return result;
    }

    private Object cellValue(Object obj) {
        if (obj instanceof Map) {
            return this.createObjectTable((Map<?, ?>) obj, false);
        }
        if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            if (!list.isEmpty() && list.get(0) instanceof Map) {
                return this.createListTable(list, false);
            }
            if (!list.isEmpty()) {
                return this.truncate(joinList(list), this.config.getValueMaxLen());
            }
        }
        String text = stringify(obj);
        int maxLen = this.isUrl(text) ? this.config.getUrlMaxLen() : this.config.getValueMaxLen();
        return this.truncate(text, maxLen);
    }

    private void configure(PrettyTable result, boolean outer, boolean dividers, String caption) {
        result.setShowHeader(outer);
        result.setShowLines(outer);
        result.setRowDividers(dividers);
        result.setRemoveRedundant(false);
        result.setCaption(caption);
        HorizontalAlign[] aligns = new HorizontalAlign[result.getHeaders().size()];
        Arrays.fill(aligns, HorizontalAlign.LEFT);
        result.setHorizAligns(aligns);
    }

    private String findNameKey(Map<?, ?> item) {
        for (String field : this.config.getKeyFields()) {
            if (item.containsKey(field)) {
                return field;
            }
        }
        return null;
    }

    private Object singleOtherKey(Map<?, ?> item, String nameKey) {
        Object found = null;
        boolean seen = false;
        for (Object key : item.keySet()) {
            if (nameKey.equals(key)) {
                continue;
            }
            if (seen) {
                return MANY_KEYS;
            }
            found = key;
            seen = true;
        }
        return seen ? found : MANY_KEYS;
    }

    private String nameCell(Map<?, ?> map, String nameKey) {
        Object raw = map.containsKey(nameKey) ? map.get(nameKey) : this.config.getUnknownLabel();
        return this.truncate(stringify(raw), this.config.getKeyMaxLen());
    }

    private boolean isUrl(String text) {
        for (String prefix : this.config.getUrlPrefixes()) {
            if (text.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private static Map<?, ?> asMap(Object item) {
        if (!(item instanceof Map)) {
            throw new IllegalArgumentException("Unable to create table for type list");
        }
        return (Map<?, ?>) item;
    }

    private static Map<Object, Object> copyWithout(Map<?, ?> source, Set<?> skip) {
        Map<Object, Object> copy = new LinkedHashMap<Object, Object>();
        for (Map.Entry<?, ?> entry : source.entrySet()) {
            if (skip.contains(entry.getKey())) {
                continue;
            }
            copy.put(entry.getKey(), entry.getValue());
        }
        return copy;
    }

    private static String joinList(List<?> items) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(stringify(items.get(i)));
        }
        return result.toString();
    }

    String headerize(String value) {
        if (WILDCARD_COLUMN.equals(value)) {
            return this.config.getWildcardHeader();
        }
        if (value.isEmpty()) {
            return value;
        }
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    static String stringify(Object value) {
        if (value == null) {
            return "None";
        }
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue() ? "True" : "False";
        }
        return String.valueOf(value);
    }

    String truncate(String value, int maxLength) {
        String ellipsis = this.config.getEllipsis();
        if (value.length() <= maxLength) {
            return value;
        }
        int keep = maxLength - ellipsis.length();
        if (keep < 0) {
            keep = 0;
        }
        return value.substring(0, keep) + ellipsis;
    }

    static String formatCaption(String template, int count) {
        String countText = Integer.toString(count);
        if (template.contains("{}")) {
            return template.replace("{}", countText);
        }
        if (template.contains("%d") || template.contains("%s")) {
            return String.format(template, count);
        }
        return template;
    }
}
