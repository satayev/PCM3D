package pcm.model;

import java.util.ArrayList;
import java.util.List;

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
  /** Position of the photon */
  public Vector p;
  /** Initial position of the photon */
  public Vector p0;
  /** Velocity of the photon (unit vector) */
  // TODO(satayev): perhaps change this to spherical coordinates.
  public Vector v;
  /** Initial velocity of the photon (unit vector) */
  public Vector v0;

  public int reflectionCounter = 0;
  public boolean absorbed = false;
  public List<List<Vector>> path = new ArrayList<List<Vector>>(); // the path the photon takes

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
    this.p0 = p.clone();
    this.v0 = v.clone();

    this.reflectionCounter = 0;
    this.absorbed = false;
    path.add(new ArrayList<Vector>());
    path.get(path.size()-1).add(p.clone());
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
    path.get(path.size()-1).add(p.clone());
  }

  public void bounce(Vector normal) {
    this.reflectionCounter++;
    this.v.reflect(normal);
  }
  
  public void move(Vector differential) {
    p.add(differential);
    path.add(new ArrayList<Vector>());
    path.get(path.size()-1).add(p.clone());
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
