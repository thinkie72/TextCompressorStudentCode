/******************************************************************************
 *  Compilation:  javac TextCompressor.java
 *  Execution:    java TextCompressor - < input.txt   (compress)
 *  Execution:    java TextCompressor + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *  Data files:   abra.txt
 *                jabberwocky.txt
 *                shakespeare.txt
 *                virus.txt
 *
 *  % java DumpBinary 0 < abra.txt
 *  136 bits
 *
 *  % java TextCompressor - < abra.txt | java DumpBinary 0
 *  104 bits    (when using 8-bit codes)
 *
 *  % java DumpBinary 0 < alice.txt
 *  1104064 bits
 *  % java TextCompressor - < alice.txt | java DumpBinary 0
 *  480760 bits
 *  = 43.54% compression ratio!
 ******************************************************************************/

import java.util.HashMap;

/**
 *  The {@code TextCompressor} class provides static methods for compressing
 *  and expanding natural language through textfile input.
 *
 *  @author Zach Blick, Tyler Hinkie
 */
public class TextCompressor {

    private static void compress() {
        String[] words = {"the", "and", "i", "to", "of", "a",
                "you", "my", "in", "that", "is", "not", "with", "me", "it", "alice"};
        HashMap<String, Integer> common = new HashMap<>();
        for (int i = 0; i < words.length; i++) common.put(words[i], i + 1);

        BinaryStdOut.write(words.length, 4);

        int x = 0;
        int sLength;
        for (String s : words) {
            sLength = s.length();
            while (x < sLength) {
                BinaryStdOut.write(s.charAt(x), 8);
                x++;
            }
            BinaryStdOut.write(0, 8);
        }


        BinaryStdOut.write();
        String input = BinaryStdIn.readString();
        int length = input.length();
        String indexing = input;
        int current = 0;
        int end = input.indexOf(" ");

        while (end < length) {
            if (common.get(input.substring(current, end)) == null) {
                // write each char value out and then a 0;
            }
            else {
                // write corresponding value of word into the file
            }
        }


        BinaryStdOut.close();
    }

    private static void expand() {

        // TODO: Complete the expand() method

        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
