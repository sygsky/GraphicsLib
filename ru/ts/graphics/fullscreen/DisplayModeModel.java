package ru.ts.graphics.fullscreen;/*
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

import ru.ts.graphics.fullscreen.test.DisplayModeTest;

import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DisplayModeModel extends DefaultTableModel {
  private DisplayMode[] modes;

  public DisplayModeModel(DisplayMode[] modes) {
    this.modes = modes;
  }

  public DisplayMode getDisplayMode(int r) {
    return modes[r];
  }

  public String getColumnName(int c) {
    return DisplayModeTest.COLUMN_NAMES[c];
  }

  public int getColumnCount() {
    return DisplayModeTest.COLUMN_WIDTHS.length;
  }

  public boolean isCellEditable(int r, int c) {
    return false;
  }

  public int getRowCount() {
    if (modes == null) {
      return 0;
    }
    return modes.length;
  }

  public Object getValueAt(int rowIndex, int colIndex) {
    DisplayMode dm = modes[rowIndex];
    switch (colIndex) {
    case DisplayModeTest.INDEX_WIDTH:
      return Integer.toString(dm.getWidth());
    case DisplayModeTest.INDEX_HEIGHT:
      return Integer.toString(dm.getHeight());
    case DisplayModeTest.INDEX_BITDEPTH: {
      int bitDepth = dm.getBitDepth();
      String ret;
      if (bitDepth == DisplayMode.BIT_DEPTH_MULTI) {
        ret = "Multi";
      } else {
        ret = Integer.toString(bitDepth);
      }
      return ret;
    }
    case DisplayModeTest.INDEX_REFRESHRATE: {
      int refreshRate = dm.getRefreshRate();
      String ret;
      if (refreshRate == DisplayMode.REFRESH_RATE_UNKNOWN) {
        ret = "Unknown";
      } else {
        ret = Integer.toString(refreshRate);
      }
      return ret;
    }
    }
    throw new ArrayIndexOutOfBoundsException("Invalid column value");
  }

}

