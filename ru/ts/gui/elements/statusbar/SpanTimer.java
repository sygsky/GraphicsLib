package ru.ts.gui.elements.statusbar;

import ru.ts.common.misc.TimeSpan;
import ru.ts.gui.intf.IStatusBar;

import java.util.Calendar;

public class SpanTimer extends Thread
{
  boolean m_do;

  private final IStatusBar.ISBPanel m_timePanel;
  private TimeSpanPanel m_panel;

  public SpanTimer( TimeSpanPanel timePnl )
  {
    m_panel = timePnl;
    m_do = true;
    m_timePanel = timePnl;
  }

  public void run()
  {
    while ( m_do )
    {
      if ( isInterrupted() )
        break;
      try
      {
        sleep( 1000 );
      }
      catch ( InterruptedException e )
      {
        break;
      }
      // print time
      final TimeSpan ts = new TimeSpan( m_panel.m_start );
      final String txt = ts.subtract().toString( Calendar.SECOND );
      m_timePanel.setText( txt );
    }
  }

  public void finish()
  {
    m_do = false;
    m_panel.setToolTip( "Время исполнения. Остановлено." );

    //this.notify();
  }
}