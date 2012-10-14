package finddups;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class MainTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void Messages_output_are_as_expected() throws IOException {
        final RecordingOutputter outputter = new RecordingOutputter();

        final File dir = this.temp.newFolder();
        final File firstDir = FileUtils.mkdir(dir, "one");
        final File secondDir = FileUtils.mkdir(firstDir, "two");
        final File thirdDir = FileUtils.mkdir(secondDir, "three");

        final File a = new File(thirdDir, "a");
        final File b = new File(thirdDir, "b");
        FileUtils.writeContent(a, "test");
        FileUtils.writeContent(b, "test");

        final String testDir = dir.toString();
        Main.finddups(new String[] { "0", testDir }, outputter);

        final String expectedMessage1 = String.format("4 %s/one/two/three/a = %s/one/two/three/b",
                testDir, testDir);
        final String expectedMessage2 = String.format("4 %s/one/two/three/b = %s/one/two/three/a",
                testDir, testDir);

        try {
            assertEquals(expectedMessage1, outputter.getMessages().get(5));
        } catch (final AssertionError e) {
            // It's possible the files are listed in the second order
            assertEquals(expectedMessage2, outputter.getMessages().get(5));
        }
        assertEquals("Bytes read to check    : 10", outputter.getMessages().get(6));
        assertEquals("Total bytes duplicated : 4", outputter.getMessages().get(7));
    }
}
