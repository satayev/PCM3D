package pcm.model.geom.solids;

import pcm.model.Photon;
import pcm.model.geom.Hit;
import pcm.model.geom.Plane;
import pcm.model.geom.V;
import pcm.model.geom.Vector;

/**
 * Polygon Mesh.
 * 
 * @author Satayev
 */
@Deprecated
public class PolygonMesh extends Solid {

  @SuppressWarnings("unused")
  private Vector[] vertices;
  private int nFaces;
  private Vector[][] faces;
  private Vector[] normals;
  private Plane[] facePlanes;
  private int[] axis;

  /** TODO(Satayev): hack to check if the point is inside of the Solid: check if it falls into a shadow of the "top" face. */
  public int topFaceId;

  // TODO(satayev): change to Ray/Triangle Intersection to speed up
  /**
   * Constracts a polygon mesh.
   * Each {@code int[] face} stores indexes of the mesh vertices and defines a face vertices in counter-clockwise order.
   * 
   * @param worldPosition position in the world.
   * @param vertices vertices of the mesh.
   * @param faces faces of the mesh.
   */
  public PolygonMesh(Vector worldPosition, Vector[] vertices, int[][] faces) {
    super(worldPosition);
    this.nFaces = faces.length;
    this.vertices = vertices;
    this.faces = new Vector[nFaces][];
    for (int i = 0; i < nFaces; i++) {
      this.faces[i] = new Vector[faces[i].length];
      for (int j = 0; j < faces[i].length; j++)
        this.faces[i][j] = vertices[faces[i][j]];
    }

    normals = new Vector[nFaces];
    facePlanes = new Plane[nFaces];
    axis = new int[nFaces];
    for (int i = 0; i < nFaces; i++) {
      Vector a = V.sub(this.faces[i][2], this.faces[i][0]);
      Vector b = V.sub(this.faces[i][1], this.faces[i][0]);
      Vector n = V.cross(a, b);
      n.normalize();
      normals[i] = n;
      if (Math.abs(n.x) > Math.abs(n.y))
        if (Math.abs(n.x) > Math.abs(n.z))
          axis[i] = 0;
        else
          axis[i] = 2;
      else if (Math.abs(n.y) > Math.abs(n.z))
        axis[i] = 1;
      else
        axis[i] = 2;
      facePlanes[i] = new Plane(this.faces[i][0], normals[i]);
    }
  }

  @Override
  public Hit getHit(Photon photon) {
    Hit closest = null;
    for (int i = 0; i < nFaces; i++) {
      Hit hit = facePlanes[i].getHit(photon);
      if (hit != null && hit.distance < Double.POSITIVE_INFINITY) {
        hit.v = V.scaleAdd(photon.p, hit.distance, photon.v);
        if ((axis[i] == 0 && !pointInPolygonX(hit.v, faces[i])) || (axis[i] == 1 && !pointInPolygonY(hit.v, faces[i]))
            || (axis[i] == 2 && !pointInPolygonZ(hit.v, faces[i]))) {
          hit = null;
        } else if (closest == null || closest.distance > hit.distance) {
          closest = hit;
          closest.i = i;
        }
      }
    }
    if (closest != null)
      closest.surface = this;
    return closest;
  }

  @Override
  public boolean contains(Vector p) {
    return pointInPolygonZ(p, faces[topFaceId]);
  }

  @Override
  public Vector normalAt(Hit hit) {
    // hit.i is index of the face
    return normals[hit.i];
  }

  // Projection of the point and polygon on YZ-plane
  private boolean pointInPolygonX(Vector q, Vector[] polygon) {
    boolean inside = false;
    double z1, y1, z2, y2;
    Vector prev = polygon[polygon.length - 1], curr = null;
    for (int i = 0; i < polygon.length; i++) {
      curr = polygon[i];
      if (prev.z + V.EPS < curr.z) {
        z1 = prev.z;
        z2 = curr.z;
        y1 = prev.y;
        y2 = curr.y;
      } else {
        z2 = prev.z;
        z1 = curr.z;
        y2 = prev.y;
        y1 = curr.y;
      }
      if ((curr.z + V.EPS < q.z) == (q.z < prev.z + V.EPS))
        if ((q.y - y1) * (z2 - z1) < (y2 - y1) * (q.z - z1) + V.EPS)
          inside = !inside;
      prev = curr;
    }
    return inside;
  }

  // Projection of the point and polygon on XZ-plane
  private boolean pointInPolygonY(Vector q, Vector[] polygon) {
    boolean inside = false;
    double x1, z1, x2, z2;
    Vector prev = polygon[polygon.length - 1], curr = null;
    for (int i = 0; i < polygon.length; i++) {
      curr = polygon[i];
      if (prev.x + V.EPS < curr.x) {
        x1 = prev.x;
        x2 = curr.x;
        z1 = prev.z;
        z2 = curr.z;
      } else {
        x2 = prev.x;
        x1 = curr.x;
        z2 = prev.z;
        z1 = curr.z;
      }
      if ((curr.x + V.EPS < q.x) == (q.x < prev.x + V.EPS))
        if ((q.z - z1) * (x2 - x1) < (z2 - z1) * (q.x - x1) + V.EPS)
          inside = !inside;
      prev = curr;
    }
    return inside;
  }

  // Projection of the point and polygon on XY-plane
  public boolean pointInPolygonZ(Vector q, Vector[] polygon) {
    boolean inside = false;
    double x1, y1, x2, y2;
    Vector prev = polygon[polygon.length - 1], curr = null;
    for (int i = 0; i < polygon.length; i++) {
      curr = polygon[i];
      if (prev.x + V.EPS < curr.x) {
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
      if ((curr.x + V.EPS < q.x) == (q.x < prev.x + V.EPS))
        if ((q.y - y1) * (x2 - x1) < (y2 - y1) * (q.x - x1) + V.EPS)
          inside = !inside;
      prev = curr;
    }
    return inside;
  }
}
