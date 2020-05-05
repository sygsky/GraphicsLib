package ru.ts.graphics;

/*import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.PlugInFilterRunner;
import ij.plugin.filter.RankFilters;
import ij.*;
import ij.gui.GenericDialog;
import ij.gui.DialogListener;
import ij.process.*;
import ij.measure.Calibration;*/

/**
 * A collection of fast filters (mean, min, max, median, background, ...)
 * working on a rectangular n*m or square n*n kernel.
 * Unidirectional filtering can be done with a kernel size of n*0 or 0*n pixels.
 * Apart form the speed, the kernel type is the main difference to the built-in ImageJ
 * Process>Filters, which are working on a circular kernel, see Process>Filters>Show Circular Masks.
 * <p/>
 * "Filter Types":
 * - Mean: Average over n*m pixels, where out-of-image pixels are replaced by nearest border pixel.
 * For large kerner sizes (radii), this gives high weight to the border pixels.
 * - Border-limited mean: Average over n*m pixels; in contrast to most ImageJ filters, out-of-image
 * pixels are not replaced by border pixels but rather the area for averaging is reduced at the border.
 * - Median: For unidirectional filters (i.e., with one of the radii = 0) the pixel is replaced by
 * the median of the pixels within a distance of 8x or y-)radius.
 * For bidirectional filters (i.e., if both x and y radii are >0), a fast and
 * rough approximation to the median in a rectangular surrounding is used:
 * First a median filter is applied in x direction, then in y direction.
 * In contrast to most ImageJ filters, out-of-image pixels are not replaced by border pixels but
 * rather the area for median determination is reduced at the border.
 * - Minimum: Minimum over n*m pixels
 * - Maximum: Maximum over n*m pixels
 * - Eliminate maxima: Runs "minimum" and "maximum" filters in succession.
 * Eliminates maxima smaller than the kernel.
 * - Eliminate minima: Runs "maximum" and "minimum" filters in succession.
 * Eliminates minima smaller than the kernel.
 * - Background from minima: eliminates maxima (see above) and smoothens the result (border-limited mean)
 * - Background from maxima: eliminates minima (see above) and smoothens the result (border-limited mean)
 * - Background from median: runs a median (median approximation for bidirectional filtering, see above),
 * then smoothens the result (border-limited mean)
 * <p/>
 * "x Radius" and "y Radius" determine the kernel size of the filters (in pixels, irrespective of any
 * spatial calibration of the image).
 * For each target (output) pixel, the simple operations (mean, min, max) are performed over a
 * neighborhood given by a rectangle of width = 2*xRadius+1 and height = 2*yRadius+1.
 * x Radius = 0 or y Radius=0 results in no filter operation in that direction.
 * <p/>
 * Check "Link x & y" if a square kernel (x Radius = y Radius) is desired.
 * <p/>
 * "Preprocessing" is an operation applied before all others. It can be "none", "smooth" or "median".
 * For unidirectinal filtering (y radius = 0 or x radius = 0), preprocessing is also unidirectional:
 * the kernel size for unidirectional preprocessing is 5x1 or 1x5 pixels.
 * For bidirectional filters (i.e., if both x and y radii are >0), for preprocessing averaging or
 * the (approximated) median runs over a 3x3 pixel kernel.
 * Preprocessing helps to eliminate outliers for the "minimum", "maximum" and related filters.
 * <p/>
 * "Subtract Filtered" does not output the result of the filter operation above, but rather the
 * original (input) image minus the result of the filter operation, plus an offset.
 * With "mean" filters, "Subtract Filtered" results in a high-pass filter; with "median" it
 * highlights outliers. With the "minimum" and "maximum" filters, "Subtract Filtered" is a
 * kind of edge detection, and with the other filters it provides various types of background
 * subtraction. Especially the "Background from minima" and "Background from maxima" filters
 * are suitable for background subtraction in images with particles: Make sure that xRadius
 * and yRadius are large enough to eliminate any particles (use preview without "Subtract Filtered",
 * then apply the filter with "Subtract Filtered").
 * <p/>
 * "Offset" is added to the data when subtracting a filtered image from the original one.
 * The offset is needed except for 32-bit float images to keep the result in the range of
 * the image type, e.g. 0-255 for 8-bit grayscale and 8-bit/channel RGB.
 * Use a low value (e.g., 10) for subtracting "background from minima", high values (e.g. 245)
 * for subtracting "background from maxima".
 * <p/>
 * Notes for users:
 * - All operations are performed on raw pixel values, not taking any grayscale calibration into account.
 * - This plugin provides a 'top hat' filter with "Eliminate Maxima" or "Eliminate Minima", and
 * the "Subtract Filtered" option enabled. When using "Background from maxima", set a suitable offset,
 * i.e., 255 for 8-bit and RGB, and the maximum of the image data range for 16-bit images (65535, or,
 * e.g., 4096 if your data are limited to 12 bits).
 * - The top-hat filter is also handy as a fast alternative to filtering 16-bit images with the ImageJ
 * built-in "Subtract Background" command with the traditional algorithm (rolling ball, not sliding
 * paraboloid) and produces similar results (especially for large pixel value ranges), but the
 * "Background from minima" and "Background from maxima" filters usually produce better results
 * (smoother background).
 * <p/>
 * Implementation notes:
 * - The plugin handles stack slices in parallel as long as there is enough free memory.
 * This is faster than parallel processing of lines in each image because it also parallelizes the
 * conversion to float.
 * - Creation and destruction of a thread needs about 0.1 msec on a typical year-2011 computer
 * (Intel core i5, Sun Java 1.6.0). This could significantly hamper the performance when processing
 * many slices and creating new threads for each slice (90% of CPU time for thread creation/destruction
 * at 50x50-pixel images!). Thus, small images (<0.1 MPixels) are processed in only one thread.
 * <p/>
 * <p/>
 * Performance on Intel i5 2.4 GHz, 1067 MHz DDR3, Java 1.6.0_24 for Mac,
 * 4000*4000 pixel float image (data: 'add noise' on blank image), radius 5*5:
 * mean:          0.27 s
 * maximum:       0.31 s
 * pseudo median: 1.45 s
 * <p/>
 * Code by Michael Schmid
 * 2008-11-21 released
 * 2010-04-16 Parallel threads for multi-CPU machines, proper handling of NaN pixels
 */

public class Fast_Filters
{

	private final static String[] TYPES = new String[] {
		"mean", "border-limited mean", "median", "minimum", "maximum", "eliminate maxima", "eliminate minima",
		"background from minima", "background from maxima", "background from median"
	};

	private final static int MEAN = 0, BORDER_LIMITED_MEAN = 1, MEDIAN = 2, MIN = 3, MAX = 4;//"elementary" filter types
	//ELIM_MAX=5,ELIM_MIN=6,BACK_MIN=7, BACK_MAX=8, BACK_MEDIAN=9;                  //"composite filters" from > 1 elementary types
	private final static int[][] taskLists = new int[][] { //what to do for the filter types
		{ MEAN },
		{ BORDER_LIMITED_MEAN },
		{ MEDIAN },
		{ MIN },
		{ MAX },
		{ MIN, MAX },
		{ MAX, MIN },
		{ MIN, MAX, BORDER_LIMITED_MEAN },
		{ MAX, MIN, BORDER_LIMITED_MEAN },
		{ MEDIAN, BORDER_LIMITED_MEAN }
	};
	private final static String[] PREPROCESSES = new String[] {
		"none", "smooth", "median"//preprocessing types; 1 and 2 are the same as filter types 1 and 2 above
	};
	// F i l t e r   p a r a m e t e r s
	// Note that this makes it impossible to run the filter in parallel threads with different filterSelectedColors parameters!
	private static int type = MEAN;// Filter type
	private static int xRadius = 5;// The kernel radius in x direction
	private static int yRadius = 5;// The kernel radius in x direction
	private static boolean linkXY = true;// Whether to use the same radius in x&y
	private static int preProcess = 0;// Preprocessing type
	private static boolean subtract = false;// Whether output should be the original minus filtered
	private static double[] offset = new double[] { // When subtracting, this will be added to the result.
		128, 32768, 0, 128, 128// Array for image types GRAY8, GRAY16, GRAY32, COLOR_256, COLOR_RGB
	};
	// F u r t h e r   c l a s s   v a r i a b l e s
	private int impType;// type of ImagePlus (GRAY_8, etc...)
	private int nPasses = 1;// The number of passes (color channels * stack slices)
	private int pass;// Current pass
	// Multithreading-related
	private int maxThreads = Runtime.getRuntime().availableProcessors();// number of threads for filtering


	/**
	 * This method is called by ImageJ to set the number of calls to run(ip)
	 * corresponding to 100% of the progress bar
	 */
	public void setNPasses( int nPasses )
	{
		if ( xRadius > 0 && yRadius > 0 ) nPasses *= 2;
		this.nPasses = nPasses * taskLists[ type ].length;
		pass = 0;
	}

	// Mean filter of a line.
	// When trying to access out-of-border pixels it replaces them with the nearest border pixel.
	// (this is the usual behavior of ImageJ filters)
	// radius: Kernel width is 2*radius+1
	// cache: Holds input data for one line, i.e., for one image row or column
	// pixels: Image data are written to this point
	// writeFrom: Index of first point of the line that should be written.
	// writeTo: Last point + 1 of the line that should be written. Data will be read from 'cache'
	// also outside the 'writeFrom', 'writeTo' range.
	// pixel0: Index of the first value to be written in 'pixels'. Corresponds to index 'writeFrom' in 'cache'.
	// pointInc: increment of index in 'pixels' from one point to the next (1 for image rows, width for columns).
	private static void lineMean( int radius, float[] cache, float[] pixels, int writeFrom, int writeTo,
	                              int pixel0, int pointInc )
	{
		double sum = 0;
		double factor = 1. / ( 1 + 2 * radius );
		int length = cache.length;
		float first = cache[ 0 ];
		float last = cache[ length - 1 ];
		int sumFrom = writeFrom - radius;
		int sumTo = writeFrom + radius;
		if ( sumFrom < 0 )
		{
			sum = -sumFrom * first;
			sumFrom = 0;
		}
		if ( sumTo > length )
		{
			sum += ( sumTo - length ) * last;
			sumTo = length;
		}
		for ( int i = sumFrom; i < sumTo; i++ )
			sum += cache[ i ];
		for ( int i = writeFrom, iMinus = i - radius, iPlus = i + radius, p = pixel0;
		      i < writeTo; i++, iMinus++, iPlus++, p += pointInc )
		{
			sum += ( iPlus < length ) ? cache[ iPlus ] : last;
			if ( Double.isNaN( sum ) )
				sum = nanAwareSum( radius, cache, i, pixels, p );//writes pixel, returns NaN unless no NaNs
			else
				pixels[ p ] = ( float ) ( sum * factor );
			sum -= ( iMinus >= 0 ) ? cache[ iMinus ] : first;
		}
	}

	private static double nanAwareSum( int radius, float[] cache, int cachePos, float[] pixels, int p )
	{
		int n = 0;
		double sum = 0;
		for ( int i = cachePos - radius; i <= cachePos + radius; i++ )
		{
			float v = cache[ i < 0 ? 0 : ( i >= cache.length ? cache.length - 1 : i ) ];
			if ( v == v )
			{//!isNaN(v)
				sum += v;
				n++;
			}
		}
		if ( n > 0 ) pixels[ p ] = ( float ) ( sum / n );
		return n == 2 * radius + 1 ? sum : Double.NaN;
	}

	// Mean filter of a line; at the image borders it does not give extra weight to the border pixels.
	private static void lineBorderLimitedMean( int radius, float[] cache, float[] pixels, int writeFrom, int writeTo,
	                                           int pixel0, int pointInc )
	{
		double sum = 0;
		int length = cache.length;
		int sumFrom = ( writeFrom - radius > 0 ) ? writeFrom - radius : 0;
		int sumTo = ( writeFrom + radius < length ) ? writeFrom + radius : length;
		int kSize = sumTo - sumFrom;
		for ( int i = sumFrom; i < sumTo; i++ )
			sum += cache[ i ];
		for ( int i = writeFrom, iMinus = i - radius, iPlus = i + radius, p = pixel0;
		      i < writeTo; i++, iMinus++, iPlus++, p += pointInc )
		{
			if ( iPlus < length )
			{
				sum += cache[ iPlus ];
				kSize++;
			}
			if ( Double.isNaN( sum ) )
				sum = nanAwareBLMean( radius, cache, i, kSize, pixels, p );
			else
				pixels[ p ] = ( ( float ) sum ) / kSize;
			if ( iMinus >= 0 )
			{
				sum -= cache[ iMinus ];
				kSize--;
			}
		}
	}

	private static double nanAwareBLMean( int radius, float[] cache, int cachePos, int kSize, float[] pixels, int p )
	{
		int n = 0;
		double sum = 0;
		for ( int i = Math.max( cachePos - radius, 0 ); i <= Math.min( cachePos + radius, cache.length - 1 ); i++ )
		{
			float v = cache[ i ];
			if ( v == v )
			{//!isNaN(v)
				sum += v;
				n++;
			}
		}
		if ( n > 0 ) pixels[ p ] = ( float ) ( sum / n );
		return n == kSize ? sum : Float.NaN;
	}

	// Median filter of a line; at the image borders it does not give extra weight to the border pixels.
	private static void lineMedian( int radius, float[] cache, float[] pixels, int writeFrom, int writeTo,
	                                int pixel0, int pointInc, float[] vHi, float[] vLo )
	{
		int length = cache.length;
		float median = Float.isNaN( cache[ writeFrom ] ) ? 0 : cache[ writeFrom ];//a first guess
		for ( int i = writeFrom, iMinus = i - radius, iPlus = i + radius, p = pixel0;
		      i < writeTo; i++, iMinus++, iPlus++, p += pointInc )
		{
			int nHi = 0, nLo = 0;
			int iStart = ( iMinus >= 0 ) ? iMinus : 0;
			int iStop = ( iPlus < length ) ? iPlus : length - 1;
			int nEqual = 0;
			for ( int iRead = iStart; iRead <= iStop; iRead++ )
			{
				float v = cache[ iRead ];
				if ( v > median ) vHi[ nHi++ ] = v;
				else if ( v < median ) vLo[ nLo++ ] = v;
				else if ( v == v ) nEqual++;//if (!isNaN(v))
			}
			int nPoints = nHi + nLo + nEqual;
			if ( nPoints == 0 )
			{
				pixels[ p ] = Float.NaN;
			}
			else
			{
				if ( nPoints % 2 == 0 )
				{//avoid an even number of points: in case of doubt, leave it closer to original value
					float v = cache[ i ];
					if ( v > median ) vHi[ nHi++ ] = v;
					else if ( v < median ) vLo[ nLo++ ] = v;
				}
				int half = nPoints / 2;//>>1; //(nHi+nLo)/2, but faster
				if ( nHi > half )
					median = 0; //RankFilters.findNthLowestNumber( vHi, nHi, nHi - half - 1 );
				else if ( nLo > half )
					median = 0;//RankFilters.findNthLowestNumber( vLo, nLo, half );
				pixels[ p ] = median;
			}
		}
	}

	// Algorithm for finding maxima within a range of the input array 'cache':
	// - When going to the next pixel, if the new pixel is > than the old maximum take it.
	// - Get the maximum over the full range only if the pixel that is not in the range any more ('out')
	//   could be the one that has caused the current value of the maximum.
	// - It is faster to start at a border than to end there: In the beginning, we need not care about
	//   pixels that get out of the range. Thus the algorithm starts from both borders; whenever a full
	//   determination of the maximum over the full range becomes necessary, the algorithm tries to avoid
	//   this by continuing at the other end.
	private static void lineMax( int radius, float sign, float[] cache, float[] pixels, int writeFrom, int writeTo,
	                             int pixel0, int pointInc )
	{
		int length = cache.length;
		int pUp = pixel0;
		int pDn = pixel0 + ( writeTo - writeFrom - 1 ) * pointInc;
		float maxUp = -Float.MAX_VALUE;
		float maxDn = -Float.MAX_VALUE;
		int iInUp = writeFrom + radius;//new in the range that we have to find the max of
		int iOutUp = writeFrom - radius - 1;//not in the range any more
		int iInDn = writeTo - radius - 1;
		int iOutDn = writeTo + radius;
		boolean firstUp = true;
		boolean firstDn = true;

		while ( pUp <= pDn )
		{
			boolean switchDirection = false;
			for ( ; pUp <= pDn; pUp += pointInc, iInUp++, iOutUp++ )
			{
				float oldmax = maxUp;
				if ( iInUp < length )
				{
					if ( firstUp || Float.isNaN( oldmax ) || Float.isNaN( cache[ iInUp ] ) )
					{
						maxUp = nanAwareMax( iOutUp + 1, iInUp, cache, sign, pixels, pUp );//find max one-by-one with possible NaNs
						firstUp = false;
						continue;
					}
					if ( maxUp <= cache[ iInUp ] )
					{
						maxUp = cache[ iInUp ];
						pixels[ pUp ] = maxUp * sign;
						continue;
					}
				}
				if ( iOutUp >= 0 && cache[ iOutUp ] == oldmax )
				{//full one-by-one determination
					if ( switchDirection )
						break;
					switchDirection = true;
					int maxFrom = ( iOutUp >= -1 ) ? iOutUp + 1 : 0;
					int maxTo = ( iInUp < length ) ? iInUp : length - 1;
					maxUp = cache[ maxFrom ];
					for ( int i = maxFrom + 1; i <= maxTo; i++ )
						if ( maxUp < cache[ i ] ) maxUp = cache[ i ];
				}
				pixels[ pUp ] = maxUp * sign;
			}
			for ( ; pUp <= pDn; pDn -= pointInc, iInDn--, iOutDn-- )
			{
				switchDirection = false;
				float oldmax = maxDn;
				if ( iInDn >= 0 )
				{
					if ( firstDn || Float.isNaN( oldmax ) || Float.isNaN( cache[ iInDn ] ) )
					{
						maxDn = nanAwareMax( iInDn, iOutDn - 1, cache, sign, pixels, pDn );
						firstDn = false;
						continue;
					}
					if ( maxDn < cache[ iInDn ] )
					{
						maxDn = cache[ iInDn ];
						pixels[ pDn ] = maxDn * sign;
						continue;
					}
				}
				if ( iOutDn < length && cache[ iOutDn ] == oldmax )
				{//full one-by-one determination
					if ( switchDirection )
						break;
					switchDirection = true;
					int maxFrom = ( iOutDn <= length ) ? iOutDn - 1 : length - 1;
					int maxTo = ( iInDn > 0 ) ? iInDn : 0;
					maxDn = cache[ maxFrom ];
					for ( int i = maxFrom - 1; i >= maxTo; i-- )
						if ( maxDn < cache[ i ] ) maxDn = cache[ i ];
				}
				pixels[ pDn ] = maxDn * sign;
			}
		}
	}

	private static float nanAwareMax( int from, int to, float[] cache, float sign, float[] pixels, int p )
	{
		float max = -Float.MAX_VALUE;
		boolean anyGood = false;
		boolean anyNaN = false;
		for ( int i = Math.max( from, 0 ); i <= Math.min( to, cache.length - 1 ); i++ )
		{
			float v = cache[ i ];
			if ( Float.isNaN( v ) )
			{
				anyNaN = true;
			}
			else
			{
				if ( max < cache[ i ] ) max = cache[ i ];
				anyGood = true;
			}
		}
		if ( anyGood ) pixels[ p ] = max * sign;
		return anyNaN ? Float.NaN : max;
	}

}
