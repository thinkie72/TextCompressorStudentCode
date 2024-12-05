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
                "you", "my", "in", "that", "is", "not", "with", "me", "it"};
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
            x = 0;
        }

        String input = BinaryStdIn.readString();
        int length = input.length();
        int end = input.indexOf(" ");
        int check;
        String in;
        int startR;
        String first = input.substring(0, end);
        if (common.get(first) == null) {
            BinaryStdOut.write(8, 4);
            for (int i = 0; i < first.length(); i++) {
                BinaryStdOut.write(first.charAt(i), 8);
            }
        }
        else {
            BinaryStdOut.write(4, 4);
            BinaryStdOut.write(common.get(first), 4);

        }

        String indexing = input.substring(end + 1);
        end = indexing.indexOf(" ");

        while (end != -1) {
            in = input.substring(0, end);
            if (common.get(in) == null) {
                // write each char value out and then a 0;
                BinaryStdOut.write(0, 4);
                for (int i = 0; i < in.length(); i++) {
                    BinaryStdOut.write(in.charAt(i), 8);
                }
            } else {
                // write corresponding value of word into the file
                BinaryStdOut.write(0, 8);
                BinaryStdOut.write(common.get(in), 4);
            }

            indexing = indexing.substring(end + 1);
            end = indexing.indexOf(" ");
        }


        BinaryStdOut.close();
    }

    private static void expand() {
        int r = 4;
        int runs = BinaryStdIn.readInt(r);
        HashMap<Integer, String> common = new HashMap<>();
        r = r(r);

        StringBuilder s = new StringBuilder();
        char c;
        int i = 1;

        while (runs != 0) {
            c = BinaryStdIn.readChar();
            while (c != 0) {
                s.append(c);
                c = BinaryStdIn.readChar();
            }

            common.put(i, String.valueOf(s));
            s = new StringBuilder();
            i++;
            runs--;
        }

        r = r(r);

        r = BinaryStdIn.readInt(4);

        int check;

        while (!BinaryStdIn.isEmpty()) {
            check = BinaryStdIn.readInt(r);
            if (check == 0) {
                r = r(r);
                BinaryStdOut.write(" ");
            }
            else {
                if (rIs4(r)) BinaryStdOut.write(common.get(check));
                else BinaryStdOut.write((char) check);
            }
        }

        BinaryStdOut.close();
    }

    private static int r(int r) {
        if (r == 4) return 8;
        return 4;
    }

    private static boolean rIs4(int r) {
        return r == 4;
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
