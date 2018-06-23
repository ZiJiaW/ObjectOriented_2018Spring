package elev;
import java.util.*;
/**
 * @OVERVIEW: parse input line by line and form vector of request;
 * @INHERIT: None; 
 * @INVARIANT: None;
 * @repOk: this.rq.size() >= 0;
*/
public class RequestQueue {
	public Vector<Request> rq;
    /**
    * @REQUIRES: None;
    * @MODIFIES: this;
    * @EFFECTS: this.rq == new Vector<Request>();
    */
    RequestQueue()
    {
    	rq = new Vector<Request>();
    }

    /**
    * @REQUIRES: None;
    * @MODIFIES: this;
    * @EFFECTS:
    * r == new Request(str);
    * str == new Scanner(System.in).nextLine().replaceAll(" +","");
    * str != "RUN" && r.IsValid() && !(this.rq.size() == 0 && !r.IsRightStart()) &&
    * !(this.rq.size() > 0 && r.time() < rq.lastElement().time()) ==> this.rq.contain(new Request(str));
    */
	public void ParseInput()
	{
		Scanner sc = new Scanner(System.in);
		while(true)
		{
			String str = sc.nextLine();
			str = str.replaceAll(" +","");

			if(str.equals("RUN"))
				break;
			Request r = new Request(str);
			r.Parse();
			if(r.IsValid())
			{
				if(rq.size() == 0 && !r.IsRightStart())
				{
					System.out.println("INVALID ["+str+"]");
					continue;
				}
				if(rq.size() > 0 && r.time() < rq.lastElement().time())
				{
					System.out.println("INVALID ["+str+"]");
					continue;
				}
				rq.add(r);
			}
			else
			{
				System.out.println("INVALID ["+str+"]");
			}
		}
		sc.close();
	}
    /**
    * @REQUIRES: None;
    * @MODIFIES: None;
    * @EFFECTS: \result == this.rq.size() >= 0;
    */
	public boolean repOk() 
	{
	    return rq.size() >= 0;
	}
}
