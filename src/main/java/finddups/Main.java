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
     * Usage: java -jar finddups.jar /data/path/ 10000
     * 
     * @param args the arguments to use
     */
    public static void main(final String[] args) {
        try {
            if (args.length < 2) {
                System.err.println("Usage: <minbytes> <dir1> [<dir2>] ... ");
                System.exit(1);
            }

            minBytes = Integer.parseInt(args[0]);
            final String[] dirs = Arrays.copyOfRange(args, 1, args.length);
            System.out.println(String.format("Finding files over %d bytes in %s", minBytes,
                    Arrays.toString(dirs)));

            final FileFinder fileFinder = new FileFinder();
            final long start = System.currentTimeMillis();
            for (final String dir : dirs) {
                fileFinder.findFiles(new File(dir), minBytes);
            }
            final List<File> files = fileFinder.getFiles();
            System.out.println(String.format("Found %d files over %d bytes in %d ms.",
                    files.size(), minBytes, System.currentTimeMillis() - start));

            final DuplicateFinder duplicateFinder = new DuplicateFinder();
            duplicateFinder.findDuplicates(files);
        } catch (final RuntimeException e) {
            e.printStackTrace(System.err);
        }
    }
}