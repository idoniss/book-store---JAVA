package bgu.spl.mics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;

public class FutureTest {
    private Future f;

    @Before
    public void setUp() throws Exception {
        f = new Future();
    }

    @After
    public void tearDown() throws Exception {
        f = null;
    }

    @Test
    //@post: future has been resolved.
    public void get() {
        assertFalse(f.get()==null);
    }

    @Test
    public void resolve() {
        f.resolve(true);
        assertTrue(f.isDone() == true);
        assertEquals(f.get(), true);
    }
    //no pre or post conditions for this method
    @Test
    public void get1() {
        assertEquals(f.get(100, TimeUnit.MILLISECONDS), null);
        f.resolve(true);

        assertEquals(f.get(100, TimeUnit.MILLISECONDS), true);
    }
}