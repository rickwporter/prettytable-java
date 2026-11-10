package rickwporter.prettytable;

/**
 * Output produced by {@link PrettyTable} and {@link PrettyObject}.
 */
public enum OutputFormat {
    /** ASCII rules and vertical bars. */
    TEXT,

    /** Unicode box-drawing rules and vertical bars. */
    RICH,

    /** Comma-separated values. */
    CSV,

    /** An HTML table. */
    HTML,

    /** A JSON array of the header row followed by one object per data row. */
    JSON,

    /** A YAML list of the header row followed by one mapping per data row. */
    YAML,
}
