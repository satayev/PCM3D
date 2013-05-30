package john;

/**
 * This class contains all information for the photon
 * @author John Stewart
 */
public class Photon {

	public static final double X = 1, Y = 1, Z = 1;;
	public double rx, ry, rz, nx, ny, nz;
	public double e = .0001; // Uncertainty in position, for error
	
	public Photon() {
		reset();
	}

	/**
	 * This method randomizes the variables in the photon for a new simulation
	 * The photon starts at the top with a downward trajectory
	 */
	public void reset() {
		rx = X * Math.random();
		ry = Y * Math.random();
		rz = Z;
		double theta = 2*Math.PI*Math.random(), phi = Math.PI/2*Math.random();
		nx = Math.cos(theta)*Math.cos(phi);
		ny = Math.sin(theta)*Math.cos(phi);
		nz = -Math.sin(phi);
	}
	
	/**
	 * Moves the photon to the indicated position
	 * @param rx X-coordinate
	 * @param ry Y-coordinate
	 * @param rz Z-coordinate
	 */
	public void move(double rx, double ry, double rz) {
		this.rx = rx;
		this.ry = ry;
		this.rz = rz;
	}

	public void move(double d) {
		rx += d*nx;
		ry += d*ny;
		rz += d*nz;
	}

	/**
	 * Reflects the velocity on the vector given and makes a statistical recording
	 * Checks if photon is absorbed instead.  
	 * @param nx X-coordinate of surface normal vector
	 * @param ny Y-coordinate of surface normal vector
	 * @param nz Z-coordinate of surface normal vector
	 * @return Return true if the photon bounced, false if it was absorbed
	 */
	public boolean bounce(double nx, double ny, double nz) {
		double r = -2 * (this.nx * nx + this.ny * ny + this.nz * nz);
		this.nx += r * nx;
		this.ny += r * ny;
		this.nz += r * nz;
		// Statistics here
		if(absorbsionChance()) return false;
		else return false;
	}

	/**
	 * This method calculates whether the photon is absorbed or not
	 * @return true if absorbed
	 */
	private boolean absorbsionChance() {
		return (Math.random() < .2);
	}

	/**
	 * This method handles absorbtion of the photon
	 */
	public void absorb() {
		// TODO Statistics
	}

}
