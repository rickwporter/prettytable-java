package rickwporter.prettytable;

public final class TableGlyphs {
    private Character mLeft;
    private Character mFill;
    private Character mJunction;
    private Character mRight;

    public TableGlyphs(Character left, Character fill, Character junction, Character right) {
        this.mLeft = left;
        this.mFill = fill;
        this.mJunction = junction;
        this.mRight = right;
    }

    public Character left() {
        return this.mLeft;
    }

    public Character fill() {
        return this.mFill;
    }

    public Character junction() {
        return this.mJunction;
    }

    public Character right() {
        return this.mRight;
    }
}
