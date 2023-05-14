package enigma;
import org.junit.Test;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** Class to test the Alphabet.
 *  @author Arthur Utnehmer on 3/2/2022
 */

public class AlphabetTest {

    @Test
    public void sizeTest() {
        Alphabet test = new Alphabet("ABCDEF");
        assertEquals(test.size(), 6);
        Alphabet test2 = new Alphabet();
        assertEquals(test2.size(), 26);
    }

    @Test
    public void testContains() {
        Alphabet test = new Alphabet("ABCDEF");
        assertFalse(test.contains('N'));
        Alphabet test2 = new Alphabet("ABCDEF");
        assertTrue(test2.contains('B'));
        test2 = new Alphabet("ABCDEF");
        assertFalse(test2.contains('G'));
    }

    @Test
    public void testCharacterIndex() {
        Alphabet test = new Alphabet();
        assertEquals(test.toInt('A'), 0);
        assertEquals(test.toInt('B'), 1);
        assertEquals(test.toInt('Z'), 25);
        assertEquals(test.toInt('-'), -1);

    }

    @Test
    public void testToInt() {
        Alphabet test = new Alphabet();
        assertEquals(test.toChar(2), 'C');
        assertEquals(test.toChar(0), 'A');
        assertEquals(test.toChar(25), 'Z');

    }

}
