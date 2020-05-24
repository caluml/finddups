package finddups.files;

public class FileFindRequest {

  private final String path;
  private final long minFileLength;

  public FileFindRequest(final String path,
                         final long minFileLength) {
    this.path = path;
    this.minFileLength = minFileLength;
  }

  public String getPath() {
    return path;
  }

  public long getMinFileLength() {
    return minFileLength;
  }
}
