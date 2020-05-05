/**
 * Created on 02.12.2008 11:25:41 by Syg for project in
 * 'ru.ts.GUI'
 */
package ru.ts.gui;

import ru.ts.common.misc.Text;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA for ru.ts.GUI by User 'Syg' at 02.12.2008  11:25:41
 * <p/>
 * This class contains small static methods to use in GUI for any application
 */
public class GuiUtils
{

	/**
   * Finds the ancestor {@link JFrame} for the designated {@link Component} instance
   * @param child {@link Component} to find its {@link JFrame} ancestor
   * @return {@link JFrame} instance if found, else <code>null</code> is returned
   */
  public static JFrame findAncestorJFrame( Component child )
  {
    if ( child == null )
      return null;
    Container cont = child.getParent();
    while ( cont != null )
    {
      if ( cont instanceof JFrame )
        return (JFrame) cont;
      cont = cont.getParent();
    }
    return null;
  }

	/**
	 * Gets width of multiline field splitting in pixels (600 is default value)
	 * @return int with pixel number for multiline fields
	 */
  public static int getWidth()
  {
    return MessageBoxes.m_width;
  }

	public static class MessageBoxes
	{
		/**
   * Default split width
		 */
		private static int m_width = 600;

		/**
		 * The stub for {@link JOptionPane#showMessageDialog(java.awt.Component, Object, String, int, javax.swing.Icon)}
		 *
		 * @param parent {@link java.awt.Component} instance to be parent of the message box,
		 *               use {@code null} if you don't know it, but after window change you can lost this
		 *               message box behind any otheк window
		 * @param msg    String with the message itself
		 * @param title  String with box title
		 * @param type   int value with {@link JOptionPane#ERROR_MESSAGE} or
		 *               {@link JOptionPane#WARNING_MESSAGE} or
		 *               {@link JOptionPane#QUESTION_MESSAGE} or
		 *               {@link JOptionPane#PLAIN_MESSAGE}
		 */
		static public void showMessage( Component parent, String msg, String title, int type )
		{
      msg = parseToMultiLine(msg);
      JOptionPane.showMessageDialog( parent, msg, title, type );
		}

    private static String parseToMultiLine( String msg )
    {
	    return parseMultiLine( msg, m_width );
    }

		public static String parseMultiLine( String msg, int splitWidth )
		{
		  //msg = Text.replaceAll( msg, "\n", "\\n" /*<br>*/ );
		  //msg = Text.replaceAll( msg, ">", "&gt;" );
		  //msg = Text.replaceAll( msg, "<", "&lt;" );
		  return "<html><body style='width: " + splitWidth + "px; padding: 5px;'>" + msg ; // <font color="red">
		}

    /**
		 *
		 * @param parent
		 * @param msg
		 * @param title
		 * @param icon
		 * @return option entered, may be of {@link JOptionPane#YES_OPTION} or {@link JOptionPane#NO_OPTION}
		 */
		static public int showYesNoDialog( Component parent, String msg, String title, Icon icon )
		{
			msg = parseToMultiLine( msg );
			return JOptionPane.showConfirmDialog( parent, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon );
		}

		/**
		 *
		 * @param parent
		 * @param msg
		 * @param title
		 * @return option entered, may be of {@link JOptionPane#YES_OPTION} or {@link JOptionPane#NO_OPTION}
		 */
		static public int showYesNoDialog( Component parent, String msg, String title )
		{
			msg = parseToMultiLine( msg );
			return JOptionPane.showConfirmDialog( parent, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );
		}

		/**
		 *
		 * @param parent
		 * @param msg
		 * @param title
		 * @param icon
		 * @return option entered, may be of {@link JOptionPane#YES_OPTION} or {@link JOptionPane#NO_OPTION}
		 */
		static public int showOkCancelDialog( Component parent, String msg, String title, Icon icon )
		{
			msg = parseToMultiLine( msg );
			return JOptionPane.showConfirmDialog( parent, msg, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon );
		}

		/**
		 *
		 * @param parent
		 * @param msg
		 * @param title
		 * @return option entered, may be of {@link JOptionPane#YES_OPTION} or {@link JOptionPane#NO_OPTION}
		 */
		static public int showOkCancelDialog( Component parent, String msg, String title )
		{
			msg = parseToMultiLine( msg );
			return JOptionPane.showConfirmDialog( parent, msg, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE );
		}


		/**
		 * The stub for error message dialog box
		 *
		 * @param parent {@link java.awt.Component} instance to be parent of the message box,
		 *               use {@code null} if you don't know it, but after window change you can lost this
		 *               message box behind any otheк window
		 * @param msg    String weth the message itself
		 * @param title  String with box title
		 */
		static public void showInfo( Component parent, String msg, String title )
		{
			showMessage( parent, msg, title, JOptionPane.INFORMATION_MESSAGE );
		}

		/**
		 * The stub for error message dialog box
		 *
		 * @param parent {@link java.awt.Component} instance to be parent of the message box,
		 *               use {@code null} if you don't know it, but after window change you can lost this
		 *               message box behind any otheк window
		 * @param msg    String weth the message itself
		 * @param title  String with box title
		 */
		static public void showError( Component parent, String msg, String title )
		{
			showMessage( parent, msg, title, JOptionPane.ERROR_MESSAGE );
		}

		/**
		 * The stub for warning message dialog box
		 *
		 * @param parent {@link java.awt.Component} instance to be parent of the message box,
		 *               use {@code null} if you don't know it, but after window change you can lost this
		 *               message box behind any otheк window
		 * @param msg    String weth the message itself
		 * @param title  String with box title
		 */
		static public void showWarning( Component parent, String msg, String title )
		{
			showMessage( parent, msg, title, JOptionPane.WARNING_MESSAGE );
		}

		/**
		 * The stub for question message dialog box
		 *
		 * @param parent {@link java.awt.Component} instance to be parent of the message box,
		 *               use {@code null} if you don't know it, but after window change you can lost this
		 *               message box behind any otheк window
		 * @param msg    String weth the message itself
		 * @param title  String with box title
		 */
		static public void showQuestion( Component parent, String msg, String title )
		{
			showMessage( parent, msg, title, JOptionPane.QUESTION_MESSAGE );
		}

		/**
		 * The stub for question message dialog box
		 *
		 * @param parent {@link java.awt.Component} instance to be parent of the message box,
		 *               use {@code null} if you don't know it, but after window change you can lost this
		 *               message box behind any otheк window
		 * @param msg    String weth the message itself
		 * @param title  String with box title
		 * @param icon {@link Icon} instance to output on the message box
		 */
		static public void showIcon( Component parent, String msg, String title, Icon icon )
		{
			msg = parseToMultiLine( msg );
			JOptionPane.showMessageDialog( parent, msg, title, JOptionPane.PLAIN_MESSAGE, icon );
		}

		public static void setWidth( int width )
		{
		  m_width = width;
		}
	}
	/**
	 * Sets the current system look and feel properties
	 */
	public static void setSystemLookAndFeel()
	{
		/* Get the default operation system style */
		final String uiName = UIManager.getSystemLookAndFeelClassName();
		/* Set this default current style for out task */
		try
		{
			UIManager.setLookAndFeel( uiName );
		}
		catch ( Exception ex )
		{
			System.err.println( "Can't set GUI style:" + ex.getMessage() );
		}
	}

  /**
   * Seeks an AbstractButton with given action command among parent's components.
   *
   * @param parent   parent component
   * @param acommand action command string
   * @return reference to found AbstractButton or null
   */
  static public AbstractButton seekACommand( JComponent parent, String acommand )
  {
    if ( parent == null ) return null;
    Component[] cmps = parent.getComponents();
    for ( Component cmp : cmps )	{
      if ( cmp instanceof AbstractButton )	{   // JToggleButton
        AbstractButton abtn = (AbstractButton) cmp;
        if ( abtn.getActionCommand().equals( acommand ) )	return abtn;
      }
    }
    return null;
  }

  /**
   * Seeks an AbstractButton with given action command among JMenu items.
   *
   * @param parent   parent JMenu
   * @param acommand action command string
   * @return reference to found AbstractButton or null
   */
  static public AbstractButton seekACommand( JMenu parent, String acommand )
  {
    if ( parent == null ) return null;
    int n = parent.getItemCount();
    for ( int i = 0; i < n; i++ )	{
      AbstractButton abtn = parent.getItem( i );
      if ( abtn.getActionCommand().equals( acommand ) )	return abtn;
    }
    return null;
  }

	/**
	 * Checks all components for AbstractButton derived ones, seeks among them
	 * a button with given action command, sets its status to selected or deselected,
	 * depending on a given flag.
	 *
	 * @param parent   parent component
	 * @param acommand action command string
	 * @param on       flag of selected status
	 */
	static public void seekAndSelectACommand( JComponent parent, String acommand, boolean on )
	{
    AbstractButton abtn = seekACommand(parent, acommand);
    if ( abtn == null ) return;
    abtn.setSelected( on );
	}

	/**
	 * Checks all items, seeks among them a button with given action command,
	 * sets its status to selected or deselected, depending on a given flag.
   * Works incorrectly!!! ToDo
	 *
	 * @param parent   parent menu
	 * @param acommand action command string
	 * @param on       flag of selected status
	 */
	static public void seekAndSelectACommand( JMenu parent, String acommand, boolean on )
	{
    AbstractButton abtn = seekACommand(parent, acommand);
    if ( abtn == null ) return;
//      parent.invalidate();
//      abtn.setSelected( on );
//      parent.validate();
    abtn.setSelected( on );
	}

  /**
   * Checks all components for AbstractButton derived ones, seeks among them
   * a button with given action command, sets its status to enabled or disabled,
   * depending on a given flag.
   *
   * @param parent   parent component
   * @param acommand action command string
   * @param on       flag of enabled status
   */
  static public void seekAndEnableACommand( JComponent parent, String acommand, boolean on )
  {
    AbstractButton abtn = seekACommand(parent, acommand);
    if ( abtn == null ) return;
    abtn.setEnabled( on );
  }
  /**
   * Checks all components for AbstractButton derived ones, seeks among them
   * a button with given action command, sets its status to visible or invisible,
   * depending on a given flag.
   *
   * @param parent   parent component
   * @param acommand action command string
   * @param on       flag of visible status
   */
  static public void seekAndSetVisibleACommand( JComponent parent, String acommand, boolean on )
  {
    AbstractButton abtn = seekACommand(parent, acommand);
    if ( abtn == null ) return;
    abtn.setVisible( on );
  }

}
