package dev.satayev.raytracer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

import pcm.model.Photon;
import pcm.model.geom.Hit;
import pcm.model.geom.Surface;
import pcm.model.geom.V;
import pcm.model.geom.Vector;
import pcm.model.geom.solids.PolygonMesh;
import pcm.model.geom.solids.Sphere;

/**
 * Simple ray tracer, takes into account reflections, refractions, and transparency.
 * 
 * @author Satayev
 */
@SuppressWarnings({ "serial", "deprecation" })
public class Raytracer extends JPanel {

  /////////////////////////////////////////////////////////////////////////////
  // Constants to determine a color of a pixel
  /////////////////////////////////////////////////////////////////////////////

  private double lambert = 0.4, phong = 0.6, phongpower = 7, refractionCoef = 1;
  private double reflectionCoef = 0.15, transparencyCoef = 0.15;
  private double surfaceColorCoef = 1 - reflectionCoef - transparencyCoef;

  /////////////////////////////////////////////////////////////////////////////
  // Create GUI and add objects into a scene
  /////////////////////////////////////////////////////////////////////////////

  // eye is on the following plane
  private final static double eyeZ = 1000;
  // size of the square canvas
  private final static int size = 500;
  // used to color surfaces
  private final static Color[] colors = { Color.red, Color.blue, Color.green, Color.yellow, Color.cyan, Color.magenta, Color.pink, Color.white,
      Color.orange, Color.gray };

  public static void main(String[] args) {
    Raytracer rt = new Raytracer();

    // add lights
    rt.lights.add(new Vector(eyeZ, eyeZ, eyeZ));
    rt.lights.add(new Vector(-eyeZ / 5, 0, 0));
    rt.lights.add(new Vector(0, eyeZ, 0));

    // add parallelepiped
    double C0 = 25;
    Vector[] rectV = new Vector[] { new Vector(-C0, -C0, 0), new Vector(-C0, C0, 0), new Vector(C0, C0, 0), new Vector(C0, -C0, 0),
        new Vector(-C0, -C0, 100), new Vector(-C0, C0, 100), new Vector(C0, C0, 100), new Vector(C0, -C0, 100) };
    rt.surfaces.add(new PolygonMesh(new Vector(), rectV, new int[][] { new int[] { 4, 5, 6, 7 }, new int[] { 4, 7, 3, 0 }, new int[] { 5, 1, 2, 6 },
        new int[] { 4, 0, 1, 5 }, new int[] { 7, 6, 2, 3 } }));

    // add tetrahedron
    double C1 = Math.sqrt(2) / 4 * 250;
    Vector[] tetrV = new Vector[] { new Vector(0, 2 * C1, C1), new Vector(C1, 0, C1), new Vector(-C1, 0, C1), new Vector(0, C1, 2 * C1) };
    rt.surfaces.add(new PolygonMesh(new Vector(), tetrV, new int[][] { new int[] { 1, 2, 0 }, new int[] { 3, 2, 0 }, new int[] { 1, 3, 0 },
        new int[] { 2, 3, 1 }, }));

    // add spheres
    rt.surfaces.add(new Sphere(new Vector(100, 55, 0), 25));
    rt.surfaces.add(new Sphere(new Vector(100, 100, 100), 50));
    rt.surfaces.add(new Sphere(new Vector(175, 100, 100), 25));
    rt.surfaces.add(new Sphere(new Vector(0, 0, 0), 75));

    //  add a prism
    //    int C2 = 50;
    //    Polygon rect = new Polygon(new Vector[] { new Vector(0, C2, 0), new Vector(C2, 0, 0), new Vector(0, -C2, 0), new Vector(-C2, 0, 0) });
    //    rt.surfaces.add(new Prism(new Vector(), rect));

    //    rt.surfaces.add(new Cylinder(new Vector(), 50));
    //    rt.surfaces.add(new Prism(new Vector(), new Circle(50)));

    // assign different colors to the surfaces
    for (int i = 0; i < rt.surfaces.size(); i++) {
      Color c = colors[i];
      Vector v = new Vector(c.getRed(), c.getGreen(), c.getBlue());
      v.normalize();
      rt.surfaceColors.put(rt.surfaces.get(i), v);
    }

    JFrame frame = new JFrame("Render");
    frame.setContentPane(rt);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(size, size);
    frame.setVisible(true);
  }

  /** Draws a scene with an orthogonal camera. */
  @Override
  public void paintComponent(Graphics g) {
    BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
    for (int x = 0; x < size; x++)
      for (int z = 0; z < size; z++) {
        Photon photon = new Photon(new Vector(x - size / 2, eyeZ, z - size / 2), new Vector(0, -1, 0));
        Vector clr = this.color(photon);
        int rgb = new Color((float) clr.x, (float) clr.y, (float) clr.z).getRGB();
        // swap y-coordinate to match OpenGL
        img.setRGB(x, size - 1 - z, rgb);
      }
    g.drawImage(img, 0, 0, size, size, null);
  }

  /////////////////////////////////////////////////////////////////////////////
  // Raytracer code
  /////////////////////////////////////////////////////////////////////////////

  // point lights of the same power
  private List<Vector> lights = new ArrayList<Vector>();
  // different objects in the scene
  private List<Surface> surfaces = new ArrayList<Surface>();
  // surface colors
  private Map<Surface, Vector> surfaceColors = new HashMap<Surface, Vector>();

  /* Given a maximum distance photon can travel, determine the closest surface that it collides with. */
  private Hit trace(Photon photon, double maxDistance) {
    Hit closest = null;
    for (Surface s : surfaces) {
      Hit h = s.getHit(photon);
      if (h != null && h.distance < maxDistance && h.distance > 0)
        if (closest == null || closest.distance > h.distance)
          closest = h;
    }
    return closest;
  }

  /* Determine color for a pixel defined by a photon */
  private Vector color(Photon photon) {
    Hit hit = trace(photon, Double.POSITIVE_INFINITY);
    if (hit == null || hit.distance == Double.POSITIVE_INFINITY)
      // return black
      return new Vector();

    Vector surfcol = diffuse(photon.clone(), hit);
    Vector reflcol = reflection(photon.clone(), hit);
    Vector refrcol = refraction(photon.clone(), hit);

    if (surfcol.length() > 1)
      surfcol.normalize();

    double r = (surfaceColorCoef * surfcol.x + reflectionCoef * reflcol.x + transparencyCoef * refrcol.x);
    double g = (surfaceColorCoef * surfcol.y + reflectionCoef * reflcol.y + transparencyCoef * refrcol.y);
    double b = (surfaceColorCoef * surfcol.z + reflectionCoef * reflcol.z + transparencyCoef * refrcol.z);

    return new Vector(r, g, b);
  }

  private Vector refraction(Photon photon, Hit hit) {
    // refract once for simplicity
    if (photon.reflectionCounter > 1)
      return new Vector();
    photon.travel(hit.distance);
    photon.v.refract(hit.surface.normalAt(hit), refractionCoef);
    if (photon.v.x == Double.NaN)
      return new Vector();
    photon.reflectionCounter++;
    return color(photon);
  }

  private Vector reflection(Photon photon, Hit hit) {
    // reflect once for simplicity
    if (photon.reflectionCounter > 1)
      return new Vector();
    photon.travel(hit.distance);
    photon.v.reflect(hit.surface.normalAt(hit));
    photon.reflectionCounter++;
    return color(photon);
  }

  private Vector diffuse(Photon photon, Hit hit) {
    double sunlight = 0;
    photon.travel(hit.distance);

    for (Vector light : lights) {
      Vector dir = V.sub(light, photon.p);
      double dist = dir.length();
      dir.normalize();
      Photon p = new Photon(photon.p, dir);
      Hit test = trace(p, dist);
      if (test == null || test.distance == Double.POSITIVE_INFINITY) {
        Vector normal = hit.surface.normalAt(hit);
        // point is not in the shadow
        if (phong > 0) {
          Vector lr = V.reflect(dir, normal);
          double cos = V.dot(lr, photon.v);
          if (cos > 0)
            sunlight += phong * Math.pow(cos, phongpower);
        }
        if (lambert > 0) {
          double cos = V.dot(dir, normal);
          if (cos > 0)
            sunlight += lambert * cos;
        }
      }
    }
    return V.mult(sunlight, surfaceColors.get(hit.surface));
  }
}
