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

    private String pad(String value, int width, HorizontalAlign align) {
        switch (align) {
        case LEFT:
            return " " + StringUtils.rightPad(value, width) + " ";
        case RIGHT:
            return " " + StringUtils.leftPad(value, width) + " ";
        case CENTER:
            return StringUtils.center(value, width + 2);
        default:
            return StringUtils.center(value, width + 2);
        }
    }

    private String textRow(List<? extends Object> row, List<Integer> maxWidths, List<HorizontalAlign> hAligns) {
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
            result.append(this.textRowLine(line, maxWidths, hAligns));
        }
        return result.toString();
    }

    private String textRowLine(List<String> row, List<Integer> maxWidths, List<HorizontalAlign> hAligns) {
        StringBuilder result = new StringBuilder();
        result.append("|");
        for (int cIdx = 0; cIdx < row.size(); cIdx++) {
            result.append(this.pad(row.get(cIdx), maxWidths.get(cIdx), hAligns.get(cIdx)));
            result.append("|");
        }
        result.append("\n");
        return result.toString();
    }

    private String textRule(List<Integer> maxWidths, Integer maxColumns) {
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
        List<HorizontalAlign> hAligns = new ArrayList<>(table.getHorizAligns());
        for (int i = hAligns.size(); i <= maxColumns; i++) {
            hAligns.add(table.getDefaultHorizAlign());
        }

        if (!table.getHeaders().isEmpty()) {
            result.append(textRule(maxWidths, maxColumns));
            result.append(textRow(table.getHeaders(), maxWidths, hAligns));
        }
        result.append(textRule(maxWidths, maxColumns));
        List<String> lastRow = new ArrayList<>();
        for (List<Object> row : table.getRows()) {
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
            result.append(textRow(currentRow, maxWidths, hAligns));
        }
        result.append(textRule(maxWidths, maxColumns));
        return result.toString();
    }
}
