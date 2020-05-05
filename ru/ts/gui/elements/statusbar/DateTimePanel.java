package ru.ts.gui.elements.statusbar;

import ru.ts.gui.intf.IDTPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;

/**
 * <pre>
 * Class name: DateTimePanel
 * Created by SYGSKY for package ru.ts.gui.elements.statusbar
 * Date: 24.01.2011
 * Time: 16:02:07
 * <p/>
 * ... To change this template use File | Settings | File Templates ...
 * <p/>
 * Changes:
 * </pre>
 */
public class DateTimePanel extends JSBPanel implements IDTPanel
{
  private DateTimer m_timer;
  int m_mode;
  int m_stop;

  public DateTimePanel( JStatusBar sb, String name, Icon icon,
                        int panelMode )
  {
    this( sb, name, icon, panelMode, null );
    switch( panelMode )
    {
	    default:
      case IDTPanel.PANEL_WITH_DATE_ONLY:
        setToolTip( "Дата" );
        break;
      case IDTPanel.PANEL_WITH_DATETIME:
        setToolTip( "Дата и время суток" );
        break;
    }
  }

	public DateTimePanel( JStatusBar sb, String name, Icon icon,
	                      int panelMode, String TooltipText )
	{
	  super( sb, name );
	  String mask;
	  String toolTip;
	  int stop;
	  switch( panelMode )
	  {
		  default:
	    case IDTPanel.PANEL_WITH_DATE_ONLY:
	      mask = IDTPanel.TEXT_MASK_DATE_ONLY;
	      stop = Calendar.HOUR;
	      break;
	    case IDTPanel.PANEL_WITH_DATETIME:
	      mask = IDTPanel.TEXT_MASK_DATETIME;
	      stop = Calendar.MILLISECOND;
	      break;
	  }
	  setTextWidth( mask );
	  setTextColor( Color.BLACK );
	  setIcon( icon );
	  setMode( panelMode, mask, TooltipText );
	  m_stop = stop;
	  // will start activity on adding to the status bar
	}

  public synchronized boolean isRunning()
  {
    return m_timer != null;
  }

  public synchronized void start()
  {
    if ( !isRunning() )
    {
      m_timer = new DateTimer( this );
      m_timer.start();
    }
  }

  public synchronized void stop()
  {
    if ( !isRunning() )
      return;
    m_timer.finish();
    try
    {
      m_timer.join();
    }
    catch ( InterruptedException e )
    {
      e.printStackTrace();
    }
    m_timer = null;
  }

  public void setMode( int pMode, String textMask,
                       String toolTip )
  {
    m_mode = pMode;
    switch ( pMode )
    {
      case DateTimePanel.PANEL_WITH_DATE_ONLY:
        m_stop = Calendar.HOUR;
      break;

      default:
      case DateTimePanel.PANEL_WITH_DATETIME:
        m_stop = Calendar.MILLISECOND;
      break;
    }
    setTextWidth( textMask );
    setToolTip( toolTip );
  }

  public int getMode()
  {
    return m_mode;
  }
}
