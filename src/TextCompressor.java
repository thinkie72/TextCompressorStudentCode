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

/**
 *  The {@code TextCompressor} class provides static methods for compressing
 *  and expanding natural language through textfile input.
 *
 *  @author Zach Blick, Tyler Hinkie
 */
public class TextCompressor {

    private static void compress() {
        TST codes = new TST();
        for (int i = 0; i < 256; i++) {
            codes.insert("" + (char) i, i);
        }

        codes.insert("π", 256);

        int currentCode = 257;
        final int MAX_CODE = 4096;


//        read data into String text
        String text = BinaryStdIn.readString();
//                index = 0
        int length = text.length();
        int index = 0;
        String prefix;
        int code;
        int next;
//        while index < text.length:
        while (index < length) {
            prefix = codes.getLongestPrefix(text, index);
//        prefix = longest coded word that matches text @ index

//        write out that code
            BinaryStdOut.write(codes.lookup(prefix), 12);
//        if possible, look ahead to the next character
            next = index + prefix.length();
//        append that character to prefix
            if (next < length) {
                if (currentCode < MAX_CODE) {
                    codes.insert(prefix + text.charAt(next), currentCode);
                    currentCode++;
                }
            }

            index += prefix.length();
//        associate prefix with the next code (if available)
//        index += prefix.length
//        write out EOF and close
        }

        BinaryStdOut.write(codes.lookup("π"), 12);

        BinaryStdOut.close();
    }

    private static void expand() {
        String[] prefixes = new String[4096];

        for (int i = 0; i < 256; i++) {
            prefixes[i] = "" + (char) i;
        }

        int currentCode = 257;
        int maxCode = 4096;

        String prefix;
        String nextPrefix;
        int code = BinaryStdIn.readInt(12);
        int nextCode = BinaryStdIn.readInt(12);

        while (!BinaryStdIn.isEmpty()) {
            prefix = prefixes[code];
            BinaryStdOut.write(prefix);
            if (prefixes[nextCode] == null) prefixes[nextCode] = prefix + prefix.charAt(0);
            if (currentCode < maxCode) {
                prefixes[currentCode] = prefix + prefixes[nextCode];
                currentCode++;
            }
            code = nextCode;
            if (code != 256) nextCode = BinaryStdIn.readInt(12);
        }

        BinaryStdOut.write(prefixes[currentCode]);

        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
