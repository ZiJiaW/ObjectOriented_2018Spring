package multielev;

import java.util.*;
import java.util.concurrent.*;
import java.io.PrintWriter;
import java.text.DecimalFormat;

public class Elevator implements Runnable{
    /* @OVERVIEW: simulate the running of elevators;
     * @INHERIT: None;
     * @INVARIANT: None;
     * */
    private PrintWriter writer;
    private Floor floor;
    private int workLoad;
    private int id;
    private int dstFloor;
    private int curFloor;
    private long lastStopTime;// 电梯上次从停止到运动的时间
    
    private boolean[] erLight;
    
    private volatile State state;
    private volatile Direction direction;
    
    private PriorityBlockingQueue<Request> upQueue;// 上升时所用的优先队列，按请求楼层升序
    private PriorityBlockingQueue<Request> downQueue;// 下降时所用的优先队列，按请求楼层降序
    private PriorityBlockingQueue<Request> curQueue;// 现在使用的捎带优先队列(上面两个中选择)
    private ConcurrentLinkedQueue<Request> waitingQueue;// 当前方向无法捎带但是被分配了的请求
    private ConcurrentLinkedQueue<Request> toRelease;// 运行到某层，开关门后需要放开的按钮请求队列
    
    DecimalFormat df;
    
    public Elevator(int _id, PrintWriter _writer, Floor _floor)
    {
        id = _id;
        writer = _writer;
        floor = _floor;
        workLoad = 0;
        erLight = new boolean[21];
        state = State.WAITING;
        direction = Direction.STILL;
        curFloor = Floor.minFloor;
        df = new DecimalFormat("0.0");
        upQueue = new PriorityBlockingQueue<Request>(1000, new Comparator<Request>() {
            @Override
            public int compare(Request q1, Request q2)
            {
                if(q1.DstFloor() != q2.DstFloor()) return q1.DstFloor()-q2.DstFloor();
                else return q1.Time()-q2.Time() > 0 ? 1 : -1;
            }
        });
        
        downQueue = new PriorityBlockingQueue<Request>(1000, new Comparator<Request>() {
            @Override
            public int compare(Request q1, Request q2)
            {
                if(q1.DstFloor() != q2.DstFloor()) return q2.DstFloor()-q1.DstFloor();
                else return q1.Time()-q2.Time() > 0 ? 1 : -1;
            }
        });
        
        curQueue = upQueue;// 开始时默认上升
        waitingQueue = new ConcurrentLinkedQueue<Request>();
        toRelease = new ConcurrentLinkedQueue<Request>();
    }
    @Override
    public void run()
    {
        /* @REQUIRES: None;
         * @MODIFIES: this;
         * @EFFECTS: this.curFloor = last finished request's destination;
         *           this.workLoad = count of floors this elv passes before finishing;
         * */
        boolean endElv = false;
        while(true)
        {
            while(state == State.WAITING)
            {
                //System.out.println(id+":waiting!");
                if(curQueue.isEmpty())
                {
                    if(!waitingQueue.isEmpty())
                    {
                        curQueue = waitingQueue.peek().DstFloor() > curFloor ? upQueue : downQueue;
                        curQueue.offer(waitingQueue.poll());
                    }
                    else if(ElevatorSystem.endFlag && ElevatorScheduler.endScheduler)
                    {
                        endElv = true;
                        break;
                    }
                }
                else
                {
                    dstFloor = curQueue.peek().DstFloor();
                    if(dstFloor > curFloor)
                    {
                        direction = Direction.UP;
                        state = State.RUNNING;
                    }
                    else if(dstFloor < curFloor)
                    {
                        direction = Direction.DOWN;
                        state = State.RUNNING;
                    }
                    else
                        state = State.STALL;
                }
            }
            if(endElv) break;
            while(state == State.RUNNING)
            {
                boolean flag = false;// 终止死循环
                while(!waitingQueue.isEmpty() && !flag)// 实时检查捎带，添加到捎带队列curQueue中
                {
                    int frontDst = waitingQueue.peek().DstFloor();
                    flag = true;
                    if((frontDst > curFloor && direction == Direction.UP) || (frontDst < curFloor && direction == Direction.DOWN))
                    {
                        flag = false;
                        curQueue.offer(waitingQueue.poll());
                    }
                }
                long curTime = new Date().getTime();
                // 运行一层还需要多少时间，同下面开关门的计算方法
                long sleepTime = 3000 - (curTime - lastStopTime) % 3000 > 1000 ? 3000 : 3000 - (curTime - lastStopTime) % 3000;
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                curTime = new Date().getTime();
                curFloor = direction == Direction.UP ? curFloor + 1 : curFloor - 1;// 上行或下行一层
                workLoad++;// 电梯工作量更新
                //System.out.println("ELV"+id+" running to: "+curFloor);
                toRelease.clear();
                while(!curQueue.isEmpty() && curFloor == curQueue.peek().DstFloor())// 处理完成的请求
                {
                    Request req = curQueue.poll();
                    toRelease.offer(req);
                    String dr = direction == Direction.UP ? "UP" :
                                direction == Direction.DOWN ? "DOWN" : "STILL";
                    writer.println(
                        curTime + ":[" + req.StringWithoutPar() + "," + df.format((req.Time() - ElevatorSystem.startTime) / 1000) + "] / (#" + 
                        id+", "+curFloor+", "+dr+", "+workLoad+", "+df.format((curTime - ElevatorSystem.startTime) / 1000)+")"
                    );
                    state = State.OPEN_AND_CLOSE;
                }
            }
            while(state == State.OPEN_AND_CLOSE || state == State.STALL)
            {
                //System.out.println("ELV"+id+" stall or open/close in: "+curFloor);
                long curTime = new Date().getTime();
                // 睡眠时间理论上6000-x，但是有时候前面的睡眠时间不够3*t秒，导致需要睡够6秒，否则少睡很久
                long sleepTime = (curTime - lastStopTime) % 3000 < 1000 ? 6000 - (curTime - lastStopTime) % 3000 : 6000;
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                curTime = new Date().getTime();
                if(state == State.STALL)
                {
                    Request req = curQueue.poll();
                    String dr = direction == Direction.UP ? "UP" :
                        direction == Direction.DOWN ? "DOWN" : "STILL";
                    writer.println(
                            curTime + ":[" + req.StringWithoutPar() + "," + df.format((req.Time() - ElevatorSystem.startTime) / 1000) + "] / (#" + 
                            id+", "+curFloor+", "+dr+", "+workLoad+", "+df.format((curTime - ElevatorSystem.startTime) / 1000)+")"
                        );
                    if(req.Type() == ReqType.FR)// 关灯
                        floor.Release(req.DstFloor(), req.Direction());
                }
                for(Request req : toRelease)// 关灯
                {
                    if(req.Type() == ReqType.FR)
                        floor.Release(req.DstFloor(), req.Direction());
                    else
                        erLight[req.DstFloor()] = false;
                }
                if(curQueue.isEmpty())// 完成可完成的任务后，如果任务队列为空
                {
                    if(waitingQueue.isEmpty()) state = State.WAITING;// 等待队列也空，则电梯处于等待状态，不动
                    else// 否则将等待队列最顶部请求加入任务
                    {
                        Request req = waitingQueue.poll();
                        if(req.DstFloor() > curFloor)
                        {
                            curQueue = upQueue;
                            state = State.RUNNING;
                            direction = Direction.UP;
                        }
                        else if(req.DstFloor() < curFloor)
                        {
                            curQueue = downQueue;
                            state = State.RUNNING;
                            direction = Direction.DOWN;
                        }
                        else
                        {
                            curQueue = upQueue;
                            state = State.STALL;// 同层请求，再做一次开关门
                        }
                        curQueue.offer(req);
                    }
                }
                else// 不空，则更新当前目标楼层和方向
                {
                    Request req = curQueue.peek();
                    dstFloor = req.DstFloor();
                    if(dstFloor > curFloor)
                    {
                        direction = Direction.UP;
                        state = State.RUNNING;
                    }
                    else if(dstFloor < curFloor)
                    {
                        direction = Direction.DOWN;
                        state = State.RUNNING;
                    }
                    else
                    {
                        state = State.STALL;
                    }
                }
            }
        }
        ElevatorSystem.countDownLatch.countDown();
    }
    
    public synchronized void GetRequest(Request r)
    {
        /* @REQUIRES: r != null;
         * @MODIFIES: this;
         * @EFFECTS: this.state == State.RUNNING || state == State.OPEN_AND_CLOSE &&
         *           (direction == Direction.UP && r.DstFloor() > curFloor) ||
         *           (direction == Direction.DOWN && r.DstFloor() < curFloor)
         *           ==> this.curQueue.offer(r) && this.curQueue.size = \old(this).curQueue.size;
         *           this.state == State.WAITING 
         *           ==> this.lastStopTime = r.Time() &&
         *               (r.DstFloor() != this.curFloor
         *               ==> this.state = State.RUNNING &&
         *                   (r.DstFloor() > this.curFloor
         *                   ==> this.direction = Direction.UP &&
         *                       this.curQueue = this.upQueue) &&
         *                   (r.DstFloor() < this.curFloor
         *                   ==> this.direction = Direction.DOWN &&
         *                       this.curQueue = this.downQueue)) &&
         *               r.DstFloor() == this.curFloor ==>
         *               this.direction = Direction.STILL && this.state = State.STALL;
         *           this.state == State.STALL ==> this.curQueue.offer(r) && this.curQueue.size = \old(this).curQueue.size;
         *           r.Type() == ReqType.ER ==> this.erLight[r.DstFloor()] = true;
         *           r.Type() != ReqType.ER ==> this.floor.Press(r.DstFloor(), r.Direction());
         * @THREAD_EFFECTS: \locked();
         * */
        if(state == State.RUNNING || state == State.OPEN_AND_CLOSE)
        {
            if((direction == Direction.UP && r.DstFloor() > curFloor) || (direction == Direction.DOWN && r.DstFloor() < curFloor))
                curQueue.offer(r);
            else
                waitingQueue.offer(r);
        }
        else if(state == State.WAITING)
        {
            lastStopTime = r.Time();
            dstFloor = r.DstFloor();
            if(dstFloor != curFloor)
            {
                state = State.RUNNING;
                if(dstFloor > curFloor)
                {
                    direction = Direction.UP;
                    curQueue = upQueue;
                }
                else
                {
                    direction = Direction.DOWN;
                    curQueue = downQueue;
                }
            }
            else
            {
                direction = Direction.STILL;
                state = State.STALL;
            }
            curQueue.offer(r);
        }
        else// STALL
        {
            curQueue.offer(r);
        }
        if(r.Type() == ReqType.ER)
            erLight[r.DstFloor()] = true;
        else
            floor.Press(r.DstFloor(), r.Direction());
    }
    
    public synchronized boolean CanRespond(Request r)// 可否响应
    {
        /* @REQUIRES: r != null;
         * @MODIFIES: None;
         * @EFFECTS: \result = this.state == State.WAITING && this.curQueue.isEmpty() && this.waitingQueue.isEmpty() ||
         *                     r.Direction() == direction &&
         *                     (this.direction == Direction.UP && r.DstFloor() <= this.dstFloor && r.DstFloor() > this.curFloor ||
         *                     this.direction == Direction.DOWN && r.DstFloor() >= this.dstFloor && r.DstFloor() < this.curFloor);
         * @THREAD_EFFECTS: \locked();
         * */
        if(state == State.WAITING && curQueue.isEmpty() && waitingQueue.isEmpty())// 否则电梯很快会转变状态，在其他状态捎带
            return true;
        else if(r.Direction() == direction)
        {
            if(direction == Direction.UP && r.DstFloor() <= dstFloor && r.DstFloor() > curFloor)
                return true;
            if(direction == Direction.DOWN && r.DstFloor() >= dstFloor && r.DstFloor() < curFloor)
                return true;
        }
        return false;
    }
    
    public synchronized boolean CanPickaback(Request r)// 可响应的情况下可否捎带
    {
        /* @REQUIRES: r != null;
         * @MODIFIES: None;
         * @EFFECTS: \result = r.Direction() == direction &&
         *                     (this.direction == Direction.UP && r.DstFloor() <= this.dstFloor && r.DstFloor() > this.curFloor ||
         *                     this.direction == Direction.DOWN && r.DstFloor() >= this.dstFloor && r.DstFloor() < this.curFloor);
         * @THREAD_EFFECTS: \locked();
         * */
        if(r.Direction() == direction)
        {
            if(direction == Direction.UP && r.DstFloor() <= dstFloor && r.DstFloor() > curFloor)
                return true;
            if(direction == Direction.DOWN && r.DstFloor() >= dstFloor && r.DstFloor() < curFloor)
                return true;
        }
        return false;
    }
    
    public synchronized boolean IsSame(int floor)
    {
        /* @REQUIRES: 1 <= floor <= 20;
         * @MODIFIES: None;
         * @EFFECTS: \result = this.erLight[floor];
         * @THREAD_EFFECTS: \locked();
         * */
        return erLight[floor];
    }
    
    public int WorkLoad()
    {
        /* @REQUIRES: None;
         * @MODIFIES: None;
         * @EFFECTS: \result = this.workLoad;
         * */
        return workLoad;
    }
    
    public int Id()
    {
        /* @REQUIRES: None;
         * @MODIFIES: None;
         * @EFFECTS: \result = this.id;
         * */
        return id;
    }
}
