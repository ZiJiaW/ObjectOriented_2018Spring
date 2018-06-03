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
    private long lastStopTime;// �����ϴδ�ֹͣ���˶���ʱ��
    
    private boolean[] erLight;
    
    private volatile State state;
    private volatile Direction direction;
    
    private PriorityBlockingQueue<Request> upQueue;// ����ʱ���õ����ȶ��У�������¥������
    private PriorityBlockingQueue<Request> downQueue;// �½�ʱ���õ����ȶ��У�������¥�㽵��
    private PriorityBlockingQueue<Request> curQueue;// ����ʹ�õ��Ӵ����ȶ���(����������ѡ��)
    private ConcurrentLinkedQueue<Request> waitingQueue;// ��ǰ�����޷��Ӵ����Ǳ������˵�����
    private ConcurrentLinkedQueue<Request> toRelease;// ���е�ĳ�㣬�����ź���Ҫ�ſ��İ�ť�������
    
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
        
        curQueue = upQueue;// ��ʼʱĬ������
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
                boolean flag = false;// ��ֹ��ѭ��
                while(!waitingQueue.isEmpty() && !flag)// ʵʱ����Ӵ�����ӵ��Ӵ�����curQueue��
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
                // ����һ�㻹��Ҫ����ʱ�䣬ͬ���濪���ŵļ��㷽��
                long sleepTime = 3000 - (curTime - lastStopTime) % 3000 > 1000 ? 3000 : 3000 - (curTime - lastStopTime) % 3000;
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                curTime = new Date().getTime();
                curFloor = direction == Direction.UP ? curFloor + 1 : curFloor - 1;// ���л�����һ��
                workLoad++;// ���ݹ���������
                //System.out.println("ELV"+id+" running to: "+curFloor);
                toRelease.clear();
                while(!curQueue.isEmpty() && curFloor == curQueue.peek().DstFloor())// ������ɵ�����
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
                // ˯��ʱ��������6000-x��������ʱ��ǰ���˯��ʱ�䲻��3*t�룬������Ҫ˯��6�룬������˯�ܾ�
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
                    if(req.Type() == ReqType.FR)// �ص�
                        floor.Release(req.DstFloor(), req.Direction());
                }
                for(Request req : toRelease)// �ص�
                {
                    if(req.Type() == ReqType.FR)
                        floor.Release(req.DstFloor(), req.Direction());
                    else
                        erLight[req.DstFloor()] = false;
                }
                if(curQueue.isEmpty())// ��ɿ���ɵ����������������Ϊ��
                {
                    if(waitingQueue.isEmpty()) state = State.WAITING;// �ȴ�����Ҳ�գ�����ݴ��ڵȴ�״̬������
                    else// ���򽫵ȴ�������������������
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
                            state = State.STALL;// ͬ����������һ�ο�����
                        }
                        curQueue.offer(req);
                    }
                }
                else// ���գ�����µ�ǰĿ��¥��ͷ���
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
    
    public synchronized boolean CanRespond(Request r)// �ɷ���Ӧ
    {
        /* @REQUIRES: r != null;
         * @MODIFIES: None;
         * @EFFECTS: \result = this.state == State.WAITING && this.curQueue.isEmpty() && this.waitingQueue.isEmpty() ||
         *                     r.Direction() == direction &&
         *                     (this.direction == Direction.UP && r.DstFloor() <= this.dstFloor && r.DstFloor() > this.curFloor ||
         *                     this.direction == Direction.DOWN && r.DstFloor() >= this.dstFloor && r.DstFloor() < this.curFloor);
         * @THREAD_EFFECTS: \locked();
         * */
        if(state == State.WAITING && curQueue.isEmpty() && waitingQueue.isEmpty())// ������ݺܿ��ת��״̬��������״̬�Ӵ�
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
    
    public synchronized boolean CanPickaback(Request r)// ����Ӧ������¿ɷ��Ӵ�
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
