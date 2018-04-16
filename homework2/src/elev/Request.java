package elev;
import java.lang.Long;
import java.lang.Integer;
public class Request {
	private boolean isFR;// 该请求的类型(FR: TRUE, ER: FALSE)
	private long T;// 发出请求的时间
	private boolean isUP;// 请求上行or下行
	private int floor;// 楼层号
	private String str;// 一行请求的原始字符串
	private boolean isValid;// 该请求是否合法
	// 构造函数
	Request(String str){
		this.str = str;
	}
	// 解析输入的请求，合法则设置isValid为true，并初始化各成员变量
	public void Parse()
	{
		try {
			isValid = false;
			int len = str.length();
			if(len < 8) return;// 请求的最短长度为8: (ER,n,T) -- (FR,m,UP,T)
			// 括号判断
			if(str.charAt(0) != '(' || str.charAt(len - 1) != ')') return;
			// 去除括号
			String[] splited = str.substring(1, len - 1).split(",");
			// 判断请求类型
			if(splited[0].equals("FR"))
			{
				isFR = true;
				if(splited.length != 4) return;
				// 判断上行下行请求
				if(splited[2].equals("UP"))
					isUP = true;
				else if(splited[2].equals("DOWN"))
					isUP = false;
				else return;
				// 解析时间和请求楼层号
				floor = Integer.parseInt(splited[1]);
				T = Long.parseLong(splited[3]);
			}
			else if(splited[0].equals("ER"))
			{
				isFR = false;
				isUP = true;// 为了一致性
				if(splited.length != 3) return;
				floor = Integer.parseInt(splited[1]);
				T = Long.parseLong(splited[2]);
			}
			else return;
			// T不能超过32位无符号整数最大值2^32-1
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
	// 外部接口
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
		return "["+str.substring(1, str.length() - 1)+"]";
	}
	public boolean equals(Request rc)
	{
		return this.str.equals(rc.toString());
	}
}
