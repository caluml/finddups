package finddups.logic;

import finddups.checksum.Checksummer;
import finddups.output.LargeNumberFormatter;
import finddups.output.Outputter;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class DupeFinder {

  private final Outputter outputter;

  private final Checksummer checksummer;

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
      outputter.output("  Checking files with size " + LargeNumberFormatter.format(length) + ":");
      filesOfSameLength.forEach(f -> outputter.output("    " + f));

      final Map<String, Set<File>> identicalFilesMap = getIdenticalFiles(filesOfSameLength);

      for (final Map.Entry<String, Set<File>> identicalFilesEntry : identicalFilesMap.entrySet()) {
        outputter.output("Found duplicates of length " + LargeNumberFormatter.format(length) + " :");
        identicalFilesEntry.getValue().forEach(f -> outputter.output(f.getAbsolutePath()));
        result.add(identicalFilesEntry);

        bytesWasted = bytesWasted + ((identicalFilesEntry.getValue().size() - 1) * length);
      }
    }

    outputter.output("Bytes wasted by duplication: " + LargeNumberFormatter.format(bytesWasted));
    return result;
  }

  private Map<String, Set<File>> getIdenticalFiles(final List<File> sameLengthFiles) {
    assertAllSameLength(sameLengthFiles);
    sameLengthFiles.sort(Comparator.comparing(File::getAbsolutePath));

    // We assume that most files of the same length will be identical, and therefore calculate the checksums first
    final Map<String, Set<File>> fileChecksums = new HashMap<>();
    for (final File file : sameLengthFiles) {
      final String checksum = checksummer.getChecksum(file);
      final Set<File> fileSet = fileChecksums.getOrDefault(checksum, new HashSet<>());
      fileSet.add(file);
      fileChecksums.put(checksum, fileSet);
    }

    return fileChecksums.entrySet().stream()
      .filter(e -> e.getValue().size() > 1)
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private void assertAllSameLength(final Collection<File> files) {
    final Set<Long> sizes = files.stream().map(File::length).collect(Collectors.toSet());
    if (sizes.size() != 1) throw new RuntimeException("Files should all be of the same length here");
  }
}
