package pcm.model.geom;

import pcm.PCM3D;
import pcm.model.Photon;
import pcm.model.statics.BandgapEnergy;
import pcm.model.statics.ProbabilityOfAbsorption;

/**
 * Abstract Surface which can absorb a photon and provide information to properly reflect from it.
 * 
 * @author Satayev
 */
public abstract class Surface {

  /** Position vector */
  public Vector p;

  public Surface(Vector position) {
    this.p = position;
  }

  /**
   * Finds a unit normal vector at some point on the surface.
   * 
   * @param at point on the surface.
   * @return a unit normal vector.
   */
  public abstract Vector normalAt(Hit hit);

  /**
   * Returns collision of the photon and this surface.
   * 
   * @param p the photon.
   * @return null, if no collision.
   */
  public abstract Hit getHit(Photon photon);

  /**
   * Perform absorption of the photon.
   * 
   * @param photon
   * @return false, if the photon was not absorbed.
   */
  public boolean absorb(Photon photon) {
    // TODO(satayev): generate heat-maps
    if (ProbabilityOfAbsorption.test(photon.w)) {
      if (photon.E < BandgapEnergy.genEnergy()) photon.absorbed = false;
      else photon.absorbed = true;
      return true;
    } else return photon.absorbed = false;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + p;
  }

}
