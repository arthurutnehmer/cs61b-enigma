package enigma;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainTest {

    @Test
    public void testReadConfig() {
        String[] args = {"/home/arthur/repo/proj1/testing/test.conf",
                         "/home/arthur/repo/proj1/testing/test.in"};
        Main.main(args);

    }

}
