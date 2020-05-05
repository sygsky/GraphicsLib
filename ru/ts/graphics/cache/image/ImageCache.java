package ru.ts.graphics.cache.image;

import ru.ts.common.arrays.DirectByteArray;
import ru.ts.common.misc.Text;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * Class name: ImageCache
 * Created by SYGSKY for package ru.ts.graphics.image
 * Date: 07.12.2010
 * Time: 12:37:44
 * <p/>
 * ... To change this template use File | Settings | File Templates ...
 * <p/>
 * Changes:
 * </pre>
 */
public class ImageCache implements IImageCache
{
  private Map<String, ICachedImage> m_map = new HashMap<String, ICachedImage>();

  public void put( String key, ICachedImage img )
  {
    m_map.put(key.toLowerCase(), img );

    Text.sout(
      MessageFormat.format( "{0}: Added \"{1}\" of size {2}, cache size now {3}",
        Thread.currentThread().getName(), key, ((CachedImage)m_map.get( key )).getStoredSize(),
        m_map.size() ) );
  }

  public void put( String key, BufferedImage img )
  {
    m_map.put(key.toLowerCase(), new CachedImage( img ) );
    Text.sout(
      MessageFormat.format( "{0}: Added \"{1}\" of size {2}, cache size now {3}",
        Thread.currentThread().getName(), key, ((CachedImage)m_map.get( key )).getStoredSize(),
        m_map.size() ) );
  }

  public void put( String key, InputStream inp ) throws IOException
  {
    m_map.put(key.toLowerCase(), new CachedImage( inp ) );
    Text.sout(
      MessageFormat.format( "{0}: Added \"{1}\" of size {2}, cache size now {3}",
        Thread.currentThread().getName(), key, ((CachedImage)m_map.get( key )).getStoredSize(),
        m_map.size() ) );
  }

  public void put( String key, DirectByteArray arr )
  {
    m_map.put(key.toLowerCase(), new CachedImage( arr ) );
    Text.sout(
      MessageFormat.format( "{0}: Added \"{1}\" of size {2}, cache size now {3}",
        Thread.currentThread().getName(), key, ((CachedImage)m_map.get( key )).getStoredSize(),
        m_map.size() ) );
  }

  public ICachedImage get( String key )
  {
    return m_map.get( key.toLowerCase() );
  }

  public void remove( String key )
  {
    boolean has = has( key );
    if ( has )
      m_map.remove( key );
    Text.sout( MessageFormat.format( "Remove \"{0}\": {1}", key, has ? "removed" : " absent"));

  }

  public boolean has( String key )
  {
    return m_map.containsKey( key.toLowerCase() );
  }

}
