package ru.ts.graphics; /**
 *</pre>
 * This example generates the Mandelbrot set in a pixel buffer and uses
 * the MemoryImageSource image producer to create an image from the pixel buffer.
 * A 16-color index color model is used to represent the pixel values.
 *</pre>
 */

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * <pre>
 * Package ru.ts.common.misc
 * Author unknown
 * Class created by Sygsky 20.08.2010  12:35:12
 * Instantiate this class and then use the draw() method to draw the
 * generated on the graphics context.
 * </pre>
 */
public class Mandelbrot
{
	class MyCanvas extends Canvas
	{
		Mandelbrot mandelbrot;

		MyCanvas()
		{
			// Add a listener for resize events
			addComponentListener( new ComponentAdapter()
			{
				// This method is called when the component's size changes
				public void componentResized( ComponentEvent evt )
				{
					Component c = (Component) evt.getSource();

					// Get new size
					Dimension newSize = c.getSize();

					// Regenerate the image
					mandelbrot = new Mandelbrot( newSize.width, newSize.height );
					c.repaint();
				}
			} );
		}

		public void paint( Graphics g )
		{
			if ( mandelbrot != null )
			{
				mandelbrot.draw( g, 0, 0 );
			}
		}
	}
	    // Dimension of the image
    int width;
    int height;

    // Holds the generated image
    Image image;

    public Mandelbrot(int width, int height) {
        // Initialize with default location
        this(width, height, new Rectangle2D.Float(-2.0f, -1.2f, 3.2f, 2.4f));
    }

    public Mandelbrot(int width, int height, Rectangle2D.Float loc) {
        // Initialize color model
        generateColorModel();

        this.width = width;
        this.height = height;
        image = Toolkit.getDefaultToolkit().createImage(
            new MemoryImageSource(width, height,
            colorModel, generatePixels(width, height, loc), 0, width));
    }

    public void draw(Graphics g, int x, int y) {
        g.drawImage(image, x, y, null);
    }

    private byte[] generatePixels(int w, int h, Rectangle2D.Float loc) {
        float xmin = loc.x;
        float ymin = loc.y;
        float xmax = loc.x+loc.width;
        float ymax = loc.y+loc.height;

        byte[] pixels = new byte[w * h];
        int pIx = 0;
        float[] p = new float[w];
        float q = ymin;
        float dp = (xmax-xmin)/w;
        float dq = (ymax-ymin)/h;

        p[0] = xmin;
        for (int i=1; i<w; i++) {
            p[i] = p[i-1] + dp;
        }

        for (int r=0; r<h; r++) {
            for (int c=0; c<w; c++) {
                int color = 1;
                float x = 0.0f;
                float y = 0.0f;
                float xsqr = 0.0f;
                float ysqr = 0.0f;
                do {
                    xsqr = x*x;
                    ysqr = y*y;
                    y = 2*x*y + q;
                    x = xsqr - ysqr + p[c];
                    color++;
                } while (color < 512 && xsqr + ysqr < 4);
                pixels[pIx++] = (byte)(color % 16);
            }
            q += dq;
        }
        return pixels;
    }

    // 16-color model
    ColorModel colorModel;

    private void generateColorModel() {
        // Generate 16-color model
        byte[] r = new byte[16];
        byte[] g = new byte[16];
        byte[] b = new byte[16];

        r[0] = 0; g[0] = 0; b[0] = 0;
        r[1] = 0; g[1] = 0; b[1] = (byte)192;
        r[2] = 0; g[2] = 0; b[2] = (byte)255;
        r[3] = 0; g[3] = (byte)192; b[3] = 0;
        r[4] = 0; g[4] = (byte)255; b[4] = 0;
        r[5] = 0; g[5] = (byte)192; b[5] = (byte)192;
        r[6] = 0; g[6] = (byte)255; b[6] = (byte)255;
        r[7] = (byte)192; g[7] = 0; b[7] = 0;
        r[8] = (byte)255; g[8] = 0; b[8] = 0;
        r[9] = (byte)192; g[9] = 0; b[9] = (byte)192;
        r[10] = (byte)255; g[10] = 0; b[10] = (byte)255;
        r[11] = (byte)192; g[11] = (byte)192; b[11] = 0;
        r[12] = (byte)255; g[12] = (byte)255; b[12] = 0;
        r[13] = (byte)80; g[13] = (byte)80; b[13] = (byte)80;
        r[14] = (byte)192; g[14] = (byte)192; b[14] = (byte)192;
        r[15] = (byte)255; g[15] = (byte)255; b[15] = (byte)255;

        colorModel = new IndexColorModel(4, 16, r, g, b);
    }
	private void generateColorModel2() {
	    // Generate 16-color model
	    byte[] r = new byte[16];
	    byte[] g = new byte[16];
	    byte[] b = new byte[16];

	    r[0] = 66; g[0] = 30; b[0] = 15; //brown 3
	    r[1] = 25; g[1] = 7; b[1] = 26; // dark violet
	    r[2] = 9; g[2] = 1; b[2] = 47;  //9   1  47 # darkest blue
	    r[3] = 4; g[3] = 4; b[3] = 73;  //4   4  73 # blue 5
	    r[4] = 0; g[4] = 7; b[4] = 100; // 0   7 100 # blue 4
	    r[5] = 12; g[5] = 44; b[5] = (byte)138; //12  44 138 # blue 3
	    r[6] = 24; g[6] = 82; b[6] = (byte)177;// 24  82 177 # blue 2
	    r[7] = 57; g[7] = 25; b[7] = ( byte ) 209; // 57 125 209 # blue 1
	    r[8] = (byte)134; g[8] = ( byte ) 181; b[8] = ( byte ) 229; //134 181 229 # blue 0
	    r[9] = (byte)211; g[9] = ( byte ) 236; b[9] = (byte)248; //211 236 248 # lightest blue
	    r[10] = (byte)241; g[10] = ( byte ) 233; b[10] = (byte)191; //241 233 191 # lightest yellow
	    r[11] = (byte)248; g[11] = (byte)201; b[11] = 95;  //248 201  95 # light yellow
	    r[12] = (byte)255; g[12] = (byte)170; b[12] = 0;  //255 170   0 # dirty yellow
	    r[13] = (byte)204; g[13] = (byte)128; b[13] = 0; //204 128   0 # brown 0
	    r[14] = (byte)153; g[14] = (byte)87; b[14] = 0;  //153  87   0 # brown 1
	    r[15] = (byte)106; g[15] = (byte)52; b[15] = 3;//106  52   3 # brown 2

	    colorModel = new IndexColorModel(4, 16, r, g, b);
	}


}
