package su;

import ru.ts.graphics.ImgUtil;
import ru.ts.common.misc.ICmdArgs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.*;

/**
 * <pre>
 * Created by IntelliJ IDEA.
 * User: sigolaev_va
 * Date: 22.04.2014
 * Time: 17:05:45
 * Original package: fores.raster
 * *
 * To change this template use File | Settings | File Templates.
 * *
 * <pre>
 */
public class ARGBConvertor
{
	/**
	 * Sets all pixels not this designated transparent color to be opaque. If input image doesn't contain alpha channle, no changes done to image
	 * @param ARGBImage {@link java.awt.image.BufferedImage} instance. Must have alpha channel. If image is {@code null} the {@code null} is returned
	 * if image in not ARGB, it is returned without any changes
	 * @return {@link java.awt.image.BufferedImage} instance with possibly some pixels changed to opaque or {@code null} if no image
	 */
	public static BufferedImage setColor2Opaque (BufferedImage ARGBImage, Color transparentColor )
	{
		if ( ARGBImage == null || (!isImageARGB(ARGBImage)) )
			return ARGBImage;
		int tColor = transparentColor.getRGB() & 0x00FFFFFF; // transparent color
		for(int i = 0; i < ARGBImage.getHeight(); i++ )
			for (int j = 0; j < ARGBImage.getWidth(); j++ )
			{
				int pix = ARGBImage.getRGB( j, i );
				if ( (pix & 0x00FFFFFF) != tColor ) // check if current pixel has transparent color
					ARGBImage.setRGB( j, i, pix | 0xFF000000 ); // and set it opaque if is it not eq to transparent color
			}
		return ARGBImage;
	}

	public static boolean isImageARGB (BufferedImage ARGBImage )
	{
		switch ( ARGBImage.getType() )
		{
			case BufferedImage.TYPE_4BYTE_ABGR:
			case BufferedImage.TYPE_4BYTE_ABGR_PRE:
			case BufferedImage.TYPE_INT_ARGB:
			case BufferedImage.TYPE_INT_ARGB_PRE:
				return true;
			default:
				return false;
		}
	}

	/**
	 * Change all pixels of designated raster NOT EQUAL to tColor to be OPAQUE (with alpha 1.0)
	 * @param iPath String with input raster path
	 * @param oPath String with output raster path
	 * @param tColor {@link Color} instance to remain only all of them transparent. Transparency of pixels with color eq to
	 * transparent one is no changed!
	 * @return
	 */
	public static boolean setToOpaque( String iPath, String oPath, Color tColor )
	{
		BufferedImage bi = ImgUtil.fileToImage( iPath );
		if ( bi == null )
		{
			System.out.println( "--- Failure to read image ---" );
			return false;
		}
		if ( !isImageARGB( bi ))
		{
			System.out.println( "--- Not ARGB image found ---" );
			return false;
		}
		// process ARGB image
		bi = setColor2Opaque( bi, tColor );
		return ImgUtil.imageToFile( bi, oPath );
	}

	public static void main( String[] args )
	{
		ICmdArgs cmd = new ICmdArgs.Impl( args );
		if ( !cmd.contains( 'i' ))
		{
			System.err.println( "--- Expected key -i <input path> not found" );
			return;
		}
		String iPath = cmd.value( 'i' );

		if ( !cmd.contains( 'о', 'o' ))
		{
			System.err.println( "--- Expected key -o <output path> not found" );
			return;
		}
		String oPath = cmd.value( 'о', 'o' );

		if ( !cmd.contains( ICmdArgs.Keys.C_KEYS) )
		{
			System.err.println( "--- Expected key -с <color> not found" );
			return;
		}
		final String colStr = cmd.value( 'c', 'с' );
		Color col = ru.ts.colors.ColorUtils.parseColor( colStr, null );
		if ( col == null )
		{
			System.err.println( "--- expected -c <color> is illegal: " + colStr );
			return;
		}

		if ( setToOpaque( iPath, oPath, col ))
		{
			System.out.println( "+++ Raster is changed and stored +++" );
		}
		else
		{
			System.err.println( "--- Failure to process or store raster ---" );
		}

	}
}
