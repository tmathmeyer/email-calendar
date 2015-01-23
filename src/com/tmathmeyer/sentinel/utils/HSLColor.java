/*******************************************************************************
 * Copyright (c) 2013 WPI-Suite
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Team YOCO (You Only Compile Once)
 ******************************************************************************/
package com.tmathmeyer.sentinel.utils;

import java.awt.Color;

public class HSLColor
{

	private Color rgb;
	private float[] hsl;
	private float alpha;

	/**
	 * 
	 * @param rgb the color as integer to make the HSL from
	 */
	public HSLColor(int rgb)
	{
		this(new Color(rgb));
	}

	/**
	 * 
	 * @param rgb the color as Color object to make the HSL from
	 */
	public HSLColor(Color rgb)
	{
		this.rgb = rgb;
		hsl = fromRGB(rgb);
		alpha = rgb.getAlpha() / 255.0f;
	}

	/**
	 * makes an HSL color based on hsl vals
	 * 
	 * @param h hue
	 * @param s saturation
	 * @param l luminance
	 */
	public HSLColor(float h, float s, float l)
	{
		this(h, s, l, 1.0f);
	}

	/**
	 * 
	 * @param h hue
	 * @param s saturation
	 * @param l luminance
	 * @param alpha transparency
	 */
	public HSLColor(float h, float s, float l, float alpha)
	{
		hsl = new float[] { h, s, l };
		this.alpha = alpha;
		rgb = toRGB(hsl, alpha);
	}

	/**
	 * 
	 * @param hsl the HSL values as an array
	 * @throws Exception if the user-supplied array is not valid for hsl
	 */
	public HSLColor(float[] hsl) throws Exception
	{
		this(hsl, 1.0f);
	}

	/**
	 * 
	 * @param hsl the hsl values as an array
	 * @param alpha the transparency
	 * @throws Exception if the user-supplied array is not valid for hsl
	 */
	public HSLColor(float[] hsl_in, float alpha) throws Exception
	{
		if (hsl_in.length != 3)
		{
			throw new Exception("Color specified with an invalid array");
		}
		// never copy arrays directly! bad for security.
		hsl[0] = hsl_in[0];
		hsl[1] = hsl_in[1];
		hsl[2] = hsl_in[2];
		this.alpha = alpha;
		rgb = toRGB(hsl, alpha);
	}

	/**
	 * 
	 * @param degrees change hues by certain degree (around the color wheel)
	 * @return the Color object that represents this HSLColor after having been
	 *         rotated
	 */
	public Color adjustHue(float degrees)
	{
		return toRGB(degrees, hsl[1], hsl[2], alpha);
	}

	/**
	 * 
	 * @param percent the percent to set the luminance to. (0=black 1=white)
	 * @return the color object that represents this HSLColor after being
	 *         adjusted
	 */
	public Color adjustLuminance(float percent)
	{
		return toRGB(hsl[0], hsl[1], percent, alpha);
	}

	/**
	 * 
	 * @param percent the percent to set the saturation to
	 * @return the color object that represents this HSLColor after being
	 *         adjusted
	 */
	public Color adjustSaturation(float percent)
	{
		return toRGB(hsl[0], percent, hsl[2], alpha);
	}

	/**
	 * 
	 * @param percent the percentage to set the shade to.
	 * @return the color object that represents this HSLColor after being
	 *         adjusted
	 */
	public Color adjustShade(float percent)
	{
		float multiplier = (100.0f - percent) / 100.0f;
		float l = Math.max(0.0f, hsl[2] * multiplier);
		return toRGB(hsl[0], hsl[1], l, alpha);
	}

	/**
	 * 
	 * @param percent the percentage to set the tone to
	 * @return the color object that represents this HSLColor after being
	 *         adjusted
	 */
	public Color adjustTone(float percent)
	{
		float multiplier = (100.0f + percent) / 100.0f;
		float l = Math.min(100.0f, hsl[2] * multiplier);

		return toRGB(hsl[0], hsl[1], l, alpha);
	}

	/**
	 * 
	 * @return the transparency value
	 */
	public float getAlpha()
	{
		return alpha;
	}

	/**
	 * 
	 * @return the color on the direct opposite side of the color wheel
	 */
	public Color getComplementary()
	{
		float hue = (hsl[0] + 180.0f) % 360.0f;
		return toRGB(hue, hsl[1], hsl[2]);
	}

	/**
	 * 
	 * @return the hue of the color
	 */
	public float getHue()
	{
		return hsl[0];
	}

	/**
	 * 
	 * @return the color's hue, saturation, and luminance, as an array
	 */
	public float[] getHSL()
	{
		return hsl;
	}

	/**
	 * 
	 * @return the luminance of the color
	 */
	public float getLuminance()
	{
		return hsl[2];
	}

	/**
	 * 
	 * @return this color converted to a standard java color object
	 */
	public Color getRGB()
	{
		return rgb;
	}

	/**
	 * 
	 * @return this color's saturation
	 */
	public float getSaturation()
	{
		return hsl[1];
	}

	@Override
	public String toString()
	{
		String toString = "HSLColor[h=" + hsl[0] + ",s=" + hsl[1] + ",l=" + hsl[2] + ",alpha=" + alpha + "]";

		return toString;
	}

	/**
	 * 
	 public Color getComplementary(
	 * 
	 * @param color the color to turn into float vals
	 * @return the provided color as HSL float vals
	 */
	public static float[] fromRGB(Color color)
	{

		float[] rgb = color.getRGBColorComponents(null);
		float r = rgb[0];
		float g = rgb[1];
		float b = rgb[2];

		float min = Math.min(r, Math.min(g, b));
		float max = Math.max(r, Math.max(g, b));

		float h = 0;

		if (max == min)
		{
			h = 0;
		} else if (max == r)
		{
			h = ((60 * (g - b) / (max - min)) + 360) % 360;
		} else if (max == g)
		{
			h = (60 * (b - r) / (max - min)) + 120;
		} else if (max == b)
		{
			h = (60 * (r - g) / (max - min)) + 240;
		}

		float l = (max + min) / 2;

		float s = 0;

		if (max == min)
		{
			s = 0;
		} else if (l <= .5f)
		{
			s = (max - min) / (max + min);
		} else
		{
			s = (max - min) / (2 - max - min);
		}

		return new float[] { h, s * 100, l * 100 };
	}

	/**
	 * 
	 * @param hsl the HSL values to turn into a java Color
	 * @return the Color Object of these HSL vals
	 */
	public static Color toRGB(float[] hsl)
	{
		return toRGB(hsl, 1.0f);
	}

	/**
	 * 
	 * @param hsl the HSL values of the color
	 * @param alpha the transparency of the color
	 * @return the color made from these values
	 */
	public static Color toRGB(float[] hsl, float alpha)
	{
		return toRGB(hsl[0], hsl[1], hsl[2], alpha);
	}

	/**
	 * 
	 * @param h the hue
	 * @param s the saturation
	 * @param l the luminance
	 * @return the color made from this HSL
	 */
	public static Color toRGB(float h, float s, float l)
	{
		return toRGB(h, s, l, 1.0f);
	}

	/**
	 * 
	 * @param h the hue
	 * @param s the saturation
	 * @param l the luminance
	 * @param alpha the transparency
	 * @return the color made from these values
	 */
	public static Color toRGB(float h, float s, float l, float alpha)
	{
		if (s < 0.0f || s > 100.0f)
		{
			String message = "Color parameter outside of expected range - Saturation";
			throw new IllegalArgumentException(message);
		}

		if (l < 0.0f || l > 100.0f)
		{
			String message = "Color parameter outside of expected range - Luminance";
			throw new IllegalArgumentException(message);
		}

		if (alpha < 0.0f || alpha > 1.0f)
		{
			String message = "Color parameter outside of expected range - Alpha";
			throw new IllegalArgumentException(message);
		}

		h = h % 360.0f;
		h /= 360f;
		s /= 100f;
		l /= 100f;

		float q = 0;

		if (l < 0.5)
		{
			q = l * (1 + s);
		} else
		{
			q = (l + s) - (s * l);
		}

		float p = 2 * l - q;

		float r = Math.max(0, HueToRGB(p, q, h + (1.0f / 3.0f)));
		float g = Math.max(0, HueToRGB(p, q, h));
		float b = Math.max(0, HueToRGB(p, q, h - (1.0f / 3.0f)));

		r = Math.min(r, 1.0f);
		g = Math.min(g, 1.0f);
		b = Math.min(b, 1.0f);

		return new Color(r, g, b, alpha);
	}

	/**
	 * 
	 * @param p the P
	 * @param q the Q
	 * @param h the H
	 * @return the float made from these p,q,h
	 */
	private static float HueToRGB(float p, float q, float h)
	{
		if (h < 0)
		{
			h += 1;
		}

		if (h > 1)
		{
			h -= 1;
		}

		if (6 * h < 1)
		{
			return p + ((q - p) * 6 * h);
		}

		if (2 * h < 1)
		{
			return q;
		}

		if (3 * h < 2)
		{
			return p + ((q - p) * 6 * ((2.0f / 3.0f) - h));
		}

		return p;
	}
}
