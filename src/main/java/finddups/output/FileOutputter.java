package finddups.output;

import java.io.*;

public class FileOutputter implements Outputter {

  private final File file;
  private final boolean append;

  public FileOutputter(final String path,
                       final boolean append) {
    this.append = append;
    final File file = new File(path);
    // Checks?
    this.file = file;
  }

  @Override
  public void output(final String message) {
    // Inefficient
    try (final FileOutputStream fileOutputStream = new FileOutputStream(file, append);
         final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
         final BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {

      bufferedWriter.write(message + "\n");
    } catch (final IOException e) {
      throw new RuntimeException("Error writing " + message + " to " + file, e);
    }
  }

  @Override
  public void outputError(final String message) {
    output(message);
  }
}
