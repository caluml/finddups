package finddups.handler;

import java.io.File;
import java.util.Map;
import java.util.Set;

public interface DuplicateFileHandler {

  void handleDuplicates(Map.Entry<String, Set<File>> duplicates);
}
