/*
 * @(#)Thumbnail.java
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
 * http://javagraphics.blogspot.com/
 * 
 * And the latest version should be available here:
 * https://javagraphics.dev.java.net/
 */
package com.bric.image;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/** Static methods related to rendering thumbnails. */
public class Thumbnail {
	
	private static final RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	
	static {
		qualityHints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		qualityHints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	}
	
	/** Creates a white frame of about 5 pixels around an image and adds a light shadow.
	 * 
	 * @param bi the image to make a thumbnail of.
	 * @param maxSize the maximum width and height this thumbnail can use.
	 * @return a small thumbnail.
	 */
	public static BufferedImage frameWithShadow(BufferedImage bi,Dimension maxSize) {
		Insets insets = new Insets(5,5,5,5);
		float widthRatio = ((float)maxSize.width-insets.left-insets.right)/((float)bi.getWidth());
		float heightRatio = ((float)maxSize.height-insets.top-insets.bottom)/((float)bi.getHeight());
		float ratio = Math.min(widthRatio, heightRatio);
		int w = (int)(bi.getWidth()*ratio);
		int h = (int)(bi.getHeight()*ratio);
		//this is unusuable, but will prevent errors
		if(w==0) w = 1;
		if(h==0) h = 1;
		BufferedImage scaledThumbnail = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = scaledThumbnail.createGraphics();
		g.setRenderingHints(qualityHints);

		RoundRectangle2D shadow = new RoundRectangle2D.Float();
		g.setColor(new Color(0,0,0,7));
		for(int a = 0; a<5; a++) {
			shadow.setRoundRect(a, a+a*.5f, scaledThumbnail.getWidth()-2*a, scaledThumbnail.getHeight()-2*a, 12, 12);
			g.fill(shadow);
		}
		RoundRectangle2D.Float frame = new RoundRectangle2D.Float(2,2,scaledThumbnail.getWidth()-5,scaledThumbnail.getHeight()-5,4,4);
		g.setColor(new Color(0xFBFBFC));
		g.fill(frame);
		g.setColor(new Color(0xC6C6C6));
		g.draw(frame);
		
		frame.setRoundRect(5,5,scaledThumbnail.getWidth()-11,scaledThumbnail.getHeight()-11,1,1);
		g.setColor(Color.white);
		g.fill(frame);
		
		Graphics2D graphicsG = (Graphics2D)g.create();
		graphicsG.translate( insets.left, insets.top );
		graphicsG.scale( ((double)scaledThumbnail.getWidth()-insets.left-insets.right)/((double)bi.getWidth()), 
				((double)scaledThumbnail.getHeight()-insets.top-insets.bottom)/((double)bi.getHeight()) );
		graphicsG.drawImage(bi, 0, 0, null);
		graphicsG.dispose();
		bi.flush();

		g.setStroke(new BasicStroke(1.5f));
		g.setColor(new Color(0,0,0,85));
		g.draw(frame);
		g.dispose();
		return scaledThumbnail;
	}

	/** Creates a very subtle shadow and applies a hairline translucent black frame around
	 * the image.
	 * 
	 * @param bi the image to make a thumbnail of.
	 * @param maxSize the maximum width and height this thumbnail can use.
	 * @return a small thumbnail.
	 */
	public static BufferedImage shadow(BufferedImage bi,Dimension maxSize) {
		Insets insets = new Insets(3,3,3,3);
		float widthRatio = ((float)maxSize.width-insets.left-insets.right)/((float)bi.getWidth());
		float heightRatio = ((float)maxSize.height-insets.top-insets.bottom)/((float)bi.getHeight());
		float ratio = Math.min(widthRatio, heightRatio);
		int w = (int)(bi.getWidth()*ratio);
		int h = (int)(bi.getHeight()*ratio);
		//this is unusuable, but will prevent errors
		if(w==0) w = 1;
		if(h==0) h = 1;
		BufferedImage scaledThumbnail = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = scaledThumbnail.createGraphics();
		g.setRenderingHints(qualityHints);
		
		RoundRectangle2D shadow = new RoundRectangle2D.Float();
		g.setColor(new Color(0,0,0,9));
		for(int a = 0; a<3; a++) {
			shadow.setRoundRect(a-.5f, a-.5f, scaledThumbnail.getWidth()-2*a, scaledThumbnail.getHeight()-2*a, 12, 12);
			g.fill(shadow);
		}
		
		g.translate( insets.left, insets.top );
		g.scale( ((double)scaledThumbnail.getWidth()-insets.left-insets.right)/((double)bi.getWidth()), 
				((double)scaledThumbnail.getHeight()-insets.top-insets.bottom)/((double)bi.getHeight()) );
		g.drawImage(bi, 0, 0, null);
		g.setStroke(new BasicStroke(1.5f));
		g.setColor(new Color(0,0,0,105));
		g.draw(new Rectangle2D.Float(0,0,bi.getWidth()-1,bi.getHeight()-1));
		g.dispose();
		bi.flush();
		return scaledThumbnail;
	}

	public static BufferedImage scale(BufferedImage bi,Dimension maxSize) {
		return scale(bi, maxSize, true);
	}

	/** Simply scales this image to a maximum width & height.  No border, shadow, etc.
	 * 
	 * @param bi the image to make a thumbnail of.
	 * @param maxSize the maximum width and height this thumbnail can use.
	 * @param scaleUpwards if the image is smaller than the maxSize provided, this
	 * controls whether this image will be scaled upwards or not.
	 * @return a small thumbnail.
	 */
	public static BufferedImage scale(BufferedImage bi,Dimension maxSize,boolean scaleUpwards) {
		float widthRatio = ((float)maxSize.width)/((float)bi.getWidth());
		float heightRatio = ((float)maxSize.height)/((float)bi.getHeight());
		float ratio = Math.min(widthRatio, heightRatio);
		int w = (int)(bi.getWidth()*ratio);
		int h = (int)(bi.getHeight()*ratio);
		
		if(scaleUpwards==false && ratio>1) {
			ratio = 1;
		}
		
		//this makes the image unusuable, but will prevent errors
		if(w==0) w = 1;
		if(h==0) h = 1;
		BufferedImage scaledThumbnail = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = scaledThumbnail.createGraphics();
		g.setRenderingHints(qualityHints);
		g.translate(w/2, h/2);
		g.scale( ratio, ratio );
		g.translate(-bi.getWidth()/2, -bi.getHeight()/2);
		g.drawImage(bi, 0, 0, null);
		g.dispose();
		
		bi.flush();
		return scaledThumbnail;
	}

	/** Rotates this image.
	 * 
	 * @param bi the image to make a thumbnail of.
	 * @param maxSize the maximum width and height this thumbnail can use.
	 * @param theta the angle (in radians) to rotate this thumbnail.
	 * @return a small thumbnail.
	 */
	public static BufferedImage rotate(BufferedImage bi,Dimension maxSize,double theta) {
		GeneralPath p = new GeneralPath();
		p.moveTo(0, 0);
		p.lineTo(bi.getWidth(), 0);
		p.lineTo(bi.getWidth(), bi.getHeight());
		p.lineTo(0, bi.getHeight());
		p.lineTo(0, 0);
		p.transform(AffineTransform.getRotateInstance(theta, bi.getWidth()/2, bi.getHeight()/2));
		Rectangle2D b = p.getBounds2D();

		float widthRatio = ((float)maxSize.width)/((float)b.getWidth());
		float heightRatio = ((float)maxSize.height)/((float)b.getHeight());
		float ratio = Math.min(widthRatio, heightRatio);
		int w = (int)(b.getWidth()*ratio+.5);
		int h = (int)(b.getHeight()*ratio+.5);
		//this is unusuable, but will prevent errors
		if(w==0) w = 1;
		if(h==0) h = 1;
		
		BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = newImage.createGraphics();
		g.setRenderingHints(qualityHints);
		
		g.translate(newImage.getWidth()/2, newImage.getHeight()/2);
		g.rotate(theta);
		g.scale(ratio, ratio);
		g.translate(-bi.getWidth()/2, -bi.getHeight()/2);
		
		/*int x = newImage.getWidth()/2-bi.getWidth()/2;
		int y = newImage.getHeight()/2-bi.getHeight()/2;
		int alpha = 10;
		for(int a = 0; a<3; a++) {
			alpha = alpha+10;
			g.setColor(new Color(0,0,0,alpha));
			g.fill(new RoundRectangle2D.Float(x+2+a,y+2+a,bi.getWidth()-2*a,bi.getHeight()-2*a,10-a*2,10-a*2));
		}*/
		
		g.drawImage(bi, 0, 0, null);
		g.dispose();
		
		return newImage;
	}

	/** Rotates this image and adds a slight shadow.
	 * 
	 * @param bi the image to make a thumbnail of.
	 * @param maxSize the maximum width and height this thumbnail can use.
	 * @param theta the angle (in radians) to rotate.
	 * @return a small thumbnail.
	 */
	public static BufferedImage rotateWithShadow(BufferedImage bi,Dimension maxSize,double theta) {
		GeneralPath p = new GeneralPath();
		p.moveTo(0, 0);
		p.lineTo(bi.getWidth(), 0);
		p.lineTo(bi.getWidth(), bi.getHeight());
		p.lineTo(0, bi.getHeight());
		p.lineTo(0, 0);
		p.transform(AffineTransform.getRotateInstance(theta, bi.getWidth()/2, bi.getHeight()/2));
		Rectangle2D b = p.getBounds2D();

		int shadowSize = 4;
		
		float widthRatio = ((float)maxSize.width-2*shadowSize)/((float)b.getWidth());
		float heightRatio = ((float)maxSize.height-2*shadowSize)/((float)b.getHeight());
		float ratio = Math.min(widthRatio, heightRatio);
		int w = (int)(b.getWidth()*ratio+.5+2*shadowSize);
		int h = (int)(b.getHeight()*ratio+.5+2*shadowSize);
		//this is unusuable, but will prevent errors
		if(w==0) w = 1;
		if(h==0) h = 1;
		
		BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g = newImage.createGraphics();
		g.setRenderingHints(qualityHints);
		
		g.translate(newImage.getWidth()/2, newImage.getHeight()/2);
		g.rotate(theta);
		g.scale(ratio, ratio);
		g.translate(-bi.getWidth()/2, -bi.getHeight()/2);
		
		for(int a = 0; a<shadowSize; a++) {
			g.setColor(new Color(0,0,0,20-5*a));
			g.fill(new RoundRectangle2D.Float(0-a/ratio,0-a/ratio,bi.getWidth()+2*a/ratio,bi.getHeight()+2*a/ratio,10+a*2/ratio,10+a*2/ratio));
		}
		
		g.drawImage(bi, 0, 0, null);
		g.dispose();
		
		return newImage;
	}
}
