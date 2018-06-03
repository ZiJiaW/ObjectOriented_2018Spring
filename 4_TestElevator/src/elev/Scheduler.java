package elev;
/**
 * @OVERVIEW: fool schedule requests to manage the elevator system;
 * @INHERIT: None; 
*/
public class Scheduler {
	Request[] rqs;
	Elevator elv;
    /**
    * @REQUIRES: None;
    * @MODIFIES: this;
    * @EFFECTS: this.rqs == r;
    * this.elv == e;
    */
	Scheduler(Request[] r, Elevator e)
	{
		rqs = r;
		elv = e;
	}
    /**
    * @REQUIRES: None;
    * @MODIFIES: this.elv;
    * @EFFECTS: this.elv.floor == this.rqs.lastElement.floor();
    * this.elv.time == finishTime;
    * this.elv.direction == finishDirection;
    */
	public void schedule()
	{
		int n = rqs.length;
		Request last = rqs[0];
		for(int i = 0; i < n; ++i)
		{
			if(i == 0)
			{
				elv.open();
				System.out.println(rqs[i]+"/"+elv);
				continue;
			}
			boolean isSame = false;
			for(int j = 0; j < i; ++j)
			{
				if(rqs[i].equals(rqs[j]))
				{
					System.out.println("#SAME [("+rqs[i].toString().substring(1, rqs[i].toString().length()-1)+")]");
					isSame = true;
					break;
				}
			}
			if(rqs[i].type() == last.type() &&
			   rqs[i].floor() == last.floor() &&
			   rqs[i].time() <= elv.time() &&
			   rqs[i].direction() == last.direction())
			{
				System.out.println("#SAME [("+rqs[i].toString().substring(1, rqs[i].toString().length()-1)+")]");
				isSame = true;
			}
			if(isSame) continue;

			int dist = Math.abs(elv.floor() - rqs[i].floor());

			if(rqs[i].time() > elv.time())
				elv.timeFly(rqs[i].time() - elv.time());
			if(elv.floor() > rqs[i].floor())
				elv.down(dist);
			else
				elv.up(dist);
			System.out.println(rqs[i]+"/"+elv);
			elv.open();
			last = rqs[i];
		}
	}
    /**
    * @REQUIRES: None;
    * @MODIFIES: None;
    * @EFFECTS: \result == this.elv.repOk();
    */
	public boolean repOk()
	{
	    return elv.repOk();
	}
}
