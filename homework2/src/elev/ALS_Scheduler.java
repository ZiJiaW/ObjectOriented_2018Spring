package elev;
import java.util.*;
public class ALS_Scheduler extends Scheduler{
	// rqs请求数组, elv电梯对象
	private int[] finished;// 已完成请求的下标数组
	private double[] finishtime;// 请求完成时间
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
		int mainRequest = -1;// 主请求下标，初始为-1
		while(getUnfinished() != -1)// 单次循环执行一次主请求和能够完成的捎带
		{
			if(mainRequest == -1)
				mainRequest = getUnfinished();
			LinkedList<Integer> tog = new LinkedList<Integer>();// 可捎带请求
			// 计算当前电梯状态
			int floor = elv.floor();
			if(rqs[mainRequest].time() > elv.time())// 电梯时间进行到至少晚于主请求时间
				elv.timeFly(rqs[mainRequest].time() - elv.time());
			double time = elv.time();
			int state = rqs[mainRequest].floor() == floor ? 0 :
						rqs[mainRequest].floor() > floor ? 1 : 2;
			int dest = rqs[mainRequest].floor();// 主请求的目标楼层
			finishtime[mainRequest] = Math.abs(dest - floor) * 0.5 + time + 1;
			//System.out.println(rqs[mainRequest]);
			
			// 添加可捎带请求
			for(int i = 0; i < n; ++i)
			{
				// 过滤和主请求同质的请求
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
				// 计算请求i发生时的电梯位置，需要考虑捎带请求
				int index = 0, lastRequest = -1;
				for(Integer k : tog)
				{
					if(finishtime[k] <= rqs[i].time())
					{
						index = tog.indexOf(k) + 1;
						lastRequest = k;
					}
					// 过滤和捎带请求同质的请求
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
				// 筛选捎带请求
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
					finishtime[i] = nowTime + Math.abs(rqs[i].floor() - nowFloor) * 0.5 + 1;// 计算新插入请求的完成时间，包括开关门
					
					//System.out.println(rqs[i]+" "+finishtime[i]+" "+nowFloor+" "+nowTime);
					
					int pos = 0;// 插入位置
					for(Integer k : tog)
					{
						//if(finishtime[k] <= finishtime[i] +0.1)
						if(state == 1 && rqs[k].floor() <= rqs[i].floor() || state == 2 && rqs[k].floor() >= rqs[i].floor())
						{
							pos = tog.indexOf(k) + 1;
						}
					}
					tog.add(pos, i);
					// 更新后方请求的结束时间（加上新请求的开关门）
					for(int k = pos + 1; k < tog.size(); ++k)
						finishtime[tog.get(k)]++;
					if((state == 1 && rqs[i].floor() < dest) || (state == 2 && rqs[i].floor() > dest))
						finishtime[mainRequest]++;
				}
			}
			// 执行
			for(Integer k : tog)
			{
				if(state == 1 && rqs[k].floor() > dest || state == 2 && rqs[k].floor() < dest) break;
				// 如果与主请求楼层一致，则按照出现顺序输出
				if(rqs[k].floor() == rqs[mainRequest].floor() && rqs[k].time() >= rqs[mainRequest].time())
				{
					executeMainrq(mainRequest, state);
					finished[mainRequest] = 1;
				}
				// 捎带中电梯一直在运动，如果同层，必然在一次开关门中完成
				if(rqs[k].floor() == elv.floor())// 同层请求
				{
					elv.timeFly(-1.0);// 回到开关门之前
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
				else// DOWN  注：STILL无捎带
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
			// 主请求变为第一个未完成的捎带请求，没有则置-1
			mainRequest = -1;
			for(Integer k : tog)
			{
				if(finished[k] == 0) mainRequest = k;
			}
		}
	}
	// 执行主请求
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
	// 无捎带时，获得一个未完成的请求下标，无未完成则返回-1
	private int getUnfinished()
	{
		for(int i = 0; i < rqs.length; ++i)
		{
			if(finished[i] == 0) return i;
		}
		return -1;
	}
}
