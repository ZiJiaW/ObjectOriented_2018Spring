package elev;

public class Main {
	public static void main(String [] args)
	{
		RequestQueue reqq = new RequestQueue();
		reqq.ParseInput();
		Elevator e = new Elevator();
		ALS_Scheduler s = new ALS_Scheduler((Request[]) reqq.rq.toArray(new Request[reqq.rq.size()]), e);
		s.schedule();
		return;
	}
}
