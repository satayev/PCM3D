package pcm.geom;

import pcm.model.shape.Surface;

/**
 * Represents intersection of a ray with an object.
 * 
 * @author Satayev
 */
public class Intersection {

  /** Intersection point on the surface */
  public Vector at;
  /** Distance from ray's initial point to intersection point */
  public double dist;
  /** Reference to the surface */
  public Surface owner;

  public Intersection(Vector at, double d) {
    this.at = at;
    this.dist = d;
  }
}