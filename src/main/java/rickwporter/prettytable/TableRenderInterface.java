package rickwporter.prettytable;

/**
 * Turns a {@link PrettyTable} into a string.
 */
public interface TableRenderInterface {
    /**
     * Renders {@code table}.
     * Text, rich, and HTML renderers clear repeated leading cells when {@code removeRedundant} is
     * true. CSV, JSON, and YAML keep every cell.
     *
     * @param table table to render
     * @param removeRedundant whether repeated leading cells should be cleared
     * @return the rendered table
     */
    String render(PrettyTable table, boolean removeRedundant);
}
