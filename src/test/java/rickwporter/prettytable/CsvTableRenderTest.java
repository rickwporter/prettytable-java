package rickwporter.prettytable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyChar;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

public class CsvTableRenderTest {
    @Test
    public void testIoException() throws IOException {
        IOException failure = new IOException("csv failed");
        Appendable out = mock(Appendable.class);
        when(out.append(any(CharSequence.class))).thenThrow(failure);
        when(out.append(any(CharSequence.class), anyInt(), anyInt())).thenThrow(failure);
        when(out.append(anyChar())).thenThrow(failure);

        // A header makes CSVPrinter's constructor write, so the failing Appendable throws there.
        CSVFormat withHeader = CSVFormat.DEFAULT.builder()
            .setRecordSeparator("\n")
            .setHeader("Name")
            .get();
        try (MockedConstruction<CSVFormat.Builder> ignored = mockConstruction(CSVFormat.Builder.class,
                (mock, context) -> {
                    when(mock.setRecordSeparator(anyString())).thenReturn(mock);
                    when(mock.get()).thenReturn(withHeader);
                })) {
            PrettyTable table = new PrettyTable("Name");
            table.addRow("Ada");
            CsvTableRender uut = new CsvTableRender(out);

            IllegalStateException ex = Assertions.assertThrows(
                IllegalStateException.class,
                () -> uut.render(table, false)
            );
            Assertions.assertEquals("Unable to render table as CSV", ex.getMessage());
            Assertions.assertSame(failure, ex.getCause());
        }
    }
}
