package elev;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ALS_SchedulerTest {
    private static int testCount = 0;
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        System.out.println("##### SchedulerTest starts! #####");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        System.out.println("##### SchedulerTest ends! #####");
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
    public void testALS_Scheduler() {
        Request[] rqs = new Request[8];
        Elevator e = new Elevator();
        ALS_Scheduler as = new ALS_Scheduler(rqs, e);
        assertTrue(as.repOk());
    }
    
    @Test
    public void testSchedule() {
        Request[] rqs = new Request[8];
        Elevator e = new Elevator();
        rqs[0] = new Request("(FR,1,UP,0)");
        rqs[1] = new Request("(ER,4,0)");
        rqs[2] = new Request("(ER,4,1)");
        rqs[3] = new Request("(FR,2,UP,1)");
        rqs[4] = new Request("(FR,2,UP,2)");
        rqs[5] = new Request("(ER,7,3)");
        rqs[6] = new Request("(ER,9,4)");
        rqs[7] = new Request("(FR,3,DOWN,5)");
        for(int i=0;i<8;++i) rqs[i].Parse();
        ALS_Scheduler as = new ALS_Scheduler(rqs, e);
        as.schedule();
        assertTrue(e.floor()==3 && e.time() == 13.0 && as.repOk());
        System.out.println("---------separation---------------------------");
        rqs = new Request[7];
        e = new Elevator();
        rqs[0] = new Request("(FR,1,UP,0)");
        rqs[1] = new Request("(ER,10,1)");
        rqs[2] = new Request("(FR,2,UP,8)");
        rqs[3] = new Request("(ER,5,9)");
        rqs[4] = new Request("(FR,4,DOWN,11)");
        rqs[5] = new Request("(ER,2,12)");
        rqs[6] = new Request("(ER,1,13)");
        for(int i=0;i<7;++i) rqs[i].Parse();
        as = new ALS_Scheduler(rqs, e);
        as.schedule();
        assertTrue(as.repOk()&&e.floor()==1&&e.time()==16.5);
        System.out.println("---------separation---------------------------");
        rqs = new Request[6];
        e = new Elevator();
        rqs[0] = new Request("(FR,1,UP,0)");
        rqs[1] = new Request("(ER,4,2)");
        rqs[2] = new Request("(ER,3,2)");
        rqs[3] = new Request("(FR,4,UP,3)");
        rqs[4] = new Request("(FR,2,UP,3)");
        rqs[5] = new Request("(ER,7,5)");
        for(int i=0;i<6;++i) rqs[i].Parse();
        as = new ALS_Scheduler(rqs, e);
        as.schedule();
        assertTrue(as.repOk()&&e.floor()==7&&e.time()==11.0);
        System.out.println("---------separation---------------------------");
        rqs = new Request[7];
        e = new Elevator();
        rqs[0] = new Request("(FR,1,UP,0)");
        rqs[1] = new Request("(ER,4,1)");
        rqs[2] = new Request("(FR,4,UP,2)");
        rqs[3] = new Request("(FR,2,DOWN,9)");
        rqs[4] = new Request("(ER,2,9)");
        rqs[5] = new Request("(ER,4,13)");
        rqs[6] = new Request("(FR,4,UP,13)");
        for(int i=0;i<7;++i) rqs[i].Parse();
        as = new ALS_Scheduler(rqs, e);
        as.schedule();
        assertTrue(as.repOk()&&e.floor()==4&&e.time()==15.0);
        System.out.println("---------separation---------------------------");
        rqs = new Request[10];
        e = new Elevator();
        rqs[0] = new Request("(FR,1,UP,0)");
        rqs[1] = new Request("(ER,4,1)");
        rqs[2] = new Request("(FR,7,UP,2)");
        rqs[3] = new Request("(ER,7,2)");
        rqs[4] = new Request("(ER,5,3)");
        rqs[5] = new Request("(ER,6,3)");
        rqs[6] = new Request("(ER,10,4)");
        rqs[7] = new Request("(ER,5,12)");
        rqs[8] = new Request("(FR,2,DOWN,13)");
        rqs[9] = new Request("(ER,2,13)");
        for(int i=0;i<10;++i) rqs[i].Parse();
        as = new ALS_Scheduler(rqs, e);
        as.schedule(); 
        assertTrue(as.repOk()&&e.floor()==2&&e.time()==18.0);
    }
 
    @Test
    public void testRepOk() {
        Request[] rqs = new Request[8];
        Elevator e = new Elevator();
        rqs[0] = new Request("(FR,1,UP,0)");
        rqs[1] = new Request("(ER,4,0)");
        rqs[2] = new Request("(ER,4,1)");
        rqs[3] = new Request("(FR,2,UP,1)");
        rqs[4] = new Request("(FR,2,UP,2)");
        rqs[5] = new Request("(ER,7,3)");
        rqs[6] = new Request("(ER,9,4)");
        rqs[7] = new Request("(FR,3,DOWN,5)");
        for(int i=0;i<8;++i) rqs[i].Parse();
        ALS_Scheduler as = new ALS_Scheduler(rqs, e);
        as.schedule();
        assertTrue(as.repOk());
    }
}