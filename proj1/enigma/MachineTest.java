package enigma;

import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;

import static enigma.TestUtils.UPPER;
import static org.junit.Assert.*;

/** The suite of all JUnit tests for the Machine class.
 *  @author Arthur Utnehmer on 3/3/2022
 */
public class MachineTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTS ***** */

    private static final Alphabet AZ = new Alphabet(TestUtils.UPPER_STRING);

    private static final HashMap<String, Rotor> ROTORS = new HashMap<>();

    static {
        HashMap<String, String> nav = TestUtils.NAVALA;
        ROTORS.put("B", new Reflector("B", new Permutation(nav.get("B"), AZ)));
        ROTORS.put("Beta",
                new FixedRotor("Beta",
                        new Permutation(nav.get("Beta"), AZ)));
        ROTORS.put("III",
                new MovingRotor("III",
                        new Permutation(nav.get("III"), AZ), "V"));
        ROTORS.put("IV",
                new MovingRotor("IV", new Permutation(nav.get("IV"), AZ),
                        "J"));
        ROTORS.put("I",
                new MovingRotor("I", new Permutation(nav.get("I"), AZ),
                        "Q"));
    }

    private static final String[] ROTORS1 = { "B", "Beta", "III", "IV", "I" };
    private static final String SETTING1 = "AXLE";

    private Machine mach1() {
        Machine mach = new Machine(AZ, 5, 3, ROTORS.values());
        mach.insertRotors(ROTORS1);
        mach.setRotors(SETTING1);
        return mach;
    }

    @Test
    public void testInsertRotors() {
        Machine mach = new Machine(AZ, 5, 3, ROTORS.values());
        mach.insertRotors(ROTORS1);
        assertEquals(5, mach.numRotors());
        assertEquals(3, mach.numPawls());
        assertEquals(AZ, mach.alphabet());
        assertEquals(ROTORS.get("B"), mach.getRotor(0));
        assertEquals(ROTORS.get("Beta"), mach.getRotor(1));
        assertEquals(ROTORS.get("III"), mach.getRotor(2));
        assertEquals(ROTORS.get("IV"), mach.getRotor(3));
        assertEquals(ROTORS.get("I"), mach.getRotor(4));
    }


    @Test
    public void testSetRotar() {
        Machine mach = new Machine(AZ, 5, 3, ROTORS.values());
        mach.insertRotors(ROTORS1);
        assertEquals(mach.getRotor(0).setting(), AZ.toInt('A'));
        assertEquals(mach.getRotor(1).setting(), AZ.toInt('A'));
        assertEquals(mach.getRotor(2).setting(), AZ.toInt('A'));
        assertEquals(mach.getRotor(3).setting(), AZ.toInt('A'));
        assertEquals(mach.getRotor(4).setting(), AZ.toInt('A'));
        mach.setRotors(SETTING1);
        assertEquals(mach.getRotor(0).setting(), AZ.toInt('A'));
        assertEquals(mach.getRotor(1).setting(), AZ.toInt('A'));
        assertEquals(mach.getRotor(2).setting(), AZ.toInt('X'));
        assertEquals(mach.getRotor(3).setting(), AZ.toInt('L'));
        assertEquals(mach.getRotor(4).setting(), AZ.toInt('E'));

    }



    @Test
    public void testAdvanceRotarsBasic() {
        Permutation perm = new Permutation("(ABC)", new Alphabet("ABC"));
        String[] answers = {"AAAB", "AAAC", "AABA", "AABB", "AABC", "AACA",
                            "ABAB", "ABAC", "ABBA", "ABBB", "ABBC", "ABCA",
                            "ACAB", "ACAC", "ACBA", "ACBB", "ACBC", "ACCA",
                            "AAAB"};
        ArrayList<Rotor> testRotor = new ArrayList<Rotor>();
        String[] toInsertRotors = {"Beta", "III", "IV", "I"};
        Rotor rotarForList = new MovingRotor("I", perm, "C");
        testRotor.add(rotarForList);
        rotarForList = new MovingRotor("IV", perm, "C");
        testRotor.add(rotarForList);
        rotarForList = new MovingRotor("III", perm, "C");
        testRotor.add(rotarForList);
        rotarForList = new FixedRotor("Beta", perm);
        testRotor.add(rotarForList);

        Machine mach = new Machine(UPPER, 5, 3, testRotor);
        mach.insertRotors(toInsertRotors);
        for (int x = 0; x < 19; x++) {
            mach.advanceRotors();
            assertEquals(answers[x], mach.getRotorPositions());
        }
        for (int x = 1; x < 19; x++) {
            mach.advanceRotors();
            assertEquals(answers[x], mach.getRotorPositions());
        }
    }

    @Test
    public void applyRotorsTest() {
        Alphabet alphabetUsed = new Alphabet("abcdefghijklmnopqrstuvwxyz");
        ArrayList<Rotor> listOfRotors = new ArrayList<Rotor>();
        listOfRotors.add(new MovingRotor("I",
                new Permutation("(wordle) (is) (fun)", alphabetUsed), "a"));
        listOfRotors.add(new MovingRotor("II",
                new Permutation("(tears) (boing) (lucky)", alphabetUsed), "b"));
        listOfRotors.add(new MovingRotor("III",
                new Permutation("(quack) (froze) (twins) (glyph)",
                        alphabetUsed), "m"));
        listOfRotors.add(new FixedRotor("Beta",
                new Permutation("(az) (by) (cx) (dw) (ev) (fu) (gt) "
                        + "(hs) (ir) (jq) (kp) (lo) (mn)", alphabetUsed)));
        String[] toInsertRotors = {"Beta", "III", "II", "I"};
        Machine mach = new Machine(alphabetUsed, 4, 3, listOfRotors);
        mach.insertRotors(toInsertRotors);
        mach.getRotor(1).set(alphabetUsed.toInt('m'));
        mach.getRotor(2).set(alphabetUsed.toInt('a'));
        mach.getRotor(3).set(alphabetUsed.toInt('a'));
        mach.setPlugboard(new Permutation("(az) (mn)", alphabetUsed));
        assertEquals(mach.convert("aldie"), "wgqxv");
    }

    @Test
    public void testConvertChar() {
        Machine mach = mach1();
        mach.setPlugboard(new Permutation("(YF) (HZ)", AZ));
        assertEquals(25, mach.convert(24));
    }

    @Test
    public void testConvertMsg() {
        Machine mach = mach1();
        mach.setPlugboard(new Permutation("(HQ) (EX) (IP) (TR) (BY)", AZ));
        assertEquals("QVPQSOKOILPUBKJZPISFXDW",
                mach.convert("FROMHISSHOULDERHIAWATHA"));
    }
}
