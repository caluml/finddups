package finddups.files;

import finddups.output.Outputter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileFinder {

  private final Outputter outputter;

  public FileFinder(final Outputter outputter) {
    this.outputter = outputter;
  }

  public FileFindResult findFiles(final FileFindRequest fileFindRequest) {
    if (fileFindRequest.getPaths().length != 1) throw new RuntimeException("Multiple paths not supported yet");
    final long start = System.currentTimeMillis();
    final List<File> foundFiles = new ArrayList<>();
    walk(new File(fileFindRequest.getPaths()[0]), foundFiles, fileFindRequest.getMinFileLength());

    return new FileFindResult(foundFiles, System.currentTimeMillis() - start);
  }

  private void walk(final File dir,
                    final List<File> foundFiles,
                    final long minFileLength) {

    final File[] entries = dir.listFiles();
    if (entries == null) {
      outputter.outputError("Error (link?) listing entries for " + dir);
      return;
    }

    for (final File entry : entries) {
      if (entry.isDirectory()) {
        if (entry.canRead()) {
          outputter.output("  Checking directory: " + entry);
          walk(entry, foundFiles, minFileLength);
        }
      } else if (entry.isFile()) {
        if (entry.length() >= minFileLength) {
          foundFiles.add(entry);
        }
      } else {
        outputter.outputError("  Not file or dir: " + entry);
      }
    }
  }
}