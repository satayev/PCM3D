package graphics;

import java.util.*;

import simple.*;
import pcm.geom.Vector;
import pcm.util.V;
import processing.core.*;

/**
 * Class utilized by Applet class to draw photons, CNT towers on a solar cell.
 * Doubles casted to floats to accommodate float-specific Processing function parameters.
 * 
 * @author Susan
 */
public class AppletModel {

  private Applet applet; // parent applet
  Tools tools;

  private PImage CNTimg; // carbon nanotube texture
  
  // Macroscale (applet) parameters from model:
  // photonRadius and spacing in microscopic scaling with magnification applied to them,
  // x-y-zBounds (magnified) and speed of photon used in applet view
  public float photonRadius, xBounds, yBounds, zBounds, magnif;
  public double spacing, speed;
  
  private Photon currentPhoton; // newest-instantiated photon
  private ArrayList<Surface> surfaces;
  //Each photon and its trajectory (starting point, points at which it changes direction)
  private Map<Photon, ArrayList<Vector>> photons; 

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

    photons = new HashMap<Photon, ArrayList<Vector>>();
    addPhoton();

    surfaces = new ArrayList<Surface>();
    surfaces.add(new Floor());
    surfaces.add(new Ribbon(.25, .50, .50, .25));

  }

  public void addPhoton() {
    currentPhoton = new Photon();
    
    if (photons.keySet().size() == 0) { // first photon created
      currentPhoton.r = new Vector(0.9958721568311371, 0.7201843672250674, 1.0);
      currentPhoton.n = new Vector(-0.803547769282961, -0.3105094754346569, -0.507833484663695);
    }
    
    ArrayList<Vector> trajectory = new ArrayList<Vector>();
    trajectory.add(currentPhoton.r.clone());
    photons.put(currentPhoton, trajectory);
    
    // Print new photon's position and direction
    //tools.printVec("New photon ",currentPhoton.r);
    //tools.printVec("dir ",currentPhoton.n);
  }

  public boolean isOutsideBounds(Photon p) {
    return (p.r.x < -4 * Photon.X || p.r.x > 4 * Photon.X || p.r.y < -4 * Photon.Y
        || p.r.y >  4* Photon.Y || p.r.z < -4 * Photon.Z || p.r.z > 4 * Photon.Z);

  }

  public void reset() {
    photons.clear();
  }

  public void drawPhotons() {
    applet.fill(tools.yellow);
    applet.stroke(tools.yellow, 15);
    for (Photon p : photons.keySet()) {
      if (isOutsideBounds(p)){
        continue;
      }
      applet.pushMatrix();
      applet.translate((float) p.r.x * magnif, (float) p.r.y * magnif, (float) p.r.z * magnif);
      applet.sphere(photonRadius);
      applet.popMatrix();
      if (applet.keyPressed && applet.key == 'q' || applet.runAnim)// animSpeed*frame>1)
      {
        p.move(speed);
        // tools.printVec("Photon position ",p.position());
        for (Surface s : surfaces) {
          double dist = s.collisionDistance(p);
          // System.out.println("Distance until collision = "+dist);
          if (dist < .05) {
            s.collision(p);
            photons.get(p).add(p.r.clone());
          }
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
        tools.vertex(r.corners[0].multiple(magnif), 0, yMapping);
        tools.vertex(r.corners[1].multiple(magnif), xMapping, yMapping);
        tools.vertex(new Vector(r.corners[1].x, r.corners[1].y, zBounds).multiple(magnif), xMapping, 0);
        tools.vertex(new Vector(r.corners[0].x, r.corners[0].y, zBounds).multiple(magnif), 0, 0);
        applet.endShape(applet.CLOSE);
      }
    }
  }

  public void drawTrajectory() {
    applet.strokeWeight(2);
    for (Photon p : photons.keySet()) {
      if (p == currentPhoton)
        applet.stroke(tools.red);
      else
        applet.stroke(tools.yellow);
      ArrayList<Vector> trajectory = photons.get(p);
      Vector from = trajectory.get(0);
      for (int i = 1; i < trajectory.size(); i++) {
        Vector to = trajectory.get(i);
        tools.drawLine(from.multiple(magnif), to.multiple(magnif));
        from = to.clone();
      }
      tools.drawLine(from.multiple(magnif), p.r.multiple(magnif));
    }
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
