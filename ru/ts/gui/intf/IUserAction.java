/**
 * Created on 17.09.2008 16:37:54 by Syg for project in
 * 'ru.ts.gui'
 */
package ru.ts.gui.intf;

import javax.swing.AbstractButton;

/**
 * Works for input command checking and managing
 */
public interface IUserAction
{

	/**
	 * Gets unique command name for this action
	 * @return String with command name. Is should be an unique name among all different actions
	 */
	public String getCommand();

	/**
	 * Adds one more component under action control
	 * @param comp AbstractButton for handling on action
	 * @return true if component was added, false if component already
	 * registered or null
	 */
	public boolean addControl( AbstractButton comp);

	/**
	 * Removes control from this action control
	 * @param comp AbstractButton to remove from control
	 * @return true if component was removed successfully
	 * or flase if no suc vcomponent found or null was designated
	 */
	public boolean removeControl( AbstractButton comp );
}
