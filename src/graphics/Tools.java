package graphics;

import pcm.geom.V;
import pcm.geom.Vector;
import processing.core.PApplet;

/**
 * Processing applet tools class for ease of drawing text, lines, colors, and rotation.
 * 
 * @author Susan
 */
public class Tools {

  private PApplet applet; // parent applet
  
  // Colors
  public int red, yellow, white, black, lgray, gray, dgray;

  public Tools(PApplet p) {
    applet = p;

    // Set colors
    red = applet.color(250, 0, 0);
    yellow = applet.color(250, 250, 0);
    white = applet.color(255, 255, 255);
    black = applet.color(0, 0, 0);
    lgray = applet.color(215, 215, 215);
    gray = applet.color(100, 100, 100);
    dgray = applet.color(150, 150, 250);
  }

  // Writes on screen at (x,y) with current fill color
  public void scribe(String S, int x, int y) {
    applet.fill(0);
    applet.text(S, x, y);
    applet.noFill();
  }

  public void printVec(String name, Vector v) {
    System.out.println(name + " " + v.x + " " + v.y + " " + v.z);
  }

  // O + xI + yJ
  public Vector transform(Vector O, float x, Vector I, float y, Vector J) {
    return new Vector(O.x + x * I.x + y * J.x, O.y + x * I.y + y * J.y, O.z + x * I.z + y * J.z);
  }

  // Rotated point P by a around point G in plane (I,J)
  public Vector rotate(Vector P, float a, Vector I, Vector J, Vector G) {
    float x = (float) I.dot(V.sub(P, G)), y = (float) J.dot(V.sub(P, G));
    float c = applet.cos(a), s = applet.sin(a);
    return transform(P, x * c - x - y * s, I, x * s + y * c - y, J);
  };

  public void drawLine(Vector P, Vector Q) {
    applet.line((float) Q.x, (float) Q.y, (float) Q.z, (float) P.x, (float) P.y, (float) P.z);
  };

  // Vertex for shading or drawing
  public void vertex(Vector P) {
    applet.vertex((float) P.x, (float) P.y, (float) P.z);
  }; 

  // Vertex with texture coordinates
  public void vertex(Vector P, float u, float v) {
    applet.vertex((float) P.x, (float) P.y, (float) P.z, u, v);
  }; 

}
