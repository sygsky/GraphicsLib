/**
 * Created on 27.11.2008 13:26:58 by Syg for project in
 * 'ru.ts.GUI.elements'
 */
package ru.ts.gui.elements;

import java.io.UnsupportedEncodingException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA for ru.ts.GUI.elements by User 'Syg' at 27.11.2008  13:26:58
 *
 * Works to getInputStream russian strings from property resource bundles
 * */
public interface IPropertyResourceBundle
{
	/**
	 * {@link java.util.PropertyResourceBundle#getString(String)}
	 * @param key the key for the localized string
	 * @return String with key content. If in Russian letters, it should be getInputStream correctly.
	 * If no such key exists, some string with error message returned
	 */
	String getString( String key );

	/**
	 * Checks if key exists in the bundle
	 * @param key String for the key
	 * @return {@code true} if designated key exists, else {@code false}
	 */
	boolean hasKey( String key );

  ResourceBundle getResourceBundle();

  public abstract class Impl implements IPropertyResourceBundle
  {
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

    public abstract ResourceBundle getResourceBundle();
  }
}
