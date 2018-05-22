package multielev;

import java.io.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

enum ReqType {ER, FR};
enum Direction {UP, DOWN, STILL};
enum State {RUNNING, WAITING, OPEN_AND_CLOSE, STALL};

public class ElevatorSystem {
    /* @OVERVIEW: arrange three elevators and one scheduler and feed requirements;
     * @INHERIT: None
     * @INVARIANT: None
     * */
    public static long startTime;
    public static CountDownLatch countDownLatch = new CountDownLatch(4);
    public static boolean endFlag = false;
    public static void main(String[] args)
    {
        /* @REQUIRES: None;
         * @MODIFIES: this;
         * @EFFECTS: this.startTime = first requirement's time;
         *           this.endFlag = true;
         * */
        try {
            Scanner sc = new Scanner(System.in);
            PrintWriter writer = new PrintWriter("result.txt");
            RequestQueue requestQueue = new RequestQueue(writer);
            Floor floor = new Floor();
            Elevator[] elvs = new Elevator[3];
            for(int i = 1; i <= 3; ++i)
            {
                elvs[i-1] = new Elevator(i, writer, floor);
                new Thread(elvs[i-1]).start();
            }
            ElevatorScheduler es = new ElevatorScheduler(writer, floor, elvs, requestQueue);
            new Thread(es).start();
            boolean isFirst = true;
            while(true)
            {
                String input = sc.nextLine();
                if(input.equals("END"))
                    break;
                input = input.replaceAll(" ", "");
                String[] strs = input.split(";");
                long curTime = new Date().getTime();
                if(isFirst)
                {
                    startTime = curTime;
                    isFirst = false;
                }
                requestQueue.Offer(strs, curTime);
            }
            endFlag = true;
            System.out.println("Wait elevators to finish!");
            countDownLatch.await();
            sc.close();
            writer.close();
            System.out.println("All elevators have finished!");
            System.exit(0);
        }
        catch(Exception e)
        {
            System.out.println("Error Happened!");
            System.exit(0);
        }
    }
}
