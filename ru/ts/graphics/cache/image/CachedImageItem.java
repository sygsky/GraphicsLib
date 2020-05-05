package ru.ts.graphics.cache.image;

import ru.ts.common.arrays.DirectByteArray;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * <pre>
 * Class name: CachedImageItem
 * Created by SYGSKY for package ru.ts.graphics.image
 * Date: 07.12.2010
 * Time: 12:24:50
 * <p/>
 * ... To change this template use File | Settings | File Templates ...
 * <p/>
 * Changes:
 * </pre>
 */
public class CachedImageItem extends CachedImage
{
  private String m_name;

  public CachedImageItem( DirectByteArray arr, String name )
  {
    super( arr );
    m_name = name.toLowerCase();
  }

  public CachedImageItem( InputStream inp, String name ) throws IOException
  {
    this ( new DirectByteArray( inp ), name );
  }

  public CachedImageItem( BufferedImage bi, String name ) throws IOException
  {
    super( bi );
    m_name = name.toLowerCase();
  }
}
