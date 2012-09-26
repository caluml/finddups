package finddups;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of Outputter for testing which records output rather than displaying it
 * 
 * @author calum
 */
public class RecordingOutputter implements Outputter {

    private final List<String> messages = new ArrayList<String>();
    private final List<String> errors = new ArrayList<String>();

    @Override
    public void output(final String message) {
        this.messages.add(message);
    }

    @Override
    public void outputError(final String message) {
        this.errors.add(message);
    }

    public List<String> getMessages() {
        return Collections.unmodifiableList(this.messages);
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(this.errors);
    }

}
