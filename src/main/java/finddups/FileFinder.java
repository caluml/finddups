package finddups;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileFinder {

    private final List<File> files = new ArrayList<File>();

    public List<File> recurseDir(final File dir, final int minBytes) {
        final File[] entries = dir.listFiles();

        for (final File entry : entries) {
            if (!isSymlink(entry)) {
                if (entry.isFile()) {
                    if (entry.length() >= minBytes) {
                        this.files.add(entry);
                    }
                } else if (entry.isDirectory()) {
                    if (entry.canRead()) {
                        recurseDir(entry, minBytes);
                    } else {
                        System.err.println("Can't read directory " + entry);
                    }
                } else {
                    System.err.println("Skipping " + entry + " as it's not a file or dir");
                }
            } else {
                System.err.println("Skipping " + entry + " as it's a symlink");
            }
        }
        return this.files;
    }

    private boolean isSymlink(final File file) {
        if (file == null) {
            throw new NullPointerException("File must not be null");
        }
        try {
            File canon;
            if (file.getParent() == null) {
                canon = file;
            } else {
                final File canonDir = file.getParentFile().getCanonicalFile();
                canon = new File(canonDir, file.getName());
            }
            return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}