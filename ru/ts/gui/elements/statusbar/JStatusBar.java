/**
 *</pre>
 * Created on 10.07.2009 16:08:26<br> 
 * by Syg<br> 
 * for project in 'ru.ts.gui'
 *</pre>
 */
package ru.ts.gui.elements.statusbar;

import ru.ts.common.misc.Text;
import ru.ts.gui.intf.IStatusBar;
import ru.ts.gui.intf.ITSPanel;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

import static ru.ts.gui.intf.IStatusBar.ISBPanel.*;

/**
 * Package ru.ts.gui<br> Author 'Syg'<br> Created  10.07.2009  16:08:26<br>
 * Implementation for the {@link IStatusBar}. Sample for realization:
 * <pre>
 * private final String MOUSE_INFO_MASK = "X: 000 000; Y: 000 000";
 * private final String TIMESPAN_TEXT_MASK = "23:59:59";
 * <p/>
 * ...
 * <p/>
 * public static void main(String[] args)
 * // status bar in main():
 * IStatusBar sb = createStatusBar();
 * <p/>
 * content.add( sb.asComponent(), BorderLayout.SOUTH );
 * <p/>
 * ...
 * <p/>
 * private IStatusBar createStatusBar()
 * {
 * IStatusBar sb = new JStatusBar();
 * <p/>
 * // main panel icon
 * sb.getPanel( 0 ).setIcon( guiFactory.load_icon( "Map16x16.gif" ) );
 * sb.getPanel( 0 ).setToolTip( "Имя файла загруженного проекта" );
 * <p/>
 * // scale panel
 * final IStatusBar.ISBPanel sPanel = sb.createNewPanel( "Scale" );
 * sPanel.setTextWidth( "1: XXX XXX XXX XXX" );
 * sPanel.setText( "1: XXX XXX XXX XXX" );
 * sPanel.setIcon( guiFactory.load_icon( "scale_in_circle.gif" ) );
 * sPanel.setTextColor( Color.BLUE );
 * sPanel.setToolTip( "Не реализовано" );
 * sb.appendPanel( sPanel );  // append to the right end
 * <p/>
 * // coordinates of mouse panel
 * final IStatusBar.ISBPanel cPanel;
 * cPanel = sb.createNewPanel( "Coordinates" );
 * cPanel.setText( MOUSE_INFO_MASK );
 * cPanel.setTextWidth( MOUSE_INFO_MASK );
 * cPanel.setTextColor( Color.RED );
 * cPanel.setToolTip( "Координаты курсора мыши" );
 * cPanel.setIcon( guiFactory.load_icon( "mouse.gif" ) );
 * sb.addPanel( cPanel, 1 );
 * <p/>
 * // time panel
 * final IStatusBar.ISBPanel tPanel;
 * tPanel = sb.createNewPanel( "DateTime" );
 * tPanel.setText( TIMESPAN_TEXT_MASK );
 * tPanel.setTextWidth( TIMESPAN_TEXT_MASK );
 * tPanel.setTextColor( Color.BLACK );
 * tPanel.setToolTip( "Время исполнения" );
 * tPanel.setIcon( guiFactory.load_icon( "date_and_time.gif" ) );
 * sb.addPanel( tPanel, 1 );
 * m_spanTimerThread = new SpanTimer( tPanel );
 * m_spanTimerThread.start();
 * <p/>
 * return sb;
 * }
 */
public class JStatusBar extends JPanel implements IStatusBar, MouseListener, MouseMotionListener
{

	private Stack<String> _txtStk;
	ArrayList<JSBPanel> _panels;
	private Color _leftColor;
	private Color _rightColor;
	private boolean _adjusted;
	/**
	 * Font to draw text int he status bar
	 */
	Font _font;

	/**
	 * {@link FontMetrics} of the default one
	 */
	FontMetrics _fontMetrics;


	/**
	 * Insets for the status bar panel
	 */
	private Insets _ins;

	/**
	 * Font height (vertical distance between neighbour text base lines)
	 */
	private int _txtHeight;

	/**
	 * Typical character width in pixels
	 */
	private int _charWidth;


	/**
	 * The width of separator in pixels. It includes 1 free pixel from left 1 free
	 * pixel from right and 2 pixels for the separator itself, that include 2
	 * lines, one gray and one light.
	 * <p/>
	 * Separator width includes 4 elements: 1. vertical empty line before separator
	 * 2. dark vertical line defined in {@link JStatusBar#_leftColor} item 3. light
	 * vertical line defined in {@link JStatusBar#_rightColor} item 4. vertical
	 * empty line after separator
	 */
	static final int SEPARATOR_WIDTH = 4;

	/**
	 * Allowed square icon size value
	 */
	static final int ICON_SIDE_SIZE = 16;

	/**
	 * Default value for the status bar text drawing font
	 */
	static final int FONT_HEIGHT = 12;

	/**
	 * Last status bar width
	 */
	private int _lastWidth;

	public JStatusBar()
	{
		this( new Color( 188, 188, 188 ), new Color( 252, 252, 252 ) );
	}

	public JStatusBar( final Color leftColor, final Color rightColor )
	{
		_leftColor = leftColor;
		_rightColor = rightColor;
		_panels = new ArrayList<JSBPanel>();
		_txtStk = new Stack<String>();

		final ISBPanel pnl = createNewPanel( ANCESTOR_PANEL_NAME );
		pnl.setTextColor( Color.BLACK );
		addPanel( pnl, 0 );
		setPreferredSize( new Dimension( 22, 22 ) );
		//setBorder( BorderFactory.createBevelBorder( BevelBorder.LOWERED ) );
		setBorder( BorderFactory.createEtchedBorder() );

		addMouseListener( this );
		addMouseMotionListener( this );
	}


	/**
	 * Recalculates all panels from the scratch against Graphics object available
	 */
	void reCalcStatusBar()
	{
		if ( _ins == null )
		{
			_ins = this.getBorder().getBorderInsets( this );
		}
		JSBPanel pnl = null;
		Rectangle bounds = this.getBounds();
		int xr = (int) bounds.getMaxX() - _ins.right;
		// recalculate all static panels
		for ( int i = _panels.size() - 1; i > 0; i-- )
		{
			pnl = _panels.get( i );
			pnl._right = xr;
			pnl.setTextWidth( pnl.getTextWidth() );
			xr = pnl._left - SEPARATOR_WIDTH;
		}
		// recalculate first (leftest) panel
		JSBPanel fPanel = _panels.get( 0 );
		fPanel._left = (int) bounds.getMinX() + _ins.left;
		if ( pnl == null )
		{
			fPanel._right = this.getWidth() - _ins.right + 1;
		}
		else
		{
			fPanel._right = pnl._left - SEPARATOR_WIDTH;
		}
		// calculate text width allowed
		int pw = fPanel.getWidth();
		if ( fPanel._icon != null )
		{
			pw -= fPanel._icon.getIconWidth();
		}
		else
		{
			pw -= 1;
		}
		fPanel._charNum = pw / _charWidth;
	}

	/**
	 * The {@link ISBPanel} created by this method, is suitable to add to any
	 * {@link IStatusBar} instance
	 *
	 * @param panelName name for the new panel
	 * @return {@link ISBPanel} instance
	 */
	public ISBPanel createNewPanel( String panelName )
	{
		return new JSBPanel( this, panelName );
	}

	public Component asComponent()
	{
		return this;
	}

	public int getPanelCount()
	{
		return _panels.size();
	}

	/**
	 * Checks the panel index to be in range
	 *
	 * @param index int value for the panel index
	 * @return {@code true} if panel index is valid, else {@code false}
	 */
	private boolean checkIndex( int index )
	{
		return ( index >= 0 ) && ( index < _panels.size() );
	}

	public ISBPanel getPanel( final int index )
	{
		if ( !checkIndex( index ) )
		{
			return null;
		}
		return _panels.get( index );
	}

	public ISBPanel getPanel( final String panelName )
	{
		for ( ISBPanel panel : _panels )
		{
			if ( panel.getName().equals( panelName ) )
			{
				return panel;
			}
		}
		return null;
	}

	public ISBPanel getMainPanel()
	{
		return getPanel( 0 );
	}


	public int getPanelIndex( final String panelName )
	{
		for ( int i = 0; i < _panels.size(); i++ )
		{
			if ( _panels.get( i ).getName().equals( panelName ) )
			{
				return i;
			}
		}
		return -1;
	}

	public int addPanel( final ISBPanel panel, int index )
	{
		if ( ( index < 1 ) && ( _panels.size() > 0 ) )
		{
			// can't insert before first panel
			index = 1;
		}
		else if ( index >= _panels.size() )
		{
			index = _panels.size();
		}
		_panels.add( index, (JSBPanel) panel );
		// update all follow indexes
		for ( int i = index; i < _panels.size(); i++ )
		{
			_panels.get( i )._index = i;
		}
		// emulate text width change to recalculate the panels width
		panel.setTextWidth( panel.getTextWidth() );
		if ( panel instanceof ITSPanel )// then it has a timer and need to start it
		{
			( (ITSPanel) panel ).start();
		}
		repaint();
		return index;
	}

	public int appendPanel( final ISBPanel panel )
	{
		return addPanel( panel, Integer.MAX_VALUE );
	}

	public boolean removePanel( final int index )
	{
		if ( checkIndex( index ) && ( index != 0 ) )
		{
			_panels.remove( index );
			repaint();
			return true;
		}
		return false;
	}

	public boolean removePanel( final String panelName )
	{
		int index = getPanelIndex( panelName );
		if ( index > 0 )
		{
			return removePanel( index );
		}
		return false;
	}


	public void setSeparatorColors( final Color leftColor,
	                                final Color rightColor )
	{
		_leftColor = leftColor;
		_rightColor = rightColor;
	}

	public Color getSeparatorLeftColor()
	{
		return _leftColor;
	}

	public Color getSeparatorRightColor()
	{
		return _rightColor;
	}

	public void setText( final String text )
	{
		_panels.get( 0 ).setText( text );
	}

	@Override
	protected void processMouseMotionEvent( final MouseEvent e )
	{
		super.processMouseMotionEvent( e );
		int x = e.getX();
		ISBPanel panel = findPanel( x );
		if ( panel != null )
		{
			// panel is found, translate status bar coordinates to panel ones
			int y = e.getY();
			MouseEvent me = new MouseEvent( (Component) e.getSource(), e.getID(),
				e.getWhen(), e.getModifiersEx(), x - panel.getLeftBound(), y,
				e.getClickCount(), e.isPopupTrigger(), e.getButton() );
			panel.processMouseMotionEvent( me );
			if ( me.isConsumed() )
			{
				e.consume();
			}
		}
	}

	@Override
	protected void processMouseEvent( final MouseEvent e )
	{
		super.processMouseEvent( e );
		if ( e.isConsumed() )
		{
			return;
		}
		int x = e.getX();
		ISBPanel panel = findPanel( x );
		if ( panel == null )
		{
			return;
		}
		// panel is found
		MouseEvent me = new MouseEvent( (Component) e.getSource(), e.getID(),
			e.getWhen(), e.getModifiersEx(), x - panel.getLeftBound(), e.getY(),
			e.getClickCount(), e.isPopupTrigger(), e.getButton() );
		panel.processMouseEvent( me );
		if ( me.isConsumed() )
		{
			e.consume();
		}
	}


	public void mouseClicked( final MouseEvent e )
	{
	}

	public void mousePressed( final MouseEvent e )
	{
	}

	public void mouseReleased( final MouseEvent e )
	{
	}

	public void mouseEntered( final MouseEvent e )
	{
	}

	public void mouseExited( final MouseEvent e )
	{
		// restore previous value for the main panel
		if ( _txtStk.isEmpty() )
		{
			return;
		}
		String str = _txtStk.pop();
		setText( str );// restore mail panel text
	}

	public void mouseDragged( final MouseEvent e )
	{
		mouseMoved( e );
	}

	public void mouseMoved( final MouseEvent e )
	{
		// 1. find the component for a tooltip
		int x = e.getX();
		JSBPanel panel = findPanel( x );
		if ( panel == null )// panel no found
		{
			return;
		}
		if ( _txtStk.isEmpty() )// save the previous text
		{
			_txtStk.push( _panels.get( 0 ).getText() );
		}
		String text = panel.getToolTip();
		if ( Text.isEmpty( text ) )
		{
			text = "Info absent";
		}
		setText( text );
	}

	/**
	 * Finds panel by x coordinate (usually from mouse)
	 *
	 * @param x int value with x coordinate
	 * @return {@link JSBPanel} containig designated x or <code>null</code> if no
	 *         panel found
	 */
	private JSBPanel findPanel( int x )
	{
		for ( JSBPanel panel : _panels )
		{
			// is mouse righter then this panel ?
			if ( x > panel._right )
			{
				continue;
			}
			// is mouse lefter then this panel ?
			if ( x < panel._left )
			{
				break;
			}
			return panel;
		}
		return null;
	}

	@Override
	protected void paintComponent( final Graphics g )
	{
		super.paintComponent( g );
		//Text.sout( "JStatusBar redraw, rect {0}", g.getClipBounds() );
		Rectangle bounds = g.getClipBounds();
		//Text.sout( "paintComponent: bounds are {0}", bounds );
		int pntLeft = (int) bounds.getMinX();// min x to repaint
		int pntRight = (int) bounds.getMaxX();// max x to repaint
		Color bgColor = this.getBackground();

		// TODO draw gradient on background in the future

		// clear the whole status bar area
		int h = this.getHeight();
		g.setColor( bgColor );
		g.fillRect( 0, 0, this.getWidth() - 1, h - 1 );

		if ( _font == null )
		{
			_font = new Font( "Monospaced", Font.PLAIN, FONT_HEIGHT );
			_fontMetrics = g.getFontMetrics( _font );
		}
		g.setFont( _font );

		if ( ( _lastWidth != this.getWidth() ) || ( !_adjusted ) )
		{
			FontMetrics fm = g.getFontMetrics( _font );
			_txtHeight = fm.getHeight() - fm.getDescent() * 2 - 1;
			_charWidth = fm.charWidth( 'W' );
			reCalcStatusBar();
			// remember the Status bar width
			_lastWidth = this.getWidth();
			_adjusted = true;
		}
		int top = _ins.top;
		int bottom = _ins.bottom;
		// draw all panels one by one
		for ( JSBPanel panel : _panels )
		{
			int left = panel._left;
			if ( pntRight < left )
			{
				/*
												if (panel._index <= 1 )
													Text.sout("Рисовка остановлена до начала Panel 1");
								*/
				break;// lefter than this panel
			}
			int right = panel._right;
			if ( pntLeft > right )// out of remaining panels
			{
				continue;
			}
			if ( panel.m_progress != 0.0f )
			{
				// draw progress bar
				panel.m_progressWidth = calcProgressWidth( panel );
				if ( panel.m_progressWidth > 0 )
				{
					g.setColor( panel.getProgressColor() );
					g.fillRect( top, bottom, panel.m_progressWidth, h );
					//Text.sout(String.format("JStatusBar: progress %f in %d pix",panel.m_progress, progressWidth ));
				}
			}
			switch ( panel._index )
			{
				case 0:
					break;
				default:
					// draw separator
					// position to the first (dark) separator line
					int x = left - SEPARATOR_WIDTH + 1;
					g.setColor( _leftColor );
					int y1 = top + 1;
					int y2 = h - bottom - 1;
					g.drawLine( left, y1, left, y2 );
					left++;
					g.setColor( _rightColor );
					g.drawLine( left, y1, left, y2 );
					left += 2;
			}
			// draw [icon and] text
			if ( panel._icon != null )
			{
				// draw the icon centered on Y
				panel._icon.paintIcon( JStatusBar.this, g, left, ( h - ICON_SIDE_SIZE ) / 2 );
				// skip icon area and position to the text 1st char
				left += ICON_SIDE_SIZE + 1;
			}

			// calculate the text to output
			String txt = panel._text == null ? "" : panel._text;
			int txtDiff = panel._charNum - txt.length();
			if ( txtDiff <= 0 )
			{
				// text is longer than reserved space, truncate it to the limit
				txt = txt.substring( 0, panel._charNum );
			}
			else
			{
				// text is shorter than space reserved so align it according to
				// the user setting
				switch ( panel.getAlign() )
				{
					case ALIGN_LEFT:
						// do nothing for this case
						break;
					case ALIGN_CENTER:
						txt = Text.pattern( ' ', txtDiff / 2 ).intern() + txt;
						break;
					case ALIGN_RIGHT:
						txt = Text.pattern( ' ', txtDiff ).intern() + txt;
						break;
				}
			}
			g.setColor( panel._textColor );
			final int y = h - ( ( h - _txtHeight ) >> 1 );
			g.drawString( txt, left, y );
		}// end of -> for ( JSBPanel panel : _panels )
	}

	static int calcProgressWidth( JSBPanel panel )
	{
		if ( panel.m_progress == 0.0 )
			return 0;
		int progressStartX = panel.getLeftBound() + 1;
		final Icon icon = panel.getIcon();
		if ( icon != null )
			progressStartX += icon.getIconWidth();
		int progressStopX = panel.getRightBound();
		return (int) ( (float) ( progressStopX - progressStartX + 1 ) * panel.m_progress );
	}


	/**
	 * Creates new panel with time span display, that is HH:MM:SS text modified
	 * each second
	 *
	 * @param sb    {@link JStatusBar} instance to accept the panel
	 * @param name  String with a name for the panel
	 * @param icon  {@link Icon} with a panel icon
	 * @param start {@link Date} instance with start time. If <code>null</code>
	 *              current time used
	 * @return new {@link ISBPanel} instance with predefined time span display
	 *         functionality
	 */
	public static ITSPanel createTimeSpanPanel( JStatusBar sb, String name, Icon icon, Date start )
	{
		return new TimeSpanPanel( sb, name, icon, start );
	}
}
