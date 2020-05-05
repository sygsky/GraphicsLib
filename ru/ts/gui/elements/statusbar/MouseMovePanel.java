package ru.ts.gui.elements.statusbar;

import javax.swing.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;

/**
 * Created by IntelliJ IDEA.
 * User: sigolaev_va
 * Date: 14.11.2013
 * Time: 13:20:53
 * Mouse movement
 * Move movement panel to add to {@link ru.ts.gui.elements.statusbar.JStatusBar}.
 * Register this component as {@link MouseMotionListener} for component you are interested to see mouse movement at status bar
 */
public class MouseMovePanel extends JSBPanel implements MouseMotionListener
{

	public static final String MOUSE_INFO_MASK = "X: *** ***; Y: *** ***";
	public static final int SPACE_FOR_X_POS_1 = 6; // where to insert 1st space
	public static final int SPACE_FOR_X_POS_2 = 17; // where to insert 2nd space

	public MouseMovePanel( JStatusBar sb, String panelName, Icon ico, String TooltipText )
	{
		super( sb, panelName );
		setText( MOUSE_INFO_MASK );
		setTextWidth( MOUSE_INFO_MASK.length() );
		setIcon(ico);
		setToolTip( TooltipText );
	}

	public void mouseDragged( MouseEvent e )
	{
		mouseMoved( e );
	}

	public void mouseMoved( MouseEvent e )
	{
		setXY(e.getX(), e.getY());
	}

	public void setXY( int x, int y )
	{
		StringBuilder strb = new StringBuilder( String.format( "X: %06d; Y: %06d", x, y ) );
		strb.insert( SPACE_FOR_X_POS_2, ' ');
		strb.insert( SPACE_FOR_X_POS_1, ' ');
		setText( strb.toString() );

	}
}
