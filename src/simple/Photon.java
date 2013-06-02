package simple;

import pcm.geom.Vector;

/**
 * This class contains all information for the photon
 * 
 * @author John Stewart
 */
public class Photon {

  public static final double X = 1, Y = 1, Z = 1;
  public Vector r, n;
  public double e = .0001; // Uncertainty in position, for error

  public Statistic stat = new Statistic();

  public Photon() {
    reset();
  }

  /**
   * This method randomizes the variables in the photon for a new simulation
   * The photon starts at the top with a downward trajectory
   */
  public void reset() {
    r.x = X * Math.random();
    r.y = Y * Math.random();
    r.z = Z;
    double theta = 2 * Math.PI * Math.random(), phi = Math.PI / 2 * Math.random();
    n.x = Math.cos(theta) * Math.cos(phi);
    n.y = Math.sin(theta) * Math.cos(phi);
    n.z = -Math.sin(phi);
  }

  /**
   * Moves the photon to the indicated position
   * 
   * @param v The new position
   */
  public void move(Vector v) {
    r.set(v);
  }

  public void move(double d) {
    r.add(n.clone().mul(d));
  }

  /**
   * Reflects the velocity on the vector given and makes a statistical recording
   * Checks if photon is absorbed instead.
   * 
   * @param v The surface normal vector
   * @return Return true if the photon bounced, false if it was absorbed
   */
  public boolean bounce(Vector v) {
    n.add(v.clone().mul(-2 * n.dot(v)));
    // Statistics here
    if (absorbsionChance()) {
      absorb();
      return false;
    } else {
      stat.addPath(r);
      return true;
    }
  }

  /**
   * This method calculates whether the photon is absorbed or not
   * 
   * @return true if absorbed
   */
  private boolean absorbsionChance() {
    return (Math.random() < .2);
  }

  /**
   * This method handles absorbtion of the photon
   */
  public void absorb() {
    stat.addPath(r);
    stat.absorb(r);
    stat.newPhoton();
  }

}
