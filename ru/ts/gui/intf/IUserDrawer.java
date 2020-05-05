package ru.ts.gui.intf;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Syg
 * Date: 06.11.2008
 * Time: 13:23:18
 * <p/>
 * Used to help user to draw its graphics above the component one, called by any overrided method
 * of component to draw on its Graphics. Added by user method like <code>addUserDrawer( IUserDrawer )</code> etc
 */
public interface IUserDrawer
{
	/**
	 * Performes the user drawing onto Component graphics
	 * @param c Component, calling this method
	 * @param g Graphics to draw onto it an user data
	 */
	public void drawPerformed( Component c, Graphics g );
}
