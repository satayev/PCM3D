package pcm.model.geom.curves;

import pcm.model.Photon;
import pcm.model.geom.Hit;
import pcm.util.V;
import pcm.util.Vector;

/**
 * @author Satayev
 */
@Deprecated
public class Circle extends Curve {

  public double r;

  public Circle(double r) {
    this.r = r;
    // TODO(satayev): demo of a circle curve
    // x = rcos(theta)
    // y = rsin(theta)
  }

  private double x(double t) {
    return r * Math.cos(t);
  }

  private double y(double t) {
    return r * Math.sin(t);
  }

  @Override
  public Vector normalAt(Hit hit) {
    double x = x(hit.d);
    double y = y(hit.d);
    Vector result = new Vector(x, y);
    result.normalize();
    return result;
  }

  @Override
  public Hit getHit(Photon photon) {
    if (Math.abs(photon.v.x) < V.EPS) {
      if (Math.abs(photon.p.x) + V.EPS > r)
        return null;
      double u = Math.acos(photon.p.x / r);
      double v = (r * Math.sin(u) - photon.p.y) / photon.v.y;
      if (v < V.EPS) {
        u += Math.PI;
        v = (r * Math.sin(u) - photon.p.y) / photon.v.y;
      }
      if (v < V.EPS) {
        u += Math.PI;
        v = (r * Math.sin(u) - photon.p.y) / photon.v.y;
      }
      if (v < V.EPS) {
        u += Math.PI;
        v = (r * Math.sin(u) - photon.p.y) / photon.v.y;
      }
      if (v < V.EPS)
        return null;
      return new Hit(v, null, u);
    }

    double a = r;
    double b = photon.v.y / photon.v.x;

    double sqrt = Math.sqrt(a * a + b * b);
    double phi = Math.asin(b / sqrt);

    double u = Math.asin((photon.p.y - b * photon.p.x) / sqrt) - phi;
    double v = (r * Math.cos(u) - photon.p.x) / photon.v.x;
    if (v < V.EPS) {
      u += Math.PI;
      v = (r * Math.cos(u) - photon.p.x) / photon.v.x;
    }
    if (v < V.EPS) {
      u += Math.PI;
      v = (r * Math.cos(u) - photon.p.x) / photon.v.x;
    }
    if (v < V.EPS) {
      u += Math.PI;
      v = (r * Math.cos(u) - photon.p.x) / photon.v.x;
    }
    if (v < V.EPS)
      return null;
    return new Hit(v, null, u);
  }

  @Override
  public boolean contains(Vector p) {
    //    double theta = Math.atan2(p.y, p.x);
    return p.x * p.x + p.y * p.y < r * r + V.EPS;
  }

}
