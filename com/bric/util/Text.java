/*
 * @(#)Text.java
 *
 * $Date: 2010-04-05 15:07:55 -0500 (Mon, 05 Apr 2010) $
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
package com.bric.util;

/** Static methods related to <code>Strings</code> and text. */
public class Text {

	/**
	 * This replaces all the occurrences of one substring with another.
	 * <P>
	 * A comparable method is built into the <code>String</code> class in Java
	 * 1.5, but this method is provided to maintain compatibility with Java 1.4.
	 */
	public static String replace(String text, String searchFor,
			String replaceWith) {
		if(text==null)
			throw new NullPointerException();
		if(searchFor==null)
			throw new NullPointerException();
		if(replaceWith==null)
			throw new NullPointerException();
		if(searchFor.equals(replaceWith))
			return text;
		
		int i;
		while ((i = text.indexOf(searchFor)) != -1) {
			text = text.substring(0, i) + replaceWith
					+ text.substring(i + searchFor.length());
		}
		return text;
	}
}
