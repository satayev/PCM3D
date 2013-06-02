package pcm.geom;

/**
 * <b>Mutable</b> representation of a vector in 3D space.
 * 
 * @author Satayev
 */
public class Vector {

  // TODO(satayev): perhaps allow coordinates to be Integer, Float, BigDecimal, Rational as well.
  public double x, y, z;

  /** Creates a zero vector. */
  public Vector() {
  }

  /**
   * Creates a vector in 3D space.
   * 
   * @param x x coordinate of the vector.
   * @param y y coordinate of the vector.
   * @param z z coordinate of the vector.
   */
  public Vector(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * Adds a vector to the current vector.
   * 
   * @param dx x coordinate of the addend vector.
   * @param dy y coordinate of the addend vector.
   * @param dz z coordinate of the addend vector.
   */
  public void add(double dx, double dy, double dz) {
    x += dx;
    y += dy;
    z += dz;
  }

  /**
   * Adds a vector to the current vector.
   * 
   * @param dx x coordinate of the addend vector.
   * @param dy y coordinate of the addend vector.
   * @param dz z coordinate of the addend vector.
   */
  public void add(Vector addend) {
    this.x += addend.x;
    this.y += addend.y;
    this.z += addend.z;
  }

  /**
   * Finds a distance to another point.
   * 
   * @param x x coordinate of the point.
   * @param y y coordinate of the point.
   * @param z z coordinate of the point.
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
   * @param that requested point.
   * @return distance to the requested point.
   */
  public double distance(Vector that) {
    double dx = this.x - that.x;
    double dy = this.y - that.y;
    double dz = this.z - that.z;
    return Math.sqrt(dx * dx + dy * dy + dz * dz);
  }

  /**
   * Finds a dot product of this and requested vector.
   * 
   * @param x x coordinate of the vector.
   * @param y y coordinate of the vector.
   * @param z z coordinate of the vector.
   * @return dot product of this and the requested vector.
   */
  public double dot(double x, double y, double z) {
    return x * this.x + y * this.y + z * this.z;
  }

  /**
   * Finds a dot product of this and requested vector.
   * 
   * @param that requested vector.
   * @return dot product of this and the requested vector.
   */
  public double dot(Vector that) {
    return this.x * that.x + this.y * that.y + this.z * that.z;
  }

  /**
   * Finds a dot product of this and requested vector.
   * 
   * @param x x coordinate of the vector.
   * @param y y coordinate of the vector.
   * @param z z coordinate of the vector.
   * @return dot product of this and the requested vector.
   */
  public Vector cross(double x, double y, double z) {
    return new Vector(this.y * z - this.z * y, this.z * x - this.x * z, this.x * y - this.y * x);
  }

  /**
   * Finds a cross product of this and requested vector.
   * 
   * @param that requested vector.
   * @return dot product of this and the requested vector.
   */
  public Vector cross(Vector that) {
    return new Vector(this.y * that.z - this.z * that.y, this.z * that.x - this.x * that.z, this.x * that.y - this.y * that.x);
  }

  /** @return Length of the vector. */
  public double length() {
    return Math.sqrt(x * x + y * y + z * z);
  }

  /** @return Squared length of the vector. */
  public double sqrlength() {
    return x * x + y * y + z * z;
  }

  /**
   * Reflects a vector given the normal vector.
   * 
   * @param n normal vector.
   */
  public void reflect(Vector n) {
    double k = 2 * this.dot(n);
    this.x = this.x - k * n.x;
    this.y = this.y - k * n.y;
    this.z = this.z - k * n.z;
  }

  /**
   * Refracts a vector given the normal and refraction coefficient.
   * x,y,z are NaN if refraction is impossible.
   * 
   * @param n normal vector.
   * @param q refraction coefficient.
   */
  public void refract(Vector n, double q) {
    double dot = this.dot(n);
    double ka, kb;
    if (dot > 0) {
      dot = -dot;
      ka = q;
      kb = 1;
    } else {
      ka = 1.0 / q;
      kb = -1;
    }
    double D = 1 - ka * ka * (1 - dot * dot);
    double b = kb * (dot * ka + Math.sqrt(D));

    this.x = ka * this.x + b * n.x;
    this.y = ka * this.y + b * n.y;
    this.z = ka * this.z + b * n.z;
  }

  /** Normalizes this vector. */
  public Vector normalize() {
    double len = length();
    if (len > 0) {
      this.x /= len;
      this.y /= len;
      this.z /= len;
    }
    return this;
  }

  /**
   * @return a copy of this vector
   */
  public Vector clone() {
    return new Vector(x, y, z);
  }

  /**
   * Sets the current vector to equal the values in r
   * 
   * @param r The vector to copy from
   */
  public void set(Vector r) {
    x = r.x;
    y = r.y;
    z = r.z;
  }

  /**
   * Multiplies the vector by a scalar
   * 
   * @param d The scalar to multiply by
   * @return The current vector
   */
  public Vector mul(double d) {
    x = d * x;
    y = d * y;
    z = d * z;
    return this;
  }

  @Override
  public String toString() {
    return "[" + x + ", " + y + ", " + z + "]";
  }
}
