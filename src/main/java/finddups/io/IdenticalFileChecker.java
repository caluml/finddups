package finddups.io;

import java.io.*;

public class IdenticalFileChecker {

  private final InputStreamChecker inputStreamChecker = new InputStreamChecker();

  public boolean areFilesIdentical(final File file1,
                                   final File file2) {
    if (file1.length() != file2.length()) {
      return false;
    }

    try (final BufferedInputStream bufferedInputStream1 = new BufferedInputStream(new FileInputStream(file1));
         final BufferedInputStream bufferedInputStream2 = new BufferedInputStream(new FileInputStream(file2))) {

      return inputStreamChecker.areInputStreamsIdentical(file1.length(), bufferedInputStream1, bufferedInputStream2);
    } catch (final IOException e) {
      //noinspection CallToPrintStackTrace
      e.printStackTrace();
      return false;
    }
  }
}
