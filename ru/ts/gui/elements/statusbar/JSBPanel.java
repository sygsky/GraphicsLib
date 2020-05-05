package ru.ts.gui.elements.statusbar;

import ru.ts.common.misc.Text;
import ru.ts.gui.intf.IStatusBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class JSBPanel implements IStatusBar.ISBPanel
{
	/**
	 * Unique among this status bar name of the panel
	 */
	private String _name = null;

	/**
	 * Index of this panel onto the status bar. range from 0 to {@link
	 * ru.ts.gui.intf.IStatusBar#getPanelCount()} - 1
	 */
	int _index;

	int _charNum;

	String _text;

	String _toolTipText;

	Icon _icon;

	/**
	 * Left boundary of the panel, not includes separator
	 */
	int _left;

	/**
	 * Right boundary of the panel, before status bar end or next separator
	 */
	int _right;

	/**
	 * Color of panel text
	 */
	Color _textColor;

	private int _align;
	private JStatusBar m_sb;
	private Color m_progColor;
	private static final Color DEFAULT_PROGRESS_COLOR = new Color( 118, 237, 254 );
	private static final Color DEFAULT_TEXT_COLOR = Color.BLACK;
	float m_progress;
//	private int m_progressLength;
	public int m_progressWidth;

	JSBPanel( JStatusBar sb, final String panelName )
	{
		if ( sb == null )
		{
			throw new NullPointerException( "JSBPanel: JStatusBar is null" );
		}
		m_sb = sb;
		_index = -1;// not inserted
		_name = panelName;
		_charNum = 1;
		_align = IStatusBar.ISBPanel.ALIGN_CENTER;
		_textColor = Color.BLACK;
		m_progColor = DEFAULT_PROGRESS_COLOR;
		m_progress = 0.0f;
	}

	JSBPanel()
	{
	}

	public int getAlign()
	{
		return _align;
	}

	public void setAlign( final int align )
	{
		switch ( align )
		{
			case IStatusBar.ISBPanel.ALIGN_LEFT:
			case IStatusBar.ISBPanel.ALIGN_CENTER:
			case IStatusBar.ISBPanel.ALIGN_RIGHT:
				_align = align;
				break;
		}
	}

	public String getName()
	{
		return _name;
	}

	public int getTextWidth()
	{
		return _charNum;
	}

	public void setTextWidth( int newCharNum )
	{
		if ( newCharNum < 1 )
		{
			newCharNum = 1;
		}
		_charNum = newCharNum;
		if ( _index == 0 )// if inserted first panel
		{
			_left = 1;
			if ( m_sb._panels.size() == 1 )
			{
				_right = m_sb.getWidth() - 1;
			}
			else
			{
				_right =
					m_sb.getPanel( 1 ).getLeftBound() - JStatusBar.SEPARATOR_WIDTH - 1;
			}
		}
		else
		{
			Graphics g = m_sb.getGraphics();
			if ( g == null )
			{
				return;
			}
			// TODO ensure monospace font is known before calculations
			// get whole text width without any pixel before and after
			if ( m_sb._fontMetrics == null || m_sb._font == null )
			{
				m_sb._font = new Font( "Monospaced", Font.PLAIN, JStatusBar.FONT_HEIGHT );
				m_sb._fontMetrics = g.getFontMetrics( m_sb._font );
			}
			int w = m_sb._fontMetrics.stringWidth( Text.pattern( 'W', _charNum ) );
			if ( _icon != null )
			{
				// add icon width + 1 pixel before icon. Icon is drawn directly after
				// left border, text is centered in the remaining space
				w += 1 + _icon.getIconWidth();
			}
			w += 2;// add 1 pixel before and 1 pixel after
			synchronized ( m_sb )
			{
				_left = _right - w - 2;
				reSizePanels();
			}
		}
		_charNum = newCharNum;
	}

	public void setTextWidth( final String sampleText )
	{
		setTextWidth( sampleText.length() );
	}

	/**
	 * Resizes all consequent panels lefter than current one
	 */
	void reSizePanels()
	{
		int left = _left;
		JSBPanel panel;
		for ( int i = _index - 1; i >= 0; i-- )
		{
			panel = (JSBPanel) m_sb.getPanel( i );
			int w = panel.getWidth();
			panel._right = left - JStatusBar.SEPARATOR_WIDTH;
			if ( i > 0 )
			{
				left = panel._left = panel._right - w + 1;
			}
			else
			{
				panel._left = 1;
			}
			panel.update();
		}
	}

	public String getText()
	{
		return _text;
	}

	public void setText( final String newText )
	{
		if ( ( ( _text != null ) && ( newText != null ) ) && _text.equals( newText ) )
			return;
		_text = newText;
		repaint();
	}

	public String getToolTip()
	{
		return _toolTipText;
	}

	public void setToolTip( final String newToolTipText )
	{
		_toolTipText = newToolTipText;
	}

	public Icon getIcon()
	{
		return _icon;
	}

	public void setIcon( final Icon newIcon )
	{
		boolean iconStatusChanged = ( _icon == null ) != ( newIcon == null );
		if ( newIcon != null )
		{
			if ( ( _icon != null ) && ( newIcon.equals( _icon ) ) )
				return;
			if ( ( newIcon.getIconWidth() != JStatusBar.ICON_SIDE_SIZE ) ||
				( newIcon.getIconHeight() != JStatusBar.ICON_SIDE_SIZE ) )
			{
				throw new IllegalArgumentException( "setIcon: the only icon size " +
					JStatusBar.ICON_SIDE_SIZE + "x" + JStatusBar.ICON_SIDE_SIZE + " is allowed, "
					+ newIcon.getIconWidth() + "x" +
					newIcon.getIconHeight() + " detected" );
			}
			iconStatusChanged = _icon != newIcon;
		}
		_icon = newIcon;
		if ( iconStatusChanged )
			reSizePanels();
	}

	void repaint()
	{
		/*
								if ( _index == 1 )
								Text.sout("repaint [x={0},y={1},width={2},height={3}]", _left, 0, getWidth(), JStatusBar.this.getHeight());
				*/
		m_sb.repaint( _left, 0, getWidth(), m_sb.getHeight() );
	}

	public int getLeftBound()
	{
		if ( _index == 0 )
		{
			return 1;
		}
		return _left;
	}

	public int getRightBound()
	{
		return _right;
	}

	public int getWidth()
	{
		return _right - _left + 1;
	}

	private ArrayList<MouseListener> _mListeners;

	public void addMouseListener( final MouseListener listener )
	{
		if ( _mListeners == null )
		{
			_mListeners = new ArrayList<MouseListener>();
		}
		if ( _mListeners.contains( listener ) )
		{
			return;
		}
		_mListeners.add( listener );
	}

	public void removeMouseListener( final MouseListener listener )
	{
		if ( _mListeners == null )
		{
			return;
		}
		_mListeners.remove( listener );
	}

	private ArrayList<MouseMotionListener> _mmListeners;

	public void addMouseMotionListener( final MouseMotionListener listener )
	{
		if ( _mmListeners == null )
		{
			_mmListeners = new ArrayList<MouseMotionListener>();
		}
		if ( _mmListeners.contains( listener ) )
		{
			return;
		}
		_mmListeners.add( listener );
	}

	public void removeMouseMotionListener( final MouseMotionListener listener )
	{
		if ( _mmListeners == null )
		{
			return;
		}
		_mmListeners.remove( listener );
	}

	public boolean isFixed()
	{
		return _index > 0;
	}

	public void processMouseEvent( final MouseEvent me )
	{
		if ( _mListeners == null )
		{
			return;
		}
		for ( MouseListener ml : _mListeners )
		{
			switch ( me.getID() )
			{
				case MouseEvent.MOUSE_CLICKED:
					ml.mouseClicked( me );
					break;
				case MouseEvent.MOUSE_ENTERED:
					ml.mouseEntered( me );
					break;
				case MouseEvent.MOUSE_EXITED:
					ml.mouseExited( me );
					break;
				case MouseEvent.MOUSE_PRESSED:
					ml.mousePressed( me );
					break;
				case MouseEvent.MOUSE_RELEASED:
					ml.mouseReleased( me );
					break;
					/*
														case MouseEvent.MOUSE_DRAGGED:
															ml.mouseReleased( me );
															break;
									*/
				default:
					break;
			}
		}
	}

	public void processMouseMotionEvent( final MouseEvent me )
	{
		if ( _mmListeners == null )
		{
			return;
		}
		for ( MouseMotionListener mml : _mmListeners )
		{
			switch ( me.getID() )
			{
				case MouseEvent.MOUSE_DRAGGED:
					mml.mouseDragged( me );
					break;
				case MouseEvent.MOUSE_MOVED:
					mml.mouseDragged( me );
					break;
				default:
					break;
			}
		}
	}

	public void setTextColor( final Color color )
	{
		if ( color != null )
			_textColor = color;
		repaint();
	}

	public Color getTextColor()
	{
		return _textColor != null ? _textColor : Color.BLACK;
	}

	public void setProgressColor( Color color )
	{
		if ( color == null )
			color = DEFAULT_PROGRESS_COLOR;
		m_progColor = color;
	}

	public Color getProgressColor()
	{
		return m_progColor;
	}

	public void setProgressValue( float val )
	{
		if ( val < 0.0f || val > 1.0f )
			val = 0.0f;
		if ( m_progress != val )
		{
			m_progress = val;
			// check if something changed
			if ( m_progressWidth != JStatusBar.calcProgressWidth( this) )
				update();
		}
	}

	public void update()
	{
		m_sb.repaint( _left, 0, getWidth(), m_sb.getHeight() );
	}

	public void setProgress( float value )
	{
		setProgressValue( value );
	}
}