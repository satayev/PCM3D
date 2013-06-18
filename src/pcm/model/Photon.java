package pcm.model;

import java.util.List;

import pcm.PCM3D;
import pcm.util.V;
import pcm.util.Vector;

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
  /** Velocity of the photon (unit vector) */
  // TODO(satayev): perhaps change this to spherical coordinates.
  public Vector v;
  // TODO(satayev): add phase/wave-length for the wave properties.
  /** Landa, Wavelength of the photon */
  public double l;
  /** Energy of the photon */
  public double E;
  
  /** Boundary vectors of the simulation */
  public Vector[] r;
  /** Spawning height of the simulation */
  public double h;
  
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
   * @param boundaries the boundary vectors of the simulation.
   */
  public Photon(Vector position, Vector velocity, Vector[] boundary, double height) {
    p = position;
    v = velocity;
    if (v != null)
      v.normalize();
    
    r = boundary;
    h = height;
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
  
  public void reset() {
    double theta = Math.PI * 3 / 4 * (PCM3D.rnd.nextDouble() / 4 + 1), phi = Math.PI * 2 * PCM3D.rnd.nextDouble();
    v.x = Math.sin(theta) * Math.cos(phi);
    v.y = Math.sin(theta) * Math.sin(phi);
    v.z = Math.cos(theta);

    p.mult(0);
    for (int i = 0; i < r.length; i++) 
      p.add(V.mult(PCM3D.rnd.nextDouble() - 0.5, r[i]));
    // TODO(john): make this random for hexagons
    p.z = h;

    reflectionCounter = 0;
    absorbed = false;
  }

  /////////////////////////////////////////////////////////////////////////////
  // Helper methods
  /////////////////////////////////////////////////////////////////////////////

  public Photon clone() {
    Photon photon = new Photon(p.clone(), v.clone(), r.clone(), h);
    photon.reflectionCounter = reflectionCounter;
    photon.absorbed = absorbed;
    return photon;
  }

}
