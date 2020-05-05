/**
 *</pre>
 * Created on 24.09.2010 12:56:00 
 * by SYGSKY
 * for project in 'ru.ts.graphics.ru.ts.gradient'
 *</pre>
 */
package ru.ts.gradient;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;

/**
 * <pre>
 * Package ru.ts.graphics.ru.ts.gradient
 * Author 'SYGSKY'
 * Created  24.09.2010  12:56:00
 * To change this template use File | Settings | File Templates.
 * </pre>
 */
public class RoundGradientPaint implements Paint {
    protected Point2D point;

    protected Point2D mRadius;

    protected Color mPointColor, mBackgroundColor;

    public RoundGradientPaint(double x, double y, Color pointColor,
        Point2D radius, Color backgroundColor) {
      if (radius.distance(0, 0) <= 0)
        throw new IllegalArgumentException("Radius must be greater than 0.");
      point = new Point2D.Double(x, y);
      mPointColor = pointColor;
      mRadius = radius;
      mBackgroundColor = backgroundColor;
    }

    public PaintContext createContext( ColorModel cm, Rectangle deviceBounds,
        Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
      Point2D transformedPoint = xform.transform(point, null);
      Point2D transformedRadius = xform.deltaTransform(mRadius, null);
      return new RoundGradientContext(transformedPoint, mPointColor,
          transformedRadius, mBackgroundColor);
    }

    public int getTransparency() {
      int a1 = mPointColor.getAlpha();
      int a2 = mBackgroundColor.getAlpha();
      return (((a1 & a2) == 0xff) ? OPAQUE : TRANSLUCENT);
    }
  }