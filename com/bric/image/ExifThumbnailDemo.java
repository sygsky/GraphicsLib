/*
 * @(#)ExifThumbnailDemo.java
 *
 * $Date: 2010/12/29 13:31:58 $
 *
 * Copyright (c) 2010 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.dev.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
package com.bric.image;

import com.bric.io.SuffixFilenameFilter;
import com.bric.swing.BasicConsole;
import com.bric.util.JVM;
import ru.ts.gui.GuiUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * A simple demo for the <code>ExifThumbnailReader</code> class.
 *
 * @name ExifThumbnailReader
 * @title Images: Reading JPEG Thumbnails
 * @release March 2010
 * @blurb This demonstrates a class that extracts thumbnail information out of a
 * JPEG. <p><code>ImageIO</code> can do this: but only if you already have
 * <code>JAI</code> installed on your machine.
 * @see <a href="http://javagraphics.blogspot.com/2010/03/images-reading-jpeg-thumbnails.html">Images:
 *      Reading JPEG Thumbnails</a>
 */
public class ExifThumbnailDemo extends JPanel
{

  public static void main( String[] args )
  {
    GuiUtils.setSystemLookAndFeel(  );
    JFrame f = new JFrame();
    f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    f.getContentPane().add( new ExifThumbnailDemo() );
    f.pack();
    f.setVisible( true );
  }

  int maxHeight = 80;
  BufferedImage bigImage;
  JLabel label = new JLabel( "Current File:" );
  JLabel fileName = new JLabel( "None" );
  JLabel iconLabel = new JLabel();

  JButton button = new JButton( "Select JPEG" );

  public ExifThumbnailDemo()
  {
    bigImage = new BufferedImage( 500, 500, BufferedImage.TYPE_INT_ARGB );
    Graphics2D g = bigImage.createGraphics();
    g.setPaint( new GradientPaint( 0, 0, Color.green, 500, 500, Color.blue ) );
    g.fillRect( 0, 0, 500, 500 );
    g.dispose();

    setLayout( new GridBagLayout() );
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.insets = new Insets( 3, 3, 3, 3 );
    c.weightx = 1;
    c.weighty = 0;
    c.anchor = GridBagConstraints.EAST;
    add( label, c );
    c.gridx++;
    c.anchor = GridBagConstraints.WEST;
    add( fileName, c );
    c.gridy++;
    c.gridx = 0;
    c.anchor = GridBagConstraints.CENTER;
    c.gridwidth = 2;
    add( button, c );
    c.gridy++;
    c.weighty = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    add( new JLabel( "Console:" ), c );
    c.gridx = 2;
    c.gridy = 0;
    c.gridheight = 3;
    c.gridwidth = 1;
    add( iconLabel, c );
    c.gridy = 4;
    c.gridx = 0;
    c.gridwidth = GridBagConstraints.REMAINDER;

    c.gridy++;
    c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;
    JScrollPane scrollPane = new JScrollPane( new BasicConsole() );
    scrollPane.setPreferredSize( new Dimension( 600, 600 ) );
    add( scrollPane, c );

    button.addActionListener( new ActionListener()
    {
      public void actionPerformed( ActionEvent e )
      {
        selectNewImage();
      }
    } );
  }

  private void selectNewImage()
  {
    FileDialog fd =
      new FileDialog( ( Frame ) SwingUtilities.getWindowAncestor( button ) );
    fd.setFilenameFilter( new SuffixFilenameFilter( "jpeg", "jpg" ) );
    fd.setVisible( true );
    if ( fd.getFile() == null ) return; //cancelled

    File jpegFile = new File( fd.getDirectory() + fd.getFile() );
    BufferedImage thumbnail = ExifThumbnailReader.getThumbnail( jpegFile );
    if ( thumbnail != null )
    {
      iconLabel.setIcon( new ImageIcon( thumbnail ) );
      bigImage = thumbnail;
      fileName.setText( jpegFile.getName() );
    }
    else
    {
      iconLabel.setIcon( null );
      try
      {
        bigImage = ImageIO.read( jpegFile );
        fileName.setText( jpegFile.getName() );
      }
      catch ( IOException e2 )
      {
        e2.printStackTrace();
      }
    }

    Thread testThread = new Thread( new TestRunnable( jpegFile ) );
    testThread.start();
  }

  static class TestRunnable implements Runnable
  {
    static boolean printedProfile = false;

    File jpeg;

    public TestRunnable( File jpeg )
    {
      this.jpeg = jpeg;
    }

    public void run()
    {
      if ( printedProfile == false )
      {
        printedProfile = true;
        JVM.printProfile();
      }
      System.out.println( "-------------------------\n" +
        "Starting tests for \"" + jpeg.getName() + "\"" );

      //this thread is triggered by selecting a new JPEG:
      //give the GUI a chance to catch up and repaint everything
      //before we start:
      try
      {
        Thread.sleep( 200 );
      }
      catch ( Exception e )
      {
      }
      ;

      long time, memory;
      System.out.println( "\nTesting ExifThumbnailReader:" );
      try
      {
        time = System.currentTimeMillis();
        memory = Runtime.getRuntime().freeMemory();
        if ( ExifThumbnailReader.getThumbnail( jpeg ) == null )
          throw new UnsupportedOperationException(
            "ExifThumbnailReader could not read a thumbnail for \"" + jpeg
              .getName() + "\"" );
        time = System.currentTimeMillis() - time;
        memory = memory - Runtime.getRuntime().freeMemory();

        System.out.println( "\tTime for ExifThumbnailReader: " + time + " ms" );
        System.out.println(
          "\tAllocation for ExifThumbnailReader: " + memory / 1024 + " KB" );

      }
      catch ( Exception e )
      {
        e.printStackTrace();
      }

      System.out.println( "\nTesting ImageIO (thumbnail):" );
      Iterator iterator = ImageIO.getImageReadersBySuffix( "jpeg" );
      while ( iterator.hasNext() )
      {
        ImageReader reader = ( ImageReader ) iterator.next();
        try
        {
          time = System.currentTimeMillis();
          memory = Runtime.getRuntime().freeMemory();
          reader.setInput( ImageIO.createImageInputStream( jpeg ) );
          BufferedImage thumbnail = reader.readThumbnail( 0, 0 );
          if ( thumbnail == null )
          {
            throw new UnsupportedOperationException(
              "ImageIO could not read a thumbnail for \"" + jpeg
                .getName() + "\"" );
          }
          time = System.currentTimeMillis() - time;
          memory = memory - Runtime.getRuntime().freeMemory();

          System.out.println( "\tTime for " + getName(
            reader.getClass().getName() ) + ": " + time + " ms" );
          System.out.println( "\tAllocation for " + getName(
            reader.getClass().getName() ) + ": " + memory / 1024 + " KB" );
        }
        catch ( Exception e )
        {
          e.printStackTrace();
        }
      }

      System.out.println( "\nTesting ImageIO (full size):" );
      iterator = ImageIO.getImageReadersBySuffix( "jpeg" );
      while ( iterator.hasNext() )
      {
        ImageReader reader = ( ImageReader ) iterator.next();
        try
        {
          time = System.currentTimeMillis();
          memory = Runtime.getRuntime().freeMemory();
          reader.setInput( ImageIO.createImageInputStream( jpeg ) );
          BufferedImage image = reader.read( 0 );
          if ( image == null )
          {
            throw new UnsupportedOperationException(
              "ImageIO could not read an image for \"" + jpeg
                .getName() + "\"" );
          }
          Thumbnail.scale( image, new Dimension( 128, 128 ), false );
          time = System.currentTimeMillis() - time;
          memory = memory - Runtime.getRuntime().freeMemory();
          System.out.println( "\tTime for " + getName(
            reader.getClass().getName() ) + ": " + time + " ms" );
          System.out.println( "\tAllocation for " + getName(
            reader.getClass().getName() ) + ": " + memory / 1024 + " KB" );
        }
        catch ( Exception e )
        {
          e.printStackTrace();
        }
      }
    }

    private String getName( String className )
    {
      int i = className.lastIndexOf( '.' );
      if ( i == -1 ) return className;
      return className.substring( i + 1 );
    }
  }
}
