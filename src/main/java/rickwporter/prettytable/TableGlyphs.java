package rickwporter.prettytable;

/**
 * Four characters that draw one horizontal rule or one vertical border.
 * In order they are the left edge, the repeated fill, the junction between columns, and the right edge.
 * The fill of a vertical border is unused.
 */
public final class TableGlyphs {
    private Character mLeft;
    private Character mFill;
    private Character mJunction;
    private Character mRight;

    /**
     * Creates a glyph set.
     *
     * @param left character at the left edge
     * @param fill character repeated across a column, unused for a vertical border
     * @param junction character between columns
     * @param right character at the right edge
     */
    public TableGlyphs(Character left, Character fill, Character junction, Character right) {
        this.mLeft = left;
        this.mFill = fill;
        this.mJunction = junction;
        this.mRight = right;
    }

    /**
     * Returns the left-edge character.
     *
     * @return the left-edge character
     */
    public Character left() {
        return this.mLeft;
    }

    /**
     * Returns the fill character.
     *
     * @return the fill character
     */
    public Character fill() {
        return this.mFill;
    }

    /**
     * Returns the character drawn between columns.
     *
     * @return the junction character
     */
    public Character junction() {
        return this.mJunction;
    }

    /**
     * Returns the right-edge character.
     *
     * @return the right-edge character
     */
    public Character right() {
        return this.mRight;
    }
}
