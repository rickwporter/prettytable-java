package rickwporter.prettytable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

import rickwporter.prettytable.PrettyTable.HorizontalAlign;

class TextTableRender implements TableRenderInterface {
    private static final Pattern NEWLINE = Pattern.compile("\\R");

    String pad(String value, int width, HorizontalAlign align) {
        if (align == null) {
            throw new IllegalArgumentException("Unsupported HorizontalAlignment: " + align);
        }
        switch (align) {
        case LEFT:
            return " " + StringUtils.rightPad(value, width) + " ";
        case RIGHT:
            return " " + StringUtils.leftPad(value, width) + " ";
        case CENTER:
            return StringUtils.center(value, width + 2);
        default:
            throw new IllegalArgumentException("Unsupported HorizontalAlignment: " + align);
        }
    }

    private String textRow(List<? extends Object> row, List<Integer> maxWidths, List<HorizontalAlign> hAligns,
        boolean showLines) {
        StringBuilder result = new StringBuilder();
        List<List<String>> splits = row.stream()
            .map(c -> Arrays.asList(NEWLINE.split(c.toString())))
            .collect(Collectors.toList());
        int maxLines = splits.stream().map(c -> c.size()).max(Integer::compare).get();
        for (int idx = 0; idx < maxLines; idx++) {
            List<String> line = new ArrayList<String>();
            for (List<String> column : splits) {
                line.add(idx < column.size() ? column.get(idx) : "");
            }
            result.append(this.textRowLine(line, maxWidths, hAligns, showLines));
        }
        return result.toString();
    }

    private String textRowLine(List<String> row, List<Integer> maxWidths, List<HorizontalAlign> hAligns,
        boolean showLines) {
        StringBuilder result = new StringBuilder();
        if (showLines) {
            result.append("|");
        }
        for (int cIdx = 0; cIdx < row.size(); cIdx++) {
            result.append(this.pad(row.get(cIdx), maxWidths.get(cIdx), hAligns.get(cIdx)));
            if (showLines) {
                result.append("|");
            }
        }
        result.append("\n");
        return result.toString();
    }

    // Caption is drawn under the table
    private String textCaption(String caption) {
        if (caption == null || caption.isEmpty()) {
            return "";
        }
        return caption + "\n";
    }

    private String textRule(List<Integer> maxWidths, Integer maxColumns, boolean showLines) {
        if (!showLines) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        result.append("+");
        for (int i = 0; i < maxColumns; i++) {
            for (int j = 0; j < maxWidths.get(i) + 2; j++) {
                result.append("-");
            }
            result.append("+");
        }
        result.append("\n");
        return result.toString();
    }

    @Override
    public String render(PrettyTable table, boolean removeRedundant) {
        List<Integer> maxWidths = table.getMaxWidths();
        StringBuilder result = new StringBuilder();
        Integer maxColumns = table.getMaxColumns();
        boolean showLines = table.isShowLines();
        boolean rowDividers = table.isRowDividers() && showLines;
        List<HorizontalAlign> hAligns = new ArrayList<>(table.getHorizAligns());
        for (int i = hAligns.size(); i <= maxColumns; i++) {
            hAligns.add(table.getDefaultHorizAlign());
        }

        if (table.isShowHeader() && !table.getHeaders().isEmpty()) {
            result.append(textRule(maxWidths, maxColumns, showLines));
            result.append(textRow(table.getHeaders(), maxWidths, hAligns, showLines));
        }
        result.append(textRule(maxWidths, maxColumns, showLines));
        List<String> lastRow = new ArrayList<>();
        for (List<Object> row : table.getRows()) {
            List<String> currentRow = row.stream().map(c -> c.toString()).collect(Collectors.toList());
            List<String> fullRow = new ArrayList<String>(currentRow);  // make a copy before manipulating
            boolean divider = true;
            if (removeRedundant) {
                for (int i = 0; i < lastRow.size(); i++) {
                    if (!lastRow.get(i).equals(currentRow.get(i))) {
                        break;
                    }
                    currentRow.set(i, "");
                    divider = false;
                }
                lastRow = fullRow;
            }
            result.append(textRow(currentRow, maxWidths, hAligns, showLines));
            result.append(textRule(maxWidths, maxColumns, rowDividers && divider));
        }
        result.append(textRule(maxWidths, maxColumns, showLines && !rowDividers));
        result.append(textCaption(table.getCaption()));

        return result.toString();
    }
}
