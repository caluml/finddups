package finddups.handler;

import finddups.output.Outputter;
import finddups.output.FileOutputter;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DeleteNewestDuplicateFileHandler implements DuplicateFileHandler {

  private final Outputter outputter;

  private final Outputter fileOutputter;

  public DeleteNewestDuplicateFileHandler(final Outputter outputter,
                                          final String path) {
    this.outputter = outputter;
    fileOutputter = new FileOutputter(path, true);
    //noinspection HardcodedFileSeparator
    fileOutputter.output("#!/bin/bash");
    fileOutputter.output("");
    outputter.output("COMMAND='rm -fv'");
    fileOutputter.output("COMMAND='rm -fv'");
  }

  @Override
  public void handleDuplicates(final Map.Entry<String, Set<File>> duplicates) {
    outputter.output("# " + duplicates.getKey());
    fileOutputter.output("echo " + duplicates.getKey());

    final List<File> oldestFirst = duplicates.getValue().stream()
      .sorted(Comparator.comparing(File::lastModified))
      .collect(Collectors.toList());

    outputter.output("#Retaining :" + oldestFirst.get(0));
    fileOutputter.output("echo Retaining oldest: " + oldestFirst.get(0));
    for (final File file : oldestFirst.subList(1, oldestFirst.size())) {
      outputter.output("${COMMAND} '" + file.getAbsolutePath() + "'");
      fileOutputter.output("${COMMAND} '" + file.getAbsolutePath() + "'");
    }
  }
}
