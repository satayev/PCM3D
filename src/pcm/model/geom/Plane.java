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
  public Vector normal(Vector at) {
    return this.n;
  }

  @Override
  public double travelTime(Photon photon) {
    Vector v = photon.v;
    double vn = V.dot(v, n);
    if (Math.abs(vn) < V.EPS)
      // parallel case
      return V.INFINITY;

    Vector from = photon.p;
    double t = (dot - V.dot(from, n)) / vn;
    if (t < V.EPS)
      return V.INFINITY;

    return t;
  }
  
  @Override
  public String toString() {
    return "Plane{p=" + p + ", n=" + n + "}";
  }
}
