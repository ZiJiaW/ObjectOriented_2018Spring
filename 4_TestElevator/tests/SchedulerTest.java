package elev;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SchedulerTest {

    @Test
    public void testScheduler() {
        Request[] rqs = new Request[8];
        Elevator e = new Elevator();
        Scheduler as = new Scheduler(rqs, e);
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
        rqs[4] = new Request("(FR,2,UP,1)");
        rqs[5] = new Request("(ER,7,3)");
        rqs[6] = new Request("(ER,9,4)");
        rqs[7] = new Request("(FR,3,DOWN,25)");
        for(int i=0;i<8;++i) rqs[i].Parse();
        Scheduler s = new Scheduler(rqs, e);
        s.schedule();
        assertTrue(e.floor()==3&&e.time()==29&&s.repOk());
    }

}
