package ru.ts.graphics.cache.image;

import ru.ts.common.arrays.DirectByteArray;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * <pre>
 * Class name: ICachedImage
 * Created by SYGSKY for package ru.ts.graphics.image
 * Date: 07.12.2010
 * Time: 12:00:16
 * <p/>
 * ... To change this template use File | Settings | File Templates ...
 * <p/>
 * Changes:
 * </pre>
 */
public interface ICachedImage
{
  /**
   * If <code>true</code>  - image stored in packed format (PNG JPG etc), else
   * stored as a BufferedImage
   * @return <code>true</code> if image is stored in packed format (PNG, JPG etc) and
   * you can unpack it with {@link javax.imageio.ImageIO#read(java.io.InputStream)}
   * or <code>false</code>  if image is already in form of {@link BufferedImage}
   */
  boolean isPacked();

  /**
   * Return real instance of the cached image as a {@link BufferedImage}. To get
   * a copy, call to {@link #getBufferedImageCopy()} method
   * @return {@link BufferedImage} instance with an internal image instance
   */
  BufferedImage getBufferedImage() throws IOException;

  /**
   * Return cached image copy as a {@link BufferedImage}. To get internal instance,
   * call {@link #getBufferedImage()} METHOD
   * @return {@link BufferedImage} instance with an image COPY
   */
  BufferedImage getBufferedImageCopy() throws IOException;

  /**
   * Return cached image in packed (PNG, JPG etc) input format. You can handle
   *  it with {@link javax.imageio.ImageIO#read(java.io.InputStream)} method
   * @return  {@link DirectByteArray} instance with packed image or <code>null</code>
   * if image is stored unpacked. See {@link ICachedImage#isPacked} method
   */
  DirectByteArray getAsPackedInputStream();
}
