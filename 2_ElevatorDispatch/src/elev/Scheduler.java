package elev;

public class Scheduler {
	Request[] rqs;
	Elevator elv;
	Scheduler(Request[] r, Elevator e)
	{
		rqs = r;
		elv = e;
	}
	public void schedule()// 原始的傻瓜调度
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
			// 过滤完全相同的请求
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
			// 过滤同质的请求
			if(rqs[i].type() == last.type() &&
			   rqs[i].floor() == last.floor() &&
			   rqs[i].time() <= elv.time() &&
			   rqs[i].direction() == last.direction())
			{
				System.out.println("#SAME [("+rqs[i].toString().substring(1, rqs[i].toString().length()-1)+")]");
				isSame = true;
			}
			if(isSame) continue;
			// 计算请求完成时间
			// 完成当前请求需要走的楼层数
			int dist = Math.abs(elv.floor() - rqs[i].floor());
			// 如果当前请求时间晚于上一条请求完成时间，电梯静止等待一段时间
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
}
