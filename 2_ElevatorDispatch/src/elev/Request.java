package elev;
import java.lang.Long;
import java.lang.Integer;
public class Request {
	private boolean isFR;// �����������(FR: TRUE, ER: FALSE)
	private long T;// ���������ʱ��
	private boolean isUP;// ��������or����
	private int floor;// ¥���
	private String str;// һ�������ԭʼ�ַ���
	private boolean isValid;// �������Ƿ�Ϸ�
	// ���캯��
	Request(String str){
		this.str = str;
		//Parse();
	}
	// ������������󣬺Ϸ�������isValidΪtrue������ʼ������Ա����
	public void Parse()
	{
		try {
			isValid = false;
			int len = str.length();
			if(len < 8) return;// �������̳���Ϊ8: (ER,n,T) -- (FR,m,UP,T)
			// �����ж�
			if(str.charAt(0) != '(' || str.charAt(len - 1) != ')') return;
			// ȥ������
			String[] splited = str.substring(1, len - 1).split(",");
			// �ж���������
			if(splited[0].equals("FR"))
			{
				isFR = true;
				if(splited.length != 4) return;
				// �ж�������������
				if(splited[2].equals("UP"))
					isUP = true;
				else if(splited[2].equals("DOWN"))
					isUP = false;
				else return;
				// ����ʱ�������¥���
				floor = Integer.parseInt(splited[1]);
				T = Long.parseLong(splited[3]);
			}
			else if(splited[0].equals("ER"))
			{
				isFR = false;
				isUP = true;// Ϊ��һ����
				if(splited.length != 3) return;
				floor = Integer.parseInt(splited[1]);
				T = Long.parseLong(splited[2]);
				//System.out.println(T+"]");
			}
			else return;
			// T���ܳ���32λ�޷����������ֵ2^32-1
			if(floor < 1 || floor > 10 ||
					isFR && floor == 1 && !isUP ||
					isFR && floor == 10 && isUP ||
					T < 0 || T > 2147483647L)
			{
				return;
			}
			isValid = true;
		}
		catch(Exception ex) {
			isValid = false;
			return;
		}
	}
	// �ⲿ�ӿ�
	public boolean IsValid()
	{
		return isValid;
	}
	public boolean type()
	{
		return isFR;
	}
	public boolean direction()
	{
		return isUP;
	}
	public int floor()
	{
		return floor;
	}
	public long time()
	{
		return T;
	}
	public boolean IsRightStart()
	{
		return isFR && floor == 1 && isUP && T == 0;
	}
	public String toString()
	{
		//return "["+str.substring(1, str.length() - 1)+"]";
		//System.out.println(T);
		String s1, s2;
		if(isFR) s1 = "FR"; else s1 = "ER";
		if(isUP) s2 = "UP"; else s2 = "DOWN";
		if(isFR) return "["+s1+","+floor+","+s2+","+T+"]";
		else return "["+s1+","+floor+","+T+"]";
	}
	public boolean equals(Request rc)
	{
		return this.str.equals(rc.toString());
	}
}
