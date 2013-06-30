package pcm.gui.graphics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pcm.AbsorptionSimulation;
import pcm.PCM3D;
import pcm.model.RectangularPrismModel;
import pcm.model.geom.V;
import pcm.model.geom.Vector;
import pcm.model.geom.curves.Polygon;
import pcm.model.geom.solids.Prism;
import pcm.model.orbit.ISSOrbit;
import dev.simple.FixedPhoton;
import dev.simple.Photon;
import dev.simple.Ribbon;
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

  // Macroscale (applet) parameters from model:
  // photonRadius and spacing in microscopic scaling with magnification
  // applied to them,
  // x-y-zBounds (magnified) and speed of photon used in applet view
  public float photonRadius, xBounds, yBounds, zBounds, magnif;
  public double spacing, speed;
  public Vector sunDir = new Vector(), sunPos = new Vector();
  public double sunDistance, degrees, dt; // 90 minus degrees of zenith angle at which the sun starts, dt is change in degrees for moving sun

  // Variables for interfacing with the statistic class
  public List<List<List<Vector>>> paths;

  public List<Integer> currentBranch = new ArrayList<Integer>(), currentLine = new ArrayList<Integer>();
  public List<Double> currentDistance = new ArrayList<Double>();
  public double maxDistance = .5; // The length of the trailing tail

  // The model to use
  public SimpleModel SM;
  public AbsorptionSimulation AS;

  public List<Tower> LT;
  public boolean runSimpleModel = true; // whether to use the simple model

  public double X = 1, Y = 1, Z = 1, Z0 = 1;
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
    this.maxPhotons = maxPhotons + 1; //*sizechange*
    this.modSize = modSize - 1;
    this.degrees = degrees;
    paramsChanged = true;
    run();
  }

  public AppletModel() {
    this(2.5, 100, 1);
  }

  public AppletModel(double degrees, int maxPhotons, int modSize) {

    // Setup of applet view from model
    magnif = 250;
    photonRadius = (float) .01 * magnif;
    spacing = .25 * magnif;
    xBounds = (float) (Photon.X * magnif);
    yBounds = (float) (Photon.Y * magnif);
    zBounds = (float) (Photon.Z);
    speed = .05;
    sunDistance = 2;
    this.degrees = degrees;
    dt = 2.5;
    this.maxPhotons = maxPhotons - 1; //*sizechange*
    this.modSize = modSize - 1;

    if (runSimpleModel) {
      Photon.X = X;
      Photon.Y = Y;
      Photon.Z = Z0;

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
      SM = new SimpleModel(LT, photon);
      //SM.p.stat.N = 0;
      //SM.p.stat.X = 0;

      paths = new ArrayList<List<List<Vector>>>();

    } else {
      RPM.cnts.add(new Prism(new Vector(), new Polygon(new Vector[] { new Vector(0, -.25, 0), new Vector(.25, 0, 0), new Vector(0, .25, 0),
          new Vector(-.25, 0, 0) })));
      ISSOrbit orbit = new ISSOrbit();
      AS = new AbsorptionSimulation(RPM, orbit);

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
      SM.p.stat = new Statistic();
      SM.p.stat.N = maxPhotons;
      SM.p.stat.X = maxPhotons;

      SM.p.degrees = degrees; // Photon class addition
      SM.run(1000);
      printOutput += degrees + " degrees\t\t\t" + SM.p.stat.getRatio() + " %" + "\n";

      //	    if (paramsChanged)
      //	    	paths = new ArrayList<List<List<Vector>>>();
      paramsChanged = false;
      paths.addAll(SM.p.stat.rv);

      //*sizechange*
      // TODO bug for last photon in simple model? 
      paths.remove(paths.size() - 1);
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

    int maxSize;
    if (runSimpleModel)
      maxSize = paths.size();
    else
      maxSize = AS.stats.photonPaths.size();

    // for some reason last photon has strange path cutting through towers so edited as "maxSize - 1"
    if (currentBranch.size() < maxSize) {
      currentBranch.add(0);
      currentLine.add(0);
      //currentDistance.add(-maxDistance);
      currentDistance.add(0.0);
    } else {
      run();
    }

  }

  public void reset() {
    currentBranch.clear();
    currentLine.clear();
    currentDistance.clear();
  }

  public void drawPhotons(Applet applet, boolean updatePhotons, boolean test) {

    /*
     * List<List<List<Vector>>> paths
     * i is photon index
     * currentbranch is which number of times it has been wrapped to other side
     * line is which path line segment it is on in this branch
     */

    //*sizechange*  "+ 1"
    for (int i = Math.max(0, currentBranch.size() - SM.p.stat.N + 1); i < currentBranch.size(); i++) {
      //for (int i = 0; i < currentBranch.size(); i++) {    

      applet.stroke(Tools.yellow);
      applet.strokeWeight(2);

      int branch = currentBranch.get(i);
      int line = currentLine.get(i);
      double distance = currentDistance.get(i);
      List<List<Vector>> branchList;

      if (runSimpleModel)
        branchList = paths.get(i);//SM.p.stat.rv.get(i);
      else
        branchList = AS.stats.photonPaths.get(i);

      List<Vector> lineList;
      //int absorptions = i%100;
      double absorptions = 0;
      for (int a = 0; a < branch; a++) {
        absorptions += branchList.get(a).size();
      }
      absorptions -= (branch) * 2;
      if (maxPhotons == 1 || maxPhotons == 2)
        System.out.println("absorptions: " + absorptions);
      absorptions = Math.log(absorptions);
      absorptions /= 3;
      // Color for each absorption number and lower transparency for older photon paths
      applet.colorMode(applet.HSB, 100);
      if (currentBranch.size() <= SM.p.stat.N)
        applet.stroke((float) absorptions * 100, 100, 100, (float) (255 * (i + 1.0) / (currentBranch.size())));
      else
        applet.stroke((float) absorptions * 100, 100, 100, (float) (255 * (i + 1.0 - currentBranch.size() + SM.p.stat.N) / SM.p.stat.N));
      //      if (currentBranch.size()<=SM.p.stat.N/2)
      //        applet.stroke(Tools.yellow, (float)(255*(i+1.0)/(currentBranch.size())));
      //      else
      //        applet.stroke(Tools.yellow, (float)(255*(i+1.0-currentBranch.size()+SM.p.stat.N/2)/(SM.p.stat.N/2)));

      for (int j = 0; j < branch; j++) {
        lineList = branchList.get(j);
        for (int k = 0; k < lineList.size() - 1; k++) {
          drawLine(applet, lineList.get(k), lineList.get(k + 1));
        }
      }
      if (branch < branchList.size()) {
        lineList = branchList.get(branch);
        for (int k = 0; k < line; k++) {
          drawLine(applet, lineList.get(k), lineList.get(k + 1));
        }
        Vector finalPoint = lineList.get(line).clone(), direction = lineList.get(line + 1).clone();
        direction.sub(finalPoint);
        double distRemaining = direction.length();
        direction.normalize();
        finalPoint.add(V.mult(distance, direction));

        drawLine(applet, lineList.get(line), finalPoint);

        applet.fill(Tools.gold);
        applet.stroke(Tools.gold);
        drawPhoton(applet, finalPoint);

        if (updatePhotons && (applet.keyPressed && applet.key == 'q' || runAnim) && test)
          advancePhotons(i, branch, line, distance, lineList, distRemaining);

      }
    }

  }

  public void advancePhotons(int i, int branch, int line, double distance, List<Vector> lineList, double distRemaining) {
    distance += speed;
    if (distance >= distRemaining) {
      distance -= distRemaining;
      line++;
      if (line >= lineList.size() - 1) {
        line = 0;
        branch++;
      }
    }
    currentBranch.set(i, branch);
    currentLine.set(i, line);
    currentDistance.set(i, distance);

  }

  /*
   * Error in this code causing unexplainable crash
   * public void drawPhotons(Applet applet, boolean updatePhotons) {
   * if (updatePhotons) {
   * long time = System.currentTimeMillis() - startTime;
   * Vector entryVector = getEntryVector(time);
   * double prob = .001 * -entryVector.z;
   * long timeDelay = time - lastTime;
   * if (prob * timeDelay > PCM3D.rnd.nextDouble()) {
   * if (runSimpleModel) {
   * SM.p.n0 = entryVector;
   * SM.p.stat.N++;
   * SM.run(1);
   * } else {
   * AS.stats.maxPhotonPaths++;
   * try {
   * AS.run(1, entryVector);
   * } catch (Exception e) {
   * // TODO Auto-generated catch block
   * e.printStackTrace();
   * }
   * }
   * addPhoton();
   * }
   * lastTime = time;
   * }
   * 
   * for (int i = 0; i < currentBranch.size(); i++) {
   * 
   * applet.fill(Tools.yellow);
   * applet.stroke(Tools.yellow);
   * applet.strokeWeight(2);
   * 
   * int branch = currentBranch.get(i);
   * int line = currentLine.get(i);
   * double distance = currentDistance.get(i);
   * List<List<Vector>> branchList;
   * List<Vector> lineList;
   * if (runSimpleModel)
   * branchList = SM.p.stat.rv.get(i);
   * else
   * branchList = AS.stats.photonPaths.get(i);
   * if (branch < branchList.size()) {
   * lineList = branchList.get(branch);
   * Vector a, b, start, finish = lineList.get(line + 1);
   * double remainingDistance = distance + maxDistance;
   * while (remainingDistance > 0) {
   * a = lineList.get(line);
   * b = lineList.get(line + 1);
   * double length = V.sub(b, a).length();
   * Vector normal = V.normalize(V.sub(b, a));
   * if (remainingDistance - maxDistance < 0)
   * start = a.clone();
   * else
   * start = V.scaleAdd(a, remainingDistance - maxDistance, normal);
   * if (remainingDistance > length)
   * finish = b.clone();
   * else
   * finish = V.scaleAdd(a, remainingDistance, normal);
   * remainingDistance = remainingDistance - length;
   * //drawLine(applet, start, finish);
   * line++;
   * while (line >= lineList.size() - 1) {
   * line = 0;
   * branch++;
   * if (branch >= branchList.size())
   * break;
   * lineList = branchList.get(branch);
   * }
   * }
   * if (remainingDistance <= 0) {
   * applet.fill(Tools.gold);
   * applet.stroke(Tools.gold);
   * //drawPhoton(applet, finish);
   * }
   * 
   * // for (int j = Math.max(branch - 2, 0); j < branch; j++) {
   * // lineList = branchList.get(j);
   * // for (int k = 0; k < lineList.size() - 1; k++)
   * // drawLine(applet, lineList.get(k), lineList.get(k + 1));
   * // }
   * //
   * // lineList = branchList.get(branch);
   * // for (int k = 0; k < line; k++)
   * // drawLine(applet, lineList.get(k), lineList.get(k + 1));
   * // Vector finalPoint = lineList.get(line).clone(), direction = lineList.get(line + 1).clone();
   * // direction.sub(finalPoint);
   * // double distRemaining = direction.length();
   * // direction.normalize();
   * // finalPoint.add(V.mult(distance, direction));
   * //
   * // drawLine(applet, lineList.get(line), finalPoint);
   * //
   * // applet.fill(Tools.gold);
   * // applet.stroke(Tools.gold);
   * // drawPhoton(applet, finalPoint);
   * 
   * if (updatePhotons && (applet.keyPressed && applet.key == 'q' || applet.runAnim))
   * advancePhotons(i);
   * }
   * 
   * }
   * 
   * }
   * 
   * private Vector getEntryVector(long time) {
   * double theta = 7 * Math.PI / 8, phi = Math.PI * time / 200000;
   * return new Vector(Math.cos(theta) * Math.cos(phi), Math.sin(theta) * Math.cos(phi), -Math.sin(phi));
   * }
   * 
   * public void advancePhotons(int i) {
   * int branch = currentBranch.get(i);
   * int line = currentLine.get(i);
   * double distance = currentDistance.get(i);
   * List<List<Vector>> branchList;
   * if (runSimpleModel)
   * branchList = SM.p.stat.rv.get(i);
   * else
   * branchList = AS.stats.photonPaths.get(i);
   * if (branch < branchList.size()) {
   * List<Vector> lineList = branchList.get(branch);
   * distance += speed;
   * double length = V.sub(lineList.get(line), lineList.get(line + 1)).length();
   * if (distance >= length) {
   * distance -= length;
   * line++;
   * if (line >= lineList.size() - 1) {
   * line = 0;
   * branch++;
   * }
   * }
   * currentBranch.set(i, branch);
   * currentLine.set(i, line);
   * currentDistance.set(i, distance);
   * }
   * 
   * }
   */

  public void drawSurfaces(Applet applet) {
    //applet.textureMode(applet.NORMAL);
    applet.fill(Tools.black);
    applet.stroke(Tools.gray);
    applet.colorMode(applet.HSB, 100);
    double Z = (runSimpleModel) ? Photon.Z : Z0;
    for (int x = 0; x < LT.size(); x++) {
      Tower t = LT.get(x);
      //applet.stroke((float)(1.0*x/LT.size()*100), 100, 100); // color denoting each tower
      //for (Surface s : surfaces) {
      //if (s instanceof Ribbon) {
      //Ribbon r = (Ribbon) s;
      for (Ribbon r : t.LS) {
        drawRibbon(applet, new Vector(r.rx, r.ry, 0), new Vector(r.x2, r.y2, 0), new Vector(r.x2, r.y2, Z), new Vector(r.rx, r.ry, Z), r.b);
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
        Vector mod = new Vector(i * Photon.X, j * Photon.Y, 0);
        Tools.drawLine(applet, V.add(a, mod), V.add(b, mod), magnif);
      }
  }

  private void drawPhoton(Applet applet, Vector a) {
    for (int i = -modSize; i <= modSize; i++)
      for (int j = -modSize; j <= modSize; j++) {
        Vector mod = new Vector(i * Photon.X, j * Photon.Y, 0);
        Tools.drawPhoton(applet, V.add(a, mod), photonRadius, magnif, 1);
      }
  }

  private void drawRibbon(Applet applet, Vector a, Vector b, Vector c, Vector d, double e) {
    //float xMapping = 1, yMapping = (float) (xMapping * applet.CNTimg.width * zBounds / (e * applet.CNTimg.height));
    for (int i = -modSize; i <= modSize; i++)
      for (int j = -modSize; j <= modSize; j++) {
        Vector mod = new Vector(i * Photon.X, j * Photon.Y, 0);
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
        Vector mod = new Vector(i * Photon.X, j * Photon.Y, 0);
        applet.beginShape();
        for (int k = 0; k < bounds.size(); k++)
          Tools.vertex(applet, V.mult(magnif, V.add(bounds.get(k), mod)));
        applet.endShape(applet.CLOSE);
      }
  }

}
