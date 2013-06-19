package pcm.model.geom.curves;

import pcm.model.Photon;
import pcm.model.geom.Hit;
import pcm.model.geom.Vector;
import pcm.model.geom.solids.Prism;

/**
 * A closed, two-dimensional region on XY-plane.
 * Curve defines a base for a vertical {@link Prism}.
 * 
 * TODO(satayev): for complex Curves methods need to be changed (that's why Curve is not Surface's subclass).
 * 
 * @author Satayev
 */
public abstract class Curve {

  /**
   * Finds a unit normal vector at some position of the curve.
   * 
   * @param hit defines position on the curve
   * @return a unit normal vector.
   */
  public abstract Vector normalAt(Hit hit);

  /**
   * {@link Surface.getHit}
   */
  public abstract Hit getHit(Photon photon);

  /**
   * Test if a point is inside of the curve.
   * 
   * @param p the point
   * @return true, if inside or on the boundary.
   */
  public abstract boolean contains(Vector p);

}
