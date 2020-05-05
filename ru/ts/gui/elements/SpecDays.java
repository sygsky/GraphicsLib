package ru.ts.gui.elements;

import ru.ts.common.misc.Text;
import ru.ts.common.misc.DateTime;
import ru.ts.common.misc.Sys;

import java.util.Date;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Syg
 * Date: 03.01.2014
 * Time: 15:38:29
 * Finds coincidence image to day of year. Get into attention if spec day will occure in weekends
 */
public class SpecDays {
  long[] m_dates;
  String[] m_dayNames;
  String[] m_iconNames;


  public SpecDays( String descr, char sep )
  {
    String[] items = Text.splitItems( descr, sep, true );
    int cnt = items.length;
    m_dates = new long[cnt];
    m_dayNames = new String[cnt];
    m_iconNames = new String[cnt];
    for ( int i = 0; i < cnt; i++ )
    {
      String[] args = Text.splitItems( items[ i ], ',', true );
      // check division by 3
      if ( args.length != 3 )
      {
        throw new IllegalArgumentException( "Number of params (" + args.length + ") in item " + i + " must be 3" );
      }
      m_dates[ i ] = DateTime.parseDate( args[ 0 ] ).getTime();
      m_dayNames[ i ] = args[ 1 ];
      m_iconNames[ i ] = args[ 2 ];
    }
  }

  public SpecDays( final long[] dates, final String[] dayNames, final String[] iconNames )
  {
    m_dates = dates;
    m_dayNames = dayNames;
    m_iconNames = iconNames;
  }

  public int todaySpecDayIndex()
  {
    return specDayIndexFor( DateTime.now() );
  }

  public int specDayIndexFor( final Date date )
  {
	  // DEBUG
/*
	  if ( true )
	    return Sys.getStaticRandom().nextInt( m_dates.length );
*/
    Calendar clnSeek = Calendar.getInstance();
    clnSeek.setTime( date );

    //int daysNow = clnSeek.get( Calendar.DAY_OF_YEAR );
    int weekday = clnSeek.get( Calendar.DAY_OF_WEEK );
    int year = clnSeek.get( Calendar.YEAR );
    Calendar clnSample = Calendar.getInstance();
    for ( int i = 0; i < m_dates.length; i++ )
    {
      clnSample.setTimeInMillis( m_dates[ i ] );
      clnSample.set( Calendar.YEAR, year );
      int diffDays = DateTime.diffInDays( clnSeek.getTime(), clnSample.getTime());
      if ( diffDays > 0 ) // this specday is before seek one
        continue;
      if ( diffDays == 0 ) // hit!!!
        return i;

      if ( ( weekday == Calendar.FRIDAY ) && ( ( diffDays == -1 ) || ( diffDays == -2 ) ) ) // spec day on weekend and now friday
        return i;
    }
    return -1;
  }

/*
    static String specsRus =
    0-12-31,Новый Год,/bg/coffee_time.png|0-1-1,Новый Год,/bg/coffee_time.png|0-01-13,Старый Новый Год,/bg/coffee_time.png|0-3-8,8 Марта,/bg/map2.png|0-4-01,День Дурака,/bg/mushroom.png|0-5-1,Первомай,/bg/map5.png|0-5-9,День Победы,/bg/map6.png|0-11-7,Наша революция,/bg/map3.png;0-12-31,New Year,/bg/coffee_time.png|0-1-1,New Year,/bg/coffee_time.png|0-3-8,USSR 8th March,/bg/map2.png|0-4-1,Foul Day,/bg/mushroom.png|0-5-1,USSR 1st May,/bg/map5.png|0-5-9,USSR WWII Victory Day,/bg/map6.png|0-11-7,USSR October Revolution,/bg/map3.png|0-12-25,Xmax,/bg/rabbit.png

  public static void main( String[] args )
  {
    SpecDays sd = new SpecDays( specs, '|' );

    //final String day = "2013-12-30";
    checkDay( sd, "2013-12-30" );
    checkDay( sd, "2014-01-1" );
    checkDay( sd, "2014-3-7" );
    checkDay( sd, "2014-3-8" );

  }

  private static void checkDay( final SpecDays sd, final String day )
  {
    int ind;
    final Date dt = DateTime.parseDate( day );
    if ( ( ind = sd.specDayIndexFor( dt ) ) >= 0 )
    {
      Text.sout( "Spec day for " + DateTime.date2StdString( dt ) + " : " + sd.name( ind ) + ", pic " + sd.picture( ind ) );
    }
    else
    {
      Text.sout( "Spec day for " + day + " not found" );
    }
  }*/

  public String picture( final int ind )
  {
    if ( !checkRange( ind ) )
      return "";
    return m_iconNames[ ind ];
  }

  private boolean checkRange( final int index )
  {
    return ( index >= 0 && index < m_dates.length );
  }

  public String name( final int ind )
  {
    if ( !checkRange( ind ) )
      return "";
    return m_dayNames[ ind ];
  }
}
