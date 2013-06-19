package dev.simple;

import pcm.model.geom.V;
import pcm.model.geom.Vector;

/**
 * This class models a generic triangle surface
 * 
 * @author John Stewart
 */
public class Triangle extends Surface {
  // norm : surface normal vector
  // n1 : normal vector along surface
  // n2 : final orthogonal vector
  // p1, p2, p3 : points on the surface
  public Vector norm, n1, n2, p1, p2, p3;
  // c : The distance from the origin 
  // rx2 : The distance to the second point at (rx2,0)
  // nx1, nx2, ny0, ny1, ny2 : The normals for the three boundaries
  public double c, rx2, ny0, nx1, ny1, nx2, ny2;

  public Triangle(Vector v1, Vector v2, Vector v3, Vector center) {
    p1 = v1.clone();
    p2 = v2.clone();
    p3 = v3.clone();
    norm = V.normalize(V.cross(V.sub(v1, v2), V.sub(v2, v3)));
    if (V.dot(norm, V.sub(v1, center)) < 0)
      norm.mult(-1);
    n1 = V.normalize(V.sub(v2, v1));
    n2 = V.normalize(V.cross(norm, n1));
    c = V.dot(p1, norm);
    rx2 = V.dot(V.sub(p2, p1), n1);
    ny0 = -1;
    nx1 = -V.dot(V.sub(p3, p1), n2);
    ny1 = V.dot(V.sub(p3, p1), n1);
    double r1 = Math.sqrt(nx1 * nx1 + ny1 * ny1);
    nx1 /= r1;
    ny1 /= r1;
    nx2 = V.dot(V.sub(p3, p2), n2);
    ny2 = -V.dot(V.sub(p3, p2), n1);
    double r2 = Math.sqrt(nx2 * nx2 + ny2 * ny2);
    nx2 /= r2;
    ny2 /= r2;
    if (V.dot(V.sub(p3, p1), n2) < 0) {
      ny0 = -ny0;
      nx1 = -nx1;
      ny1 = -ny1;
      nx2 = -nx2;
      ny2 = -ny2;
    }
  }

  @Override
  public double collisionDistance(Photon p) {
    // k : tangential velocity
    double k = V.dot(p.n, norm);
    // d : distance until collision
    double d = (c - V.dot(p.r, norm)) / k;
    if (k > 0 || d < 0)
      return Double.POSITIVE_INFINITY;
    Vector r = V.scaleAdd(p.r, d, p.n);
    double x = V.dot(r, n1), y = V.dot(r, n2);
    if (y * ny0 > 0 || x * nx1 + y * ny1 > 0 || (x - rx2) * nx2 + y * ny2 > 0)
      return Double.POSITIVE_INFINITY;
    return d;
  }

  @Override
  public boolean collision(Photon p) {
    double d = collisionDistance(p);
    p.move(d);
    return p.bounce(norm);
  }

}
