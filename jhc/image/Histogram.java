package jhc.image;

import static ru.ts.common.misc.Text.serr;
import static ru.ts.common.misc.Text.sout;
import ru.ts.common.misc.*;
import static ru.ts.graphics.ImgUtil.imageType2String;
import static ru.ts.graphics.ImgUtil.getMemorySize;
import static ru.ts.graphics.ImgUtil.imageToFile;
import ru.ts.graphics.ImgUtil;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.DataBuffer;
import java.awt.*;
import java.io.*;

import jhc.image.reader.IRasterReader;

/**
 * Created by IntelliJ IDEA.
 * User: sigolaev_va
 * Date: 28.10.2013
 * Time: 13:34:02
 * Image histogram methods. Works in RGB color space
 */
public class Histogram
{
	/*
	 Weighting factors used by getPixelValue(), getHistogram() and convertToByte().
	 Enable "Weighted RGB Conversion" in <i>Edit/Options/Conversions</i>
	 to use 0.299, 0.587 and 0.114.
 */
	//private static final double rWeight=1d/3d, gWeight=1d/3d,	bWeight=1d/3d;
	public static final double RED_WEIGHT = 0.299d;
	public static final double GREEN_WEIGHT = 0.587d;
	public static final double BLUE_WEIGHT = 0.114d;
	public static final double ONE_THIRD = 1.0d/3.0d;

	public static final double[] COLOUR_BAND_WEIGHTS = { RED_WEIGHT, GREEN_WEIGHT, BLUE_WEIGHT };

	private double rWeight = RED_WEIGHT, gWeight = GREEN_WEIGHT, bWeight = BLUE_WEIGHT;

	private boolean m_weightOn = false;

	/**
	 * Image type
	 */
	private int m_type;
	private int[] m_histByte;
	private int[][] m_histRGB;
	private int[] m_histGray;

	public double getRWeight()
	{
		return rWeight;
	}

	public boolean isWeightOn()
	{
		return m_weightOn;
	}

	public void setWeightOn( boolean weightOn )
	{
		m_weightOn = weightOn;
	}

	public double getGWeight()
	{
		return gWeight;
	}

	public double getBWeight()
	{
		return bWeight;
	}

	public static final Histogram createRGBHistogram()
	{
		return new Histogram();
	}

	public static Histogram createGrayHistogram()
	{
		return new Histogram( ONE_THIRD, ONE_THIRD, ONE_THIRD );
	}

	public Histogram( double rWeight, double gWeight, double bWeight )
	{
		this.rWeight = rWeight;
		this.gWeight = gWeight;
		this.bWeight = bWeight;
	}

	public Histogram( BufferedImage img )
	{
		switch ( m_type = img.getType())
		{
			case BufferedImage.TYPE_BYTE_BINARY:
				break;
			case BufferedImage.TYPE_BYTE_GRAY:
			case BufferedImage.TYPE_BYTE_INDEXED:
				bWeight = ONE_THIRD;
				gWeight = ONE_THIRD;
				rWeight = ONE_THIRD;
				m_histByte = readImageHistogramByte( img );
				break;
			case BufferedImage.TYPE_4BYTE_ABGR:
			case BufferedImage.TYPE_3BYTE_BGR:
			case BufferedImage.TYPE_4BYTE_ABGR_PRE:
/*
				bWeight = RED_WEIGHT;
				gWeight = GREEN_WEIGHT;
				rWeight = BLUE_WEIGHT;
				break;
*/
			case BufferedImage.TYPE_INT_ARGB:
			case BufferedImage.TYPE_INT_RGB:
			case BufferedImage.TYPE_INT_ARGB_PRE:
				rWeight = RED_WEIGHT;
				gWeight = GREEN_WEIGHT;
				bWeight = BLUE_WEIGHT;
				m_histRGB = readImageHistogramAsRGB( img );
				m_histGray = readImageHistogramAsGray( img );
				break;
		}
	}

	public int[] getImageHistogramByte()
	{
		return m_histByte;
	}

	public int[][] getImageHistogramRGB()
	{
		return m_histRGB;
	}

	public int[] getImageHistogramGray()
	{
		return m_histGray;
	}

	private int[] readImageHistogramByte( BufferedImage img )
	{
		int[] hist= new int[HIST_LEN];

		Raster rast = img.getData();
		IRasterReader rr = new IRasterReader.RasterReader( rast );

		int rgb;
		for ( int y = 0; y < img.getHeight(); y++ )
		{
			for ( int x = 0; x < img.getWidth(); x++ )
			{
				rgb = rr.getRGB( x, y );
				int bval = rgb & 0x000000FF;
				// Increase colors counter
				hist[ bval ]++;
			}
		}
		return hist;
	}

	private int[] readImageHistogramAsGray( BufferedImage img )
	{
		int[] hist= new int[HIST_LEN];

		Raster rast = img.getData();
		IRasterReader rr = new IRasterReader.RasterReader( rast );

		int rgb;
		for ( int y = 0; y < img.getHeight(); y++ )
		{
			for ( int x = 0; x < img.getWidth(); x++ )
			{
				rgb = rr.getRGB( x, y );
				int red = ( rgb >> 16 ) & 0x000000FF;
				int green = ( rgb >> 8 ) & 0x000000FF;
				int blue = rgb & 0x000000FF;

				int bval = getGrayIntValue( red, green, blue );
				// Increase colors counter
				hist[ bval ]++;
			}
		}
		return hist;
	}

	private int[][] readImageHistogramAsRGB( BufferedImage img )
	{
		int[][] RGB = new int[3][HIST_LEN];
		int[] rh = RGB[ R ];
		int[] gh = RGB[ G ];
		int[] bh = RGB[ B ];

		Raster rast = img.getData();
		IRasterReader rr = new IRasterReader.RasterReader( rast );

		Arrs.arraySet( rh, 0 );
		Arrs.arraySet( gh, 0 );
		Arrs.arraySet( bh, 0 );

		Raster raster = img.getRaster();
		int rgb;
		for ( int y = 0; y < img.getHeight(); y++ )
		{
			for ( int x = 0; x < img.getWidth(); x++ )
			{
				rgb = rr.getRGB( x, y );
				int red = ( rgb >> 16 ) & 0x000000FF;
				int green = ( rgb >> 8 ) & 0x000000FF;
				int blue = rgb & 0x000000FF;
				// Increase colors counter
				rh[ red ]++;
				gh[ green ]++;
				bh[ blue ]++;
			}
		}
		return RGB;
	}

	public Histogram()
	{
	}

	/**
	 * Index of different histogram chcnnel part
	 * R for Red
	 * G for freen
	 * B for Blue parts correspondingly
	 */
	public static final int R = 0;
	public static final int G = 1;
	public static final int B = 2;
	public static final int HIST_LEN = 256;

	/**
	 * Gets image 3 color (RGB) histogram
	 *
	 * @param input {@link java.awt.image.BufferedImage} instance to read histogram from
	 * @return int[3][256] fit with histogram values (frequences)
	 */
	public static int[][] readImageRGBHist( BufferedImage input )
	{
		int[][] RGB = new int[3][HIST_LEN];
		int[] rh = RGB[ R ];
		int[] gh = RGB[ G ];
		int[] bh = RGB[ B ];

		Raster rast = input.getData();
		IRasterReader rr = new IRasterReader.RasterReader( rast );

		Arrs.arraySet( rh, 0 );
		Arrs.arraySet( gh, 0 );
		Arrs.arraySet( bh, 0 );

		Raster raster = input.getRaster();
		DataBuffer buffer = raster.getDataBuffer();
		int rgb;
		for ( int y = 0; y < input.getHeight(); y++ )
		{
			for ( int x = 0; x < input.getWidth(); x++ )
			{
				rgb = rr.getRGB( x, y );
				int red = ( rgb >> 16 ) & 0x000000FF;
				int green = ( rgb >> 8 ) & 0x000000FF;
				int blue = rgb & 0x000000FF;
				// Increase colors counter
				rh[ red ]++;
				gh[ green ]++;
				bh[ blue ]++;
			}
		}
		return RGB;
	}

	public static int[][] readImageHistogram( BufferedImage input )
	{
		int[][] res = new int[3][HIST_LEN];
		int[] rhistogram = res[ R ];
		int[] ghistogram = res[ G ];
		int[] bhistogram = res[ B ];

		Arrs.arraySet( rhistogram, 0 );
		Arrs.arraySet( ghistogram, 0 );
		Arrs.arraySet( bhistogram, 0 );

		for ( int i = 0; i < input.getWidth(); i++ )
		{
			for ( int j = 0; j < input.getHeight(); j++ )
			{
				int red = new Color( input.getRGB( i, j ) ).getRed();
				int green = new Color( input.getRGB( i, j ) ).getGreen();
				int blue = new Color( input.getRGB( i, j ) ).getBlue();
				// Increase the values of colors
				rhistogram[ red ]++;
				ghistogram[ green ]++;
				bhistogram[ blue ]++;
			}
		}
		return res;
	}

	/**
	 * Calculates separate histogram for each color channel
	 *
	 * @param hist int[3][256] with real histogram calculated
	 * @param imgW image width
	 * @param imgH image height
	 * @return new int[3][256] with separate R, G and B channel histograms
	 */
	public static int[][] equalizeHist( int[][] hist, int imgW, int imgH )
	{
		// Create the lookup table
		// Fill the lookup table
		int[][] res = new int[3][256];
		int[] rHist = res[ Histogram.R ];
		int[] gHist = res[ Histogram.G ];
		int[] bHist = res[ Histogram.B ];
		for ( int i = 0; i < HIST_LEN; i++ ) rHist[ i ] = 0;
		for ( int i = 0; i < HIST_LEN; i++ ) gHist[ i ] = 0;
		for ( int i = 0; i < HIST_LEN; i++ ) bHist[ i ] = 0;
		long sumr = 0;
		long sumg = 0;
		long sumb = 0;
		// Calculate the scale factor
		float scale_factor = ( float ) ( 255.0 / ( imgW * imgH ) );

		for ( int i = 0; i < rHist.length; i++ )
		{
			sumr += hist[ Histogram.R ][ i ];
			int valr = ( int ) ( sumr * scale_factor );
			if ( valr > 255 )
			{
				rHist[ i ] = 255;
			}
			else rHist[ i ] = valr;

			sumg += hist[ Histogram.G ][ i ];
			int valg = ( int ) ( sumg * scale_factor );
			if ( valg > 255 )
			{
				gHist[ i ] = 255;
			}
			else gHist[ i ] = valg;

			sumb += hist[ Histogram.B ][ i ];
			int valb = ( int ) ( sumb * scale_factor );
			if ( valb > 255 )
			{
				bHist[ i ] = 255;
			}
			else bHist[ i ] = valb;
		}
		return res;
	}

	/**
	 * Equalize with standart color weight rWeight=0.299d, gWeight=0.587d,	bWeight=0.114d
	 *
	 * @param hist int[3][256] with real histogram calculated
	 * @param imgW image width
	 * @param imgH image height
	 * @return new int[256] with RGB summary histogram
	 */
	public static int[] equalizeHistAsGray( int[][] hist, int imgW, int imgH )
	{
		return equalizeHistAsGray( hist, imgW, imgH, new Histogram() );
	}

	/**
	 * Equalize using custom weight of each color channel. Method from Internet
	 *
	 * @param hist int[256] with histogram calculated before
	 * @param imgW image width
	 * @param imgH image height
	 * @param hg   {@link jhc.image.Histogram} with possible customized weights of color channels
	 * @return new int[256] with RGB summary histogram
	 */
	public static int[] equalizeHistAsGray( int[][] hist, int imgW, int imgH, Histogram hg )
	{
		double rWeight = hg.getRWeight();
		double gWeight = hg.getGWeight();
		double bWeight = hg.getBWeight();
		// Create the lookup table
		int[] res = new int[256];
		double sum = 0d;
		// Calculate the scale factor
		float scale_factor = ( float ) ( 255.0 / ( imgW * imgH ) );
		double r, g, b, v;
		int iv;
		for ( int i = 0; i < Histogram.HIST_LEN; i++ )
		{
			r = hist[ Histogram.R ][ i ];
			g = hist[ Histogram.G ][ i ];
			b = hist[ Histogram.B ][ i ];
			v = r * rWeight + g * gWeight + b * bWeight;
			iv = ( int ) ( v + 0.5 );
			sum += iv;
			int val = ( int ) ( sum * scale_factor );
			if ( val > 255 )
			{
				res[ i ] = 255;
			}
			else res[ i ] = val;
		}
		return res;
	}

	private static int[] equalizeColorHist( int[][] hist, int imgW, int imgH )
	{
		return equalizeColorHist( hist, imgW, imgH, new Histogram(), true );
	}

	/**
	 * Method from ImageJ
	 *
	 * @param hist
	 * @param imgW
	 * @param imgH
	 * @param hObj
	 * @param sqrtOn
	 * @return
	 */
	private static int[] equalizeColorHist( int[][] hist, int imgW, int imgH, Histogram hObj, boolean sqrtOn )
	{
		hObj.setWeightOn( sqrtOn );
		// Create the lookup table
		int[] lut = new int[HIST_LEN];
		double r, g, b, v;
		double sum, sum0;
		final int[] histR = hist[ Histogram.R ];
		final int[] histG = hist[ Histogram.G ];
		final int[] histB = hist[ Histogram.B ];
		sum = sum0 = hObj.getWeightedValue( hObj.getGrayValue( histR[ 0 ], histG[ 0 ], histB[ 0 ] ) );
		final int HIST_MAX_VAL = HIST_LEN - 1;
		for ( int i = 1; i < HIST_MAX_VAL; i++ )
		{
			r = histR[ i ];
			g = histG[ i ];
			b = histB[ i ];
			lut[ i ] = ( int ) Math.round( v = hObj.getWeightedValue( hObj.getGrayValue( r, g, b ) ) );
			sum += 2 * v;
		}

		r = histR[ HIST_MAX_VAL ];
		g = histG[ HIST_MAX_VAL ];
		b = histB[ HIST_MAX_VAL ];
		sum += hObj.getWeightedValue( hObj.getGrayValue( r, g, b ) );

		double scale = HIST_MAX_VAL / sum;
		lut[ 0 ] = 0;
		sum = sum0;
		for ( int i = 1; i < HIST_LEN; i++ )
		{
			r = histR[ HIST_MAX_VAL ];
			g = histG[ HIST_MAX_VAL ];
			b = histB[ HIST_MAX_VAL ];
			double delta = hObj.getWeightedValue( hObj.getGrayValue( r, g, b ) );
			sum += delta;
			lut[ i ] = ( int ) Math.round( sum * scale );
			sum += delta;
		}
		lut[ HIST_MAX_VAL ] = HIST_MAX_VAL;
		return lut;
	}

	public double getGrayValue( double r, double g, double b )
	{
		return r * rWeight + g * gWeight + b * bWeight;
	}

	public int getGrayIntValue( int r, int g, int b )
	{
		return ( int ) Math.round( r * rWeight + g * gWeight + b * bWeight );
	}

	public int getGrayIntValue( double r, double g, double b )
	{
		return ( int ) Math.round( r * rWeight + g * gWeight + b * bWeight );
	}

	public int getGrayIntValue( int rgb )
	{
		return getGrayIntValue( ( rgb >> 16 ) & 0x000000FF, ( rgb >> 8 ) & 0x000000FF, rgb & 0x000000FF );
	}

	public double getGrayValue( int r, int g, int b )
	{
		return r * rWeight + g * gWeight + b * bWeight;
	}

	public double getGrayValue( byte r, byte g, byte b )
	{
		return r * rWeight + g * gWeight + b * bWeight;
	}

	private static double getWeightedValue( int[] histogram, int i )
	{
		int h = histogram[ i ];
		if ( h < 2 )
			return ( double ) h;
		return Math.sqrt( ( double ) ( h ) );
	}

	private double getWeightedValue( int h )
	{
		if ( h < 2 )
			return ( double ) h;
		return Math.sqrt( ( double ) ( h ) );
	}

	private double getWeightedValue( double val )
	{
		if ( val < 2.0d )
			return val;
		if ( m_weightOn )// need get sqrt from value
			return Math.sqrt( val );
		return val;
	}

	public static void storeImage( BufferedImage img, String title, String imgPath, String suffix, boolean makeThumb )
		throws IOException
	{
		String ext = Files.getExtension( imgPath ).substring( 1 );
		String newPath = createNewPath( imgPath, suffix );
		if ( !ext.equalsIgnoreCase( "jpg" ) )
		{
			sout( "+ " + title + " image saved as \"" + newPath + "\"" );
			imageToFile( img, ext, newPath );
		}
		else
		{
			if ( ImgUtil.compressJpegFile( img, new FileImageOutputStream( new File( newPath ) ), 0.9f ) )
				sout( "+ " + title + " image saved as \"" + newPath + "\" with quality 90%" );
			else
				serr( "- Failured to save \"" + newPath + "\"" );
		}

		if ( makeThumb )
		{
			int w = img.getWidth();
			int h = img.getHeight();
			// now calc output demo size for an image
			int demoH = h, demoW = w;
			if ( w < h )// Portrait image
			{
				if ( h > 640 )
				{
					demoH = 640;
					demoW = ( int ) Math.round( ( double ) w * ( double ) demoH / ( double ) h );
				}
			}
			else
			{
				if ( w > 640 )
				{
					demoW = 640;
					demoH = ( int ) Math.round( ( double ) h * ( double ) demoW / ( double ) w );
				}
			}
			BufferedImage demo = ImgUtil.resizeImage( img, demoW, demoH );

			newPath = createNewPath( imgPath, suffix + "_thumb" );
			if ( !ext.equalsIgnoreCase( "jpg" ) )
			{
				sout( "+ " + title + " thumbnail image saved as \"" + newPath + "\"" );
				imageToFile( demo, ext, newPath );
			}
			else
			{
				if ( ImgUtil.compressJpegFile( demo, new FileImageOutputStream( new File( newPath ) ), 0.9f ) )
					sout( "+ " + title + " thumbnail image saved as \"" + newPath + "\" with quality 90%" );
				else
					serr( "- Failured to save \"" + newPath + "\"" );
			}
		}
	}

	private static String createNewPath( String path, String nameSuffix )
	{
		IFilePath fp = new IFilePath.Impl( path );
		String newName = fp.getFileNameNoExt() + nameSuffix;
		fp.setFileNameNoExt( newName );
		return fp.getPath();
	}

	static void printHist( int[] lut, BufferedWriter bw ) throws IOException
	{
		bw.write( "value,count" );
		bw.newLine();
		int cnt = 0;
		for ( int i = 0; i < lut.length; i++ )
		{
			bw.write( String.format( "%d,%d", i, lut[ i ] ) );
			cnt += lut[ i ];
			bw.newLine();
		}
		bw.write( String.format( "Number of pixels " + cnt ) );
		bw.newLine();
	}

	/**
	 * Test some features
	 *
	 * @param args command line options
	 */
	public static void main( String[] args ) throws IOException
	{
		if ( args.length == 0 )
		{
			serr( "- Expected file path is empty" );
			return;
		}
		Text.sout( "+++ Test ImgUtil.enhanceContrast()" );
		sout( "+ Image path: " + args[ 0 ] );
		sout( "+ Image Formats available now: " + Text.joinByCommas( ImageIO.getReaderFileSuffixes() ) );
		BufferedImage img = ImageIO.read( new File( args[ 0 ] ) );
		if ( img == null )
		{
			serr( "- Can't read this image, use  ImageReaderSpi to check Image readers" );
			return;
		}

		Histogram hist;
		if ( img.getType() == BufferedImage.TYPE_BYTE_GRAY )
			hist = createGrayHistogram( );
		else
			hist = new Histogram( );

		int w, h;
		sout( String.format( "+ Image was successfully read. Its type %s (%d), w %d, h %d, mem size %d ",
			imageType2String( img ), img.getType(), w = img.getWidth(), h = img.getHeight(), getMemorySize( img.getRaster() ) ) );

		// now calc output demo size for an image
		int demoH = h, demoW = w;
		if ( w <= h )// Portrait image
		{
			if ( h > 640 )
			{
				demoH = 640;
				demoW = ( int ) Math.round( ( double ) w * ( double ) demoH / ( double ) h );
			}
		}
		else
		{
			if ( w > 640 )
			{
				demoW = 640;
				demoH = ( int ) Math.round( ( double ) h * ( double ) demoW / ( double ) w );
			}
		}
		if ( demoW == w && demoH == h )
		{
			sout( "+ No need for thumb image creation, original is small enough" );
		}
		else
		{
			sout( String.format( "+ Thumb image [will] have dimensions H %d x W %d", demoW, demoH ) );
			String newPath = createNewPath( args[ 0 ], "_thumb" );
			if ( Files.fileExists( newPath ) )
			{
				sout( "+ Original thumb image already exists, skipping creation" );
			}
			else
			{
				BufferedImage demoImg = ImgUtil.resizeImage( img, demoW, demoH );
				String ext = Files.getExtension( args[ 0 ] ).substring( 1 );
				demoImg = ImgUtil.resizeImage( img, demoW, demoH );
				ImgUtil.imageToFile( demoImg, ext, newPath );
				sout( "+ Thumbnail image created as \"" + newPath + "\"" );
			}
		}
		sout( "\n+ Test standard enchance contrasting method (equalizeHistAsGray) on this image" );
		TimeSpan ts = new TimeSpan();

		Histogram myHist = new Histogram( img );
		int[] histGray = myHist.getImageHistogramByte();
		final int[][] hst = readImageRGBHist( img );

		/*
			BufferedWriter bw;
			printHist( hst[ 0 ], bw = new BufferedWriter( new OutputStreamWriter( System.out ) ) );
	*/
		final double oneThird = 1d / 3d;
		Histogram eqWtHist = new Histogram( oneThird, oneThird, oneThird );

		sout( "+ Histogram reading duration " + ts.resetAfter().toString() );
		int[] lut = equalizeHistAsGray( hst, img.getWidth(), img.getHeight(), eqWtHist );
		LUT.applyLUT( lut, img );
		sout( "+ equalizeHistAsGray duration " + ts.resetAfter().toString() );
		storeImage( img, "Std Equalized", args[ 0 ], "_equalized", true );

		sout( "\n+ Test alternative enchance contrasting method with SQRTing values (equalizeColorHist) on this image" );
		img = ImageIO.read( new File( args[ 0 ] ) );// reread
		//hst = readImageRGBHist( img );
		lut = equalizeColorHist( hst, img.getWidth(), img.getHeight(), eqWtHist, true );
		LUT.applyLUT( lut, img );
		storeImage( img, "equalizeColorHist", args[ 0 ], "_equalizedSqrt", true );

		sout( "\n+ Test alternative enchance contrasting method without SQRTing values (equalizeColorHist) on this image" );
		img = ImageIO.read( new File( args[ 0 ] ) );// reread
		//hst = readImageRGBHist( img );
		lut = equalizeColorHist( hst, img.getWidth(), img.getHeight(), eqWtHist, false );
		LUT.applyLUT( lut, img );
		storeImage( img, "equalizeColorHist sqrt", args[ 0 ], "_equalizedAlt", true );

		sout( "\n+ Test separate RGB channels enchance contrasting method (equalizeHist) on this image" );
		img = ImageIO.read( new File( args[ 0 ] ) );
		int[][] lut3 = equalizeHist( hst, img.getWidth(), img.getHeight() );
		LUT.applyLUT( lut3, img );
		storeImage( img, "equalizeColorHist sqrt", args[ 0 ], "_equalizedCnl", true );
	}


}
