package sys;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
/**
 * @OVERVIEW: record and update map periodically;
 * @INHERIT: None;
 * @INVARIANT: this.edges!=null&&this.rawMap!=null&&this.flow!=null&&this.tmpflow!=null;
 */
public class Map implements Runnable{
    public EdgeSet[] edges;// adjacent list
    public int[][] rawMap;// 80*80
    public int[][] flow;// 6400*6400 flow map
    private int[][] tmpflow;// 500ms flow map
    /**
     * @REQUIRES: None;
     * @MODIFIES: this;
     * @EFFECTS: this.edges == new EdgeSet[6400];
     * this.rawMap == new int[80][80];
     * this.flow == new int[6400][6400];
     * this.tmpflow == new int[6400][6400];
     * \all int i; i >= 0 && i < 6400; this.edges[i] == new EdgeSet();
     */
    Map()
    {
        edges = new EdgeSet[6400];
        for(int i = 0; i < 6400; ++i) edges[i] = new EdgeSet();
        rawMap = new int[80][80];
        flow = new int[6400][6400];
        tmpflow = new int[6400][6400];
    }
    /**
     * @REQUIRES: sc != null;
     * @MODIFIES: this.rawMap; this.edges;
     * @EFFECTS:
     * \result == file_content.length == 6400 && \all String line; line == sc.nextLine(); line.length == 80;
     * \all int i, j; i>=0&&i<80&&j>=0&&j<80; rawMap[i][j] == file_content.getByte(80*i+j);
     * \all int i; 0 <= i && i < 6400;
     * (file_content[i]=='1' || file_content[i]=='3') ==> (this.edges[i].add(i+1) && this.edges[i+1].add(i)) &&
     * (file_content[i]=='2' || file_content[i]=='3') ==> (this.edges[i].add(i+80) && this.edges[i+80].add(i));
     */ 
    public boolean readFile(Scanner sc)
    {
        StringBuilder all = new StringBuilder();
        String lineBuf;
        int lineCnt = 0;
        while(sc.hasNextLine())
        {
            lineBuf = sc.nextLine().replaceAll("[ \t]", "");
            if(lineBuf.equals("#end_map")) break;
            if(lineBuf.length()==0) continue;
            if(lineBuf.length()!=80) {
                return false;
            }
            for(int i = 0; i < lineBuf.length(); ++i)
            {
                char c = lineBuf.charAt(i);
                if(c=='0'||c=='1'||c=='2'||c=='3')
                    rawMap[lineCnt][i] = c - '0';
                else {
                    sc.close();
                    return false;
                }
            }
            lineCnt++;
            all.append(lineBuf);
        }
        if(lineCnt!=80) return false;
        // initiate this.edges
        for(int i = 0; i < all.length(); ++i)
        {
            char c = all.charAt(i);
            if(c=='1'||c=='3')
            {
                edges[i].add(i+1);
                edges[i+1].add(i);
            }
            if(c=='2'||c=='3')
            {
                edges[i].add(i+80);
                edges[i+80].add(i);
            }
        }
        return true;
    }
    /**
     * @REQUIRES: None;
     * @MODIFIES: this.flow; this.tmpflow;
     * @EFFECTS: \all int i; i >= 0 && i < 6400; 
     * \all int adj; adj == this.edges[i].getAdj(); this.flow[i][adj] == this.tmpflow[i][adj] && this.tmpflow[i][adj] == 0;
     */
    public void run()
    {// update flow map periodically
        long st = gv.getTime();
        while(true) {
            long sleep = 500 + st - gv.getTime();// every 500ms
            try {
                if(sleep > 0) Thread.sleep(sleep);
                else Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            synchronized(this)// update
            {
                for(int i = 0; i < 6400; ++i) {
                    for(int adj:edges[i].getAdj()) {
                        flow[i][adj] = tmpflow[i][adj];
                        tmpflow[i][adj] = 0;
                    }
                }
            }
            st+=500;
        }
    }
    /**
     * @REQUIRES: start >= 0 && start < 6400 && end >= 0 && end < 6400;
     * @MODIFIES: None;
     * @EFFECTS: \result == shortestPath(start, end);
     * @THREAD_EFFECTS:\locked();
     */
    public synchronized String getShortestPath(int start, int end)
    {
        if(start == end) return "";// same point
        int s = end;
        boolean[] added = new boolean[6400];// if added
        int[] d = new int[6400];// distance record
        int[] f = new int[6400];// flow accumulation record
        for(int i = 0; i < 6400; ++i) {
            d[i] = f[i] = Integer.MAX_VALUE;
        }
        int[] n = new int[6400];// successor vertex
        LinkedList<Integer> q = new LinkedList<Integer>();
        q.offer(s);
        added[s] = true; d[s] = f[s] = 0;
        int cur, cost, sumFlow;
        while (!q.isEmpty())
        {
            cur = q.poll();
            ArrayList<Integer> adj = edges[cur].getAdj();
            for (int to : adj)
            {
                cost = 1 + d[cur];
                sumFlow = f[cur] + flow[cur][to];
                if (d[to] > cost || (d[to] == cost && f[to] > sumFlow))
                {
                    n[to] = cur;
                    d[to] = cost;
                    f[to] = sumFlow;
                    if (!added[to])
                    {
                        added[to] = true;
                        q.offer(to);
                    }
                }
            }
            added[cur] = false;
        }
        StringBuilder path = new StringBuilder();
        int begin = start;
        while(begin != end)
        {
            if(n[begin] == begin + 1) path.append('R');//right
            if(n[begin] == begin - 1) path.append('L');
            if(n[begin] == begin +80) path.append('D');
            if(n[begin] == begin -80) path.append('U');
            begin = n[begin];
        }
        return path.toString();
    }
    /**
     * @REQUIRES: first >= 0 && first < 6400 && second >= 0 && second < 6400;
     * @MODIFIES: this.tmpflow;
     * @EFFECTS: Math.abs(first - second) == 1 || Math.abs(first - second) == 80 
     * ==> this.tmpflow[first][second] == \old(this.tmpflow)[first][second] + 1 
     * && this.tmpflow[second][first] == \old(this.tmpflow)[second][first] + 1;
     * @THREAD_EFFECTS:\locked();
     */
    public synchronized void addFlow(int first, int second)
    {
        int judge = Math.abs(first-second);// judge adjcency
        if(judge == 1|| judge == 80) {
            tmpflow[first][second]++;
            tmpflow[second][first]++;
        }
    }
    /**
     * @REQUIRES: first >= 0 && first < 6400 && second >= 0 && second < 6400;
     * @MODIFIES: this.edges;
     * @EFFECTS: open_or_close == true && \all int i; this.edges[first].contains(i); i != second; && 
     * (Math.abs(first - second) == 1 || Math.abs(first - second) == 80) ==> this.edges[first].add(second) && this.edges[second].add(first);
     * open_or_close == false && this.edges[first].contains(second) ==> this.edges[first].remove(second) && this.edges[second].remove(first);
     * @THREAD_EFFECTS:\locked();
     */
    public synchronized void setEdgeState(int first, int second, boolean open_or_close)
    {
        ArrayList<Integer> adj = edges[first].getAdj();
        if(open_or_close)// open
        {
            for(int i:adj) if(i == second) return;
            int judge = Math.abs(first-second);
            if(judge == 1|| judge == 80) {
                edges[first].add(second);
                edges[second].add(first);
            }
            else {
                System.out.println("不相邻的结点不能打开边！");
            }
        }
        else// close
        {
            for(int i:adj) {
                if(i == second)// exist an edge
                {
                    edges[first].remove(second);
                    edges[second].remove(first);
                    return;
                }
            }
            System.out.println("不存在这条边！");
        }
    }
    /**
     * @REQUIRES: first >= 0 && first < 6400 && second >= 0 && second < 6400 && f >= 0;
     * @MODIFIES: this.flow;
     * @EFFECTS: this.flow[first][second] == f && this.flow[second][first] == f;
     * @THREAD_EFFECTS:\locked();
     */
    public synchronized void setFlow(int first, int second, int f)
    {
        flow[first][second] = f;
        flow[second][first] = f;
    }
    
    /**
     * @REQUIRES:None;
     * @MODIFIES:None;
     * @EFFECTS:\result== this.edges!=null&&this.rawMap!=null&&this.flow!=null&&this.tmpflow!=null;;
     */
    public boolean repOk()
    {
        return this.edges!=null&&this.rawMap!=null&&this.flow!=null&&this.tmpflow!=null;
    }
}
