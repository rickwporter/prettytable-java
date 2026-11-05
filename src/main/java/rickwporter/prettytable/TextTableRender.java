package rickwporter.prettytable;

import java.util.ArrayList;
import java.util.List;

class TextTableRender extends BaseTextTableRender {
    private static final TableGlyphs RULE = new TableGlyphs('+', '-', '+', '+');
    private static final TableGlyphs VERTICAL = new TableGlyphs('|', ' ', '|', '|');

    @Override
    public String render(PrettyTable table, boolean removeRedundant) {
        List<Integer> maxWidths = table.getMaxWidths();
        StringBuilder result = new StringBuilder();
        boolean showLines = table.isShowLines();
        boolean rowDividers = table.isRowDividers() && showLines;
        List<HorizontalAlign> hAligns = this.alignments(table);
        TableGlyphs vertical = showLines ? VERTICAL : null;

        if (table.isShowHeader() && !table.getHeaders().isEmpty()) {
            result.append(this.rule(RULE, maxWidths, showLines));
            result.append(this.textRow(table.getHeaders(), maxWidths, hAligns, vertical));
        }
        result.append(this.rule(RULE, maxWidths, showLines));
        List<String> lastRow = new ArrayList<String>();
        for (List<Object> row : table.getRows()) {
            List<String> currentRow = this.cellText(row);
            List<String> fullRow = new ArrayList<String>(currentRow);
            boolean divider = true;
            if (removeRedundant) {
                divider = !this.sharesLeadingCells(lastRow, currentRow);
                lastRow = fullRow;
            }
            result.append(this.textRow(currentRow, maxWidths, hAligns, vertical));
            result.append(this.rule(RULE, maxWidths, rowDividers && divider));
        }
        result.append(this.rule(RULE, maxWidths, showLines && !rowDividers));
        result.append(this.caption(table.getCaption()));

        return result.toString();
    }
}
