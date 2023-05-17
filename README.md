# cs61b-enigma
### Enigma Simulator Project
This project involves the development of an Enigma machine simulator. The Enigma machine is a cipher device used by the Germans during World War II for the encryption and decryption of secret messages. This simulator will accept a set of rotors, a configuration, and an input message to provide an encoded or decoded output.

Project Description
The primary task is to create a program that simulates the Enigma machine's operations. The program will take an input file that contains a message to encode or decode, a configuration file that describes the machine and the available rotors, and produce an output file with the encoded or decoded message.

Permutations
The rotors and the plugboard in the Enigma machine implement permutations. We will describe these permutations using cycle representation. For example, the notation (AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S) describes a permutation where 'A' maps to 'E', 'E' maps to 'L', 'L' maps to 'T', and so on.

Example
Consider a set of rotors and suppose that:

The rotors in positions 1--5 are, respectively, B, Beta, III, IV, and I.
The rotors in positions 2--5 are currently at positions A, X, L, E, respectively.
In the plugboard, the letter pair 'Y' and 'F' and the letter pair 'Z' and 'H' are both interchanged.
When the letter 'Y' is input, it undergoes a series of transformations through the rotors and plugboard, eventually being converted to 'Z'.

Input and Output
To run the program on the command line, use the command
