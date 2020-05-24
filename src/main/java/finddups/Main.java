package finddups;

import finddups.checksum.Sha256Checksummer;
import finddups.files.FileFinder;
import finddups.handler.DeleteNewestDuplicateFileHandler;
import finddups.handler.DisplayDuplicateFilesHandler;
import finddups.handler.DuplicateFileHandler;
import finddups.logic.DupeFinder;
import finddups.output.ConsoleOutputter;
import finddups.output.Outputter;

import java.io.File;
import java.util.Map;
import java.util.Set;

public class Main {

  public static void main(final String[] args) {
    // TODO: Use Apache CLI
    final long minFileLength = Long.parseLong(args[0]);
    final String path = args[1];

    final Outputter outputter = new ConsoleOutputter();

    final DuplicateFileHandler duplicateFileHandler;
    if (args.length == 3 && "delete-newest-dupes".equals(args[2])) {
      //noinspection HardcodedFileSeparator
      duplicateFileHandler = new DeleteNewestDuplicateFileHandler(outputter, "/tmp/delete.sh");
    } else {
      duplicateFileHandler = new DisplayDuplicateFilesHandler(outputter);
    }

    final FileFinder fileFinder = new FileFinder(outputter);
    final DupeFinder dupeFinder = new DupeFinder(new Sha256Checksummer(), outputter);

    final DuplicateFinder duplicateFinder = new DuplicateFinder(fileFinder, dupeFinder, outputter);

    final FindDuplicatesRequest findDuplicatesRequest = new FindDuplicatesRequest(minFileLength, path);
    final FindDuplicatesResponse findDuplicatesResponse = duplicateFinder.findDuplicates(findDuplicatesRequest);

    // Handle duplicates
    for (final Map.Entry<String, Set<File>> duplicates : findDuplicatesResponse.getResultMap()) {
      duplicateFileHandler.handleDuplicates(duplicates);
    }
  }
}
