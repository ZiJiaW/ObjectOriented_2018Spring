package sys;
import java.util.ArrayList;
/**
 * @OVERVIEW: Edge list of vertices;
 * @INHERIT: None;
 * @INVARIANT: this.edges != null;
 * */
public class EdgeSet {
    ArrayList<Integer> edges;// adjacent list
    /**
     * @REQUIRES:None;
     * @MODIFIES:this;
     * @EFFECTS:this.edges == new ArrayList<Integer>();
     */
    EdgeSet()
    {
        edges = new ArrayList<Integer>();
    }
    /**
     * @REQUIRES:adj >= 0 && adj < 6400;
     * @MODIFIES:this;
     * @EFFECTS:this.edges.contains(adj) && this.edges.size == \old(this).edges.size + 1;
     */
    public void add(int adj)
    {
        edges.add(adj);
    }
    /**
     * @REQUIRES:adj >= 0 && adj < 6400;
     * @MODIFIES:this;
     * @EFFECTS:!this.edges.contains(adj) && this.edges.size == \old(this).edges.size - 1;
     */
    public void remove(int adj)
    {
        edges.remove(adj);
    }
    /**
     * @REQUIRES:None;
     * @MODIFIES:None;
     * @EFFECTS:\result == new ArrayList<Integer>(edges);
     */
    public ArrayList<Integer> getAdj()
    {
        return new ArrayList<Integer>(edges);// return a clone
    }
    /**
     * @REQUIRES:None;
     * @MODIFIES:None;
     * @EFFECTS:\result == this.edges != null;
     */
    public boolean repOk()
    {
        return this.edges != null;
    }
}
