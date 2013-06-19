package pcm.model;

import pcm.model.geom.Vector;

/**
 * Represents a (currently) particle-like photon in the system.
 * 
 * @author Satayev
 */
public class Photon {

  /////////////////////////////////////////////////////////////////////////////
  // Fields
  /////////////////////////////////////////////////////////////////////////////
  /** Initial position of the photon */
  public Vector p;
  /** Velocity of the photon (unit vector) */
  // TODO(satayev): perhaps change this to spherical coordinates.
  public Vector v;

  public int reflectionCounter = 0;
  public boolean absorbed = false;

  /////////////////////////////////////////////////////////////////////////////
  // Constructors
  /////////////////////////////////////////////////////////////////////////////
  /**
   * Creates a photon based on its initial position and direction.
   * 
   * @param position position of the photon.
   * @param velocity velocity of the photon.
   */
  public Photon(Vector position, Vector velocity) {
    this.p = position;
    this.v = velocity;
    if (v != null)
      this.v.normalize();

    this.reflectionCounter = 0;
    this.absorbed = false;
  }

  /////////////////////////////////////////////////////////////////////////////
  // Main methods
  /////////////////////////////////////////////////////////////////////////////

  /**
   * Moves the photon position in the direction of its velocity.
   * 
   * @param time determines how much to move.
   */
  public void travel(double time) {
    this.p.x += time * this.v.x;
    this.p.y += time * this.v.y;
    this.p.z += time * this.v.z;
  }

  public void bounce(Vector normal) {
    this.reflectionCounter++;
    this.v.reflect(normal);
  }

  /////////////////////////////////////////////////////////////////////////////
  // Helper methods
  /////////////////////////////////////////////////////////////////////////////

  public Photon clone() {
    Photon photon = new Photon(this.p.clone(), this.v.clone());
    photon.reflectionCounter = reflectionCounter;
    photon.absorbed = absorbed;
    return photon;
  }

  @Override
  public String toString() {
    return "Photon(" + p + ", " + v + ")";
  }

}
