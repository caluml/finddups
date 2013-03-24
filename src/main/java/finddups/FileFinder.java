package finddups;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FileFinder {

    private final Outputter outputter;

    /**
     * The files found
     */
    private final List<File> files = new ArrayList<File>();

    public FileFinder(final Outputter outputter) {
        this.outputter = outputter;
    }

    /**
     * Finds files in the given directory over the specified size
     * 
     * @param dir the directory
     * @param minBytes the minimum file size
     */
    public void findFiles(final File dir, final int minBytes) {
        if (!dir.isDirectory()) {
            // "Returns null if this abstract pathname does not denote a directory..."
            this.outputter.outputError(dir + " is not a directory!");
            return;
        }
        final File[] entries = dir.listFiles();
        if (entries == null) {
            // "Returns null ... if an I/O error occurs."
            this.outputter.outputError(dir + " entries is null!");
            return;
        }

        for (final File entry : entries) {
            try {
                if (!isSymlink(entry)) {
                    if (entry.isFile()) {
                        if (entry.length() >= minBytes) {
                            this.files.add(entry);
                        }
                    } else if (entry.isDirectory()) {
                        if (entry.canRead()) {
                            findFiles(entry, minBytes);
                        } else {
                            this.outputter.outputError("Can't read directory " + entry);
                        }
                    } else {
                        this.outputter.outputError("Skipping " + entry
                                + " as it's not a file or dir");
                    }
                } else {
                    this.outputter.outputError("Skipping " + entry + " as it's a symlink");
                }
            } catch (final NullPointerException e) {
                // Some file operations can return null, so rather than test for them everywhere,
                // catch here, and carry on.
                this.outputter.outputError("Null pointer exception processing " + entry + " in "
                        + dir + "\n" + exceptionToString(e));
            }
        }
    }

    /**
     * Converts Exceptions to Strings. Taken from
     * http://stackoverflow.com/questions/1149703/stacktrace-to-string-in-java
     * 
     * @param e the exception
     * @return the stackstrace as a String
     */
    private String exceptionToString(final Exception e) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
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