package multielev;


// request such as (FR, floor, UP/DOWN) and (ER, #Elev,floor)
public class Request {
    /* @OVERVIEW: record and parse requests;
     * @INHERIT: None
     * @INVARIANT: None
     * */
    private String rawString;
    private ReqType type;
    private int dstFloor;
    private int elevatorId;
    private Direction direction;
    private long time;
    private boolean isValid;
    public Request(String _raw, long _time)
    {
        /* @REQUIRES: None;
         * @MODIFIES: this;
         * @EFFECTS: this.rawString = _raw;
         *           this.time = _time;
         *           EFFECTS of Parse();
         * */
        rawString = _raw;
        time = _time;
        
        /*default assignment*/
        isValid = false;
        elevatorId = 0;
        direction = Direction.STILL;
        
        Parse();
    }
    private void Parse()
    {
        /* @REQUIRES: None;
         * @MODIFIES: this;
         * @EFFECTS: strs = rawString.substring(1, rawString.length() - 1).split(",");
         *           rawString.length() >= 9 && strs.length() == 3 && (strs[0].equals("FR")
         *           ==> this.dstFloor = Integer.parseInt(strs[1]) &&
         *               this.type = ReqType.FR &&
         *               1 <= this.dstFloor <= 20 &&
         *               (strs[2].equals("UP") && this.dstFloor != 20 ==> this.direction = Direction.UP && this.isValid = true) &&
         *               (strs[2].equals("DOWN") && this.dstFloor != 1 ==> this.direction = Direction.DOWN && this.isValid = true));
         *           rawString.length() >= 9 && strs.length() == 3 && (strs[0].equals("ER")
         *           ==> this.dstFloor = Integer.parseInt(strs[2]) &&
         *               this.type = ReqType.ER &&
         *               1 <= this.dstFloor <= 20 &&
         *               (strs[1].equals("#1") ==> this.elevatorId = 1 && isValid = true) &&
         *               (strs[1].equals("#2") ==> this.elevatorId = 2 && isValid = true) &&
         *               (strs[1].equals("#3") ==> this.elevatorId = 3 && isValid = true);
         * */
        if(rawString.length() < 9) return;
        String str = rawString.substring(1, rawString.length() - 1);
        String[] strs = str.split(",");
        if(strs.length != 3) return;
        try {
            if(strs[0].equals("FR"))
            {
                dstFloor = Integer.parseInt(strs[1]);
                type = ReqType.FR;
                if(dstFloor < Floor.minFloor || dstFloor > Floor.maxFloor) return;
                if(strs[2].equals("UP"))
                {
                    if(dstFloor == Floor.maxFloor) return;
                    direction = Direction.UP;
                    isValid = true;
                }
                else if(strs[2].equals("DOWN"))
                {
                    if(dstFloor == Floor.minFloor) return;
                    direction = Direction.DOWN;
                    isValid = true;
                }
            }
            else if(strs[0].equals("ER"))
            {
                dstFloor = Integer.parseInt(strs[2]);
                type = ReqType.ER;
                if(dstFloor < Floor.minFloor || dstFloor > Floor.maxFloor) return;
                if(strs[1].equals("#1"))
                {
                    elevatorId = 1;
                }
                else if(strs[1].equals("#2"))
                {
                    elevatorId = 2;
                }
                else if(strs[1].equals("#3"))
                {
                    elevatorId = 3;
                }
                else
                    return;
                isValid = true;
            }
        }
        catch(Exception e)
        {
            isValid = false;
        }
    }
    public String toString()
    {
        /* @REQUIRES: None;
         * @MODIFIES: None;
         * @EFFECTS: \result = this.rawString;
         * */
        return rawString;
    }
    public String StringWithoutPar()
    {
        /* @REQUIRES: None;
         * @MODIFIES: None;
         * @EFFECTS: \result = this.rawString.substring(1, rawString.length() - 1);
         * */
        return rawString.substring(1, rawString.length() - 1);
    }
    public ReqType Type()
    {
        /* @REQUIRES: None;
         * @MODIFIES: None;
         * @EFFECTS: \result = this.type;
         * */
        return type;
    }
    public int DstFloor()
    {
        /* @REQUIRES: None;
         * @MODIFIES: None;
         * @EFFECTS: \result = this.dstFloor;
         * */
        return dstFloor;
    }
    public int ElevatorId()
    {
        /* @REQUIRES: None;
         * @MODIFIES: None;
         * @EFFECTS: \result = this.elevatorId;
         * */
        return elevatorId;
    }
    public Direction Direction()
    {
        /* @REQUIRES: None;
         * @MODIFIES: None;
         * @EFFECTS: \result = this.direction;
         * */
        return direction;
    }
    public long Time()
    {
        /* @REQUIRES: None;
         * @MODIFIES: None;
         * @EFFECTS: \result = this.time;
         * */
        return time;
    }
    public boolean IsValid()
    {
        /* @REQUIRES: None;
         * @MODIFIES: None;
         * @EFFECTS: \result = this.isValid;
         * */
        return isValid;
    }
}
