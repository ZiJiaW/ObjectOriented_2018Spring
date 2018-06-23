package sys;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Pattern;
/**
 * @OVERVIEW: Provide input tools to parse file and console;
 * @INHERIT: None;
 * @INVARIANT: this.requests!=null&&this.map!=null&&this.gui!=null&&this.taxis!=null;
 */
public class InputHandler {
    private RequestQueue requests;
    private Map map;
    private TaxiGUI gui;
    private Taxi[] taxis;
    /**
     * @REQUIRES:requests!=null&&map!=null&&taxis!=null&&gui!=null;
     * @MODIFIES:this;
     * @EFFECTS:this.requests == requests;
     * this.map == map;
     * this.gui == gui;
     * this.taxis == taxis;
     */
    InputHandler(RequestQueue requests, Map map, Taxi[] taxis, TaxiGUI gui)
    {
        this.requests = requests;
        this.map = map;
        this.gui = gui;
        this.taxis = taxis;
    }
    /**
     * @REQUIRES:filename!=null;
     * @MODIFIES:this;
     * @EFFECTS:\all String line; line.isIn(file); parse(line)&&modify(this.map)&&modify(this.taxis)&&modify(this.requests);
     * file.notExist() == true ==> exceptional_behavior(FileNotFoundException);
     */
    void parseFile(String filename)
    {
        File fp = new File(filename);
        Scanner sc = null;
        try {
            sc = new Scanner(fp);
            while(sc.hasNextLine())
            {
                String buf = sc.nextLine();
                if(buf.length() == 0) continue;// skip empty line
                if(buf.equals("#map"))
                {
                    if(!map.readFile(sc)) {
                        System.out.println("地图格式错误，程序退出！");
                        System.exit(0);
                    }
                }
                else if(buf.equals("#flow"))
                {
                    while(!(buf = sc.nextLine()).equals("#end_flow"))
                    {
                        int[] info = parseFlow(buf);
                        map.setFlow(80*info[0]+info[1], 80*info[2]+info[3], info[4]);
                    }
                }
                else if(buf.equals("#taxi"))
                {
                    while(!(buf = sc.nextLine()).equals("#end_taxi"))
                    {
                        int[] info = parseTaxi(buf);
                        int id = info[0], pos = 80*info[3]+info[4], cred = info[2]; 
                        int state = info[1] == 0 ? State.SERVE : info[1] == 1 ? State.PICK : info[1] == 2 ? State.STOP : State.WFS;
                        if(state != State.STOP && state != State.WFS) {
                            System.out.println("预设文件不可指定出租车状态在服务和接单，因无单可派！");
                            continue;
                        }
                        taxis[id].setCredit(cred);
                        taxis[id].setState(state);
                        taxis[id].setPosition(pos);
                        gui.SetTaxiStatus(id, new Point(info[3], info[4]), state);
                    }
                }
                else if(buf.equals("#request"))
                {
                    while(!(buf = sc.nextLine()).equals("#end_request"))
                    {
                        int[] info = parseRequest(buf);
                        Request r = new Request(80*info[1]+info[2], 80*info[3]+info[4], gv.getTime());
                        requests.add(r);
                    }
                }
            }
        }
        catch(FileNotFoundException e)
        {
            System.out.println("文件不存在，程序退出！");
            System.exit(0);
        }
        sc.close();
    }

    /**
     * @REQUIRES: sc!=null;
     * @MODIFIES: this.map; this.requests; this.gui;
     * @EFFECTS: \all String line; line == sc.nextLine(); parse(line)&&modify(this.requests)&&modify(this.map)&&modify(this.gui);
     */
    public void parseInput(Scanner sc)
    {
        //Scanner sc = new Scanner(System.in);
        while(true)
        {
            String buf = sc.nextLine();
            if(buf.equals("END")) break;
            int[] info = parseRequest(buf);
            switch(info[0])
            {
            case 0:// new request
                Request r = new Request(80*info[1]+info[2], 80*info[3]+info[4], gv.getTime());
                requests.add(r);
                break;
            case 1:// open path
                map.setEdgeState(80*info[1]+info[2], 80*info[3]+info[4], true);
                gui.SetRoadStatus(new Point(info[1], info[2]), new Point(info[3], info[4]), 1);
                break;
            case 2:// close path
                map.setEdgeState(80*info[1]+info[2], 80*info[3]+info[4], false);
                gui.SetRoadStatus(new Point(info[1], info[2]), new Point(info[3], info[4]), 0);
                break;
            default:
                System.out.println("Invalid request: "+buf);
            }
        }
        sc.close();
        Main.sysEnd = true;
    }
    /**
     * @REQUIRES: buf != null;
     * @MODIFIES: None;
     * @EFFECTS: \result == parse(buf.replaceAll(" ", "").split("\\(|\\)|,", -1));
     */
    private int[] parseFlow(String buf)// (x1,y1),(x2,y2),value
    {
        int[] r = new int[5];
        String[] strs = buf.replaceAll(" ", "").split("\\(|\\)|,", -1);
        r[0] = Integer.parseInt(strs[1]); r[1] = Integer.parseInt(strs[2]);
        r[2] = Integer.parseInt(strs[5]); r[3] = Integer.parseInt(strs[6]);
        r[4] = Integer.parseInt(strs[8]);
        return r;
    }
    /**
     * @REQUIRES: buf != null;
     * @MODIFIES: None;
     * @EFFECTS: \result == parse(buf.replaceAll(" ", "").split("\\(|\\)|,", -1));
     */
    private int[] parseTaxi(String buf)// no,state,credit,(x,y)
    {
        int[] r = new int[5];
        String[] strs = buf.replaceAll(" ", "").split("\\(|\\)|,", -1);
        r[0] = Integer.parseInt(strs[0]); r[1] = Integer.parseInt(strs[1]);
        r[2] = Integer.parseInt(strs[2]); r[3] = Integer.parseInt(strs[4]);
        r[4] = Integer.parseInt(strs[5]);
        return r;
    }
    /**
     * @REQUIRES: buf != null;
     * @MODIFIES: None;
     * @EFFECTS: \result == parse(buf.replaceAll(" ", "").split("\\(|\\)|,|\\[|\\]", -1));
     * buf.notmatch("^\\[CR|OP|CL,\\(\\+?\\d+,\\+?\\d+\\),\\(\\+?\\d+,\\+?\\d+\\)\\]$") ==> \result[0] == 3;
     * req.startPos <0 || req.StartPos >= 6400 || req.endPos < 0 || req.endPos >= 6400 ==> \result[0] == 3;
     */
    private int[] parseRequest(String buf)//[CR,(X1, Y1),(X2, Y2)]
    {
        Pattern pat = Pattern.compile("^\\[CR|OP|CL,\\(\\+?\\d+,\\+?\\d+\\),\\(\\+?\\d+,\\+?\\d+\\)\\]$");
        int[] r = new int[5];
        buf = buf.replaceAll(" ", "");
        boolean v = pat.matcher(buf).find();
        if(!v ||buf.charAt(0)!='['|| buf.charAt(buf.length()-1) != ']') {r[0] = 3; return r;}
        String[] strs = buf.split("\\(|\\)|,|\\[|\\]", -1);
        try {
            r[0] = strs[1].equals("CR") ? 0 : strs[1].equals("OP") ? 1 : strs[1].equals("CL") ? 2 : 3;// 0: CR, 1: OP, 2: CL, 3: wrong
            r[1] = Integer.parseInt(strs[3]);
            r[2] = Integer.parseInt(strs[4]);
            r[3] = Integer.parseInt(strs[7]);
            r[4] = Integer.parseInt(strs[8]);
        } catch(Exception e) {
            r[0] = 3;
        }
        if(r[4] < 0 || r[4] >= 80 || r[1] < 0 || r[1] >= 80 ||
           r[2] < 0 || r[2] >= 80 || r[3] < 0 || r[3] >= 80)
            r[0]=3;
        return r;
    }
    /**
     * @REQUIRES:None;
     * @MODIFIES:None;
     * @EFFECTS:\result== this.requests!=null&&this.map!=null&&this.gui!=null&&this.taxis!=null;
     */
    public boolean repOk()
    {
        return  this.requests!=null&&this.map!=null&&this.gui!=null&&this.taxis!=null;
    }
}
