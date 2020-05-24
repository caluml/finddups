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

  private final Outputter fileAppender;

  public DeleteNewestDuplicateFileHandler(final Outputter outputter,
                                          final String path) {
    this.outputter = outputter;
    fileAppender = new FileOutputter(path, true);
    //noinspection HardcodedFileSeparator
    fileAppender.output("#!/bin/bash");
    fileAppender.output("");
  }

  @Override
  public void handleDuplicates(final Map.Entry<String, Set<File>> duplicates) {
    outputter.output("# " + duplicates.getKey());
    fileAppender.output("echo " + duplicates.getKey());

    final List<File> oldestFirst = duplicates.getValue().stream()
      .sorted(Comparator.comparing(File::lastModified))
      .collect(Collectors.toList());

    outputter.output("# Retaining oldest: " + oldestFirst.get(0));
    fileAppender.output("echo Retaining oldest: " + oldestFirst.get(0));
    for (final File file : oldestFirst.subList(1, oldestFirst.size())) {
      outputter.output("rm -fv " + file);
      fileAppender.output("rm -fv " + file);
    }
  }
}
