package pcm.gui.graphics;

import java.awt.FlowLayout;

import javafx.beans.property.BooleanProperty;

import javax.swing.JPanel;

import processing.core.PApplet;
import pcm.model.geom.*;

/**
 * Holder for and modifier of JPanel and the Processing applet contained inside it.
 * 
 * @author Susan
 */
public class AppletInterfacer {

  public static JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
  public static PApplet applet = new Applet();
  private static AppletModel model = new AppletModel();
  private static Earth earth = new Earth();

  public static boolean changed = false;

  /*
   * Sets JPanel and creates applet inside of it.
   * 
   * @param left pixels offset from left to show applet
   * @param top pixels offset from top to show applet
   * @param width width of applet
   * @param height height of applet
   */
  public static void create(int left, int top, int width, int height) {
    panel.setBounds(left, top, width, height);

    applet = new Applet(width, height, model, earth);
    applet.init();
    standBy();

    panel.add(applet);
  }

  /*
   * For resizing applet.
   * 
   * @param width width of applet
   * 
   * @param height height of applet
   */
  public static void resize(int width, int height) {
    applet = new Applet(width, height, model, earth);
    applet.init();
    if (!panel.isVisible())
      standBy();
  }

  /*
   * Makes applet redraw and visible when called.
   */
  public static void open() {
    applet.loop();
    panel.setVisible(true);
  }

  /*
   * Makes applet stop drawing and non-visible when called.
   */
  public static void standBy() {
    applet.noLoop();
    panel.setVisible(false);
  }

  /*
   * Defines angles of incoming solar flux
   * 
   * @param zenith angle to heighest point in sky in degrees
   * 
   * @param azimuth angle to horizon in degrees
   */
  public static void updateModel(double zenith, double azimuth) {
    model.zenith = zenith;
    model.azimuth = azimuth;
  }

  /*
   * Rotates Earth to ISS coordinates
   * 
   * @param latitude domain is [-90, 90] in degrees
   * 
   * @param longitude domain is [-180, 180] in degrees
   */
  public static void updateEarth(double latitude, double longitude, boolean toEquator) {
    earth.setISSPosition(latitude, longitude, toEquator);
  }

  /*
   * Toggles on or off playing the model's simulation animation
   */
  public static void toggleAnim() {
    model.runAnim = !model.runAnim;
  }
  
  public static boolean getAnimState() {
    return model.runAnim;
  }

  public static String modelStats() {
    return model.printOutput;
  }

  /*
   * Initial values of zenith and azimuth angles in AppletModel
   */
  public static double getZenith() {
    return model.zenith;
  }
  
  public static double getAzimuth() {
    return model.azimuth;
  }

  public static void update(double zenith, double azimuth, double latitude, double longitude, boolean toEquator) {
    if (changed) {
      model.reset();
      model.zenith = zenith;
      model.azimuth = azimuth;
      updateEarth(latitude, longitude, toEquator);
      model.runAnim = true;
    }
    else 
      toggleAnim();
    
    changed = false;
  }
}
