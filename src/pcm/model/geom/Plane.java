package pcm.model.geom;

import pcm.model.Photon;
import pcm.util.V;
import pcm.util.Vector;

/**
 * Basic double-sided plane.
 * 
 * @author Satayev
 */
public class Plane extends Surface {

  /** Unit normal vector */
  public Vector n;
  // cached dot product of the normal and position vectors
  private double dot;

  public Plane(Vector position, Vector normal) {
    super(position);
    this.n = normal;
    if (this.n != null) {
      this.n.normalize();
      this.dot = V.dot(p, n);
    }
  }

  @Override
  public Vector normalAt(Hit hit) {
    return this.n;
  }

  @Override
  public Hit getHit(Photon photon) {
    Vector v = photon.v;
    double vn = V.dot(v, n);
    if (Math.abs(vn) < V.EPS)
      // parallel case
      return null;

    Vector from = photon.p;
    double distance = (dot - V.dot(from, n)) / vn;
    if (distance > V.EPS)
      return new Hit(distance, this, null);
    else
      return null;
  }

  @Override
  public String toString() {
    return Plane.class.getSimpleName() + "(p=" + p + ", n=" + n + ")";
  }

}
