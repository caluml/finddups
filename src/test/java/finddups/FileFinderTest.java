package finddups;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileFinderTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void Filefinder_finds_files_correctly() throws IOException {
        final RecordingOutputter outputter = new RecordingOutputter();
        final FileFinder fileFinder = new FileFinder(outputter);

        final File dir = this.temp.newFolder();
        final File firstDir = FileUtils.mkdir(dir, "one");
        final File secondDir = FileUtils.mkdir(firstDir, "two");
        final File thirdDir = FileUtils.mkdir(secondDir, "three");

        final File a = new File(thirdDir, "a");
        final File b = new File(thirdDir, "b");
        FileUtils.writeContent(a, "test");
        FileUtils.writeContent(b, "XXXX");

        fileFinder.findFiles(dir, 0);

        assertEquals("Two files should have been found.", 2, fileFinder.getFiles().size());
    }
}
