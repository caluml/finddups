package finddups.output;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class LargeNumberFormatter {

  private static final NumberFormat numberFormat = new DecimalFormat();

  public static String format(long l) {
    return numberFormat.format(l);
  }

}
