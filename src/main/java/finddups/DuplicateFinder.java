package finddups;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class DuplicateFinder {

    private long duplicatedBytes = 0;

    public void checkFiles(final List<File> sameSize) {
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

    public long getDuplicatedBytes() {
        return this.duplicatedBytes;
    }
}