package elev;
import java.lang.Long;
import java.lang.Integer;
/**
 * @OVERVIEW: record all info of a request;
 * @INHERIT: None;
 * @INVARIANT: None;
 * @repOk: this.str!= null;
*/
public class Request {
	private boolean isFR;// (FR: TRUE, ER: FALSE)
	private long T;
	private boolean isUP;
	private int floor;
	private String str;
	private boolean isValid;
	
    /**
    * @REQUIRES: None;
    * @MODIFIES: this.str;
    * @EFFECTS: this.str == str;
    */
	Request(String str){
		this.str = str;
		//Parse();
	}
	
    /**
    * @REQUIRES: None;
    * @MODIFIES: this;
    * @EFFECTS: splited == this.str.substring(1, len - 1).split(",");
    * this.floor == Integer.parseInt(splited[1]);
    * this.T == splited[0] == "FR" ? Long.parseLong(splited[3]) : Long.parseLong(splited[2]);
    * this.isFR = splited[0] == "FR";
    * this.isUP = splited[2] == "UP" || !this.isFR;
    * this.isValid == this.length >= 8 && this.firstElement == '(' && this.lastElement == ')' &&
    *                 ((splited[0] == "FR" && splited.length == 4 && (splited[2] == "UP" || splited[2] == "DOWN"))
    *                 ||(splited[0] == "ER" && splited.length == 3)) && this.floor >=1 && this.floor <= 10 &&
    *                 !(this.isFR && this.floor == 1 && !this.isUP || this.isFR && this.floor == 10 && this.isUP)
    *                 && T >= 0 && T <= 2147483647L;
    */
	public void Parse()
	{
		try {
			isValid = false;
			int len = str.length();
			if(len < 8) return;// 8: (ER,n,T) -- (FR,m,UP,T)

			if(str.charAt(0) != '(' || str.charAt(len - 1) != ')') return;

			String[] splited = str.substring(1, len - 1).split(",");

			if(splited[0].equals("FR"))
			{
				isFR = true;
				if(splited.length != 4) return;
				
				if(splited[2].equals("UP"))
					isUP = true;
				else if(splited[2].equals("DOWN"))
					isUP = false;
				else return;
				
				floor = Integer.parseInt(splited[1]);
				T = Long.parseLong(splited[3]);
			}
			else if(splited[0].equals("ER"))
			{
				isFR = false;
				isUP = true;
				if(splited.length != 3) return;
				floor = Integer.parseInt(splited[1]);
				T = Long.parseLong(splited[2]);
				//System.out.println(T+"]");
			}
			else return;
			
			if(floor < 1 || floor > 10 ||
					isFR && floor == 1 && !isUP ||
					isFR && floor == 10 && isUP ||
					T < 0 || T > 2147483647L)
			{
				return;
			}
			isValid = true;
		}
		catch(Exception ex) {
			isValid = false;
			return;
		}
	}
	
    /**
    * @REQUIRES: None;
    * @MODIFIES: None;
    * @EFFECTS: \result == this.isValid;
    */
	public boolean IsValid()
	{
		return isValid;
	}
    /**
    * @REQUIRES: None;
    * @MODIFIES: None;
    * @EFFECTS: \result == this.isFR;
    */
	public boolean type()
	{
		return isFR;
	}
    /**
    * @REQUIRES: None;
    * @MODIFIES: None;
    * @EFFECTS: \result == this.isUP;
    */
	public boolean direction()
	{
		return isUP;
	}
    /**
    * @REQUIRES: None;
    * @MODIFIES: None;
    * @EFFECTS: \result == this.floor;
    */
	public int floor()
	{
		return floor;
	}
    /**
    * @REQUIRES: None;
    * @MODIFIES: None;
    * @EFFECTS: \result == this.T;
    */
	public long time()
	{
		return T;
	}
    /**
    * @REQUIRES: None;
    * @MODIFIES: None;
    * @EFFECTS: \result == this.isFR && this.floor == 1 && this.isUP && this.T == 0;
    */
	public boolean IsRightStart()
	{
		return isFR && floor == 1 && isUP && T == 0;
	}
    /**
    * @REQUIRES: None;
    * @MODIFIES: None;
    * @EFFECTS: 
    * this.isUP ==> s2 == "UP";
    * !isUP ==> s2 == "DOWN";
    * this.isFR ==> s1 == "FR" && \result == "["+s1+","+this.floor+","+s2+","+this.T+"]";
    * !this.isFR ==> s1 == "ER" && \result == "["+s1+","+this.floor+","+this.T+"]";
    */
	public String toString()
	{
		//return "["+str.substring(1, str.length() - 1)+"]";
		//System.out.println(T);
		String s1, s2;
		if(isFR) s1 = "FR"; else s1 = "ER";
		if(isUP) s2 = "UP"; else s2 = "DOWN";
		if(isFR) return "["+s1+","+floor+","+s2+","+T+"]";
		else return "["+s1+","+floor+","+T+"]";
	}
    /**
    * @REQUIRES: None;
    * @MODIFIES: None;
    * @EFFECTS: \result == this.toString().equals(rc.toString());
    */
	public boolean equals(Request rc)
	{
		return this.toString().equals(rc.toString());
	}
    /**
    * @REQUIRES: None;
    * @MODIFIES: None;
    * @EFFECTS: \result == this.str!= null;
    */
	public boolean repOk()
	{
	    return str != null;
	}
}
