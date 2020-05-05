package ru.ts.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * <pre>
 * Created by IntelliJ IDEA.
 * User: sigolaev_va
 * Date: 24.02.2014
 * Time: 16:14:04
 * Original package: ru.ts.panels
 * *
 * To change this template use File | Settings | File Templates.
 * *
 * <pre>
 */
public class ChessBoardPanel extends JPanel
{
	private Color m_dark;
	private Color m_bright;
	private int m_size;

	public ChessBoardPanel()
	{
		this( Color.LIGHT_GRAY, Color.WHITE, 32 );
	}

	public ChessBoardPanel( Color dark, Color bright, int squareSize )
	{
		//super( true );
		m_dark = dark;
		m_bright = bright;
		m_size = squareSize;
	}

	protected void paintComponent( Graphics g )
	{
		super.paintComponent( g );

		drawChessBoard( g, this.getWidth(), this.getHeight(), m_size, m_dark, m_bright );
	}

	public static void drawChessBoard( Graphics g, int w, int h, int size, Color dark, Color bright )
	{
		//Color prevColor = g.getColor();
		Rectangle rect = g.getClipBounds();
		if ( rect == null)
			rect = new Rectangle( 0, 0, w, h );
		if (rect.isEmpty() )
			return;
		//sout( "Window repaint. ClipBouns  " + rect.toString() );
		int x1 = rect.x / size * size;
		int y1 = rect.y / size * size;
		int x2 = ( rect.x + rect.width  + 1) / size * size;
		int y2 = ( rect.y + rect.height  + 1) / size * size;
		boolean startDark = ( (( x1 / size ) + (y1 / size)) & 1 ) == 0;
		for ( int y = y1; y <= y2; y += size )
		{
			boolean drawDarkSquare = startDark;
			for ( int x = x1; x <= x2; x += size )
			{
				g.setColor( drawDarkSquare ? dark : bright );
				g.fillRect( x, y, size, size );
				drawDarkSquare = !drawDarkSquare;
			}
			startDark = !startDark;
		}
	}


}
