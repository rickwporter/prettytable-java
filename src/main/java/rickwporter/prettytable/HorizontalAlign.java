package rickwporter.prettytable;

public enum HorizontalAlign {
    CENTER("text-align:center"),
    LEFT("text-align:left"),
    RIGHT("text-align:right");

    private final String htmlStyle;

    HorizontalAlign(String html) {
        this.htmlStyle = html;
    }

    public String getHtmlStyle() {
        return this.htmlStyle;
    }
}
