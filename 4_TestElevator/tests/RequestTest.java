package elev;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RequestTest {
    private static int testCount = 0;
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        System.out.println("##### RequestTest starts! #####");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        System.out.println("##### RequestTest ends! #####");
    }

    @Before
    public void setUp() throws Exception {
        testCount++;
        System.out.println("test "+testCount+" starts!");
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("test "+testCount+" ends!");
    }

    @Test
    public void testRequest() {
        Request r = new Request("1122");
        assertTrue(r.repOk());
    }
    @Test
    public void testParse() {
        Request r = new Request("END==-");r.Parse();
        assertTrue(!r.IsValid()&&r.repOk());
        r = new Request("(FR,1,UP,2");r.Parse();
        assertTrue(!r.IsValid()&&r.repOk());
        r = new Request("(RR,1,UP,3)");r.Parse();
        assertTrue(!r.IsValid()&&r.repOk());
        r = new Request("(FR,1,UP,3,5)");r.Parse();
        assertTrue(!r.IsValid()&&r.repOk());
        r = new Request("(FR,3,UPP,2)");r.Parse();
        assertTrue(!r.IsValid()&&r.repOk());
        r = new Request("(FR,3,UP,2)");r.Parse();
        assertTrue(r.IsValid()&&r.repOk()&&r.floor()==3&&r.time()==2&&r.type()&&r.direction());
        r = new Request("(FR,4,DOWN,5)");r.Parse();
        assertTrue(r.IsValid()&&r.repOk()&&r.floor()==4&&r.time()==5&&r.type()&&!r.direction());
        r = new Request("(ER,4,3,5)");r.Parse();
        assertTrue(!r.IsValid()&&r.repOk());
        r = new Request("(ER,6,4)");r.Parse();
        assertTrue(r.IsValid()&&r.repOk()&&r.floor()==6&&r.time()==4&&!r.type());
        r = new Request("(ER,0,1)");r.Parse();
        assertTrue(!r.IsValid()&&r.repOk());
        r = new Request("(ER,15,1)");r.Parse();
        assertTrue(!r.IsValid()&&r.repOk());
        r = new Request("(FR,1,DOWN,1)");r.Parse();
        assertTrue(!r.IsValid()&&r.repOk());
        r = new Request("(FR,10,UP,3)");r.Parse();
        assertTrue(!r.IsValid()&&r.repOk());
        r = new Request("(ER,0,-1)");r.Parse();
        assertTrue(!r.IsValid()&&r.repOk());
        r = new Request("(ER,0,2147483649)");r.Parse();
        assertTrue(!r.IsValid()&&r.repOk());
        r = new Request("(ER,214748364956,5)");r.Parse();
        assertTrue(!r.IsValid()&&r.repOk());
    }

    @Test
    public void testIsValid() {
        Request r;
        r = new Request("(ER,4,3,5)");r.Parse();
        assertTrue(!r.IsValid()&&r.repOk());
        r = new Request("(ER,7,3)");r.Parse();
        assertTrue(r.IsValid()&&r.repOk());
    }

    @Test
    public void testType() {
        Request r = new Request("(ER,7,3)");r.Parse();
        assertTrue(!r.type()&&r.repOk());
        r = new Request("(FR,7,UP,3)");r.Parse();
        assertTrue(r.type()&&r.repOk());
    }

    @Test
    public void testDirection() {
        Request r;
        r = new Request("(FR,7,UP,3)");r.Parse();
        assertTrue(r.direction()&&r.repOk());
        r = new Request("(FR,7,DOWN,3)");r.Parse();
        assertTrue(!r.direction()&&r.repOk());
    }

    @Test
    public void testFloor() {
        Request r;
        r = new Request("(FR,7,UP,3)");r.Parse();
        assertTrue(r.floor() == 7&&r.repOk());
        r = new Request("(ER,2,3)");r.Parse();
        assertTrue(r.floor() == 2&&r.repOk());
    }

    @Test
    public void testTime() {
        Request r;
        r = new Request("(FR,7,UP,3)");r.Parse();
        assertTrue(r.time()==3&&r.repOk());
    }

    @Test
    public void testIsRightStart() {
        Request r;
        r = new Request("(FR,1,UP,0)");r.Parse();
        assertTrue(r.IsRightStart()&&r.repOk());
        r = new Request("(FR,7,UP,3)");r.Parse();
        assertTrue(!r.IsRightStart()&&r.repOk());
    }

    @Test
    public void testToString() {
        Request r;
        r = new Request("(FR,7,UP,3)");r.Parse();
        assertTrue(r.toString().equals("[FR,7,UP,3]")&&r.repOk());
        r = new Request("(FR,7,DOWN,3)");r.Parse();
        assertTrue(r.toString().equals("[FR,7,DOWN,3]")&&r.repOk());
        r = new Request("(ER,7,3)");r.Parse();
        assertTrue(r.toString().equals("[ER,7,3]")&&r.repOk());
    }

    @Test
    public void testEqualsRequest() {
        Request r1, r2;
        r1 = new Request("(FR,1,UP,0)"); r1.Parse();
        r2 = new Request("(FR,1,UP,0)"); r2.Parse();
        assertTrue(r1.equals(r2));
        r1 = new Request("(FR,1,UP,2)"); r1.Parse();
        assertTrue(!r1.equals(r2));
    }

    @Test
    public void testRepOk() {
        Request r = new Request("(FR,1,UP,0)"); r.Parse();
        assertTrue(r.repOk());
        r = new Request("(FR,6,UP,10)");r.Parse();
        assertTrue(r.repOk());
        r = new Request("(ER,5,3)");r.Parse();
        assertTrue(r.repOk());
    }
}