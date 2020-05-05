package ru.ts.gui;

import ru.ts.common.misc.Files;
import ru.ts.common.misc.Text;
import ru.ts.gui.intf.IUserAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;

/**
 * Created 15.09.2008 17:38:27 by Syg for the "JavaAWTUITest" project
 */

/**
 * Resource loader/creator. Resources are icon, menu items, buttons etc
 *
 * @author Syg
 */
public class GUIFactory
{
	public static boolean verbose = false;
	/**
	 * Maximum icon square to store in the internal cache
	 */
	private static final int MAX_ICON_AREA = 64 * 64;

	/**
	 * Maximum icon cache size to use for icons
	 */
	private static final int MAX_CACHE_SIZE = 64;

	/**
	 * Base package for the resorces searching
	 */
	private static Class _cls;

	/**
	 * Default images directory in the [JAR] package
	 */
	private String _dir;

	/**
	 * Images small cache
	 */
	private IconCacheMap<String, ImageIcon> _icons;

	/**
	 * Default insets for gui components
	 */
	private Insets _insets;

	/**
	 * Defines if we should use text for tool bar {@code true} or not {@code
	 * false}
	 */
	private boolean _use_text_on_tb = false;

  private AbstractButton m_lastButtonAdded;
  private AbstractButton m_lastMenuAdded;

	public Insets get_insets()
	{
		return _insets;
	}

	public boolean getUseToolText()
	{
		return _use_text_on_tb;
	}

	public void set_insets( Insets insets )
	{
		this._insets = insets;
	}

	/**
	 * Sets use of toolbar text
	 * @param use_tool_text if {@code true} toolbar accept text and icons, if {@code false} only icons are used
	 */
	public void setUseTextOnTB( boolean use_tool_text )
	{
		_use_text_on_tb = use_tool_text;
	}

  public AbstractButton getLastMenuAdded()
  {
    return m_lastMenuAdded;
  }

  public AbstractButton getLastButtonAdded()
  {
    return m_lastButtonAdded;
  }

	/**
	 * Fill existing {@link Action} with designated attributes
	 * @param action existing {@link javax.swing.Action}. If {@code null} pointed, method immediatelly return {@code false}
	 * @param name name of button or menu, may be null
	 * @param tooltip  tooltip text, may be null
	 * @param command command identifing the action among other, may be null
	 * @param smallIcon icon for component, supporting this action, may be null
	 * @param ks {@link KeyStroke} instance to tie with action. May be {@code null}
	 * @param actionId Integer id (MUST BE unique for any action)
	 * @return {@code true} if action exists, {@code false} if not exists
	 */
	public static boolean setActionProps( Action action, String name, String tooltip, String command, ImageIcon smallIcon, KeyStroke ks, int actionId )
	{
		if ( action == null )
			return false;
		if ( Text.notEmpty(name) )
			action.putValue( Action.NAME, name );
		if ( Text.notEmpty( tooltip ))
			action.putValue( Action.SHORT_DESCRIPTION, tooltip );
		if ( Text.notEmpty( command ))
			action.putValue( Action.ACTION_COMMAND_KEY, command );
		if ( smallIcon != null )
			action.putValue( Action.SMALL_ICON, smallIcon );
		if ( ks != null )
			action.putValue( Action.ACCELERATOR_KEY, ks );
		action.putValue( "ID", actionId );
		return true;
	}

	private class IconCacheMap<String, IconImage> extends LinkedHashMap<String, IconImage>
	{
		@Override
		protected boolean removeEldestEntry( final Map.Entry eldest )
		{
			// is it time to remove eldest icon from a cache?
			return this.size() >= GUIFactory.MAX_CACHE_SIZE;
		}
	}

	/**
	 * Restricts user access to this constructor
	 */
	private GUIFactory()
	{
		_icons = new IconCacheMap<String, ImageIcon>();
		//_useFlatButtonStyle = false;
	}

	/**
	 * Creates the factory with an initial images directory in the package
	 *
	 * @param cls     class in which package resource directory is searched for
	 *                icons as class loaded do it for a class
	 * @param img_dir relative to cls image directory to search for image
	 *                resources. That is img_dir should be sub-directory for the
	 *                class path
	 */
	private GUIFactory( Class cls, String img_dir )
	{
		this( img_dir );
		_cls = cls;
	}

	/**
	 * Creates the factory with an initial images directory in the package
	 *
	 * @param img_dir full path to the image directory to load icons from the file system,
	 *                not resourses
	 */
	private GUIFactory( String img_dir )
	{
		this();
		set_img_dir( Files.resetDir( img_dir ) );
		set_insets( Constants.INSETS_ZERO );
	}

	/* cache for the existing factories */
	private static java.util.HashMap<String, GUIFactory> _facts = new HashMap<String, GUIFactory>();

	/**
	 * Returns the factory with a initial images directory in the package
	 *
	 * @param cls     class in which package resource directory is searched for
	 *                icons
	 * @param img_dir image directory to search for image resources
	 * @return GUIFactory instance responsible for the image handling of designated
	 *         'img_dir'
	 */
	public static synchronized GUIFactory getInstance( Class cls, String img_dir )
	{
		/* Prepare full name of the image directory */
		String name = (cls.getName() + img_dir).toLowerCase();
		/* check for the live factory with a name in our cache */
		GUIFactory fact = _facts.get( name );
		if ( fact == null )
		{
			/* No such factory, create and add it to the cache */
			fact = new GUIFactory( cls, img_dir );
			_facts.put( name, fact );
		}
		/* return new or found factory instance */
		return fact;
	}

	/**
	 * Returns the factory with an initial images directory in the package
	 *
	 * @param abs_img_dir image directory to search for image resources. If you
	 *                    plan to use JARs for image storing, be carefull to use
	 *                    correct char case or it will not work correctly
	 * @return GUIFactory instance responsible for the image handling of designated
	 *         'abs_img_dir'
	 */
	public static synchronized GUIFactory getInstance( String abs_img_dir )
	{
		/* Prepare full name of the image directory */
		String name = abs_img_dir.toLowerCase();
		/* check for the live factory with a name in our cache */
		GUIFactory fact = _facts.get( name );
		if ( fact == null )
		{
			/* No such factory, create and add it to the cache */
			fact = new GUIFactory( null, abs_img_dir );
			_facts.put( name, fact );
		}
		/* return new or found factory instance */
		return fact;
	}

	private boolean could_fit( ImageIcon icon )
	{
		return icon.getIconHeight() * icon.getIconWidth() <= MAX_ICON_AREA;
	}

	/**
	 * Loads icon from image directory
	 *
	 * @param img_name image name with or without any directory prefix
	 * @return new Icon loaded of null if no such image
	 */
	public synchronized ImageIcon load_icon( String img_name )
	{
		return load_icon( img_name, null );
	}

	/**
	 * Loads {@link Image} from image directory
	 *
	 * @param img_name image name with or without any directory prefix
	 * @return new Icon loaded of null if no such image
	 */
	public synchronized Image load_image( String img_name )
	{
		return load_icon( img_name, null).getImage() ;
	}

	/**
	 * Virtual icon 16x16 with yellow square, black border and red X in the center
	 *
	 * @return {@link ImageIcon} instance ready to draw it onto any graphics
	 */
	public synchronized ImageIcon loadMissingIcon()
	{
		return MissingImageIcon.getMissingIconInstance( );
	}

	/**
	 * Loads icon from image directory
	 *
	 * @param img_name image name with or without any directory prefix
	 * @param alt_text short text description for the loading icon
	 * @return new Icon loaded of null if no such image
	 */
	public synchronized ImageIcon load_icon( String img_name, String alt_text )
	{
		if ( !Files.hasSepsInPath( img_name ) )
			img_name = _dir + img_name.toLowerCase();  // use only upper case strings
		else
		{
			char firstChar = img_name.charAt( 0 );
			if ( firstChar == '/' || firstChar == '\\')
				img_name = _dir + img_name.substring( 1 ).toLowerCase(  );
			else
				img_name = _dir + img_name.toLowerCase();
		}
		/**
		 * Find the icon in the cache
		 */
	  ImageIcon ico = _icons.get( img_name );
		if ( ico != null )
		{
			if ( verbose )
				log( MessageFormat.format( "Icon \"{0}\" got from a cache", img_name ) );

			// renew found item with reputting it to the cache
			
			_icons.put( img_name, ico );
			return ico;
		}
		
		if ( verbose )
		{
			if ( _cls == null )
			{
				log( "load_icon->getSystemResources(" + img_name + ")" );
			}
			else
			{
				log( _cls + ".getResources(" + img_name + ")" );
			}
		}

		URL url = _cls == null ?
						ClassLoader.getSystemResource( img_name ) :
						_cls.getResource( img_name );

		if ( url == null )
		{
			// try to load as a file
			final File file = new File( img_name );
			if ( file.exists() )
			{
				try
				{
					url = new URL( "file:" + img_name );
				}
				catch ( MalformedURLException e )
				{
					url = null;
				}
			}
			if ( url == null )
			{
				// try to load "warning" icon except requested one to signal user
				// about absence of it
				ico = MissingImageIcon.getMissingIconInstance( );
			}
		}

		if ( ico == null )
		{
			if ( Text.isEmpty( alt_text ) )
			{
				ico = new ImageIcon( url );
			}
			else
			{
				ico = new ImageIcon( url, alt_text );
			}
		}

		if ( could_fit( ico ) ) /* cache accepts icons of such size */
		{
			/* and now put icon to the cache */
			_icons.put( img_name, ico );
			if ( verbose )
				log( MessageFormat.format( "Icon \"{0}\" stored in the cache", img_name ) );
		}
		else if ( verbose )
			log( MessageFormat.format( "Icon \"{0}\" not fit to the cache", img_name ) );
		return ico;
	}

	/**
	 * Sets the image loading directory (among the running jar). Keep the case of
	 * the characters - it is <b>very</b> important if working with images from
	 * JARs
	 *
	 * @param img_dir directory in jar to find image without directory
	 */
	private void set_img_dir( String img_dir )
	{
		_dir = Files.resetDir( img_dir ).replace( '\\', '/' ).toLowerCase();
	}

	public String get_img_dir()
	{
		return _dir;
	}

	/**
	 * Creates JMenuItem with designated user parameters, including:
	 *
	 * @param img       the Icon instance, icon file name, or null
	 * @param text      text for the menu item
	 * @param keystroke menu keystroke for the menu item
	 *                  You can get key strokes as follow:<br>
	 *                  {@code KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.ALT_MASK)}<br>
	 *                  for ALT/X combination as example
	 * @param tooltip   tool tips for this menu
	 * @param a         action to execute
	 * @param acommand  String with action command
	 * @return JMenuItem instance created or null on any error
	 */
	public JMenuItem load_menu_item( Object img, String text, KeyStroke keystroke,
	                                 String tooltip, Action a, String acommand )
	{
		return (JMenuItem) make_abutton( new JMenuItem( a ), img, text,
						keystroke, tooltip, null, acommand );
	}

	/**
	 * Creates JMenuItem with designated user parameters, including:
	 *
	 * @param img       the Icon instance, icon file name, or null
	 * @param text      text for the menu item
	 * @param keystroke menu keystroke for the menu item
	 *                  You can get key strokes as follow:<br>
	 *                  {@code KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.ALT_MASK)}<br>
	 *                  for ALT/X combination as example
	 * @param tooltip   tool tips for this menu
	 * @param al         ActionListener to execute
	 * @param acommand  String with action command
	 * @return JMenuItem instance created or null on any error
	 */
	public JMenuItem load_menu_item( Object img, String text, KeyStroke keystroke,
	                                 String tooltip, ActionListener al, String acommand )
	{
		return (JMenuItem) make_abutton( new JMenuItem(), img, text,
						keystroke, tooltip, al, acommand );
	}

	/**
	 * Creates and loads check box menu (JCheckBoxMenu) for the user needs
	 *
	 * @param img       icon image or icon path (may be relative for this factory)
	 *                  or <code>null</code> if absence
	 * @param pimg      pressed icon totally same as for the img parameter
	 * @param text      text for the menu
	 * @param keystroke KeyStroke for the menu
	 * @param tooltip   tooltip text
	 * @param a         Action instance to connect to or <null>null</null> iа no
	 *                  such one
	 * @param acommand  command string
	 * @return JCheckBoxMenuItem instance generated
	 */
	public JCheckBoxMenuItem load_cbmenu_item( Object img, Object pimg, String text, KeyStroke keystroke,
	                                           String tooltip, Action a, String acommand )
	{
		JCheckBoxMenuItem mnu = (JCheckBoxMenuItem) make_abutton( new JCheckBoxMenuItem( a ), img, text,
						keystroke, tooltip, null, acommand );
		Icon icon = img == null ? null // null ?
						: (img instanceof String ? load_icon( (String) pimg )
						: (img instanceof Icon ? (Icon) pimg : null));
		if ( icon != null )
		{
			mnu.setPressedIcon( icon );
		}
		return mnu;
	}

	/**
	 * Creates and loads check box menu (JCheckBoxMenu) for the user needs
	 *
	 * @param img       icon image or icon path (may be relative for this factory)
	 *                  or <code>null</code> if absence
	 * @param pimg      pressed icon totally same as for the img parameter
	 * @param text      text for the menu
	 * @param keystroke KeyStroke for the menu
	 * @param tooltip   tooltip text
	 * @param a         ActionListener instance to load to or <null>null</null> if
	 *                  no such one
	 * @param acommand  command string
	 * @return JCheckBoxMenuItem instance generated
	 */
	public JCheckBoxMenuItem load_cbmenu_item( Object img, Object pimg, String text, KeyStroke keystroke,
	                                           String tooltip, ActionListener a, String acommand )
	{
		JCheckBoxMenuItem mnu = (JCheckBoxMenuItem) make_abutton( new JCheckBoxMenuItem(), img, text,
						keystroke, tooltip, null, acommand );
		Icon icon = img == null ? null // null ?
						: (img instanceof String ? load_icon( (String) pimg )
						: (img instanceof Icon ? (Icon) pimg : null));
		if ( icon != null )
		{
			mnu.setPressedIcon( icon );
		}
		if ( a != null )
		{
			mnu.addActionListener( a );
		}
		return mnu;
	}

	/**
	 * Creates AbstractButton with designated user parameters, including:
	 *
	 * @param ab        inherited for the AbstractButton class instance to fill
	 *                  with attributes
	 * @param img       the Icon instance, icon file name, or null
	 * @param text      text for the menu item
	 * @param keystroke menu keystroke for the menu item
	 *                  You can get key strokes as follow:<br>
	 *                  {@code KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.ALT_MASK)}<br>
	 *                  for ALT/X combination as example
	 * @param tooltip   tool tips for this menu
	 * @param a         action to execute
	 * @param acommand  String with action command
	 * @return AbstractButton instance created or null on any error
	 */
	public AbstractButton make_abutton( AbstractButton ab, Object img,
	                                    String text, KeyStroke keystroke, String tooltip, Action a,
	                                    String acommand )
	{
		return make_abutton( ab, img, null, text, keystroke, tooltip, a, acommand);
	}

	/**
	 * Creates AbstractButton with designated user parameters, including:
	 *
	 * @param ab        inherited for the AbstractButton class instance to fill
	 *                  with attributes
	 * @param img       the Icon instance, icon file name, or null
	 * @param pimg      the Icon instance, icon file name, or null for pressed icon image
	 * @param text      text for the menu item
	 * @param keystroke menu keystroke for the menu item
	 *                  You can get key strokes as follow:<br>
	 *                  {@code KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.ALT_MASK)}<br>
	 *                  for ALT/X combination as example
	 * @param tooltip   tool tips for this menu
	 * @param a         action to execute
	 * @param acommand  String with action command
	 * @return AbstractButton instance created or null on any error
	 */
	public AbstractButton make_abutton( AbstractButton ab, Object img, Object pimg,
	                                    String text, KeyStroke keystroke, String tooltip, Action a,
	                                    String acommand )
	{
		if ( a != null )
		{
			ab.setAction( a );
		}
		Icon icon;
		icon = img == null ? null // null ?
						: (img instanceof String ? load_icon( (String) img )
						: (img instanceof Icon ? (Icon) img : null));
		if ( icon != null )
		{
			ab.setIcon( icon );
		}
		icon = pimg == null ? null // null ?
						: (pimg instanceof String ? load_icon( (String) pimg )
						: (pimg instanceof Icon ? (Icon) pimg : null));
		if ( icon != null )
		{
			ab.setPressedIcon( icon );
		}
		/* Set text by user request, so text may be null */
		if ( Text.notEmpty( text) )
			ab.setText( text );
		/* Sets accelerator only for menu items, not for any button */
		if ( keystroke != null && ab instanceof JMenuItem )
		{
			((JMenuItem) ab).setAccelerator( keystroke );
		}
		/* Always set tooltip */
		if ( Text.notEmpty(tooltip) )
			ab.setToolTipText( tooltip );
		/* Set action command, couldn't be empty as used to identify the commad in event handling */
		if ( Text.notEmpty( acommand) )
			ab.setActionCommand( acommand );
		ab.setMargin( _insets );
		ab.setHideActionText( _use_text_on_tb );
		return ab;
	}

	/**
	 * Creates AbstractButton with designated user parameters, including:
	 *
	 * @param ab        inherited for the AbstractButton class instance to fill
	 *                  with attributes
	 * @param img       the Icon instance, icon file name, or null
	 * @param text      text for the menu item
	 * @param keystroke menu keystroke for the menu item
	 *                  You can get key strokes as follow:<br>
	 *                  {@code KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.ALT_MASK)}<br>
	 *                  for ALT/X combination as example
	 * @param tooltip   tool tips for this menu
	 * @param al         ActionListener instance to  use on execution
	 * @param acommand  String with action command
	 * @return AbstractButton instance created or null on any error
	 */
	public AbstractButton make_abutton( AbstractButton ab, Object img,
	                                    String text, KeyStroke keystroke, String tooltip, ActionListener al,
	                                    String acommand )
	{
		return make_abutton( ab, img, null, text, keystroke, tooltip, al, acommand );
	}

	/**
	 * Creates AbstractButton with designated user parameters, including:
	 *
	 * @param ab        inherited for the AbstractButton class instance to fill
	 *                  with attributes
	 * @param img       the Icon instance, icon file name, or null
	 * @param pimg      pressed {@link Icon} instance, file name or {@code null} if not needed
	 * @param text      text for the menu item
	 * @param keystroke menu keystroke for the menu item
	 *                  You can get key strokes as follow:<br>
	 *                  {@code KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.ALT_MASK)}<br>
	 *                  for ALT/X combination as example
	 * @param tooltip   tool tips for this menu
	 * @param al         ActionListener instance to  use on execution
	 * @param acommand  String with action command
	 * @return AbstractButton instance created or null on any error
	 */
	public AbstractButton make_abutton( AbstractButton ab, Object img, Object pimg,
	                                    String text, KeyStroke keystroke, String tooltip, ActionListener al,
	                                    String acommand )
	{
		if ( al != null )
		{
			ab.addActionListener( al );
		}
		Icon icon;
		icon = img == null ? null // null ?
						: (img instanceof String ? load_icon( (String) img )
						: (img instanceof Icon ? (Icon) img : null));
		if ( icon != null )
		{
			ab.setIcon( icon );
		}
		icon = pimg == null ? null // null ?
						: (pimg instanceof String ? load_icon( (String) pimg )
						: (pimg instanceof Icon ? (Icon) pimg : null));
		if ( icon != null )
		{
			ab.setPressedIcon( icon );
		}
		/* Set text by user request, so text may be null */
		if ( Text.notEmpty(text) )
			ab.setText( text );
		/* Sets accelerator only for menu items, not for any button */
		if ( keystroke != null && ab instanceof JMenuItem )
		{
			((JMenuItem) ab).setAccelerator( keystroke );
		}
		/* Always set tooltip */
		if ( Text.notEmpty(tooltip) )
			ab.setToolTipText( tooltip );
		/* Set action command, couldn't be empty as used to idetify the command in event handling */
		if ( Text.notEmpty( acommand ) )
			ab.setActionCommand( acommand );
		ab.setMargin( _insets );
		ab.setHideActionText( _use_text_on_tb );
		return ab;
	}

	/**
	 * Creates JButton with designated user parameters, including:
	 *
	 * @param img      the Icon instance, icon file name, or null
	 * @param text     text for the menu item
	 * @param tooltip  tool tips for this menu
	 * @param a        action to execute
	 * @param acommand String with action command
	 * @return JButton instance created or null on any error
	 */
	public JButton load_button( Object img, String text,
	                            String tooltip, Action a, String acommand )
	{
		return (JButton) make_abutton( new JButton( a ), img, text, null,
						tooltip, null, acommand );
	}

	/**
	 * Creates JButton with designated user parameters, including:
	 *
	 * @param img      the Icon instance, icon file name, or null
	 * @param text     text for the menu item
	 * @param tooltip  tool tips for this menu
	 * @param a        action to execute
	 * @param acommand String with action command
	 * @return JButton instance created or null on any error
	 */
	public JButton load_button( Object img, String text,
	                            String tooltip, ActionListener a, String acommand )
	{
		return (JButton) make_abutton( new JButton(), img, text, null,
						tooltip, a, acommand );
	}

	/**
	 * Creates JToggleButton with designated user parameters, including:
	 *
	 * @param img      the Icon instance, icon file name, or null
	 * @param pimg     the pressed Icon instance, icon file name, or null
	 * @param text     text for the menu item, if icon exists, it will not be used
	 * @param tooltip  tool tips for this menu
	 * @param a        action to execute
	 * @param acommand String with action command
	 * @return JToggleButton instance created or null on any error
	 */
	public JToggleButton load_tbutton( Object img, Object pimg, String text,
	                                   String tooltip, Action a, String acommand )
	{
		Icon icon = img == null ? null // null ?
						: (img instanceof String ? load_icon( (String) img )
						: (img instanceof Icon ? (Icon) img : null));
		JToggleButton btn = (JToggleButton) make_abutton(
						new JToggleButton( a ), img, pimg, img == null ? text : null, null, tooltip, null,
						acommand );
		if ( icon != null )
		{
			btn.setPressedIcon( icon );
		}
		return btn;

	}

	/**
	 * Creates JToggleButton with designated user parameters, including:
	 *
	 * @param img      the Icon instance, icon file name, or null
	 * @param pimg     the pressed Icon instance, icon file name, or null
	 * @param text     text for the menu item, if icon exists, it will not be used
	 * @param tooltip  tool tips for this menu
	 * @param a        ActionListener to execute
	 * @param acommand String with action command
	 * @return JToggleButton instance created or null on any error
	 */
	public JToggleButton load_tbutton( Object img, Object pimg, String text,
	                                   String tooltip, ActionListener a, String acommand )
	{
		Icon icon = img == null ? null // null ?
						: (img instanceof String ? load_icon( (String) img )
						: (img instanceof Icon ? (Icon) img : null));
		JToggleButton btn = (JToggleButton) make_abutton(
						new JToggleButton(), img, pimg, img == null ? text : null, null, tooltip, a,
						acommand );
		if ( icon != null )
		{
			btn.setPressedIcon( icon );
		}
		return btn;

	}

	/**
	 * Inserts action to the program GUI
	 *
	 * @param bar       toolbar to insert into. May be null if no button is neeed
	 * @param btnClass  the button class in text form, could be "JToggleButton" or
	 *                  "JButton" only! Also you could designate "JToggleButtonDown"
	 *                  to start with button pushed down
	 * @param menu      menu to insert into, may be null if no menu item is needed
	 * @param mnuClass  the menu item class, could be "JMenuItem" or
	 *                  "JCheckBoxMenuItem" only!
	 * @param img       icon image
	 * @param pimg      pressed icon image
	 * @param text      main text for the action
	 * @param keystroke keyboard shortcut (ALT, CTRL, SHIFT are possible)
	 *                  You can get key strokes as follow:<br>
	 *                  {@code KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.ALT_MASK)}<br>
	 *                  for ALT/X combination as example
	 * @param tooltip   tooltip for the element
	 * @param a         action to assign
	 * @param acommand  action command
	 */
	public void insert_action( JToolBar bar, String btnClass, JMenu menu, String mnuClass, Object img,
	                           Object pimg, String text, KeyStroke keystroke, String tooltip, Action a,
	                           String acommand )
	{
		if ( a != null )
		{
			a.putValue( Action.NAME, acommand );
		}
		
		if ( (bar != null) && (!Text.isEmpty( btnClass )) )
		{
			AbstractButton btn = null;
			if ( btnClass.startsWith( "JToggleButton" ) )
			{
				m_lastButtonAdded = btn = load_tbutton( img, pimg, _use_text_on_tb ? text : null, tooltip, a, acommand );
				if ( btnClass.equals( "JToggleButtonDown" ) )
				{
					btn.setSelected( true );
				}
			}
			else if ( btnClass.equals( "JButton" ) )
			{
				m_lastButtonAdded = btn = load_button( img, _use_text_on_tb ? text : null, tooltip, a, acommand );
			}
			else
			{
				loge( "Button type expected to be \"JToggleButton[Down]\" or \"JButton\", but \"" + btnClass + "\" found" );
			}
			if ( btn != null )
			{
				bar.add( btn );
			}
		}

		if ( (menu != null) && (!Text.isEmpty( mnuClass )) )
		{
			AbstractButton mnu = null;
			if ( mnuClass.equals( "JMenuItem" ) )
			{
				m_lastMenuAdded = mnu = load_menu_item( img, text, keystroke, tooltip, a, acommand );
			}
			else if ( mnuClass.equals( "JCheckBoxMenuItem" ) )
			{
				m_lastMenuAdded = mnu = load_cbmenu_item( img, pimg, text, keystroke, tooltip, a, acommand );
			}
			else
			{
				loge( "Button type expected to be \"JMenuItem\" or \"JCheckBoxMenuItem\", but \"" + mnuClass + "\" found" );
			}
			if ( mnu != null )
			{
				menu.add( mnu );
			}
		}
	}

	/**
	 * Simple button of class {@link JButton} loader only for toolbars
	 *
	 * @param bar      {@link JToolBar} instance
	 * @param img      icon image
	 * @param al       {@link ActionListener} instance to use
	 * @param tooltip  tooltip for the element
	 * @param acommand action command
	 */
	public void insert_button( JToolBar bar, Icon img, ActionListener al,
	                           String tooltip, String acommand )
	{
		final JButton btn = new JButton( img );
		btn.setMargin( get_insets() );
		btn.setToolTipText( tooltip );
		btn.setActionCommand( acommand );
		btn.addActionListener( al );
		bar.add( btn );
	}

	/**
	 * Inserts action to the program GUI
	 *
	 * @param bar       toolbar to insert into. May be null if no button is neeed
	 * @param btnClass  the button class in text form, could be "JToggleButton" or
	 *                  "JButton" only! Also you can designate "JToggleButtonDown",
	 *                  in this case toggle button will be created in the pushed state
	 *                  (down position)
	 * @param menu      menu to insert into, may be null if no menu item is needed
	 * @param mnuClass  the menu item class, could be "JMenuItem" or
	 *                  "JCheckBoxMenuItem" only!
	 * @param img       icon image
	 * @param pimg      pressed icon image
	 * @param text      main text for the action
	 * @param keystroke keyboard shortcut (ALT, CTRL, SHIFT are possible).
	 *                  You can get key strokes as follow:<br>
	 *                  {@code KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.ALT_MASK)}<br>
	 *                  for ALT/X combination as example
	 * @param tooltip   tooltip for the element
	 * @param a         ActionListener to assign
	 * @param acommand  action command
	 */
	public void insert_action( JToolBar bar, String btnClass, JMenu menu, String mnuClass, Object img,
	                           Object pimg, String text, KeyStroke keystroke, String tooltip, ActionListener a,
	                           String acommand )
	{
		if ( (bar != null) && (!Text.isEmpty( btnClass )) )
		{
			AbstractButton btn = null;
			if ( btnClass.startsWith( "JToggleButton" ) )
			{
				m_lastButtonAdded = btn = load_tbutton( img, pimg, _use_text_on_tb ? text : null, tooltip, a, acommand );
				// push button down if user need it
				if ( btnClass.equals( "JToggleButtonDown" ) )
					btn.setSelected( true );
			}
			else if ( btnClass.equals( "JButton" ) )
			{
				m_lastButtonAdded = btn = load_button( img, _use_text_on_tb ? text : null, tooltip, a, acommand );
			}
			else
			{
				loge( "Button type expected to be \"JToggleButton[Down]\" or \"JButton\", but \"" + btnClass + "\" found" );
			}
			if ( btn != null )
			{
				bar.add( btn );
			}
		}
		if ( (menu != null) && (!Text.isEmpty( mnuClass )) )
		{
			AbstractButton mnu = null;
			if ( mnuClass.equals( "JMenuItem" ) )
			{
				m_lastMenuAdded = mnu = load_menu_item( img, text, keystroke, tooltip, a, acommand );
			}
			else if ( mnuClass.equals( "JCheckBoxMenuItem" ) )
			{
				m_lastMenuAdded = mnu = load_cbmenu_item( img, pimg, text, keystroke, tooltip, a, acommand );
			}
			else
			{
				loge( "Button type expected to be \"JMenuItem\" or \"JCheckBoxMenuItem\", but \"" + mnuClass + "\" found" );
			}
			if ( mnu != null )
			{
				menu.add( mnu );
			}
		}
	}

	/**
	 * Adds separator to the designated menu and tool bar simultaneously
	 *
	 * @param menu    menu to add separator
	 * @param toolbar to add separator
	 */
	public static void insert_separator( JMenu menu, JToolBar toolbar )
	{
		if ( menu != null )
		{
			menu.addSeparator();
		}
		if ( toolbar != null )
		{
			toolbar.addSeparator();
		}
	}

	private static java.util.HashMap<String, IUserAction> _set = new HashMap<String, IUserAction>();

	/**
	 * Tries to  register action in an internal registry. If action with such name is
	 * registed, nothing occures
	 *
	 * @param user_action new named user action to register
	 * @return true if was registed successfully, else false
	 */
	public static boolean registerAction( IUserAction user_action )
	{
		if ( _set.containsKey( user_action.getCommand() ) )
		{
			return false;
		}
		_set.put( user_action.getCommand(), user_action );
		return true;
	}

	/**
	 * Checks if user action with the the predefined name is already registered in
	 * the user action registry
	 *
	 * @param user_action IUserAction to check
	 * @return true if ai already registered, false if still not registered
	 */
	public static boolean isRegistered( IUserAction user_action )
	{
		return _set.containsKey( user_action.getCommand() );
	}

	/**
	 * Tries to remove IUserAction instance from the registry
	 *
	 * @param user_action IUserAction to remove. If such action is in registry it
	 *                    is removed else nothing occures
	 */
	public static void unregisterAction( IUserAction user_action )
	{
		_set.remove( user_action.getCommand() );
	}

	private static void log( String msg )
	{
		System.out.println( msg );
	}

	private static void loge( String msg )
	{
		System.err.println( msg );
	}

	/**
	 * Test for resources loading
	 *
	 * @param args arguments of the  command line
	 */
	/*
	public static void main( String[] args ) throws MalformedURLException
	{
		log(" Test GUIFactory ability to load from a directory");
		if ( args.length == 0)
		{
			log("Usage: main <image_directory> <img_1> ... <img_N>");
			return;
		}
		//GUIFactory.verbose = true; // inform on all steps
		GUIFactory gf = new GUIFactory( args[0] );
		Text.sout("GUIFactory loaded for \"{0}\"", args[0] );
		GuiUtils.setSystemLookAndFeel();
		for( int  i = 1; i < args.length; i++ )
		{
			ImageIcon ico = gf.load_icon( args[i] );
			if ( ico == null )
				JOptionPane.showMessageDialog( null, args[i], "Error load icon #" + i, JOptionPane.ERROR_MESSAGE );
			else
				JOptionPane.showMessageDialog( null, args[i], "Icon #" + i + " loaded", JOptionPane.ERROR_MESSAGE, ico );
		}
		// pereate the same text for the relative Java project image storage
		gf= new GUIFactory( "ru/ts/images");
		for( int  i = 1; i < args.length; i++ )
		{
			ImageIcon ico = gf.load_icon( args[i] );
			if ( ico == null )
				JOptionPane.showMessageDialog( null, args[i], "Error load icon #" + i, JOptionPane.ERROR_MESSAGE );
			else
				JOptionPane.showMessageDialog( null, args[i], "Icon #" + i + " loaded", JOptionPane.ERROR_MESSAGE, ico );
		}
	}
	*/
}
