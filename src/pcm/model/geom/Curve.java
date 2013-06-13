package pcm.model.geom;

import pcm.model.Photon;
import pcm.util.V;
import pcm.util.Vector;

/**
 * @author Satayev
 * 
 */
public class Curve extends Base {

  public double r;

  public Curve(double r) {
    this.r = r;
    // TODO(satayev): demo of a circle curve
    // x = rcos(theta)
    // y = rsin(theta)
  }

  @Override
  public Vector normalAt(Hit hit) {
    double x = r * Math.cos(hit.d);
    double y = r * Math.sin(hit.d);
    return new Vector(-y, x);
  }

  @Override
  public Hit getHit(Photon photon, boolean computePosition) {
    if (photon.v.x == 0) {
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
  public boolean inside(Vector p) {
    return p.x * p.x + p.y * p.y < r * r + V.EPS;
  }

}
