package rickwporter.prettytable;

import java.util.ArrayList;
import java.util.List;

import rickwporter.prettytable.PrettyTable.HorizontalAlign;

class RichTableRender extends BaseTextTableRender {
    // Four characters are left, fill, junction, right.
    private static final TableGlyphs HEAVY_TOP = new TableGlyphs('┏', '━', '┳', '┓');
    private static final TableGlyphs HEAVY_HEAD = new TableGlyphs('┡', '━', '╇', '┩');
    private static final TableGlyphs HEAVY_VERT = new TableGlyphs('┃', ' ', '┃', '┃');
    private static final TableGlyphs SQUARE_TOP = new TableGlyphs('┌', '─', '┬', '┐');
    private static final TableGlyphs LIGHT_VERT = new TableGlyphs('│', ' ', '│', '│');
    private static final TableGlyphs LIGHT_ROW = new TableGlyphs('├', '─', '┼', '┤');
    private static final TableGlyphs LIGHT_BOTTOM = new TableGlyphs('└', '─', '┴', '┘');

    @Override
    public String render(PrettyTable table, boolean removeRedundant) {
        List<Integer> maxWidths = table.getMaxWidths();
        boolean showLines = table.isShowLines();
        boolean rowDividers = table.isRowDividers() && showLines;
        boolean header = table.isShowHeader() && !table.getHeaders().isEmpty();
        List<HorizontalAlign> hAligns = this.alignments(table);
        StringBuilder result = new StringBuilder();

        if (showLines && header) {
            result.append(this.rule(HEAVY_TOP, maxWidths, true));
            result.append(this.textRow(table.getHeaders(), maxWidths, hAligns, HEAVY_VERT));
            result.append(this.rule(HEAVY_HEAD, maxWidths, true));
        } else if (header) {
            result.append(this.textRow(table.getHeaders(), maxWidths, hAligns, null));
        } else if (showLines) {
            result.append(this.rule(SQUARE_TOP, maxWidths, true));
        }

        List<List<Object>> rows = table.getRows();
        List<String> lastRow = new ArrayList<String>();
        for (int rIdx = 0; rIdx < rows.size(); rIdx++) {
            List<String> currentRow = this.cellText(rows.get(rIdx));
            List<String> fullRow = new ArrayList<String>(currentRow);
            boolean divider = true;
            if (removeRedundant) {
                divider = !this.sharesLeadingCells(lastRow, currentRow);
                lastRow = fullRow;
            }
            TableGlyphs vertical = showLines ? LIGHT_VERT : null;
            result.append(this.textRow(currentRow, maxWidths, hAligns, vertical));
            boolean last = rIdx == rows.size() - 1;
            result.append(this.rule(LIGHT_ROW, maxWidths, rowDividers && divider && !last));
        }
        result.append(this.rule(LIGHT_BOTTOM, maxWidths, showLines));
        result.append(this.caption(table.getCaption()));
        return result.toString();
    }
}
