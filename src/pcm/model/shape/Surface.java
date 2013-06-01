package pcm.model.shape;

import pcm.geom.Intersection;
import pcm.geom.Vector;
import pcm.model.Photon;

/**
 * {@code Surface} represent <i>walls</i> of the towers.
 * In 2D case, Surface is a face of the polyhedra.
 * In 3D case, Surface is a plane on which 3D structure resides.
 * 
 * @author Satayev
 */
public abstract class Surface {

  // ///////////////////////////////////////////////////////////////////////////
  // Fields
  // ///////////////////////////////////////////////////////////////////////////
  /** Position vector */
  public Vector p;
  /** Normal vector */
  public Vector n;

  // ///////////////////////////////////////////////////////////////////////////
  // Constructors
  // ///////////////////////////////////////////////////////////////////////////
  public Surface(Vector position, Vector normal) {
    this.p = position;
    this.n = normal;
  }

  // ///////////////////////////////////////////////////////////////////////////
  // Methods
  // ///////////////////////////////////////////////////////////////////////////

  /**
   * Finds a normal vector at some point on the surface.
   * 
   * @param at point on the surface.
   * @return a normal vector.
   */
  public abstract Vector normal(Vector at);

  /**
   * Traces a photon ray and returns an intersection.
   * If ray and the surface do not intersect returns null.
   * 
   * @param p photon
   * @return intersection.
   */
  public abstract Intersection trace(Photon p);

  // ///////////////////////////////////////////////////////////////////////////
  // Getters and Setters
  // ///////////////////////////////////////////////////////////////////////////

}
