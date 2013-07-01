package pcm.gui.graphics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import pcm.AbsorptionSimulation;
import pcm.PCM3D;
import pcm.model.RectangularPrismModel;
import pcm.model.geom.V;
import pcm.model.geom.Vector;
import pcm.model.geom.curves.Polygon;
import pcm.model.geom.solids.Prism;
import dev.simple.FixedPhoton;
import dev.simple.Photon;
import dev.simple.Ribbon;
import dev.simple.SimpleFixedModel;
import dev.simple.SimpleModel;
import dev.simple.Statistic;
import dev.simple.Surface;
import dev.simple.Tower;

/**
 * Class utilized by Applet class to draw photons, CNT towers on a solar cell.
 * Doubles casted to floats to accommodate float-specific Processing function
 * parameters.
 * 
 * @author Susan
 *         Revised by John
 */
public class AppletModel {

  public double X = 1, Y = 1, Z = 1, Z0 = 1;

  // Macroscale (applet) parameters from model:
  // photonRadius and spacing in microscopic scaling with magnification
  // applied to them,
  // x-y-zBounds (magnified) and speed of photon used in applet view
  public float magnif = 250, photonRadius = (float) .01 * magnif;
  public float xBounds = (float) (X * magnif), yBounds = (float) (Y * magnif), zBounds = (float) (Z * magnif);
  public double spacing = .25 * magnif, speed = .01;
  public Vector sunDir = new Vector(), sunPos = new Vector();
  public double sunDistance = 2, degrees = 2.5, dt = 2.5; // 90 minus degrees of zenith angle at which the sun starts, dt is change in degrees for moving sun

  public List<PathFollower> paths = new LinkedList<PathFollower>();
  /** The number of paths displayed on the screen */
  public int runningPaths = 0;
  /** indicates weather to draw a new photon when one wanders off screen */
  public boolean continuousRunning = true;

  // The model to use
  public SimpleFixedModel SFM;
  public AbsorptionSimulation AS;

  public List<Tower> LT;
  public boolean runSimpleModel = true; // whether to use the simple model

  public RectangularPrismModel RPM = new RectangularPrismModel(X, Y, Z);

  // The start time for the simulation
  public long startTime;
  public long lastTime = 0;

  // The size of the repeated drawing
  int modSize = 0;
  int maxPhotons = 100; //*sizechange* added one to compensate for decreasing 1 everywhere else there is //*sizechange*

  public static String printOutput = "";
  public static boolean runAnim = false;
  boolean paramsChanged = false;

  public void setParams(double degrees, int maxPhotons, int modSize) {
    this.maxPhotons = maxPhotons; //*sizechange*
    this.modSize = modSize - 1;
    this.degrees = degrees;
    paramsChanged = true;
    run();
  }

  public AppletModel() {
    this(2.5, 100, 1);
  }

  public AppletModel(double degrees, int maxPhotons, int modSize) {

    this.degrees = degrees;
    this.maxPhotons = maxPhotons;
    this.modSize = modSize - 1;

    if (runSimpleModel) {

      LT = new ArrayList<Tower>();
      //      List<Double> Lx = Arrays.asList(.5, .75, .5, .25);
      //      List<Double> Ly = Arrays.asList(.25, .5, .75, .5);
      //      LT.add(new Tower(Lx, Ly));

      List<Double> Lx;
      List<Double> Ly;
      //      Lx = Arrays.asList(.1, .3, .4, .2);
      //      Ly = Arrays.asList(.3, .2, .8, .7);
      //      LT.add(new Tower(Lx, Ly));
      Lx = Arrays.asList(.5, .8, .7, .7);
      Ly = Arrays.asList(.5, .6, .8, .9);
      LT.add(new Tower(Lx, Ly));

      //    Lx = Arrays.asList(.6, .8, .6, .4);
      //    Ly = Arrays.asList(.4, .6, .9, .7);
      //    LT.add(new Tower(Lx, Ly));
      Lx = Arrays.asList(.2, .4, .5, .4, .2, .1);
      Ly = Arrays.asList(.1, .1, .3, .5, .5, .3);
      LT.add(new Tower(Lx, Ly));

      FixedPhoton photon = new FixedPhoton(new Vector(0, 0, -1));
      SFM = new SimpleFixedModel(X, Y, Z0, Math.PI / 8, LT, photon);
      //SM.p.stat.N = 0;
      //SM.p.stat.X = 0;

    } else {
      RPM.cnts.add(new Prism(new Vector(), new Polygon(new Vector[] { new Vector(0, -.25, 0), new Vector(.25, 0, 0),
          new Vector(0, .25, 0), new Vector(-.25, 0, 0) })));
      AS = new AbsorptionSimulation(RPM);

      LT = new ArrayList<Tower>();
      List<Double> Lx = Arrays.asList(0., .25, 0., -.25);
      List<Double> Ly = Arrays.asList(-.25, 0., .25, 0.);
      LT.add(new Tower(Lx, Ly));
    }

    printOutput = "Zenith Angle\t\t\tAbsorption\n\n";
    startTime = System.currentTimeMillis();

  }

  public void run() {
    if (degrees <= 0 || degrees >= 180) {
      runAnim = false;
      degrees = 2.5;

    }

    if (runSimpleModel) {
      SFM.clear();
      SFM.p.stat.N = maxPhotons;
      SFM.p.stat.X = maxPhotons;

      double radians = Math.PI * degrees / 180;
      SFM.setEntry(new Vector(Math.cos(radians), 0, -Math.sin(radians)));
      SFM.run(10000);
      SFM.p.stat.printAll();
      printOutput += degrees + " degrees\t\t\t" + SFM.p.stat.getRatio() + " %" + "\n";

      //	    if (paramsChanged)
      //	    	paths = new ArrayList<List<List<Vector>>>();
      paramsChanged = false;
      for (int i = 0; i < SFM.p.stat.rv.size(); i++) {
        paths.add(new PathFollower(SFM.p.stat.rv.get(i)));
      }
      //*sizechange*
      // TODO bug for last photon in simple model? 
      //paths.remove(paths.size() -1);
      //reset();
    } else {
      try {
        AS.run(1000000);
        AS.printStats();
      } catch (Exception E) {
        E.printStackTrace();
      }

    }
    // Position and direction of the sun set from Photon class's baseline values for spawned photons with sunDistance taken into account
    sunPos = new Vector(.5, 1 - degrees / 180, Math.sin((1 - degrees / 180) * Math.PI));
    Vector tileCenter = new Vector(.5, .5, 0);
    tileCenter.sub(sunPos);
    tileCenter.normalize();
    sunDir = tileCenter.clone();
    sunPos.sub(V.mult(sunDistance, sunDir));

    degrees += dt;
  }

  public void addPhoton() {
    runningPaths++;
  }

  public void reset() {
    paths.clear();
  }

  public void drawPhotons(Applet applet, boolean updatePhotons, boolean test) {
    //    long time = System.currentTimeMillis() - startTime;
    //    Vector entryVector = getEntryVector(time);
    //    double prob = .001 * -entryVector.z;
    //    long timeDelay = time - lastTime;
    //    if (prob * timeDelay > PCM3D.rnd.nextDouble()) {
    //      if (runSimpleModel) {
    //        SM.p.n0 = entryVector;
    //        SM.p.stat.N++;
    //        SM.run(1);
    //      } else {
    //        AS.stats.maxPhotonPaths++;
    //        try {
    //          AS.run(1, entryVector);
    //        } catch (Exception e) {
    //          // TODO Auto-generated catch block
    //          e.printStackTrace();
    //        }
    //      }
    //      addPhoton();
    //    }
    //    lastTime = time;

    Iterator<PathFollower> i = paths.iterator();
    int size = runningPaths;
    while (i.hasNext() && size-- > 0) {
      PathFollower path = i.next();
      if (path.draw(applet)) {
        i.remove();
        if (!continuousRunning)
          runningPaths--;
      }
    }

    if (updatePhotons && (applet.keyPressed && applet.key == 'q' || runAnim) && test) {
      i = paths.iterator();
      size = runningPaths;
      while (i.hasNext() && size-- > 0) {
        PathFollower path = i.next();
        path.advance(speed);
      }
    }

    //      double absorptions = 0;
    //      for (int a = 0; a < branch; a++) {
    //        absorptions += branchList.get(a).size();
    //      }
    //      absorptions -= (branch) * 2;
    //      if (maxPhotons == 1 || maxPhotons == 2)
    //        System.out.println("absorptions: " + absorptions);
    //      absorptions = Math.log(absorptions);
    //      absorptions /= 3;
    //      // Color for each absorption number and lower transparency for older photon paths
    //      applet.colorMode(applet.HSB, 100);
    //      applet.stroke((float) absorptions * 100, 100, 100, (float) (255 * (i + 1.0) / (currentBranch.size())));
    //      //      if (currentBranch.size()<=SM.p.stat.N/2)
    //      //        applet.stroke(Tools.yellow, (float)(255*(i+1.0)/(currentBranch.size())));
    //      //      else
    //      //        applet.stroke(Tools.yellow, (float)(255*(i+1.0-currentBranch.size()+SM.p.stat.N/2)/(SM.p.stat.N/2)));

  }

  /**
   * Internal class for keeping track of each path
   * 
   * @author John Stewart
   */
  public class PathFollower {

    double maxDistance = .5;

    List<List<Vector>> path;
    int branch = 0, line = 0, reflections = 0;
    double distance = -maxDistance;

    public PathFollower(List<List<Vector>> path) {
      this.path = path;
    }

    /**
     * Draws the photon on its path
     * 
     * @return true if the photon is off screen
     */
    public boolean draw(Applet applet) {

      int branch0 = branch, line0 = line, reflections0 = reflections;
      double distance0 = distance;
      if (branch0 < path.size()) {
        List<Vector> lineList = path.get(branch0);
        Vector a, b, start, finish = lineList.get(line0 + 1);
        double remainingDistance = distance0 + maxDistance;
        boolean end = false;
        int color = Tools.colorScale[0];
        while (remainingDistance > 0 && !end) {
          a = lineList.get(line0);
          b = lineList.get(line0 + 1);
          double length = V.sub(b, a).length();
          Vector normal = V.normalize(V.sub(b, a));
          if (remainingDistance - maxDistance < 0)
            start = a.clone();
          else
            start = V.scaleAdd(a, remainingDistance - maxDistance, normal);
          if (remainingDistance > length)
            finish = b.clone();
          else
            finish = V.scaleAdd(a, remainingDistance, normal);
          remainingDistance = remainingDistance - length;
          
          int index = 0, power = reflections0;
          while ((power/=2)>0) index++;
          if (index > 8) index = 8;
          color = Tools.colorScale[index];
          applet.fill(color);
          applet.stroke(color);
          applet.strokeWeight(2);
          drawLine(applet, start, finish);
          
          line0++;
          reflections0++;
          while (line0 >= lineList.size() - 1) {
            line0 = 0;
            branch0++;
            reflections0--;
            if (branch0 >= path.size())
              end = true;
            else lineList = path.get(branch0);
          }
        }
        if (remainingDistance <= 0) {
          applet.fill(color);
          applet.stroke(color);
          drawPhoton(applet, finish);
        }
      }
      return branch >= path.size();
    }

    public void advance(double speed) {
      if (branch < path.size()) {
        List<Vector> lineList = path.get(branch);
        distance += speed;
        double length = V.sub(lineList.get(line), lineList.get(line + 1)).length();
        if (distance >= length) {
          distance -= length;
          line++;
          reflections++;
          if (line >= lineList.size() - 1) {
            line = 0;
            branch++;
            reflections++;
          }
        }
      }
    }

  }

  public void drawSurfaces(Applet applet) {
    //applet.textureMode(applet.NORMAL);
    applet.fill(Tools.black);
    applet.stroke(Tools.gray);
    applet.colorMode(applet.HSB, 100);
    double Z = Z0;
    for (int x = 0; x < LT.size(); x++) {
      Tower t = LT.get(x);
      //applet.stroke((float)(1.0*x/LT.size()*100), 100, 100); // color denoting each tower
      //for (Surface s : surfaces) {
      //if (s instanceof Ribbon) {
      //Ribbon r = (Ribbon) s;
      for (Ribbon r : t.LS) {
        drawRibbon(applet, new Vector(r.rx, r.ry, 0), new Vector(r.x2, r.y2, 0),
            new Vector(r.x2, r.y2, Z), new Vector(r.rx, r.ry, Z), r.b);
      }
      List<Vector> bounds = new ArrayList<Vector>();
      for (int i = 0; i < t.Lx.size(); i++)
        bounds.add(new Vector(t.Lx.get(i), t.Ly.get(i), Z));
      drawPolygon(applet, bounds);
    }

  }

  /**
   * Draws the solar cell grid
   */
  public void drawFloorGrid(Applet applet) {
    spacing = .2 * magnif;
    // Floor beyond small cell
    applet.strokeWeight(2);
    applet.stroke(Tools.gray);
    float size = modSize + 1;
    float offsetX, offsetY;
    if (runSimpleModel) {
      offsetX = 0;
      offsetY = 0;
    } else {
      offsetX = -xBounds / 2;
      offsetY = -yBounds / 2;
    }
    for (float col = -xBounds * (size - 1); col <= xBounds * size; col += spacing) {
      applet.line(col + offsetX, -yBounds * (size - 1) + offsetY, 0, col + offsetX, yBounds * size + offsetY, 0);
    }
    for (float row = -yBounds * (size - 1); row <= yBounds * size; row += spacing) {
      applet.line(-xBounds * (size - 1) + offsetX, row + offsetY, 0, xBounds * size + offsetX, row + offsetY, 0);
    }

    spacing = .1 * magnif;
    // Bounds inside of which photons spawn
    applet.fill(Tools.lgray);
    applet.beginShape();
    Tools.vertex(applet, new Vector(offsetX, offsetY));
    Tools.vertex(applet, new Vector(offsetX, yBounds + offsetY));
    Tools.vertex(applet, new Vector(xBounds + offsetX, yBounds + offsetY));
    Tools.vertex(applet, new Vector(xBounds + offsetX, offsetY));
    applet.endShape(applet.CLOSE);
    applet.strokeWeight(2);
    applet.stroke(Tools.white);
    for (float col = 0; col <= xBounds; col += spacing) {
      applet.line(col + offsetX, offsetY, 0, col + offsetX, yBounds + offsetY, 0);
    }
    for (float row = 0; row <= yBounds; row += spacing) {
      applet.line(offsetX, row + offsetY, 0, xBounds + offsetX, row + offsetY, 0);
    }

  }

  private void drawLine(Applet applet, Vector a, Vector b) {
    for (int i = -modSize; i <= modSize; i++)
      for (int j = -modSize; j <= modSize; j++) {
        Vector mod = new Vector(i * X, j * Y, 0);
        Tools.drawLine(applet, V.add(a, mod), V.add(b, mod), magnif);
      }
  }

  private void drawPhoton(Applet applet, Vector a) {
    for (int i = -modSize; i <= modSize; i++)
      for (int j = -modSize; j <= modSize; j++) {
        Vector mod = new Vector(i * X, j * Y, 0);
        Tools.drawPhoton(applet, V.add(a, mod), photonRadius, magnif, 1);
      }
  }

  private void drawRibbon(Applet applet, Vector a, Vector b, Vector c, Vector d, double e) {
    //float xMapping = 1, yMapping = (float) (xMapping * applet.CNTimg.width * zBounds / (e * applet.CNTimg.height));
    for (int i = -modSize; i <= modSize; i++)
      for (int j = -modSize; j <= modSize; j++) {
        Vector mod = new Vector(i * X, j * Y, 0);
        applet.beginShape();
        //applet.texture(applet.CNTimg);
        Tools.vertex(applet, V.mult(magnif, V.add(a, mod)));//, 0, yMapping);
        Tools.vertex(applet, V.mult(magnif, V.add(b, mod)));//, xMapping, yMapping);
        Tools.vertex(applet, V.mult(magnif, V.add(c, mod)));//, xMapping, 0);
        Tools.vertex(applet, V.mult(magnif, V.add(d, mod)));//, 0, 0);
        applet.endShape(applet.CLOSE);
      }
  }

  private void drawPolygon(Applet applet, List<Vector> bounds) {
    for (int i = -modSize; i <= modSize; i++)
      for (int j = -modSize; j <= modSize; j++) {
        Vector mod = new Vector(i * X, j * Y, 0);
        applet.beginShape();
        for (int k = 0; k < bounds.size(); k++)
          Tools.vertex(applet, V.mult(magnif, V.add(bounds.get(k), mod)));
        applet.endShape(applet.CLOSE);
      }
  }

}
