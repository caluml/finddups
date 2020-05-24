package finddups.io;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamChecker {

  public boolean areInputStreamsIdentical(final long length,
                                          final InputStream inputStream1,
                                          final InputStream inputStream2) {

    try {
      // Exit as soon as a difference is found
      for (int i = 0; i < length; i++) {
        if (inputStream1.read() != inputStream2.read()) {
          return false;
        }
      }
    } catch (final IOException e) {
      return false;
    }

    return true;
  }
}
