package elev;
import java.util.*;

public class RequestQueue {
	public Vector<Request> rq;// 储存所有的请求序列
    RequestQueue()
    {
    	rq = new Vector<Request>();
    }
    // 一行一行读取输入进行分析，初步确定无效输入并输出
	public void ParseInput()
	{
		Scanner sc = new Scanner(System.in);
		while(true)
		{
			String str = sc.nextLine();
			str = str.replaceAll(" +","");// 去空格	
			// 读到RUN结束
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
}
