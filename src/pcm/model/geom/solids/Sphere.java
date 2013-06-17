package pcm.model.geom.solids;

import pcm.model.Photon;
import pcm.model.geom.Hit;
import pcm.util.V;
import pcm.util.Vector;

/**
 * A surface is a whole sphere.
 * 
 * @author Satayev
 */
public class Sphere extends Solid {

  /** Radius of the sphere */
  public double r;

  /** Cached square and inverse of the radius */
  private double sqrR, invR;

  public Sphere(Vector center, double r) {
    super(center);
    this.r = r;
    this.sqrR = r * r;
    this.invR = 1 / r;
  }

  @Override
  public Vector normalAt(Hit hit) {
    Vector normal = new Vector();
    normal.x = invR * (hit.v.x - p.x);
    normal.y = invR * (hit.v.y - p.y);
    normal.z = invR * (hit.v.z - p.z);
    normal.normalize();
    return normal;
  }

  @Override
  public Hit getHit(Photon photon) {
    // vector from sphere center to photon's position
    Vector AC = V.sub(this.p, photon.p);

    double acv = V.dot(AC, photon.v);
    if (acv < V.EPS)
      // photon is moving in opposite direction
      return null;

    double len = AC.sqrlength();
    double D = acv * acv - len + sqrR;
    if (D < V.EPS)
      // line never intersects sphere
      return null;
    double sqrtD = Math.sqrt(D);
    double distance = acv - sqrtD;

    // check if intersections happen in the past
    if (distance < V.EPS)
      distance = acv + sqrtD;
    if (distance < V.EPS)
      return null;

    return new Hit(distance, this, V.scaleAdd(photon.p, distance, photon.v));
  }

  @Override
  public boolean contains(Vector p) {
    return p.sqrlength() < sqrR + V.EPS;
  }
}
