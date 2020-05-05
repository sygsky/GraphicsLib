package ru.ts.gui.elements;

import javax.swing.*;
import java.awt.*;

public class JSplash extends JWindow {

	private JFrame _parent;
    private JLabel splashImage;
    
    private Dimension screensize;
    
    private static String statusString = "";
    
    private Image image;
    
    public JSplash(JFrame parent) {
        super( parent );
	    _parent = parent;
        splashImage = new JLabel(new ImageIcon(getClass().getResource(
                   "resources/images/splash.gif")));
                   
        getContentPane().add(splashImage, BorderLayout.CENTER);
        
        pack();
        
        screensize = Toolkit.getDefaultToolkit().getScreenSize();
        
        setLocation((screensize.width / 2) - (getSize().width / 2),
                    (screensize.height / 2) - (getSize().height / 2));
        
        splashImage.setBorder(BorderFactory.createLineBorder(
                          new Color(75, 75, 75)));
                          
    }

	public static void start(final JSplash splash)
	{
		SwingUtilities.invokeLater( new Runnable()
		{
			public void run()
			{
				splash.setVisible( true );
			}
		} );
	}

	/**
	 * Stops the splash screen displaying
	 * @param splash The JSpalsh instance to stop its display
	 */
	public static void stop( final JSplash splash)
	{
		SwingUtilities.invokeLater( new Runnable()
		{
			public void run()
			{
				splash.getParent().setVisible( true );
				if ( splash.isShowing() == true )
				{
					splash.setVisible( false );
				}
			}
		} );
	}
}
