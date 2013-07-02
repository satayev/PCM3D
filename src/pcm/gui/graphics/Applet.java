package pcm.gui.graphics;

import java.awt.event.*;

import javax.media.opengl.GL;

import pcm.model.geom.*;
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

  public PImage CNTimg; // carbon nanotube texture
  // Buttons
  PImage resetButton, playButton, pauseButton, nextButton, viewsButton;

  AppletView[] views=new AppletView[6]; // main, top, front, left, right, back
  int z=3; // for multi-side-view changing
  
  int t;
  
  public Applet() {
    this(800,600);
  }
  
  /*
   * Constructor which sets up the views (camera orientations) upon the model to draw within different viewports
   * 
   * @param w width of applet
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
    
    // Main view
    views[0] = new AppletView(this, 
    new Vector(-1128.6199393109357, 941.991259236962, 783.8264517578489),
    new Vector(.5*model.magnif, .5*model.magnif, .5*model.magnif),
    new Vector(0.0, 0.0, -1.0),
    new Vector(1.0, 0.0, 0.0),
    new Vector(0.0, 0.0, -1.0),
    new Vector(0.0, 1.0, 0.0)
    
    );
    
    // Top view
    views[1] = new AppletView(this,  
        new Vector(.5*model.magnif, .5*model.magnif, 20*model.magnif),
        new Vector(.5*model.magnif, .5*model.magnif, .5*model.magnif),
      new Vector(-1, 0.0,0),
      new Vector(0, 0, 1),new Vector(-1, 0, 0),new Vector(0, 1, 0)
          );
    
    // Left side view
    views[2] = new AppletView(this, 
        new Vector(.5*model.magnif, -5*model.magnif, .5*model.magnif),
        new Vector(.5*model.magnif, .5*model.magnif, .5*model.magnif),
        new Vector(0.0, 0.0, -1.0),
        new Vector(0, 1, 0),new Vector(0, 0, 1), new Vector(-1, 0, 0)
        );
    
    // Front view
    views[3] = new AppletView(this, 
        new Vector(-5*model.magnif, .5*model.magnif, .5*model.magnif),
        new Vector(.5*model.magnif, .5*model.magnif, .5*model.magnif),
        new Vector(0.0, 0.0, -1.0),
        new Vector(0, 1, 0),new Vector(0, 0, 1), new Vector(-1, 0, 0)
        );
    

    // Right side view
    views[4] = new AppletView(this, 
        new Vector(.5*model.magnif, 6*model.magnif, .5*model.magnif),
        new Vector(.5*model.magnif, .5*model.magnif, .5*model.magnif),
        new Vector(0.0, 0.0, -1.0),
        new Vector(0, 1, 0),new Vector(0, 0, 1), new Vector(-1, 0, 0)
        );
    
    // Back view
    views[5] = new AppletView(this, 
        new Vector(6*model.magnif, .5*model.magnif, .5*model.magnif),
        new Vector(.5*model.magnif, .5*model.magnif, .5*model.magnif),
        new Vector(0.0, 0.0, -1.0),
        new Vector(0, 1, 0),new Vector(0, 0, 1), new Vector(-1, 0, 0)
        );
    
  }
  

  /*
   * Called upon running this applet(non-Javadoc)
   * @see processing.core.PApplet#setup()
   */
  public void setup() {
    size(width, height, OPENGL);
    frameRate(60);
    
    CNTimg = loadImage("cnt.jpg");
    viewsButton = loadImage("viewcontrols.jpg");

    addMouseWheelListener(new MouseWheelListener() {
      public void mouseWheelMoved(MouseWheelEvent mwe) {
        mouseWheel(mwe.getWheelRotation());
      }
    });

  }

  /*
   * Processing applet drawing function called every frame(non-Javadoc)
   * @see processing.core.PApplet#draw()
   */
  public void draw() {
    //println(frameRate);
    background(Tools.llgray);
    //background(Tools.black);

    // Speed of animation depends on number of max photons shown
    t++;
    if (t>=(101-model.maxPhotons) && model.runAnim) {
      if (model.runningPaths < model.maxPhotons) model.addPhoton();
      t=0;
    }
    if (model.runningPaths > model.paths.size()) model.run();

    
    // Main view
    float fov = (float)(PI/3.0); // field of view (default)
    // float aspect = (float)(width)/(float)(height/2); // as top half
    float aspect = (float)(width - height/2)/(float)(height);
    float cameraZ =((float)(height) / tan((float)(PI*60.0/360.0))); 
    //default
    perspective(fov/3, aspect, (float)(cameraZ/10.0), (float)(cameraZ*10.0));
    
    gl = ((PGraphicsOpenGL)g).beginGL();
    //gl.glViewport(0, height/2, width, height/2); // as top half
    //gl.glViewport(0, 0, width/2, height); // as left half
    gl.glViewport(height/2, 0, width - height/2, height); // as right half
    ((PGraphicsOpenGL)g).endGL();
    renderScene(views[0], true);
    //TODO - vary speed according to how fast model's angles are changing
    if (model.runAnim)
  	  earth.draw(true, 5);
    else
  	  earth.draw(false, 5);
    userInput(views[0]);
    
    ((PGraphicsOpenGL)g).endGL();  
    
    
    cameraZ =((float)(height/2.0) / tan((float)(PI*60.0/360.0))); //default
    
 // Bird's eye (top) view
    perspective((float)(PI/47.0), 1, (float)(cameraZ/10.0), (float)(cameraZ*10.0)); // small fov for non-immersive orthogalized view
  gl = ((PGraphicsOpenGL)g).beginGL();
  //gl.glViewport(0, 0,  width/2, height/2);  // as bottom left port 
  //gl.glViewport(width/2, height/2,  width/2, height/2); // as top right
  gl.glViewport(0, height/2,  height/2, height/2); // as top left
  ((PGraphicsOpenGL)g).endGL();
  renderScene(views[1], false);
  ((PGraphicsOpenGL)g).endGL();

//Front, either side, or back view, determined by z
perspective((float)(PI/13.0), 1, (float)(cameraZ/10.0), (float)(cameraZ*10.0));
  gl = ((PGraphicsOpenGL)g).beginGL();
  //gl.glViewport(width/2, 0, width/2, height/2);  // as bottom right
  gl.glViewport(0, 0, height/2, height/2); // as bottom left
  ((PGraphicsOpenGL)g).endGL();
  renderScene(views[z],false);
  ((PGraphicsOpenGL)g).endGL();  

    
    perspective();
    
    // User controls on applet
    gl = ((PGraphicsOpenGL)g).beginGL();
    gl.glViewport (0, 0, width, height);  
    ((PGraphicsOpenGL)g).endGL();
    userPanel();
    ((PGraphicsOpenGL)g).endGL();  

    
  }

  /*
   * A view (camera orientation) has the model's solar cell drawn upon it.
   * 
   * @param view the view camera and frame information for drawing on an alternate viewport
   * @param updatePhotons whether or not the model needs to be updated which should be once every frame, so one out of the views drawn needs this set as true
   */
  void renderScene(AppletView view, boolean updatePhotons) {
    view.camera();
    
    //view.castLights();
    noLights();
    
    colorMode(RGB); 
    
    // TODO Check spotlight
    // Last two args of spotLight():
    // angle = proportion of cone that is illuminated (2PI is whole scene, PI/4 is 1/8th of scene) I think
    // concentration = 1 to 10000 bias of light focusing toward the center of that cone
    spotLight(255, 255, 255, (float)model.sunPos.x*model.magnif, (float)model.sunPos.y*model.magnif, (float)model.sunPos.z*model.magnif, (float)model.sunDir.x, (float)model.sunDir.y, (float)model.sunDir.z, PI/15, 1);
    specular(255, 255, 255);

    model.drawFloorGrid(this);
    model.drawSurfaces(this);
    model.drawPhotons(this, updatePhotons);
    
    // Spheres on grid for easy identification of camera orientation
    lights();
    fill(Tools.white);stroke(Tools.white);
    pushMatrix();
    translate(-7, -7,0);
    sphereDetail(10);
    sphere(14);
    popMatrix();
    
    fill(Tools.black);stroke(Tools.black);
    pushMatrix();
    translate(7+model.magnif, 7+model.magnif,0);
    sphereDetail(10);
    sphere(14);
    popMatrix();

    //TODO
    // Rendering compass
    Vector compass = new Vector(7+model.magnif, 7+model.magnif,0);
    Tools.arrow(this, compass, new Vector(1,0,0)); // south
    Tools.arrow(this, compass, new Vector(-1,0,0)); // north
    Tools.arrow(this, compass, new Vector(0,1,0)); // 
    Tools.arrow(this, compass, new Vector(0,-1,0)); // 
    
    // Grid coordinates
    fill(Tools.black);
    pushMatrix();
    rotate(PI/2);textSize(20);
    //text("0, 0", -30, 15,0);
    text("1, 0", model.magnif, 15,0);
    text("0, 1", -30, -model.magnif,0);
    //text("1, 1", model.magnif, -model.magnif,0);
    popMatrix();
    
    noLights();
  }
  
  
  /*
   *  Keyboard input and mouse press aside from GUI for running animation and other things in stand-alone Applet
   *  
   * @param view the view camera and frame information for accessing an alternate viewport
   */
  void userInput(AppletView view) {
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
        model.runAnim = !model.runAnim;
        if (model.runAnim)
        	println("Running animation");
        else
        	println("Stopped animation");
      }
    }
    else if (mousePressed) {

      if (mouseButton == LEFT) {
        // Camera looking around or panning
        view.lookAround(pmouseX, pmouseY, mouseX, mouseY);
        //view.setFrame();
      }
      else if (mouseButton == RIGHT) {
        view.rotate(pmouseX, pmouseY, mouseX, mouseY);
      }
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
   * @see processing.core.PApplet#mouseClicked()
   */
  public void mouseClicked() {
      if (mouseX > width-45 && mouseY < 25) {
          views[0].initView();
        }
      
      // When multiview at bottom right
//      if (mouseX > width - viewsButton.width - 5 && mouseX < width - viewsButton.width/2 - 5 && mouseY > height/2 + 5 && mouseY < height/2 + viewsButton.height + 5){
//          z--;
//          if (z<2) z=5;
//        }
//      else if (mouseX > width - viewsButton.width/2 - 5 && mouseX < width - 5 && mouseY > height/2 + 5 && mouseY < height/2 + viewsButton.height + 5){
//          z++;
//          if (z>=6) z=2;
//        }
   // When multiview at bottom left (only change: width -> height/2)
      if (mouseX > height/2 - viewsButton.width - 5 && mouseX < height/2 - viewsButton.width/2 - 5 && mouseY > height/2 + 5 && mouseY < height/2 + viewsButton.height + 5){
          z--;
          if (z<2) z=5;
        }
      else if (mouseX > height/2 - viewsButton.width/2 - 5 && mouseX < height/2 - 5 && mouseY > height/2 + 5 && mouseY < height/2 + viewsButton.height + 5){
          z++;
          if (z>=6) z=2;
        }


  }

  /*
   * Showing interactive buttons and instructions
   */
  void userPanel() {
    hint(DISABLE_DEPTH_TEST);
    camera();
    
    stroke(Tools.black);
    
//    // main view at top
//    line(0, height/2, width, height/2);
//    line(width/2, height/2, width/2, height);
//    // main view at left
//    line(width - height/2, 0, width - height/2, height);
//    line(width - height/2, height/2, width, height/2);
    // main view at right
    line(height/2, 0, height/2, height);
    line(height/2, height/2, 0, height/2);
    noStroke();
    noFill();
    noTint();
    // multiview at bottom right
    //image(viewsButton, width - viewsButton.width - 5, height/2 + 5);
    // multiview at bottom left
    image(viewsButton, height/2 - viewsButton.width - 5, height/2 + 5);
    
    // Reset main view button
    lights();
    colorMode(RGB);
    fill(Tools.dgray);
    rect(width-45, 5, 40,20);
    fill(Tools.white);
    textSize(15);
    Tools.scribe(this, "reset", width-42, 20);

    hint(ENABLE_DEPTH_TEST);
  }

}
