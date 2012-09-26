package finddups;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DuplicateFinderTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void Identical_files_are_identical() throws IOException {
        final RecordingOutputter outputter = new RecordingOutputter();
        final DuplicateFinder duplicateFinder = new DuplicateFinder(outputter);
        final File dir = this.temp.newFolder();
        final File a = new File(dir, "a");
        final File b = new File(dir, "b");
        FileUtils.writeContent(a, "test");
        FileUtils.writeContent(b, "test");

        assertTrue("Files should be the same", duplicateFinder.isSame(a, b));
    }

    @Test
    public void Different_files_are_different() throws IOException {
        final RecordingOutputter outputter = new RecordingOutputter();
        final DuplicateFinder duplicateFinder = new DuplicateFinder(outputter);
        final File dir = this.temp.newFolder();
        final File a = new File(dir, "a");
        final File b = new File(dir, "b");
        FileUtils.writeContent(a, "test");
        FileUtils.writeContent(b, "XXXX");

        assertFalse("Files should be different", duplicateFinder.isSame(a, b));
    }

}
