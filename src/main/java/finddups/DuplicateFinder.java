package finddups;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DuplicateFinder {

    private long duplicatedBytes = 0;

    /**
     * We have a list of files, and sort them by size. We now just look down the list making another
     * list of files which are the same size, and when we have that we check them all for
     * duplication.
     * 
     * @param files
     */
    public void findDuplicates(final List<File> files) {
        // Sort them by size
        System.out.print("Sorting... ");
        Collections.sort(files, new FileSizeComparator());
        System.out.println("Sorted.");
        long previousLength = Long.MAX_VALUE;
        final List<File> sameSize = new ArrayList<File>();
        // Go through the list...
        System.out.println("Searching for duplicates.");
        for (final File file : files) {
            if (file.length() == previousLength) {
                // Add them to the a list for checking
                sameSize.add(file);
            } else {
                // We're now looking at a differently sized file.
                checkFiles(sameSize);
                sameSize.clear();
                sameSize.add(file);
            }
            previousLength = file.length();
        }
        System.out.println("Total bytes duplicated : " + getDuplicatedBytes());
    }

    private void checkFiles(final List<File> sameSize) {
        for (int firstNum = 0; firstNum < sameSize.size(); firstNum++) {
            final File firstFile = sameSize.get(firstNum);
            if (!firstFile.canRead()) {
                System.err.println("Can't read " + firstFile);
                return;
            }
            for (int secondNum = firstNum + 1; secondNum < sameSize.size(); secondNum++) {
                final File secondFile = sameSize.get(secondNum);
                if (!secondFile.canRead()) {
                    System.err.println("Can't read " + secondFile);
                    return;
                }
                if (isSame(firstFile, secondFile)) {
                    final long fileSize = firstFile.length();
                    this.duplicatedBytes = this.duplicatedBytes + fileSize;
                    System.out.println(fileSize + " " + firstFile.getAbsolutePath() + " = "
                            + secondFile.getAbsolutePath());
                }
            }
        }
    }

    boolean isSame(final File firstFile, final File secondFile) {
        // Default visibility for testing.
        BufferedInputStream first = null;
        BufferedInputStream second = null;
        try {
            first = new BufferedInputStream(new FileInputStream(firstFile), 4096);
            second = new BufferedInputStream(new FileInputStream(secondFile), 4096);
            int firstInt = Integer.MAX_VALUE;
            int secondInt = Integer.MAX_VALUE;
            while (firstInt != -1 && secondInt != -1) {
                firstInt = first.read();
                secondInt = second.read();
                if (firstInt != secondInt) {
                    return false;
                }
            }
            return true;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (first != null) {
                    first.close();
                }
                if (second != null) {
                    second.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    private long getDuplicatedBytes() {
        return this.duplicatedBytes;
    }
}