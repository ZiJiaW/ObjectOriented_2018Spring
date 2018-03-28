package Poly;
import java.io.*;
import java.util.regex.*;
import java.util.*;
import java.lang.Integer;
public class ComputePoly {
	private ArrayList<Polynomial> polys;
	private StringBuilder ops;
	public ComputePoly()
	{
		polys = new ArrayList<Polynomial>();
		ops = new StringBuilder();
	}
	public boolean Parse(String s)
	{
		// 首先去除所有的space, 同时处理非法字符
		StringBuilder s2 = new StringBuilder();
		for(int i = 0; i < s.length(); ++i)
		{
			char cc = s.charAt(i);
			if(cc == ' ')
				continue;
			if((cc >= '0' && cc <= '9')
				|| cc == '+' || cc == '-'
				|| cc == ','
				|| cc == '(' || cc == ')'
				|| cc == '{' || cc == '}')
			{
				s2.append(cc);
			}
			else
			{
				System.out.println("ERROR");
				System.out.println("# Got unexpected character!");
				//System.out.println(cc);
				return false;
			}
		}
		// 空输入
		if(s2.length() == 0)
		{
			System.out.println("ERROR");
			System.out.println("# Got empty input!");
			return false;
		}
		// 处理可能存在的先导符号
		boolean preminus = s2.charAt(0) == '-' ? true : false;
		if(s2.charAt(0) == '+' || s2.charAt(0) == '-')
			s2.delete(0, 1);
		// 按大括号抽取
		String pattern = "\\{[^\\{\\}]*\\}";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(s2);
		int count = 0;// 记录多项式的数目
		int polyLen = 0;// 记录代表多项式部分的长度
		while(m.find())
		{
			count++;
			polyLen += m.end() - m.start();
			if(m.end() == s2.length())
				;
			else if(s2.charAt(m.end()) == '+' || s2.charAt(m.end()) == '-')
			{
				ops.append(s2.charAt(m.end()));
			}
			else
			{
				System.out.println("ERROR");
				System.out.println("# Got illegal character between or after polynomials!");
				return false;
			}
			// curPloy: (c,n),(-1,3) 下面抽取内部每一项
			String curPoly = s2.substring(m.start()+1, m.end()-1);
			
			//System.out.println(curPoly);
			
			String reg = "\\((\\+|\\-)?[0-9]{1,6},(\\+|\\-)?[0-9]{1,6}\\)";
			Pattern pat = Pattern.compile(reg);
			Matcher mat = pat.matcher(curPoly);
			int termCount = 0;// 记录项数
			int termLen = 0; // 记录所有小括号的长度
			Vector<Integer> coes = new Vector<Integer>();// 记录系数
			Vector<Integer> pows = new Vector<Integer>();// 记录次数
			while(mat.find())
			{
				termCount++;
				termLen += mat.end()-mat.start();
				if(mat.end() != curPoly.length() && curPoly.charAt(mat.end()) != ',')
				{
					System.out.println("ERROR");
					System.out.println("# Character between terms must be a comma!");
					//System.out.println(curPoly.charAt(mat.end()));
					return false;
				}
				// now we find (+123,+23)
				String term = curPoly.substring(mat.start(), mat.end());
				
				//System.out.println(term);
				
				int pos = term.indexOf(',');
				int c = Integer.parseInt(term.substring(1, pos));
				int n = Integer.parseInt(term.substring(pos+1, term.length()-1));
				if(count == 1 && preminus == true)
					c = -c;
				if(c < -999999 || c > 999999)
				{
					System.out.println("ERROR");
					System.out.println("# Coefficient of terms should be in range (-10^6, 10^6)!");
					//System.out.println(c);
					return false;
				}
				if(n < 0 || n > 999999)
				{
					System.out.println("ERROR");
					System.out.println("# Power of terms should be in range [0, 10^6)!");
					return false;
				}
				coes.addElement(c);
				pows.addElement(n);
			}
			if(termCount > 50)
			{
				System.out.println("ERROR");
				System.out.println("# Number of terms should be less than or equal to 50!");
				return false;
			}
			if(termCount != 0 && termLen + 2 + termCount - 1 != m.end() - m.start())
			{
				System.out.println("ERROR");
				System.out.println("# Format error occurred!");
				return false;
			}
			if(termCount == 0 && m.end() - m.start() != 2)
			{
				System.out.println("ERROR");
				System.out.println("# Format error occurred!");
				return false;
			}
			
			// 检查多项式内次数是否重复，重复则输出错误
			
			if(pows.size() > 1)
			{
				for(int i = 0; i < pows.size() - 1; i++)
				{
					for(int j = i + 1; j < pows.size(); j++)
					{
						if(pows.get(i) == pows.get(j))
						{
							System.out.println("ERROR");
							System.out.println("# Got duplicate power of polynomials!");
							return false;
						}
					}
				}
			}
			
			// 获取多项式的次数
			int deg = 0;
			for(int i = 0; i < pows.size(); ++i)
			{
				if(deg < pows.get(i))
					deg = pows.get(i);
			}
			
			//System.out.println("complete polys!");
			
			Polynomial p = new Polynomial(deg);
			for(int i = 0; i < pows.size(); ++i)
			{
				p.AddTerm(coes.get(i), pows.get(i));
			}
			polys.add(p);
		}
		//System.out.println("Out the loop!");
		if(count > 20)
		{
			System.out.println("ERROR");
			System.out.println("# Number of polynomials should be less than or equal to 20!");
			return false;
		}
		if(polyLen + ops.length() != s2.length())
		{
			System.out.println("ERROR");
			System.out.println("# Brace mismatching or false +/- occurred!");
			return false;
		}
		return true;
	}
	public void Compute()
	{
		if(polys.size() != ops.length() + 1)
		{
			System.out.println("ERROR");
			System.out.println("# Number of opraters mismatches number of polys!");
			return;
		}
		Polynomial p = polys.get(0);
		if(polys.size() > 1)
		{
			//System.out.println(polys.size());
			for(int index = 1; index < polys.size(); index++)
			{
				if(ops.charAt(index - 1) == '+')
					p = p.add(polys.get(index));
				else
					p = p.sub(polys.get(index));
			}
		}
		p.print();
	}
	public static void main(String[] args)
	{
		Scanner scan = new Scanner(System.in);
		String s = scan.nextLine();
		scan.close();
		ComputePoly solution = new ComputePoly();
		if(solution.Parse(s))
			solution.Compute();
		return;
	}
}
