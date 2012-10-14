package finddups;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DuplicateFinder {

    private final Outputter outputter;

    private long duplicatedBytes = 0;

    private long bytesChecked = 0;

    // private long md5BytesChecked = 0;

    public DuplicateFinder(final Outputter outputter) {
        this.outputter = outputter;
    }

    /**
     * We have a list of files, and sort them by size. We now just look down the list making another
     * list of files which are the same size, and when we have that we check them all for
     * duplication.
     * 
     * @param files
     */
    public void findDuplicates(final List<File> files) {
        // Sort them by size
        this.outputter.output("Sorting... ");
        final long startSort = System.currentTimeMillis();
        Collections.sort(files, new FileSizeComparator());
        this.outputter
                .output(String.format("Sorted %d files in %d ms.", Integer.valueOf(files.size()),
                        Long.valueOf(System.currentTimeMillis() - startSort)));
        long previousLength = Long.MAX_VALUE;
        final List<File> sameSize = new ArrayList<File>();
        // Go through the list...
        this.outputter.output("Searching for duplicates.");
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
        // Need to check this after the loop to avoid leaving the final group alone if the last file
        // doesn't differ in size
        checkFiles(sameSize);

        this.outputter.output("Bytes read to check    : " + this.bytesChecked);
        // outputter.output("Bytes read to check with md5s : " + this.md5BytesChecked);
        this.outputter.output("Total bytes duplicated : " + this.duplicatedBytes);
    }

    private void checkFiles(final List<File> sameSize) {
        if (sameSize.size() < 2) {
            return;
        }
        // final int numFiles = sameSize.size();
        // final long bytesToCheck = sameSize.size() * sameSize.get(0).length();
        // System.out.println("    checking : " + bytesToCheck + " bytes in " + numFiles +
        // " files");

        // TODO: Work out where the best payoff lies. Reading the MD5 requires reading the entire
        // file, but checking byte by byte might find that it's different 5 bytes in.
        // if (bytesToCheck > 10000000) {
        // compareWithMd5(sameSize);
        // }

        for (int firstNum = 0; firstNum < sameSize.size(); firstNum++) {
            final File firstFile = sameSize.get(firstNum);
            if (!firstFile.canRead()) {
                this.outputter.outputError("Can't read " + firstFile);
                return;
            }
            for (int secondNum = firstNum + 1; secondNum < sameSize.size(); secondNum++) {
                final File secondFile = sameSize.get(secondNum);
                if (!secondFile.canRead()) {
                    this.outputter.outputError("Can't read " + secondFile);
                    return;
                }
                if (isSame(firstFile, secondFile)) {
                    final long fileSize = firstFile.length();
                    this.duplicatedBytes = this.duplicatedBytes + fileSize;
                    this.outputter.output(fileSize + " " + firstFile.getAbsolutePath() + " = "
                            + secondFile.getAbsolutePath());
                }
            }
        }
    }

    boolean isSame(final File firstFile, final File secondFile) {
        // Default visibility for testing.
        final long start = System.currentTimeMillis();
        BufferedInputStream first = null;
        BufferedInputStream second = null;
        try {
            first = new BufferedInputStream(new FileInputStream(firstFile), 4096);
            second = new BufferedInputStream(new FileInputStream(secondFile), 4096);
            int firstInt = Integer.MAX_VALUE;
            int secondInt = Integer.MAX_VALUE;
            long count = 0;
            while (firstInt != -1 && secondInt != -1) {
                firstInt = first.read();
                secondInt = second.read();
                count = count + 2;
                if (firstInt != secondInt) {
                    this.bytesChecked = this.bytesChecked + count;
                    if (Main.DEBUG) {
                        System.out.println("\tInequal after " + this.bytesChecked + " bytes in "
                                + (System.currentTimeMillis() - start) + " ms");
                    }
                    return false;
                }
            }
            this.bytesChecked = this.bytesChecked + count;
            if (Main.DEBUG) {
                System.out.println("\tEqual after " + this.bytesChecked + " bytes in "
                        + (System.currentTimeMillis() - start) + " ms");
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

    // /**
    // * Method that checks for duplicates by calculating MD5s
    // *
    // * @param sameSize
    // */
    // private void compareWithMd5(final List<File> sameSize) {
    // final Map<File, String> md5s = new HashMap<File, String>();
    // for (final File file : sameSize) {
    // try {
    // md5s.put(file, DigestUtils.md5Hex(new FileInputStream(file)));
    // this.md5BytesChecked = this.md5BytesChecked + file.length();
    // } catch (final FileNotFoundException e) {
    // e.printStackTrace();
    // } catch (final IOException e) {
    // e.printStackTrace();
    // }
    // }
    // final Set<String> hashSet = new HashSet<String>(md5s.values());
    // if (hashSet.size() != md5s.size()) {
    // System.err.println("DUPLICATE FILE!!");
    // }
    // }
}