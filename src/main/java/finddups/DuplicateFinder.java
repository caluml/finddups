package finddups;

import finddups.files.FileFindRequest;
import finddups.files.FileFindResult;
import finddups.files.FileFinder;
import finddups.logic.DupeFinder;
import finddups.output.LargeNumberFormatter;
import finddups.output.Outputter;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class DuplicateFinder {

  private final FileFinder fileFinder;
  private final DupeFinder dupeFinder;
  private final Outputter outputter;


  public DuplicateFinder(final FileFinder fileFinder,
                         final DupeFinder dupeFinder,
                         final Outputter outputter) {
    this.fileFinder = fileFinder;
    this.dupeFinder = dupeFinder;
    this.outputter = outputter;
  }


  /**
   * Main entry point
   */
  public FindDuplicatesResponse findDuplicates(final FindDuplicatesRequest findDuplicatesRequest) {

    // Find files
    outputter.output("Finding files...");
    final FileFindRequest fileFindRequest = new FileFindRequest(findDuplicatesRequest.getMinFileLength(), findDuplicatesRequest.getPaths());
    final FileFindResult fileFindResult = fileFinder.findFiles(fileFindRequest);
    outputter.output("Found " + fileFindResult.getFoundFiles().size() + " files >= " +
      LargeNumberFormatter.format(findDuplicatesRequest.getMinFileLength()) + " bytes in " + fileFindResult.getMills() + " ms");

    final Map<Long, List<File>> filesByLength = groupByLength(fileFindResult.getFoundFiles());
    final Map<Long, List<File>> duplicateFilesByLength = removeUniqueLengthFiles(filesByLength);

    // I assume people will want to find the largest duplicate files first.
    final Map<Long, List<File>> duplicateFilesSortedByLength = sortByLength(duplicateFilesByLength);
    int candidates = 0;
    for (final List<File> fileList : duplicateFilesSortedByLength.values()) {
      candidates = candidates + fileList.size();
    }
    outputter.output("  Filtered down to " + candidates + " candidates...");


    outputter.output("Finding duplicates from " + candidates + " candidates...");
    final long startFindDups = System.currentTimeMillis();
    final List<Map.Entry<String, Set<File>>> duplicateMaps = dupeFinder.checkAllDupes(duplicateFilesSortedByLength);
    outputter.output("Found " + duplicateMaps.size() + " sets of duplicates in " + (System.currentTimeMillis() - startFindDups) + " ms");

    return new FindDuplicatesResponse(duplicateMaps);
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
    return new TreeMap<>(duplicateFilesByLength).descendingMap();
  }

}
