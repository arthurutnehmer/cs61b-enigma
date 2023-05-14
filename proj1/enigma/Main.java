package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ucb.util.CommandArgs;


import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Arthur Utnehmer on 3/6/2022
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */

    public static void main(String... args) {
        try {
            CommandArgs options =
                new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                            + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
      *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        try {
            Machine enigma = this.readConfig();
            Pattern patternForSetup =
                    Pattern.compile("^.*([*]).*");
            Matcher match;
            while (_input.hasNextLine()) {
                String setupCurrentLine = _input.nextLine();
                match = patternForSetup.matcher(setupCurrentLine);
                if (match.matches()) {
                    setUp(enigma, setupCurrentLine);
                } else {
                    setupCurrentLine = setupCurrentLine.trim();
                    setupCurrentLine = setupCurrentLine.replaceAll("\\s", "");
                    printMessageLine(enigma.convert(setupCurrentLine));
                }
            }
        } catch (Exception excp) {
            throw error("No could not open", "no");
        }

        System.exit(0);
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        _rotorMoving = new ArrayList<String>();
        _rotorStill = new ArrayList<String>();
        Pattern patternMoving =
                Pattern.compile("^[\\s]*([A-z0-9]+)[\\s]*([M]).*");
        Pattern patternStill =
                Pattern.compile("^[\\s]*([A-z]+)[\\s]*"
                        + "([RN])([A-z]*)\\s*([(].*[)])");
        Pattern excess = Pattern.compile("^\\s*([(].*[)])");
        Matcher matcher;
        try {
            String alphabetImported = _config.nextLine();
            _importedAlphabet = new Alphabet(alphabetImported);
            int numberOfRotorSlots = _config.nextInt();
            int numberOfPawls = _config.nextInt();

            while (_config.hasNextLine()) {
                String lineOfText = _config.nextLine();
                if (lineOfText.isBlank()) {
                    assert true;
                } else {
                    matcher = patternMoving.matcher(lineOfText);
                    if (matcher.matches()) {
                        _rotorMoving.add(lineOfText);
                    }
                    matcher = patternStill.matcher(lineOfText);
                    if (matcher.matches()) {
                        _rotorStill.add(lineOfText);
                    }
                    matcher = excess.matcher(lineOfText);
                    if (matcher.matches()) {
                        _rotorStill.set(_rotorStill.size() - 1,
                                _rotorStill.get(_rotorStill.size() - 1)
                                        + lineOfText.trim());
                    }
                }
            }

            _listOfRotors = new ArrayList<Rotor>();
            Rotor rotar = readRotor();
            while (rotar != null) {
                _listOfRotors.add(rotar);
                rotar = readRotor();
            }

            return new Machine(_importedAlphabet,
                    numberOfRotorSlots, numberOfPawls, _listOfRotors);

        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }


    private void printRotorInto() {
        for (Rotor element: _listOfRotors) {
            System.out.println("Name:" + element.name()
                    + " rotates:" + element.rotates()
                    + " notches:" + element.notches()
                    + " cycles:"
                    + element.permutation().getCycles());
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            if (!_rotorMoving.isEmpty()) {
                String currentRotor = _rotorMoving.remove(0);
                Scanner sc = new Scanner(currentRotor);
                String name = sc.next();
                String notches = sc.next();
                notches = notches.substring(1, notches.length());
                sc.useDelimiter("^\\s*([(].*[)])");
                String permutation = sc.next();
                return new MovingRotor(name,
                        new Permutation(
                                permutation, _importedAlphabet), notches);
            } else if (!_rotorStill.isEmpty()) {
                String currentRotor = _rotorStill.remove(0);
                Scanner sc = new Scanner(currentRotor);
                String name = sc.next();
                String type = sc.next();
                sc.useDelimiter("^\\s*([(].*[)])");
                String permutation = sc.next();

                return new FixedRotor(name,
                        new Permutation(permutation, _importedAlphabet));
            } else {
                return null;
            }

        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        Scanner parse = new Scanner(settings);
        ArrayList<String> rotorSetup = new ArrayList<String>();
        ArrayList<String> positionsAndPlugboard = new ArrayList<String>();
        try {
            while (parse.hasNext()) {
                String currentToken = parse.next();
                boolean rotorFound = false;
                for (Rotor element: M.getRotorList()) {
                    if (element.name().equals(currentToken)) {
                        rotorSetup.add(element.name());
                        rotorFound = true;
                    }
                }
                if (!rotorFound) {
                    positionsAndPlugboard.add(currentToken);
                }
            }
        } catch (Exception e) {
            System.out.println("Exception detected.");
        }
        String[] rotorSetupConverted = new String[rotorSetup.size()];
        rotorSetupConverted = rotorSetup.toArray(rotorSetupConverted);
        M.insertRotors(rotorSetupConverted);

        Pattern board = Pattern.compile("^.*([(][A-z]*[)]).*");
        Pattern positionsMatch = Pattern.compile("([A-z0-9]+)");
        Matcher match;
        Matcher match2;

        String plugboard = "";
        String positions = "";
        try {
            for (String element: positionsAndPlugboard) {
                match = board.matcher(element);
                match2 = positionsMatch.matcher(element);
                if (match.matches()) {
                    plugboard = plugboard + element + " ";
                } else if (match2.matches()) {
                    positions = positions + element + " ";
                }
            }

        } catch (IndexOutOfBoundsException e) {
            System.out.println("No.");
        }
        plugboard = plugboard.trim();
        positions = positions.trim();
        M.setPlugboard(new Permutation(plugboard, _importedAlphabet));
        M.setRotors(positions);
    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        char[] display = msg.toCharArray();
        int charCount = 0;
        String messageToPrint = "";
        for (int x = 0; x < display.length; x++) {
            if (charCount == 5) {
                _output.print(messageToPrint);
                messageToPrint = "";
                charCount = 0;
                if (x - 1 < display.length) {
                    _output.print(" ");
                }
            }
            messageToPrint = messageToPrint + display[x];
            charCount++;
        }
        _output.println(messageToPrint);
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;

    /** String that has list of moving rotors that are in this machine. */
    private ArrayList<String> _rotorMoving;

    /** String that has list of still rotors that are in this machine. */
    private ArrayList<String> _rotorStill;

    /** Alphabet imported from conf file.*/
    private Alphabet _importedAlphabet;

    /** List of rotors that are used to make this machine. */
    private ArrayList<Rotor> _listOfRotors;
}
