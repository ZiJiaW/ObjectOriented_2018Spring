package sys;

import java.util.ArrayList;
/**
 * @OVERVIEW:Test tools for testers;
 * @INHERIT:None;
 * @INVARIANT:this.taxis!=null; 
 */
public class TestTool {
    private Taxi[] taxis;
    /**
     * @REQUIRES: ts!=null;
     * @MODIFIES: this.taxis;
     * @EFFECTS: this.taxis == ts;
     */
    TestTool(Taxi[] ts){
        taxis = ts;
    }
    /**
     * @REQUIRES: id>=0&&id<100;
     * @MODIFIES: None;
     * @EFFECTS: \result[0] == gv.getTime();
     * \result[1] == this.taxis[id].getPosition().x;
     * \result[2] == this.taxis[id].getPosition().y;
     * \result[3] == this.taxis[id].getState();
     */
    public long[] queryTaxiById(int id)
    {
        long[] r = new long[4];
        r[0] = gv.getTime();
        r[1] = taxis[id].getPosition().x;
        r[2] = taxis[id].getPosition().y;
        r[3] = taxis[id].getState();
        return r;
    }
    /**
     * @REQUIRES: s==0||s==1||s==2||s==3;
     * @MODIFIES: None;
     * @EFFECTS:\all Taxi t; taxis.contains(t)&&t.getState()==s; \result.contains(t.getId());
     */
    public ArrayList<Integer> queryTaxiByState(int s)
    {
        ArrayList<Integer> r = new ArrayList<Integer>();
        for(Taxi t : taxis)
        {
            if(t.getState() == s)
            {
                r.add(t.getId());
            }
        }
        return r;
    }
    /**
     * @REQUIRES:None;
     * @MODIFIES:None;
     * @EFFECTS:this.taxis!=null; 
     */
    public boolean repOk()
    {
        return this.taxis!=null; 
    }
}
