package finddups.logic;

import finddups.output.Outputter;
import finddups.checksum.Checksummer;
import finddups.io.IdenticalFileChecker;

import java.io.File;
import java.util.*;

public class DupeFinder {

  private final IdenticalFileChecker identicalFileChecker = new IdenticalFileChecker();

  private final Checksummer checksummer;

  private final Outputter outputter;

  public DupeFinder(final Checksummer checksummer,
                    final Outputter outputter) {
    this.checksummer = checksummer;
    this.outputter = outputter;
  }

  public List<Map.Entry<String, Set<File>>> checkAllDupes(final Map<Long, List<File>> filesByLength) {
    final List<Map.Entry<String, Set<File>>> result = new ArrayList<>();
    long bytesWasted = 0;

    for (final Map.Entry<Long, List<File>> entry : filesByLength.entrySet()) {
      final Long length = entry.getKey();
      final List<File> filesOfSameLength = entry.getValue();
      outputter.output("  Checking files with size " + length + " : " + filesOfSameLength);

      final Map<String, Set<File>> identicalFilesMap = getIdenticalFiles(filesOfSameLength);

      for (final Map.Entry<String, Set<File>> identicalFilesEntry : identicalFilesMap.entrySet()) {
        final String checksum = identicalFilesEntry.getKey();
        final Set<File> identicalFiles = identicalFilesEntry.getValue();

        outputter.output("    Found " + identicalFiles);

        result.add(new AbstractMap.SimpleEntry<>(checksum, identicalFiles));

        bytesWasted = bytesWasted + (length * (identicalFiles.size() - 1));
      }
    }

    outputter.output("Bytes wasted by duplication: " + bytesWasted);
    return result;
  }

  private Map<String, Set<File>> getIdenticalFiles(final List<File> files) {
    files.sort(Comparator.comparing(File::getAbsolutePath));
    final Map<String, Set<File>> identicalFilesMap = new HashMap<>();

    for (int i = 0; i < files.size(); i++) {
      for (int j = i + 1; j < files.size(); j++) {
        final File file1 = files.get(i);
        final File file2 = files.get(j);

        if (identicalFileChecker.areFilesIdentical(file1, file2)) {
          final String checksum = checksummer.getChecksum(file1);
          final Set<File> identicalFileSet = identicalFilesMap.getOrDefault(checksum, new HashSet<>());
          identicalFileSet.add(file1);
          identicalFileSet.add(file2);
          identicalFilesMap.put(checksum, identicalFileSet);
        }
      }
    }

    return identicalFilesMap;
  }
}
