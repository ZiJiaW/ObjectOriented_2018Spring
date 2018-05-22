package multielev;

import java.io.PrintWriter;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.*;
import java.text.DecimalFormat;

public class RequestQueue {
    /* @OVERVIEW: save all requests for dispatching;
     * @INHERIT: None
     * @INVARIANT: None
     * */
    private PrintWriter writer;
    public LinkedBlockingQueue<Request> queue;
    DecimalFormat df;
    public RequestQueue(PrintWriter _writer)
    {
        /* @REQUIRES: _writer != null;
         * @MODIFIES: this;
         * @EFFECTS: this.writer = _writer;
         *           this.queue = new LinkedBlockingQueue<Request>();
         *           this.df = new DecimalFormat("0.0");
         * */
        writer = _writer;
        queue = new LinkedBlockingQueue<Request>();
        df = new DecimalFormat("0.0");
    }
    public synchronized void Offer(String[] requests, long time)
    {
        /* @REQUIRES: None;
         * @MODIFIES: this;
         * @EFFECTS: \all int i && 0 <= i < requests.length() && i < 10 && requests[i].IsValid()
         *           ==> this.queue.size() = \old(this).queue.size() + 1 && this.queue.contains(requests[i]);
         * @THREAD_EFFECTS: \locked()
         * */
        int count = 0;
        
        for(String request : requests)
        {
            Request req = new Request(request, time);
            if(count < 10 && req.IsValid())
                queue.offer(req);
            else
                writer.println(new Date().getTime() + ":INVALID [" + request + ", " + df.format((time - ElevatorSystem.startTime) / 1000) + "]");
            count++;
        }
    }
    public synchronized Request Poll()
    {
        /* @REQUIRES: None;
         * @MODIFIES: this;
         * @EFFECTS: \result = this.queue.poll();
         *           this.queue.size() = \old(this).queue.size() - 1;
         *           !this.queue.contains(\old(this).queue.peek());
         * @THREAD_EFFECTS: \locked()
         * */
        return queue.poll();
    }
}
