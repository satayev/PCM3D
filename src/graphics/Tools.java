package graphics;

import pcm.model.geom.V;
import pcm.model.geom.Vector;
import processing.core.PApplet;

/**
 * Processing applet tools class for ease of drawing text, lines, colors, and rotation.
 * 
 * @author Susan
 */
public class Tools {

  // Colors
  //public static int red, yellow, white, black, lgray, gray, dgray;

  private static PApplet applet = new PApplet();

  // Colors
  public static int red = applet.color(250, 0, 0), lyellow = applet.color(255, 102, 0), yellow = applet.color(250, 250, 0), white = applet.color(255, 255, 255),
      black = applet.color(0, 0, 0), lllgray = applet.color(225, 225, 225), llgray = applet.color(215, 215, 215), lgray = applet.color(195, 195, 195), gray = applet.color(180, 180, 180), dgray = applet
          .color(150, 150, 150), orange = applet.color(255, 130, 0), gold = applet.color(204, 204, 0);

  // Writes on screen at (x,y) with current fill color
  public static void scribe(Applet applet, String S, int x, int y) {
    applet.fill(0);
    applet.text(S, x, y);
    applet.noFill();
  }

  public static void printVec(Applet applet, String name, Vector v) {
    //System.out.println(name + ": (" + v.x + ", " + v.y + ", " + v.z + ")");
    System.out.println("new Vector(" + v.x + ", " + v.y + ", " + v.z + "),");
  }

  // O + xI + yJ
  public static Vector transform(Applet applet, Vector O, float x, Vector I, float y, Vector J) {
    return new Vector(O.x + x * I.x + y * J.x, O.y + x * I.y + y * J.y, O.z + x * I.z + y * J.z);
  }

  // Rotated point P by a around point G in plane (I,J)
  public static Vector rotate(Applet applet, Vector P, float a, Vector I, Vector J, Vector G) {
    float x = (float) I.dot(V.sub(P, G)), y = (float) J.dot(V.sub(P, G));
    float c = applet.cos(a), s = applet.sin(a);
    return transform(applet, P, x * c - x - y * s, I, x * s + y * c - y, J);
  };

  public static void drawLine(Applet applet, Vector P, Vector Q) {
    applet.line((float) Q.x, (float) Q.y, (float) Q.z, (float) P.x, (float) P.y, (float) P.z);
  };
  
  public static void drawLine(Applet applet, Vector P, Vector Q, float magnif) {
    applet.line((float) Q.x * magnif, (float) Q.y * magnif, (float) Q.z * magnif, (float) P.x * magnif, (float) P.y * magnif, (float) P.z * magnif);
  };

  public static void drawPhoton(Applet applet, Vector p, float photonRadius, float magnif, int detail) {
    applet.pushMatrix();
    applet.translate((float) p.x * magnif, (float) p.y * magnif, (float) p.z * magnif);
    applet.sphereDetail(2); // 360/params = __ degrees of rotation in sphere mesh
    applet.sphere(photonRadius);
    applet.popMatrix();
  }

  // Vertex for shading or drawing
  public static void vertex(Applet applet, Vector P) {
    applet.vertex((float) P.x, (float) P.y, (float) P.z);
  };

  // Vertex with texture coordinates
  public static void vertex(Applet applet, Vector P, float u, float v) {
    applet.vertex((float) P.x, (float) P.y, (float) P.z, u, v);
  };

}
