package finddups;

import finddups.checksum.Sha256Checksummer;
import finddups.files.FileFindRequest;
import finddups.files.FileFindResult;
import finddups.files.FileFinder;
import finddups.handler.DeleteNewestDuplicateFileHandler;
import finddups.handler.DisplayDuplicateFilesHandler;
import finddups.handler.DuplicateFileHandler;
import finddups.logic.DupeFinder;
import finddups.output.ConsoleOutputter;
import finddups.output.Outputter;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class FindDups {

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


    // Find files
    final FileFinder fileFinder = new FileFinder(outputter);
    outputter.output("Finding files...");
    final FileFindResult fileFindResult = fileFinder.findFiles(new FileFindRequest(path, minFileLength));
    outputter.output("Found " + fileFindResult.getFoundFiles().size() + " files >= " + minFileLength + " bytes in " + fileFindResult.getMills() + " ms");


    // I assume people will want to find the largest duplicate files first.
    fileFindResult.getFoundFiles().sort(Comparator.comparing(File::length));


    final Map<Long, List<File>> filesByLength = groupByLength(fileFindResult.getFoundFiles());
    final Map<Long, List<File>> duplicateFilesByLength = removeUniqueLengthFiles(filesByLength);
    final Map<Long, List<File>> duplicateFilesSortedByLength = sortByLength(duplicateFilesByLength);
    int candidates = 0;
    for (final List<File> fileList : duplicateFilesSortedByLength.values()) {
      candidates = candidates + fileList.size();
    }
    outputter.output("  Filtered down to " + candidates + " candidates...");

    final DupeFinder dupeFinder = new DupeFinder(new Sha256Checksummer(), outputter);

    outputter.output("Finding duplicates from " + candidates + " candidates...");
    final long startFindDups = System.currentTimeMillis();
    final List<Map.Entry<String, Set<File>>> duplicateMaps = dupeFinder.checkAllDupes(duplicateFilesSortedByLength);
    outputter.output("Found " + duplicateMaps.size() + " sets of duplicates in " + (System.currentTimeMillis() - startFindDups) + " ms");


    // Handle duplicates
    for (final Map.Entry<String, Set<File>> duplicates : duplicateMaps) {
      duplicateFileHandler.handleDuplicates(duplicates);
    }
  }

  private static Map<Long, List<File>> groupByLength(final Collection<File> foundFiles) {
    return foundFiles.stream()
      .collect(groupingBy(File::length));
  }

  private static Map<Long, List<File>> removeUniqueLengthFiles(final Map<Long, List<File>> filesByLength) {
    return filesByLength.entrySet().stream()
      .filter(e -> e.getValue().size() > 1)
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private static Map<Long, List<File>> sortByLength(final Map<Long, List<File>> duplicateFilesByLength) {
    return new TreeMap<>(duplicateFilesByLength);
  }

}
