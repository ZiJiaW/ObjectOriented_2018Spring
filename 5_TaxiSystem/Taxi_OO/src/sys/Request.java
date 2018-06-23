package sys;

import java.util.HashSet;
import java.util.Set;
/**
 * @OVERVIEW: record request, its executing log, candidates and carrier;
 * @INHERIT: None;
 * @INVARIANT: this.start >= 0 && this.start < 6400 && this.end >= 0 && this.end < 6400 && time > 0;
 */
public class Request {
    private int id;
    private int start;
    private int end;
    private long time;
    private Set<Taxi> candidates;// grabbed taxis
    private StringBuilder record;// to print
    private Taxi carrier;
    private int count;
    /**
     * @REQUIRES: start >= 0 && start < 6400 && end >= 0 && end < 6400 && time > 0;
     * @MODIFIES: this;
     * @EFFECTS: this.start == start;
     * this.end == end;
     * this.time == time - time%100;
     * this.carrier == null;
     * this.candidates == new HashSet<Taxi>();
     * this.record == new StringBuilder();
     * this.count = 0;
     */
    Request(int start, int end, long time){
        this.start = start;
        this.end = end;
        this.time = time;
        candidates = new HashSet<Taxi>();
        record = new StringBuilder();
        carrier = null;
        count = 0;
    }
    /**
     * @REQUIRES: t != null;
     * @MODIFIES: this.candidates;
     * @EFFECTS: this.candidates.add(t);
     * \result == !\old(this).candidates.contains(t);
     */
    public boolean addTaxi(Taxi t) {
        if(candidates.contains(t)) return false;
        candidates.add(t);
        return true;
    }
    /**
     * @REQUIRES: r != null;
     * @MODIFIES: None;
     * @EFFECTS: \result == this.start == r.getStart() && this.end == r.getEnd() && this.time == r.getTime();
     */
    public boolean equals(Request r)
    {
        return start==r.getStart()&&end==r.getEnd()&&time==r.getTime();
    }
    /**
     * @REQUIRES: None;
     * @MODIFIES: this.record, this.count;
     * @EFFECTS: this.record.append('\n');
     * this.count == 0;
     */
    public void addEnter() {
        count = 0;
        record.append('\n');
    }
    /**
     * @REQUIRES: None;
     * @MODIFIES: this.carrier;
     * @EFFECTS: \result == (\exists Taxi t; this.candidates.contains(t) && t.getState() == State.WFS;);
     * \all Taxi t; this.candidates.contains(t);
     * (this.carrier == null || t.getCredit() > last_credit ||
     * t.getCredit() == last_credit && dist(t,this.start) < last_dist) ==> this.cairrier == t;
     */
    public boolean canDispose()// can dispose a taxi or not in this.candidates
    {
        int last_credit = -1, last_dist = Integer.MAX_VALUE;
        OutFuncs.printCand(candidates, id);
        for(Taxi t:candidates)
        {
            if(t.getState() == State.WFS && t.getTask() == null)
            {
                if(carrier == null || t.getCredit() > last_credit || 
                        (t.getCredit() == last_credit && Math.abs(t.getPosition().x-start/80) + Math.abs(t.getPosition().y-start%80) < last_dist))
                {
                    carrier = t;
                    last_credit = t.getCredit();
                    last_dist = Math.abs(t.getPosition().x-start/80) + Math.abs(t.getPosition().y-start%80);
                }
            }
        }
        return carrier != null;
    }
    /**
     * @REQUIRES: None;
     * @MODIFIES: this.record;
     * @EFFECTS: this.record.append(str);
     */
    public void recordInfo(String str) {
        record.append(str);
    }
    /**
     * @REQUIRES: None;
     * @MODIFIES: this.record; this.count;
     * @EFFECTS: this.record.append(str);
     * this.count == \old(this).count + 1;
     * this.count == 6 ==> this.record.append('\n') && this.count == 0;
     */
    public void recordPos(String str)
    {
        record.append(str);
        count++;
        if(count == 6) {
            record.append('\n');
            count = 0;
        }
    }
    /**
     * @REQUIRES:None;
     * @MODIFIES:None;
     * @EFFECTS:\result==this.start >= 0 && this.start < 6400 && this.end >= 0 && this.end < 6400 && time > 0;
     */
    public boolean repOk()
    {
        return this.start >= 0 && this.start < 6400 && this.end >= 0 && this.end < 6400 && time > 0;
    }
//++++++++++++++++++++++++++++++++++++++++setter and getter++++++++++++++++++++++++++++++++++++++++++++++++++1s
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public int getStart() {return start;}
    public int getEnd() {return end;}
    public long getTime() {return time;}
    public String toString() {
        return "Request NO."+id;
    }
    public Taxi getCarrier() {return carrier;}
    public String getRecord() {return record.toString();}
}
