package pcm.model.geom;

import pcm.PCM3D;
import pcm.model.Photon;
import pcm.util.Vector;

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
  public Surface() {
  }

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
  public abstract Vector normalAt(Hit hit);

  /**
   * Computes time required by particle-like photon to travel until collision with the surface.
   * Result is Double.POSITIVE_INFINITY if surface and photon never collide.
   * 
   * Collision point equals to {@code Photon.currentPosition + travelTime * Photon.velocity}.
   * 
   * @param p the photon.
   * @return time until collision.
   */
  public abstract Hit getHit(Photon photon, boolean computePosition);

  /**
   * Perform absorption of the photon.
   * 
   * @param photon
   * @return false, if the photon was not absorbed.
   */
  public boolean absorb(Photon photon) {
    // TODO(satayev): generate heat-maps
    if (PCM3D.rnd.nextDouble() < 0.2)
      return true;
    return false;
  }

  // ///////////////////////////////////////////////////////////////////////////
  // Helpers
  // ///////////////////////////////////////////////////////////////////////////

  @Override
  public String toString() {
    return "Surface{p=" + p + ", n=" + n + "}";
  }

}
