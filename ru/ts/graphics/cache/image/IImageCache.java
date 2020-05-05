package ru.ts.graphics.cache.image;

/**
 * <pre>
 * Class name: IImageCache
 * Created by SYGSKY for package ru.ts.graphics.image
 * Date: 07.12.2010
 * Time: 12:34:50
 * <p/>
 * ... To change this template use File | Settings | File Templates ...
 * <p/>
 * Changes:
 * </pre>
 */
public interface IImageCache
{
  void put( String key, ICachedImage img );
  ICachedImage get( String key );
  void remove( String key );
  boolean has( String key );
}
