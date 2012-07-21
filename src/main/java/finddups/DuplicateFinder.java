package finddups;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class DuplicateFinder {

    public void checkFiles(final List<File> sameSize) {
        for (int i = 0; i < (sameSize.size() - 1); i++) {
            final File firstFile = sameSize.get(i);
            final File secondFile = sameSize.get(i + 1);
            if (!firstFile.canRead()) {
                System.err.println("Can't read " + firstFile);
                return;
            }
            if (!secondFile.canRead()) {
                System.err.println("Can't read " + secondFile);
                return;
            }
            if (isSame(firstFile, secondFile)) {
                System.out.println(firstFile.getAbsolutePath() + " = "
                        + secondFile.getAbsolutePath());
            }
        }
    }

    private boolean isSame(final File firstFile, final File secondFile) {
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
}