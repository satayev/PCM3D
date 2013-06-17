package pcm.model.geom;

import pcm.util.Vector;

/**
 * Defines a collision.
 * 
 * @author Satayev
 */
public class Hit {

  /** Surface of collision */
  public Surface surface;
  /** Distance to the collision point */
  public double distance;

  /** Integer position */
  public int i;
  /** Double position */
  public double d;
  /** Vector position */
  public Vector v;

  public Hit(double distance, Surface surface) {
    this.distance = distance;
    this.surface = surface;
  }

  public Hit(double distance, Surface surface, int position) {
    this.distance = distance;
    this.surface = surface;
    this.i = position;
  }

  public Hit(double distance, Surface surface, double position) {
    this.distance = distance;
    this.surface = surface;
    this.d = position;
  }

  public Hit(double distance, Surface surface, Vector position) {
    this.distance = distance;
    this.surface = surface;
    this.v = position;
  }

  public String toString() {
    return "Hit(dist=" + distance + ", " + surface + ", " + i + ", " + d + ", " + v + ")";
  }

}
