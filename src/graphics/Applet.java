package graphics;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.media.opengl.GL;

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

  //GL2 gl;
  GL gl;
  PGraphicsOpenGL pgl;

  int width, height; // screen size
  boolean runAnim;

  AppletView viewMain, viewTop, viewFront;
  AppletModel model;

  public PImage CNTimg; // carbon nanotube texture
  // Buttons
  PImage resetButton, playButton, pauseButton, nextButton;

  public Applet() {
    this(600, 600);
  }

  public Applet(int w, int h) {
    width = w;
    height = h;

    model = new AppletModel();

    viewMain = new AppletView(
        this,
        //new Vector(45, -30, 50),new Vector(348, 494, 282),new Vector(0.0, 0.0, -1.0),
        //new Vector(1, 0, 0),new Vector(0, 1,0),new Vector(0, 0, 1)
        new Vector(-243.45067112147808, -116.6750026345253, 191.81623530387878), new Vector(592.614533088183, 467.5012779562986, 440.5564127864783),
        new Vector(0.0, 0.0, -1.0), new Vector(0.5727584958076477, -0.8197242021560669, 0.0), new Vector(-0.19422075152397156, -0.1357061117887497,
            0.9715256690979004), new Vector(0.7963831424713135, 0.5564495921134949, 0.23693425953388214));
    viewTop = new AppletView(this, new Vector(125, 125, 50), new Vector(125, 125, 500), new Vector(0.0, 1.0, 0.0), new Vector(1, 0, 0), new Vector(0,
        -1, 0), new Vector(0, 0, 1));
    viewFront = new AppletView(this, new Vector(235, 125, 110), new Vector(-275, 125, 135), new Vector(0.0, 0.0, -1.0), new Vector(0, 1, 0),
        new Vector(0, 0, 1), new Vector(-1, 0, 0));

    int viewMainHeight = 1 / 2 * height;
    //viewMain.lookAround(0, -(height-viewMainHeight)/2);
    viewMain.lookAround(100, 100);
    //viewMain.zoomOut(100);

  }

  public Applet(int w, int h, int windowsNum, int window, Vector F, Vector E, Vector U, Vector I, Vector J, Vector K, AppletModel model) {
    width = w;
    height = h;

    viewMain = new AppletView(this, F, E, U, I, J, K);
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
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();
    model.addPhoton();

    runAnim = true;
    model.drawPhotons(this, true);
    runAnim = false;
  }

  // Drawing function called every frame
  public void draw() {
    //println(frameRate);
    background(Tools.llgray);

    // Top port - main view
    gl = ((PGraphicsOpenGL) g).beginGL();
    gl.glViewport(0, height / 2, width, height);
    ((PGraphicsOpenGL) g).endGL();
    renderScene(viewMain, true);
    userInput(viewMain);
    ((PGraphicsOpenGL) g).endGL();

    // Bottom left port - top view
    gl = ((PGraphicsOpenGL) g).beginGL();
    gl.glViewport(0, 0, width / 2, height / 2);
    ((PGraphicsOpenGL) g).endGL();
    renderScene(viewTop, false);
    ((PGraphicsOpenGL) g).endGL();

    // Bottom right port - front, either side, or back view
    gl = ((PGraphicsOpenGL) g).beginGL();
    gl.glViewport(width / 2, 0, width / 2, height / 2);
    ((PGraphicsOpenGL) g).endGL();
    renderScene(viewFront, false);
    ((PGraphicsOpenGL) g).endGL();

    gl = ((PGraphicsOpenGL) g).beginGL();
    gl.glViewport(0, 0, width, height);
    ((PGraphicsOpenGL) g).endGL();
    userPanel();
    ((PGraphicsOpenGL) g).endGL();
  }

  void renderScene(AppletView view, boolean updatePhotons) {
    view.camera();
    view.castLights();
    model.drawFloorGrid(this);
    model.drawPhotons(this, updatePhotons);
    model.drawSurfaces(this);
  }

  // Keyboard input and mouse press
  void userInput(AppletView view) {
    if (keyPressed) {
      if (key == 'p') {
        view.printVecs();
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
    } else if (mousePressed) {

      if (mouseButton == LEFT) {
        // Camera looking around
        view.lookAround(pmouseX, pmouseY, mouseX, mouseY);
        view.setFrame();
      } else if (mouseButton == RIGHT) {
        view.rotate(pmouseX, pmouseY, mouseX, mouseY);
      }
    }

  }

  void mouseWheel(int delta) {
    if (delta < 0)
      viewMain.zoomIn();
    else
      viewMain.zoomOut();
  }

  // Button listener
  public void mouseClicked() {
    if (mouseButton == LEFT) {
      if (mouseX < 50 && mouseY < 50)
        model.reset();
      if (mouseX > 50 && mouseX < 100 && mouseY < 50)
        runAnim = !runAnim;
      if (mouseX > 100 && mouseX < 150 && mouseY < 50)
        model.addPhoton();
    }

  }

  // Showing buttons and instructions
  void userPanel() {
    hint(DISABLE_DEPTH_TEST);
    camera();

    image(resetButton, 5, 5);
    if (runAnim)
      image(pauseButton, 50, 5);
    else
      image(playButton, 50, 5);
    image(nextButton, 100, 5);

    //Tools.scribe(this, "click to look around\nr and click to rotate\nup and down arrows to zoom\nspacebar to reset view", 5,playButton.height + 20);

    hint(ENABLE_DEPTH_TEST);
  }

}
