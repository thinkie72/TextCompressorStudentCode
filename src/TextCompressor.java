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
 * The {@code TextCompressor} class provides static methods for compressing
 * and expanding natural language through textfile input.
 *
 * @author Zach Blick, Tyler Hinkie
 */
public class TextCompressor {

    private static void compress() {
        // Creates TST for codes
        TST codes = new TST();
        for (int i = 0; i < 256; i++) {
            codes.insert("" + (char) i, i);
        }

        // Variable to start new code assignments
        int currentCode = 257;
        // 1 more than maximum number represented with 12 bit ASCII codes
        final int maxCode = 4096;
        final int bits = 12;

        String text = BinaryStdIn.readString();
        int length = text.length();
        int index = 0;
        String prefix;
        int next;

        while (index < length) {
            prefix = codes.getLongestPrefix(text, index);

            BinaryStdOut.write(codes.lookup(prefix), bits);

            next = index + prefix.length();

            // Insert new prefix and next character into the TST if there's space
            if (next < length && currentCode < maxCode) codes.insert(prefix + text.charAt(next), currentCode++);

            index += prefix.length();
        }

        // Write the EOF code
        BinaryStdOut.write(256, bits);

        BinaryStdOut.close();
    }

    private static void expand() {
        final int bits = 12;

        String[] prefixes = new String[4096];
        for (int i = 0; i < 256; i++) {
            prefixes[i] = "" + (char) i;
        }

        // See comment in compress()
        int currentCode = 257;
        final int maxCode = 4096;

        // Read first prefix
        String prefix = prefixes[BinaryStdIn.readInt(bits)];
        // Write first prefix
        BinaryStdOut.write(prefix);
        int nextCode;
        String nextPrefix;

        while (!BinaryStdIn.isEmpty()) {
            nextCode = BinaryStdIn.readInt(bits);

            // End if EOF code
            if (nextCode == 256) break;

            // Handle edge case where the next code doesn't exist yet
            if (prefixes[nextCode] == null) nextPrefix = prefix + prefix.charAt(0);
            else nextPrefix = prefixes[nextCode];

            BinaryStdOut.write(nextPrefix);

            // Add new code to the table if there's empty space
            if (currentCode < maxCode) prefixes[currentCode++] = prefix + nextPrefix.charAt(0);

            prefix = nextPrefix;
        }

        BinaryStdOut.close();
    }


    public static void main(String[] args) {
        if (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
