package graphics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dev.simple.Photon;
import dev.simple.Ribbon;
import dev.simple.SimpleModel;
import dev.simple.Surface;
import dev.simple.Tower;

import pcm.util.V;
import pcm.util.Vector;
import processing.core.PImage;

/**
 * Class utilized by Applet class to draw photons, CNT towers on a solar cell.
 * Doubles casted to floats to accommodate float-specific Processing function
 * parameters.
 * 
 * @author Susan
 *         Revised by John
 */
public class AppletModel {

  private Applet applet; // parent applet
  Tools tools;

  private PImage CNTimg; // carbon nanotube texture

  // Macroscale (applet) parameters from model:
  // photonRadius and spacing in microscopic scaling with magnification
  // applied to them,
  // x-y-zBounds (magnified) and speed of photon used in applet view
  public float photonRadius, xBounds, yBounds, zBounds, magnif;
  public double spacing, speed;

  // Variables for interfacing with the statistic class
  public List<List<List<Vector>>> paths;
  public List<Integer> currentBranch = new ArrayList<Integer>(), currentLine = new ArrayList<Integer>();
  public List<Double> currentDistance = new ArrayList<Double>();

  // The model to use
  public SimpleModel SM;

  // The surfaces to render
  private List<Surface> surfaces;

  public AppletModel(Applet p, Tools t) {
    applet = p;
    tools = t;

    CNTimg = applet.loadImage("cnt.jpg");

    // Setup of applet view from model
    magnif = 250;
    photonRadius = (float) .01 * magnif;
    spacing = .25 * magnif;
    xBounds = (float) (Photon.X * magnif);
    yBounds = (float) (Photon.Y * magnif);
    zBounds = (float) (Photon.Z * 5);
    speed = .01;

    List<Tower> LT = new ArrayList<Tower>();
    List<Double> Lx = Arrays.asList(.3, .8, .7, .2);
    List<Double> Ly = Arrays.asList(.2, .3, .8, .7);
    LT.add(new Tower(Lx, Ly));
    SM = new SimpleModel(LT, new Photon());
    SM.p.stat.N = 100;
    SM.p.stat.X = 100;
    SM.run(100000);
    SM.p.stat.print();

    addPhoton();

    surfaces = SM.LS;
    paths = SM.p.stat.rv;

  }

  public void addPhoton() {
    if (currentBranch.size() < SM.p.stat.N) {
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

  public void drawPhotons() {
    applet.fill(tools.yellow);
    applet.stroke(tools.yellow, 15);
    for (int i = 0; i < currentBranch.size(); i++) {
      int branch = currentBranch.get(i);
      int line = currentLine.get(i);
      double distance = currentDistance.get(i);
      List<List<Vector>> branchList = paths.get(i);
      List<Vector> lineList;
      for (int j = 0; j < branch; j++) {
        lineList = branchList.get(j);
        for (int k = 0; k < lineList.size() - 1; k++)
          tools.drawLine(V.mult(magnif, lineList.get(k)), V.mult(magnif, lineList.get(k + 1)));
      }
      if (branch < branchList.size()) {
        lineList = branchList.get(branch);
        for (int k = 0; k < line; k++) {
          tools.drawLine(V.mult(magnif, lineList.get(k)), V.mult(magnif, lineList.get(k + 1)));
        }
        Vector finalPoint = lineList.get(line).clone(), direction = lineList.get(line + 1).clone();
        direction.add(V.mult(-1, finalPoint));
        double distRemaining = direction.length();
        direction.normalize();
        finalPoint.add(V.mult(distance, direction));
        tools.drawLine(V.mult(magnif, lineList.get(line)), V.mult(magnif, finalPoint));
        applet.pushMatrix();
        applet.translate((float) finalPoint.x * magnif, (float) finalPoint.y * magnif, (float) finalPoint.z * magnif);
        applet.sphere(photonRadius);
        applet.popMatrix();
        if (applet.keyPressed && applet.key == 'q' || applet.runAnim)// animSpeed*frame>1)
        {
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

      }
    }
  }

  public void drawSurfaces() {
    applet.noStroke();
    applet.textureWrap(applet.REPEAT);
    applet.textureMode(applet.NORMAL);
    for (Surface s : surfaces) {
      if (s instanceof Ribbon) {
        Ribbon r = (Ribbon) s;
        float xMapping = 1, yMapping = (float) (xMapping * CNTimg.width * zBounds / (r.b * CNTimg.height));
        applet.beginShape();
        applet.texture(CNTimg);
        tools.vertex(V.mult(magnif, new Vector(r.rx, r.ry, 0)), 0, yMapping);
        tools.vertex(V.mult(magnif, new Vector(r.x2, r.y2, 0)), xMapping, yMapping);
        tools.vertex(V.mult(magnif, new Vector(r.x2, r.y2, zBounds)), xMapping, 0);
        tools.vertex(V.mult(magnif, new Vector(r.rx, r.ry, zBounds)), 0, 0);
        applet.endShape(applet.CLOSE);
      }
    }
  }

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
  public void drawFloorGrid() {

    // Floor extending bounds
    applet.strokeWeight(2);
    applet.stroke(tools.lgray);
    for (float col = -xBounds; col <= xBounds * 2; col += spacing) {
      applet.line(col, -yBounds, 0, col, yBounds * 2, 0);
    }
    for (float row = -yBounds; row <= yBounds * 2; row += spacing) {
      applet.line(-xBounds, row, 0, xBounds * 2, row, 0);
    }

    // Bounds inside of which photons spawn
    applet.fill(tools.lgray);
    applet.beginShape();
    tools.vertex(new Vector());
    tools.vertex(new Vector(0, yBounds));
    tools.vertex(new Vector(xBounds, yBounds));
    tools.vertex(new Vector(xBounds, 0));
    applet.endShape(applet.CLOSE);
    applet.strokeWeight(2);
    applet.stroke(tools.white);
    for (float col = 0; col <= xBounds; col += spacing) {
      applet.line(col, 0, 0, col, yBounds, 0);
    }
    for (float row = 0; row <= yBounds; row += spacing) {
      applet.line(0, row, 0, xBounds, row, 0);
    }

  }

}
