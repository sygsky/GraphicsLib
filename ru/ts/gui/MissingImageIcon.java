/**
 *</pre>
 * Created on 04.08.2009 10:57:35<br> 
 * by Syg<br> 
 * for project in 'ru.ts.gui'
 *</pre>
 */
package ru.ts.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Class factory returns "missing" icon image depending on its relative size, 16 or 32 pixel
 * for side.
 */
public class MissingImageIcon extends ImageIcon
{

	private static ImageIcon mi16;
	private static ImageIcon mi32;
	private static ImageIcon mi48;
	private static ImageIcon mi64;

	/**
	 * Draws the image itself
	 * @param size square image size size
	 * @param img img to paint onto
	 */
	private static void paint2Image( int size, final BufferedImage img )
	{
		int coeff = 32 / size;
		Graphics2D g2d = (Graphics2D) img.getGraphics();
		BasicStroke stroke = new BasicStroke( 4 / coeff );

		g2d.setColor( Color.YELLOW );
		g2d.fillRect( 1, 1, size - 2, size - 2 );

		g2d.setColor( Color.BLACK );
		g2d.drawRect( 1, 1, size - 2, size - 2 );

		g2d.setColor( Color.RED );

		g2d.setStroke( stroke );
		int val = 10 / coeff;
		g2d.drawLine( val, val, size - val, size - val );
		g2d.drawLine( val, size - val, size - val, val );

		g2d.dispose();
	}

	/**
	 * Returns instance of {@link ImageIcon} with missing icon image
	 * @param size int value for icon side size, may be only 16 or 32, if other
	 * value designated, 16x16 icon is returned as a default one
	 * @return instance of {@link ImageIcon} of user designated size, containing
	 * "missing" image in form of a red oblique cross on the yellow background
	 */
	public static synchronized ImageIcon getMissingIconInstance( int size )
	{
		switch ( size )
		{
			default:
			case 16:
				if ( mi16 == null )
				{
					final BufferedImage img = new BufferedImage( size, size, BufferedImage.TYPE_INT_ARGB );
					paint2Image( size, img );
					mi16 = new ImageIcon( img );
				}
				return mi16;
			case 32:
				if ( mi32 == null )
				{
					final BufferedImage img = new BufferedImage( size, size, BufferedImage.TYPE_INT_ARGB );
					paint2Image( size, img );
					mi32 = new ImageIcon( img );
				}
				return mi32;
			case 48:
				if ( mi48 == null )
				{
					final BufferedImage img = new BufferedImage( size, size, BufferedImage.TYPE_INT_ARGB );
					paint2Image( size, img );
					mi48 = new ImageIcon( img );
				}
				return mi48;
			case 64:
				if ( mi64 == null )
				{
					final BufferedImage img = new BufferedImage( size, size, BufferedImage.TYPE_INT_ARGB );
					paint2Image( size, img );
					mi64 = new ImageIcon( img );
				}
				return mi64;
		}
	}

	/**
	 * Returns instance of {@link ImageIcon} with missing icon image of size 16 x 16 pixels
	 * @return instance of {@link ImageIcon} of size 16 x 16 pixels containing
	 * "missing" image in form of a red oblique cross on the yellow background 
	 */
	public static synchronized ImageIcon getMissingIconInstance()
	{
		return getMissingIconInstance( 16 );
	}

}
