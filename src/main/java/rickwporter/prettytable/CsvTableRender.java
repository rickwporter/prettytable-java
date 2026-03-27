package rickwporter.prettytable;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

class CsvTableRender implements TableRenderInterface {

    @Override
    public String render(PrettyTable table, boolean removedRedundant) {
        try {
            StringBuilder result = new StringBuilder();
            CSVFormat format = CSVFormat.DEFAULT.builder().setRecordSeparator("\n").build();
            CSVPrinter printer = new CSVPrinter(result, format);
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
            // nothing to do here
        }
        return null;
    }
}
