package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void testConstructor() {
        perm = new Permutation("(AELTPHQXRU) (BKNW) (CMOY)"
                + " (DFG) (IV) (JZ) (S)", UPPER);
        perm.checkMappingBothWays();

        perm = new Permutation("(FIXVYOMW) (CDKLHUP) (ESZ) (BJ)"
                + " (GR) (NT) (A) (Q)", UPPER);
        perm.checkMappingBothWays();

        perm = new Permutation("(FIXVYOMW)", UPPER);
        perm.checkMappingBothWays();
    }

    @Test
    public void testSize() {
        perm = new Permutation("(AELTPHQXRU) (BKNW) "
                + "(CMOY) (DFG) (IV) (JZ) (S)", UPPER);
        assertEquals(26, perm.size());

        perm = new Permutation("(ABCD)", new Alphabet("ABCD"));
        assertEquals(4, perm.size());
    }

    @Test
    public void permuteIntTest() {
        perm = new Permutation("(AELTPHQXRU) (BKNW) (CMOY) (DFG)"
                + " (IV) (JZ) (S)", UPPER);
        assertEquals(perm.permute(UPPER.toInt('A')), UPPER.toInt('E'));
        assertEquals(perm.permute(UPPER.toInt('U')), UPPER.toInt('A'));
        assertEquals(perm.permute(UPPER.toInt('S')), UPPER.toInt('S'));
        assertEquals(perm.permute(UPPER.toInt('J')), UPPER.toInt('Z'));

        assertEquals(perm.permute('A'), 'E');
        assertEquals(perm.permute('S'), 'S');
        assertEquals(perm.permute('I'), 'V');
        assertEquals(perm.permute('J'), 'Z');

        assertEquals(perm.invert('E'), 'A');
        assertEquals(perm.invert('S'), 'S');
        assertEquals(perm.invert('V'), 'I');
        assertEquals(perm.invert('I'), 'V');
        assertEquals(perm.invert(UPPER.toInt('V')), UPPER.toInt('I'));
        assertEquals(perm.invert(UPPER.toInt('I')), UPPER.toInt('V'));
        assertNotEquals(perm.invert(UPPER.toInt('I')), UPPER.toInt('I'));

        perm = new Permutation("(ABCD)", UPPER);
        assertEquals(perm.permute(UPPER.toInt('A')), UPPER.toInt('B'));
        assertEquals(perm.permute(UPPER.toInt('B')), UPPER.toInt('C'));
        assertEquals(perm.permute(UPPER.toInt('C')), UPPER.toInt('D'));
        assertEquals(perm.permute(UPPER.toInt('D')), UPPER.toInt('A'));

        perm = new Permutation("(ABCD)", UPPER);
        assertEquals(perm.invert(UPPER.toInt('D')), UPPER.toInt('C'));
        assertEquals(perm.invert(UPPER.toInt('A')), UPPER.toInt('D'));
        assertEquals(perm.invert(UPPER.toInt('B')), UPPER.toInt('A'));
        assertEquals(perm.invert(UPPER.toInt('C')), UPPER.toInt('B'));
        assertEquals(perm.invert(UPPER.toInt('E')), UPPER.toInt('E'));
        assertEquals(perm.invert(UPPER.toInt('Z')), UPPER.toInt('Z'));

    }

    @Test
    public void derangement() {
        perm = new Permutation("(AELTPHQXRU) (BKNW) (CMOY) (DFG) "
                + "(IV) (JZ) (S)", UPPER);
        assertFalse(perm.derangement());

        perm = new Permutation("(AEPLIYWCOXMRFZBSTGJQNH) (DV) (KU)", UPPER);
        assertTrue(perm.derangement());

        perm = new Permutation("(ABCD)", new Alphabet("ABCDE"));
        assertFalse(perm.derangement());

        perm = new Permutation("(ABCD)", new Alphabet("ABCD"));
        assertTrue(perm.derangement());
    }
}
