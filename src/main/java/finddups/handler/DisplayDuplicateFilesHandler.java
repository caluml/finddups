package finddups.handler;

import finddups.output.Outputter;

import java.io.File;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

public class DisplayDuplicateFilesHandler implements DuplicateFileHandler {

  private final Outputter outputter;


  public DisplayDuplicateFilesHandler(final Outputter outputter) {
    this.outputter = outputter;
  }

  @Override
  public void handleDuplicates(final Map.Entry<String, Set<File>> duplicates) {
    outputter.output(duplicates.getKey());
    for (final File file : duplicates.getValue()) {
      outputter.output("Modified: " + Instant.ofEpochMilli(file.lastModified()) + " " + file.length() + ": " + file.getAbsolutePath());
    }
  }
}
