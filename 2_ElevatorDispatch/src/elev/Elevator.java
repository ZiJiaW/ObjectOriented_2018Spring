package elev;
import java.text.DecimalFormat;
public class Elevator implements ElevAction{
	private int floor;// ��������¥����
	private int state;// ����״̬ 0: still; 1: up; 2: down;
	private double time;// ��ǰʱ��
	Elevator()
	{
		floor = 1;
		time = 0;
		state = 0;
	}
	public void up(int n)
	{
		floor += n;
		time += 0.5 * n;
		state = 1;
	}
	public void down(int n)
	{
		floor -= n;
		time += 0.5 * n;
		state = 2;
	}
	public void open()
	{
		time++;
		state = 0;
	}
	public void SetState(int n)
	{
		state = n;
	}
	public void timeFly(double n)
	{
		time += n;
	}
	public double time()
	{
		return time;
	}
	public int state()
	{
		return state;
	}
	public int floor()
	{
		return floor;
	}
	public String toString()
	{
		String st = state == 0 ? "STILL" : state == 1 ? "UP" : "DOWN";
		DecimalFormat format = new DecimalFormat("#0.0");
		return "("+floor+","+st+","+format.format(time)+")";
	}
}
