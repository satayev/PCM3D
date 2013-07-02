package pcm.gui.graphics;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;

import processing.opengl.*;

import pcm.model.geom.*;

/**
 * Class utilized by Applet class to to adjust 3D camera view.
 * 
 * @author Susan
 */
public class AppletView {

  private Applet applet; // parent applet

  GL gl;
  PGraphicsOpenGL pgl;

  // View parameters: focus, eye, up vector, and screen aligned vectors
  // {I,J,K} set when picked
  public Vector E, F, U, I, J, K, initE, initF, initU, initQ, initI, initJ, initK;

  public AppletView(Applet p) {
    applet = p;
    
    initE = new Vector(120, 440, 280); // (0, 0, 500) default
    initF = new Vector(125, -50, 15); // (0, 0, 0) default
    initU = new Vector(0, 0, -1); // (0, 1, 0) default
    initI = new Vector(1, 0, 0);
    initJ = new Vector(0, .5, -.85); // (0, 1, 0) default
    initK = new Vector(0, .85, .5); // (0, 0, 1) default

    initView(); // declares the local frames for 3D GUI
  }

  public AppletView(Applet p, Vector E, Vector F, Vector U, Vector I, Vector J, Vector K) {
    applet = p;

    initE = E;
    initF = F;
    initU = U;
    initI = I;
    initJ = J;
    initK = K;

    initView();
  }

  public void startOpenGL() {
    // gl = ((PGraphicsOpenGL) applet.g).beginPGL().gl.getGL2();

  }

  // Declares the local frames
  public void initView() {
    E = initE.clone();
    F = initF.clone();
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

  GLU glu;

  
  
  // Sets I, J, K to be aligned with the screen (I right, J up, K towards the viewer)
  void setFrame() {
    glu = ((PGraphicsOpenGL) applet.g).glu;
    PGraphicsOpenGL pgl = (PGraphicsOpenGL) applet.g;
    float modelviewm[] = new float[16];
    gl = pgl.beginGL();
    gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, modelviewm, 0);
    pgl.endGL();
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
    lookAround(mouseX - pmouseX, mouseY - pmouseY);
  }

  public void lookAround(float dx, float dy) {
    // Looking around
    //F.add(V.scaleAdd(-(dx), new Vector(1.0, -0.0, 0.0), -(dy), new Vector(0.0, -0.0, 1.0))); // I, J?
    // vs
    // Panning
    Vector d = V.scaleAdd(-dx, I, dy, J);
    F.add(d);
    E.add(d);
    
  }
  
  public void zoomIn() {
    Vector v = V.sub(F, E);
    v.normalize();
    v.mult(15);
    E.add(v);
  }

  public void zoomOut() {
    Vector v = V.sub(F, E);
    v.normalize();
    v.mult(-15);
    E.add(v);
  }
  

  public void rotate(float pmouseX, float pmouseY, float mouseX, float mouseY) {
    E = Tools.rotate(applet, E, (float) Math.PI * (mouseX - pmouseX) / applet.width, I, K, F);
    E = Tools.rotate(applet, E, (float) -Math.PI * (mouseY - pmouseY) / applet.width, J, K, F);
  }

  public void printVecs() {
    Tools.printVec("Eye", E);
    Tools.printVec("Focus", F);
    Tools.printVec("Up", U);
    Tools.printVec("I", I);
    Tools.printVec("J", J);
    Tools.printVec( "K", K);
    System.out.println();
  }

}
