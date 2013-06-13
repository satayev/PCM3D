package graphics;

import java.awt.event.*;

import javax.media.opengl.GL2;

import pcm.geom.V;
import pcm.geom.Vector;
import processing.core.PApplet;
import processing.core.PImage;
import processing.opengl.PGraphicsOpenGL;

/**
 * Entry point for Processing v2.0b9 applet, usage of OpenGL, for simulation of CNT ribbon tower and any number of photons.
 * 
 * @author Susan
 */
public class Applet extends PApplet {

  GL2 gl;
  PGraphicsOpenGL pgl;

  int width, height; // screen size
  boolean runAnim = false;
  int windowsNum, window; // number of applets using the same model

  AppletView view;
  AppletModel model;

  public PImage CNTimg; // carbon nanotube texture
  // Buttons
  PImage resetButton, playButton, pauseButton, nextButton;

  public Applet() {
    width = 600;
    height = 600;
    this.windowsNum = 1;
    this.window = 0;

    view = new AppletView(this);
    model = new AppletModel(windowsNum);

  }

  public Applet(int w, int h, int windowsNum, int window, Vector F, Vector E, Vector U, Vector I, Vector J, Vector K,
      AppletModel model) {
    width = w;
    height = h;
    this.windowsNum = windowsNum;
    this.window = window;

    view = new AppletView(this, F, E, U, I, J, K);
    this.model = model;

  }

  // Called upon running this applet
  public void setup() {
    size(width, height, OPENGL);

    gl = ((PGraphicsOpenGL) g).beginPGL().gl.getGL2();

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
    background(Tools.white);

    view.camera();

    model.drawFloorGrid(this);

    view.castLights();

    model.drawPhotons(this);
    model.drawSurfaces(this);

    // subject to change, temporary
    if (window == 0) {
      userInput();
      userPanel();
    }

  }

  // Keyboard input and mouse press
  void userInput() {
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
    }
    else if (mousePressed) {

      if (mouseButton == LEFT) {
        // Camera looking around
        view.lookAround(pmouseX, pmouseY, mouseX, mouseY);
        view.setFrame();
      }
      else if (mouseButton == RIGHT) {
        view.rotate(pmouseX, pmouseY, mouseX, mouseY);
      }
    }

  }

  void mouseWheel(int delta) {
    if (delta < 0)
      view.zoomIn();
    else
      view.zoomOut();
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

    Tools.scribe(this, "click to look around\nr and click to rotate\nup and down arrows to zoom\nspacebar to reset view", 5,
        playButton.height + 20);

    hint(ENABLE_DEPTH_TEST);
  }

}
