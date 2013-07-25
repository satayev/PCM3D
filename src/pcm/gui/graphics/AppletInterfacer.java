package pcm.gui.graphics;

import dev.simple.FixedPhoton;
import dev.simple.SimpleFixedModel;
import dev.simple.Tower;

import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import pcm.AbsorptionSimulation;
import pcm.StatisticalEvaluator;
import pcm.model.RectangularPrismModel;
import pcm.model.geom.V;
import pcm.model.geom.Vector;
import pcm.model.geom.curves.Polygon;
import pcm.model.geom.solids.Prism;
import pcm.model.orbit.ISSOrbit;
import pcm.model.statics.WavelengthAM0;
import processing.core.PApplet;

/**
 * Holder for and modifier of JPanel and the Processing applet contained inside it.
 * 
 * @author Susan
 */
public class AppletInterfacer {

  public static JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
  public static PApplet applet = new Applet();
  public static AppletModel model = new AppletModel();
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
  
  public static void setModel(double X, double Y, double Z, List<List<Vector>> edgelists) {
    double theta = 0;
    List<Tower> towers = new ArrayList<Tower>();
    for (List<Vector> i : edgelists) {
      List<Double> lx = new ArrayList<Double>();
      List<Double> ly = new ArrayList<Double>();
      for (Vector j : i) {
        lx.add(j.x);
        ly.add(j.y);
      }
      towers.add(new Tower(lx, ly));
    }
    model.SFM = new SimpleFixedModel(X, Y, Z, theta, towers, new FixedPhoton(new Vector(0, 0, -1)));
    model.LT = towers;

    model.RPM = new RectangularPrismModel(X, Y, Z);
    Vector displacement = new Vector(X/2,Y/2,0);
    for (List<Vector> i : edgelists) {
      for (Vector j : i) j.sub(displacement);
      model.RPM.cnts.add(new Prism(new Vector(), new Polygon(i.toArray(new Vector[i.size()]))));
    }
    model.edgelists = edgelists;
    ISSOrbit orbit = new ISSOrbit();
    model.AS = new AbsorptionSimulation(model.RPM, orbit);
    model.SE.AssignModel(X, Y, Z, edgelists);
    model.reset();
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
      model.zenith = zenith;
      model.azimuth = azimuth;
      model.reset();
      model.updated = true;
      updateEarth(latitude, longitude, toEquator);
      model.runAnim = true;
    }
    else 
      toggleAnim();
    
    changed = false;
  }
}
