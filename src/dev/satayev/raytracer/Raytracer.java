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
import pcm.model.geom.Curve;
import pcm.model.geom.Hit;
import pcm.model.geom.Polygon;
import pcm.model.geom.Polygon3D;
import pcm.model.geom.Prism;
import pcm.model.geom.Sphere;
import pcm.model.geom.Surface;
import pcm.util.V;
import pcm.util.Vector;

/**
 * Simple ray tracer, takes into account reflections, refractions, and transparency.
 * 
 * @author Satayev
 */
@SuppressWarnings("serial")
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
  private final static Vector[] boundary = { new Vector(size,0,0), new Vector(0,size,0) };
  private final static double height = size;
  // used to color surfaces
  private final static Color[] colors = { Color.red, Color.blue, Color.green, Color.yellow, Color.cyan, Color.magenta, Color.pink, Color.white,
      Color.orange };

  public static void main(String[] args) {
    Raytracer rt = new Raytracer();

    // add lights
    rt.lights.add(new Vector(eyeZ, eyeZ, eyeZ));
    rt.lights.add(new Vector(-eyeZ / 5, 0, 0));
    rt.lights.add(new Vector(0, eyeZ, 0));
    rt.lights.add(new Vector(0, 0, -eyeZ));

    // add tetrahedron
    double C0 = Math.sqrt(2) / 4 * 250;
    Vector[] vert = new Vector[] { new Vector(0, 2 * C0, C0), new Vector(C0, 0, C0), new Vector(-C0, 0, C0), new Vector(0, C0, 2 * C0) };
    rt.surfaces.add(new Polygon3D(new Vector[] { vert[1], vert[2], vert[0] }));
    rt.surfaces.add(new Polygon3D(new Vector[] { vert[3], vert[2], vert[0] }));
    rt.surfaces.add(new Polygon3D(new Vector[] { vert[1], vert[3], vert[0] }));
    rt.surfaces.add(new Polygon3D(new Vector[] { vert[2], vert[3], vert[1] }));

    // add spheres
    rt.surfaces.add(new Sphere(new Vector(100, 55, 0), 25));
    rt.surfaces.add(new Sphere(new Vector(100, 100, 100), 50));
    rt.surfaces.add(new Sphere(new Vector(175, 100, 100), 25));
    rt.surfaces.add(new Sphere(new Vector(0, 0, 0), 100));

    // add a prism
    Polygon rect = new Polygon(new Vector[] { new Vector(0, 20, 0), new Vector(20, 0, 0), new Vector(0, -20, 0), new Vector(-20, 0, 0) });
    rt.surfaces.add(new Prism(new Vector(), 100, new Curve(20)));

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
        Photon photon = new Photon(new Vector(x - size / 2, eyeZ, z - size / 2), new Vector(0, -1, 0), boundary, height);
        //        Photon photon = new Photon(new Vector(x - size / 2, z - size / 2, eyeZ), new Vector(0, 0, -1));
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
      Hit h = s.getHit(photon, true);
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
      Photon p = new Photon(photon.p, dir, boundary, height);
      Hit test = trace(p, dist - V.EPS);
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
