package finddups;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DuplicateFinderTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void Identical_files_are_identical() throws IOException {
        final DuplicateFinder duplicateFinder = new DuplicateFinder();
        final File dir = this.temp.newFolder();
        final File a = new File(dir, "a");
        final File b = new File(dir, "b");
        final FileOutputStream aos = new FileOutputStream(a);
        aos.write("test".getBytes("UTF-8"));
        aos.flush();
        aos.close();
        final FileOutputStream bos = new FileOutputStream(b);
        bos.write("test".getBytes("UTF-8"));
        bos.flush();
        bos.close();

        assertTrue("Files should be the same", duplicateFinder.isSame(a, b));
    }

    @Test
    public void Different_files_are_different() throws IOException {
        final DuplicateFinder duplicateFinder = new DuplicateFinder();
        final File dir = this.temp.newFolder();
        final File a = new File(dir, "a");
        final File b = new File(dir, "b");
        final FileOutputStream aos = new FileOutputStream(a);
        aos.write("test".getBytes("UTF-8"));
        aos.flush();
        aos.close();
        final FileOutputStream bos = new FileOutputStream(b);
        bos.write("XXXX".getBytes("UTF-8"));
        bos.flush();
        bos.close();

        assertFalse("Files should be different", duplicateFinder.isSame(a, b));
    }

}
