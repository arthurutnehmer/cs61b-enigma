package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Arthur Utnehmer on 3/2/2022
 */
public class MovingRotorTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Rotor rotor;
    private String alpha = UPPER_STRING;

    /** Check that rotor has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkRotor(String testId,
                            String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, rotor.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d (%c)", ci, c),
                         ei, rotor.convertForward(ci));
            assertEquals(msg(testId, "wrong inverse of %d (%c)", ei, e),
                         ci, rotor.convertBackward(ei));
        }
    }

    /** Set the rotor to the one with given NAME and permutation as
     *  specified by the NAME entry in ROTORS, with given NOTCHES. */
    private void setRotor(String name, HashMap<String, String> rotors,
                          String notches) {
        rotor = new MovingRotor(name, new Permutation(rotors.get(name), UPPER),
                                notches);
    }

    /* ***** TESTS ***** */

    @Test
    public void checkRotorAtA() {
        setRotor("I", NAVALA, "");
        checkRotor("Rotor I (A)", UPPER_STRING, NAVALA_MAP.get("I"));
    }

    @Test
    public void checkRotorAdvance() {
        setRotor("I", NAVALA, "");
        rotor.advance();
        checkRotor("Rotor I advanced", UPPER_STRING, NAVALB_MAP.get("I"));
    }

    @Test
    public void checkRotorSet() {
        setRotor("I", NAVALA, "");
        rotor.set(25);
        checkRotor("Rotor I set", UPPER_STRING, NAVALZ_MAP.get("I"));
    }

    @Test
    public void atNotch() {
        Permutation perm = new Permutation("(AELTPHQXRU) (BKNW) (CMOY)"
                + " (DFG) (IV) (JZ) (S)", UPPER);
        ArrayList<MovingRotor> listOfRotars = new ArrayList<MovingRotor>();
        for (int x = 0; x < 26; x++) {
            listOfRotars.add(new MovingRotor("test", perm,
                    Character.toString(perm.alphabet().toChar(x))));
        }

        for (int x = 0; x < listOfRotars.size(); x++) {
            for (int y = 0; y < x; y++) {
                listOfRotars.get(x).advance();
            }
            assertEquals(listOfRotars.get(x).atNotch(), true);
        }

        perm = new Permutation("(AELTPHQXRU)"
                + " (BKNW) (CMOY) (DFG) (IV) (JZ) (S)", UPPER);
        Rotor testRotar = new FixedRotor("test", perm);
        assertFalse(testRotar.atNotch());
        testRotar.advance();
        assertFalse(testRotar.atNotch());

        perm = new Permutation("(AELTPHQXRU) "
                + "(BKNW) (CMOY) (DFG) (IV) (JZ) (S)", UPPER);
        testRotar = new MovingRotor("test", perm, "D E F");
        assertFalse(testRotar.atNotch());
        testRotar.advance();
        assertFalse(testRotar.atNotch());
        testRotar.advance();
        assertFalse(testRotar.atNotch());
        testRotar.advance();
        assertTrue(testRotar.atNotch());
        testRotar.advance();
        assertTrue(testRotar.atNotch());
        testRotar.advance();
        assertTrue(testRotar.atNotch());
        testRotar.advance();
        assertFalse(testRotar.atNotch());
        testRotar.advance();
        assertFalse(testRotar.atNotch());
    }

    @Test
    public void checkRotarAtPositions() {
        Permutation perm = new Permutation("(AELTPHQXRU) "
                + "(BKNW) (CMOY) (DFG) (IV) (JZ) (S)", UPPER);
        MovingRotor testRotar = new MovingRotor("test", perm, "N");
        testRotar.set(0);
        assertEquals(perm.alphabet().toInt('E'),
                testRotar.convertForward(perm.alphabet().toInt('A')));
        testRotar.set(0);
        assertEquals(perm.alphabet().toInt('J'),
                testRotar.convertForward(perm.alphabet().toInt('Z')));
        testRotar.set(1);
        assertEquals(perm.alphabet().toInt('J'),
                testRotar.convertForward(perm.alphabet().toInt('A')));
        testRotar.set(0);
        assertEquals(perm.alphabet().toInt('S'),
                testRotar.convertForward(perm.alphabet().toInt('S')));
        testRotar.set(0);
        assertEquals(perm.alphabet().toInt('I'),
                testRotar.convertForward(perm.alphabet().toInt('V')));
        testRotar.set(1);
        assertEquals(perm.alphabet().toInt('J'),
                testRotar.convertForward(perm.alphabet().toInt('A')));
        testRotar.set(2);
        assertEquals(perm.alphabet().toInt('K'),
                testRotar.convertForward(perm.alphabet().toInt('A')));
        testRotar.set(3);
        assertEquals(perm.alphabet().toInt('I'),
                testRotar.convertForward(perm.alphabet().toInt('B')));
        testRotar.set(10);
        assertEquals(perm.alphabet().toInt('D'),
                testRotar.convertForward(perm.alphabet().toInt('A')));
        testRotar.set(25);
        assertEquals(perm.alphabet().toInt('K'),
                testRotar.convertForward(perm.alphabet().toInt('A')));
        testRotar.set(25);
        assertEquals(perm.alphabet().toInt('D'),
                testRotar.convertForward(perm.alphabet().toInt('Z')));
    }

    @Test
    public void checkConvertForward() {
        setRotor("I", NAVALA, "");
        checkRotor("Rotor I (A)", UPPER_STRING, NAVALA_MAP.get("I"));
        assertEquals(alpha.indexOf('P'),
                rotor.convertForward(alpha.indexOf('T')));
        assertEquals(alpha.indexOf('A'),
                rotor.convertForward(alpha.indexOf('U')));
        assertEquals(alpha.indexOf('S'),
                rotor.convertForward(alpha.indexOf('S')));
    }

    @Test
    public void checkConvertBackward() {
        Permutation perm = new Permutation("(AELTPHQXRU) (BKNW) (CMOY) "
                + "(DFG) (IV) (JZ) (S)", UPPER);
        MovingRotor testRotar = new MovingRotor("test", perm, "N");
        testRotar.set(0);
        assertEquals(perm.alphabet().toInt('A'),
                testRotar.convertBackward(perm.alphabet().toInt('E')));
        testRotar.set(0);
        assertEquals(perm.alphabet().toInt('J'),
                testRotar.convertBackward(perm.alphabet().toInt('Z')));
        testRotar.set(1);
        assertEquals(perm.alphabet().toInt('A'),
                testRotar.convertBackward(perm.alphabet().toInt('J')));
        testRotar.set(3);
        assertEquals(perm.alphabet().toInt('B'),
                testRotar.convertBackward(perm.alphabet().toInt('I')));
        testRotar.set(10);
        assertEquals(perm.alphabet().toInt('A'),
                testRotar.convertBackward(perm.alphabet().toInt('D')));
        testRotar.set(25);
        assertEquals(perm.alphabet().toInt('A'),
                testRotar.convertBackward(perm.alphabet().toInt('K')));
        testRotar.set(25);
        assertEquals(perm.alphabet().toInt('Z'),
                testRotar.convertBackward(perm.alphabet().toInt('D')));

        setRotor("I", NAVALA, "");
        checkRotor("Rotor I (A)", UPPER_STRING, NAVALA_MAP.get("I"));
        assertEquals(alpha.indexOf('T'),
                rotor.convertBackward(alpha.indexOf('P')));
        assertEquals(alpha.indexOf('U'),
                rotor.convertBackward(alpha.indexOf('A')));
        assertEquals(alpha.indexOf('S'),
                rotor.convertBackward(alpha.indexOf('S')));
    }

}
