package finddups;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Main {

    private static int minBytes;

    public static void main(final String[] args) {
        try {
            if (args.length != 2) {
                System.err.println("Usage: <dir> <minbytes>");
                System.exit(1);
            }

            minBytes = Integer.parseInt(args[1]);
            System.out.println("Finding files over " + minBytes + " bytes in " + args[0]);

            final FileFinder fileFinder = new FileFinder();
            final List<File> files = fileFinder.recurseDir(new File(args[0]), minBytes);

            System.out.println("Found " + files.size() + " files over " + minBytes + " bytes.");
            findDuplicates(files);
        } catch (final RuntimeException e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * We have a list of files, and sort them by size. We now just look down the list making another
     * list of files which are the same size, and when we have that we check them all for
     * duplication.
     * 
     * @param files
     */
    private static void findDuplicates(final List<File> files) {
        // Sort them by size
        System.out.print("Sorting... ");
        Collections.sort(files, new FileSizeComparator());
        System.out.println("Sorted.");
        long previousLength = Long.MAX_VALUE;
        final List<File> sameSize = new ArrayList<File>();
        final DuplicateFinder duplicateFinder = new DuplicateFinder();
        // Go through the list...
        System.out.println("Searching for duplicates.");
        for (final File file : files) {
            if (file.length() == previousLength) {
                // Add them to the a list for checking
                sameSize.add(file);
            } else {
                // We're now looking at a differently sized file
                duplicateFinder.checkFiles(sameSize);
                sameSize.clear();
                sameSize.add(file);
            }
            previousLength = file.length();
        }
        System.out.println("Total bytes duplicated : " + duplicateFinder.getDuplicatedBytes());
    }
}

class FileSizeComparator implements Comparator<File> {

    @Override
    public int compare(final File first, final File second) {
        return (int) (second.length() - first.length());
    }
}