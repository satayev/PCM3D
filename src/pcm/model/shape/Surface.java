package pcm.model.shape;

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
  /** Unit normal vector */
  public Vector n;

  // ///////////////////////////////////////////////////////////////////////////
  // Constructors
  // ///////////////////////////////////////////////////////////////////////////
  public Surface(Vector position, Vector normal) {
    this.p = position;
    this.n = normal;
    if (this.n != null)
      this.n.normalize();
  }

  // ///////////////////////////////////////////////////////////////////////////
  // Methods
  // ///////////////////////////////////////////////////////////////////////////

  /**
   * Finds a unit normal vector at some point on the surface.
   * 
   * @param at point on the surface.
   * @return a unit normal vector.
   */
  public abstract Vector normal(Vector at);

  /**
   * Computes time required by particle-like photon to travel until collision with the surface.
   * Result is Double.POSITIVE_INFINITY if surface and photon never collide.
   * 
   * Collision point equals to {@code Photon.currentPosition + travelTime * Photon.velocity}.
   * 
   * @param p the photon.
   * @return time until collision.
   */
  public abstract double travelTime(Photon photon);

  // ///////////////////////////////////////////////////////////////////////////
  // Helpers
  // ///////////////////////////////////////////////////////////////////////////

  @Override
  public String toString() {
    return "Surface{p=" + p + ", n=" + n + "}";
  }

}
