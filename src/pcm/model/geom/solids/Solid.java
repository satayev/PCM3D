package pcm.model.geom.solids;

import pcm.model.geom.Surface;
import pcm.model.geom.Vector;

/**
 * @author Satayev
 */
public abstract class Solid extends Surface {

  public Solid(Vector position) {
    super(position);
  }

  /**
   * Test if the point is inside of the solid.
   * 
   * @param p the point to test.
   * @return true, if inside or on the boundary.
   */
  public abstract boolean contains(Vector p);

}
