package ru.ts.gui.elements;

import ru.ts.gui.GuiUtils;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.StyledEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class SplashScreenTest  extends IPropertyResourceBundle.Impl
{

	private JFrame mainFrame = null;

	private JSplash splash = null;

	private ResourceBundle bundle = null;

	private JLabel statusBar = null;

	private JTextPane textarea = null;

	private JMenuBar menubar = null;

	private JPopupMenu popupMenu = null;

	private boolean hasChanged = false;

	public SplashScreenTest()
	{
		mainFrame = new JFrame( getString( "MainWin_Title" ) + " - [Untitled]" );
		Image icon = Toolkit.getDefaultToolkit().getImage(
				getClass().getResource( "resources/images/icon.gif" ) );
		mainFrame.setIconImage( icon );

		splash = new JSplash( getFrame() );

		JSplash.start(splash);

		mainFrame.addWindowListener( new WindowAdapter()
		{
			public void windowClosing( WindowEvent e )
			{
				System.exit( 0 );
			}
		} );

		initComponents();
		mainFrame.setSize( 600, 600 );

		JSplash.stop(splash);
	}

	public void initComponents()
	{

		menubar = new JMenuBar();

		JMenu fileMenu = createMenu( menubar, "FileMenu_Label", "FileMenu_Mnemonic" );

		createMenuItem( fileMenu, "FileMenu.New_Label", "FileMenu.New_Mnemonic",
				KeyStroke.getKeyStroke( KeyEvent.VK_N, KeyEvent.CTRL_MASK ), new NewAction() );

		createMenuItem( fileMenu, "FileMenu.Open_Label", "FileMenu.Open_Mnemonic",
				KeyStroke.getKeyStroke( KeyEvent.VK_O, KeyEvent.CTRL_MASK ), new OpenAction() );

		createMenuItem( fileMenu, "FileMenu.Save_Label", "FileMenu.Save_Mnemonic",
				KeyStroke.getKeyStroke( KeyEvent.VK_S, KeyEvent.CTRL_MASK ), new SaveAction() );

		fileMenu.addSeparator();

		createMenuItem( fileMenu, "FileMenu.Pref_Label", null, null, new ExitAction() );

		fileMenu.addSeparator();

		createMenuItem( fileMenu, "FileMenu.Exit_Label", "FileMenu.Exit_Mnemonic",
				KeyStroke.getKeyStroke( KeyEvent.VK_Q, KeyEvent.CTRL_MASK ), new ExitAction() );

		JMenu editMenu = createMenu( menubar, "EditMenu_Label", "EditMenu_Mnemonic" );

		createMenuItem( editMenu, "EditMenu.Cut_Label", "EditMenu.Cut_Mnemonic",
				KeyStroke.getKeyStroke( KeyEvent.VK_X, KeyEvent.CTRL_MASK ), new DefaultEditorKit.CutAction() );

		createMenuItem( editMenu, "EditMenu.Copy_Label", "EditMenu.Copy_Mnemonic",
				KeyStroke.getKeyStroke( KeyEvent.VK_C, KeyEvent.CTRL_MASK ), new DefaultEditorKit.CopyAction() );

		createMenuItem( editMenu, "EditMenu.Paste_Label", "EditMenu.Paste_Mnemonic",
				KeyStroke.getKeyStroke( KeyEvent.VK_V, KeyEvent.CTRL_MASK ), new DefaultEditorKit.PasteAction() );

		editMenu.addSeparator();

		createMenuItem( editMenu, "EditMenu.SelectAll_Label", null,
				KeyStroke.getKeyStroke( KeyEvent.VK_A, KeyEvent.CTRL_MASK ), new SelectAllAction() );

		editMenu.addSeparator();

		createMenuItem( editMenu, "EditMenu.Find_Label", null,
				KeyStroke.getKeyStroke( KeyEvent.VK_F, KeyEvent.CTRL_MASK ), null );

		createMenuItem( editMenu, "EditMenu.Replace_Label", null,
				KeyStroke.getKeyStroke( KeyEvent.VK_H, KeyEvent.CTRL_MASK ), null );


		JMenu helpMenu = createMenu( menubar, "HelpMenu_Label", "HelpMenu_Mnemonic" );

		createMenuItem( helpMenu, "HelpMenu.About_Label", "HelpMenu.About_Mnemonic",
				KeyStroke.getKeyStroke( "F1" ), new AboutAction() );

		getFrame().setJMenuBar( menubar );

		textarea = new JTextPane();
		textarea.setEditorKit( new StyledEditorKit() );
		JScrollPane scrollpane = new JScrollPane( textarea );
		getContentPane().add( scrollpane, BorderLayout.CENTER );
		textarea.addMouseListener( new PopupAdapter() );


		statusBar = new JLabel( "http://jon.boyce.net" );
		getContentPane().add( statusBar, BorderLayout.SOUTH );

		popupMenu = new JPopupMenu();

		createPopupMenuItem( popupMenu, "EditMenu.Cut_Label", "EditMenu.Cut_Mnemonic",
				new DefaultEditorKit.CutAction() );

		createPopupMenuItem( popupMenu, "EditMenu.Copy_Label", "EditMenu.Copy_Mnemonic",
				new DefaultEditorKit.CopyAction() );

		createPopupMenuItem( popupMenu, "EditMenu.Paste_Label", "EditMenu.Paste_Mnemonic",
				new DefaultEditorKit.PasteAction() );

		popupMenu.addSeparator();

		createPopupMenuItem( popupMenu, "EditMenu.SelectAll_Label", null,
				new SelectAllAction() );

		popupMenu.pack();


	}

	public JMenu createMenu( JMenuBar parent, String label, String mnemonic )
	{
		JMenu menu = (JMenu) parent.add( new JMenu( getString( label ) ) );
		if ( mnemonic != null )
		{
			menu.setMnemonic( getMnemonic( mnemonic ) );
		}
		return menu;
	}

	public JMenu createSubMenu( JMenu parent, String label, String mnemonic )
	{
		JMenu menu = (JMenu) parent.add( new JMenu( getString( label ) ) );
		if ( mnemonic != null )
		{
			menu.setMnemonic( getMnemonic( mnemonic ) );
		}
		return menu;
	}

	public JMenuItem createMenuItem( JMenu parent, String label, String mnemonic,
	                                 KeyStroke accelerator, Action action )
	{
		JMenuItem menuItem = (JMenuItem) parent.add( new JMenuItem( getString( label ) ) );
		if ( accelerator != null )
		{
			menuItem.setAccelerator( accelerator );
		}
		if ( action != null )
		{
			menuItem.addActionListener( action );
		}
		if ( mnemonic != null )
		{
			menuItem.setMnemonic( getMnemonic( mnemonic ) );
		}
		return menuItem;
	}

	public JMenuItem createPopupMenuItem( JPopupMenu parent, String label, String mnemonic,
	                                      Action action )
	{
		JMenuItem menuItem = (JMenuItem) parent.add( new JMenuItem( getString( label ) ) );
		if ( action != null )
		{
			menuItem.addActionListener( action );
		}
		if ( mnemonic != null )
		{
			menuItem.setMnemonic( getMnemonic( mnemonic ) );
		}
		return menuItem;
	}

	public JMenuItem createMenuItem( JMenu parent, String label, Action action )
	{
		JMenuItem menuItem = (JMenuItem) parent.add( new JMenuItem( label ) );
		if ( action != null )
		{
			menuItem.addActionListener( action );
		}
		return menuItem;
	}


	public char getMnemonic( String key )
	{
		return ( getString( key ) ).charAt( 0 );
	}

	public Container getContentPane()
	{
		return mainFrame.getContentPane();
	}

	public JFrame getFrame()
	{
		return mainFrame;
	}

	public JTextPane getTextPane()
	{
		return textarea;
	}

	public JMenuBar getMenuBar()
	{
		return menubar;
	}

	public JPopupMenu getPopupMenu()
	{
		return popupMenu;
	}

	public JWindow getSplashScreen()
	{
		return splash;
	}

	public JLabel getStatusBar()
	{
		return statusBar;
	}

	public SplashScreenTest getInstance()
	{
		return this;
	}

	public IPropertyResourceBundle getResBundle()
	{
		return this;
	}

/*
  private final String NO_STR_MSG = "Couldn't find value for ";

	public String getString( String key )
	{
		String value = null;
		try
		{
			value = getResourceBundle().getString( key );
			value = new String(value.getBytes( "ISO-8859-1" ), "UTF-8");
		}
		catch ( MissingResourceException e )
		{
			System.out.println( "java.util.MissingResourceException: Couldn't find value for " + key );
		}
		catch( UnsupportedEncodingException uce )
		{
			System.out.println( "java.io.UnsupportedEncodingException: Couldn't convert value for " + key );
		}
		if ( value == null )
		{
			value = NO_STR_MSG + key;
		}
		return value;
	}

	public boolean hasKey( String key )
	{
		try
		{
			String str = getResourceBundle().getString( key );
      return !str.startsWith( NO_STR_MSG );
		}
		catch(Exception ex)
		{
			return false;
		}
	}
*/

	public ResourceBundle getResourceBundle()
	{
		if ( bundle == null )
		{
			bundle = ResourceBundle.getBundle( "ru.ts.gui.elements.resources.jse" );
		}
		return bundle;
	}

	public static void main( String args[] )
	{
		String vers = System.getProperty( "java.version" );
		if ( vers.compareTo( "1.1.2" ) < 0 )
		{
			System.out.println( "!!!WARNING: This application must be run with a " +
					"1.1.2 or higher version VM!!!" );
			System.exit( 0 );
		}

		GuiUtils.setSystemLookAndFeel();

		new SplashScreenTest();
	}

	class ExitAction extends AbstractAction
	{
		public void actionPerformed( ActionEvent e )
		{
			System.exit( 0 );
		}
	}

	class SelectAllAction extends AbstractAction
	{
		public void actionPerformed( ActionEvent e )
		{
			getTextPane().selectAll();
		}
	}

	class OpenAction extends AbstractAction
	{

		FileDialog fileDialog;

		public void actionPerformed( ActionEvent e )
		{
			JFrame frame = getFrame();
			if ( fileDialog == null )
			{
				fileDialog = new FileDialog( frame );
			}
			fileDialog.setMode( FileDialog.LOAD );
			fileDialog.show();

			String file = fileDialog.getFile();
			if ( file == null )
			{
				return;
			}
			String directory = fileDialog.getDirectory();
			File f = new File( directory, file );
			if ( f.exists() )
			{
				getStatusBar().setText( "http://jon.boyce.net" );
				try
				{
					FileInputStream fin = new FileInputStream( f );
					EditorKit editor = getTextPane().getEditorKit();
					Document doc = editor.createDefaultDocument();
					editor.read( fin, doc, 0 );
					getTextPane().setDocument( doc );
					frame.setTitle( getString( "MainWin_Title" ) + " - [" + file + "]" );
					fin.close();
				}
				catch ( Exception ex )
				{
					System.err.println( "Exception: " + ex.getMessage() );
				}
			}
			else
			{
				getStatusBar().setText( "No such file: " + f );
			}
		}
	}

	class SaveAction extends AbstractAction
	{

		FileDialog fileDialog;

		public void actionPerformed( ActionEvent e )
		{
			JFrame frame = getFrame();
			if ( fileDialog == null )
			{
				fileDialog = new FileDialog( frame );
			}
			fileDialog.setMode( FileDialog.SAVE );
			fileDialog.show();
			String file = fileDialog.getFile();
			if ( file == null )
			{
				return;
			}
			String directory = fileDialog.getDirectory();
			File f = new File( directory, file );
			try
			{
				FileOutputStream fout = new FileOutputStream( f );
				EditorKit editor = getTextPane().getEditorKit();
				Document doc = getTextPane().getDocument();
				editor.write( fout, doc, 0, doc.getLength() );
				fout.close();
				getStatusBar().setText( "File saved: " + f );
			}
			catch ( Exception ex )
			{
				System.err.println( "Exception: " + ex );
				getStatusBar().setText( "Error Saving File: " + f );
			}
		}
	}

	class NewAction extends AbstractAction
	{
		public void actionPerformed( ActionEvent e )
		{
			EditorKit editor = textarea.getEditorKit();
			Document doc = editor.createDefaultDocument();
			getTextPane().setDocument( doc );
			getFrame().setTitle( getString( "MainWin_Title" ) + " - [Untitled]" );
		}
	}

	class AboutAction extends AbstractAction
	{
		public void actionPerformed( ActionEvent e )
		{
			final AboutDialog ad = new AboutDialog( getFrame(), getResBundle(), "resources/images/splash.gif" );
			ad.requestFocusInWindow();
			ad.setVisible( true );
		}
	}

	class PopupAdapter extends MouseAdapter
	{

		public void mousePressed( MouseEvent e )
		{
			showPopupMenu( e );
		}

		public void mouseReleased( MouseEvent e )
		{
			showPopupMenu( e );
		}

		public void showPopupMenu( MouseEvent e )
		{
			if ( e.isPopupTrigger() )
			{
				getPopupMenu().show( e.getComponent(),
						e.getX(), e.getY() );
			}
		}

	}
}
