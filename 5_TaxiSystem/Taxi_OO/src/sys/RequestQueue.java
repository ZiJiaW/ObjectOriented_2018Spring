package sys;

import java.awt.Point;
import java.util.concurrent.LinkedBlockingQueue;
/**
 * @OVERVIEW:Save all unprocessed requests and query taxis to grab them;
 * @INHERIT:None;
 * @INVARIANT:this.queue != null;
 */
public class RequestQueue {
    private LinkedBlockingQueue<Request> queue;
    private static int id_num = 0;// for id given
    /**
     * @REQUIRES:None;
     * @MODIFIES:this.queue;
     * @EFFECTS:this.queue == new LinkedBlockingQueue<Request>();
     */
    RequestQueue()
    {
        queue = new LinkedBlockingQueue<Request>();
    }
    /**
     * @REQUIRES:None;
     * @MODIFIES:this.queue;
     * @EFFECTS:\result == this.queue.poll();
     * @THREAD_EFFECTS:\locked();
     */
    public synchronized Request poll() {return queue.poll();}
    /**
     * @REQUIRES:None;
     * @MODIFIES:None;
     * @EFFECTS:\result == this.queue.peek();
     * @THREAD_EFFECTS:\locked();
     */
    public synchronized Request peek() {return queue.peek();}
    /**
     * @REQUIRES:None;
     * @MODIFIES:None;
     * @EFFECTS:\result == this.queue.isEmpty();
     * @THREAD_EFFECTS:\locked();
     */
    public synchronized boolean isEmpty() {return queue.isEmpty();}
    
    /**
     * @REQUIRES:r != null;
     * @MODIFIES:this.queue; this.id_num;
     * @EFFECTS:\all Request it; this.queue.contains(it); !it.equals(r) ==>
     * this.queue.offer(r) && r.setId(this.id_num) && this.id_num == \old(this).id_num +; 
     * @THREAD_EFFECTS: \locked();
     */
    public synchronized void add(Request r)
    {
        for(Request it:queue) if(it.equals(r)) return;// same request
        queue.offer(r);
        r.setId(id_num++);
        OutFuncs.printReq(r);// only print valid requests
    }
    
    /**
     * @REQUIRES:taxis != null;
     * @MODIFIES:this.queue;
     * @EFFECTS:\all Request r; this.queue.contains(r); 
     * (\all Taxi t; taxis.contains(t); t.state == WFS && t.pos.x-r.start.x <= 2 && t.pos.y - r.start.y <= 2)
     *  ==>(r.addTaxi(t) ==> t.grab()); 
     */
    public void questTaxis(Taxi[] taxis)
    {
        for(Request r:queue)
        {
            for(int i = 0; i < 100; ++i)
            {
                Point pos = taxis[i].getPosition();
                if(taxis[i].getState() == State.WFS &&
                   Math.abs(pos.x-r.getStart()/80)<=2 &&
                   Math.abs(pos.y-r.getStart()%80)<=2)
                {
                    if(r.addTaxi(taxis[i]))
                        taxis[i].grab();
                }
            }
        }
    }
    /**
     * @REQUIRES:None;
     * @MODIFIES:None;
     * @EFFECTS:this.queue != null;
     */
    public boolean repOk()
    {
        return this.queue != null;
    }
}
