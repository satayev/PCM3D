package pcm.model.geom.curves;

import pcm.model.Photon;
import pcm.model.geom.Hit;
import pcm.model.geom.V;
import pcm.model.geom.Vector;

/**
 * Polygon comprises of a list of (x,y) coordinate pairs ordered <b>counter-clockwise</b>, where each pair defines a vertex of
 * the
 * polygon, and two successive pairs are the endpoints of a side of the polygon. The first and last vertices are joined by a
 * line a segment that closes the polygon.
 * 
 * @author Satayev
 */
public class Polygon extends Curve {

  private int size;
  private Vector[] polygon;

  /**
   * Constructs the surface based on the polygon.
   * Assumes polygon is defined by at least 3 points.
   * 
   * @param polygon the vertices of polygon in counter-clockwise order.
   */
  public Polygon(Vector[] polygon) {
    this.size = polygon.length;
    this.polygon = polygon;
  }

  @Override
  public Vector normalAt(Hit hit) {
    Vector a = polygon[(hit.i - 1 + size) % size];
    Vector b = polygon[hit.i];
    double dx = b.x - a.x;
    double dy = b.y - a.y;
    Vector result = new Vector(-dy, dx);
    result.normalize();
    return result;
  }

  @Override
  public Hit getHit(Photon photon) {
    int position = -1;
    double distance = Double.POSITIVE_INFINITY;
    Vector prev, curr = polygon[size - 1];
    for (int i = 0; i < size; i++) {
      prev = curr;
      curr = polygon[i];
      double d = distanceToLineSegment(photon, prev, curr);
      if (d < distance) {
        position = i;
        distance = d;
      }
    }
    return position == -1 ? null : new Hit(distance, null, position);
  }

  // TODO(satayev): replace with Ribbon's method after fixing it
  private double distanceToLineSegment(Photon photon, Vector a, Vector b) {
    double bax = b.x - a.x;
    double bay = b.y - a.y;
    double det = bax * photon.v.y - bay * photon.v.x;
    if (Math.abs(det) < V.EPS)
      // lines do not intersect
      return Double.POSITIVE_INFINITY;

    double dx = a.x - photon.p.x;
    double dy = a.y - photon.p.y;
    double u = (dy * bax - dx * bay) / det;
    double v = (dy * photon.v.x - dx * photon.v.y) / det;

    // TODO(satayev): add tangent check if needed?
    if (u > V.EPS && v > -V.EPS && v < 1)
      return u;
    else
      return Double.POSITIVE_INFINITY;
  }

  @Override
  public boolean contains(Vector p) {
    boolean inside = false;
    double x1, y1, x2, y2;
    Vector prev = polygon[size - 1], curr = null;
    for (int i = 0; i < polygon.length; i++) {
      curr = polygon[i];
      if (prev.x < curr.x) {
        x1 = prev.x;
        x2 = curr.x;
        y1 = prev.y;
        y2 = curr.y;
      } else {
        x2 = prev.x;
        x1 = curr.x;
        y2 = prev.y;
        y1 = curr.y;
      }
      if (curr.x < p.x == (p.x < prev.x + V.EPS))
        if ((p.y - y1) * (x2 - x1) < (y2 - y1) * (p.x - x1) + V.EPS)
          inside = !inside;
      prev = curr;
    }
    return inside;
  }

}
