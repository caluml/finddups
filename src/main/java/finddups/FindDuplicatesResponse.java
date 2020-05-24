package finddups;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FindDuplicatesResponse {

  private final List<Map.Entry<String, Set<File>>> resultMap;

  public FindDuplicatesResponse(final List<Map.Entry<String, Set<File>>> resultMap) {
    this.resultMap = resultMap;
  }

  public List<Map.Entry<String, Set<File>>> getResultMap() {
    return resultMap;
  }
}
