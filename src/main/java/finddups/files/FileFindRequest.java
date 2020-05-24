package finddups.files;

public class FileFindRequest {

  private final long minFileLength;
  private final String[] paths;

  public FileFindRequest(final long minFileLength,
                         final String... paths) {
    this.minFileLength = minFileLength;
    this.paths = paths;
  }

  public long getMinFileLength() {
    return minFileLength;
  }

  public String[] getPaths() {
    return paths;
  }
}
