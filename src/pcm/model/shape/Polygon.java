package pcm.model.shape;

import pcm.geom.V;
import pcm.geom.Vector;
import pcm.model.Photon;

/**
 * Represents a convex plane polygon in 3D.
 * 
 * @author Satayev
 */
public class Polygon extends Plane {

  private int size;
  private Vector[] polygon;

  /**
   * Constructs the surface based on the polygon,
   * assuming polygon is defined by at least 3 points.
   * 
   * @param polygon the vertices of polygon in clockwise order.
   */
  public Polygon(Vector[] polygon) {
    super(polygon[0], V.sub(polygon[2], polygon[0]).cross(V.sub(polygon[1], polygon[0])));
    this.size = polygon.length;
    this.polygon = polygon;
  }

  @Override
  public double travelTime(Photon photon) {
    double t = super.travelTime(photon);
    if (t > V.EPS && t < Double.POSITIVE_INFINITY) {
      Vector at = V.travel(photon.p, photon.v, t);
      if (!pointInPolygon(at))
        t = Double.POSITIVE_INFINITY;
    }
    return t;
  }

  // TODO(satayev): if slow, recode with 2D polygon and rotations.
  private boolean pointInPolygon(Vector q) {
    double len1, len2;
    double totalAngle = 0, cos;
    Vector p1 = new Vector(), p2 = new Vector();
    p2.x = polygon[size - 1].x - q.x;
    p2.y = polygon[size - 1].y - q.y;
    p2.z = polygon[size - 1].z - q.z;
    for (int i = 0; i < size; i++) {
      p1.set(p2);
      p2.x = polygon[i].x - q.x;
      p2.y = polygon[i].y - q.y;
      p2.z = polygon[i].z - q.z;

      len1 = p1.length();
      len2 = p2.length();
      if (len1 < V.EPS || len2 < V.EPS)
        return true; // point is on a vertex
      else
        cos = V.dot(p1, p2) / (len1 * len2);

      totalAngle += Math.acos(cos);
    }
    // TODO(satayev): !!!FIXME!!! double precision error is way to big
    return Math.abs(totalAngle - Math.PI * 2) < V.EPS ? true : false;
  }
}
