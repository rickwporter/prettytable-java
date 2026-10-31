package rickwporter.prettytable;

import org.junit.jupiter.api.Test;

import rickwporter.prettytable.PrettyTable.HorizontalAlign;

import org.junit.jupiter.api.Assertions;

public class TextTableRenderTest {
    @Test 
    public void testPad() {
        TextTableRender uut = new TextTableRender();

        Assertions.assertEquals(" a     ", uut.pad("a", 5, HorizontalAlign.LEFT));
        Assertions.assertEquals("   a   ", uut.pad("a", 5, HorizontalAlign.CENTER));
        Assertions.assertEquals("     a ", uut.pad("a", 5, HorizontalAlign.RIGHT));

        Assertions.assertThrows(IllegalArgumentException.class, () -> uut.pad("a", 5, null));
        //Assertions.assertThrows(IllegalArgumentException.class, () -> uut.pad("a", 5, new Integer(3)));
    }    
}
