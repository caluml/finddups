package finddups.output;

/**
 * Implementation of Outputter which outputs to System.out and System.err
 * 
 * @author calum
 */
@SuppressWarnings("UseOfSystemOutOrSystemErr")
public class ConsoleOutputter implements Outputter {

    @Override
    public void output(final String message) {
        System.out.println(message);
    }

    @Override
    public void outputError(final String message) {
        System.err.println(message);
    }

}
