package pcm.model.shape;

import pcm.geom.Intersection;
import pcm.geom.Vector;
import pcm.model.Photon;
import pcm.util.V;

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
    return normal;
  }

  @Override
  public Intersection trace(Photon photon) {
    Vector A = photon.p;
    Vector CA = V.sub(p, A);
    Vector v = photon.v;

    double cav = V.dot(CA, v);
    if (cav < 1e-5)
      return null;
    double len = CA.sqrlength();
    double D = cav * cav - len + sqrR;
    if (D < 1e-5)
      return null;
    double sqrtD = Math.sqrt(D);
    double t = cav - sqrtD;
    if (t < 1e-5)
      t = cav + sqrtD;
    if (t < 1e-5)
      return null;
    Vector at = new Vector();
    at.x = A.x + t * v.x;
    at.y = A.y + t * v.y;
    at.z = A.z + t * v.z;
    return new Intersection(at, t);
  }
}
