package jhc.image;

import java.awt.image.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: sigolaev_va
 * Date: 28.10.2013
 * Time: 14:43:05
 * Plays with a LUT functionality
 */
public class LUT
{

	/**
	 * Apllies gray LUT only
	 *
	 * @param lut
	 * @param img
	 * @throws IllegalArgumentException
	 */
	public static void applyLUT( int[] lut, BufferedImage img ) throws IllegalArgumentException
	{
		if ( lut == null || img == null )
			throw new IllegalArgumentException( "null value for int lut[] or BufferedImage img parameters" );
		if ( lut.length < 256 )
			throw new IllegalArgumentException( "int lut[] length must be >= 256" );
		ColorModel cm = img.getColorModel();
		for ( int y = 0; y < img.getHeight(); y++ )
		{
			for ( int x = 0; x < img.getWidth(); x++ )
			{
				int rgb = img.getRGB( x, y );
				int red = ( rgb >> 16 ) & 0x000000FF;
				int green = ( rgb >> 8 ) & 0x000000FF;
				int blue = rgb & 0x000000FF;
				rgb = ( lut[ red ] << 16 ) | ( lut[ green ] << 8 ) | ( lut[ blue ] );
				img.setRGB( x, y, rgb );
			}
		}
	}

	/**
	 * \
	 * Applies full LUT (R G B)
	 *
	 * @param lut
	 * @param img
	 * @throws IllegalArgumentException
	 */
	public static void applyLUT( int[][] lut, BufferedImage img ) throws IllegalArgumentException
	{
		if ( lut == null || img == null )
			throw new IllegalArgumentException( "null value for int lut[] or BufferedImage img parameters" );
		if ( lut.length < 3 )
			throw new IllegalArgumentException( "int lut[] length must be >= 3" );
		ColorModel cm = img.getColorModel();

		int[] lutr = lut[ Histogram.R ];
		if ( lutr.length < 256 )
			throw new IllegalArgumentException( "int lut[r][] length must be >= 256" );

		int[] lutg = lut[ Histogram.G ];
		if ( lutg.length < 256 )
			throw new IllegalArgumentException( "int lut[g][] length must be >= 256" );

		int[] lutb = lut[ Histogram.B ];
		if ( lutb.length < 256 )
			throw new IllegalArgumentException( "int lut[b][] length must be >= 256" );

		for ( int y = 0; y < img.getHeight(); y++ )
		{
			for ( int x = 0; x < img.getWidth(); x++ )
			{
				int rgb = img.getRGB( x, y );
				int red = ( rgb >> 16 ) & 0x000000FF;
				int green = ( rgb >> 8 ) & 0x000000FF;
				int blue = rgb & 0x000000FF;
				rgb = ( lutr[ red ] << 16 ) | ( lutg[ green ] << 8 ) | ( lutb[ blue ] );
				img.setRGB( x, y, rgb );
			}
		}
	}


}
