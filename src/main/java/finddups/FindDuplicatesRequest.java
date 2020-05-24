package finddups;

public class FindDuplicatesRequest {

  private final long minFileLength;
  private final String[] paths;

  public FindDuplicatesRequest(final long minFileLength,
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
