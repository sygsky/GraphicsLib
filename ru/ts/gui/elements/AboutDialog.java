package ru.ts.gui.elements;

import static ru.ts.common.misc.Text.sout;
import ru.ts.common.misc.Text;
import ru.ts.gui.GUIFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Date;

public class AboutDialog extends JDialog
	implements Runnable
{
	private final int OFFSET_EXIT_MSG_FROM_BOTTOM = 10;// offset of exit message from the bottom of the window in pixels

	private Thread t;

	private JFrame parent;

	//	private int drawIndex = 0;

	private String strArray[] = {
		"JSplash V1.10",
		"Programming by John Boy, graphics by Sygsky.",
		"Beta Testing by Sygsky from JHC.",
		"Long live Ivan Susanin. If he was existed, he won!",
		"Also best wishes to the Father Frost and",
		"with a New 2009 Year!!!"
	};

	private Color color;

	private Image offScreenImage;

	// main background image
	private Image backgroundImage;

	private Graphics offScreenGraphics;

	private Font _font;

	private JLabel splashImageLabel;

	private Dimension screenSize;

	//private Image image;
	private int vertSpace;// distance between lines
	private float transparency;
	private int transpLoopCnt;// transparency loop count
	private String m_exitStr;

	/**
	 * surprize small image
	 */
	private Image m_simg;
	private int drawIndex;
	private int drawSteps;
	private int moveCnt;
	private static final int FONT_SIZE = 14;
	private boolean stopped;
	private boolean finished;
	//private String m_specs;

	public AboutDialog( String title, String exit, JFrame frame, String[] lines, String bgImgPath, ImageIcon aboutIco, String specs, GUIFactory gf )
	{
		super( (Frame)null,  Text.isEmpty( title ) ? "About this program" : title );
		_init( frame, lines, bgImgPath, aboutIco );
		m_exitStr = Text.isEmpty( exit ) ? "Press/click any key/button to exit" : exit;
		m_simg = null;
		if ( Text.notEmpty( specs ) && ( gf != null ) )
		{
			try
			{
				SpecDays sd = new SpecDays( specs, '|' );
				int index = sd.specDayIndexFor( new Date() );
				//Text.sout( "Spec Day index is " + index );
				if ( index >= 0 )
				{
					// some spec day found
					String iconPath = sd.picture( index );
					m_simg = gf.load_image( iconPath );
				}
			}
			catch ( Exception e )
			{
			}
		}
	}

	//private int lineTranspOffset = 30; // each consequent line transparency difference offset

	private void _init( JFrame parent, String[] lines, String imgPath, ImageIcon ico )
	{
		this.setResizable( false );
		backgroundImage = Toolkit.getDefaultToolkit().getImage( getClass().getResource( imgPath ) );
		if ( ico != null )
		{
			setIconImage( ico.getImage() );
		}
		int w = backgroundImage.getWidth( null );
		int h = backgroundImage.getHeight( null );

		if ( w > 0 && h > 0)
		{
			BufferedImage bi = new BufferedImage( w, h, BufferedImage.TYPE_INT_RGB );
			bi.getGraphics().drawImage( backgroundImage, 0, 0, null );
			backgroundImage = bi;
		}

		// sout("main image w = " + w + ", h = " + h);

		splashImageLabel = new JLabel( new ImageIcon( backgroundImage ) );

		getContentPane().add( splashImageLabel, BorderLayout.CENTER );
		pack();

		screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		setLocation( ( ( screenSize.width - getSize().width ) / 2 ),
			( ( screenSize.height - getSize().height ) / 2 ) );

		splashImageLabel.setBorder( BorderFactory.createLineBorder( new Color( 75, 75, 75 ) ) );

		this.parent = parent;
		addMouseListener( new MouseAdapter()
		{
			public void mouseReleased( MouseEvent e )
			{
				setVisible( false );
				dispose();
			}
		} );

		addKeyListener( new KeyAdapter()
		{
			public void keyPressed( KeyEvent e )
			{
				/* Exit on any key */
				setVisible( false );
				dispose();
			}
		} );

		/*		addFocusListener( new java.awt.event.FocusListener()
				{
					public void focusGained( FocusEvent e )
					{
						System.out.println( "Focus gained" );
					}

					public void focusLost( FocusEvent e )
					{
						System.out.println( "Focus lost" );
					}
				} );*/

		color = Color.white;
		vertSpace = FONT_SIZE * 3 / 2;

		offScreenImage = createImage( this.getSize().width, this.getSize().height );
		offScreenGraphics = offScreenImage.getGraphics();

		_font = new Font( /*Font.DIALOG*/ Font.MONOSPACED, Font.PLAIN, FONT_SIZE );
		if ( ( lines != null ) && ( lines.length > 0 ) )
		{
			strArray = lines.clone();
		}
		t = new Thread( this );
	}

	public AboutDialog( JFrame parent, ru.ts.gui.elements.IPropertyResourceBundle rbundle, String imgPath )
	{
		super( parent, "About this program" );
		String lines[] = null;
		if ( rbundle != null )
		{
			ArrayList<String> list = new ArrayList<String>();
			/* try to load info from an user text resource */
			for ( int i = 1; ; i++ )
			{
				String key = "About.Line" + i;
				if ( !rbundle.hasKey( key ) )
				{
					break;
				}
				String line = rbundle.getString( key );
				list.add( line );
			}
			if ( list.size() > 0 )
			{
				lines = new String[list.size()];
				for ( int i = 0; i < lines.length; i++ )
				{
					lines[ i ] = list.get( i );
				}
			}
		}
		_init( parent, lines, imgPath, null );
		m_exitStr = "Press/click any key/button to exit";
	}

	public AboutDialog( JFrame parent, String[] lines, String imgPath )
	{
		super( parent, "About this program" );
		_init( parent, lines, imgPath, null );
		m_exitStr = "Press/click any key/button to exit";
	}

	public void setVisible( boolean flag )
	{
		super.setVisible( flag );
		if ( flag == true )
		{
			if ( !t.isAlive() )
			{
				t.start();
			}
			parent.setEnabled( false );
			drawIndex = 0;
		}
		else
		{
			parent.setEnabled( true );
			parent.toFront();
		}
	}

	//	boolean debugDraw = false;

	public void run()
	{
		//stopped = false;
		/*
			int color1 = Color.blue.getRGB();
			int color2 = Color.white.getRGB();
	*/
		final int dist = 100 / strArray.length;// 1 second for 1st line, less and less for follow ones
		//drawSteps = 20; // number of steps in redraw (not needed)
		for ( drawIndex = 0; drawIndex < strArray.length; drawIndex++ )
		{
			//			debugDraw = false;
			transparency = 0.0f;
			//			transpLoopCnt = 50; // number of transparency changing steps
			transpLoopCnt = dist * strArray.length - drawIndex * dist;// number of transparency changing steps
			float step = 1.0f / transpLoopCnt;// transparency change for 1 step
			// change transparency from 0.0f to 1.0f step by step in the same time moving text lines if needed
			for ( moveCnt = 0; moveCnt < transpLoopCnt; moveCnt++ )
			{
				transparency += step;
				if ( transparency > 1.0f )
				{
					transparency = 1.0f;
				}
				repaint();
				try
				{
					t.sleep( 10 );
				}
				catch ( InterruptedException e )
				{
				}
			}
			repaint();// signal to fix current line into bg image
			// wait 300 ms . What we waits here?
			for ( int j = 0; j < 10; j++ )
			{
				try
				{
					t.sleep( 30 );
				}
				catch ( InterruptedException e )
				{
				}
			}
		}
		//stopped = true;
		repaint();
	}

	public void update( Graphics g )
	{
		paint( g );
	}

	int lastPos = -1;

	public void paint( Graphics g )
	{
		if ( !t.isAlive() )
		{
			if ( finished )
			{
				g.drawImage( offScreenImage, 0, 0, getSize().width - 1, getSize().height - 1, this );
				return;
			}
			// but result is still stored
			finished = true;
		}
		final int strArrLen = strArray.length;

		Graphics2D g2d = (Graphics2D) offScreenGraphics;
		Composite comp = AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1.0f );
		g2d.setComposite( AlphaComposite.SrcOver );
		offScreenGraphics.drawImage( backgroundImage, 0, 0, this );// re-initiate canvas
		if ( m_simg != null )// put special congratulation image if any
		{
			int iw = m_simg.getWidth( null );
			int ih = m_simg.getHeight( null );
			int w = backgroundImage.getWidth( null );
			int h = backgroundImage.getHeight( null );
//			comp = AlphaComposite.getInstance( AlphaComposite.SRC, 1.0F);
			g2d.setComposite( AlphaComposite.SrcOver );
			offScreenGraphics.drawImage( m_simg, w - iw - 10, h - ih - 10, null );
		}

		offScreenGraphics.setFont( _font );


		// for each text line do
		// draw text lines
		int lastY = 2 * vertSpace + 10;// start position of 1st line
		//Text.sout("drawIndex = " + drawIndex );

		// print static lines before
		for ( int i = 0; i < drawIndex; i++ )// draw already animated lines in static position
		{
			if ( Text.notEmpty( strArray[ i ] ) )
			{
				offScreenGraphics.setColor( color );
				offScreenGraphics.drawString( strArray[ i ], FONT_SIZE, lastY );
				// Paint title letter
				// offScreenGraphics.setFont( _font.deriveFont( Font.BOLD ) );
				offScreenGraphics.setColor( Color.RED );
				offScreenGraphics.drawString( strArray[ i ].substring( 0, 1 ), FONT_SIZE, lastY );
				// offScreenGraphics.setFont( _font );
			}
			/*			if ( !debugDraw )
					 Text.sout( "  draw str at" + lastY );
	 */
			lastY += vertSpace;
		}

		final int height = offScreenImage.getHeight( null );// about screen height
		// next pos to stop is lastY, we have to move from bottom start pos to this last pos
		final int exitStrY = height - OFFSET_EXIT_MSG_FROM_BOTTOM;
		if ( drawIndex < strArrLen )
		{
			comp = AlphaComposite.getInstance( AlphaComposite.SRC_OVER, transparency );
			g2d.setComposite( comp );
			//g2d.setPaint(Color.red);

			final int startPos = exitStrY - vertSpace;
			final int finishPos = lastY;

			final float step = (float) ( finishPos - startPos ) / (float) ( transpLoopCnt - 1 );

//			int cnt = Math.min( drawIndex, strArrLen - 1 );
			final int lineOff = (int) ( step * (float) moveCnt );
			lastPos = startPos + lineOff;
			if ( lastPos < finishPos )
				lastPos = finishPos;				
			offScreenGraphics.setColor( color );
			offScreenGraphics.drawString( strArray[ drawIndex ], FONT_SIZE,  lastPos );

//			offScreenGraphics.setColor( new Color( 75, 75, 75 ) );
			/*
			 if ( !debugDraw )
						 {
							 Text.sout( String.format( "drawIndex %d, startpos %d, step %d, lastY %d", drawIndex, startPos, step, lastY ) );
							 debugDraw = true;
						 }
				 */

			/*
			 comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0F);
						 g2d.setComposite(comp);
				 */
		}
		offScreenGraphics.setColor( Color.RED );
		offScreenGraphics.drawString( m_exitStr, 200, exitStrY );
		//offScreenGraphics.drawRect( 0, 0, getSize().width - 1, getSize().height - 1 );
		g.drawImage( offScreenImage, 0, 0, getSize().width - 1, height - 1, this );
	}

	/**
	 * calculates colour between two boundary colours ( low and high ) by the fraction
	 * from low to high. Fraction is 0 if set to color1 and is equal 1 when set to color2.
	 * Fraction 0.5 means some,e colour between color1 and color2 :o)
	 * <p/>
	 * While a mathematical "linear interpolation" is used here, this is a
	 * non-linear interpolation in colour perception space.  Fraction is assumed
	 * to be from 0.0 to 1.0, but this is not enforced.  Returns color1 for
	 * fraction = 0.0 and color2 for fraction = 1.0.
	 *
	 * @param fraction how new colour is different from color1. 1-fraction stands
	 *                 for differences from color2.
	 * @param color1   low boundary colour ( left on a spectral line)
	 * @param color2   high boundary colour ( right on a spectral line )
	 * @return intermediate colour between color1 and colour 2
	 */
	public static int ColorInterpolate( double fraction, int color1, int color2 )
	{
		if ( fraction <= 0.0 )
		{
			return color1;
		}
		if ( fraction >= 1.0 )
		{
			return color2;
		}

		int B1 = color1 & 0x00FF;
		int G1 = ( color1 >>> 8 ) & 0x00FF;
		int R1 = ( color1 >>> 16 ) & 0x00FF;

		int B2 = color2 & 0x00FF;
		int G2 = ( color2 >>> 8 ) & 0x00FF;
		int R2 = ( color2 >>> 16 ) & 0x00FF;

		double complement = 1.0 - fraction;
		return RGB2Int( (int) Math.round( complement * R1 + fraction * R2 ),
			(int) Math.round( complement * G1 + fraction * G2 ),
			(int) Math.round( complement * B1 + fraction * B2 ) );
	}

	/**
	 * constructs Java style 24 bits colour from its R,G,B components
	 *
	 * @param R int Red component
	 * @param G int Green component
	 * @param B int Blue component
	 * @return integer colour representation where
	 *         R has mask 0x00FF0000
	 *         G has mask 0x0000FF00 and
	 *         B has mask 0x000000FF
	 */
	public static int RGB2Int( int R, int G, int B )
	{
		return ( B & 0x00FF ) | ( ( G << 8 ) & 0x00FF00 ) | ( ( R << 16 ) & 0x00FF0000 );
	}

}
