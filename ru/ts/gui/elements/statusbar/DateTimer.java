package ru.ts.gui.elements.statusbar;

import ru.ts.common.misc.DateTime;
import ru.ts.common.misc.Text;
import ru.ts.common.misc.TimeSpan;

import java.util.Calendar;
import java.util.Date;

/**
 * <pre>
 * Class name: DateTimer
 * Created by SYGSKY for package ru.ts.gui.elements.statusbar
 * Date: 24.01.2011
 * Time: 16:31:40
 * <p/>
 * ... To change this template use File | Settings | File Templates ...
 * <p/>
 * Changes:
 * </pre>
 */
public class DateTimer extends Thread
{
  DateTimePanel m_dtp;
  private boolean m_do;

  public DateTimer( DateTimePanel dtp )
  {
    m_dtp = dtp;
    //m_stop = stop;
    m_do = true;
  }

  @Override
  public void run()
  {
    while ( m_do )
    {
      if ( isInterrupted() )
        break;
      // print time
      String txt = DateTime.now2StdString ( m_dtp.m_stop );
      m_dtp.setText( txt );
      try
      {
          sleep( 1000L ); // update each second
      }
      catch ( InterruptedException e )
      {
        break;
      }
    }
  }

  public void finish()
  {
    m_do = false;
  }

  public synchronized void wakeUpAll()
  {
    Text.sout( "Notifying" );
    this.notifyAll();
    Text.sout( "Notified" );
  }

}
