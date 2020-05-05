/**
 *</pre>
 * Created on 23.09.2010 14:55:35 
 * by SYGSKY
 * for project in 'ru.ts.graphics.ru.ts.colors'
 *</pre>
 */
package ru.ts.colors;

import ru.ts.common.misc.FormatInt;

import java.awt.*;

/**
 * <pre>
 * Package ru.ts.graphics.ru.ts.colors
 * Author 'SYGSKY'
 * Created  23.09.2010  14:55:35
 * Help methods for {@link Color} operation (print etc)
 * </pre>
 */
public class ColorHndl
{
	public static String toString( Color c )
	{
    return toString( c.getRGB());
/*
		return "[a=" + FormatInt.toString(c.getAlpha(), 3, false, true) +
      ",r=" + FormatInt.toString(c.getRed(), 3, false, true) +
			",g=" + FormatInt.toString( c.getGreen(), 3, false, true) +
			",b=" + FormatInt.toString( c.getBlue(), 3, false, true) + "]";
*/
	}

  public static String toString( int argb )
  {
    return "[a=" + FormatInt.toString((argb >> 24) & 0x00FF, 3, false, true) +
           ",r=" + FormatInt.toString((argb >> 16) & 0x00FF, 3, false, true) +
           ",g=" + FormatInt.toString((argb >> 8) & 0x00FF, 3, false, true) +
           ",b=" + FormatInt.toString((argb & 0x00FF), 3, false, true) + "]";
  }
}
