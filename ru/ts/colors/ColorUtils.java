/**
 * Created on 06.03.2009 11:40:18<br> 
 * by Syg<br> 
 * for project in 'ru.ts.common.misc'
 */
package ru.ts.colors;

import ru.ts.common.misc.FormatInt;
import ru.ts.common.misc.Text;

import java.awt.*;
import java.util.HashMap;

/**
 * Package ru.ts.common.misc<br> Author 'Syg'<br> Created  06.03.2009
 * 11:40:18<br> To change this template use File | Settings | File Templates.
 */
public class ColorUtils
{
	private static HashMap<String, Color> _colors = new HashMap<String, Color>( 13 );

	static
	{
		_colors.put( "BLACK", Color.BLACK );
		_colors.put( "BLUE", Color.BLUE );
		_colors.put( "CYAN", Color.CYAN );
		_colors.put( "DARKGRAY", Color.DARK_GRAY );
		_colors.put( "GRAY", Color.GRAY );
		_colors.put( "GREEN", Color.GREEN );
		_colors.put( "LIGHTGRAY", Color.LIGHT_GRAY );
		_colors.put( "MAGENTA", Color.MAGENTA );
		_colors.put( "ORANGE", Color.ORANGE );
		_colors.put( "PINK", Color.PINK );
		_colors.put( "RED", Color.RED );
		_colors.put( "YELLOW", Color.YELLOW );
		_colors.put( "WHITE", Color.WHITE );
	}

	/**
	 * Parse string in format: RNNN/GNNN/BNNN or 0xRRGGBB to integer with R,G,B in
	 * corresponding bytes
	 *
	 * @param RGB    String in corresponding format RNNN/GNNN/BNNN or R###G###B###
	 *               in any combination, that is G###B### of B###R### or R###  -
	 *               all will be acepted, absent color componetes will be replaced
	 *               by zero.
	 * @param defVal Color value to be returned if incorrect string used
	 *
	 * @return Color with color components read from string<br> <b>If any error,
	 *         {@code null} is returned</b>
	 */
	public static Color parseColor( String RGB, Color defVal )
	{
		if ( Text.isEmpty( RGB ) )
			return defVal;
		RGB = RGB.trim().toUpperCase();
		if ( RGB.startsWith( "0X" ) )
		{
			RGB = RGB.substring( 2 );
			try
			{
				int val = Integer.parseInt( RGB, 16 );
				return new Color( val, false );
			}
			catch( Exception e )
			{
				return defVal;
			}
		}
		if ( _colors.containsKey( RGB ) )
			return _colors.get( RGB );
		Color col;
		int R = 0, G = 0, B = 0;
		String[] vals; // 1st Red, 2nd Green, 3rd Blue
		/*first find is separator exists */
		int pos = RGB.indexOf( '/' );
		if ( pos >= 0 )
			RGB = Text.join( Text.splitItems( RGB, '/', false ) );
		/* parse string without separators e.g. "R###G###B##" or "G###B###R###" etc */
		vals = new String[] { "0" , "0" , "0" };
		StringBuffer sb = new StringBuffer();
		int ind = -1;
		for ( int i = 0; i < RGB.length(); i++ )
		{
			char ch = RGB.charAt( i );
			if ( Character.isDigit( ch ) )
			{
				sb.append( ch );
			}
			else
			{
				if ( sb.length() > 0 )
				{
					vals[ ind ] = sb.toString();
					sb.delete( 0, Integer.MAX_VALUE );
				}
				switch ( ch )
				{
					case 'R':
						ind = 0;
						break;
					case 'G':
						ind = 1;
						break;
					case 'B':
						ind = 2;
						break;
					default:
						System.err.println( "Illegal symbol '" + ch + "' at pos " + i + " in \"" + RGB + "\"" );
						return null;
				}
			}
		}
		try
		{
			if ( sb.length() != 0 )
				vals[ ind ] = sb.toString();
		}
		catch ( Exception e )
		{
			return defVal;
		}
		R = Integer.valueOf( vals[ 0 ] );
		G = Integer.valueOf( vals[ 1 ] );
		B = Integer.valueOf( vals[ 2 ] );
		return new Color( R, G, B );
	}

	/**
	 * Format color to the string as follow  "R###G###B###"
	 *
	 * @return
	 */
	public static String color2Str( Color color )
	{
    return color2Str( color.getRGB());
	}

	/**
	 * Format color to the string as follow  "0xAARRGGBB", wher AA is alpha, RR is red, GG is green and BB is blue color
	 * components correspondently
	 *
	 * @return String with color interpratation as hexadecimal int32
	 */
	public static String color2HexStr( Color color )
	{
		return String.format("0x%8x",color.getRGB() );
	}

  /**
   * Appends text representation of the color component ( R, G or B kind).
   * It contain value from 0 to 255
   * @param colorComponent int value in range 0..255
   * @param sb {@link StringBuffer} instance to apped created value
   * @return String with 3 characters with leading zeroes if needed
   */
  private static StringBuffer addColorStr( int colorComponent, StringBuffer sb )
  {
    if ( sb == null)
      sb = new StringBuffer( );
    sb.append(FormatInt.toString( colorComponent & 0x000000FF, 3 ) );
    return sb;
  }
  /**
   * Format color to the string as follow  "R###G###B###"
   *
   * @return
   */
  public static String color2Str( int rgb )
  {
    final StringBuffer sb = new StringBuffer( 12 );
    addColorStr( (rgb & 0x00FF0000) >>>16, sb.append( 'R' ) );
    addColorStr( (rgb & 0x0000FF00) >>> 8, sb.append( 'G' ) );
    addColorStr(  rgb & 0x000000FF       , sb.append( 'B' ) );
    return sb.toString();
  }
}
