package pcm.util;

import pcm.geom.Vector;

/**
 * Static methods to work with {@code Vector}.
 * 
 * @author Satayev
 */
public final class V {

  /** Used for double comparison */
  public static final double EPS = 1e-9;

  public static final double INFINITY = Double.POSITIVE_INFINITY;

  /**
   * Adds two vectors and returns a new vector.
   * 
   * @param a addend vector.
   * @param b addend vector.
   * @return sum of the two vectors.
   */
  public static Vector add(Vector a, Vector b) {
    return new Vector(a.x + b.x, a.y + b.y, a.z + b.z);
  }

  /**
   * Subtracts two vectors and returns a new vector
   * 
   * @param a vector one.
   * @param b vector two.
   * @return sum of the two vectors.
   */
  public static Vector sub(Vector a, Vector b) {
    return new Vector(a.x - b.x, a.y - b.y, a.z - b.z);
  }

  /**
   * Performs a scalar multiplcation.
   * 
   * @param k scalar cooeficient.
   * @param a vector.
   * @return a new vector.
   */
  public static Vector mul(double k, Vector a) {
    return new Vector(k * a.x, k * a.y, k * a.z);
  }

  /** @return returns a dot product of two vectors. */
  public static double dot(Vector a, Vector b) {
    return a.x * b.x + a.y * b.y + a.z * b.z;
  }

  /**
   * Reflects a vector given the normal vector.
   * 
   * @param a requested vector.
   * @param n normal vector.
   * @return reflected vector.
   */
  public static Vector reflect(Vector a, Vector n) {
    double k = 2 * dot(a, n);
    Vector result = new Vector();
    result.x = a.x - k * n.x;
    result.y = a.y - k * n.y;
    result.z = a.z - k * n.z;
    return result;
  }

  /**
   * Refracts a vector given the normal and refraction coefficient.
   * 
   * @param a requested vector.
   * @param n normal vector.
   * @param q refraction coefficient.
   * @return refracted vector.
   */
  public static Vector refract(Vector a, Vector n, double q) {
    double dot = dot(a, n);
    double k;
    if (dot > 0) {
      dot = -dot;
      k = q;
    } else {
      k = 1.0 / q;
    }
    double D = 1 - k * k * (1 - dot * dot);
    if (D < 0)
      return null;
    double b = -(dot * k + Math.sqrt(D));

    Vector result = new Vector();
    result.x = k * a.x + b * n.x;
    result.y = k * a.y + b * n.y;
    result.z = k * a.z + b * n.z;
    return result;
  }

  /**
   * Creates a unit vector of the passed vector.
   * 
   * @param vector requested vector.
   * @return a unit vector
   */
  public static Vector normalize(Vector vector) {
    Vector result = new Vector(vector.x, vector.y, vector.z);
    double dist = vector.distance(0, 0, 0);
    if (dist > 0) {
      result.x /= dist;
      result.y /= dist;
      result.z /= dist;
    }
    return result;
  }
}
