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

  private double dot;

  public Plane(Vector position, Vector normal) {
    super(position, normal);
    this.dot = V.dot(p, n);
  }

  @Override
  public Vector normalAt(Hit hit) {
    return this.n;
  }

  @Override
  public Hit getHit(Photon photon, boolean computePosition) {
    Vector v = photon.v;
    double vn = V.dot(v, n);
    if (Math.abs(vn) < V.EPS)
      // parallel case
      return null;

    Vector from = photon.p;
    double distance = (dot - V.dot(from, n)) / vn;
    if (distance < V.EPS)
      return null;

    double z = photon.p.z + distance * photon.v.z;
    if (z < -V.EPS)
      return null;

    return new Hit(distance, this, computePosition ? V.scaleAdd(photon.p, distance, photon.v) : null);
  }

  @Override
  public String toString() {
    return "Plane{p=" + p + ", n=" + n + "}";
  }

}
