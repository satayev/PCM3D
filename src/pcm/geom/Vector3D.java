package pcm.geom;

/**
 * <b>Mutable</b> representation of a vector in 3D space.
 * 
 * @author Satayev
 */
public class Vector3D {

	// for ease of use in other classes fields are public
	// TODO(satayev): perhaps allow coordinates to be Integer, Float,
	// BigDecimal, Fraction as well.
	public double x, y, z;

	/** Creates a zero vector. */
	public Vector3D() {
		x = y = z = 0;
	}

	/**
	 * Creates a vector on {@code z=0} plane.
	 * 
	 * @param x
	 *            x coordinate of the vector.
	 * @param y
	 *            y coordinate of the vector.
	 */
	public Vector3D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Creates a vector in 3D space.
	 * 
	 * @param x
	 *            x coordinate of the vector.
	 * @param y
	 *            y coordinate of the vector.
	 * @param z
	 *            z coordinate of the vector.
	 */
	public Vector3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Adds a vector to the current vector.
	 * 
	 * @param dx
	 *            x coordinate of the addend vector.
	 * @param dy
	 *            y coordinate of the addend vector.
	 * @param dz
	 *            z coordinate of the addend vector.
	 */
	public void add(double dx, double dy, double dz) {
		x += dx;
		y += dy;
		z += dz;
	}

	/**
	 * Adds a vector to the current vector.
	 * 
	 * @param dx
	 *            x coordinate of the addend vector.
	 * @param dy
	 *            y coordinate of the addend vector.
	 * @param dz
	 *            z coordinate of the addend vector.
	 */
	public void add(Vector3D addend) {
		this.x += addend.x;
		this.y += addend.y;
		this.z += addend.z;
	}

	/**
	 * Creates a new vector based on the summation of two addends.
	 * 
	 * @param a
	 *            addend vector.
	 * @param b
	 *            addend vector.
	 * @return sum of the two vectors.
	 */
	public static Vector3D add(Vector3D a, Vector3D b) {
		return new Vector3D(a.x + b.x, a.y + b.y, a.z + b.z);
	}

	/**
	 * Finds a distance to another point.
	 * 
	 * @param x
	 *            x coordinate of the point.
	 * @param y
	 *            y coordinate of the point.
	 * @param z
	 *            z coordinate of the point.
	 * @return distance between {@code this} and the argument vector.
	 */
	public double distance(double x, double y, double z) {
		double dx = this.x - x;
		double dy = this.y - y;
		double dz = this.z - z;
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	/**
	 * Finds a distance to another point.
	 * 
	 * @param that
	 *            requested point.
	 * @return distance to the requested point.
	 */
	public double distance(Vector3D that) {
		double dx = this.x - that.x;
		double dy = this.y - that.y;
		double dz = this.z - that.z;
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	/**
	 * Finds a dot product of this and requested vector.
	 * 
	 * @param x
	 *            x coordinate of the vector.
	 * @param y
	 *            y coordinate of the vector.
	 * @param z
	 *            z coordinate of the vector.
	 * @return dot product of this and the requested vector.
	 */
	public double dot(double x, double y, double z) {
		return x * this.x + y * this.y + z * this.z;
	}

	/**
	 * Finds a dot product of this and requested vector.
	 * 
	 * @param that
	 *            requested vector.
	 * @return dot product of this and the requested vector.
	 */
	public double dot(Vector3D that) {
		return this.x * that.x + this.y * that.y + this.z * that.z;
	}

	/**
	 * Finds a dot product of this and requested vector.
	 * 
	 * @param x
	 *            x coordinate of the vector.
	 * @param y
	 *            y coordinate of the vector.
	 * @param z
	 *            z coordinate of the vector.
	 * @return dot product of this and the requested vector.
	 */
	public Vector3D cross(double x, double y, double z) {
		return new Vector3D(this.y * z - this.z * y, this.z * x - this.x * z,
				this.x * y - this.y * x);
	}

	/**
	 * Finds a cross product of this and requested vector.
	 * 
	 * @param that
	 *            requested vector.
	 * @return dot product of this and the requested vector.
	 */
	public Vector3D cross(Vector3D that) {
		return new Vector3D(this.y * that.z - this.z * that.y, this.z * that.x
				- this.x * that.z, this.x * that.y - this.y * that.x);
	}

	public static Vector3D unitVector(Vector3D vector) {
		Vector3D result = new Vector3D(vector.x, vector.y, vector.z);
		double dist = vector.distance(0, 0, 0);
		if (dist > 0) {
			result.x /= dist;
			result.y /= dist;
			result.z /= dist;
		}
		return result;
	}

	/**
	 * Returns a copy of the current vector
	 * @return The copied vector
	 */
	public Vector3D copy() {
		return new Vector3D(x,y,z);
	}

	/**
	 * Sets the current vector to equal the values in r
	 * @param r The vector to copy from
	 */
	public void set(Vector3D r) {
		x = r.x;
		y = r.y;
		z = r.z;
	}

	/**
	 * Multiplies the vector by a scalar
	 * @param d The scalar to multiply by
	 * @return The current vector
	 */
	public Vector3D mult(double d) {
		x = d*x;
		y = d*y;
		z = d*z;
		return this;
	}
}
