package pcm.model;

import pcm.util.Vector;

/**
 * Testing class, where photons have a fixed starting velocity and position
 * @author John
 */
public class FixedPhoton extends Photon {
  
  /** Entering angle */
  public Vector v0;
  /** Entering position */
  public Vector p0;

  public FixedPhoton(Vector position, Vector velocity, Vector[] boundary, double height, Vector entryPosition, Vector entryVelocity) {
    super(position, velocity, boundary, height);
    p0 = entryPosition.clone();
    v0 = entryVelocity.clone();
  }
  
  @Override
  public void reset() {
    super.reset();
    p.set(p0);
    v.set(v0);
  }

}
