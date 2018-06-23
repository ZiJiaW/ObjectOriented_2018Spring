package sys;

import java.text.DecimalFormat;
import java.util.Set;
/**
 * @OVERVIEW: Provide output functions for logging;
 * @INHERIT: None;
 * @INVARIANT: None;
 */
public class OutFuncs {
    private static String split = "-----------------------------------------------------------------------------------"
            +"--------------------------------------------------------------------------------------------";
    public static DecimalFormat df = new DecimalFormat("0.0");
    /**
     * @REQUIRES: s!=null;
     * @MODIFIES: None;
     * @EFFECTS: None;
     */
    public static void println(String s)
    {
        Main.out.println(s);
    }
    /**
     * @REQUIRES: r!=null;
     * @MODIFIES: None;
     * @EFFECTS: None;
     */
    public static void printReq(Request r)
    {
        String start = "("+r.getStart()/80+","+r.getStart()%80+")";
        String end = "("+r.getEnd()/80+","+r.getEnd()%80+")";
        println("Get new valid request #"+r.getId()+": "+start+"---->"+end+" at "+df.format(r.getTime()/100.0));
        println(split);
    }
    /**
     * @REQUIRES: candidates!=null;
     * @MODIFIES: None;
     * @EFFECTS: None;
     */
    public static void printCand(Set<Taxi> candidates, int r_id)
    {
        println("Request #"+r_id+" is grabbed by following taxis:");
        if(candidates.isEmpty())
            println("No taxi grabbed it!");
        String pos;
        for(Taxi t:candidates)
        {
            pos = "("+t.getPosition().x+","+t.getPosition().y+")";
            println("Taxi #"+t.getId()+": "+pos+", "+"WFS"+", credit is "+t.getCredit());
        }
        println(split);
    }
    /**
     * @REQUIRES: r!=null;
     * @MODIFIES: None;
     * @EFFECTS: None;
     */
    public static void printRecord(Request r)
    {
        println(r.getRecord());
        println(split);
    }
    
}
