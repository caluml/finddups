package finddups.files;

import java.io.File;
import java.util.List;

public class FileFindResult {

  private final List<File> foundFiles;
  private final long mills;

  public FileFindResult(final List<File> foundFiles,
                        final long mills) {
    this.foundFiles = foundFiles;
    this.mills = mills;
  }

  public List<File> getFoundFiles() {
    return foundFiles;
  }

  public long getMills() {
    return mills;
  }
}
