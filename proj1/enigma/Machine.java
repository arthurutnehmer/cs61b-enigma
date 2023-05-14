package enigma;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ListIterator;


/** Class that represents a complete enigma machine.
 *  @author Arthur Utnehmer on 3/2/2022
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */

    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numberOfRotors = numRotors;
        _NumberOfPawls = pawls;
        if (allRotors == null) {
            _rotorListSetup = new ArrayList<Rotor>();
            _rotorList = new ArrayList<Rotor>();
        } else {
            _rotorListSetup = new ArrayList<Rotor>(allRotors);
            _rotorList = new ArrayList<Rotor>(allRotors);
        }
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numberOfRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _NumberOfPawls;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        return _rotorListSetup.get(k);
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        ArrayList<Rotor> orderedRotarList = new ArrayList<Rotor>();
        for (String rotar: rotors) {
            for (Rotor element: _rotorList) {
                if (element.name().equals(rotar)) {
                    element.set(0);
                    orderedRotarList.add(element);
                }
            }
        }
        _rotorListSetup = orderedRotarList;
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (int x = 1; x < numRotors(); x++) {
            _rotorListSetup.get(x).set(_alphabet.toInt(setting.charAt(x - 1)));
        }
    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return _plugboard;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    public void advanceRotors() {
        ListIterator<Rotor> listToIterate =
                _rotorListSetup.listIterator(_rotorListSetup.size());
        boolean prevHasNotch = true;
        Rotor currentRotar = listToIterate.previous();
        while (listToIterate.hasPrevious()) {
            boolean currentRotorState = currentRotar.atNotch();
            Rotor nextRotor = listToIterate.previous();
            if (currentRotorState && nextRotor.rotates()) {
                currentRotar.advance();
                prevHasNotch = true;
            } else {
                if (prevHasNotch) {
                    currentRotar.advance();
                    prevHasNotch = false;
                }
                if (currentRotorState) {
                    prevHasNotch = true;
                }
            }
            currentRotar = nextRotor;
        }
    }

    /** Return the Rotor positions from left to right. */
    public String getRotorPositions() {
        String listOfPositions = "";
        for (Rotor element: _rotorListSetup) {
            listOfPositions = listOfPositions
                    + element.alphabet().toChar(element.setting());
        }
        return listOfPositions;
    }

    /** Return the Rotor positions from left to right. */
    public String getRotorNotches() {
        String listOfNotches = "";
        for (Rotor element: _rotorListSetup) {
            listOfNotches = listOfNotches + element.notches() + " ";
        }
        return listOfNotches;
    }

    /** test the apply rotars.
     * @return to test apply to rotor.
     * @param c used to state rotor position. */
    public int testApplyRotors(int c) {
        return applyRotors(c);
    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        ListIterator<Rotor> reverseRotorList =
                _rotorListSetup.listIterator(_rotorListSetup.size());
        while (reverseRotorList.hasPrevious()) {
            c = reverseRotorList.previous().convertForward(c);
        }
        for (int x = 1; x < _rotorListSetup.size(); x++) {
            c = _rotorListSetup.get(x).convertBackward(c);
        }
        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String convertedMessage  = "";
        for (int x = 0; x < msg.length(); x++) {
            convertedMessage = convertedMessage
                    + _alphabet.toChar(convert(_alphabet.toInt(msg.charAt(x))));
        }
        return convertedMessage;
    }

    /** Returns the rotor list.*/
    public ArrayList<Rotor> getRotorList() {
        return _rotorList;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** List of rotors from user conf file. */
    private ArrayList<Rotor> _rotorList;

    /** List of rotors that are setup (inserted). */
    private ArrayList<Rotor> _rotorListSetup;

    /** Number of pawls and rotors in this setup. */
    private int _numberOfRotors, _NumberOfPawls;

    /** Permutation. */
    private Permutation _plugboard;

}
