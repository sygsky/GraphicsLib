/**
 *</pre>
 * Created on 24.09.2010 18:11:34 
 * by SYGSKY
 * for project in 'ru.ts.bigscreen.forms'
 *</pre>
 */
package ru.ts.panels;
/*
 *  soapUI, copyright (C) 2004-2009 eviware.com
 *
 *  soapUI is free software; you can redistribute it and/or modify it under the
 *  terms of version 2.1 of the GNU Lesser General Public License as published by
 *  the Free Software Foundation.
 *
 *  soapUI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 *  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details at gnu.org.
 */
import ru.ts.common.misc.Text;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * <pre>
 * Package ru.ts.bigscreen.forms
 * Author 'SYGSKY'
 * Created  24.09.2010  18:11:34
 * Panel with gradient background
 * </pre>
 */
public class GradientPanel extends JPanel
{
 // ------------------------------ FIELDS ------------------------------

  public final static int HORIZONTAL = 0;
  public final static int VERTICAL = 1;
  public final static int DIAGONAL_LEFT = 2;
  public final static int DIAGONAL_RIGHT = 3;

  private int direction = HORIZONTAL;
  private boolean cyclic;
  private int maxLength;

  // --------------------------- CONSTRUCTORS ---------------------------

  public GradientPanel()
  {
    this( HORIZONTAL );
  }

  public GradientPanel( int direction )
  {
    super( new BorderLayout() );
    setOpaque( false );
    this.direction = direction;
  }

  public GradientPanel( LayoutManager layoutManager )
  {
    super( layoutManager );
    setOpaque( false );
    this.direction = HORIZONTAL;
  }

  // --------------------- GETTER / SETTER METHODS ---------------------

  public int getDirection()
  {
    return direction;
  }

  public void setDirection( int direction )
  {
    this.direction = direction;
  }

  public boolean isCyclic()
  {
    return cyclic;
  }

  public void setCyclic( boolean cyclic )
  {
    this.cyclic = cyclic;
  }

  public void setMaxLength( int maxLength )
  {
    this.maxLength = maxLength;
  }

  // -------------------------- OTHER METHODS --------------------------

  public void paintComponent( Graphics g )
  {
    if( isOpaque() )
    {
      super.paintComponent( g );
      return;
    }

    int width = getWidth();
    int height = getHeight();

    // Create the gradient paint
    GradientPaint paint = null;

    Color foreColor = getForeground();
    Color bgColor = getBackground();
	  Insets ins = this.getInsets();

    switch( direction )
    {
    case HORIZONTAL :
    {
      paint = new GradientPaint( 0, height / 2, foreColor, width, height / 2, bgColor, cyclic );
      break;
    }
    case VERTICAL :
    {
      paint = new GradientPaint( width / 2, 0, foreColor, width / 2, maxLength > 0 ? maxLength : height, bgColor, cyclic );
      break;
    }
    case DIAGONAL_LEFT :
    {
      paint = new GradientPaint( 0, 0, foreColor, width, height, bgColor, cyclic );
      break;
    }
    case DIAGONAL_RIGHT :
    {
      paint = new GradientPaint( width, 0, foreColor, 0, height, bgColor, cyclic );
      break;
    }
    }

    if( paint == null )
    {
      throw new RuntimeException( "Invalid direction specified in GradientPanel" );
    }

    // we need to cast to Graphics2D for this operation
    Graphics2D g2d = ( Graphics2D )g;

    // save the old paint
    Paint oldPaint = g2d.getPaint();

    // set the paint to use for this operation
    g2d.setPaint( paint );

    // fill the background using the paint
    //g2d.fillRect( 0, 0, width, height );
	  g2d.fillRect( ins.left, ins.top, width - ins.left - ins.right, height - ins.top - ins.bottom );

    // restore the original paint
    g2d.setPaint( oldPaint );

/*
	  Font fnt = g2d.getFont();
	  fnt = new Font( "Arial", Font.BOLD, 200 );
	  Color col = new Color( 0, 128, 0, 128 );
	  g2d.drawLine( 0, height/2, width, height/2 );
	  g2d.setColor( col );
	  g2d.setFont( fnt );
	  final String outText = "test";
	  Rectangle2D bounds = g2d.getFontMetrics().getStringBounds( outText, g2d );
	  g2d.drawLine( 0, (int)(height/2 - bounds.getHeight()), width, (int)(height/2 - bounds.getHeight()) );
	  int x = (int) ((width - bounds.getWidth())/2);
	  int y = (int) (height /2);
	  g2d.drawString( outText, x, y );
	  Text.sout("W " + width + ", H " + height + ", Text x " + x + ", y " + y + ". Bounds " + bounds);
*/

    super.paintComponent( g );
  }

}
