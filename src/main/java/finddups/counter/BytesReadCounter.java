package finddups.counter;

public class BytesReadCounter {

  private long bytesRead;

  private void add(final long length) {
    this.bytesRead = this.bytesRead + length;
  }

  public long getBytesRead() {
    return bytesRead;
  }
}
