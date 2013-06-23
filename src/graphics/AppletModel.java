package graphics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pcm.PCM3D;
import pcm.model.geom.V;
import pcm.model.geom.Vector;
import dev.simple.FixedPhoton;
import dev.simple.Photon;
import dev.simple.Ribbon;
import dev.simple.SimpleModel;
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
  public double spacing, speed, maxTimeTillSystem, timeTillSystem; // time till photons travel into CNT system
  public Vector sunlightDirection, sunPosition;

  // Variables for interfacing with the statistic class
  public List<Integer> currentBranch = new ArrayList<Integer>(), currentLine = new ArrayList<Integer>();
  public List<Double> currentDistance = new ArrayList<Double>();

  // The model to use
  public SimpleModel SM;
  public List<Tower> LT;

  // The surfaces to render
  private List<Surface> surfaces;
  // The start time for the simulation
  public long startTime;
  public long lastTime = 0;
  
  // The size of the repeated drawing
  int modSize = 0;

  public AppletModel() {

    // Setup of applet view from model
    magnif = 250;
    photonRadius = (float) .01 * magnif;
    spacing = .25 * magnif;
    xBounds = (float) (Photon.X * magnif);
    yBounds = (float) (Photon.Y * magnif);
    zBounds = (float) (Photon.Z);
    speed = .01;
    maxTimeTillSystem = 200;
    timeTillSystem = maxTimeTillSystem;

    LT = new ArrayList<Tower>();
    List<Double> Lx = Arrays.asList(.5, .75, .5, .25);
    List<Double> Ly = Arrays.asList(.25, .5, .75, .5);
    LT.add(new Tower(Lx, Ly));
    FixedPhoton photon = new FixedPhoton(new Vector(0,0,-1));
    SM = new SimpleModel(LT, photon);
    SM.p.stat.N = 0;
    SM.p.stat.X = 0;
    SM.run(100000);
    SM.p.stat.print();

    surfaces = SM.LS;

    
    sunlightDirection = SM.p.n;
    //sunPosition

    startTime = System.currentTimeMillis();

  }

  public void addPhoton() {
    if (currentBranch.size() < SM.p.stat.rv.size()) {
      currentBranch.add(0);
      currentLine.add(0);
      currentDistance.add((double) 0);
    }
  }

  public void reset() {
    currentBranch.clear();
    currentLine.clear();
    currentDistance.clear();
  }

  public void drawPhotons(Applet applet, boolean updatePhotons) {
    long time = System.currentTimeMillis()-startTime;
    Vector entryVector = getEntryVector(time);
    double prob = .001*-entryVector.z;
    long timeDelay = time - lastTime;
    if (prob*timeDelay > PCM3D.rnd.nextDouble()) {
      SM.p.n0 = entryVector;
      SM.p.stat.N++;
      SM.run(1);
      addPhoton();
    }
    lastTime = time;

    for (int i = 0; i < currentBranch.size(); i++) {

      applet.fill(Tools.yellow);
      applet.stroke(Tools.yellow);
      applet.strokeWeight(2);

      int branch = currentBranch.get(i);
      int line = currentLine.get(i);
      double distance = currentDistance.get(i);
      List<List<Vector>> branchList = SM.p.stat.rv.get(i);
      List<Vector> lineList;

      /*
       * TODO: have photons start traveling further from model
       */
      //      if (timeTillSystem > 0) {
      //        lineList = branchList.get(0);
      //
      //        Vector initialPoint = lineList.get(line).clone(), finalPoint = lineList.get(line).clone(), direction = lineList.get(line+1).clone();
      //        direction.add(finalPoint.multiple(-1));
      //        direction.normalize();
      //        finalPoint.add(direction.multiple(-1 * timeTillSystem));
      //        initialPoint.add(direction.multiple(-1 * maxTimeTillSystem));
      //        
      //        Tools.drawLine(applet, initialPoint, finalPoint, magnif);
      //        Tools.drawPhoton(applet, finalPoint, photonRadius, magnif);
      //        if (windowsDrawn == windowsNum - 1 && (applet.keyPressed && applet.key == 'q' || applet.runAnim))
      //          timeTillSystem--;
      //        continue;
      //      }

      if (branch < branchList.size()) {
        for (int j = Math.max(branch-2,0); j < branch; j++) {
          lineList = branchList.get(j);
          for (int k = 0; k < lineList.size() - 1; k++) 
            drawLine(applet, lineList.get(k), lineList.get(k + 1));
        };
        lineList = branchList.get(branch);
        for (int k = 0; k < line; k++) 
          drawLine(applet, lineList.get(k), lineList.get(k + 1));
        Vector finalPoint = lineList.get(line).clone(), direction = lineList.get(line + 1).clone();
        direction.sub(finalPoint);
        double distRemaining = direction.length();
        direction.normalize();
        finalPoint.add(V.mult(distance, direction));

        drawLine(applet, lineList.get(line), finalPoint);

        applet.fill(Tools.gold);
        applet.stroke(Tools.gold);
        drawPhoton(applet, finalPoint);

        /*
         * TODO: Photons glow or leave trail behind them, alternative to trajectories
         * or have trajectories of old photons fade out to make way for new photons
         */
        // Photon trail
        //        for (double fraction = .20;fraction>.01;fraction*=.6){
        //          //System.out.println(fraction);
        //          Tools.drawPhoton(applet, V.sub(finalPoint,direction.multiple(fraction)), photonRadius*(float)(.5-fraction*2), magnif, 10);
        //        }

        if (updatePhotons && (applet.keyPressed && applet.key == 'q' || applet.runAnim))
          advancePhotons(i, branch, line, distance, lineList, distRemaining);
      }

    }

  }

  private Vector getEntryVector(long time) {
    double theta = 7*Math.PI/8, phi = Math.PI*time/200000;
    return new Vector(Math.cos(theta)*Math.cos(phi), Math.sin(theta)*Math.cos(phi), -Math.sin(phi));
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

  public void drawSurfaces(Applet applet) {
    applet.noStroke();
    //applet.textureWrap(applet.REPEAT);
    applet.textureMode(applet.NORMAL);
    for (Surface s : surfaces) {
      if (s instanceof Ribbon) {
        Ribbon r = (Ribbon) s;
        drawRibbon(applet, new Vector(r.rx, r.ry, 0), new Vector(r.x2, r.y2, 0), 
            new Vector(r.x2, r.y2, Photon.Z), new Vector(r.rx, r.ry, Photon.Z), r.b);
      }
    }
    for (Tower t : LT) {
      List<Vector> bounds = new ArrayList<Vector>();
      for (int i = 0; i < t.Lx.size(); i++)
        bounds.add(new Vector(t.Lx.get(i), t.Ly.get(i), Photon.Z));
      drawPolygon(applet, bounds);
    }

  }

  /*
   * @deprecated
   */
  @Deprecated
  public void drawTrajectory() {
    //		applet.strokeWeight(2);
    //		for (Photon p : photons.keySet()) {
    //			if (p == currentPhoton)
    //				applet.stroke(tools.red);
    //			else
    //				applet.stroke(tools.yellow);
    //			ArrayList<Vector> trajectory = photons.get(p);
    //			Vector from = trajectory.get(0);
    //			for (int i = 1; i < trajectory.size(); i++) {
    //				Vector to = trajectory.get(i);
    //				tools.drawLine(from.multiple(magnif), to.multiple(magnif));
    //				from = to.clone();
    //			}
    //			tools.drawLine(from.multiple(magnif), p.r.multiple(magnif));
    //		}
  }

  /**
   * Draws the solar cell grid
   */
  public void drawFloorGrid(Applet applet) {

    // Floor beyond small cell
    applet.strokeWeight(2);
    applet.stroke(Tools.gray);
    float size = modSize+1;
    for (float col = -xBounds * (size - 1); col <= xBounds * size; col += spacing) {
      applet.line(col, -yBounds * (size - 1), 0, col, yBounds * size, 0);
    }
    for (float row = -yBounds * (size - 1); row <= yBounds * size; row += spacing) {
      applet.line(-xBounds * (size - 1), row, 0, xBounds * size, row, 0);
    }

    // Bounds inside of which photons spawn
    applet.fill(Tools.lgray);
    applet.beginShape();
    Tools.vertex(applet, new Vector());
    Tools.vertex(applet, new Vector(0, yBounds));
    Tools.vertex(applet, new Vector(xBounds, yBounds));
    Tools.vertex(applet, new Vector(xBounds, 0));
    applet.endShape(applet.CLOSE);
    applet.strokeWeight(2);
    applet.stroke(Tools.white);
    for (float col = 0; col <= xBounds; col += spacing) {
      applet.line(col, 0, 0, col, yBounds, 0);
    }
    for (float row = 0; row <= yBounds; row += spacing) {
      applet.line(0, row, 0, xBounds, row, 0);
    }

  }

  private void drawLine(Applet applet, Vector a, Vector b) {
    for (int i = -modSize; i <= modSize; i++)
      for (int j = -modSize; j <= modSize; j++) {
        Vector mod = new Vector(i*Photon.X, j*Photon.Y, 0);
        Tools.drawLine(applet, V.add(a,mod), V.add(b,mod), magnif);
      }
  }

  private void drawPhoton(Applet applet, Vector a) {
    for (int i = -modSize; i <= modSize; i++)
      for (int j = -modSize; j <= modSize; j++) {
        Vector mod = new Vector(i*Photon.X, j*Photon.Y, 0);
        Tools.drawPhoton(applet, V.add(a,mod), photonRadius, magnif, 10);
      }
  }

  private void drawRibbon(Applet applet, Vector a, Vector b, Vector c, Vector d, double e) {
    float xMapping = 1, yMapping = (float) (xMapping * applet.CNTimg.width * zBounds / (e * applet.CNTimg.height));
    for (int i = -modSize; i <= modSize; i++)
      for (int j = -modSize; j <= modSize; j++) {
        Vector mod = new Vector(i*Photon.X, j*Photon.Y, 0);
        applet.beginShape();
        applet.texture(applet.CNTimg);
        Tools.vertex(applet, V.mult(magnif, V.add(a,mod)), 0, yMapping);
        Tools.vertex(applet, V.mult(magnif, V.add(b,mod)), xMapping, yMapping);
        Tools.vertex(applet, V.mult(magnif, V.add(c,mod)), xMapping, 0);
        Tools.vertex(applet, V.mult(magnif, V.add(d,mod)), 0, 0);
        applet.endShape(applet.CLOSE);
      }
  }

  private void drawPolygon(Applet applet, List<Vector> bounds) {
    for (int i = -modSize; i <= modSize; i++)
      for (int j = -modSize; j <= modSize; j++) {
        Vector mod = new Vector(i*Photon.X, j*Photon.Y, 0);
        applet.fill(Tools.black);
        applet.beginShape();
        for (int k = 0; k < bounds.size(); k++)
          Tools.vertex(applet, V.mult(magnif, V.add(bounds.get(k),mod)));
        applet.endShape(applet.CLOSE);
      }
  }
  
}
