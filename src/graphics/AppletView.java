package graphics;

import processing.opengl.*;
import javax.media.opengl.GL2;

import pcm.geom.*;

/**
 * Class utilized by Applet class to to adjust 3D camera view.
 * 
 * @author Susan
 */
public class AppletView {

  private Applet applet; // parent applet

  GL2 gl;
  PGraphicsOpenGL pgl;

  // View parameters: focus, eye, and up vector, picked surface point Q and screen aligned vectors
  // {I,J,K} set when picked
  public Vector F, E, U, Q, I, J, K,
      initF, initE, initU, initQ, initI, initJ, initK;

  public AppletView(Applet p) {
    applet = p;

    initI = new Vector(1, 0, 0);
    initJ = new Vector(0, .5, -.85); // (0, 1, 0) default
    initK = new Vector(0, .85, .5); // (0, 0, 1) default
    initF = new Vector(125, -50, 15); // (0, 0, 0) default
    initE = new Vector(120, 440, 280); // (0, 0, 500) default
    initU = new Vector(0, 0, -1); // (0, 1, 0) default

    initView(); // declares the local frames for 3D GUI
  }

  public AppletView(Applet p, Vector F, Vector E, Vector U, Vector I, Vector J, Vector K) {
    applet = p;

    initF = F;
    initE = E;
    initU = U;
    initI = I;
    initJ = J;
    initK = K;

    initView();
  }

  public void startOpenGL() {
    gl = ((PGraphicsOpenGL) applet.g).beginPGL().gl.getGL2();

  }

  // Declares the local frames
  public void initView() {
    F = initF.clone();
    E = initE.clone();
    U = initU.clone();
    I = initI.clone();
    J = initJ.clone();
    K = initK.clone();
  }

  // Defines the view : eye, focus, up
  public void camera() {
    applet.camera((float) E.x, (float) E.y, (float) E.z, (float) F.x, (float) F.y, (float) F.z, (float) U.x, (float) U.y,
        (float) U.z);
  }

  // Sets I, J, K to be aligned with the screen (I right, J up, K towards the viewer)
  void setFrame() {
    startOpenGL();
    float modelviewm[] = new float[16];
    gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, modelviewm, 0);
    I.set(modelviewm[0], modelviewm[4], modelviewm[8]);
    J.set(modelviewm[1], modelviewm[5], modelviewm[9]);
    K.set(modelviewm[2], modelviewm[6], modelviewm[10]);
    applet.noStroke();
  }

  // Light up view from behind and above the viewer
  public void castLights() {
    Vector Li = V.normalize(V.scaleAdd(V.sub(F, E), 0.1 * E.distance(F), J));
    applet.directionalLight(255, 255, 255, (float) Li.x, (float) Li.y, (float) Li.z);
    applet.specular(255, 255, 255);
    applet.shininess(5);
  }

  // Methods for changing camera angle and orientation
  public void lookAround(float pmouseX, float pmouseY, float mouseX, float mouseY) {
    F.add(V.scaleAdd(-(mouseX - pmouseX), I, -(mouseY - pmouseY), J));
  }

  public void zoomIn() {
    E.scaleAdd(-15, K);

  }

  public void zoomOut() {
    E.scaleAdd(15, K);

  }

  public void rotate(float pmouseX, float pmouseY, float mouseX, float mouseY) {
    E = Tools.rotate(applet, E, (float) Math.PI * (mouseX - pmouseX) / applet.width, I, K, F);
    E = Tools.rotate(applet, E, (float) -Math.PI * (mouseY - pmouseY) / applet.width, J, K, F);
  }

  public void printVecs() {
    Tools.printVec(applet, "Focus", F);
    Tools.printVec(applet, "Eye", E);
    Tools.printVec(applet, "Up", U);
    //Tools.printVec(applet, "Q", Q); unnecessary for now
    Tools.printVec(applet, "I", I);
    Tools.printVec(applet, "J", J);
    Tools.printVec(applet, "K", K);
  }

}
