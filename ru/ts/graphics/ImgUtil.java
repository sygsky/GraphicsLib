package ru.ts.graphics;

import ru.ts.common.misc.*;
import static ru.ts.common.misc.Text.sout;
import static ru.ts.common.misc.Text.serr;
import ru.ts.colors.SpectralLibrary;
import sun.awt.image.ToolkitImage;

import javax.imageio.*;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Iterator;

import cern.colt.bitvector.BitMatrix;
import su.misc.intf.IProgressObserver;

/**
 * Created by IntelliJ IDEA. User: YUGL Date: 13.01.2009 Time: 13:32:00 Some
 * utils for images processing
 */
public class ImgUtil
{

	/**
	 * Saves image to file of given format
	 *
	 * @param img      image to be saved
	 * @param format   file format
	 * @param filename file name
	 */
	static public boolean imageToFile( BufferedImage img, String format,
	                                   String filename )
	{
		try
		{
			return ImageIO.write( img, format, new File( filename ) );
		}
		catch ( IOException e )
		{
			e.printStackTrace();
			return false;
		}
	}

	static public void imageToFile( Image img, String format, String filename )
	{
		if ( img instanceof ToolkitImage )
		{
			imageToFile( ( (ToolkitImage) img ).getBufferedImage(), format, filename );
		}
		else
		{
			Text.serr( "Don't know how to save image of \"{0}\" type",
				new Object[] { img.getClass().getCanonicalName() } );
		}
	}

	static public boolean imageToFile( Image img, String filename )
	{
		return imageToFile( ( (ToolkitImage) img ).getBufferedImage(), filename );
	}


	/**
	 * Saves image to file of default (PNG) format
	 *
	 * @param img      image to be saved
	 * @param filename file name
	 * @return
	 */
	static public boolean imageToFile( BufferedImage img, String filename )
	{
		//    imageToFile(img, "GIF", filename);
		// get ext
		String ext = Files.getExtension( filename );
		if ( Text.isEmpty( ext ) )
		{
			return imageToFile( img, "PNG", filename );
		}
		else
		{
			return imageToFile( img, ext.substring( 1 ), filename );
		}
	}

	/**
	 * Loads image from file. Returns null, if error.
	 *
	 * @param filename file name
	 */
	static public BufferedImage fileToImage( String filename )
	{
		try
		{
			FileInputStream fis = new FileInputStream( filename );
			BufferedImage img = ImageIO.read( fis );
			fis.close();
			return img;
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Loads image from url. Returns null, if error.
	 *
	 * @param url file name
	 */
	static public BufferedImage urlToImage( URL url )
	{
		try
		{
			BufferedImage img = ImageIO.read( url );
			return img;
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * Checks if a given image size is not less than given width and height.
	 * Creates a new image when size is not large enough.
	 *
	 * @param img a given image
	 * @param w   necessary width
	 * @param h   necessary height
	 * @return old or new image
	 */
	static public BufferedImage EnsureImageSize( BufferedImage img, int w, int h,
	                                             int type )
	{
		if ( img != null )
		{
			if ( ( img.getHeight() < h ) || ( img.getWidth() < w ) )
			{
				img = null;
			}
		}
		if ( img == null )
		{
			img = new BufferedImage( w, h, type );
		}
		return img;
	}


	/**
	 * Fills an image by transparent color.
	 *
	 * @param img image instance
	 */
	static public void clearTransparentImage( BufferedImage img )
	{
		fillImage( img, 0 );
		//    Graphics2D gr = img.createGraphics();
		//    gr.setColor(_selBack);
		//    gr.fillRect(0, 0, img.getWidth(), img.getHeight());
	}

	/**
	 * Fills an image by given color. Works a bit slowly.
	 *
	 * @param img  image instance
	 * @param argb int ARGB color representation
	 */
	static public void fillImagePixels( BufferedImage img, int argb )
	{
		if ( img == null )
			return;
		final int w = img.getWidth();
		final int h = img.getHeight();
		for ( int y = 0; y < h; y++ )
			for ( int x = 0; x < w; x++ )
				img.setRGB( x, y, argb );
	}

	/**
	 * Fills an image by given color.
	 *
	 * @param img   image instance
	 * @param color color
	 */
	static public void fillImage( BufferedImage img, Color color )
	{
		if ( img == null )
		{
			return;
		}
		int argb = color.getRGB();
		fillImage( img, argb );
	}

	/**
	 * Fills an image by given color, trying to do it quickly.
	 *
	 * @param img  image instance
	 * @param argb int ARGB color representation
	 */
	static public void fillImage( BufferedImage img, int argb )
	{
		if ( img == null )
			return;
		if ( fillRaster(img, argb) ) return;
		fillImagePixels( img, argb );
	}

	/**
	 * Clears image with setting user designated transparency. This method could
	 * work faster than  {@link ImgUtil#clearTransparentImage} for the case of
	 * image with 1 band and data type {@link java.awt.image.DataBuffer#TYPE_INT}
	 *
	 * @param img          {@link java.awt.image.BufferedImage} instance to clear
	 * @param transparency int with the transparency value in range 0..255. Only
	 *                     first 8 bits will be used as the transparency value.
	 */
	public static void clearImage( BufferedImage img, int transparency )
	{
		if ( img == null )
		{
			return;
		}
		DataBuffer db = img.getRaster().getDataBuffer();
		int numb = db.getNumBanks();
		if ( db.getDataType() == DataBuffer.TYPE_INT && ( numb == 1 ) )
		{

			int pix = ( transparency & 0xFF ) << 24;
			DataBufferInt dbi = (DataBufferInt) db;
			Arrays.fill( dbi.getData(), pix );
		}
		else
		{
			clearTransparentImage( img );
		}
	}

	/**
	 * Clears image with setting user designated transparency. This method could
	 * work faster than  {@link ImgUtil#clearTransparentImage} for the case of
	 * image with 1 band and data type {@link java.awt.image.DataBuffer#TYPE_INT}
	 *
	 * @param img          {@link java.awt.image.BufferedImage} instance to clear
	 * @param transparency float with the transparency value in range 0..1. If
	 *                     value out of range designated, 0 is used as default one
	 */
	public static void clearImage( BufferedImage img, float transparency )
	{
		int transp;
		if ( transparency < 0.0 )
		{
			transp = 0;
		}
		else
		{
			if ( transparency > 1.0 )
			{
				transp = 255;
			}
			else
			{
				transp = Math.round( 255.0F * transparency );
			}
		}
		clearImage( img, transp );
	}

	/**
	 * Prepares image that looks more red to be notable. The opacity of notable
	 * pixels is 255, (totally opaque/nontransparent) (Usually the source image
	 * contains selected objects.) The operations may have more parameters to be
	 * more flexible.
	 *
	 * @param src source image of type {@link java.awt.image.BufferedImage#TYPE_INT_ARGB}
	 * @param dst result image or null
	 * @return reference to result image
	 */
	static public BufferedImage makeSelectionBeNotable( BufferedImage src,
	                                                    BufferedImage dst )
	{
		return makeSelectionBeNotable( src, dst, 255 );
	}

	/**
	 * Prepares image that looks more red to be notable. (Usually the source image
	 * contains selected objects.) The operations may have more parameters to be
	 * more flexible.
	 *
	 * @param src    source image of type {@link java.awt.image.BufferedImage#TYPE_INT_ARGB}
	 * @param dst    result image or null
	 * @param transp int value with transparency to set for notable pixels, if 255
	 *               - totally not transparent, if 0 - totally transparent, 128 -
	 *               semitransparent etc
	 * @return reference to result image
	 */
	static public BufferedImage makeSelectionBeNotable( BufferedImage src,
	                                                    BufferedImage dst,
	                                                    int transp )
	{
		// prepare image for notable selection
		if ( src == null )
		{
			return dst;
		}
		int w = src.getWidth();
		int h = src.getHeight();
		if ( ( dst == null ) || ( dst.getWidth() < w ) || ( dst.getHeight() < h ) )
		{
			dst = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
		}

		// clear
		if ( !src.equals( dst ) )
		{
			ImgUtil.clearImage( dst, 0 );
		}
		/* Set transparency mask */
		transp = ( transp & 0x000000FF ) << 24;
		if ( src.equals( dst ) )
		{
			DataBuffer db = src.getRaster().getDataBuffer();
			int numb = db.getNumBanks();
			if ( db.getDataType() == DataBuffer.TYPE_INT && ( numb == 1 ) )
			{
				DataBufferInt dbi = (DataBufferInt) db;
				int[] pixs = dbi.getData();
				//int RMASK = 0x00FF0000;
				//        int radd = (128 << 16) & 0x00FF0000;
				int radd = ( 160 << 16 ) & 0x00FF0000;
				for ( int i = 0; i < pixs.length; i++ )
				{
					int rgb = pixs[ i ];
					if ( ( rgb & 0xFF000000 ) != 0xFF000000 )
					{
						continue;// skip transparent pixel
					}
					/* keep a lot of red, namely R = 128 + R/2 */
					//          int r = ( radd + (( rgb & 0x00FF0000) >> 1 )) & 0x00FF0000;
					int r = ( radd | ( ( rgb & 0x00FF0000 ) >> 1 ) ) & 0x00FF0000;
					/* G /= 4, B /= 4 (quoter of green and blue) */
					int gb = ( rgb >> 1 ) & 0x00003F3F;
					rgb = transp | r | gb;
					pixs[ i ] = rgb;
				}
				return dst;
			}
		}

		//    Graphics2D gr = dst.createGraphics();
		//    gr.drawImage(_imgSel, 0, 0, null);
		for ( int x = 0; x < w; x++ )
		{
			for ( int y = 0; y < h; y++ )
			{
				int rgb = src.getRGB( x, y );
				if ( ( rgb & 0xFF000000 ) != 0xFF000000 )
				{
					continue;// skip transparent pixel
				}
				int r = ( rgb & 0x00FF0000 ) >> 16;
				int g = ( rgb & 0x0000FF00 ) >> 8;
				int b = ( rgb & 0x000000FF );

				r = 128 + r / 2;// keep a lot of red
				g = g / 4;// remove most of green
				b = b / 4;// remove most of blue
				//        g = g/3;  // remove most of green
				//        b = b/3;  // remove most of blue
				//        g = g/2;  // remove most of green
				//        b = b/2;  // remove most of blue

				rgb =
					transp | ( ( r & 0xFF ) << 16 ) | ( ( g & 0xFF ) << 8 ) | ( ( b & 0xFF ) << 0 );
				dst.setRGB( x, y, rgb );
			}
		}
		return dst;
	}

	/**
	 * Processes a source image and put a result in a destination image. Every zero
	 * point remains a zero one. Colors of other points are transformed. The
	 * transparency becomes equal to a given parameter {@code transp}. Other color
	 * components are calculated using the following formula: x' = ax + x*mx/dx. if
	 * x'<0 then x'=0. if x'>255 then x'=255.
	 *
	 * @param src    source image of type {@link java.awt.image.BufferedImage#TYPE_INT_ARGB}
	 * @param dst    result image of type {@link java.awt.image.BufferedImage#TYPE_INT_ARGB}
	 *               or null
	 * @param transp int value for tranparency [0 - 255]
	 * @param ar     ax for red
	 * @param mr     mx for red
	 * @param dr     dx for red
	 * @param ag     ax for green
	 * @param mg     mx for green
	 * @param dg     dx for green
	 * @param ab     ax for blue
	 * @param mb     mx for blue
	 * @param db     dx for blue
	 * @return reference to result image
	 */
	static public BufferedImage applyColorLinearTransform(
		BufferedImage src, BufferedImage dst, int transp,
		int ar, int mr, int dr, int ag, int mg, int dg, int ab, int mb, int db )
	{
		// prepare image
		if ( src == null )
		{
			return dst;
		}
		int w = src.getWidth();
		int h = src.getHeight();
		if ( ( dst == null ) || ( dst.getWidth() < w ) || ( dst.getHeight() < h ) )
		{
			dst = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
		}

		// clear
		if ( !src.equals( dst ) )
		{
			ImgUtil.fillImage( dst, 0 );
		}
		/* Set transparency mask */
		transp = ( transp & 0x000000FF ) << 24;

		if ( w == dst.getWidth() )
		{
			// check images format to work faster
			DataBuffer db1 = src.getRaster().getDataBuffer();
			int numb1 = db1.getNumBanks();
			DataBuffer db2 = src.getRaster().getDataBuffer();
			int numb2 = db2.getNumBanks();
			if ( db1.getDataType() == DataBuffer.TYPE_INT && numb1 == 1 &&
				db2.getDataType() == DataBuffer.TYPE_INT && numb2 == 1 )
			{
				int[] pixs1 = ( (DataBufferInt) db1 ).getData();
				int[] pixs2 = ( (DataBufferInt) db2 ).getData();
				//        int radd = (128 << 16) & 0x00FF0000;
				int radd = ( 160 << 16 ) & 0x00FF0000;
				for ( int i = 0; i < pixs1.length; i++ )
				{
					int rgb = pixs1[ i ];
					if ( ( rgb & 0xFF000000 ) != 0xFF000000 )
					{
						continue;// skip transparent pixel
					}
					rgb =
						transp | applyColorLinearTransform( rgb, ar, mr, dr, ag, mg, dg, ab,
							mb, db );
					pixs2[ i ] = rgb;
				}
				return dst;
			}
		}
		for ( int x = 0; x < w; x++ )
		{
			for ( int y = 0; y < h; y++ )
			{
				int rgb = src.getRGB( x, y );
				if ( ( rgb & 0xFF000000 ) != 0xFF000000 )
				{
					continue;// skip transparent pixel
				}
				rgb =
					transp | applyColorLinearTransform( rgb, ar, mr, dr, ag, mg, dg, ab,
						mb, db );
				dst.setRGB( x, y, rgb );
			}
		}
		return dst;
	}

	/**
	 * Processes a color component using the following formula: x' = ax + x*mx/dx.
	 * if x'<0 then x'=0. if x'>255 then x'=255.
	 *
	 * @param x  x
	 * @param ax ax
	 * @param mx mx
	 * @param dx dx
	 * @return x'
	 */
	static public int applyColorLinearTransform( int x, int ax, int mx, int dx )
	{
		if ( dx == 0 )
		{
			return 255;
		}
		x = ax + x * mx / dx;
		if ( x < 0 )
		{
			return 0;
		}
		if ( x > 255 )
		{
			return 255;
		}
		return x;
	}

	/**
	 * Processes a color using the following formula for every component: x' = ax +
	 * x*mx/dx. if x'<0 then x'=0. if x'>255 then x'=255.
	 *
	 * @param rgb source color rgb
	 * @param ar  ax for red
	 * @param mr  mx for red
	 * @param dr  dx for red
	 * @param ag  ax for green
	 * @param mg  mx for green
	 * @param dg  dx for green
	 * @param ab  ax for blue
	 * @param mb  mx for blue
	 * @param db  dx for blue
	 * @return result color rgb
	 */
	static public int applyColorLinearTransform(
		int rgb, int ar, int mr, int dr, int ag, int mg, int dg, int ab, int mb,
		int db )
	{
		int r = ( rgb & 0x00FF0000 ) >> 16;
		int g = ( rgb & 0x0000FF00 ) >> 8;
		int b = ( rgb & 0x000000FF );
		r = applyColorLinearTransform( r, ar, mr, dr );
		g = applyColorLinearTransform( g, ag, mg, dg );
		b = applyColorLinearTransform( b, ab, mb, db );
		rgb =
			( ( r & 0xFF ) << 16 ) | ( ( g & 0xFF ) << 8 ) | ( ( b & 0xFF ) << 0 );
		return rgb;
	}

	static public BufferedImage resizeImage( BufferedImage img, int newWidth,
	                                         int newHeight )
	{
		if ( ( img.getWidth() == newWidth ) && ( img.getHeight() == newHeight ) )
		{
			BufferedImage newImg =
				new BufferedImage( newWidth, newHeight, img.getType()
					/*BufferedImage.TYPE_INT_ARGB*/ );
			copyRaster( img, newImg );
			return newImg;
		}
		BufferedImage newImg = new BufferedImage( newWidth, newHeight, img.getType()
			/*BufferedImage.TYPE_INT_ARGB*/ );
		Graphics2D graphics = newImg.createGraphics();
		try
		{
			graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON );
			graphics.setRenderingHint( RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY );
			graphics.setRenderingHint( RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC );
			graphics.drawImage( img, 0, 0, newWidth, newHeight, null );
		}
		finally
		{
			graphics.dispose();
		}
		return newImg;
	}

	/**
	 * Creates a new raster that has a <b>copy</b> of the data in <tt>ras</tt>.
	 * This is highly optimized for speed.  There is no provision for changing any
	 * aspect of the SampleModel. However you can specify a new location for the
	 * returned raster.
	 * <p/>
	 * This method should be used when you need to change the contents of a Raster
	 * that you do not "own" (ie the result of a <tt>getData</tt> call).
	 *
	 * @param ras  The Raster to copy.
	 * @param minX The x location for the upper left corner of the returned
	 *             WritableRaster.
	 * @param minY The y location for the upper left corner of the returned
	 *             WritableRaster.
	 * @return A writable copy of <tt>ras</tt>
	 */
	public static WritableRaster copyRaster( Raster ras, int minX, int minY )
	{
		WritableRaster ret = Raster.createWritableRaster
			( ras.getSampleModel(),
				new Point( 0, 0 ) );
		ret = ret.createWritableChild
			( ras.getMinX() - ras.getSampleModelTranslateX(),
				ras.getMinY() - ras.getSampleModelTranslateY(),
				ras.getWidth(), ras.getHeight(),
				minX, minY, null );
		copyRaster( ras, ret );
		return ret;
	}

	public static void copyRaster( final Raster src, final Raster dst )
	{
		// Use System.arraycopy to copy the data between the two...
		DataBuffer srcDB = src.getDataBuffer();
		DataBuffer retDB = dst.getDataBuffer();
		if ( srcDB.getDataType() != retDB.getDataType() )
		{
			throw new IllegalArgumentException
				( "New DataBuffer doesn't match original" );
		}
		int len = srcDB.getSize();
		int banks = srcDB.getNumBanks();
		int[] offsets = srcDB.getOffsets();
		for ( int b = 0; b < banks; b++ )
		{
			switch ( srcDB.getDataType() )
			{
				case DataBuffer.TYPE_BYTE:
				{
					DataBufferByte srcDBT = (DataBufferByte) srcDB;
					DataBufferByte retDBT = (DataBufferByte) retDB;
					System.arraycopy( srcDBT.getData( b ), offsets[ b ],
						retDBT.getData( b ), offsets[ b ], len );
					break;
				}
				case DataBuffer.TYPE_SHORT:
				{
					DataBufferShort srcDBT = (DataBufferShort) srcDB;
					DataBufferShort retDBT = (DataBufferShort) retDB;
					System.arraycopy( srcDBT.getData( b ), offsets[ b ],
						retDBT.getData( b ), offsets[ b ], len );
					break;
				}
				case DataBuffer.TYPE_USHORT:
				{
					DataBufferUShort srcDBT = (DataBufferUShort) srcDB;
					DataBufferUShort retDBT = (DataBufferUShort) retDB;
					System.arraycopy( srcDBT.getData( b ), offsets[ b ],
						retDBT.getData( b ), offsets[ b ], len );
					break;
				}
			}
		}
	}

	/**
	 * Creates the copy of the used {@link java.awt.image.DataBuffer}
	 *
	 * @param src {@link java.awt.image.DataBuffer} instance to create the copy
	 * @return new {@link java.awt.image.DataBuffer} instance with data copied from
	 *         'src'
	 */
	public static DataBuffer copyDataBuffer( final DataBuffer src )
	{
		// Use System.arraycopy to copy the data between the two...
		DataBuffer srcDB = src;
		DataBuffer retDB = copyDataBufferStruct( srcDB );

		int len = srcDB.getSize();
		int banks = srcDB.getNumBanks();
		int[] offsets = srcDB.getOffsets();
		for ( int b = 0; b < banks; b++ )
		{
			switch ( srcDB.getDataType() )
			{
				case DataBuffer.TYPE_BYTE:
				{
					DataBufferByte srcDBT = (DataBufferByte) srcDB;
					DataBufferByte retDBT = (DataBufferByte) retDB;
					System.arraycopy( srcDBT.getData( b ), offsets[ b ],
						retDBT.getData( b ), offsets[ b ], len );
					break;
				}
				case DataBuffer.TYPE_INT:
				{
					DataBufferInt srcDBT = (DataBufferInt) srcDB;
					DataBufferInt retDBT = (DataBufferInt) retDB;
					System.arraycopy( srcDBT.getData( b ), offsets[ b ],
						retDBT.getData( b ), offsets[ b ], len );
					break;
				}
				case DataBuffer.TYPE_SHORT:
				{
					DataBufferShort srcDBT = (DataBufferShort) srcDB;
					DataBufferShort retDBT = (DataBufferShort) retDB;
					System.arraycopy( srcDBT.getData( b ), offsets[ b ],
						retDBT.getData( b ), offsets[ b ], len );
					break;
				}
				case DataBuffer.TYPE_USHORT:
				{
					DataBufferUShort srcDBT = (DataBufferUShort) srcDB;
					DataBufferUShort retDBT = (DataBufferUShort) retDB;
					System.arraycopy( srcDBT.getData( b ), offsets[ b ],
						retDBT.getData( b ), offsets[ b ], len );
					break;
				}
			}
		}
		return retDB;
	}

	/**
	 * Creates used {@link java.awt.image.DataBuffer} structural copy, that is no
	 * data (pixels) are copied, only memeory is reserved
	 *
	 * @param db {@link java.awt.image.DataBuffer} instance to create struct copy
	 * @return new {@link java.awt.image.DataBuffer} instance with memeory
	 *         reserved, but data not copied
	 */
	private static DataBuffer copyDataBufferStruct( final DataBuffer db )
	{
		DataBuffer srcDB = db;
		DataBuffer retDB;
		switch ( srcDB.getDataType() )
		{
			case DataBuffer.TYPE_BYTE:
				DataBufferByte srcDBB = (DataBufferByte) srcDB;
				retDB =
					new DataBufferByte( srcDBB.getData().length, srcDBB.getNumBanks() );
				break;
			case DataBuffer.TYPE_INT:
				DataBufferInt srcDBI = (DataBufferInt) srcDB;
				retDB =
					new DataBufferInt( srcDBI.getData().length, srcDBI.getNumBanks() );
				break;
			case DataBuffer.TYPE_SHORT:
				DataBufferShort srcDBS = (DataBufferShort) srcDB;
				retDB =
					new DataBufferShort( srcDBS.getData().length, srcDBS.getNumBanks() );
				break;
			case DataBuffer.TYPE_USHORT:
				DataBufferUShort srcDBU = (DataBufferUShort) srcDB;
				retDB =
					new DataBufferUShort( srcDBU.getData().length, srcDBU.getNumBanks() );
				break;
			case DataBuffer.TYPE_FLOAT:
				DataBufferFloat srcDBF = (DataBufferFloat) srcDB;
				retDB =
					new DataBufferFloat( srcDBF.getData().length, srcDBF.getNumBanks() );
				break;
			case DataBuffer.TYPE_DOUBLE:
				DataBufferDouble srcDBD = (DataBufferDouble) srcDB;
				retDB =
					new DataBufferDouble( srcDBD.getData().length, srcDBD.getNumBanks() );
				break;
			default:
				throw new IllegalArgumentException(
					"Expected DataBuffer type UNDEFINED" );

		}
		return retDB;
	}


	/**
	 * Tries to copy content of one image to other. They should be comparable in
	 * the sence of method {@link ImgUtil#areComparable(java.awt.image.BufferedImage,
	 * java.awt.image.BufferedImage)}
	 *
	 * @param src {@link java.awt.image.BufferedImage} instance to copy to
	 * @param dst {@link java.awt.image.BufferedImage}  instance to copy from
	 */
	public static void copyRaster( final BufferedImage src,
	                               final BufferedImage dst )
	{
		copyRaster( src.getRaster(), dst.getRaster() );
	}

	/**
	 * Prepares deep copy of source raster
	 *
	 * @param image {@link java.awt.image.BufferedImage} instance to copy
	 * @return {@link java.awt.image.BufferedImage} new instance with deep copy of
	 *         source one
	 */
	public static BufferedImage cloneRasterData( final BufferedImage image )
	{

		int type = image.getType();
		BufferedImage ret;
		if ( type == BufferedImage.TYPE_CUSTOM )
		{
			SampleModel sm = image.getSampleModel();
			Point loc = new Point( 0, 0 );
			DataBuffer db = image.getRaster().getDataBuffer();
			DataBuffer ndb = ImgUtil.copyDataBuffer( db );// create databuffer copy
			WritableRaster rs = Raster.createWritableRaster( sm, ndb, loc );
			ColorModel cm = image.getColorModel();
			return new BufferedImage( cm, rs, image.isAlphaPremultiplied(),
				new Hashtable( 0 ) );
		}
		ret = new BufferedImage( image.getWidth(), image.getHeight(), type );
		copyRaster( image, ret );
		return ret;
	}

	/**
	 * Prepares structural copy of the source raster that is the image type, size
	 * color model etc
	 *
	 * @param image {@link java.awt.image.BufferedImage} instance to copy
	 * @return {@link java.awt.image.BufferedImage} new instance with structural of
	 *         source one
	 */
	public static BufferedImage cloneRasterStruct( final BufferedImage image )
	{

		int type = image.getType();
		BufferedImage ret;
		if ( type == BufferedImage.TYPE_CUSTOM )
		{
			SampleModel sm = image.getSampleModel();
			Point loc = new Point( 0, 0 );
			DataBuffer db = image.getRaster().getDataBuffer();
			DataBuffer ndb = ImgUtil
				.copyDataBufferStruct( db );// create databuffer only structure copy
			WritableRaster rs = Raster.createWritableRaster( sm, ndb, loc );
			ColorModel cm = image.getColorModel();
			return new BufferedImage( cm, rs, image.isAlphaPremultiplied(),
				new Hashtable( 0 ) );
		}
		return new BufferedImage( image.getWidth(), image.getHeight(), type );
	}

	/**
	 * Fills raster with user predefined value. Note that if raster pixel width is
	 * narrower that int 4 bytes, only less significant bytes of the set value is
	 * used
	 *
	 * @param src {@link java.awt.image.Raster} instance to fill with 'val'
	 * @param val int value to fill with
	 * @return true on success else false
	 */
	public static boolean fillRaster( final Raster src, int val )
	{
		// Use Arrays.fill to set the data between with ...
		DataBuffer srcDB = src.getDataBuffer();
		int dataType = srcDB.getDataType();
		if (dataType == DataBuffer.TYPE_FLOAT || dataType == DataBuffer.TYPE_FLOAT || dataType == DataBuffer.TYPE_UNDEFINED )
			return false;
		int banks = srcDB.getNumBanks();
		for ( int b = 0; b < banks; b++ )
		{
			switch ( srcDB.getDataType() )
			{
				case DataBuffer.TYPE_BYTE:
				{
					DataBufferByte srcDBT = (DataBufferByte) srcDB;
					Arrays.fill( srcDBT.getData( b ), (byte) val );
					break;
				}
				case DataBuffer.TYPE_INT:
				{
					DataBufferInt srcDBT = (DataBufferInt) srcDB;
					Arrays.fill( srcDBT.getData( b ), val );
					break;
				}
				case DataBuffer.TYPE_SHORT:
				{
					DataBufferShort srcDBT = (DataBufferShort) srcDB;
					Arrays.fill( srcDBT.getData( b ), (short) val );
					break;
				}
				case DataBuffer.TYPE_USHORT:
				{
					DataBufferUShort srcDBT = (DataBufferUShort) srcDB;
					Arrays.fill( srcDBT.getData( b ),  (short) val );
					break;
				}
			}
		}
		return true;
	}

	/**
	 * Gets raster memory buffers size in bytes. Understands any known image type.
	 * If image memory storage has undefined type, negative number of image pixels
	 * is returned as a result
	 *
	 * @param src {@link java.awt.image.Raster} instance to get size
	 * @return int value with whole internal buffer memory size in bytes. If returned value
	 *         if negative it mean that type is unknown and result absolute value is size of
	 *         image in pixels
	 */
	public static int getMemorySize( final Raster src )
	{
		// Use Arrays.fill to set the data between with ...
		DataBuffer srcDB = src.getDataBuffer();
		int bankLen = srcDB.getSize();
		int banksNum = srcDB.getNumBanks();
		int arrayElementBytes = 0;
		switch ( srcDB.getDataType() )
		{
			case DataBuffer.TYPE_BYTE:
			{
				arrayElementBytes = 1;
				break;
			}
			case DataBuffer.TYPE_SHORT:
			case DataBuffer.TYPE_USHORT:
			{
				arrayElementBytes = 2;
				break;
			}
			case DataBuffer.TYPE_FLOAT:
			case DataBuffer.TYPE_INT:
			{
				arrayElementBytes = 4;
				break;
			}
			case DataBuffer.TYPE_DOUBLE:
			{
				arrayElementBytes = 8;
				break;
			}
			case DataBuffer.TYPE_UNDEFINED:
			default:
				arrayElementBytes = -1;
		}
		return banksNum * bankLen * arrayElementBytes;
	}

	/**
	 * Make image transparent pixel by pixel
	 *
	 * @param im           {@link java.awt.Image} to set transparency
	 * @param transparency in range 0..255
	 * @return new {@link java.awt.Image} created
	 */
	public static Image translucifyImage( Image im, int transparency )
	{
		final int tr = ( transparency & 0x000000FF ) << 24;

		ImageFilter filter = new RGBImageFilter()
		{
			public final int filterRGB( int x, int y, int rgb )
			{
				return ( rgb & 0x00FFFFFF ) | tr;
			}
		};
		ImageProducer ip = new FilteredImageSource( im.getSource(), filter );
		return Toolkit.getDefaultToolkit().createImage( ip );
	}

	/**
	 * Check is images can be compared by its contents or can by copied in fast
	 * mode ( see {@link ImgUtil#copyRaster(java.awt.image.Raster,
	 * java.awt.image.Raster)} } or {@link ImgUtil#cloneRasterData(java.awt.image.BufferedImage)}
	 *
	 * @param a {@link java.awt.image.BufferedImage} instance to compare with 'b'
	 * @param b {@link java.awt.image.BufferedImage} instance to compare with 'a'
	 * @return {@code true} if both images have follow equal requisites:<br> width,
	 *         height, color mode and sample mode. Else {@code false}
	 */
	public static boolean areComparable( BufferedImage a, BufferedImage b )
	{
		if ( ( a.getWidth() != b.getWidth() ) || ( a.getHeight() != b
			.getHeight() ) )
		{
			return false;
		}
		if ( a.getType() == b.getType() )
		{
			return true;
		}
		return ( a.getColorModel().equals( b.getColorModel() ) )
			&& ( a.getSampleModel().equals( b.getSampleModel() ) );
	}


	/**
	 * Fills image with user predefined value. Note that if raster pixel width is
	 * narrower that int 4 bytes, only less significant bytes of the set value is
	 * used
	 *
	 * @param src {@link java.awt.image.BufferedImage} instance to fill with 'val'
	 * @param val int value to fill with
	 * @return true on success else false
	 */
	public static boolean fillRaster( final BufferedImage src, int val )
	{
		return fillRaster( src.getRaster(), val );
	}

	/**
	 * Gets String representation for image type
	 *
	 * @param img {@link BufferedImage} iinstance to process
	 * @return String with name for image type
	 */
	public static String imageType2String( BufferedImage img )
	{
		int imgType = img.getType();
		switch ( imgType )
		{
			case BufferedImage.TYPE_3BYTE_BGR:
				return "TYPE_3BYTE_BGR";
			case BufferedImage.TYPE_4BYTE_ABGR:
				return "TYPE_4BYTE_ABGR";
			case BufferedImage.TYPE_4BYTE_ABGR_PRE:
				return "TYPE_4BYTE_ABGR_PRE";
			case BufferedImage.TYPE_BYTE_BINARY:
				return "TYPE_BYTE_BINARY";
			case BufferedImage.TYPE_BYTE_GRAY:
				return "TYPE_BYTE_GRAY";
			case BufferedImage.TYPE_BYTE_INDEXED:
				return "TYPE_BYTE_INDEXED";
			case BufferedImage.TYPE_CUSTOM:
				return "TYPE_CUSTOM";
			case BufferedImage.TYPE_INT_ARGB:
				return "TYPE_INT_ARGB";
			case BufferedImage.TYPE_INT_ARGB_PRE:
				return "TYPE_INT_ARGB_PRE";
			case BufferedImage.TYPE_INT_BGR:
				return "TYPE_INT_BGR";
			case BufferedImage.TYPE_INT_RGB:
				return "TYPE_INT_RGB";
			case BufferedImage.TYPE_USHORT_555_RGB:
				return "TYPE_USHORT_555_RGB";
			case BufferedImage.TYPE_USHORT_565_RGB:
				return "TYPE_USHORT_565_RGB";
			case BufferedImage.TYPE_USHORT_GRAY:
				return "TYPE_USHORT_GRAY";
			default:
				return "Type UNKNOWN";
		}
	}

	/**
	 * Work method.
	 * Reads the jpeg image in rendImage, compresses the image, and writes it back out to outfile.
	 * JPEGQuality ranges between 0.0F and 1.0F, 0-lowest, 1-highest. ios is closed internally
	 *
	 * @param rendImage   [@link RenderedImage} instance with an Rendered Image
	 * @param ios         {@link ImageOutputStream} instance,
	 *                    note that it is disposed in this method
	 * @param JPEGQuality float value for the JPEG compression quality (0.1(min) .. 1.0(max))
	 * @return {@code true} if image was successfully compressed
	 *         else {@code false} on any error, e.g. bad (null) parameters
	 */
	public static boolean compressJpegFile( RenderedImage rendImage, ImageOutputStream ios, final float JPEGQuality )
	{
		if ( rendImage == null )
			return false;
		if ( ios == null )
			return false;
		float JPEGQuality1 = Math.min(Math.max(JPEGQuality, 0.1f), 1.0f);
		if (  JPEGQuality != JPEGQuality1 )
			System.out.printf("jpeg writer quality has illegal value %f, limited to %f%n", JPEGQuality, JPEGQuality1);

		ImageWriter writer = null;
		try
		{

			// Find a jpeg writer
			Iterator iter = ImageIO.getImageWritersByFormatName( "jpg" );
			if ( iter.hasNext() )
				writer = (ImageWriter) iter.next();

			if ( writer == null )
				throw new IllegalArgumentException( "jpeg writer not found by call to ImageIO.getImageWritersByFormatName( \"jpg\" )" );
			writer.setOutput( ios );

			// Set the compression quality
			ImageWriteParam iwparam = new MyImageWriteParam();
			iwparam.setCompressionMode( ImageWriteParam.MODE_EXPLICIT );
			iwparam.setCompressionQuality( JPEGQuality1 );
			//      float res = iwparam.getCompressionQuality();

			// Write the image
			writer.write( null, new IIOImage( rendImage, null, null ), iwparam );

			return true;
		}
		catch ( Exception e )
		{
			System.err.printf("--- imgUtil: jpeg writer error - \"%s\"%n", e.getMessage());
			return false;
		}
		finally
		{
			if ( writer != null )
				writer.dispose();
			// Cleanup
			try
			{
				ios.flush();
				ios.close();
			}
			catch ( IOException e )
			{
			}
		}
	}

	/**
	 * Selects designated color and returns 1-bit two color {@link java.awt.image.BufferedImage} instance,
	 * zero bit value means non-data value and colored always white, one valued bit means data value
	 * and is colored with user designated color (newDataColor)
	 *
	 * @param bi           {@link java.awt.image.BufferedImage} instance to select data color from it
	 * @param dataColor    {@link java.awt.Color} instance to find in image
	 * @param dist         color range to select from designated dataColor. If dist == 0.0, only namely designated dataColor
	 *                     will be selected. <br>See {@link ru.ts.colors.SpectralLibrary#colorDistance(java.awt.Color, java.awt.Color)} description
	 *                     to understand about color space distance
	 * @param newDataColor Color to set for pixel if is is selected by color. Color of empty bits is always {@list Color#WHITE}
	 * @return new {@link java.awt.image.BufferedImage} 1-bit 2 color instance image with value 0 for not selected and 1
	 *         for selected colors only.<br>You may be sure that {@link SampleModel} for call to
	 *         {@link java.awt.image.BufferedImage#getSampleModel()} always is {@link java.awt.image.MultiPixelPackedSampleModel}<br>
	 *         {@link DataBuffer} is of type {@link DataBuffer#TYPE_BYTE}
	 */
	public static BufferedImage makeBinaryImage( BufferedImage bi, Color dataColor, double dist, Color newDataColor )
	{
		int w = bi.getWidth();
		int h = bi.getHeight();
		MultiPixelPackedSampleModel sm = new MultiPixelPackedSampleModel( DataBuffer.TYPE_BYTE, w, h, 1 );
		byte[] r = { (byte) 0x000000FF, (byte) newDataColor.getRed() };
		byte[] g = { (byte) 0x000000FF, (byte) newDataColor.getGreen() };
		byte[] b = { (byte) 0x000000FF, (byte) newDataColor.getBlue() };
		ColorModel cm = new IndexColorModel( 1, 2, r, g, b, 0 );
		Raster raster = Raster.createWritableRaster( sm, null );
		//Text.sout("Image created with size = " + getMemorySize( raster ));
		//		raster = Raster.createPackedRaster( DataBuffer.TYPE_BYTE, w, h, 1, 1, new Point());
		//		raster = raster.createCompatibleWritableRaster();
		BufferedImage bi2 = new BufferedImage( cm, (WritableRaster) raster, false, null );
		int valueRGB = newDataColor.getRGB();
		for ( int y = 0; y < h; y++ )
		{
			for ( int x = 0; x < w; x++ )
			{
				int pixCol = bi.getRGB( x, y );
				double colDist = SpectralLibrary.colorDistance( new Color( pixCol ), dataColor );
				if ( colDist <= dist )
				{
					bi2.setRGB( x, y, valueRGB );
				}
				else
				{
					bi2.setRGB( x, y, 0x00FFFFFF );
				}

			}
		}
		return bi2;
	}

	/**
	 * Selects designated color and returns 1-bit matric {@link BitMatrix} instance,
	 * zero bit means non-data value and one valued bit means data value
	 * and must be colored in image with user designated color
	 *
	 * @param bi        {@link java.awt.image.BufferedImage} instance to select data color from it
	 * @param dataColor {@link java.awt.Color} instance to find in image
	 * @param dist      color range to select from designated dataColor. If dist == 0.0, only namely designated dataColor
	 *                  will be selected. See {@link ru.ts.colors.SpectralLibrary#colorDistance(java.awt.Color, java.awt.Color)} description
	 *                  to understand about color space distance
	 * @param bm        {@link BitMatrix} instance to accept result. Is cleared before usage.
	 *                  If width and height of matrix not coincide with input BufferedImage dimensions or matrix is {@code null},
	 *                  new BitMatrix is returned, original one remain unchanged
	 * @return {@link BitMatrix} instance. May be new one if designated input matrix is {@code null} or not correspond
	 *         to designated image size. If designated matrix is allright, itself is returned
	 */
	public static BitMatrix makeBitMatrixFromImage( BufferedImage bi, Color dataColor, double dist, BitMatrix bm, IProgressObserver prog )
	{
		int w = bi.getWidth();
		int h = bi.getHeight();
		final float fH = (float) h;
		if ( bm == null || bm.columns() != w || bm.rows() != h )
		{
			bm = new BitMatrix( w, h );
		}
		else
		{
			bm.clear();
		}
		for ( int y = 0; y < h; y++ )
		{
			for ( int x = 0; x < w; x++ )
			{
				int pixCol = bi.getRGB( x, y );
				double colDist = SpectralLibrary.colorDistance( new Color( pixCol ), dataColor );
				if ( colDist <= dist )
				{
					bm.putQuick( x, y, true );
				}
			}
			if ( prog != null )
			{
				prog.setProgress( ( (float) ( y + 1 ) ) / fH );
			}
		}
		return bm;
	}

	/**
	 * Selects designated color and returns 1-bit matric {@link BitMatrix} instance,
	 * zero bit means non-data value and one valued bit means data value
	 * and must be colored in image with user designated color
	 *
	 * @param bi        {@link java.awt.image.BufferedImage} instance to select data color from it
	 * @param dataColor {@link java.awt.Color} instance to find in image
	 * @param dist      color range to select from designated dataColor. If dist == 0.0, only namely designated dataColor
	 *                  will be selected. See {@link ru.ts.colors.SpectralLibrary#colorDistance(Color, Color)}  description
	 *                  to understand about color space distance
	 * @param bm        {@link BitMatrix} instance to accept result. Is cleared before usage.
	 *                  If width and height of matrix not coincide with input BufferedImage dimensions or matrix is {@code null},
	 *                  new BitMatrix is returned, original one remain unchanged
	 * @param expand    int value to expand resulting matrix from original size in both directions
	 * @return {@link BitMatrix} instance. May be new one if designated input matrix is {@code null} or not correspond
	 *         to designated image size. If designated matrix is allright, itself is returned
	 */
	public static BitMatrix makeBitMatrixFromImage( BufferedImage bi, Color dataColor, double dist, BitMatrix bm, int expand )
	{
		int w = bi.getWidth();
		int h = bi.getHeight();
		final int exH = h + expand;
		final int exW = w + expand;
		if ( bm == null || bm.columns() != exW || bm.rows() != exH )
		{
			bm = new BitMatrix( exW, exH );
		}
		else
		{
			bm.clear();
		}
		for ( int y = 0; y < h; y++ )
		{
			for ( int x = 0; x < w; x++ )
			{
				int pixCol = bi.getRGB( x, y );
				double colDist = SpectralLibrary.colorDistance( new Color( pixCol ), dataColor );
				if ( colDist <= dist )
				{
					bm.putQuick( x + expand, y + expand, true );
				}
			}
		}
		return bm;
	}

	/**
	 * Returns 1-bit {@link BufferedImage} with copy of designated {@link BitMatrix}. Bits set to 1 are colored as valueColor (default RED),
	 * other colored as emptyColor (default WHITE)
	 *
	 * @param bm         {@link BitMatrix} instance to convert to image.
	 * @param valueColor how to color bit set to 1
	 * @param emptyColor how to color bit set to 0
	 * @return resulted {@link BufferedImage} instance. Or null if input matrix is null too
	 */
	public static BufferedImage makeImageFromBitMatrix( BitMatrix bm, Color valueColor, Color emptyColor )
	{
		if ( bm == null )
		{
			return null;
		}
		int w = bm.columns();
		int h = bm.rows();
		MultiPixelPackedSampleModel sm = new MultiPixelPackedSampleModel( DataBuffer.TYPE_BYTE, w, h, 1 );
		/*
			Color valueColor = Color.RED;
			Color emptyColor = Color.WHITE;
	*/
		byte[] r = { (byte) emptyColor.getRed(), (byte) valueColor.getRed() };
		byte[] g = { (byte) emptyColor.getRed(), (byte) valueColor.getGreen() };
		byte[] b = { (byte) emptyColor.getRed(), (byte) valueColor.getBlue() };
		ColorModel cm = new IndexColorModel( 1, 2, r, g, b, 0 );

		Raster raster = Raster.createWritableRaster( sm, new Point() );
		//Text.sout("Image created with size = " + getMemorySize( raster ));
		byte[] bytes = ( (DataBufferByte) raster.getDataBuffer() ).getData();
		int lineStride = ( w + 7 ) / 8;
		BufferedImage bi2 = new BufferedImage( cm, (WritableRaster) raster, false, null );
		//		int valueRGB = valueColor.getRGB();
		//		int cardinality = 0;
		for ( int y = 0, ypos = 0; y < h; y++, ypos += lineStride )
		{
			for ( int x = 0; x < w; x++ )
			{
				int pos = ypos + x / 8;
				if ( bm.get( x, y ) )
				{
					int off = 0x80 >>> ( x % 8 );
					bytes[ pos ] |= off;
					//					cardinality++;
				}
			}
		}
		//Text.sout(String.format("DEBUG:makeImageFromBitMatrix  w %d, h %d, cardinality %d, lineStride %d", w, h, cardinality, lineStride ));
		return bi2;
	}

	/**
	 * This class overrides the setCompressionQuality() method to workaround
	 * a problem in compressing JPEG images using the javax.imageio package.
	 */
	private static class MyImageWriteParam extends JPEGImageWriteParam
	{
		public MyImageWriteParam()
		{
			super( Locale.getDefault() );
		}

		/**
		 * This method accepts quality levels between 0.0 (lowest) and 1.0 (highest)
		 *
		 * @param quality float JPEG compression quality value. Min 0.0, max 1.0
		 */
		public void setCompressionQuality( float quality )
		{
			if ( ( quality < 0.0F ) || ( quality > 1.0F ) )
			{
				throw new IllegalArgumentException( "Quality out-of-bounds (0..1) !" );
			}
			this.compressionQuality = quality;
		}
	}

	/**
	 * Returns the format name of the image in the object 'o'.
	 *
	 * @param o 'o' an Object to be used as an input source, such as a {@link File}, readable {@link java.io.RandomAccessFile},
	 *          or {@link java.io.InputStream}
	 * @return {@code null} if image is bad or format is not known.
	 */
	public static String getFormatName( Object o )
	{
		ImageInputStream iis = null;
		try
		{
			// Create an image input stream on the image
			iis = ImageIO.createImageInputStream( o );

			// Find all image readers that recognize the image format
			Iterator iter = ImageIO.getImageReaders( iis );
			if ( !iter.hasNext() )
			{
				// No readers found
				return null;
			}

			// Use the first reader
			javax.imageio.ImageReader reader = (javax.imageio.ImageReader) iter.next();

			// Close stream
			iis.close();

			// Return the format name
			return reader.getFormatName();
		}
		catch ( IOException e )
		{
		}
		finally
		{
			if ( iis != null )
			{
				try
				{
					iis.close();
				}
				catch ( IOException e )
				{
				}
			}
		}
		// The image could not be read
		return null;
	}


	/**
	 * Test some features
	 *
	 * @param args command line options
	 */
	public static void main( String[] args ) throws IOException
	{
		if ( args.length == 0 )
		{
			Text.sout( "Expected file paths not found " );
		}


		for ( String path : args )
		{
			Text.sout( "Processing file \"" + path + "\"" );
			try
			{
				/*
						FileImageInputStream iis = new FileImageInputStream( new File( path ) );

						String ext = Files.getExtension( path );
						if ( Text.isEmpty( ext ) )
						{
							Text.serr( "Input path doesn't have extension" );
							continue;
						}

						// Find corresponding writer
						Text.sout( "Finding reader by extension \"*" + ext + "\"" );
						Iterator iter = ImageIO.getImageReadersByFormatName( ext.substring( 1 ) );
						javax.imageio.ImageReader reader = null;
						if ( iter.hasNext() )
						{
							reader = (javax.imageio.ImageReader) iter.next();
						}
						if ( reader == null )
						{
							throw new IllegalArgumentException( "\"*" + ext + "\" writer not found by call to ImageIO.getImageReadersByFormatName( *\"" + ext + "\" )" );
						}
						reader.setInput( iis );
		*/
				// Set some read parameters
				PartialImageReader pir = new PartialImageReader( path );
				int minIndex = pir.getMinIndex();
				int cnt = pir.imageCount();
				int w = pir.getWidth( 0 );
				int h = pir.getHeight( 0 );
				final int newW = w / 2;
				final int newH = h / 2;
				final int x0 = 0;
				final int y0 = 0;
				Rectangle rect = new Rectangle( 0, 0, 1181, 929 );
				ImageReader reader = pir.getReader();
				ImageTypeSpecifier imgType = reader.getRawImageType( 0 );

				BufferedImage nbi = ImgUtil.fileToImage( path );
				SampleModel sm = nbi.getSampleModel();
				ColorModel cm = nbi.getColorModel();
				//
				Text.sout( String.format( "Input file has w=%d h=%d, image num %d, min imdex %d, requesting rect %s", w, h, cnt, minIndex, Sys.rect2str( rect ) ) );
				// read the image
				BufferedImage bi = pir.read( rect );
				Text.sout( String.format( "Partially read image BufferedImage has w=%d h=%d", bi.getWidth(), bi.getHeight() ) );
				rect.x = x0;
				rect.y = y0;
				rect.setSize( newW, newH );
				bi = pir.read( rect );
				Text.sout( String.format( "Partially read image BufferedImage has w=%d h=%d", bi.getWidth(), bi.getHeight() ) );
				String newPath = Files.changeFileNameNoExt( path, Files.getNameNoExt( path ) + "_cut" );
				if ( ImgUtil.imageToFile( bi, newPath ) )
				{
					Text.sout( String.format( "Cut image sucessfully wrote to \"%s\"", newPath ) );
				}
				else
				{
					Text.sout( "Error to write output file" );
				}
			}
			catch ( Exception e )
			{
				Text.serr( "Error: " + e.getMessage() );
			}
		}
	}

	/**
	 * Gets image dimensions for given file  from
	 * <a href="https://stackoverflow.com/questions/672916/how-to-get-image-height-and-width-using-java/12164026#12164026">Stackoverflow</a>
	 * @param imgFile image file
	 * @return dimensions of image
	 * @throws IOException if the file is not a known image
	 */
	public static Dimension getImageDimension(File imgFile) throws IOException {
		int pos = imgFile.getName().lastIndexOf(".");
		if (pos == -1)
			throw new IOException("--- No extension for file: " + imgFile.getAbsolutePath());
		String suffix = imgFile.getName().substring(pos + 1);
		Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
		while(iter.hasNext()) {
			ImageReader reader = iter.next();
			try {
				ImageInputStream stream = new FileImageInputStream( imgFile);
				reader.setInput(stream);
				int width = reader.getWidth(reader.getMinIndex());
				int height = reader.getHeight(reader.getMinIndex());
				return new Dimension(width, height);
			} catch (IOException e) {
				Text.serr( "--- Error reading: \"%s\" -> %s", imgFile.getAbsolutePath(),  e.getLocalizedMessage()  );
			} finally {
				reader.dispose();
			}
		}

		throw new IOException("--- Not a known image file: " + imgFile.getAbsolutePath());
	}

}