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

  //GL2 gl;
  GL gl;
  PGraphicsOpenGL pgl;

  int width, height; // screen size
  int viewsShowing;
  
// temp for bottom right viewport changing
int z=3;

boolean test=true;

  AppletModel model;

  public PImage CNTimg; // carbon nanotube texture
  // Buttons
  PImage resetButton, playButton, pauseButton, nextButton;

  AppletView[] views=new AppletView[6]; // main, top, front, left, right, back
  int t;
  
  public Applet() {
    this(600,600);
  }
  
  public Applet(int w, int h) {
    this(w, h, new AppletModel());
  }
  
  public Applet(int w, int h, AppletModel model) {
    width = w;
    height = h;

    this.model = model;
    
    viewsShowing=3;
    
    // Main view
    views[0] = new AppletView(this, 
        new Vector(-237.67475172390084, 369.7502131296976, 322.29050694796695),
        new Vector(.5*model.magnif, .5*model.magnif, .5*model.magnif),
        new Vector(0.0, 0.0, -1.0),
        new Vector(1, 0, 0),new Vector(0, 0, -1),new Vector(0, 1, 0)
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
        new Vector(.5*model.magnif, 5*model.magnif, .5*model.magnif),
        new Vector(.5*model.magnif, .5*model.magnif, .5*model.magnif),
        new Vector(0.0, 0.0, -1.0),
        new Vector(0, 1, 0),new Vector(0, 0, 1), new Vector(-1, 0, 0)
        );
    
    // Back view
    views[5] = new AppletView(this, 
        new Vector(5*model.magnif, .5*model.magnif, .5*model.magnif),
        new Vector(.5*model.magnif, .5*model.magnif, .5*model.magnif),
        new Vector(0.0, 0.0, -1.0),
        new Vector(0, 1, 0),new Vector(0, 0, 1), new Vector(-1, 0, 0)
        );
    
  }
  
  public Applet(int w, int h, int windowsNum, int window, Vector E, Vector F, Vector U, Vector I, Vector J, Vector K,
      AppletModel model) {
    width = w;
    height = h;

    viewsShowing=1;
    views[0] = new AppletView(this, E, F, U, I, J, K);
    this.model = model;

  }

  // Called upon running this applet
  public void setup() {
    size(width, height, OPENGL);
    frameRate(60);
    
    CNTimg = loadImage("cnt.jpg");
    playButton = loadImage("play.jpg");
    pauseButton = loadImage("pause.jpg");
    nextButton = loadImage("next.jpg");
    resetButton = loadImage("reset.jpg");

    addMouseWheelListener(new MouseWheelListener() {
      public void mouseWheelMoved(MouseWheelEvent mwe) {
        mouseWheel(mwe.getWheelRotation());
      }
    });

  }

  // Drawing function called every frame
  public void draw() {
    //println(frameRate);
    background(Tools.llgray);
    //background(Tools.black);

    t++;
    if (t>=2 && model.runAnim) {
      model.addPhoton();
      t=0;
    }

    
    if (viewsShowing==3){
      float cameraZ =((float)(height/2.0) / tan((float)(PI*60.0/360.0))); //default
      perspective((float)(PI/40.0), (float)(width)/(float)(height), (float)(cameraZ/10.0), (float)(cameraZ*10.0));
 
      
    // Bottom left port - top view
    gl = ((PGraphicsOpenGL)g).beginGL();
    gl.glViewport(0, 0,  width/2, height/2);  
    ((PGraphicsOpenGL)g).endGL();
    renderScene(views[1], false);
    ((PGraphicsOpenGL)g).endGL();

    perspective((float)(PI/10.0), (float)(width)/(float)(height), (float)(cameraZ/10.0), (float)(cameraZ*10.0));
    
    // Bottom right port - front, either side, or back view
    gl = ((PGraphicsOpenGL)g).beginGL();
    gl.glViewport(width/2, 0, width/2, height/2);  
    ((PGraphicsOpenGL)g).endGL();
    renderScene(views[z],false);
    ((PGraphicsOpenGL)g).endGL();  

    }
    

    // main view taking up half of screen
    float fov = (float)(PI/3.0); // field of view (default)
    float aspect = (float)(width)/(float)(height/2);
    float cameraZ =((float)(height/2.0) / tan((float)(PI*60.0/360.0))); 
    //default
    perspective(fov, aspect, (float)(cameraZ/10.0), (float)(cameraZ*10.0));
    
    
    // Top port - main view
    gl = ((PGraphicsOpenGL)g).beginGL();
    if (viewsShowing==1)
      gl.glViewport(0, 0, width, height);  
    else if (viewsShowing==3)
      gl.glViewport(0, height/2, width, height/2);
    ((PGraphicsOpenGL)g).endGL();
    renderScene(views[0], true);
    
    userInput(views[0]);
    
    ((PGraphicsOpenGL)g).endGL();  
    
    
    perspective();
    
    // Temporary user play controls
//    gl = ((PGraphicsOpenGL)g).beginGL();
//    gl.glViewport (0, 0, width, height);  
//    ((PGraphicsOpenGL)g).endGL();
//    userPanel();
//    ((PGraphicsOpenGL)g).endGL();  

    
  }

  void renderScene(AppletView view, boolean updatePhotons) {
    view.camera();
    
/*
 * TODO: have user options for lights
 * 
 */
    //view.castLights();
    noLights();
    
    // Last two args of spotLight():
    // angle = proportion of cone that is illuminated (2PI is whole scene, PI/4 is 1/8th of scene) I think
    // concentration = 1 to 10000 bias of light focusing toward the center of that cone
    colorMode(RGB); 
    //spotLight(255, 255, 255, (float)model.sunPos.x*model.magnif, (float)model.sunPos.y*model.magnif, (float)model.sunPos.z*model.magnif, (float)model.sunDir.x, (float)model.sunDir.y, (float)model.sunDir.z, PI/15, 1);specular(255, 255, 255);

    model.drawFloorGrid(this);
    model.drawSurfaces(this);
    model.drawPhotons(this, updatePhotons, test);
    
    /*
     * TODO: maybe only need for cell creation stage
     * 
     */
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
  
  
  // Keyboard input and mouse press
  void userInput(AppletView view) {
    if (keyPressed) {
      if (key == 'p') {
        //view.printVecs();
        println(model.printOutput);
      }
      if (key == 'r' && mousePressed) {
        // Rotating
        view.rotate(pmouseX, pmouseY, mouseX, mouseY);
      }

      if (key == CODED) {
        // Zooming in and out
        if (keyCode == UP)
          view.zoomIn();
        else if (keyCode == DOWN)
          view.zoomOut();
        // Altering speed of photon animation
        else if (keyCode == LEFT)
          model.speed *= .99;
        else if (keyCode == RIGHT)
          model.speed *= 1.01;
      }
      if (key == ' ') {
        view.initView();
      }
    }
    else if (mousePressed) {

      if (mouseButton == LEFT) {
        // Camera looking around
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

  // Button listener
  public void mouseClicked() {
    
//TODO: place arrows on viewport instead
    if (key == 'z') {
      z--;
      if (z<2) z=5;
    }
    if (key == 'x') {
      z++;
      if (z>=6) z=2;
    } 
    if (key == 't') {
      test=!test;
    } 
    if (mouseButton == LEFT) {
      if (mouseX < 50 && mouseY < 50)
        model.reset();
      if (mouseX > 50 && mouseX < 100 && mouseY <50)
        model.runAnim = !model.runAnim;
      if (mouseX > 100 && mouseX < 150 && mouseY <50)
        model.addPhoton();
    }

  }

  // Showing buttons and instructions
  void userPanel() {
    hint(DISABLE_DEPTH_TEST);
    camera();
    
    image(resetButton, 5, 5);
    if (model.runAnim)
      image(pauseButton, 50, 5);
    else
      image(playButton, 50, 5);
    image(nextButton, 100, 5);

    //Tools.scribe(this, "click to look around\nr and click to rotate\nup and down arrows to zoom\nspacebar to reset view", 5,playButton.height + 20);

    hint(ENABLE_DEPTH_TEST);
  }

}
