package rickwporter.prettytable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;


public class YamlTableRenderTest {
    @Test
    public void testJsonProcessingException() throws IOException {
        JsonProcessingException failure = new JsonGenerationException("this is it", (JsonGenerator)null);
        YAMLMapper mapper = mock(YAMLMapper.class);
        YamlTableRender uut = new YamlTableRender(mapper);
        when(mapper.writeValueAsString(any())).thenThrow(failure);
    
        PrettyTable table = new PrettyTable("Name");
        table.addRow("Ada");

        IllegalStateException ex = Assertions.assertThrows(
            IllegalStateException.class,
            () -> uut.render(table, false)
        );
        Assertions.assertEquals("Unable to render table as YAML", ex.getMessage());
        Assertions.assertSame(failure, ex.getCause());
    }
}
