package elev;
import java.util.*;

public class RequestQueue {
	public Vector<Request> rq;// �������е���������
    RequestQueue()
    {
    	rq = new Vector<Request>();
    }
    // һ��һ�ж�ȡ������з���������ȷ����Ч���벢���
	public void ParseInput()
	{
		Scanner sc = new Scanner(System.in);
		while(true)
		{
			String str = sc.nextLine();
			str = str.replaceAll(" +","");// ȥ�ո�	
			// ����RUN����
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
