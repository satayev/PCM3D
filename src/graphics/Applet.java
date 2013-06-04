package graphics;

import processing.core.*;
import java.nio.*;
import processing.opengl.*;
//import javax.media.opengl.*; 
//import javax.media.opengl.glu.*; 
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import simple.*;
import pcm.geom.*;
import pcm.util.*;

/**
 * Entry point for Processing v2.0b9 applet, usage of OpenGL, for simulation of CNT ribbon tower and any number of photons.
 * 
 * @author Susan
 */
public class Applet extends PApplet {

  GL2 gl;
  PGraphicsOpenGL pgl;

  int width = 600, height = 600; // screen size
  boolean runAnim = false;
  
  Tools tools;
  AppletView view;
  AppletModel model;
  
  // Buttons
  PImage resetButton, playButton, pauseButton, nextButton;

  // Called upon running this applet
  public void setup() {
    size(width, height, OPENGL);
    gl = ((PGraphicsOpenGL) g).beginPGL().gl.getGL2();

    tools = new Tools(this);
    model = new AppletModel(this, tools);
    view = new AppletView(this, tools);

    view.initView(); // declares the local frames for 3D GUI

    playButton = loadImage("play.jpg");
    pauseButton = loadImage("pause.jpg");
    nextButton = loadImage("next.jpg");
    resetButton = loadImage("reset.jpg");
  }

  // Drawing function called every frame
  public void draw() {
    background(tools.white);

    view.camera();

    model.drawFloorGrid();

    view.castLights();

    model.drawPhotons();
    model.drawSurfaces();
    model.drawTrajectory();

    userInput();
    userPanel();

  }

  // Keyboard input and mouse press
  void userInput() {
    if (keyPressed) {
      if (key == 'p') {
        tools.printVec("Eye", view.E);
        tools.printVec("Focus", view.F);
        tools.printVec("Up", view.U);
        tools.printVec("Q", view.Q);
        tools.printVec("I", view.I);
        tools.printVec("J", view.J);
        tools.printVec("K", view.K);
      }
      if (key == 'r' && mousePressed) {
        // Rotating
        view.E = tools.rotate(view.E, PI * (float) (mouseX - pmouseX) / width, view.I, view.K, view.F);
        view.E = tools.rotate(view.E, -PI * (float) (mouseY - pmouseY) / width, view.J, view.K, view.F);
      }

      if (key == CODED) {
        // Zooming in and out
        if (keyCode == UP)
          view.E.mul(.99);// E.scaleAdd(-10.0,K);
        else if (keyCode == DOWN)
          view.E.mul(1.01);// E.scaleAdd(10.0,K);
        // Altering speed of photon animation
        else if (keyCode == LEFT)
          model.speed -= .01;
        else if (keyCode == RIGHT)
          model.speed += .01;
      }
      if (key == ' ') {
        view.initView();
      }
    }
    else if (mousePressed) {
      // Camera looking around
      view.F.add(V.scaleAdd(-(mouseX - pmouseX), view.I, -(mouseY - pmouseY), view.J));
    }

  }

  // Button listener
  public void mouseClicked() {
    if (mouseX < 50 && mouseY < 50)
      model.reset();
    if (mouseX > 50 && mouseX < 100 && mouseY < 50)
      runAnim = !runAnim;
    if (mouseX > 100 && mouseX < 150 && mouseY < 50)
      model.addPhoton();
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

    tools.scribe("click to look around\nr and click to rotate\nup and down arrows to zoom\nspacebar to reset view", 5,
        playButton.height + 20);

    hint(ENABLE_DEPTH_TEST);
  }

}
