package nl.tudelft.bw4t.server.util;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;

import nl.tudelft.bw4t.server.environment.BW4TEnvironment;
import nl.tudelft.bw4t.server.model.zone.BlocksArea;
import nl.tudelft.bw4t.server.model.zone.DropZone;
import nl.tudelft.bw4t.server.model.zone.Area;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;

/** Tests the functions in the utility class AreaLocator. */
@RunWith(Parameterized.class)
public class AreaLocatorTest {

    @Mock private static BlocksArea BLOCKSROOM = mock(BlocksArea.class);
    @Mock private static DropZone DROPZONE = mock(DropZone.class);
    @Mock private static BW4TEnvironment env = mock(BW4TEnvironment.class);
    
    /** A minor offset is done on certain elements in order to fulfill requirements of the definition of insideness. */
    private static double OFFSET = 1E-5;
    private static Context<Object> context = new DefaultContext<>();
    
    private double x;
    private double y;
    private Area expectedOutAt;
    private Area expectedOutFor;
    
    @BeforeClass
    public static void setUp() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method method = BW4TEnvironment.class.getDeclaredMethod("setInstance", BW4TEnvironment.class);
        method.setAccessible(true);
        method.invoke(null, env);
        when(env.getContext()).thenReturn(context);

        context.add(BLOCKSROOM);
        context.add(DROPZONE);

        when(BLOCKSROOM.getBoundingBox()).thenReturn(new Rectangle(0, 0, 1, 1));
        when(DROPZONE.getBoundingBox()).thenReturn(new Rectangle(10, 10, 1, 1));
    }
    
    /**
     * Set the values to use for the current test.
     * @param x - The x coordinate to search with.
     * @param y - The y coordinate to search with.
     * @param expectedOutAt - The expected output of the getAreaAt function.
     * @param expectedOutFor - The expected output of the getAreaFor function.
     */
    public AreaLocatorTest(double x, double y, Area expectedOutAt, Area expectedOutFor){
        this.x = x;
        this.y = y;
        this.expectedOutAt = expectedOutAt;
        this.expectedOutFor = expectedOutFor;
    }

    /** We test the getAreaAt method for correctness. */
    @Test
    public void getAreaAtTest() {
        Area result = AreaLocator.getAreaAt(x, y);
        assertTrue((result == null && expectedOutAt == null)
                || (result != null && result.equals(expectedOutAt)));
    }

    /** We test the getAreaFor method for correctness. */
    @Test
    public void getAreaForTest() {
        Area result = AreaLocator.getAreaFor(x, y);
        assertTrue((result == null && expectedOutFor == null)
                || (result != null && result.equals(expectedOutFor)));
    }
    
    /** @return The list of parameters to use for testing */
    @Parameters(name = "{index} - {0},{1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0,             0,              BLOCKSROOM, BLOCKSROOM},
                {0,             1 - OFFSET,     BLOCKSROOM, BLOCKSROOM},
                {1 - OFFSET,    0,              BLOCKSROOM, BLOCKSROOM},
                {1 - OFFSET,    1 - OFFSET,     BLOCKSROOM, BLOCKSROOM},
                {0.5,           0.5,            BLOCKSROOM, BLOCKSROOM},
                {10,            10,             DROPZONE,   null},
                {10,            11 - OFFSET,    DROPZONE,   null},
                {11 - OFFSET,   10,             DROPZONE,   null},
                {11 - OFFSET,   11 - OFFSET,    DROPZONE,   null},
                {10.5,          10.5,           DROPZONE,   null},
                {0,             0 - OFFSET,     null,       null},
                {0 - OFFSET,    0,              null,       null},
                {0 - OFFSET,    0 - OFFSET,     null,       null},
                {0,             1,              null,       null},
                {1,             0,              null,       null},
                {1,             1,              null,       null}
        });
    }

}
