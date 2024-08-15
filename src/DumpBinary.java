/******************************************************************************
 *  Compilation:  javac DumpBinary.java
 *  Execution:    java BinaryDump n < file
 *  Dependencies: BinaryStdIn.java
 *
 *  Reads in a binary file and writes out the bits, n per line.
 *
 *  % java DumpBinary 60 < genomeTest.txt
 * 010000010101010001000001010001110100000101010100010001110100
 * 001101000001010101000100000101000111010000110100011101000011
 * 010000010101010001000001010001110100001101010100010000010100
 * 011101000001010101000100011101010100010001110100001101010100
 * 010000010100011101000011
 * 264 bits
 ******************************************************************************/

/**
 *  The {@code DumpBinary} class provides a client for displaying the contents
 *  of a binary file in binary.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *  @author Zach Blick
 */
public class DumpBinary {
    /**
     * Reads in a sequence of bytes from standard input and writes
     * them to standard output in binary, k bits per line,
     * where k is given as a command-line integer (defaults
     * to 16 if no integer is specified); also writes the number
     * of bits.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        int bitsPerLine = 16;
        if (args.length == 1) {
            bitsPerLine = Integer.parseInt(args[0]);
        }

        int count;
        for (count = 0; !BinaryStdIn.isEmpty(); count++) {
            if (bitsPerLine == 0) {
                BinaryStdIn.readBoolean();
                continue;
            }
            else if (count != 0 && count % bitsPerLine == 0) System.out.println();
            if (BinaryStdIn.readBoolean()) System.out.print(1);
            else                           System.out.print(0);
        }
        if (bitsPerLine != 0) System.out.println();
        System.out.println(count + " bits");
    }
}