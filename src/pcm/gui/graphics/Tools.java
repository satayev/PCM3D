package pcm.gui.graphics;

import processing.core.PApplet;
import pcm.model.geom.*;

/**
 * Processing applet tools class for ease of drawing text, lines, colors, and rotation.
 * For drawing the model, the x- and y-axises are flipped due to Processing's inverted
 * appearance (default for rendering is having y axis being the up vector instead of z axis which
 * model uses as axis of elevation). The alternative is having z be negative, though would involve changes with a view's camera
 * vectors.
 * 
 * @author Susan
 */
public class Tools {

  private static PApplet applet = new PApplet();

  // Colors
  public static int red = applet.color(250, 0, 0),
      lyellow = applet.color(255, 102, 0), yellow = applet.color(250, 250, 0),
      white = applet.color(255, 255, 255),
      black = applet.color(0, 0, 0),
      lllgray = applet.color(225, 225, 225), llgray = applet.color(215, 215, 215), lgray = applet.color(195, 195, 195),
      gray = applet.color(180, 180, 180), dgray = applet.color(150, 150, 150),
      orange = applet.color(255, 130, 0), gold = applet.color(204, 204, 0),
      blue = applet.color(0, 0, 250),
      cyan = applet.color(0, 250, 250),
      green = applet.color(0, 250, 0);
  public static int[] colorScale = { applet.color(255, 255, 0), applet.color(223, 255, 32), applet.color(191, 255, 64),
      applet.color(159, 255, 96), applet.color(127, 255, 128), applet.color(96, 255, 159), applet.color(64, 255, 191),
      applet.color(32, 255, 223), applet.color(0, 255, 255) };

  // Writes on screen at (x,y) with current fill color
  public static void scribe(Applet applet, String S, int x, int y) {
    applet.text(S, x, y);
    applet.noFill();
  }

  public static void printVec(String name, Vector v) {
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

  // Rotated vector 90 degrees in XY plane
  public static Vector rotate(Applet applet, Vector Vec) {
    return new Vector(-Vec.y, Vec.x, Vec.z);
  };

  // The following methods for drawing have x and y inverted because opengl is such
  // Draws a line between two points
  public static void drawMLine(Applet applet, Vector P, Vector Q) {
    applet.line((float) Q.y, (float) Q.x, (float) Q.z, (float) P.y, (float) P.x, (float) P.z);
  };

  public static void drawMLine(Applet applet, Vector P, Vector Q, float magnif) {
    applet.line((float) Q.y * magnif, (float) Q.x * magnif, (float) Q.z * magnif, (float) P.y * magnif, (float) P.x * magnif,
        (float) P.z * magnif);
  };

  public static void drawMPhoton(Applet applet, Vector p, float photonRadius, float magnif, int detail) {
    Vector v = V.mult(magnif, p);
    applet.sphereDetail(detail); // 360/params = __ degrees of rotation in sphere mesh
    drawSphere(applet, new Vector(v.y, v.x, v.z), photonRadius);
  }

  public static void drawSphere(Applet applet, Vector p, float radius) {
    applet.pushMatrix();
    applet.translate((float) p.x, (float) p.y, (float) p.z);
    applet.sphere(radius);
    applet.popMatrix();
  }

  // Vertex for shading or drawing
  public static void vertexM(Applet applet, Vector P) {
    applet.vertex((float) P.y, (float) P.x, (float) P.z);
  };

  // Vertex with texture coordinates
  public static void vertexM(Applet applet, Vector P, float u, float v) {
    applet.vertex((float) P.y, (float) P.x, (float) P.z, u, v);
  };

  public static void drawLine(Applet applet, Vector P, Vector Q) {
    applet.line((float) Q.x, (float) Q.y, (float) Q.z, (float) P.x, (float) P.y, (float) P.z);
  };

  public static void vertex(Applet applet, Vector P) {
    applet.vertex((float) P.x, (float) P.y, (float) P.z);
  };


  public static void drawMArrow(Applet applet, Vector P, Vector Q, Vector dir, float len) {
    Vector D = V.normalize(dir);
    Vector A = V.add(V.mult(.5, V.add(P, Q)), V.mult(len, D)), // arrow tip
    s1 = V.sub(P, Q), s2 = V.sub(Q, P);
    double size = s1.length();
    Vector S1 = V.sub(V.add(s2, A), V.mult(size, D)), S2 = V.sub(V.add(s1, A), V.mult(size, D)), // sides of the arrow
    S = V.sub(V.add(Q, V.mult(len, D)), V.mult(size, D)), R = V.sub(V.add(P, V.mult(len, D)), V.mult(size, D));//V.sub(S2,s1);

    applet.beginShape();
    vertexM(applet, Q);
    vertexM(applet, P);
    vertexM(applet, R);
    vertexM(applet, S2);
    vertexM(applet, A);
    vertexM(applet, S1);
    vertexM(applet, S);
    applet.endShape(applet.CLOSE);

  }

  public static void drawArrow(Applet applet, Vector P, Vector Q, Vector dir, float len) {
    Vector D = V.normalize(dir);
    Vector A = V.add(V.mult(.5, V.add(P, Q)), V.mult(len, D)), // arrow tip
    s1 = V.sub(P, Q), s2 = V.sub(Q, P);
    double size = s1.length();
    Vector S1 = V.sub(V.add(s2, A), V.mult(size, D)), S2 = V.sub(V.add(s1, A), V.mult(size, D)), // sides of the arrow
    S = V.sub(V.add(Q, V.mult(len, D)), V.mult(size, D)), R = V.sub(V.add(P, V.mult(len, D)), V.mult(size, D));//V.sub(S2,s1);

    applet.beginShape();
    vertex(applet, Q);
    vertex(applet, P);
    vertex(applet, R);
    vertex(applet, S2);
    vertex(applet, A);
    vertex(applet, S1);
    vertex(applet, S);
    applet.endShape(applet.CLOSE);

  }

}
