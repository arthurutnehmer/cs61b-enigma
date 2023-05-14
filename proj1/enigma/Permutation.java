package enigma;

import java.util.HashMap;
import java.util.Scanner;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Arthur Utnehmer on 3/2/2022
 */
class Permutation {



    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        mapBiDirectional(cycles);
        _cycles = cycles;

    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        Scanner cyclesToMap = new Scanner(cycle);
        map(cyclesToMap);
    }

    /** Maps cycles from left to right.
     * @param cycles A cycle string to be added to the map. */
    private void mapBiDirectional(String cycles) {
        Scanner cyclesToMap = new Scanner(cycles);
        cyclesToMap.useDelimiter("[\\p{javaWhitespace}()]+");
        map(cyclesToMap);
    }

    /** General map that takes in scanner and maps each permutation.
     * @param cyclesToMap Maps the cycles from scanner to hashmap. */
    private void map(Scanner cyclesToMap) {
        while (cyclesToMap.hasNext()) {
            char[] cycleToEncode = cyclesToMap.next().toCharArray();
            for (int x = 0; x < cycleToEncode.length - 1; x++) {
                forwardDirectionMap.put(cycleToEncode[x],
                        cycleToEncode[(x + 1)]);
                reverseDirectionMap.put(cycleToEncode[x + 1],
                        cycleToEncode[(x)]);
            }
            forwardDirectionMap.put(cycleToEncode[cycleToEncode.length - 1],
                    cycleToEncode[0]);
            reverseDirectionMap.put(cycleToEncode[0],
                    cycleToEncode[cycleToEncode.length - 1]);
        }

        for (Character element: _alphabet.getAlphabet()) {
            if (forwardDirectionMap.get(element) == null) {
                forwardDirectionMap.put(element, element);
                reverseDirectionMap.put(element, element);
            }
        }
    }
    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Used to check mapping. */
    public void checkMappingBothWays() {
        System.out.println("------------------------"
                + "---------------------------------");
        for (Character element:_alphabet.getAlphabet()) {
            System.out.print(element + "->"
                    + forwardDirectionMap.get(element));
            System.out.println(" "
                    + forwardDirectionMap.get(element)
                    +  "->"
                    + reverseDirectionMap.get(
                            forwardDirectionMap.get(element)));
        }
        System.out.println("--------------------------"
                + "-------------------------------");
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        return _alphabet.toInt(
                forwardDirectionMap.get(_alphabet.toChar((wrap(p)))));
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        return _alphabet.toInt(
                reverseDirectionMap.get(_alphabet.toChar((wrap(c)))));
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        return forwardDirectionMap.get(p);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        return reverseDirectionMap.get(c);
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return the cycles. */
    public String getCycles() {
        return _cycles;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (Character element: _alphabet.getAlphabet()) {
            if (forwardDirectionMap.get(element) == element) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Mapping for converting forward. */
    private HashMap<Character, Character> forwardDirectionMap =
            new HashMap<Character, Character>();

    /** Mapping for converting backwards. */
    private HashMap<Character, Character> reverseDirectionMap =
            new HashMap<Character, Character>();

    /** String of cycles for this permutation. */
    private String _cycles;

}
