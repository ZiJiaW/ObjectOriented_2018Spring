package sys;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
/**
 * @OVERVIEW:notate state of taxis;
 * @INHERIT:None;
 * @INVARIANT:None;
 * */
class State
{
    public static int STOP = 0;
    public static int SERVE = 1;
    public static int WFS = 2;
    public static int PICK = 3;
}
/**
 * @OVERVIEW:Simulate taxi movement, they move in state of {STOP,SERVE,WFS,PICK} to tackle requests;
 * @INHERIT:None;
 * @INVARIANT:this.pos>=0&&this.pos<6400;
 * */
public class Taxi implements Runnable{
    private static long startTime = gv.getTime();
    private int pos;
    private int prePos;
    private int id;
    private int state;
    private int credit;
    private int count;
    private long curTime;
    
    private Request task;
    private int curDest;// current destination
    private String curPath;
    private int curIndex;
    
    private TaxiGUI gui;
    private Map map;
    /**
     * @REQUIRES:map!=null&&id>=0&&id<100&&gui!=null;
     * @MODIFIES:this;
     * @EFFECTS:this.map==map;
     * this.id == id;
     * this.gui==gui;
     * this.pos==new Random().nextInt(6400);
     * this.prePos == pos;
     * this.state == State.WFS;
     * this.count == 40;
     * this.curTime == startTime;
     * this.credit == 0;
     */
    Taxi(Map map, int id, TaxiGUI gui)
    {
        this.map = map;
        this.id = id;
        this.gui = gui;
        
        pos = new Random().nextInt(6400);
        prePos = pos;
        state = State.WFS;
        count = 40;
        curTime = startTime;
        credit = 0;
    }
    /**
     * @REQUIRES:None;
     * @MODIFIES:this.curTime;this.state;Main.countDownLatch;
     * @EFFECTS:this.curTime == endTime;
     * this.state == State.WFS;
     * Main.countDownLatch.countDown();
     */
    public void run()
    {
        while(true)
        {
            if(state == State.STOP)
            {
                gui.SetTaxiStatus(id, new Point(pos/80, pos%80), State.STOP);
                try {
                    long sleepTime = 1000+curTime-gv.getTime();
                    if(sleepTime > 0)
                        Thread.sleep(1000+curTime-gv.getTime());
                    else
                        Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                count = 40;
                curTime+=1000;
                state = State.WFS;
            }
            else if(state == State.WFS)
            {
                count--;
                if(count == 0) {// time to stop for 1s
                    state = State.STOP;
                } else {
                    try {
                        long sleepTime = 500+curTime-gv.getTime();// it may be negative due to low efficiency of computing
                        if(sleepTime > 0)
                            Thread.sleep(sleepTime);
                        else
                            Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    curTime+=500;
                    driveRandomly();
                    if(task != null) {// get a request
                        curDest = task.getStart();// pick this customer
                        curPath = map.getShortestPath(pos, curDest);
                        curIndex = 0;
                        state = State.PICK;
                    }
                    else if(Main.sysEnd && Main.schEnd)// end
                    {
                        break;
                    }
                }
            }
            else if(state == State.PICK)
            {
                if(curIndex == curPath.length())
                {
                    gui.SetTaxiStatus(id, new Point(pos/80, pos%80), 0);// stop for 1s
                    System.out.println(this.toString() + " arrives at ("+pos/80+","+pos%80+") for "+task);
                    
                    task.addEnter();
                    task.recordInfo("It arrives at ("+pos/80+","+pos%80+") to pick at "+OutFuncs.df.format(gv.getTime()/100.0)+"\n");
                    
                    try {Thread.sleep(1000);} catch(Exception e) {}
                    curTime+=1000;
                    curDest = task.getEnd();
                    curIndex = 0;
                    curPath = map.getShortestPath(pos, curDest);
                    state = State.SERVE;
                }
                else
                {// update path
                    curPath = map.getShortestPath(pos, curDest);
                    curIndex = 0;
                    try {
                        long sleepTime = 500+curTime-gv.getTime();// it may be negative due to low efficiency of computing
                        if(sleepTime > 0)
                            Thread.sleep(sleepTime);
                        else
                            Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    curTime+=500;
                    driveTo(curPath.charAt(curIndex++));
                }
            }
            else if(state == State.SERVE)
            {
                if(curIndex == curPath.length())
                {
                    //System.out.println("+3");
                    credit+=3;
                    System.out.println(this+" finishes "+task);
                    
                    task.addEnter();
                    task.recordInfo("It finishes task at ("+pos/80+","+pos%80+") at "+OutFuncs.df.format(gv.getTime()/100.0));
                    
                    OutFuncs.printRecord(task);
                    task = null;
                    state = State.STOP;
                }
                else
                {   // update path if edge changed
                    curPath = map.getShortestPath(pos, curDest);
                    curIndex = 0;
                    try {
                        long sleepTime = 500+curTime-gv.getTime();// it may be negative due to low efficiency of computing
                        if(sleepTime > 0)
                            Thread.sleep(sleepTime);
                        else
                            Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    curTime+=500;
                    driveTo(curPath.charAt(curIndex++));
                }
            }
        }
        Main.countDownLatch.countDown();
    }
    /**
     * @REQUIRES:None;
     * @MODIFIES:this.pos;this.gui;this.map;
     * @EFFECTS:this.pos == map.edges[\old(this.pos)].getAdj().minFlowOne();
     * gui.SetTaxiStatus(this.id, new Point(pos/80,pos%80),this.state);
     * this.map.addFlow(prePos, pos);
     * @THREAD_EFFECTS:\locked();
     */
    private synchronized void driveRandomly()
    {
        prePos = pos;
        ArrayList<Integer> adj = map.edges[pos].getAdj();
        int next = -1;
        for(int i:adj)
        {
            if(next == -1 || map.flow[pos][i] < map.flow[pos][next]) {
                next = i;
            }
            else if(map.flow[pos][i] == map.flow[pos][next])
            {
                if(new Random().nextInt(10) > 4)
                    next = i;
            }
        }
        if(next != -1) {
            map.addFlow(pos, next);
            int instate = state == State.PICK ? 1 : state;
            gui.SetTaxiStatus(id, new Point(next/80, next%80), instate);
            pos = next;
        }
    }
    /**
     * @REQUIRES:direction == 'U'||direction == 'D'||direction == 'L'||direction == 'R';
     * @MODIFIES:this.pos; this.gui; this.map;
     * @EFFECTS: direction == 'U'==>this.pos == \old(this).pos - 80;
     * direction == 'D'==>this.pos == \old(this).pos + 80;
     * direction == 'L'==>this.pos == \old(this).pos - 1;
     * direction == 'R'==>this.pos == \old(this).pos + 1;
     * gui.SetTaxiStatus(this.id, new Point(pos/80,pos%80),this.state);
     * this.map.addFlow(prePos, pos);
     * @THREAD_EFFECTS:\locked();
     */
    private synchronized void driveTo(char direction)
    {
        prePos = pos;
        switch(direction) {
        case 'U':
            pos-=80; break;
        case 'D':
            pos+=80; break;
        case 'L':
            pos-=1; break;
        case 'R':
            pos+=1; break;
        }
        map.addFlow(prePos, pos);
        int instate = state == State.PICK ? 1 : state;
        gui.SetTaxiStatus(id, new Point(pos/80, pos%80), instate);
        task.recordPos("---->("+pos/80+","+pos%80+") at "+OutFuncs.df.format(gv.getTime()/100.0));
    }
    /**
     * @REQUIRES:None;
     * @MODIFIES:this.credit;
     * @EFFECTS:this.credit == \old(this).credit + 1;
     */
    public synchronized void grab()
    {
        credit++;
    }
    /**
     * @REQUIRES:r != null;
     * @MODIFIES:this.task;
     * @EFFECTS:this.task == r;
     */
    public synchronized void dispose(Request r)
    {
        task = r;
        r.recordInfo("Taxi #"+id+" carries Request #"+r.getId()+" at "+OutFuncs.df.format(gv.getTime()/100.0));
        r.recordInfo(" in ("+pos/80+","+pos%80+")");
        r.addEnter();
    }
    
//++++++++++++++++++++++++++++++++++++++++++++++setter and getter+++++++++++++++++++++++++++++++++++++++++++1s
    public synchronized int getId() {return id;}
    public synchronized Point getPosition() {return new Point(pos/80, pos%80);}
    public synchronized long getTime() {return curTime - startTime;}
    public synchronized int getCredit() {return credit;}
    public synchronized int getState() {return state;}
    public synchronized Request getTask() {return task;}
    public String toString() {return "Taxi #"+id;}
    
    public synchronized void setState(int state) {this.state = state;}
    public synchronized void setPosition(int loc) {this.pos = loc;}
    public synchronized void setCredit(int credit) {this.credit = credit;}
    /**
     * @REQUIRES:None;
     * @MODIFIES:None;
     * @EFFECTS:\result==this.pos>=0&&this.pos<6400;
     */
    public boolean repOk()
    {
        return this.pos>=0&&this.pos<6400;
    }
}
