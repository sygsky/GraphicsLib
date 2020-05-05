/**
 *</pre>
 * Created on 24.09.2010 12:56:51 
 * by SYGSKY
 * for project in 'ru.ts.graphics.ru.ts.gradient'
 *</pre>
 */
package ru.ts.gradient;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.geom.Point2D;

/**
 * <pre>
 * Package ru.ts.graphics.ru.ts.gradient
 * Author 'SYGSKY'
 * Created  24.09.2010  12:56:51
 * To change this template use File | Settings | File Templates.
 * </pre>
 */
public class RoundGradientContext implements PaintContext {
    protected Point2D mPoint;

    protected Point2D mRadius;

    protected Color color1, color2;

    public RoundGradientContext(Point2D p, Color c1, Point2D r, Color c2) {
      mPoint = p;
      color1 = c1;
      mRadius = r;
      color2 = c2;
    }

    public void dispose() {
    }

    public ColorModel getColorModel() {
      return ColorModel.getRGBdefault();
    }

    public Raster getRaster(int x, int y, int w, int h) {
      WritableRaster raster = getColorModel().createCompatibleWritableRaster(
          w, h);

      int[] data = new int[w * h * 4];
	    final double radius = mRadius.distance(0, 0);
	    int red_1 = color1.getRed();
	    int green_1 = color1.getGreen();
	    int blue_1 = color1.getBlue();
	    int alpha_1 = color1.getAlpha();
	    int red_2 = color2.getRed();
	    int green_2 = color2.getGreen();
	    int blue_2 = color2.getBlue();
	    int alpha_2 = color2.getAlpha();
	    int delta_red = red_2 - red_1;
	    int delta_green = green_2 - green_1;
	    int delta_blue = blue_2 - blue_1;
	    int delta_alpha = alpha_2 - alpha_1;

	    int base = 0;
      for (int j = 0; j < h; j++) {
        for (int i = 0; i < w; i++) {
          double distance = mPoint.distance(x + i, y + j);
          double ratio = distance / radius;
          if (ratio > 1.0)
            ratio = 1.0;

          //int base = (j * w + i) * 4;
	        data[base++] = (int) (red_1 + ratio
              * delta_red);
	        data[base++] = (int) (green_1 + ratio
              * delta_green);
	        data[base++] = (int) (blue_1 + ratio
              * delta_blue);
	        data[base++] = (int) (alpha_1 + ratio
              * delta_alpha);
        }
      }
      raster.setPixels(0, 0, w, h, data);

      return raster;
    }
  }
