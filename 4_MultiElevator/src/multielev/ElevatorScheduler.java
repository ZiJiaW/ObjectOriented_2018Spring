package multielev;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.*;

public class ElevatorScheduler extends ALS_Scheduler implements Runnable{
    /* @OVERVIEW: schedule requests in request queue;
     * @INHERIT: None
     * @INVARIANT: None
     * */
    public static boolean endScheduler = false;
    private RequestQueue requestQueue;
    private PriorityBlockingQueue<Elevator> workLoadQueue;
    private Elevator[] elvs;
    private Floor floor;
    private PrintWriter writer;
    DecimalFormat df;
    public ElevatorScheduler(PrintWriter _writer, Floor _floor, Elevator[] _elvs, RequestQueue _requestQueue)
    {
        /* @REQUIRES: None;
         * @MODIFIES: this;
         * @EFFECTS: this.writer = _writer;
         *           this.floor = _floor;
         *           this.elvs = _elvs;
         *           this.requestQueue = _requestQueue;
         *           this.workLoadQueue = new PriorityBlockingQueue<Elevator>(3, new Comparator<Elevator>() {
         *                public int compare(Elevator e1, Elevator e2) {
         *                   return e1.WorkLoad()-e2.WorkLoad();
         *               }
         *           });
         *           this.df = new DecimalFormat("0.0");
         * */
        writer = _writer;
        floor = _floor;
        elvs = _elvs;
        requestQueue = _requestQueue;
        workLoadQueue = new PriorityBlockingQueue<Elevator>(3, new Comparator<Elevator>() {
            @Override
            public int compare(Elevator e1, Elevator e2) {
                return e1.WorkLoad()-e2.WorkLoad();
            }
        });
        df = new DecimalFormat("0.0");
    }
    
    public void run()
    {
        /* @REQUIRES: None;
         * @MODIFIES: this;
         * @EFFECTS: this.endScheduler = true;
         * */
        while(true)
        {
            Request r = requestQueue.Poll();
            if(r == null && ElevatorSystem.endFlag) break;
            if(r == null) continue;
            for(Request req : requestQueue.queue)
            {
                if(req.Type() == ReqType.ER && elvs[req.ElevatorId()-1].IsSame(req.DstFloor()))
                {
                    long curTime = new Date().getTime();
                    writer.println("#" + curTime + ":SAME [" + req.StringWithoutPar() + "," + df.format((curTime - ElevatorSystem.startTime) / 1000) + "]");
                    requestQueue.queue.remove(req);
                }
                else if(floor.IsSame(req.DstFloor(), req.Direction()))
                {
                    long curTime = new Date().getTime();
                    writer.println("#" + curTime + ":SAME [" + req.StringWithoutPar() + "," + df.format((curTime - ElevatorSystem.startTime) / 1000) + "]");
                    requestQueue.queue.remove(req);
                }
            }
            if(r.Type() == ReqType.ER)
            {
                long curTime = new Date().getTime();
                Elevator e = elvs[r.ElevatorId()-1];
                if(e.IsSame(r.DstFloor()))
                    writer.println("#" + curTime + ":SAME [" + r.StringWithoutPar() + "," + df.format((curTime - ElevatorSystem.startTime) / 1000) + "]");
                else
                    e.GetRequest(r);
            }
            else
            {
                if(floor.IsSame(r.DstFloor(), r.Direction()))
                {
                    long curTime = new Date().getTime();
                    writer.println("#" + curTime + ":SAME [" + r.StringWithoutPar() + "," + df.format((curTime - ElevatorSystem.startTime) / 1000) + "]");
                }
                else
                {
                    workLoadQueue.clear();
                    while(!elvs[0].CanRespond(r) && !elvs[1].CanRespond(r) && !elvs[2].CanRespond(r)) {}
                    for(Elevator elv : elvs)
                    {
                        if(elv.CanPickaback(r))
                            workLoadQueue.offer(elv);
                    }
                    if(!workLoadQueue.isEmpty())
                    {
                        workLoadQueue.peek().GetRequest(r);
                    }
                    else
                    {
                        for(Elevator elv : elvs)
                        {
                            if(elv.CanRespond(r))
                                workLoadQueue.offer(elv);
                        }
                        if(!workLoadQueue.isEmpty())
                        {
                            workLoadQueue.peek().GetRequest(r);
                        }
                    }
                }
            }
        }
        endScheduler = true;
        ElevatorSystem.countDownLatch.countDown();
    }
}
