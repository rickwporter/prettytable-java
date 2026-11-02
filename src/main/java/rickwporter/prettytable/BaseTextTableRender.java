package rickwporter.prettytable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import rickwporter.prettytable.PrettyTable.HorizontalAlign;

abstract class BaseTextTableRender implements TableRenderInterface {
    private static final Pattern NEWLINE = Pattern.compile("\\R");

    // Pattern characters are left, fill, junction, right. The fill of a vertical pattern is unused.
    final String pad(String value, int width, HorizontalAlign align) {
        if (align == HorizontalAlign.LEFT) {
            return " " + StringUtils.rightPad(value, width) + " ";
        } else if (align == HorizontalAlign.RIGHT) {
            return " " + StringUtils.leftPad(value, width) + " ";
        } else if (align == HorizontalAlign.CENTER) {
            return StringUtils.center(value, width + 2);
        } else {
            throw new IllegalArgumentException("Unsupported HorizontalAlignment: " + align);
        }
    }

    final List<HorizontalAlign> alignments(PrettyTable table) {
        List<HorizontalAlign> hAligns = new ArrayList<HorizontalAlign>(table.getHorizAligns());
        int maxColumns = table.getMaxColumns();
        for (int i = hAligns.size(); i <= maxColumns; i++) {
            hAligns.add(table.getDefaultHorizAlign());
        }
        return hAligns;
    }

    final List<String> cellText(List<?> row) {
        return row.stream().map(c -> c.toString()).collect(Collectors.toList());
    }

    final String textRow(List<? extends Object> row, List<Integer> maxWidths, List<HorizontalAlign> hAligns,
            TableGlyphs glyphs) {
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
            result.append(this.textRowLine(line, maxWidths, hAligns, glyphs));
        }
        return result.toString();
    }

    private String textRowLine(List<String> row, List<Integer> maxWidths, List<HorizontalAlign> hAligns,
            TableGlyphs glyphs) {
        StringBuilder result = new StringBuilder();
        if (glyphs != null) {
            result.append(glyphs.left());
        }
        for (int cIdx = 0; cIdx < row.size(); cIdx++) {
            if (glyphs != null && cIdx > 0) {
                result.append(glyphs.junction());
            }
            result.append(this.pad(row.get(cIdx), maxWidths.get(cIdx), hAligns.get(cIdx)));
        }
        if (glyphs != null) {
            result.append(glyphs.right());
        }
        result.append("\n");
        return result.toString();
    }

    final String rule(TableGlyphs glyphs, List<Integer> maxWidths, boolean visible) {
        if (!visible) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        result.append(glyphs.left());
        for (int i = 0; i < maxWidths.size(); i++) {
            if (i > 0) {
                result.append(glyphs.junction());
            }
            int span = maxWidths.get(i) + 2;
            for (int j = 0; j < span; j++) {
                result.append(glyphs.fill());
            }
        }
        result.append(glyphs.right());
        result.append("\n");
        return result.toString();
    }

    // Caption is drawn under the table.
    final String textCaption(String caption) {
        if (caption == null || caption.isEmpty()) {
            return "";
        }
        return caption + "\n";
    }

    final boolean sharesLeadingCells(List<String> lastRow, List<String> currentRow) {
        boolean shared = false;
        for (int i = 0; i < lastRow.size(); i++) {
            if (!lastRow.get(i).equals(currentRow.get(i))) {
                break;
            }
            currentRow.set(i, "");
            shared = true;
        }
        return shared;
    }
}
