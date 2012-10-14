package finddups;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * This is a command-line application which will find duplicate files.
 * 
 * @author calum
 */
public class Main {

    private static int minBytes;

    /**
     * boolean to control debug output. Run with DEBUG=true java -jar ... to enable. Not intended
     * for general usage.
     */
    static boolean DEBUG;

    /**
     * Usage: java -jar finddups.jar 10000 /data/path/ /another/path
     * 
     * @param args the arguments to use
     */
    public static void main(final String[] args) {
        try {
            if (args.length < 2) {
                System.err.println("Usage: <minbytes> <dir1> [<dir2>] ... ");
                System.exit(1);
            }
            final Outputter outputter = new SysoutOutputter();
            finddups(args, outputter);
        } catch (final RuntimeException e) {
            e.printStackTrace(System.err);
        }
    }

    static void finddups(final String[] args, final Outputter outputter) {
        // Default visibility for testing
        if ("true".equals(System.getenv("DEBUG"))) {
            Main.DEBUG = true;
        }
        minBytes = Integer.parseInt(args[0]);
        final String[] dirs = Arrays.copyOfRange(args, 1, args.length);
        outputter.output(String.format("Finding files over %d bytes in %s",
                Integer.valueOf(minBytes), Arrays.toString(dirs)));

        final FileFinder fileFinder = new FileFinder(outputter);
        final long start = System.currentTimeMillis();
        for (final String dir : dirs) {
            fileFinder.findFiles(new File(dir), minBytes);
        }
        final List<File> files = fileFinder.getFiles();
        outputter.output(String.format("Found %d files over %d bytes in %d ms.",
                Integer.valueOf(files.size()), Integer.valueOf(minBytes),
                Long.valueOf(System.currentTimeMillis() - start)));

        final DuplicateFinder duplicateFinder = new DuplicateFinder(outputter);
        duplicateFinder.findDuplicates(files);
    }
}