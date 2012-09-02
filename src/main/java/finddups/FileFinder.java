package finddups;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FileFinder {

    /**
     * The files found
     */
    private final List<File> files = new ArrayList<File>();

    /**
     * Finds files in the given directory over the specified size
     * 
     * @param dir the directory
     * @param minBytes the minimum file size
     */
    public void findFiles(final File dir, final int minBytes) {
        final File[] entries = dir.listFiles();

        for (final File entry : entries) {
            if (!isSymlink(entry)) {
                if (entry.isFile()) {
                    if (entry.length() >= minBytes) {
                        this.files.add(entry);
                    }
                } else if (entry.isDirectory()) {
                    if (entry.canRead()) {
                        findFiles(entry, minBytes);
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
    }

    /**
     * Method to detect symlinks Taken from
     * http://stackoverflow.com/questions/813710/java-1-6-determine-symbolic-links
     * 
     * @param file the file to check
     * @return true if the file is a symlink
     */
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

    public List<File> getFiles() {
        return this.files;
    }
}

class FileSizeComparator implements Comparator<File> {

    @Override
    public int compare(final File first, final File second) {
        return (int) (second.length() - first.length());
    }
}