package pcm.model.geom;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Static methods to work with {@code Vector}.
 * 
 * @author Satayev
 */
public final class V {

  /** Used for double comparison */
  public static final double EPS = 1e-7;
  public static final double INFINITY = Double.POSITIVE_INFINITY;
  public static final Vector I = new Vector(1, 0, 0), J = new Vector(0, 1, 0), K = new Vector(0, 0, 1);
  public static final DecimalFormat DF = new DecimalFormat("#0.000000");

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
  public static Vector mult(double k, Vector a) {
    return new Vector(k * a.x, k * a.y, k * a.z);
  }

  /**
   * Finds a cross product of two vectors
   * 
   * @param a first vector.
   * @param b second vector.
   * @return dot product of this and the requested vector.
   */
  public static Vector cross(Vector a, Vector b) {
    return new Vector(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);
  }

  /**
   * Creates a new vector by adding one to a scaled multiple of another
   * 
   * @param scale the input scale
   * @param vector the input vector
   */
  public static Vector scaleAdd(Vector a, double scale, Vector b) {
    return new Vector(a.x + scale * b.x, a.y + scale * b.y, a.z + scale * b.z);
  }

  /**
   * Creates a new vector by adding a multiple of one to a multiple of another
   * 
   * @param scale the input scale
   * @param vector the input vector
   */
  public static Vector scaleAdd(double scale1, Vector a, double scale2, Vector b) {
    return new Vector(scale1 * a.x + scale2 * b.x, scale1 * a.y + scale2 * b.y, scale1 * a.z + scale2 * b.z);
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

  /**
   * Determines the mean of a list of vectors
   * 
   * @param list The list of vectors
   * @return The mean vector of the list
   */
  public static Vector mean(List<Vector> list) {
    Vector mean = new Vector(0, 0, 0);
    for (int i = 0; i < list.size(); i++)
      mean.add(list.get(i));
    mean.mult(1. / list.size());
    return mean;
  }

  /**
   * Rotates vector p along the line (A + t*dx) by theta rad.
   * 
   * @param p vector to rotate
   * @param A point on the line
   * @param dx direction of the line
   * @param theta angle of rotation
   * @return rotation of p
   */
  public static Vector rotate(Vector p, Vector A, Vector dx, double theta) {
    Vector q = new Vector(0, 0, 0);
    dx.normalize();
    double x = p.x, y = p.y, z = p.z;
    double a = A.x, b = A.y, c = A.z;
    double u = dx.x, v = dx.y, w = dx.z;
    q.x = (a * (v * v + w * w) - u * (b * v + c * w - u * x - v * y - w * z)) * (1 - Math.cos(theta)) + x * Math.cos(theta)
        + (-c * v + b * w - w * y + v * z) * Math.sin(theta);
    q.y = (b * (u * u + w * w) - v * (a * u + c * w - u * x - v * y - w * z)) * (1 - Math.cos(theta)) + y * Math.cos(theta)
        + (c * u - a * w + w * x - u * z) * Math.sin(theta);
    q.z = (c * (u * u + v * v) - w * (a * u + b * v - u * x - v * y - w * z)) * (1 - Math.cos(theta)) + z * Math.cos(theta)
        + (-b * u + a * v - v * x + u * y) * Math.sin(theta);
    return q;
  }

  public static Vector rotateAbout(Vector vector, Vector rotation, double angle) {
    rotation.normalize();
    Vector next = V.mult(vector.dot(rotation) * (1-Math.cos(angle)), rotation);
    next.add(V.mult(Math.cos(angle), vector));
    next.add(V.mult(Math.sin(angle),V.cross(rotation, vector)));
    return next;
  }

  public static Vector matrixRotate(Vector[] matrix, Vector vector) {
    if (matrix.length != 3 || matrix[0] == null || matrix[1] == null || matrix[2] == null) {
      System.err.println("Invalid Matrix: " + matrix.length);
      return vector;
    }
    if (vector == null) return vector;
    return new Vector(matrix[0].dot(vector), matrix[1].dot(vector), matrix[2].dot(vector));
  }
}
