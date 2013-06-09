package graphics;

import pcm.geom.V;
import pcm.geom.Vector;

/**
 * Class utilized by Applet class to to adjust 3D camera view.
 * 
 * @author Susan
 */
public class AppletView {

  private Applet applet; // parent applet
  private Tools tools;

  // View parameters: focus, eye, and up vector, picked surface point Q and screen aligned vectors
  // {I,J,K} set when picked
  public Vector F, E, U, Q, I, J, K;

  public AppletView(Applet p, Tools t) {
    applet = p;
    tools = t;

  }

  // Declares the local frames
  public void initView() {
    I = new Vector(1, 0, 0);
    J = new Vector(0, .5, -.85); // (0, 1, 0) default
    K = new Vector(0, .85, .5); // (0, 0, 1) default
    F = new Vector(125, -50, 15); // (0, 0, 0) default
    E = new Vector(120, 440, 280); // (0, 0, 500) default
    U = new Vector(0, 0, -1); // (0, 1, 0) default
  }

  // Defines the view : eye, focus, up
  public void camera() {
    applet.camera((float) E.x, (float) E.y, (float) E.z, (float) F.x, (float) F.y, (float) F.z, (float) U.x, (float) U.y,(float) U.z); 
  }

  // Light up view from behind and above the viewer
  public void castLights() {
    Vector Li = V.normalize(V.scaleAdd(V.sub(F, E), 0.1 * E.distance(F), J));
    applet.directionalLight(255, 255, 255, (float) Li.x, (float) Li.y, (float) Li.z);
    applet.specular(255, 255, 255);
    applet.shininess(5);
  }
}
