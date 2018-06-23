package elev;
import java.text.DecimalFormat;
/**
 * @OVERVIEW: simulate elevator's movement;
 * @INHERIT: None; 
 * @INVARIANT: None;
 * @repOK:this.floor>=1&&this.floor<=10 && (this.state==1||this.state==2||this.state==0) && this.time >= 0;
*/
public class Elevator implements ElevAction{
	private int floor;
	private int state;// still; 1: up; 2: down;
	private double time;
	/**
	* @REQUIRES: None;
	* @MODIFIES: this;
	* @EFFECTS: this.floor == 1;
	* this.time == 0;
	* this.state == 0;
	*/
	Elevator()
	{
		floor = 1;
		time = 0;
		state = 0;
	}
	/**
    * @REQUIRES: n >= 0 && n <= 10 - this.floor;
    * @MODIFIES: this;
    * @EFFECTS: this.floor == \old(this).floor + n;
    * this.time == \old(this).time + 0.5*n;
    * this.state == 1;
    */
	public void up(int n)
	{
		floor += n;
		time += 0.5 * n;
		state = 1;
	}
	/**
    * @REQUIRES: n >= 0 && n <= this.floor;
    * @MODIFIES: this;
    * @EFFECTS: this.floor == \old(this).floor - n;
    * this.time == \old(this).time + 0.5*n;
    * this.state == 2;
    */
	public void down(int n)
	{
		floor -= n;
		time += 0.5 * n;
		state = 2;
	}
	/**
    * @REQUIRES: None;
    * @MODIFIES: this;
    * @EFFECTS: this.time == \old(this).time + 1;
    * this.state == 0;
    */
	public void open()
	{
		time++;
		state = 0;
	}
	/**
    * @REQUIRES: n >=0 && n <= 2;
    * @MODIFIES: this.state;
    * @EFFECTS: this.state == n;
    */
	public void SetState(int n)
	{
		state = n;
	}
    /**
    * @REQUIRES: None;
    * @MODIFIES: this.time;
    * @EFFECTS: this.time == \old(this).time + n;
    */
	public void timeFly(double n)
	{
		time += n;
	}
    /**
    * @REQUIRES: None;
    * @MODIFIES: None;
    * @EFFECTS: \result == this.time;
    */
	public double time()
	{
		return time;
	}
    /**
    * @REQUIRES: None;
    * @MODIFIES: None;
    * @EFFECTS: \result == this.state;
    */
	public int state()
	{
		return state;
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
    * @EFFECTS: \result == "("+this.floor+","+this.state+","+this.time+")";
    */
	public String toString()
	{
		String st = state == 0 ? "STILL" : state == 1 ? "UP" : "DOWN";
		DecimalFormat format = new DecimalFormat("#0.0");
		return "("+floor+","+st+","+format.format(time)+")";
	}
    /**
    * @REQUIRES: None;
    * @MODIFIES: None;
    * @EFFECTS: \result == this.floor>=1&&this.floor<=10 && (this.state==1||this.state==2||this.state==0) && this.time >= 0;
    */
	public boolean repOk()
	{
	    return floor>=1&&floor<=10 && (state==1||state==2||state==0) && time>=0;
	}
}
