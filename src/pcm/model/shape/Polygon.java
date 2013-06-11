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

  private int size, axis;
  private Vector[] polygon;

  /**
   * Constructs the surface based on the polygon,
   * assuming polygon is defined by at least 3 points.
   * 
   * @param polygon the vertices of polygon in clockwise order.
   */
  public Polygon(Vector[] polygon) {
    super(polygon[0], V.cross(V.sub(polygon[2], polygon[0]), V.sub(polygon[1], polygon[0])));
    this.size = polygon.length;
    this.polygon = polygon;

    // TODO(satayev): change to Ray/Triangle Intersection to speed up and allow more complex shapes.
    if (Math.abs(n.x) > Math.abs(n.y))
      if (Math.abs(n.x) > Math.abs(n.z))
        axis = 0;
      else
        axis = 2;
    else if (Math.abs(n.y) > Math.abs(n.z))
      axis = 1;
    else
      axis = 2;
  }

  @Override
  public double travelTime(Photon photon) {
    double t = super.travelTime(photon);
    if (t > V.EPS && t < Double.POSITIVE_INFINITY) {
      Vector at = V.travel(photon.p, photon.v, t);
      //      System.out.println(at);
      //      double d = n.x * polygon[0].x + n.y * polygon[0].y + n.z * polygon[0].z;
      //      System.out.println(Math.abs(n.x * at.x + n.y * at.y + n.z * at.z - d) < V.EPS);
      if ((axis == 0 && !pointInPolygonX(at)) || (axis == 1 && !pointInPolygonY(at)) || (axis == 2 && !pointInPolygonZ(at)))
        t = Double.POSITIVE_INFINITY;
    }
    return t;
  }

  // Projection of the point and polygon on YZ-plane
  private boolean pointInPolygonX(Vector q) {
    boolean inside = false;
    double x1, y1, x2, y2;
    Vector prev = polygon[size - 1], curr = null;
    for (int i = 0; i < size; i++) {
      curr = polygon[i];
      if (prev.z + V.EPS < curr.z) {
        x1 = prev.z;
        x2 = curr.z;
        y1 = prev.y;
        y2 = curr.y;
      }
      else {
        x2 = prev.z;
        x1 = curr.z;
        y2 = prev.y;
        y1 = curr.y;
      }
      if ((curr.z + V.EPS < q.z) == (q.z < prev.z + V.EPS))
        if ((q.y - y1) * (x2 - x1) < (y2 - y1) * (q.z - x1) + V.EPS)
          inside = !inside;
      prev = curr;
    }
    return inside;
  }

  // Projection of the point and polygon on XZ-plane
  private boolean pointInPolygonY(Vector q) {
    boolean inside = false;
    double x1, y1, x2, y2;
    Vector prev = polygon[size - 1], curr = null;
    for (int i = 0; i < size; i++) {
      curr = polygon[i];
      if (prev.x + V.EPS < curr.x) {
        x1 = prev.x;
        x2 = curr.x;
        y1 = prev.z;
        y2 = curr.z;
      }
      else {
        x2 = prev.x;
        x1 = curr.x;
        y2 = prev.z;
        y1 = curr.z;
      }
      if ((curr.x + V.EPS < q.x) == (q.x < prev.x + V.EPS))
        if ((q.z - y1) * (x2 - x1) < (y2 - y1) * (q.x - x1) + V.EPS)
          inside = !inside;
      prev = curr;
    }
    return inside;
  }

  // Projection of the point and polygon on XY-plane
  private boolean pointInPolygonZ(Vector q) {
    boolean inside = false;
    double x1, y1, x2, y2;
    Vector prev = polygon[size - 1], curr = null;
    for (int i = 0; i < size; i++) {
      curr = polygon[i];
      if (prev.x + V.EPS < curr.x) {
        x1 = prev.x;
        x2 = curr.x;
        y1 = prev.y;
        y2 = curr.y;
      }
      else {
        x2 = prev.x;
        x1 = curr.x;
        y2 = prev.y;
        y1 = curr.y;
      }
      if ((curr.x + V.EPS < q.x) == (q.x < prev.x + V.EPS))
        if ((q.y - y1) * (x2 - x1) < (y2 - y1) * (q.x - x1) + V.EPS)
          inside = !inside;
      prev = curr;
    }
    return inside;
  }

}
