package pcm.model.shape;

import pcm.geom.Intersection;
import pcm.geom.Vector;
import pcm.model.Photon;
import pcm.util.V;

/**
 * Basic plane.
 * 
 * @author Satayev
 */
public class Plane extends Surface {

  private double dot;

  public Plane(Vector position, Vector normal) {
    super(position, V.normalize(normal));
    this.dot = V.dot(p, n);
  }

  @Override
  public Vector normal(Vector at) {
    return this.n;
  }

  @Override
  public Intersection trace(Photon photon) {
    Vector v = photon.v;
    double vn = V.dot(v, n);
    // parallel case
    if (vn == 0)
      return null;

    Vector from = photon.p;
    double t = (dot - V.dot(from, n)) / vn;
    if (t < 1e-5)
      return null;
    
    Vector at = new Vector();
    at.x = from.x + t * v.x;
    at.y = from.y + t * v.y;
    at.z = from.z + t * v.z;
    return new Intersection(at, t);
  }
}
