package sys;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Main {
    public static boolean sysEnd = false;
    public static boolean schEnd = false;
    public static CountDownLatch countDownLatch = new CountDownLatch(101);
    public static PrintWriter out;
    public static void main(String[] args)
    {
        try {
            out = new PrintWriter("TaxiLog.txt");
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Map map = new Map();
        TaxiGUI gui = new TaxiGUI();
        RequestQueue requests = new RequestQueue();
        Taxi[] taxis = new Taxi[100];
        for(int i = 0; i < 100; ++i)
        {
            taxis[i] = new Taxi(map, i, gui);
        }
        Scheduler scheduler = new Scheduler(requests, gui, taxis);
        InputHandler inputHandler = new InputHandler(requests, map, taxis, gui);
        // parse config file
        System.out.print("请输入配置文件路径: ");
        Scanner sc = new Scanner(System.in);
        String file = sc.nextLine();
        inputHandler.parseFile(file);
        
        gui.LoadMap(map.rawMap, 80);
        for(int i = 0; i < 100; ++i) new Thread(taxis[i]).start();
        new Thread(scheduler).start();
        new Thread(map).start();
        
        // parse console
        inputHandler.parseInput(sc);
        // get END
        System.out.println("Wait threads to end!");
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        out.close();
        System.out.println("System closed!");
        System.exit(0);
    }
}
