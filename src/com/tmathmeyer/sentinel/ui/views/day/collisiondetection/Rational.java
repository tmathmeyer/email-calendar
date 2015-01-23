/*******************************************************************************
 * Copyright (c) 2013 WPI-Suite
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Team YOCO (You Only Compile Once)
 ******************************************************************************/
package com.tmathmeyer.sentinel.ui.views.day.collisiondetection;

/**
 * Used for scalable fractions in day panel. Non-simplifying
 */
public class Rational
{
	private int numerator;
	private int denominator;

	/**
	 * A rational number is defined as the any number that can be expressed as a
	 * fraction of two whole numbers
	 * 
	 * @param num the numerator
	 * @param denom the denominator
	 */
	public Rational(int num, int denom)
	{
		numerator = num;
		denominator = denom;
	}

	/**
	 * effectively maps this rational onto the given integer
	 * 
	 * @param width the maximum
	 * @return the mapped value
	 */
	public int toInt(int width)
	{
		return (int) Math.round(((double) width * numerator) / (double) denominator);
	}

	/**
	 * 
	 * @return the numerator
	 */
	public int getNumerator()
	{
		return numerator;
	}

	/**
	 * 
	 * @return the denominator
	 */
	public int getDenominator()
	{
		return denominator;
	}

	/**
	 * 
	 * @param rational the rational to multiply this by
	 * @return the product of this and the provided rational
	 */
	public Rational multiply(Rational rational)
	{
		return new Rational(numerator * rational.numerator, denominator * rational.denominator);
	}

	/**
	 * 
	 * @param rational the rational to add this to
	 * @return the sum of this and the provided rational
	 */
	public Rational addition(Rational rational)
	{
		return new Rational(rational.denominator * numerator + rational.numerator * denominator, denominator
		        * rational.denominator);
	}

	/**
	 * 
	 * @param rational the rational to subtract from this
	 * @return the difference between this and the provided rational
	 */
	public Rational subtract(Rational rational)
	{
		return this.addition(rational.negate());
	}

	/**
	 * 
	 * @param rational the rational to divide this by
	 * @return the quotient of this / rational
	 */
	public Rational divide(Rational rational)
	{
		return this.multiply(rational.inverse());
	}

	/**
	 * 
	 * @return one divided by this rational
	 */
	public Rational inverse()
	{
		return new Rational(denominator, numerator);
	}

	/**
	 * 
	 * @return 0 - this rational
	 */
	public Rational negate()
	{
		return new Rational(-numerator, denominator);
	}

	@Override
	public String toString()
	{
		return String.format("{%d/%d}", numerator, denominator);
	}

}
