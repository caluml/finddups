package finddups.checksum;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.InputStream;

public class Sha256Checksummer extends Checksummer {

  @Override
  public String calculateChecksum(final InputStream inputStream) {
    try {
      return DigestUtils.sha512Hex(inputStream);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }
}
