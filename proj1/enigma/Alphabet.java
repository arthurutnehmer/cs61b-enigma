package enigma;


/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Arthur Utnehmer on 3/3/2022
 */
class Alphabet {

    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        _alphabet = chars.toCharArray();
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _alphabet.length;
    }

    /** Returns the alphabet. */
    public char[] getAlphabet() {
        return _alphabet;
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        for (int x = 0; x < _alphabet.length; x++) {
            if (ch == _alphabet[x]) {
                return true;
            }
        }
        return false;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        return _alphabet[index];
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        for (int x = 0; x < _alphabet.length; x++) {
            if (ch == _alphabet[x]) {
                return x;
            }
        }
        return -1;
    }

    /** A char array containing the alphabet. */
    private char[] _alphabet;
}
