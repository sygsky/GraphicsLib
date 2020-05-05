package ru.ts.graphics;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: YUGL
 * Date: 05.03.2009
 * Time: 11:05:41
 * To get a color contrasting with a given one.
 * This implementation is used now for shaded styles in module 'artist',
 * so, if you need your own version of contrast color, make a derived class.
 */
public class ContrastingColor {
  /**
   * previous color
   */
  protected Color normal;
  /**
   * previous contrasting color
   */
  protected Color shaded;
  /**
   *
   * @param c  given color
   * @return  contrasting color for a given one
   */
  public Color getContrastingFor(Color c) {
    if (!c.equals(normal)) {
      normal = c;
      shaded = ContrastingColor.getFor(c);
    }
    return shaded;
  }
  /**
   * static variant
   * @param c  given color
   * @return  contrasting color for a given one
   */
  static public Color getFor(Color c) {
    int r = getContrastingByte(c.getRed());
    int g = getContrastingByte(c.getGreen());
    int b = getContrastingByte(c.getBlue());
    return new Color(r, g, b);
  }
  /**
   * static variant
   * @param c  given color conponent
   * @return  contrasting color conponent for a given one
   */
  static public int getContrastingByte(int c) {
    c = 255 - c;
    if (c < 96) return c;
    if (c >= 160) return c;
//    if (c < 128) return 0;
    if (c < 128) return 48;
//    return 255;
    return 208;
  }

  /**
   * static variant
   * @param c  given color
   * @return  near but contrasting color for a given one
   */
  static public Color getNearFor(Color c) {
    int r = getContrastingNearByte(c.getRed());
    int g = getContrastingNearByte(c.getGreen());
    int b = getContrastingNearByte(c.getBlue());
    return new Color(r, g, b);
  }
  /**
   * static variant
   * @param c  given color conponent
   * @return  near but contrasting color conponent for a given one
   */
  static public int getContrastingNearByte(int c) {
    if (c < 128) return c+32;
    return c-32;
  }
}
