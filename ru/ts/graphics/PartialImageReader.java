package ru.ts.graphics;

import ru.ts.common.misc.Files;
import ru.ts.common.misc.Text;
import ru.ts.common.misc.Sys;
import ru.ts.graphics.bmp.MyBMPImageReader;

import javax.imageio.ImageReadParam;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.awt.*;
import java.awt.image.BufferedImage;

import com.sun.imageio.plugins.bmp.BMPImageReaderSpi;

/**
 * This class allows to read part of standard Java source image only by system means
 */
public class PartialImageReader
{
	private String m_path;

	private javax.imageio.ImageReader m_reader;
	private int m_imgNum;
	private int m_minIndex;
	private int m_defaultIndex;
	private boolean m_seekForward;
	ImageReadParam m_params;

	public PartialImageReader( String path ) throws IOException
	{
		m_path = path;
		m_params = new ImageReadParam();
		final FileImageInputStream iis = new FileImageInputStream( new File( path ) );

		final String ext = Files.getExtension( path );
		if ( Text.isEmpty( ext ) )
		{
			Text.serr( "Input path doesn't have extension" );
			throw new IllegalArgumentException( "Input path " + path + " doesn't have extension" );
		}

		// Find corresponding writer

		m_reader = null;
		final String ext0 = ext.substring( 1 );
		if ( ext0.equalsIgnoreCase( "BMP" ) )
		{
			m_reader = new MyBMPImageReader( new BMPImageReaderSpi() );
		}
		else
		{
			final Iterator iter = ImageIO.getImageReadersBySuffix( ext0 );
			if ( iter.hasNext() )
			{
				m_reader = (javax.imageio.ImageReader) iter.next();
			}
			if ( m_reader == null )
			{
				throw new IllegalArgumentException( "\"*" + ext + "\" writer not found by call to ImageIO.getImageReadersByFormatName( *\"" + ext + "\" )" );
			}
		}
		m_reader.setInput( iis );
		m_seekForward = m_reader.isSeekForwardOnly();
		m_imgNum = m_reader.getNumImages( true );
		m_defaultIndex = m_minIndex = m_reader.getMinIndex();
	}

	/**
	 * This method sets read region for source region
	 *
	 * @param x X offset
	 * @param y Y offset
	 * @param w X size
	 * @param h Y size
	 */
	public void setReadRegion( int x, int y, int w, int h )
	{
		setReadRegion( new Rectangle( x, y, w, h ) );
	}

	public ImageReader getReader()
	{
		return m_reader;
	}

	/**
	 * This method sets read region for source region
	 */
	public void setReadRegion( Rectangle rect )
	{
		m_params.setSourceRegion( rect );
	}

	public Rectangle getReadRegion( Rectangle rect )
	{
		if ( rect == null )
		{
			return m_params.getSourceRegion();
		}
		rect.setRect( m_params.getSourceRegion() );
		return rect;
	}

	/**
	 * Gets available image count
	 *
	 * @return int with image count available
	 */
	public int imageCount()
	{
		return m_imgNum;
	}

	/**
	 * Returns {@code true} if this stream is seek forward one
	 *
	 * @return {@code true} if this stream is seek forward one else {@code false}
	 */
	public boolean isSeekForward()
	{
		return m_seekForward;
	}

	/**
	 * Reads whole image with designated index
	 *
	 * @param i index of image (main one is usually 0)
	 * @return resulted {@link java.awt.image.BufferedImage} instance to
	 * @throws IOException on any error
	 */
	public BufferedImage read( int i ) throws IOException
	{
		m_params.setSourceRegion( null );
		return m_reader.read( i, m_params );
	}

	/**
	 * Reads whole image with minimum index (usually main one)
	 *
	 * @return resulted {@link java.awt.image.BufferedImage} instance to
	 * @throws IOException on any error
	 */
	public BufferedImage read() throws IOException
	{
		m_params.setSourceRegion( null );
		return m_reader.read( m_defaultIndex, m_params );
	}

	/**
	 * Read designated rectangle from source image with sunsampling 1 to 1
	 *
	 * @param i        int index of requested image
	 * @param readRect {@link java.awt.Rectangle} instance of rectangle to read
	 * @return {@link java.awt.image.BufferedImage} instance read from source
	 * @throws java.io.IOException on any error
	 */
	public BufferedImage read( int i, Rectangle readRect ) throws IOException
	{
		m_params.setSourceRegion( readRect );
		return m_reader.read( i, m_params );
	}

	public BufferedImage read( Rectangle readRect ) throws IOException
	{
		m_params.setSourceRegion( readRect );
		//Text.sout( "Source rect " + Sys.rect2str( readRect ) );
		return m_reader.read( m_defaultIndex, m_params );
	}

	public int getMinIndex()
	{
		return m_minIndex;
	}

	public int getWidth( int i ) throws IOException
	{
		return m_reader.getWidth( i );
	}

	public int getWidth() throws IOException
	{
		return m_reader.getWidth( m_defaultIndex );
	}

	public int getHeight( int i ) throws IOException
	{
		return m_reader.getHeight( i );
	}

	public int getHeight() throws IOException
	{
		return m_reader.getHeight( m_defaultIndex );
	}

}
