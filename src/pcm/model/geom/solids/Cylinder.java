package pcm.model.geom.solids;

import pcm.model.Photon;
import pcm.model.geom.Hit;
import pcm.util.V;
import pcm.util.Vector;

/**
 * Cylindrical prism.
 * 
 * @author Satayev
 */
public class Cylinder extends Solid {

  /** Radius of the sphere */
  public double r;

  /** Cached square and inverse of the radius */
  private double sqrR, invR;

  public Cylinder(Vector center, double r) {
    super(center);
    this.r = r;
    this.sqrR = r * r;
    this.invR = 1 / r;
  }

  @Override
  public Vector normalAt(Hit hit) {
    Vector normal = new Vector();
    normal.x = invR * (hit.v.x - p.x);
    normal.y = invR * (hit.v.y - p.y);
    normal.normalize();
    return normal;
  }

  @Override
  public Hit getHit(Photon photon) {
    double A = photon.v.x * photon.v.x + photon.v.y * photon.v.y;
    double B = 2 * (photon.p.x * photon.v.x + photon.p.y * photon.v.y);
    double C = photon.p.x * photon.p.x + photon.p.y * photon.p.y - sqrR;

    double D = B * B - 4 * A * C;
    if (D < V.EPS)
      return null;

    double t = (-B - Math.sqrt(D)) / 2 / A;
    if (t < V.EPS)
      t = (-B + Math.sqrt(D)) / 2 / A;
    if (t < V.EPS)
      return null;
    return new Hit(t, this, V.scaleAdd(photon.p, t, photon.v));
  }

  @Override
  public boolean contains(Vector p) {
    return p.x * p.x + p.y * p.y < sqrR + V.EPS;
  }
}
