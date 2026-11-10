package rickwporter.prettytable;

/**
 * Horizontal placement of cell text in text, rich, and HTML output.
 */
public enum HorizontalAlign {
    /** Centers the cell text. The HTML style is {@code text-align:center}. */
    CENTER("text-align:center"),

    /** Aligns the cell text to the left. The HTML style is {@code text-align:left}. */
    LEFT("text-align:left"),

    /** Aligns the cell text to the right. The HTML style is {@code text-align:right}. */
    RIGHT("text-align:right");

    private final String htmlStyle;

    HorizontalAlign(String html) {
        this.htmlStyle = html;
    }

    /**
     * Returns the CSS declaration written onto HTML header and body cells.
     *
     * @return a style such as {@code text-align:left}
     */
    public String getHtmlStyle() {
        return this.htmlStyle;
    }
}
