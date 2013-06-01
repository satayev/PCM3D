package pcm.model;

import pcm.geom.Vector;
import pcm.util.V;

/**
 * Represents a (currently) particle-like photon in the system.
 * 
 * @author Satayev
 */
public class Photon {

  // ///////////////////////////////////////////////////////////////////////////
  // Fields
  // ///////////////////////////////////////////////////////////////////////////
  /** Initial position of the photon */
  public Vector p;
  /** Velocity of the photon */
  // TODO(satayev): perhaps change this to spherical coordinates.
  public Vector v;
  /** A point on the ray in the direction of velocity */
  // used for speed-ups in the computations
  public Vector q;
  // TODO(satayev): counts how many times this photon reflected in the system;
  // add phase/wave-length for the wave properties.
  private int √ = 0;

  // ///////////////////////////////////////////////////////////////////////////
  // Constructors
  // ///////////////////////////////////////////////////////////////////////////
  /**
   * Creates a photon based on its initial position and direction.
   * 
   * @param position position of the photon.
   * @param velocity velocity of the photon.
   */
  public Photon(Vector position, Vector velocity) {
    this.p = position;
    this.v = velocity;
    this.q = V.add(position, velocity);
  }

  // ///////////////////////////////////////////////////////////////////////////
  // Methods
  // ///////////////////////////////////////////////////////////////////////////

  // ///////////////////////////////////////////////////////////////////////////
  // Getters and Setters
  // ///////////////////////////////////////////////////////////////////////////
  /**
   * @return number of reflections photon has made.
   */
  public int get√() {
    return √;
  }

  /** Increments number of reflections photon has made. */
  public void inc√() {
    √++;
  }
}
