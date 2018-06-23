package sys;

import java.awt.Point;
/**
 * @OVERVIEW:Scheduler disposes all the requests in RequestQueue to taxis, if there's no taxi to carry, discard it;
 * @INHERIT:None;
 * @INVARIANT:this.requests != null && this.taxis != null && this.gui != null;
 */
public class Scheduler implements Runnable{
    private RequestQueue requests;
    private TaxiGUI gui;
    private Taxi[] taxis;
    /**
     * @REQUIRES rs != null && g != null && ts != null:
     * @MODIFIES:this;
     * @EFFECTS:this.requests == rs;
     * this.gui == g;
     * this.taxis == ts;
     */
    Scheduler(RequestQueue rs, TaxiGUI g, Taxi[] ts)
    {
        requests = rs;
        gui = g;
        taxis = ts;
    }
    /**
     * @REQUIRES:None;
     * @MODIFIES:this.requests; this.gui; Main.countDownLacth;
     * @EFFECTS:this.requests.size == 0;
     * \all Request r; \old(this).requests.contains(r);
     * this.gui.RequestTaxi(new Point(r.getStart()/80, r.getStart()%80), new Point(r.getEnd()/80, r.getEnd()%80))
     * && r.canDispose() ==> r.getCarrier().dispose(r);
     * Main.countDownLacth.countDown();
     */
    public void run()
    {
        while(true)
        {
            if(requests.isEmpty() && Main.sysEnd) break;
            long curTime = gv.getTime();
            while(!requests.isEmpty() && curTime - requests.peek().getTime() >= 7500)
            {
                Request r = requests.poll();
                gui.RequestTaxi(new Point(r.getStart()/80, r.getStart()%80), new Point(r.getEnd()/80, r.getEnd()%80));
                if(r.canDispose())
                {
                    r.getCarrier().dispose(r);
                    System.out.println(r+" succeeds to get taxi #"+r.getCarrier().getId());
                }
                else
                {
                    System.out.println(r+" fails to get a taxi!");
                }
            }
            requests.questTaxis(taxis);
        }
        Main.schEnd = true;
        Main.countDownLatch.countDown();
    }
    /**
     * @REQUIRES:None;
     * @MODIFIES:None;
     * @EFFECTS:\result== this.requests != null && this.taxis != null && this.gui != null;
     */
    public boolean repOk()
    {
        return this.requests != null && this.taxis != null && this.gui != null;
    }
}
