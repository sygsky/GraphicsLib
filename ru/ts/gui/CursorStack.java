package ru.ts.gui;

import java.awt.*;
import java.util.Stack;

/**
 * Created 12.09.2008 14:42:12
 * by Syg for the "JavaAWTUITest" project.
 *
 * Works for mouse cursor over form change/push/pop operation. For example if
 * you are doing some operation, you could want to change form cursor some times and
 * to forget about old cursor storing/restoring, you can use this class instance.
 *
 * @author Syg
 */
public class CursorStack extends Stack<Cursor>
{

	/**
	 * Driven component
	 */
	private Component _comp;
	/**
	 * Original cursor at the moment of the class instantiation
	 */
	private Cursor _orig_cursor;

	/**
	 * Disable default constructor access
	 */
	private CursorStack()
	{
	}

	/**
	 * Main constructor  to set component which cursor is handled by this class instance
	 *
	 * @param comp component to handle cursors
	 */
	public CursorStack( Component comp )
	{
		_comp = comp;
		_orig_cursor = comp.getCursor();
	}

	/**
	 * Gets served component
	 * @return Component instance which cursor is handled by this class instance
	 */
	public Component get_component()
	{
		return _comp;
	}

	/**
	 * Gets current cursor
	 *
	 * @return current Cursor for the controlled component
	 */
	public Cursor getCursor()
	{
		return _comp.getCursor();
	}

	/**
	 * Sets new cursor for the controlled component
	 *
	 * @param cursor new Cursor to set for the component
	 */
	public void setCursor( Cursor cursor )
	{
		if ( cursor == null )
			return;
		_comp.setCursor( cursor );
	}

	/**
	 * Sets new cursor for the controlled component
	 *
	 * @param cursor new Cursor to set for the component
	 */
	public void setCursor( int cursor )
	{
		try
		{
			setCursor( Cursor.getPredefinedCursor(cursor) );
		}
		catch( Exception ex ){};
	}
	/**
	 * Stores current cursor to the stack
	 */
	public void pushCursor()
	{
		push( getCursor() );
	}

	/**
	 * Stores current cursor to the stack
	 * @param newCursor
	 */
	public void pushCursor( Cursor newCursor )
	{
		if ( newCursor != null )
		{
			pushCursor();
			setCursor( newCursor );
		}
	}

	/**
	 * Stores current cursor to the stack
	 * @param predefined_cursor int constants from the {@link Cursor} class, for example
	 * {@link Cursor#CROSSHAIR_CURSOR} etc
	 */
	public void pushCursor( int predefined_cursor )
	{
		Cursor newCursor = null;
		try
		{
			newCursor = Cursor.getPredefinedCursor(predefined_cursor);
		}
		catch ( Exception e )
		{

		}
		if ( newCursor != null )
		{
			pushCursor();
			setCursor( newCursor );
		}
	}
	/**
	 * Restores previous cursor for the component
	 */
	public void popCursor()
	{
		if ( this.empty() )
		{
			_comp.setCursor( _orig_cursor );
			return;
		}
		_comp.setCursor( this.pop() );
	}


}
