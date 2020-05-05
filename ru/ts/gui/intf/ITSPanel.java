package ru.ts.gui.intf;

/**
 * <pre>
 * Class name: ITSPanel
 * Created by SYGSKY for package ru.ts.gui.intf
 * Date: 24.01.2011
 * Time: 16:12:28
 * <p/>
 * ... Extension for the time span panel ...
 * <p/>
 * Changes:
 * </pre>
 */
public interface ITSPanel extends IStatusBar.ISBPanel
{
  /**
   * Inform about panel activity
   *
   * @return <code>true</code> if panel is displaying span time or
   *         <code>false</code> if display is stopped
   */
  boolean isRunning();

  void start();

  void stop();
}
