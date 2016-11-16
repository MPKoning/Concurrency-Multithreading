package data_structures;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * This is another main program of the Concurrency and Multithreading
 * programming assignment.This version is using String instead of Integer.
 */
public class Main2 {

    private static final int STRING_LENGTH = 16;

    /**
     * Generates a random string of the specified length, using the random
     * number generator specified, the characterset specified.
     *
     * @param rnd
     *            the random number generator
     * @param characters
     *            the characters to be used
     * @param length
     *            length of the resulting string
     * @return the generated string
     */
    private static String generateString(Random rnd, String characters,
            int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(rnd.nextInt(length));
        }
        return new String(text);
    }

    /**
     * Permutes an array in a predictable manner, based on a specific seed.
     *
     * @param array
     *            the array to permute.
     * @param seed
     *            the seed to use for the random number generator.
     */
    private static void permute(String[] array, long seed) {
        Random random = new Random(seed);

        for (int i = 0; i < array.length; i++) {
            int r = random.nextInt(array.length);
            String swapped = array[i];
            array[i] = array[r];
            array[r] = swapped;
        }
    }

    /**
     * Fills the specified <code>itemsToAdd</code> and
     * <code>itemsToRemove</code> arrays with pseudo-random strings, based on
     * the specified seed. There resulting arrays will not contain duplicates.
     *
     * @param itemsToAdd
     *            array to be initialized with items to add
     * @param itemsToRemove
     *            array to be initialized with items to remove (the same items
     *            as <code>ItemsToAdd</code>, but in a different order)
     * @param seed
     *            the seed
     * @param allowDuplicates
     *            whether duplicates are allowed or not
     */
    private static void createWorkData(String[] itemsToAdd,
            String[] itemsToRemove, long seed, boolean allowDuplicates) {
        Random random = new Random(seed);
        Set<String> strings = new HashSet<String>();
        for (int i = 0; i < itemsToAdd.length; i++) {
            String s = generateString(random,
                    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789",
                    STRING_LENGTH);
            if (allowDuplicates || strings.add(s)) {
                itemsToAdd[i] = s;
                itemsToRemove[i] = s;
            }
        }

        permute(itemsToRemove, seed + 1);
    }

    /**
     * This method is called when there was some error in the arguments, and
     * explains how the program should be invoked.
     */
    static void exitWithError() {
        System.out.println(
                "test_data_structures <data_structure> <nrThreads> <nrItems> <workTime> [debug]");
        System.out.println("  where:");
        System.out.printf("    <data_structure> in {%s, %s, %s, %s, %s, %s}\n",
                Main.CGL, Main.CGT, Main.FGL, Main.FGT, Main.LFL, Main.LFT);
        System.out.println("    <nrThreads> is a number > 0");
        System.out.println("    <nrItems> is a number > 0");
        System.out.println("    <workTime> is a number >= 0 (micro seconds)");
        System.out.println(
                "    [debug] can be omitted. If added as the last parameter,");
        System.out.println("            the data structure will be printed ");
        System.out.println(
                "            after adding and before removing the numbers.");
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length < 4 || args.length > 5) {
            exitWithError();
        }

        String dataStructure = args[0];
        int nrThreads = Integer.parseInt(args[1]);
        if (nrThreads < 1) {
            exitWithError();
        }

        int nrItems = Integer.parseInt(args[2]);
        if (nrItems < 1) {
            exitWithError();
        }

        if (nrItems % nrThreads != 0) {
            System.out.println("nrItems should be divisible by nrThreads");
            System.exit(1);
        }

        int workTime = Integer.parseInt(args[3]);
        if (workTime < 0) {
            exitWithError();
        }

        boolean debug = false;
        if (args.length == 5) {
            if (args[4].equalsIgnoreCase("debug")) {
                debug = true;
            } else {
                System.out.println(
                        "last argument should be 'debug', or be omitted\n");
                System.exit(1);
            }
        }

        // Create the items to be added and deleted.
        long seed = Main.computeSeed(nrThreads, nrItems, workTime);
        String[] itemsToAdd = new String[nrItems];
        String[] itemsToRemove = new String[nrItems];

        RunData<String> run = new RunData<String>(dataStructure, nrItems,
                nrThreads, itemsToAdd, itemsToRemove, workTime, debug);
        boolean mayHaveDuplicates = !dataStructure.equalsIgnoreCase(Main.LFT);

        createWorkData(itemsToAdd, itemsToRemove, seed, mayHaveDuplicates);

        run.runDataStructure();
    }
}
