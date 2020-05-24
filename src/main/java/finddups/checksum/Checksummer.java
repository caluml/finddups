package finddups.checksum;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public abstract class Checksummer {

  private final Map<File, String> cache = new HashMap<>();

  public String getChecksum(final File file) {
    // To minimise as much IO as possible, we crudely cache checksums
    if (cache.containsKey(file)) {
      return cache.get(file);
    }

    try (final FileInputStream fileInputStream = new FileInputStream(file);
         final BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {

      final String checksum = calculateChecksum(bufferedInputStream);
      cache.put(file, checksum);
      return checksum;
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }


  public abstract String calculateChecksum(InputStream inputStream);
}
