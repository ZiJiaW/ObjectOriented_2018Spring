package elev;
import java.util.*;
public class ALS_Scheduler extends Scheduler{
	// rqs��������, elv���ݶ���
	private int[] finished;// �����������±�����
	private double[] finishtime;// �������ʱ��
	ALS_Scheduler(Request[] r, Elevator e)
	{
		super(r, e);
		finished = new int[rqs.length];
		finishtime = new double[rqs.length];
	}
	// @override
	public void schedule()
	{
		int n = rqs.length;
		int mainRequest = -1;// �������±꣬��ʼΪ-1
		while(getUnfinished() != -1)// ����ѭ��ִ��һ����������ܹ���ɵ��Ӵ�
		{
			if(mainRequest == -1)
				mainRequest = getUnfinished();
			LinkedList<Integer> tog = new LinkedList<Integer>();// ���Ӵ�����
			// ���㵱ǰ����״̬
			int floor = elv.floor();
			if(rqs[mainRequest].time() > elv.time())// ����ʱ����е���������������ʱ��
				elv.timeFly(rqs[mainRequest].time() - elv.time());
			double time = elv.time();
			int state = rqs[mainRequest].floor() == floor ? 0 :
						rqs[mainRequest].floor() > floor ? 1 : 2;
			int dest = rqs[mainRequest].floor();// �������Ŀ��¥��
			finishtime[mainRequest] = Math.abs(dest - floor) * 0.5 + time + 1;
			//System.out.println(rqs[mainRequest]);
			
			// ��ӿ��Ӵ�����
			for(int i = 0; i < n; ++i)
			{
				// ���˺�������ͬ�ʵ�����
				if(i == mainRequest || finished[i] == 1)
					continue;
				if(rqs[i].floor() == rqs[mainRequest].floor()
				&& rqs[i].type() == rqs[mainRequest].type()
				&& rqs[i].direction() == rqs[mainRequest].direction()
				&& rqs[i].time() <= Math.abs(rqs[i].floor() - floor) * 0.5 + time + 1)
				{
					finished[i] = 1;
					System.out.println("#SAME [("+rqs[i].toString().substring(1, rqs[i].toString().length()-1)+")]");
				}
				if(state == 0) break;
				// ��������i����ʱ�ĵ���λ�ã���Ҫ�����Ӵ�����
				int index = 0, lastRequest = -1;
				for(Integer k : tog)
				{
					if(finishtime[k] <= rqs[i].time())
					{
						index = tog.indexOf(k) + 1;
						lastRequest = k;
					}
					// ���˺��Ӵ�����ͬ�ʵ�����
					if(rqs[i].floor() == rqs[k].floor()
					&& rqs[i].type() == rqs[k].type()
					&& rqs[i].direction() == rqs[k].direction()
					&& rqs[i].time() <= finishtime[k])
					{
						finished[i] = 1;
						System.out.println("#SAME [("+rqs[i].toString().substring(1, rqs[i].toString().length()-1)+")]");
					}
				}
				if(finished[i] == 1)
					continue;
				int nowFloor = 1;
				double nowTime = Math.max(rqs[i].time(), time);
				if(index == 0 && !(index < tog.size() && finishtime[tog.get(index)] - rqs[i].time() < 0.6))
				{
					if(rqs[i].time() <= elv.time())
						nowFloor = floor;
					else
						nowFloor = state == 1 ? floor + (int)((rqs[i].time() - elv.time())*2)
											  : floor - (int)((rqs[i].time() - elv.time())*2);
				}
				else if(index < tog.size() && 
						(finishtime[tog.get(index)] - rqs[i].time() < 0.6||
								(state == 1 && rqs[i].floor()>rqs[tog.get(index)].floor() 
							   ||state == 2 && rqs[i].floor()<rqs[tog.get(index)].floor())))
				{
					nowFloor = rqs[tog.get(index)].floor();
					nowTime = finishtime[tog.get(index)];
				}
				else
				{
					nowFloor = state == 1 ? rqs[lastRequest].floor() + (int)((rqs[i].time()-finishtime[lastRequest])*2)
							              : rqs[lastRequest].floor() - (int)((rqs[i].time()-finishtime[lastRequest])*2);
					nowTime = finishtime[tog.get(index - 1)];
				}
				//if(tog.size() > 0)
					//System.out.println(finishtime[tog.get(0)]);
				//System.out.println(i + " "+nowFloor+" "+floor+" "+rqs[i]+index);
				// ɸѡ�Ӵ�����
				boolean toAdd = false;
				if(state == 1 && !rqs[i].type() && rqs[i].floor() > dest)
				{
					if(rqs[i].time() <= finishtime[mainRequest]-1.1)
						toAdd = true;
				}
				else if(state == 2 && !rqs[i].type() && rqs[i].floor() < dest)
				{
					if(rqs[i].time() <= finishtime[mainRequest]-1.1)
						toAdd = true;
				}
				else if(state == 1 && ((rqs[i].type() && rqs[i].direction() && rqs[i].floor() <= dest && rqs[i].floor() > nowFloor)
						||(!rqs[i].type() && rqs[i].floor() <= dest && rqs[i].floor() > nowFloor)))
				{
					toAdd = true;
				}
				else if(state == 2 && ((rqs[i].type() && !rqs[i].direction() && rqs[i].floor() >= dest && rqs[i].floor() < nowFloor)
						|| (!rqs[i].type() && rqs[i].floor() >= dest && rqs[i].floor() < nowFloor)))
				{
					toAdd = true;
				}
				//System.out.println(toAdd);
				if(toAdd)
				{
					finishtime[i] = nowTime + Math.abs(rqs[i].floor() - nowFloor) * 0.5 + 1;// �����²�����������ʱ�䣬����������
					
					//System.out.println(rqs[i]+" "+finishtime[i]+" "+nowFloor+" "+nowTime);
					
					int pos = 0;// ����λ��
					for(Integer k : tog)
					{
						//if(finishtime[k] <= finishtime[i] +0.1)
						if(state == 1 && rqs[k].floor() <= rqs[i].floor() || state == 2 && rqs[k].floor() >= rqs[i].floor())
						{
							pos = tog.indexOf(k) + 1;
						}
					}
					tog.add(pos, i);
					// ���º�����Ľ���ʱ�䣨����������Ŀ����ţ�
					for(int k = pos + 1; k < tog.size(); ++k)
						finishtime[tog.get(k)]++;
					if((state == 1 && rqs[i].floor() < dest) || (state == 2 && rqs[i].floor() > dest))
						finishtime[mainRequest]++;
				}
			}
			// ִ��
			for(Integer k : tog)
			{
				if(state == 1 && rqs[k].floor() > dest || state == 2 && rqs[k].floor() < dest) break;
				// �����������¥��һ�£����ճ���˳�����
				if(rqs[k].floor() == rqs[mainRequest].floor() && rqs[k].time() >= rqs[mainRequest].time())
				{
					executeMainrq(mainRequest, state);
					finished[mainRequest] = 1;
				}
				// �Ӵ��е���һֱ���˶������ͬ�㣬��Ȼ��һ�ο����������
				if(rqs[k].floor() == elv.floor())// ͬ������
				{
					elv.timeFly(-1.0);// �ص�������֮ǰ
					elv.SetState(state);
					System.out.println(rqs[k]+"/"+elv);
					elv.open();
				}
				else if(state == 1)// UP
				{
					elv.up(rqs[k].floor() - elv.floor());
					System.out.println(rqs[k]+"/"+elv);
					elv.open();
				}
				else// DOWN  ע��STILL���Ӵ�
				{
					elv.down(elv.floor() - rqs[k].floor());
					System.out.println(rqs[k]+"/"+elv);
					elv.open();
				}
				finished[k] = 1;
			}
			if(finished[mainRequest] != 1)
			{
				executeMainrq(mainRequest, state);
				finished[mainRequest] = 1;
			}
			// �������Ϊ��һ��δ��ɵ��Ӵ�����û������-1
			mainRequest = -1;
			for(Integer k : tog)
			{
				if(finished[k] == 0) mainRequest = k;
			}
		}
	}
	// ִ��������
	private void executeMainrq(int mainRequest, int state)
	{
		if(finished[mainRequest] == 1) return;
		if(rqs[mainRequest].floor() == elv.floor())
		{
			elv.open();
			System.out.println(rqs[mainRequest]+"/"+elv);
		}
		else if(state == 1)
		{
			elv.up(rqs[mainRequest].floor() - elv.floor());
			System.out.println(rqs[mainRequest]+"/"+elv);
			elv.open();
		}
		else
		{
			elv.down(elv.floor() - rqs[mainRequest].floor());
			System.out.println(rqs[mainRequest]+"/"+elv);
			elv.open();
		}
	}
	// ���Ӵ�ʱ�����һ��δ��ɵ������±꣬��δ����򷵻�-1
	private int getUnfinished()
	{
		for(int i = 0; i < rqs.length; ++i)
		{
			if(finished[i] == 0) return i;
		}
		return -1;
	}
}
