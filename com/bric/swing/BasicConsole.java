/*
 * @(#)BasicConsole.java
 *
 * $Date: 2011/01/18 10:25:03 $
 *
 * Copyright (c) 2009 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * http://javagraphics.blogspot.com/
 * 
 * And the latest version should be available here:
 * https://javagraphics.dev.java.net/
 */
package com.bric.swing;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * A very primitive JTextArea that presents System.out to the user. This version
 * does not support dealing with System.err or System.in.
 */
public class BasicConsole extends JTextArea
{
  private static final long serialVersionUID = 1L;

  public BasicConsole()
  {
    setFont( new Font( "Monospaced", 0, 13 ) );
    setEditable( false );
    OutputStream out = new OutputStream()
    {
      public void write( int b ) throws IOException
      {
        BasicConsole.this.append( Character.toString( ( char ) b ) );
      }

      public void write( byte[] b, int off, int len ) throws IOException
      {
        BasicConsole.this.append( new String( b, off, len ) );
      }

      public void write( byte[] b ) throws IOException
      {
        BasicConsole.this.append( new String( b ) );
      }
    };
    try
    {
      System.setOut( new PrintStream( out )
      {
        public void print( String s )
        {
          BasicConsole.this.append( s );
        }
      } );
      System.setErr( new PrintStream( out )
      {
        public void print( String s )
        {
          BasicConsole.this.append( s );
        }
      } );
    }
    catch ( SecurityException e )
    {
      System.err.println(
        "A SecurityException occurred while trying to call System.setOut() and/or System.setErr()" );
      throw e;
    }
  }
}
