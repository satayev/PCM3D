package simple;

import pcm.geom.Vector;

/**
 * This class contains all information for the photon
 * @author John Stewart
 */
public class Photon {

	public static final double X = 1, Y = 1, Z = 1;
	
	public Vector r = new Vector(), n = new Vector();
	
	public Statistic stat = new Statistic();
	
	public Photon() {
	}

	/**
	 * This method randomizes the variables in the photon for a new simulation
	 * The photon starts at the top with a downward trajectory
	 */
	public void reset() {
		r.x = X * Math.random();
		r.y = Y * Math.random();
		r.z = Z;
		double theta = 2*Math.PI*Math.random(), phi = Math.PI/2*Math.random();
		n.x = Math.cos(theta)*Math.cos(phi);
		n.y = Math.sin(theta)*Math.cos(phi);
		n.z = -Math.sin(phi);
		stat.newPhoton(r);
	}

	/**
	 * Moves the photon to the indicated position
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
	 * @param v The surface normal vector
	 * @return Return true if the photon was absorbed, false if not
	 */
	public boolean bounce(Vector v) {
		n.add(v.clone().mul(-2*n.dot(v)));
		// Statistics here
		if(absorpsionChance()) {
			absorb();
			return true;
		} else {
			stat.addPath(r);
			return false;
		}
	}

	/**
	 * This method calculates whether the photon is absorbed or not
	 * @return true if absorbed
	 */
	public boolean absorpsionChance() {
		return (Math.random() < .2);
	}

	/**
	 * This method handles absorbtion of the photon
	 */
	public void absorb() {
		stat.addPath(r);
		stat.absorb(r);
	}

}
