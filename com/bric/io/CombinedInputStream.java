/*
 * @(#)CombinedInputStream.java
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
package com.bric.io;

import java.io.IOException;
import java.io.InputStream;

/** This is a combination of multiple <code>InputStreams</code>.
 * One the first stream is finished, the next stream is read.
 */
public class CombinedInputStream extends InputStream {
	InputStream[] inputs;
	
	public CombinedInputStream(InputStream in1,InputStream in2) {
		inputs = new InputStream[] {in1, in2};
	}

	public int available() throws IOException {
		if(inputs.length==0) return 0;
		int avail = inputs[0].available();
		if(avail!=0) {
			return avail;
		}
		removeInput();
		return available();
	}
	
	private void removeInput() {
		try {
			inputs[0].close();
		} catch(IOException e) {
			//tough call:
			//technically this IOException isn't a problem,
			//since we're done with this stream.
			//But really could throw it anyway?
			//When it doubt: go with the more stable option:
			e.printStackTrace();
		}
		InputStream[] newArray = new InputStream[inputs.length-1];
		System.arraycopy(inputs, 1, newArray, 0, newArray.length);
		inputs = newArray;
	}

	public void close() throws IOException {
		for(int a = 0; a<inputs.length; a++) {
			inputs[a].close();
		}
		inputs = new InputStream[0];
	}

	public boolean markSupported() {
		return false;
	}

	public synchronized void reset() throws IOException {}
	
	public synchronized void mark(int readlimit) {}


	public int read() throws IOException {
		if(inputs.length==0) return -1;
		int k = inputs[0].read();
		if(k!=-1) {
			return k;
		}
		removeInput();
		return read();
	}

	public int read(byte[] b, int off, int len) throws IOException {
		if(inputs.length==0) return -1;
		
		int k = inputs[0].read(b, off, len);
		if(k!=-1) {
			return k;
		}
		removeInput();
		return read(b, off, len);
	}

	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	public long skip(long n) throws IOException {
		if(inputs.length==0) return -1;
		
		long skipped = 0;
		while(skipped<n) {
			long t = inputs[0].skip(n-skipped);
			if(t==-1) {
				removeInput();
				if(inputs.length==0)
					return skipped;
			}
			skipped += t;
		}
		return skipped;
	}
}
