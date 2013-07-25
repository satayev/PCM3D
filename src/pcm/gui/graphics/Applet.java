package pcm.gui.graphics;

import java.awt.event.*;

import javax.media.opengl.GL;

import pcm.model.geom.V;
import pcm.model.geom.Vector;
import processing.core.PApplet;
import processing.core.PImage;
import processing.opengl.PGraphicsOpenGL;

/**
 * Entry point for Processing v2.0b9 applet, usage of OpenGL, for simulation of CNT ribbon tower and any number of photons.
 * 
 * @author Susan
 */
public class Applet extends PApplet {

  int width, height; // screen size

  Earth earth;
  AppletModel model;

  //GL2 gl; // processing 2.09
  GL gl; // processing 1.5
  PGraphicsOpenGL pgl;

  boolean mouseClicked = false, mousePressedAgain = false, mouseDragged = false;
  int backgroundColor = Tools.llgray,
      buttonStroke = Tools.orange, buttonOverStroke = Tools.black, buttonFill = Tools.white;

  // Image textures
  public PImage CNTimg, NASAimg;

  AppletView[] views = new AppletView[7]; // main, top, front, left, right, back, earth
  int z = 3; // for multi-side-view changing

  // Backporch rotating cube
  float xRotation = PI, yRotation = 0;

  // Simulation time span
  int t = 0, // time counter
	photonsReiterated = 0; // associated with AppletModel's paths of photons
  double orbitDuration = 4; // in minutes 
  
  public Applet() {
    this(700, 700);
  }

  /*
   * Constructor which sets up the views (camera orientations) upon the model to draw within different viewports
   * 
   * @param w width of applet
   * 
   * @param h height of applet
   */
  public Applet(int w, int h) {
    this(w, h, new AppletModel(), new Earth());
  }

  public Applet(int w, int h, AppletModel model, Earth earth) {
    width = w;
    height = h;

    this.model = model;
    this.earth = earth;
    earth.load(this);
    
    println("Optional key controls ->\nleft arrow = slows down model animation\nright arrow = speeds up model animation\nt = resets animation\nr = runs animation\ns = stops animation\np = prints camera vectors of the view with the rotating ability");
    
    System.out.println("\nISS complete orbit will take " + orbitDuration + " minutes, ISS will be in sunlight " + (earth.ISSSunlightProportion * orbitDuration) + " minutes");

    float magnif = model.magnif;
    // Main view
    views[0] = new AppletView(this,
        //new Vector(3649.2651652694626, 640.8186570186839, 807.227290303987),
        new Vector(2367.9393409229156, 1683.7968615726913, -697.3584421318751),//modSize=1 (2*2 cells)
        //          new Vector(1189.4688663645709, 963.3238398033682, -769.9952779021853),// modSize=0 (1*1 cell)
        new Vector(-16.0, 0.0, -142.0),
        new Vector(0.0, 0.0, -1.0),
        new Vector(1.0, 0.0, 0.0),
        new Vector(0.0, 0.0, -1.0),
        new Vector(0.0, 1.0, 0.0)
        );

    // Top view
    views[1] = new AppletView(this,
        new Vector(.5 * magnif, .5 * magnif, 20 * magnif),
        new Vector(.5 * magnif, .5 * magnif, .5 * magnif),
        new Vector(-1, 0.0, 0),
        new Vector(0, 0, 1), new Vector(-1, 0, 0), new Vector(0, 1, 0)
        );

    // Left side view
    views[2] = new AppletView(this,
        new Vector(.5 * magnif, -5 * magnif, .5 * magnif),
        new Vector(.5 * magnif, .5 * magnif, .5 * magnif),
        new Vector(0.0, 0.0, -1.0),
        new Vector(0, 1, 0), new Vector(0, 0, 1), new Vector(-1, 0, 0)
        );

    // Front view
    views[3] = new AppletView(this,
        new Vector(-5 * magnif, .5 * magnif, .5 * magnif),
        new Vector(.5 * magnif, .5 * magnif, .5 * magnif),
        new Vector(0.0, 0.0, -1.0),
        new Vector(0, 1, 0), new Vector(0, 0, 1), new Vector(-1, 0, 0)
        );

    // Right side view
    views[4] = new AppletView(this,
        new Vector(.5 * magnif, 6 * magnif, .5 * magnif),
        new Vector(.5 * magnif, .5 * magnif, .5 * magnif),
        new Vector(0.0, 0.0, -1.0),
        new Vector(0, 1, 0), new Vector(0, 0, 1), new Vector(-1, 0, 0)
        );

    // Back view
    views[5] = new AppletView(this,
        new Vector(6 * magnif, .5 * magnif, .5 * magnif),
        new Vector(.5 * magnif, .5 * magnif, .5 * magnif),
        new Vector(0.0, 0.0, -1.0),
        new Vector(0, 1, 0), new Vector(0, 0, 1), new Vector(-1, 0, 0)
        );

    // Earth view
    views[6] = new AppletView(this,
        new Vector(0, 0, 5 * magnif),
        new Vector(),
        new Vector(0, 1, 0),
        //new Vector(0, 0, -1),
        new Vector(0, 1, 0), new Vector(0, 0, 1), new Vector(-1, 0, 0)
        );

  }

  /*
   * Called upon running this applet(non-Javadoc)
   * 
   * @see processing.core.PApplet#setup()
   */
  public void setup() {
    size(width, height, OPENGL);
    frameRate(60);

    //CNTimg = loadImage("cnt.jpg");
    NASAimg = loadImage("nasa.jpg");


    addMouseWheelListener(new MouseWheelListener() {
      public void mouseWheelMoved(MouseWheelEvent mwe) {
        mouseWheel(mwe.getWheelRotation());
      }
    });

  }

  /*
   * Processing applet drawing function called every frame(non-Javadoc)
   * 
   * @see processing.core.PApplet#draw()
   */
  public void draw() {
    //println(frameRate);
    background(backgroundColor);

    
    /*
     * if length of time for ISS rotation = 4 minutes = orbitDuration, earth speed scalar is (90 * 60 + 50) / (4 * 60)
     * typically 55 minutes in sunlight?
     * (proportion of time ISS in sunlight) * 4 = y = number of minutes ISS will be in sunlight
     * (delta zenith)/x = (180 degrees)/(y * 60 seconds)
     * x = delta time to re-run model
     */
//    if (model.runAnim) {
//	    earth.spin((90. * 60 + 50) / (orbitDuration * 60));
//	    
//        t++;
//	    double rerunTime = (Math.abs(model.dzenith))/(180./(earth.ISSSunlightProportion * orbitDuration * 60));
//
//        if (t * 1. / this.frameRate >= rerunTime -1 || model.updated) {
//	    	if (!model.updated) {
//	    		model.solarAdvance();
//	    		println("advancing angles, seconds until next = " + rerunTime);
//	    	}
//	    	model.updated = false;
//	    	//model.paths.clear();
//	    	//model.reset(); not working right or only corresponds to user-inputed angles
//	    	model.run(true);
//	    	t = 0;
//		    } 
//        else if (model.runningPaths < model.maxPhotons)
//            model.addPhoton();
//        else {
//        	println("repeating angles");
//        	model.run(false);
//        }
//    }
    
    
    
    if (model.runAnim) earth.spin((90. * 60 + 50) / (orbitDuration * 60));
    // Speed of animation depends on number of max photons shown
    t++;
    if (t >= (101 - model.maxPhotons) && model.runAnim) {
      if (model.runningPaths < model.maxPhotons)
        model.addPhoton();
      t = 0;
    }
    // Rerun simulation when old photons are exiting system
    if (model.runningPaths > model.paths.size())
      model.run();
    
    
    // Main view - top right viewport
    float fov = (float) (PI / 3.0); // field of view (default)
    float aspect = width / height;
    float cameraZ = ((float) (height) / tan((float) (PI * 60.0 / 360.0)));

    perspective(fov / 3, aspect, (float) (cameraZ / 10.0), (float) (cameraZ * 10.0));

    gl = ((PGraphicsOpenGL) g).beginGL();
    gl.glViewport(width / 2, height / 2, width / 2, height / 2);
    ((PGraphicsOpenGL) g).endGL();

    views[0].camera();

    backPorch();

    userInput(views[0]);
    ((PGraphicsOpenGL) g).endGL();

    cameraZ = ((float) (height / 2.0) / tan((float) (PI * 60.0 / 360.0))); //default

    // Bird's eye (top) view - top left viewport
    perspective((float) (PI / 47.0), aspect, (float) (cameraZ / 10.0), (float) (cameraZ * 10.0)); // small fov for non-immersive orthogalized view
    gl = ((PGraphicsOpenGL) g).beginGL();
    gl.glViewport(0, height / 2, width / 2, height / 2);
    ((PGraphicsOpenGL) g).endGL();
    views[1].camera();
    model.draw(this, false);
    ((PGraphicsOpenGL) g).endGL();

    // Front, either side, or back view, determined by z - bottom left viewport
    perspective((float) (PI / 11.0), aspect, (float) (cameraZ / 10.0), (float) (cameraZ * 10.0));
    gl = ((PGraphicsOpenGL) g).beginGL();
    gl.glViewport(0, 0, width / 2, height / 2);
    ((PGraphicsOpenGL) g).endGL();
    views[z].camera();
    model.draw(this, false);
    ((PGraphicsOpenGL) g).endGL();

    cameraZ = ((float) (height) / tan((float) (PI * 60.0 / 360.0)));
    perspective(fov / 3, aspect, (float) (cameraZ / 10.0), (float) (cameraZ * 10.0));
    // Earth and ISS orbit view - bottom right viewport
    gl = ((PGraphicsOpenGL) g).beginGL();
    gl.glViewport(width / 2, 0, width / 2, height / 2);
    ((PGraphicsOpenGL) g).endGL();

    views[6].camera();
    lights();
    // TODO Check spotlight
    // Last two args of spotLight():
    // angle = proportion of cone that is illuminated (2PI is whole scene, PI/4 is 1/8th of scene) I think
    // concentration = 1 to 10000 bias of light focusing toward the center of that cone
    //    spotLight(255, 255, 255, (float) model.sunPos.x * magnif, (float) model.sunPos.y * magnif,
    //        (float) model.sunPos.z * magnif, (float) model.sunDir.x, (float) model.sunDir.y, (float) model.sunDir.z, PI / 15,
    //        1);
    //    specular(255, 255, 255);

    earth.draw();

    ((PGraphicsOpenGL) g).endGL();

    perspective();
    // User controls on applet
    gl = ((PGraphicsOpenGL) g).beginGL();
    gl.glViewport(0, 0, width, height);
    ((PGraphicsOpenGL) g).endGL();
    userPanel();
    ((PGraphicsOpenGL) g).endGL();

    mouseClicked = false;
  }

  void backPorch() {
    model.modSize++;

    float magnif = model.magnif, modSize = model.modSize + 1;
    //noLights();
    /** ISS that back porch cube is connected */
    fill(Tools.dgray);
    stroke(Tools.dgray);
    pushMatrix();
    translate(-magnif / 2 * modSize, -magnif / 2 * modSize, magnif / 2 * modSize);
    float zero = magnif * -5, one = magnif * 5;
    beginShape();
    Tools.vertexM(this, new Vector(zero, zero, 0));
    Tools.vertexM(this, new Vector(zero, one, 0));
    Tools.vertexM(this, new Vector(one, one, 0));
    Tools.vertexM(this, new Vector(one, zero, 0));
    endShape(CLOSE);
    //fill(Tools.black);
    noFill();
    textureMode(NORMAL);
    zero = 0;
    one = magnif * modSize;
    beginShape();
    texture(NASAimg);
    Tools.vertexM(this, new Vector(zero, zero, -1), 1, 1);
    Tools.vertexM(this, new Vector(zero, one, -1), 1, 0);
    Tools.vertexM(this, new Vector(one, one, -1), 0, 0);
    Tools.vertexM(this, new Vector(one, zero, -1), 0, 1);
    endShape(CLOSE);
    fill(Tools.red);
    Tools.drawMArrow(this, new Vector(0, one / 2 - 50, -1), new Vector(0, one / 2 + 50, -1), new Vector(-1, 0, 0), 300);

    //Tools.drawSphere(this, new Vector(one,one/2,-5), 50);
    popMatrix();

    /** Cube underneath ISS - "the back porch" */
    stroke(color(0)); // the framing box has only outlines
    noFill(); // no surface
    pushMatrix(); // push the overall image centering

    // TODO - John where xRotation and yRotation are applied to the cube 
    rotateX(xRotation);
    rotateY(yRotation);
    box(model.magnif * modSize); // draw the framing box outlines

    pushMatrix();
    translate(-magnif / 2 * modSize, -magnif / 2 * modSize, magnif / 2 * modSize);

    model.draw(this, true);

    backPorchGUI();

    popMatrix();
    popMatrix();
    model.modSize--;
  }

  void backPorchGUI() {

    float magnif = model.magnif, modSize = model.modSize + 1;
    Vector[] arrows = { new Vector(.5 * magnif * modSize, 0, 0), new Vector(.5 * magnif * modSize, magnif * modSize, 0),
        new Vector(magnif * modSize, .5 * magnif * modSize, 0), new Vector(0, .5 * magnif * modSize, 0) };
    for (int i = 0; i < arrows.length; i++) {
      Vector a = arrows[i].clone(), diff = (i < 2) ? new Vector(9 * modSize, 0, 0) : new Vector(0, 9 * modSize, 0), P = arrows[i]
          .clone(), Q = arrows[i].clone(), dir = new Vector(0, 0, -1);
      P.sub(diff);
      Q.add(diff);
      float len = 50 * modSize;

      float x = screenX((float) arrows[i].y, (float) arrows[i].x, (float) (arrows[i].z + dir.z * len / 2)) * .5f + width / 2;
      float y = screenY((float) arrows[i].y, (float) arrows[i].x, (float) (arrows[i].z + dir.z * len / 2)) * .5f;

      if (mouseX < x + 6 && mouseX > x - 6 && mouseY < y + 6 && mouseY > y - 6) {
        strokeWeight(2);
        stroke(buttonOverStroke);
        //TODO - John associate xRotation and yRotation with rotation matrix, a little confusing here, so I commented out my adaptation
        // It prints out how xRotation and yRotation were changed
        float xRot = xRotation % (2 * PI), yRot = yRotation % (2 * PI);
        if (xRot < 0)
          xRot += 2 * PI;
        if (yRot < 0)
          yRot += 2 * PI;

        if (mouseClicked && !mouseDragged) {
          if (i == 0) {
            yRotation -= PI / 2;
            //          if (xRot == PI/2)
            //            xRotation -= PI / 2;
            //          else if (xRot == 3*PI/2)
            //            xRotation += PI / 2;
          }
          if (i == 1) {
            yRotation += PI / 2;
            if (xRot == 3 * PI / 2)
              xRotation -= PI / 2;
            else if (xRot == PI / 2)
              xRotation += PI / 2;
          }
          if (i == 2) {
            xRotation -= PI / 2;
            if (yRot == PI / 2)
              yRotation -= PI / 2;
            else if (yRot == 3 * PI / 2)
              yRotation += PI / 2;
          }
          if (i == 3) {
            xRotation += PI / 2;
            if (yRot == 3 * PI / 2)
              yRotation += PI / 2;
            else if (yRot == PI / 2)
              yRotation -= PI / 2;
          }
          println(xRotation + " " + yRotation);
          // printing/testing
//          xRot = xRotation%(2*PI); yRot = yRotation%(2*PI);
//          if (xRot < 0) xRot+=2*PI; if (yRot < 0) yRot+=2*PI;
//          println(xRot + " " + yRot);
//          model.reset();
        }
      }
      else {
        stroke(buttonStroke);
        strokeWeight(2);
      }
      fill(buttonFill);

      Tools.drawMArrow(this, P, Q, dir, len);
    }

    // Clickable ball that rotates model clockwise
    //	    float x=screenX(-7, -7, 0)*.5f + width/2;
    //	    float y=screenY(-7, -7, 0)*.5f;
    //	    
    //	    if (mouseX<x+6 && mouseX>x-6 && mouseY<y+6 && mouseY>y-6) {
    //	    	strokeWeight(4);
    //	    	stroke(Tools.blue);
    //	    }
    //	    else {
    //	    	stroke(Tools.gray);
    //	    	strokeWeight(2);
    //	    }
    //	    fill(Tools.white);
    //	    Tools.drawSphere(this, new Vector(-7, -7, 0), 20);
  }

  /*
   * Keyboard input and mouse press aside from GUI for running animation and other things in stand-alone Applet
   * 
   * Controls for testing:
   * p = prints camera vectors of the view with the rotating ability
   * left arrow = slows down model animation
   * right arrow = speeds up model animation
   * t = resets animation
   * r = runs animation
   * s = stops animation
   * 
   * @param view the view camera and frame information for accessing an alternate viewport
   */
  void userInput(AppletView view) {
    if (mousePressedAgain)
      mouseDragged = true;
    if (mousePressed)
      mousePressedAgain = true;

    if (keyPressed) {
      if (key == 'p') {
        view.printVecs();
        //println(model.printOutput);
      }
      if (key == CODED) {
        // Altering speed of photon animation
        if (keyCode == LEFT)
          model.speed *= .99;
        else if (keyCode == RIGHT)
          model.speed *= 1.01;
      }

      if (key == 't') {
        model.reset();
      }
      if (key == 'r') {
        model.runAnim = true;
        println("Running animation");
      }
      if (key == 's') {
        model.runAnim = false;
        println("Stopped animation");
      }

    }
    else if (mouseDragged) {

      if (mouseButton == LEFT) {
        // Camera looking around or panning
        view.lookAround(pmouseX, pmouseY, mouseX, mouseY);
        //view.setFrame();
      }
      else if (mouseButton == RIGHT) {
        view.rotate(pmouseX, pmouseY, mouseX, mouseY);
      }
      mousePressedAgain = mouseDragged = false;
    }

  }

  void mouseWheel(int delta) {
    if (delta < 0)
      views[0].zoomIn();
    else
      views[0].zoomOut();
  }

  /*
   * Listener for interactive buttons
   * 
   * @see processing.core.PApplet#mouseClicked()
   */
  public void mouseClicked() {
    // Resets view that has rotation ability
    if (mouseX > width - 45 && mouseY < 25) {
      views[0].initView();
    }
    mouseClicked = true;

  }

  void sideViewGUI() {
    // Multiview buttons
    lights();
    Vector viewArrows[] = { new Vector(width / 2 - 35, height / 2 + 10, 0), new Vector(width / 2 - 30, height / 2 + 10, 0) };
    fill(buttonFill);
    stroke(buttonStroke);
    for (int i = 0; i < viewArrows.length; i++) {
      double x = (i == 0) ? viewArrows[i].x - 25 : viewArrows[i].x, y = viewArrows[i].y;
      Vector Q = viewArrows[i].clone(), dir = (i == 0) ? new Vector(-1, 0, 0) : new Vector(1, 0, 0);
      Q.add(new Vector(0, 10, 0)); // y value is width of arrow
      if (mouseX < x + 25 && mouseX > x && mouseY < y + 10 && mouseY > y) {
        strokeWeight(2);
        stroke(buttonOverStroke);
        if (mouseClicked && !mouseDragged) {
          if (i == 0) {
            z--;
            if (z < 2)
              z = 5;
          }
          else {
            z++;
            if (z >= 6)
              z = 2;
          }
        }
      }
      else {
        stroke(buttonStroke);
        strokeWeight(2);
      }
      fill(buttonFill);

      Tools.drawArrow(this, viewArrows[i], Q, dir, 25);
    }
    strokeWeight(2);
  }

  /*
   * Showing interactive buttons and instructions
   */
  void userPanel() {
    hint(DISABLE_DEPTH_TEST);
    camera();

    stroke(Tools.black);
    strokeWeight(2);
    // Lines dividing 4 viewports
    line(width / 2, 0, width / 2, height);
    line(0, height / 2, width, height / 2);
    fill(Tools.black);
    noLights();

    fill(backgroundColor);
    rect(-1, -1, 65, 17);
    rect(width / 2, -1, 65, 17);
    rect(-1, height / 2, 65, 17);
    rect(width / 2, height / 2, 65, 17);

    fill(Tools.black);
    textSize(11);
    text("Top View", 2, 13);
    text("Back Porch", width / 2 + 2, 13);
    text("Side View", 2, height / 2 + 13);
    text("Earth", width / 2 + 2, height / 2 + 13);

    noStroke();
    noFill();
    noTint();

    // Side view
    sideViewGUI();

    // Earth view
    earth.displayCoord();

    hint(ENABLE_DEPTH_TEST);
  }

}
