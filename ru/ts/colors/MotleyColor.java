package ru.ts.colors;

import java.awt.*;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: YUGL
 * Date: 05.03.2009
 * Time: 15:55:52
 * To get a color near a given one.
 * This implementation is used now for motley styles in module 'artist',
 * so, if you need your own version of motley color, make a derived class.
 */
public class MotleyColor
{
  /**
   * Random instance
   */
  protected Random rnd;
  /**
   * permissible color component difference
   */
  protected int gap;

  /**
   * constructor
   */
  public MotleyColor() {
    this(12);
  }
  /**
   * constructor
   * @param gap  permissible color component difference
   */
  public MotleyColor(int gap) {
    rnd = new Random();
    setDiff(gap);
  }

  /**
   * Sets state of Random instance
   * @param seed  long seed
   */
  public void setSeed(long seed) {
    rnd.setSeed(seed);
  }
  /**
   * Sets state of Random instance
   * @param seed  double seed
   */
  public void setSeed(double seed) {
    rnd.setSeed(Double.doubleToLongBits(seed));
  }

  public void setDiff(int diff) {
    if (diff < 1) diff = 1;
    if (diff > 255) diff = 255;
    gap = diff;
  }

  /**
   *
   * @param c  given color
   * @return  motley color for a given one
   */
  public Color getMotleyFor(Color c) {
    return MotleyColor.getFor(c, gap, rnd);
  }
  /**
   * 1st variant to keep a nearness to central color
   * @param c  given color conponent
   * @param gap  permissible color component difference
   * @param rnd  Random instance
   * @return  contrasting color conponent for a given one
   */
  static public int getMotleyByte1(int c, int gap, Random rnd) {
    int c1 = c - gap;
    if (c1 < 0) c1 = 0;
    int c2 = c + gap;
    if (c2 > 255) c2 = 255;
    return c1 + rnd.nextInt(c2-c1+1);
  }
  /**
   * 2nd variant to keep gap value
   * @param c  given color conponent
   * @param gap  permissible color component difference
   * @param rnd  Random instance
   * @return  contrasting color conponent for a given one
   */
  static public int getMotleyByte2(int c, int gap, Random rnd) {
    int c1 = c - gap;
    if (c1 < 0) c1 = 0;
    int c2 = c1 + gap + gap;
    if (c2 > 255) {
      c2 = 255;
      c1 = 255 - gap - gap;
      if (c1 < 0) c1 = 0;
    }
    return c1 + rnd.nextInt(c2-c1+1);
  }

  /**
   *
   * @param c  given color
   * @param gap  permissible color component difference
   * @param rnd  Random instance
   * @return  motley color for a given one
   */
  static public Color getFor(Color c, int gap, Random rnd) {
    int r = getMotleyByte1(c.getRed(), gap, rnd);
    int g = getMotleyByte1(c.getGreen(), gap, rnd);
    int b = getMotleyByte1(c.getBlue(), gap, rnd);
    return new Color(r, g, b);
  }

}