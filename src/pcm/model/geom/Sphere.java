package pcm.model.geom;

import pcm.model.Photon;
import pcm.util.V;
import pcm.util.Vector;

/**
 * A surface is a whole sphere.
 * 
 * @author Satayev
 */
public class Sphere extends Surface {

  /** Radius of the sphere */
  public double r;

  /** Caches square and inverse of the radius */
  private double sqrR, invR;

  public Sphere(Vector center, double r) {
    super(center, null);
    this.r = r;
    this.sqrR = r * r;
    this.invR = 1 / r;
  }

  @Override
  public Vector normal(Vector at) {
    Vector normal = new Vector();
    normal.x = invR * (at.x - p.x);
    normal.y = invR * (at.y - p.y);
    normal.z = invR * (at.z - p.z);
    normal.normalize();
    return normal;
  }

  /**
   * Assumes photon is always outside of the sphere.
   */
  @Override
  public double travelTime(Photon photon) {
    Vector A = photon.p;
    Vector AC = V.sub(this.p, A);
    Vector v = photon.v;

    double acv = V.dot(AC, v);
    if (acv < V.EPS)
      // photon is moving in opposite direction
      return V.INFINITY;

    double len = AC.sqrlength();
    double D = acv * acv - len + sqrR;
    if (D < V.EPS)
      // line never intersects sphere
      return V.INFINITY;
    double sqrtD = Math.sqrt(D);
    double t = acv - sqrtD;
    if (t < V.EPS)
      t = acv + sqrtD;
    if (t < V.EPS)
      // intersections happen in the past
      return V.INFINITY;
    return t;
  }
}
