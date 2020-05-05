package ru.ts.graphics.fullscreen.test;/*
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

/**
 * This test takes a number up to 13 as an argument (assumes 2 by default) and
 * creates a multiple buffer strategy with the number of buffers given. This
 * application enters full-screen mode, if available, and flips back and forth
 * between each buffer (each signified by a different color).
 */

import ru.ts.colors.ContrastingColor;
import ru.ts.colors.ColorHndl;

import java.awt.*;
import java.awt.image.BufferStrategy;

public class MultiBufferTest
{

	private static Color[] COLORS = new Color[]{ Color.red, Color.blue,
					Color.green, Color.white, Color.black, Color.yellow, Color.gray,
					Color.cyan, Color.pink, Color.lightGray, Color.magenta,
					Color.orange, Color.darkGray };

	private static DisplayMode[] BEST_DISPLAY_MODES = new DisplayMode[]{
					new DisplayMode( 1280, 1024, 32, 0 ), new DisplayMode( 1024, 768, 32, 0 ),
					new DisplayMode( 640, 480, 32, 0 ), new DisplayMode( 640, 480, 16, 0 ),
					new DisplayMode( 640, 480, 8, 0 ) };

	Frame mainFrame;

	public MultiBufferTest( int numBuffers, GraphicsDevice device )
	{
		try
		{
			GraphicsConfiguration gc = device.getDefaultConfiguration();
			mainFrame = new Frame( gc );
			mainFrame.setUndecorated( true );
			mainFrame.setIgnoreRepaint( true );
			device.setFullScreenWindow( mainFrame );
			if ( device.isDisplayChangeSupported() )
			{
				chooseBestDisplayMode( device );
			}
			Rectangle bounds = mainFrame.getBounds();
			mainFrame.createBufferStrategy( numBuffers );
			BufferStrategy bufferStrategy = mainFrame.getBufferStrategy();
			for ( float lag = 2000.0f; lag > 0.00000006f; lag = lag / 1.33f )
			{
				for ( int i = 0; i < numBuffers; i++ )
				{
					Graphics g = bufferStrategy.getDrawGraphics();
					final Font fnt = new Font( "Helvetica", Font.BOLD, 20 );
					g.setFont( fnt );
					if ( !bufferStrategy.contentsLost() )
					{
						g.setColor( COLORS[ i ] );
						g.fillRect( 0, 0, bounds.width, bounds.height );
						g.setColor( ContrastingColor.getFor( COLORS[ i ] ) );
						g.drawString( ColorHndl.toString(COLORS[i]), 50, 50 );

						bufferStrategy.show();
						g.dispose();
					}
					try
					{
						Thread.sleep( (int) lag );
					}
					catch ( InterruptedException e )
					{
					}
				}
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			device.setFullScreenWindow( null );
		}
	}

	private static DisplayMode getBestDisplayMode( GraphicsDevice device )
	{
		for ( int x = 0; x < BEST_DISPLAY_MODES.length; x++ )
		{
			DisplayMode[] modes = device.getDisplayModes();
			for ( int i = 0; i < modes.length; i++ )
			{
				if ( modes[ i ].getWidth() == BEST_DISPLAY_MODES[ x ].getWidth()
								&& modes[ i ].getHeight() == BEST_DISPLAY_MODES[ x ]
								.getHeight()
								&& modes[ i ].getBitDepth() == BEST_DISPLAY_MODES[ x ]
								.getBitDepth() )
				{
					return BEST_DISPLAY_MODES[ x ];
				}
			}
		}
		return null;
	}

	public static void chooseBestDisplayMode( GraphicsDevice device )
	{
		DisplayMode best = getBestDisplayMode( device );
		if ( best != null )
		{
			device.setDisplayMode( best );
		}
	}

	public static void main( String[] args )
	{
		try
		{
			int numBuffers = 2;
			if ( args != null && args.length > 0 )
			{
				numBuffers = Integer.parseInt( args[ 0 ] );
				if ( numBuffers < 2 || numBuffers > COLORS.length )
				{
					System.err.println( "Must specify between 2 and "
									+ COLORS.length + " buffers" );
					System.exit( 1 );
				}
			}
			GraphicsEnvironment env = GraphicsEnvironment
							.getLocalGraphicsEnvironment();
			GraphicsDevice device = env.getDefaultScreenDevice();
			MultiBufferTest test = new MultiBufferTest( numBuffers, device );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
		System.exit( 0 );
	}
}
