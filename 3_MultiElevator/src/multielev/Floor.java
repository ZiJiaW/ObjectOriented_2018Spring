package multielev;

public class Floor {
    /* @OVERVIEW: record floor's state and max/min NO.
     * @INHERIT: None
     * @INVARIANT: None
     * */
    public static int maxFloor = 20;
    public static int minFloor = 1;
    private boolean[] upLight;
    private boolean[] downLight;
    public Floor()
    {
        /* @REQUIRES: None;
         * @MODIFIES: this;
         * @EFFECTS: this.upLight = new boolean[21];
         *           this.downLight = new boolean[21];
         * */
        upLight = new boolean[21];
        downLight = new boolean[21];
    }
    
    public synchronized void Press(int floor, Direction dr)
    {
        /* @REQUIRES: 1 <= floor <= 20 && dr == Direction.UP || dr == Direction.DOWN;
         * @MODIFIES: this;
         * @EFFECTS: dr == Direction.UP ==> this.upLight[floor] = true &&
         *           dr == Direction.DOWN ==> this.downLight[floor] = true;
         * @THREAD_EFFECTS: \locked()
         * */
        if(dr == Direction.UP) upLight[floor] = true;
        else downLight[floor] = true;
    }
    
    public synchronized void Release(int floor, Direction dr)
    {
        /* @REQUIRES: 1 <= floor <= 20 && dr == Direction.UP || dr == Direction.DOWN;
         * @MODIFIES: this;
         * @EFFECTS: dr == Direction.UP ==> this.upLight[floor] = false &&
         *           dr == Direction.DOWN ==> this.downLight[floor] = false;
         * @THREAD_EFFECTS: \locked()
         * */
        if(dr == Direction.UP) upLight[floor] = false;
        else downLight[floor] = false;
    }
    public synchronized boolean IsSame(int floor, Direction dr)
    {
        /* @REQUIRES: 1 <= floor <= 20 && dr == Direction.UP || dr == Direction.DOWN;
         * @MODIFIES: None;
         * @EFFECTS: \result = dr == Direction.UP ? upLight[floor] : downLight[floor];
         * @THREAD_EFFECTS: \locked()
         * */
        return dr == Direction.UP ? upLight[floor] : downLight[floor];
    }
}
