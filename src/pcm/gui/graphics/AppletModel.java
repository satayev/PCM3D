package pcm.gui.graphics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import pcm.AbsorptionSimulation;
import pcm.PCM3D;
import pcm.StatisticalEvaluator;
import pcm.model.RectangularPrismModel;
import pcm.model.Statistics;
import pcm.model.geom.V;
import pcm.model.geom.Vector;
import pcm.model.geom.curves.Polygon;
import pcm.model.geom.solids.Prism;
import pcm.model.orbit.ISSOrbit;
import pcm.model.statics.Wavelength;
import pcm.model.statics.WavelengthAM0;
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

  // Variables edited outside of AppletModel
  public static boolean runAnim = false;
  public static String printOutput = "";

  // Variables for drawing the model
  public static float magnif = 250;
  public Applet applet;
  public double speed = .15; // speed of traveling photons used in applet view
  /**
   * Zenith angle is [0, 90) degrees, 0 being straight down, 90 straight towards the side
   * Azimuth is degrees to the x-y axis
   * Tilt Angle is the tilt of the rotation, with 0 degrees being upwards
   */
  public static double zenith = 50, azimuth = 0, tiltAngle = 0;
  public boolean updated = true;
  
  // Model microscale bounds
  public double X = 1, Y = 1, Z = 1, Z0 = 1;

  // TODO need to reconsider the following vars
  public Vector sunDir = new Vector(), sunPos = new Vector();
  public double sunDistance = 2, dzenith = -2.5; // delta is change in zenith for moving sun

  public List<PathFollower> paths = new LinkedList<PathFollower>(), 
		  pathsCopy = new LinkedList<PathFollower>(); // for re-using paths list which has been altered
  /** The number of paths displayed on the screen */
  public int runningPaths = 0;
  /** indicates weather to draw a new photon when one wanders off screen */
  public boolean continuousRunning = true;

  // The model to use
  public SimpleFixedModel SFM;
  public AbsorptionSimulation AS;

  public List<Tower> LT;
  public boolean runSimpleModel = false; // whether to use the simple model

  public List<List<Vector>> edgelists = new ArrayList<List<Vector>>();
  public RectangularPrismModel RPM = new RectangularPrismModel(X, Y, Z);

  public StatisticalEvaluator SE = new StatisticalEvaluator();

  // The start time for the simulation
  public long startTime;
  public long lastTime = 0;

  // The size of the repeated drawing
  int modSize = 0;
  int maxPhotons = 40;
  
  public boolean useAngles = false, useStatisticalResults = true, useOrbit = true;
  public double orbitStartTime = 0, orbitStepSize = 1, orbitLength = 90;
  public int statisticsCounter = 0;
  
  public Vector rotationVector = new Vector(1,0,0);
  public double rotationAngle = Math.PI / 36;
  public int rotationSize = 72;
  
  public Vector[] rotationMatrix = new Vector[3];
  
  public List<Vector> vectorList = new ArrayList<Vector>();

  public AppletModel() {

    /** Tower initialization here */
    // TODO - have parameters programmatically initialized - variable length of vertices
    // TODO - use dev.simple Tower or pcm.model Prism as default tower data structure?
    LT = new ArrayList<Tower>();
    List<Double> Lx;
    List<Double> Ly;
    //      Lx = Arrays.asList(.5, .75, .5, .25);
    //      Ly = Arrays.asList(.25, .5, .75, .5);
    //      LT.add(new Tower(Lx, Ly));
    //      Lx = Arrays.asList(.1, .3, .4, .2);
    //      Ly = Arrays.asList(.3, .2, .8, .7);
    //      LT.add(new Tower(Lx, Ly));
    Lx = Arrays.asList(.5, .8, .7, .7);
    Ly = Arrays.asList(.5, .6, .8, .9);
    LT.add(new Tower(Lx, Ly));
    Lx = Arrays.asList(.2, .4, .5, .4, .2, .1);
    Ly = Arrays.asList(.1, .1, .3, .5, .5, .3);
    LT.add(new Tower(Lx, Ly));

    /** dev.simple model initialization here */
    if (runSimpleModel) {

      FixedPhoton photon = new FixedPhoton(new Vector(0, 0, -1));
      SFM = new SimpleFixedModel(X, Y, Z0, 0, LT, photon);

    }
    /** pcm.model model initialization here */
    else {
      // TODO - Currently uses each first four vertices of initialized towers
      for (Tower t : LT) {
        RPM.cnts.add(new Prism(new Vector(), new Polygon(new Vector[] { new Vector(t.Lx.get(0), t.Ly.get(0), 0),
            new Vector(t.Lx.get(1), t.Ly.get(1), 0),
            new Vector(t.Lx.get(2), t.Ly.get(2), 0), new Vector(t.Lx.get(3), t.Ly.get(3), 0) })));
      }
      ISSOrbit orbit = new ISSOrbit();
      AS = new AbsorptionSimulation(RPM, orbit);

    }

    printOutput = "Zenith Angle\tAzimuth Angle\tAbsorption\n\n";
    startTime = System.currentTimeMillis();
    
    setRotationMatrix(Math.PI, 0, 0);
    
    reset();
    
  }

  /*
   * Model rerun here with reset solar flux angles
   */
  public void run() {//boolean newRun) {
	String simResults = "";
	  
//	if (!reset && advanceSun)
//		solarAdvance();
	
	
    if ((zenith < 0 || zenith >= 90) && !useStatisticalResults || useStatisticalResults && SE.points != null && statisticsCounter >= SE.points.size()) {
      runAnim = false;
    } else {
      if (runSimpleModel) {
        SFM.clear();
        SFM.p.stat.N = maxPhotons;
        SFM.p.stat.X = maxPhotons;

        // Cautious Incorporation of applet.xRotation and applet.yRotation into angles
        float xRot = applet.xRotation, yRot = applet.yRotation;
        yRot = (yRot == (float) Math.PI / 2) ? 0 : applet.yRotation;
        //System.out.println("appletmodel " + xRot + " " + yRot);
        double zenith0 = Math.PI * (180 - (90 - zenith)) / 180
            + (Math.PI - xRot) + (Math.PI - yRot), 
            azimuth0 = Math.PI * azimuth / 180
            + (Math.PI - yRot);
        Vector entry = new Vector(Math.cos(zenith0) * Math.cos(azimuth0), Math.cos(zenith0) * Math.sin(azimuth0), -Math
            .sin(zenith0));

        SFM.setEntry(entry);
        SFM.run(10000);
        SFM.p.stat.printAll();
        simResults += zenith + " degrees\t" + azimuth + " degrees\t" + SFM.p.stat.getRatio() + " %" + "\n";

        List<List<List<Vector>>> photonPaths = SFM.p.stat.rv;
        for (List<List<Vector>> i : photonPaths)
          paths.add(new PathFollower(i));

      } else {
        if (useStatisticalResults) {
          if (SE.points != null && statisticsCounter < SE.points.size()) {
            List<List<List<Vector>>> photonPaths = SE.points.get(statisticsCounter).photonPaths;
            Vector shift = new Vector(X/2,Y/2,0);
            for (List<List<Vector>> i : photonPaths) {
              List<List<Vector>> i0 = new ArrayList<List<Vector>>();
              for (List<Vector> j : i) {
                List<Vector> j0 = new ArrayList<Vector>();
                for (Vector k : j) j0.add(V.add(k,shift));
                i0.add(j0);
              }
              paths.add(new PathFollower(i0));
            }
            statisticsCounter++;
          }
        } else {
          try {
            System.out.println("start");
            AS.stats = new Statistics();
            AS.stats.maxPhotonPaths = maxPhotons;
            AS.stats.maxPhotonAbsorptionPoints = maxPhotons;
            System.out.println("clear");
  
            double zenith0 = Math.PI / 2 - Math.PI * zenith / 180, azimuth0 = Math.PI * azimuth / 180;
            AS.run(1000,
                new Vector(Math.cos(zenith0) * Math.cos(azimuth0), Math.cos(zenith0) * Math.sin(azimuth0), -Math.sin(zenith0)));
            System.out.println("finish");
            AS.printStats();
            simResults += zenith + " degrees\t" + azimuth + " degrees\t"
                + (AS.stats.photonAbsorbedCounter * 1. / AS.stats.photonTotalCounter) + " %" + "\n";
  
            List<List<List<Vector>>> photonPaths = AS.stats.photonPaths;
            Vector shift = new Vector(X/2,Y/2,0);
            for (List<List<Vector>> i : photonPaths) {
              for (List<Vector> j : i) for (Vector k : j) k.add(shift);
              paths.add(new PathFollower(i));
            }
            System.out.println("print");
  
          } catch (Exception E) {
            E.printStackTrace();
          }
        }

      }
      System.out.println("run with zenith = " + zenith + ", azimuth = " + azimuth);
      //if (newRun)
    	  printOutput += simResults;
      // test
//      System.out.println(paths.size()+ " "+runningPaths);
//      pathsCopy.clear();
//      pathsCopy.addAll(paths);
      
      // Position and direction of the sun set from Photon class's baseline values for spawned photons with sunDistance taken into account
      sunPos = new Vector(.5, 1 - zenith / 180, Math.sin((1 - zenith / 180) * Math.PI));
      Vector tileCenter = new Vector(.5, .5, 0);
      tileCenter.sub(sunPos);
      tileCenter.normalize();
      sunDir = tileCenter.clone();
      sunPos.sub(V.mult(sunDistance, sunDir));


      solarAdvance();
    }
  }

  public void solarAdvance() {
      if (zenith == 0 && zenith == 90) {
          dzenith *= -1;
          azimuth = (azimuth + 180) % 360;
        }
        zenith += dzenith;
        if (useStatisticalResults) {
          Vector entry;
          if (statisticsCounter < vectorList.size()) {
            entry = vectorList.get(statisticsCounter);
          } else if (!vectorList.isEmpty()){
            entry = vectorList.get(vectorList.size()-1);
          } else {
            entry = new Vector(0,0,1);
          }
          azimuth = SE.vectorToAzimuth(entry);
          zenith = SE.vectorToZenith(entry);
          
          ISSOrbit orbit = new ISSOrbit();
          Vector p = orbit.getISSPosition(orbitStartTime).clone();
          Vector p0 = V.normalize(p);
          Vector v = orbit.getISSVelocity(orbitStartTime).clone();
          double latitude = Math.asin(p0.z) * 180 / Math.PI;
          p0.z = 0;
          p0.normalize();
          double longitude = Math.acos(p0.x) * 180 / Math.PI; // assuming longitude = 0 at x-axis
          boolean headedTowardEquator = latitude > 0 ^ v.z > 0;
          Vector s = orbit.getSunlightDirection(orbitStartTime);
          
          orbitStartTime += orbitStepSize;
        }
  }
  
  public void addPhoton() {
    runningPaths++;
  }

  public void reset() {
    paths.clear();
    System.out.println("reset");
    if (useOrbit) {
      int steps = (int) (orbitLength / orbitStepSize);
      ISSOrbit orbit = new ISSOrbit();
      vectorList = new ArrayList<Vector>();
      for (int i = 0; i <= steps; i++) 
        vectorList.add(orbit.getSunlightDirection(orbitStartTime + orbitStepSize * i));
    } else {
      Vector entry = SE.angleToVector(zenith, azimuth);
      rotationVector = SE.generateRotationVector(entry, tiltAngle);
      vectorList = SE.generateVectors(entry, rotationVector, rotationAngle, rotationSize);
    }
    
    List<Vector> currentVectorList = new ArrayList<Vector>(vectorList.size());
    System.out.println("VectorList: "+vectorList.get(0));
    for (Vector i : vectorList) {
      if (i == null) currentVectorList.add(i);
      else {
        Vector rotated = V.matrixRotate(rotationMatrix, new Vector(-i.y,-i.x,-i.z));
        currentVectorList.add(new Vector(-rotated.y,-rotated.x,-rotated.z));
      }
    }
    System.out.println("VectorList: "+currentVectorList.get(0));
    SE.runSimulation(currentVectorList, new WavelengthAM0(), maxPhotons, 1000);
  }
  
  public void setRotationMatrix(double xRotation, double yRotation, double zRotation) {
    rotationMatrix[0] = new Vector(1,0,0);
    rotationMatrix[1] = new Vector(0,1,0);
    rotationMatrix[2] = new Vector(0,0,1);
    for (Vector i : rotationMatrix)
      i.set(V.rotateAbout(i, new Vector(1,0,0), -xRotation));
    for (Vector i : rotationMatrix)
      i.set(V.rotateAbout(i, new Vector(0,1,0), -yRotation));
    for (Vector i : rotationMatrix)
      i.set(V.rotateAbout(i, new Vector(0,0,1), -zRotation));
  }

  /*
   * Draws entire model/solar cell
   */
  public void draw(Applet applet, boolean updatePhotons) {
    this.applet = applet;

    drawFloorGrid();
    
//    applet.shader(applet.heatmap);
    drawSurfaces();
//    applet.resetShader();
    
    drawPhotons(updatePhotons);
  }

  /*
   * 
   * @param updatePhotons whether or not the model needs to be updated which should be once every frame, so one out of the views
   * drawn needs this set as true
   */
  public void drawPhotons(boolean updatePhotons) {
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
      if (path.drawOnPath(applet)) {
        i.remove();
        if (!continuousRunning)
          runningPaths--;
      }
    }

    if (updatePhotons && (applet.keyPressed && applet.key == 'q' || runAnim)) {
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

    /*
     * List<List<Vector>> path: i is photon index, branch is which number of times it has been wrapped to other side, line is
     * which path line segment it is on in this branch
     */
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
    public boolean drawOnPath(Applet applet) {

      int branch0 = branch, line0 = line, reflections0 = reflections;
      double distance0 = distance;
      if (branch0 < path.size()) {
        List<Vector> lineList = path.get(branch0);
        Vector a, b, start, finish = new Vector(); // = lineList.get(line0 + 1);
        double remainingDistance = distance0 + maxDistance;
        boolean end = false;
        int color = Tools.colorScale[0];
        if (lineList.size() >= 2) {
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
            while ((power /= 2) > 0)
              index++;
            if (index > 8)
              index = 8;
            color = Tools.colorScale[index];
            applet.fill(color);
            applet.stroke(color);
            applet.strokeWeight(2);
            drawLine(start, finish);
  
            line0++;
            reflections0++;
            while (line0 >= lineList.size() - 1) {
              line0 = 0;
              branch0++;
              reflections0--;
              if (branch0 >= path.size())
                end = true;
              else
                lineList = path.get(branch0);
            }
          }
        }
        if (remainingDistance < 0) {
          applet.lights();
          applet.fill(color);
          applet.stroke(color);
          drawPhoton(finish);
          applet.noLights();
        }
      }
      return branch >= path.size();
    }

    public void advance(double speed) {
      if (branch < path.size()) {
        List<Vector> lineList = path.get(branch);
        distance += speed;
        if (lineList.size() >= 2) {
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

	public void reset() {
	    branch = 0; 
	    line = 0; 
	    reflections = 0;
	    distance = -maxDistance;
		
	}

  }

  /*
   * Draws towers
   * 
   * @param applet Processing applet that handles drawing
   */
  public void drawSurfaces() {
    //applet.textureMode(applet.NORMAL);
    applet.fill(Tools.black);
    applet.stroke(Tools.gray);
    applet.colorMode(applet.HSB, 100);
    double Z = Z0;
    if (runSimpleModel) {
      for (int x = 0; x < LT.size(); x++) {
        Tower t = LT.get(x);
        //applet.stroke((float)(1.0*x/LT.size()*100), 100, 100); // color denoting each tower
        //for (Surface s : surfaces) {
        //if (s instanceof Ribbon) {
        //Ribbon r = (Ribbon) s;
        for (Ribbon r : t.LS) {
          drawRibbon(new Vector(r.rx, r.ry, 0), new Vector(r.x2, r.y2, 0),
              new Vector(r.x2, r.y2, Z), new Vector(r.rx, r.ry, Z), r.b);
        }
        List<Vector> bounds = new ArrayList<Vector>();
        for (int i = 0; i < t.Lx.size(); i++)
          bounds.add(new Vector(t.Lx.get(i), t.Ly.get(i), Z));
        drawPolygon(bounds);
      }
    } else {
      Vector shift = new Vector(X/2,Y/2,0);
      for (List<Vector> i : edgelists) {
        List<Vector> i0 = new ArrayList<Vector>(i.size());
        for (Vector j : i) i0.add(V.add(j, shift));
        Vector v0 = i0.get(i0.size() - 1), v1 = i0.get(0);
        drawRibbon(v0, v1, new Vector(v1.x, v1.y, Z), new Vector(v0.x, v0.y, Z), V.sub(v0, v1).length());
        for (int j = 0; j < i0.size() - 1; j++) {
          v0 = i0.get(j);
          v1 = i0.get(j + 1);
          drawRibbon(v0, v1, new Vector(v1.x, v1.y, Z), new Vector(v0.x, v0.y, Z), V.sub(v0, v1).length());
        }
        Vector rise = new Vector(0, 0, Z);
        List<Vector> bounds = new ArrayList<Vector>(i0.size());
        for (Vector j : i0)
          bounds.add(V.add(j, rise));
        drawPolygon(bounds);
      }
    }

  }

  /**
   * Draws the solar cell grid
   * 
   * @param applet Processing applet that handles drawing
   */
  public void drawFloorGrid() {
    double spacing = .25 * magnif;
    // x-y-zBounds (magnified)
    float xBounds = (float) (X * magnif), yBounds = (float) (Y * magnif), zBounds = (float) (Z * magnif);

    spacing = .2 * magnif;
    // Floor beyond small cell
    applet.strokeWeight(2);
    applet.stroke(Tools.gray);
    float size = modSize + 1;
    float offsetX, offsetY;
    for (float col = 0; col <= xBounds * size; col += spacing) {
      applet.line(col, 0, 0, col, yBounds * size, 0);
    }
    for (float row = 0; row <= yBounds * size; row += spacing) {
      applet.line(0, row, 0, xBounds * size, row, 0);
    }

    spacing = .1 * magnif;
    // Bounds inside of which photons spawn
    applet.fill(Tools.lgray);
    applet.beginShape();
    Tools.vertexM(applet, new Vector(0, 0));
    Tools.vertexM(applet, new Vector(0, yBounds));
    Tools.vertexM(applet, new Vector(xBounds, yBounds));
    Tools.vertexM(applet, new Vector(xBounds, 0));
    applet.endShape(applet.CLOSE);
    applet.strokeWeight(2);
    applet.stroke(Tools.white);
    for (float col = 0; col <= xBounds; col += spacing) {
      applet.line(col, 0, 0, col, yBounds, 0);
    }
    for (float row = 0; row <= yBounds; row += spacing) {
      applet.line(0, row, 0, xBounds, row, 0);
    }

    // Spheres on grid for easy identification of camera orientation
    applet.lights();
    applet.sphereDetail(10);
    applet.fill(Tools.white);
    applet.stroke(Tools.white);
    Tools.drawSphere(applet, new Vector(-7, -7, 0), 14);

    applet.fill(Tools.black);
    applet.stroke(Tools.black);
    Tools.drawSphere(applet, new Vector(7 + magnif, 7 + magnif, 0), 14);

    //TODO
    // Rendering compass
    //    Vector compass = new Vector(7 + magnif, 7 + magnif, 0);
    //    Tools.arrow(this, compass, new Vector(1, 0, 0)); // south
    //    Tools.arrow(this, compass, new Vector(-1, 0, 0)); // north
    //    Tools.arrow(this, compass, new Vector(0, 1, 0)); // 
    //    Tools.arrow(this, compass, new Vector(0, -1, 0)); // 

    // Grid coordinates
    applet.fill(Tools.black);
    applet.pushMatrix();
    applet.rotate(applet.PI / 2);
    applet.textSize(20);
    //text("0, 0", -30, 15,0);
    applet.text("1, 0", magnif, 15, 0);
    applet.text("0, 1", -30, -magnif, 0);
    //text("1, 1", magnif, -magnif,0);
    applet.popMatrix();

    applet.noLights();

  }

  /*
   * Drawing functions using Tools class
   */
  private void drawLine(Vector a, Vector b) {
    for (int i = 0; i <= modSize; i++)
      for (int j = 0; j <= modSize; j++) {
        Vector mod = new Vector(i * X, j * Y, 0);
        Tools.drawMLine(applet, V.add(a, mod), V.add(b, mod), magnif);
      }
  }

  private void drawPhoton(Vector a) {
    float photonRadius = .01f * magnif;

    for (int i = 0; i <= modSize; i++)
      for (int j = 0; j <= modSize; j++) {
        Vector mod = new Vector(i * X, j * Y, 0);
        Tools.drawMPhoton(applet, V.add(a, mod), photonRadius, magnif, 1);
      }
  }

  private void drawRibbon(Vector a, Vector b, Vector c, Vector d, double e) {
    //float xMapping = 1, yMapping = (float) (xMapping * applet.CNTimg.width * zBounds / (e * applet.CNTimg.height));
    for (int i = 0; i <= modSize; i++)
      for (int j = 0; j <= modSize; j++) {
        Vector mod = new Vector(i * X, j * Y, 0);
        applet.beginShape();
        //applet.texture(applet.CNTimg);
        Tools.vertexM(applet, V.mult(magnif, V.add(a, mod)));//, 0, yMapping);
        Tools.vertexM(applet, V.mult(magnif, V.add(b, mod)));//, xMapping, yMapping);
        Tools.vertexM(applet, V.mult(magnif, V.add(c, mod)));//, xMapping, 0);
        Tools.vertexM(applet, V.mult(magnif, V.add(d, mod)));//, 0, 0);
        applet.endShape(applet.CLOSE);
      }
  }

  private void drawPolygon(List<Vector> bounds) {
    for (int i = 0; i <= modSize; i++)
      for (int j = 0; j <= modSize; j++) {
        Vector mod = new Vector(i * X, j * Y, 0);
        applet.beginShape();
        for (int k = 0; k < bounds.size(); k++)
          Tools.vertexM(applet, V.mult(magnif, V.add(bounds.get(k), mod)));
        applet.endShape(applet.CLOSE);
      }
  }

}
