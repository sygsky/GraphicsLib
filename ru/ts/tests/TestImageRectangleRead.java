package ru.ts.tests;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.stream.FileImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;

public class TestImageRectangleRead
{
	public static void main( String[] args )
	{
		System.out.println( "+++ TEST rectangle sub-image API +++" );
		if ( args.length == 0 )
			System.out.println( "Expected file paths not found" );

		javax.imageio.ImageReader reader;

		for ( String path : args )
		{
			System.out.println( "Processing file \"" + path + "\"" );
			try
			{
				File file = new File( path );
				FileImageInputStream iis = new FileImageInputStream( file );

				int pntIndex = path.lastIndexOf( '.' );
				if ( pntIndex < 0  || pntIndex >= path.length() )
				{
					System.err.println( "Input path doesn't have known extension, skipping" );
					continue;
				}
				String ext = path.substring( pntIndex + 1 );
				System.out.println( "Extension is \"" +  ext  + "\"" );

				// Find corresponding reader
				Iterator iter = ImageIO.getImageReadersBySuffix( ext );
				reader = null;
				if ( iter.hasNext() )
					reader = (javax.imageio.ImageReader) iter.next();
				else
				{
					System.err.println( "Reader for \""+ext +"\" not found by call to ImageIO.getImageReadersBySuffix( \"" + ext + "\" ), skipping" );
					continue;
				}
				reader.setInput( iis );
				ImageReadParam param = reader.getDefaultReadParam();
				int w = reader.getWidth( 0 );
				int h = reader.getHeight( 0 );
				System.out.println( "Image successfully opened, w=" + w + ", h=" + h );
				// read UL corner
				System.out.println( "Read UL corner and write it to disk" );
				Rectangle ulRect = new Rectangle( 0, 0, w/2, h/2);
				param.setSourceRegion( ulRect );
				BufferedImage bi = reader.read( 0, param );
				final String ulpath = "C:/temp/ULCorner." + ext;
				if ( ImageIO.write( bi, ext, new File( ulpath ) ) )
					System.out.println( "New UL file successfully stored: \"" + ulpath + "\"" );
				else
					System.err.println( "No no appropriate writer was found, file not created" );
				// read LR corner
				System.out.println( "Read LR corner and write it to disk" );
				Rectangle lrRect = new Rectangle( w/2, h/2, w/2, h/2 );
				param.setSourceRegion( lrRect );
				bi = reader.read( 0, param );
				final String lrpath = "C:/temp/LRCorner." + ext;
				if ( ImageIO.write( bi, ext, new File( lrpath ) ) )
					System.out.println( "New LR file successfully stored: \"" + lrpath + "\"" );
				else
					System.err.println( "No no appropriate writer was found, file not created" );
			}
			catch ( Exception e )
			{
				e.printStackTrace();
			}
		}
	}
}
