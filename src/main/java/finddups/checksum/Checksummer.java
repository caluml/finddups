package finddups.checksum;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static finddups.Constants.FILE_INPUT_BUFFER_SIZE;

public abstract class Checksummer {

  private final Map<File, String> cache = new HashMap<>();

  public String getChecksum(final File file) {
    // To minimise as much IO as possible, we crudely cache checksums
    if (cache.containsKey(file)) {
      System.err.println("Found checksum in cache for " + file.getAbsolutePath());
      return cache.get(file);
    }

    try (final FileInputStream fileInputStream = new FileInputStream(file);
         final BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream, FILE_INPUT_BUFFER_SIZE)) {

      final String checksum = calculateChecksum(bufferedInputStream);
      cache.put(file, checksum);
//      System.err.println("Calculated " + checksum + " for " + file.getAbsolutePath());
      return checksum;
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }


  public abstract String calculateChecksum(InputStream inputStream);
}
