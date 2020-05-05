package ru.ts.gui.intf;

/**
 * <pre>
 * Class name: IDTPanel
 * Created by SYGSKY for package ru.ts.gui.intf
 * Date: 24.01.2011
 * Time: 16:03:21
 * <p/>
 * ... Date Time panel
 * Changes:
 * </pre>
 */
public interface IDTPanel extends ITSPanel
{

  int PANEL_WITH_DATETIME = 1;
  int PANEL_WITH_DATE_ONLY = 2;
  int PANEL_WITH_TIME_ONLY = 3;

  static final String TEXT_MASK_TIME_ONLY = "16:17:06";
  static final String TEXT_MASK_DATE_ONLY = "24-01-2011";
  static final String TEXT_MASK_DATETIME = TEXT_MASK_DATE_ONLY + ' ' + TEXT_MASK_TIME_ONLY;
}
