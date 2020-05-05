package ru.ts.graphics.cache.image;

import ru.ts.common.arrays.DirectByteArray;
import ru.ts.graphics.ImgUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * <pre>
 * Class name: CachedImage
 * Created by SYGSKY for package ru.ts.graphics.image
 * Date: 07.12.2010
 * Time: 12:17:14
 * <p/>
 * ... To change this template use File | Settings | File Templates ...
 * <p/>
 * Changes:
 * </pre>
 */
public class CachedImage implements ICachedImage
{
  private boolean m_isPacked;
  private BufferedImage m_image;
  private DirectByteArray m_array;

  /**
   * Create packed image cache item
   * @param arr {@link DirectByteArray} instance to storte in cache
   */
  public CachedImage( DirectByteArray arr )
  {
    m_isPacked = true;
    m_array = arr;
  }

  public CachedImage( InputStream inp ) throws IOException
  {
    this( new DirectByteArray( inp ) );
  }

  /**
   * Create not packed cache item
   * @param bi
   * @throws IOException
   */
  public CachedImage( BufferedImage bi )
  {
    m_image = bi; // Not packed image
  }

  public boolean isPacked()
  {
    return m_image != null;
  }

  public BufferedImage getBufferedImage() throws IOException
  {
      return m_image == null ? ImageIO.read( m_array.getInputStream() ) : m_image;
  }

  public BufferedImage getBufferedImageCopy() throws IOException
  {
    if ( m_image == null )
      return ImageIO.read( m_array.getInputStream() );
    // We should return a copy image not internal one. Prepare the copy now
    return ImgUtil.cloneRasterData( m_image );
  }

  public DirectByteArray getAsPackedInputStream()
  {
    return m_array;
  }

  /**
   * Debug mode method
   * @return int approximated value for stored object (correct if stored in byte array
   * and approximated if stored in a form of a {@link BufferedImage}
   */
  public int getStoredSize()
  {
    if ( m_image != null )
//      return ImgUtil.getImageSize();
    {
      int vol = m_image.getWidth() * m_image.getHeight();
      int bits;
      //m_image.getRaster().get
      switch (m_image.getType() )
      {
        case BufferedImage.TYPE_3BYTE_BGR:
          bits = 24;
        case BufferedImage.TYPE_BYTE_GRAY:
      }
    }
    return m_array.size();
  }
}
