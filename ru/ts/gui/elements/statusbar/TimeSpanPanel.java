package ru.ts.gui.elements.statusbar;

import ru.ts.common.misc.Text;
import ru.ts.gui.intf.ITSPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class TimeSpanPanel extends JSBPanel implements ITSPanel
{
  SpanTimer m_spanTimerThread;
  long m_start;

	/**
	 * may include max length string of "[-]Dd hh:mm:ss.ff". See {@link ru.ts.common.misc.TimeSpan#toString(int)}  
 	 */

  static final String TIMESPAN_TEXT_MASK = "   18:19:12";

  //private TimeSpanPanel() {}

  public TimeSpanPanel( JStatusBar sb, final String panelName, final Icon icon,
                 Date start )
  {
    this( sb, panelName, icon, start, "Date/Time" );
  }
	public TimeSpanPanel( JStatusBar sb, final String panelName, final Icon icon,
	               Date start, String tooltipText )
	{
	  super( sb, panelName );
	  setTextWidth( TIMESPAN_TEXT_MASK );
	  setText( TIMESPAN_TEXT_MASK );
	  setTextColor( Color.BLACK );
	  setToolTip( tooltipText );
	  setIcon( icon );
	  if ( start == null )
	    m_start = System.currentTimeMillis();
	  else
	    m_start = start.getTime();
	  //start(); // will be started at the moment of the addition to the status bar
	}

  public synchronized void start()
  {
    if ( m_spanTimerThread != null )
      return;
    m_spanTimerThread = new SpanTimer( this );
	  if ( Text.isEmpty( super.getToolTip() ) )
      setToolTip( "Execution time span. Working." );
    m_spanTimerThread.start();
  }

  public synchronized void stop()
  {
    if ( m_spanTimerThread == null )
      return;
    m_spanTimerThread.finish();
    try
    {
      m_spanTimerThread.join();
    }
    catch ( InterruptedException e )
    {
      e.printStackTrace();
    }
    m_spanTimerThread = null;
  }

  public synchronized boolean isRunning()
  {
    return m_spanTimerThread != null;
  }
}