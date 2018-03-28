package Poly;

public class Polynomial {
    private int[] coefficients;
    private int degree;
    public Polynomial(int idegree)
    {
    	degree = idegree;
    	coefficients = new int[idegree + 1];
    }
    public void AddTerm(int c, int n)
    {
    	coefficients[n] = c;
    }
    public int degree()
    {
    	return degree;
    }
    public int coeffs(int deg)
    {
    	return coefficients[deg];
    }
    
    public Polynomial add(Polynomial q)
    {
    	int len = Math.max(degree, q.degree()) + 1;
    	Polynomial result = new Polynomial(len);
    	int index = 0;
    	while(index < len)
    	{
    		if(index <= Math.min(degree, q.degree()))
    		{
    			result.AddTerm(coefficients[index] + q.coeffs(index), index);
    		}
    		else if(index <= degree)
    		{
    			result.AddTerm(coefficients[index], index);
    		}
    		else
    		{
    			result.AddTerm(q.coeffs(index), index);
    		}
    		index++;
    	}
    	return result;
    }
    
    public Polynomial sub(Polynomial q)
    {
    	int len = Math.max(degree, q.degree()) + 1;
    	Polynomial result = new Polynomial(len);
    	int index = 0;
    	while(index < len)
    	{
    		if(index <= Math.min(degree, q.degree()))
    		{
    			result.AddTerm(coefficients[index] - q.coeffs(index), index);
    		}
    		else if(index <= degree)
    		{
    			result.AddTerm(coefficients[index], index);
    		}
    		else
    		{
    			result.AddTerm(-q.coeffs(index), index);
    		}
    		index++;
    	}
    	return result;
    }
    public void print()
    {
    	//System.out.println("OK!");
    	StringBuilder s = new StringBuilder("{");
    	for(int i = 0; i < coefficients.length; i++)
    	{
    		if(coefficients[i] == 0)
    			continue;
    		s.append('(');
    		s.append(String.valueOf(coefficients[i]));
    		s.append(',');
    		s.append(String.valueOf(i));
    		s.append("),");
    	}
    	if(s.length() == 1)
    	{
    		System.out.println("{}");
    		return;
    	}
    	s.deleteCharAt(s.length() - 1);
    	s.append('}');
    	System.out.println(s);
    }
}
