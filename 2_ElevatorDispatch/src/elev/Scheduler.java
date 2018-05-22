package elev;

public class Scheduler {
	Request[] rqs;
	Elevator elv;
	Scheduler(Request[] r, Elevator e)
	{
		rqs = r;
		elv = e;
	}
	public void schedule()// ԭʼ��ɵ�ϵ���
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
			// ������ȫ��ͬ������
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
			// ����ͬ�ʵ�����
			if(rqs[i].type() == last.type() &&
			   rqs[i].floor() == last.floor() &&
			   rqs[i].time() <= elv.time() &&
			   rqs[i].direction() == last.direction())
			{
				System.out.println("#SAME [("+rqs[i].toString().substring(1, rqs[i].toString().length()-1)+")]");
				isSame = true;
			}
			if(isSame) continue;
			// �����������ʱ��
			// ��ɵ�ǰ������Ҫ�ߵ�¥����
			int dist = Math.abs(elv.floor() - rqs[i].floor());
			// �����ǰ����ʱ��������һ���������ʱ�䣬���ݾ�ֹ�ȴ�һ��ʱ��
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
