package finddups;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class FileUtils {

    private FileUtils() {
        // Intentionally blank
    }

    public static void writeContent(final File file, final String content)
            throws FileNotFoundException, IOException, UnsupportedEncodingException {
        final FileOutputStream aos = new FileOutputStream(file);
        aos.write(content.getBytes("UTF-8"));
        aos.flush();
        aos.close();
    }

    public static File mkdir(final File parentDir, final String subdir) {
        final File createdDir = new File(parentDir, subdir);
        createdDir.mkdir();
        return createdDir;
    }

}
