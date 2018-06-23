package elev;
import static org.junit.Assert.*;

import org.junit.*;

public class ElevatorTest {
    Elevator elv;
    private static int testCount = 0;
    @BeforeClass
    public static void beforeClass() {
        System.out.println("##### ElevatorTest starts! #####");
    }
    @AfterClass
    public static void afterClass() {
        System.out.println("##### ElevatorTest ends! #####");
    }
    @Before
    public void before() {
        elv = new Elevator();
        testCount++;
        System.out.println("test "+testCount+" starts!");
    }
    @After
    public void after() {
        elv = null;
        System.out.println("test "+testCount+" ends!");
    }
    @Test
    public void testElevator() {
        assertTrue(elv.floor()==1&&elv.time()==0&&elv.state()==0);
    }
    @Test
    public void testUp() {
        int floor = elv.floor();
        double time = elv.time();
        elv.up(2);
        assertTrue(elv.floor()==floor+2 && elv.time()-time-1<=0.00001 && elv.state() == 1 && elv.repOk());
    }

    @Test
    public void testDown() {
        int floor = elv.floor();
        double time = elv.time();
        elv.up(7);
        assertTrue(elv.floor()==floor+7 && elv.time()-time-3.5<=0.00001 && elv.state() == 1 && elv.repOk());
        elv.down(4);
        assertTrue(elv.floor()==floor+3 && elv.time()-time-5.5<=0.00001 && elv.state() == 2 && elv.repOk());
    }

    @Test
    public void testOpen() {
        int floor = elv.floor();
        double time = elv.time();
        elv.open();
        assertTrue(elv.floor() == floor && elv.time()-time-1 <= 0.00001 && elv.state() == 0 && elv.repOk());
    }

    @Test
    public void testSetState() {
        elv.SetState(0);
        assertTrue(elv.state() == 0 && elv.repOk());
        elv.SetState(1);
        assertTrue(elv.state() == 1 && elv.repOk());
        elv.SetState(2);
        assertTrue(elv.state() == 2 && elv.repOk());
    }

    @Test
    public void testTimeFly() {
        elv.timeFly(12.5);
        assertTrue(elv.time() - 12.5 <= 0.00001 && elv.repOk());
        elv.timeFly(-10);
        assertTrue(elv.time() - 2.5 <= 0.00001 && elv.repOk());
    }

    @Test
    public void testTime() {
        elv.timeFly(11.5);
        assertTrue(elv.time() - 11.5 <= 0.00001 && elv.repOk());
    }

    @Test
    public void testState() {
        assertTrue(elv.state() == 0 && elv.repOk());
        elv.SetState(1);
        assertTrue(elv.state() == 1 && elv.repOk());
        elv.SetState(2);
        assertTrue(elv.state() == 2 && elv.repOk());
    }

    @Test
    public void testFloor() {
        assertTrue(elv.floor() == 1 && elv.repOk());
        elv.up(5);
        assertTrue(elv.floor() == 6 && elv.repOk());
    }

    @Test
    public void testToString() {
        assertTrue(elv.toString().equals("(1,STILL,0.0)")&&elv.repOk());
        elv.up(3);
        assertTrue(elv.toString().equals("(4,UP,1.5)")&&elv.repOk());
        elv.open();
        assertTrue(elv.toString().equals("(4,STILL,2.5)")&&elv.repOk());
        elv.down(1);
        assertTrue(elv.toString().equals("(3,DOWN,3.0)")&&elv.repOk());
    }

    @Test
    public void testRepOk() {
        assertTrue(elv.repOk());
        elv.SetState(1);
        assertTrue(elv.repOk());
        elv.SetState(2);
        assertTrue(elv.repOk());
        elv.SetState(3);
        assertTrue(!elv.repOk());
    }
}
