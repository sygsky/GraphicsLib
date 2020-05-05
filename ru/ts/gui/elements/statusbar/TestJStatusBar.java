/**
 *</pre>
 * Created on 14.07.2009 16:55:38<br> 
 * by Syg<br> 
 * for project in 'TestGUI.TestStatusBar'
 *</pre>
 */
package ru.ts.gui.elements.statusbar;

import ru.ts.common.misc.*;
/*
import ru.ts.gisutils.gisproj.ini.IBufferProps;
import ru.ts.gpclient.ext.IGPClientContext;
import ru.ts.gpclient.gpr.GPClientSessionGPR;
import ru.ts.gpclient.gui.ContextViewPanel;
import ru.ts.gpclient.imp.GPClientBase;
import ru.ts.gui.GUIFactory;
import ru.ts.gui.GuiUtils;
import ru.ts.gui.JStatusBar;
import ru.ts.gui.elements.statusbar.JStatusBar;
import ru.ts.gui.intf.IStatusBar;
import window.multimap.GisPanelToWork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Calendar;
*/

/**
 * Package TestGUI.TestStatusBar<br>
 * Author 'Syg'<br>
 * Created  14.07.2009  16:55:38<br>
 */
public class TestJStatusBar //implements MouseMotionListener
{
/*
	static public TestJStatusBar mainForm;
	static public JFrame mainFrame;
	static private GUIFactory guiFactory;

	*/
/**
	 * Selected object style library properties
	 */
/*
	static public IBufferProps bufferProps;

	private ContextViewPanel contextPane;

	private JStatusBar sb;

	private DrawTime dt;

	public TestJStatusBar( String[] args )
	{
		*/
/* Create and set up the window */
/*
		guiFactory = GUIFactory.getInstance( "ru/ts/images" );
		mainFrame = new JFrame( "Демо компонтента JStatusBar" );

		// content
		JPanel content = new JPanel();
		content.setPreferredSize( new Dimension( 800, 600 ) );
//    mainFrame.setSize( 800, 600 );
		content.setFocusable( true );
		mainFrame.setContentPane( content );
		mainFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		defineWindowListener();
		mainFrame.setIconImage( guiFactory.load_icon( "TS_Logo2.gif", "MainIcon" ).getImage() );

		// demo
		content.setLayout( new BorderLayout() );

		// content
		contextPane = new ContextViewPanel( false );
		contextPane.setPreferredSize( new Dimension( 500, 500 ) );
		content.add( contextPane, BorderLayout.CENTER );

		// status bar
		sb = createStatusBar();
		content.add( sb, BorderLayout.SOUTH );

		// main menu & toolbar
		JMenuBar mainMenuBar = new JMenuBar();
		JToolBar toolBar = new JToolBar();
		createMenu( mainMenuBar, toolBar );
		mainFrame.setJMenuBar( mainMenuBar );
		mainFrame.add( toolBar, BorderLayout.NORTH );

		mainFrame.pack();
		mainFrame.setVisible( true );
		//    mainFrame.getContentPane().repaint();
		if ( !Text.isEmpty( fnmGpr ) )
		{
			setContext( fnmGpr );
		}
		contextPane.toDraw.addMouseMotionListener( this );
	}


	private JStatusBar createStatusBar()
	{
		JStatusBar sb = new JStatusBar();

		// main panel icon
		sb.getPanel( 0 ).setIcon( guiFactory.load_icon( "Map16x16.gif" */
/* "information_light_blue.gif" */ /*
) );
		sb.getPanel( 0 ).setToolTip( "Имя файла загруженного проекта" );

		// scale panel
		final IStatusBar.ISBPanel sPanel = sb.createNewPanel( "Scale" );
		sPanel.setTextWidth( "1: XXX XXX XXX XXX" );
		sPanel.setText( "1: XXX XXX XXX XXX" );
		sPanel.setIcon( guiFactory.load_icon( "scale_in_circle.gif" ) );
		sPanel.setTextColor( Color.BLUE );
		sPanel.setToolTip( "Масштаб изображения" );
		sb.appendPanel( sPanel );  // append to the right end

		// coordinates of mouse panel
		final IStatusBar.ISBPanel cPanel;
		cPanel = sb.createNewPanel( "Coordinates" );
		cPanel.setText( MOUSE_INFO_MASK );
		cPanel.setTextWidth( MOUSE_INFO_MASK );
		cPanel.setTextColor( Color.RED );
		cPanel.setToolTip( "Координаты курсора мыши" );
		cPanel.setIcon( guiFactory.load_icon( "mouse.gif" ) );
		sb.addPanel( cPanel, 1 );

		// time panel
		final IStatusBar.ISBPanel tPanel;
		tPanel = sb.createNewPanel( "DateTime" );
		tPanel.setText( TIME_INFO_MASK );
		tPanel.setTextWidth( TIME_INFO_MASK );
		tPanel.setTextColor( Color.BLACK );
		tPanel.setToolTip( "Время исполнения" );
		tPanel.setIcon( guiFactory.load_icon( "date_and_time.gif" ) );
		sb.addPanel( tPanel, 1 );
		dt = new DrawTime( tPanel );
		dt.start();

		return sb;
	}

	class DrawTime extends Thread
	{
		Date _start;

		private IStatusBar.ISBPanel _tpnl;

		public DrawTime( final IStatusBar.ISBPanel timePnl )
		{
			_tpnl = timePnl;
		}

		public void run()
		{
			_start = DateTime.now();
			while( true )
			{
				if ( this.isInterrupted() )
					break;
				try
				{
					this.sleep( 1000 );
				}
				catch ( InterruptedException e )
				{
					break;
				}
				// print time
				final TimeSpan ts = new TimeSpan( _start );
				final String txt = ts.toString( Calendar.SECOND );
				_tpnl.setText( txt );
			}
		}
	}

	*/
/**
	 * Creates all UI elements for the application
	 *
	 * @param menubar menu to be created
	 */
/*
	private void createMenu( JMenuBar menubar, JToolBar tb )
	{
		// file menu
		GUIFactory gf = GUIFactory.getInstance( "ru/ts/images" );
		JMenu menu = new JMenu( "Файл" );
		ActionListener a = new ActionListener()
		{
			public void actionPerformed( final ActionEvent e )
			{
				final FileChooserInfo fci;
				String prjpath = Files.FileChooser.getPathToOpenFromUser( mainFrame, GisPanelToWork.prjChoose, null );
				if ( prjpath != null )
				{
					setContext( prjpath );
					sb.setText( prjpath );
				}
				else
				{
					//sb.setText( null );
				}
				//TODO: add your realization here
				//To change body of implemented methods use File | Settings | File Templates.
			}
		};
		gf.insert_action( tb, "JButton", menu, "JMenuItem", "file.gif", null, "Открыть проект", null, "Открыть проект", a, "OpenProject" );

		a = new ActionListener()
		{
			public void actionPerformed( final ActionEvent e )
			{
				System.exit( 0 );
			}
		};
		gf.insert_action( tb, "JButton", menu, "JMenuItem", "Exit2.gif", null, "Выход", null, "Выход из программы", a, "Exit" );
*/
/*
		JMenuItem mi = new JMenuItem( "Exit");
		menu.add( mi );
*/
/*
		menubar.add( menu );

		// view menu

		// about menu
		menu = new JMenu( "Помощь" );
		addHelpMenu( menu );
		menubar.add( menu );
	}

	private void setContext( final String prjpath )
	{
		final File file = new File( prjpath );
		if ( !file.exists() || file.isDirectory() )
		{
			final String name = Files.getName( prjpath );
			sb.setText( "Указанный файл  " + name + " не годится" );
			return;
		}
		openProjectContext( file );
	}

	protected GPClientBase client = new GPClientBase();
	protected GPClientSessionGPR session = new GPClientSessionGPR( client );

	public void openProjectContext( File file )
	{
		try
		{
			String fname = file.getCanonicalPath();
			String dir = Files.getDirectory( fname );
			fname = Files.getName( fname );
			IGPClientContext context = session.openDefaultContext( dir, fname );
			if ( context == null )
				throw new Exception();
			contextPane.setContext( context );
			sb.setText( file.getCanonicalPath() );
		}
		catch ( Exception ex )
		{
			sb.setText( "Ошибка открытия \"" + file + "\"" );
			ex.printStackTrace();
		}
	}

	*/
/**
	 * Adds help menu to the main frame
	 *
	 * @param menu menu
	 */
/*
	private void addHelpMenu( JMenu menu )
	{
		JMenuItem menuItem = guiFactory.load_menu_item( "information.gif", "<html>О программе <font color=\"red\">(About)",
						KeyStroke.getKeyStroke( KeyEvent.VK_F1, InputEvent.ALT_MASK ),
						"Краткая справка о нашей программе", null, "About" );

		menuItem.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent e )
			{
				showAbout();
			}
		} );
		menu.add( menuItem );
	}

	public void showAbout()
	{
		*/
/* Icon icon = ProcessComponents.loadIconFromResources( "images\\Help24.gif", "О..." );*/
/*
		ImageIcon icon = guiFactory.load_icon( "information.gif", "O..." );
		JOptionPane.showMessageDialog( null, new String[]{
						"демо-версия \"GenGIS\" для JStatusBar , версия 0.1 Alpha",
						"Сейчас (текущее время " + DateTime.date2StdString( DateTime.now() ) + ")",
						"вы иcпользуете следующий софт:", "",
						"Версия 0.1 Альфа, вариант для демо-режима.",
						"Разработчики Yugl, Sygsky.",
						"Предназначена для демонстрации подхода к отображению карт на Java.",
						"." }, "Краткая (пока) информация о программе",
						JOptionPane.INFORMATION_MESSAGE, icon
		);
	}

	static public String fnmGpr;

	public static void main( String[] args ) //throws IOException, UnsupportedEncodingException
	{
		GuiUtils.setSystemLookAndFeel();
		final ICmdArgs cmd = new ICmdArgs.Impl( args );

		// check of parameters presence
		if ( cmd.contains( "-m" ) )
		{
			fnmGpr = cmd.value( "-m" );
			printStatus( "Файл проекта: \"" + fnmGpr + "\"" );
		}
		else
			printStatus( "Ключ -m не найден (файл проекта)" );
		// open the main form
		mainForm = new TestJStatusBar( args );
	}

	static public void reportErr( String msg )
	{
		JOptionPane.showMessageDialog( null, msg, "Обнаружена ошибка в JStatusBar Demo", JOptionPane.ERROR_MESSAGE );
	}

	//++ ------------ WindowListener ---------------
	private void defineWindowListener()
	{
		mainFrame.addWindowListener( new WindowListener()
		{
			public void windowOpened( WindowEvent e )
			{
				//TODO: add your realization here
			}

			public void windowClosing( WindowEvent e )
			{
				//TODO: add your realization here
			}

			public void windowClosed( WindowEvent e )
			{
				//TODO: add your realization here
			}

			public void windowIconified( WindowEvent e )
			{
				//TODO: add your realization here
			}

			public void windowDeiconified( WindowEvent e )
			{
				//TODO: add your realization here
			}

			public void windowActivated( WindowEvent e )
			{
				//TODO: add your realization here
			}

			public void windowDeactivated( WindowEvent e )
			{
				//TODO: add your realization here
			}
		} );
	}
	//-- ------------ WindowListener ---------------

	public static void printStatus( String msg )
	{
		if ( (mainForm != null) && (mainForm.sb != null) )
		{
			mainForm.sb.setText( msg );
		}
	}

	public void mouseDragged( final MouseEvent e )
	{
		mouseMoved( e );
	}

	private final String MOUSE_INFO_MASK = "X: 000 000; Y: 000 000";

	*/
/**
	 * format for output is as follow: "X: 000 000; Y: 000 000"
	 */
/*

	public void mouseMoved( final MouseEvent e )
	{
		int x = e.getX();
		int y = e.getY();
		StringBuilder strb = new StringBuilder( MOUSE_INFO_MASK.length() );
		String str = MessageFormat.format( "X: {0}", x );
		strb.append( str );
		int yPos = MOUSE_INFO_MASK.indexOf( "Y: " );
		for ( int i = str.length(); i < yPos; i++ )
		{
			strb.append( ' ' );
		}
		str = MessageFormat.format( "Y: {0}", y );
		strb.append( str );
		sb.getPanel( "Coordinates" ).setText( strb.toString() );
	}

	private final String TIME_INFO_MASK = "18:19:12";

*/
}
