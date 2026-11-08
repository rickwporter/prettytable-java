package rickwporter.prettytable;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

class CsvTableRender implements TableRenderInterface {
    private final Appendable output;

    CsvTableRender() {
        this(null);
    }

    CsvTableRender(Appendable destination) {
        this.output = destination;
    }

    @Override
    public String render(PrettyTable table, boolean removedRedundant) {
        StringBuilder result = new StringBuilder();
        try {
            Appendable target = this.output == null ? result : this.output;
            CSVFormat format = CSVFormat.DEFAULT.builder().setRecordSeparator("\n").get();
            CSVPrinter printer = new CSVPrinter(target, format);
            if (!table.getHeaders().isEmpty()) {
                printer.printRecord(table.getHeaders());
            }
            for (List<? extends Object> row : table.getRows()) {
                List<String> current = row.stream().map(c -> c.toString()).collect(Collectors.toList());
                printer.printRecord(current);
            }
            printer.close();
            return result.toString();
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to render table as CSV", ex);
        }
    }
}
