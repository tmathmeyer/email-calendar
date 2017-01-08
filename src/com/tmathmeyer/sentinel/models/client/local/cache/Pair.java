package com.tmathmeyer.sentinel.models.client.local.cache;

/**
*
* @param <A> the type of the first element
* @param <B> the type of the second element
*/
public class Pair<A, B> {

	private A a;
	private B b;
	
	/**
	 * creates a generic pair of two options. mostly used for caching and function returns
	 * 
	 * @param a
	 * @param b
	 */
	public Pair(A a, B b)
	{
		this.setA(a);
		this.setB(b);
	}

	/**
	 * 
	 * @return the first element in the pair
	 */
	public A getA() {
		return a;
	}

	/**
	 * 
	 * @param a set the first element in the pair
	 */
	public void setA(A a) {
		this.a = a;
	}

	/**
	 * 
	 * @return the second element in the pair
	 */
	public B getB() {
		return b;
	}

	/**
	 * 
	 * @param b set the second element in the pair
	 */
	public void setB(B b) {
		this.b = b;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof Pair))
		{
			return false;
		}
		Pair<?, ?> casted = (Pair<?, ?>)other;
		return casted.getA().equals(getA()) && casted.getB().equals(getB());
	}
	
	@Override
	public int hashCode()
	{
		return this.getA().hashCode() ^ this.getB().hashCode();
	}
}