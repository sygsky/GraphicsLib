/**
 * <pre>
 * Created on 08.07.2009 17:50:41<br> 
 * by Syg<br> 
 * for project in 'window.multimap.intf'
 *</pre>
 */
package ru.ts.gui.intf;

import su.misc.intf.IProgressObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Package ru.ts.gui.intf<br>
 * Author 'Syg'<br>
 * Created  08.07.2009  17:50:41<br>
 * Defines typical status bar methods. Typical status bar is looked as follow:
 * <pre>
 * _________________ _________ _________ ____ _________
 * | The left panel | Right 1 | Right 2 |... | Right N |
 * ----------------- --------- --------- ---- ---------
 * contains:
 * 1. Single left component with an user text. This is the only
 * component of the status bar with floating width depending on the form one. With
 * change of form width the status bar width changes with left
 * component width expense
 * 2. Any rational number of right components of fixed width. It means that
 * these fixed width components are always visible on the form. Only after the
 * form is too narrow to show all right components, they became to be invisible or
 * visible partially
 * 3. Rightmost panel contain the icon in the
 * right lower corner to help move the window itself.
 * <pre>
 * When status bar is created, the one (left) panel named "$AncestorPanel$" already
 * exists on it with a floating characters number
 */
public interface IStatusBar
{

 /**
	 * Interface for any of the Status Bar panel, appended from right side to the
   * original one named  "$AncestorPanel$"
	 */
	public interface ISBPanel extends IProgressObserver
	{
		/**
		 * Align panel text to the left border, after icon, if it is present or
		 * directly after separator. It is the <b>default</b> value
		 */
		public static final int ALIGN_LEFT = 0;

		/**
		 * Align panel text to the center
		 */
		public static final int ALIGN_CENTER = 1;

		/**
		 * Align panel text to the right boundary of the panel
		 */
		public static final int ALIGN_RIGHT = 2;

		/**
		 * Gets text alignment type for the current {@link ISBPanel} instance
		 *
		 * @return {@link ISBPanel#ALIGN_LEFT} or {@link ISBPanel#ALIGN_CENTER} or
		 *         {@link ISBPanel#ALIGN_RIGHT} value
		 */
		int getAlign();

		/**
		 * Sets the alignment for the text of the current {@link ISBPanel} instance
		 *
		 * @param align int value from the range:
		 *              {@link ISBPanel#ALIGN_LEFT}
		 *              {@link ISBPanel#ALIGN_CENTER} or
		 *              {@link ISBPanel#ALIGN_RIGHT}
		 *              If you designated unknown value as parameter, no changes occur
		 */
		void setAlign( int align );

		/**
		 * Gets the name of this panel
		 *
		 * @return String with a panel name
		 */
		String getName();

		/**
		 * Gets the preset text width allowed to output on this panel
		 *
		 * @return int value with number of chars to output for the panel
		 */
		int getTextWidth();

		/**
		 * Sets the number of characters allowed for output onto this panel
		 *
		 * @param newCharNum int wiht new text length allowed to output
		 */
		void setTextWidth( int newCharNum );

		/**
		 * Sets the number of characters allowed for output onto this panel.
		 *
		 * @param sampleText String with sample text. Width is set according
		 *                   to the ssample string length as if {@link String#length()}
		 */
		void setTextWidth( String sampleText );

		/**
		 * Gets the text assotiated with  the panel
		 *
		 * @return String with a text for the panel
		 */
		String getText();

		/**
		 * Sets the text for the panel
		 *
		 * @param newText String with a new text for the panel
		 */
		void setText( String newText );

		String getToolTip();

		void setToolTip( String newToolTipText );

		/**
		 * Gets the icon assigned for this panel
		 *
		 * @return {@link Icon} instance or {@code null} if icon is not assigned
		 */
		Icon getIcon();

		/**
		 * Sets a new icon for the panel. Icon should have size 16x16 or >= status bar
		 * height
		 *
		 * @param newIcon {@link Icon} instance for the panel or {@code null} to remove
		 *                icon from panel
		 */
		void setIcon( Icon newIcon );

		/**
		 * Left boundary of the panel acoording to the status bar
		 *
		 * @return int value with panel left boundary ordinate value
		 */
		int getLeftBound();

		/**
		 * Right boundary of the panel according to the status bar
		 *
		 * @return int value with panel right boundary ordinate value
		 */
		int getRightBound();

		/**
		 * Gets the panel current width in pixels.
		 *
		 * @return int value with panel width in pixels
		 */
		int getWidth();

		void addMouseListener( MouseListener listener );

		void removeMouseListener( MouseListener listener );

		void addMouseMotionListener( MouseMotionListener listener );

		void removeMouseMotionListener( MouseMotionListener listener );

		/**
		 * The type of the panel, may be of fixed size or floatable size
		 *
		 * @return {@code true} if the panel has fixed size (in pixels), else {@code false}
		 */
		boolean isFixed();

		/**
		 * processes {@link MouseEvent} from the mouse over panel
		 *
		 * @param me {@link MouseEvent} instance to process
		 */
		void processMouseEvent( final MouseEvent me );

		/**
		 * processes {@link MouseEvent} from the mouse over panel
		 *
		 * @param me {@link MouseEvent} instance to process
		 */
		void processMouseMotionEvent( final MouseEvent me );

		/**
		 * Sets text color to the new {@link Color} instance value
		 *
		 * @param color {@link Color} instance to use for the text drawing
		 */
		void setTextColor( final Color color );

		/**
		 * returns text drawing color
		 *
		 * @return {@link Color} instance used to draw text
		 */
		Color getTextColor();

		/**
		 * Gets progress color
		 * @param color {@link Color} instance for progress operation
		 */
		void setProgressColor( Color color );

		/**
		 * Returns predefined progress color
		 * @return {@link Color} instance used for progress operation of this panel
		 */
		Color getProgressColor( );

		/**
		 * Sets progress value. If > 0.0f, progress is displayed, else no progress displayed
		 * @param val float value from 0.0f to 1.0f. If out of range, default valaue 0.0f is used ( progress not displayed
		 */
		void setProgressValue( float val );

		/**
		 * Informs the graphics system to repaint this panel
		 */
		void update();

	}

	/**
	 * This is a first and always left panel name. This panel is created together
   * with a status bar and occupied whole it from the start. Later you can add
   * more static width panels from the right side
	 */
	public static final String ANCESTOR_PANEL_NAME = "$AncestorPanel$";

  ISBPanel createNewPanel( String panelName );

  /**
   * Returns itself as a instance of {@link Component}
   * @return  {@link Component} instance
   */
  Component asComponent();

  /**
   * Gets the main panel of the status bar. It always is present and has
   * reserved name "$AncestorPanel$"
   * @return always return main panel named "$AncestorPanel$"
   */
  ISBPanel getMainPanel( );

	/**
	 * Gets the number of all panels, including left unfixed one
	 *
	 * @return int with a whole nunmber of all status bar panels
	 */
	int getPanelCount();

	ISBPanel getPanel( int index );

	ISBPanel getPanel( String panelName );

	/**
	 * Gets the panel index with designated name. Name of a panel is case sensitive!
	 *
	 * @param panelName the name to find among existing panels
	 * @return int value with index or -1 if no such name found
	 */
	int getPanelIndex( String panelName );

	/**
	 * Adds one more panel to the status bar at designated index. Remember
	 * that panel with index 0 is the only one changing its size on form resize.
	 *
	 * @param panel panel to add
	 * @param index index to insert new panel. If index <=0 panel is inserted into
	 *              the leftmost position and became one with a floatable width.
	 *              If index >= {@link IStatusBar#getPanelCount()}, panel is appended to the list
	 * @return index where panel is added or -1 if panel has any illegal parameter,
	 *         for example non-unique name among this {@link IStatusBar} instance sub-panels.
	 */
	int addPanel( ISBPanel panel, int index );

	/**
	 * Appends the panel to the right end of the status bar
	 *
	 * @param panel {@link ISBPanel} instance
	 * @return index of the panel added to the status bar
	 */
	int appendPanel( ISBPanel panel );

	boolean removePanel( int index );

	boolean removePanel( String panelName );

	void setSeparatorColors( Color leftColor, Color rightColor );

	Color getSeparatorLeftColor();

	Color getSeparatorRightColor();

	/**
	 * Sets text for the left panel
	 *
	 * @param text String with left panel new text to display on draw
	 */
	void setText( String text );

}
