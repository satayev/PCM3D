package pcm.model;

import pcm.PCM3D;
import pcm.util.V;
import pcm.util.Vector;

/**
 * This class constrains the photon simulation to a set vector for the entry velocity
 * @author Admin
 */
public class AngledPhoton extends Photon {
  
  /** Entering angle */
  public Vector v0;

  public AngledPhoton(Vector position, Vector velocity, Vector[] boundary, double height, Vector entryVelocity) {
    super(position, velocity, boundary, height);
    v0 = entryVelocity.clone();
  }
  
  @Override
  public void reset() {
    super.reset();
    v.set(v0);
  }

}
