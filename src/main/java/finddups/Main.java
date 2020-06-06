package finddups;

import finddups.checksum.Sha256Checksummer;
import finddups.files.FileFinder;
import finddups.handler.DeleteNewestDuplicateFileHandler;
import finddups.handler.DisplayDuplicateFilesHandler;
import finddups.handler.DuplicateFileHandler;
import finddups.logic.DupeFinder;
import finddups.output.ConsoleOutputter;
import finddups.output.Outputter;
import org.apache.commons.cli.*;

import java.io.File;
import java.util.Map;
import java.util.Set;

public class Main {

  public static void main(final String[] args) throws Exception {
    final Options options = new Options();

    options.addOption(Option.builder("dir").hasArg().argName("path").desc("Directory (can be repeated)").required().build());
    options.addOption(Option.builder("min").longOpt("min-bytes").hasArg().argName("bytes").desc("Minimum file size to consider (default 0)").type(Long.class).build());
    options.addOption(Option.builder("del").longOpt("delete-newest-dupes").desc("Delete newest dupes").build());

    CommandLine parsed = null;
    try {
      final CommandLineParser parser = new DefaultParser();
      parsed = parser.parse(options, args);
    } catch (MissingOptionException e) {
      final HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("finddups", options);
      System.exit(1);
    }

    final Outputter outputter = new ConsoleOutputter();

    final DuplicateFileHandler duplicateFileHandler;
    if (parsed.hasOption("del")) {
      //noinspection HardcodedFileSeparator
      duplicateFileHandler = new DeleteNewestDuplicateFileHandler(outputter, "/tmp/delete.sh");
    } else {
      duplicateFileHandler = new DisplayDuplicateFilesHandler(outputter);
    }

    final FileFinder fileFinder = new FileFinder(outputter);
    final DupeFinder dupeFinder = new DupeFinder(new Sha256Checksummer(), outputter);

    final DuplicateFinder duplicateFinder = new DuplicateFinder(fileFinder, dupeFinder, outputter);

    final FindDuplicatesRequest findDuplicatesRequest = new FindDuplicatesRequest(Long.parseLong(parsed.getOptionValue("min", "0")), parsed.getOptionValues("dir"));
    final FindDuplicatesResponse findDuplicatesResponse = duplicateFinder.findDuplicates(findDuplicatesRequest);

    // Handle duplicates
    for (final Map.Entry<String, Set<File>> duplicates : findDuplicatesResponse.getResultMap()) {
      duplicateFileHandler.handleDuplicates(duplicates);
    }
  }
}
