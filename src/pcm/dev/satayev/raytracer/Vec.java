package pcm.dev.satayev.raytracer;

public class Vec {

  double[] a = new double[3];

  public Vec() {

  }

  public static Vec rand() {
    Vec v = new Vec();
    for (int i = 0; i < v.a.length; i++)
      v.a[i] = Math.random();
    return v;
  }

  public Vec clone() {
    Vec v = new Vec();
    v.a = a.clone();
    return v;
  }

  public Vec(double x, double y, double z) {
    a[0] = x;
    a[1] = y;
    a[2] = z;
  }

  public static double dot(Vec a, Vec b) {
    double r = 0;
    for (int i = 0; i < 3; i++)
      r += a.a[i] * b.a[i];
    return r;
  }

  public static double sqrdist(Vec a, Vec b) {
    double r = 0;
    for (int i = 0; i < 3; i++)
      r += (a.a[i] - b.a[i]) * (a.a[i] - b.a[i]);
    return r;
  }

  public static Vec sub(Vec a, Vec b) {
    Vec v = new Vec();
    for (int i = 0; i < 3; i++)
      v.a[i] = a.a[i] - b.a[i];
    return v;
  }

  public static Vec add(Vec a, Vec b) {
    Vec v = new Vec();
    for (int i = 0; i < 3; i++)
      v.a[i] = a.a[i] + b.a[i];
    return v;
  }

  public static Vec mul(double f, Vec a) {
    Vec v = new Vec();
    for (int i = 0; i < 3; i++)
      v.a[i] = a.a[i] * f;
    return v;
  }

  public static double sqrlen(Vec a) {
    double r = 0;
    for (double x : a.a)
      r += x * x;
    return r;
  }

  public static double len(Vec a) {
    return Math.sqrt(sqrlen(a));
  }

  public static Vec norm(Vec a) {
    return mul(1. / len(a), a);
  }

  public static double det(double[][] m) {
    int n = m.length;
    for (int i = 0; i < n; i++)
      for (int j = i + 1; j < n; j++) {
        if (m[i][i] == 0)
          continue;
        double f = m[j][i] / m[i][i];
        for (int k = i; k < n; k++)
          m[j][k] -= f * m[i][k];
      }
    double det = 1;
    for (int i = 0; i < n; i++)
      det *= m[i][i];
    return det;
  }

  public static Vec reflect(Vec a, Vec n) {
    double f = 2 * dot(a, n);
    Vec v = new Vec();
    for (int i = 0; i < 3; i++)
      v.a[i] = a.a[i] - f * n.a[i];
    return v;
  }

  public static Vec refract(Vec v, Vec n, double q) {
    double nv = dot(n, v);
    double bf, a;
    if (nv > 0) {
      nv = -nv;
      bf = -1;
      a = q;
    } else {
      a = 1 / q;
      bf = -1;
    }
    double D = 1 - a * a * (1 - nv * nv);
    if (D < 0)
      return null;
    double b = bf * (nv * a + Math.sqrt(D));
    Vec p = new Vec();
    for (int i = 0; i < 3; i++)
      p.a[i] = a * v.a[i] + b * n.a[i];
    return p;
  }

}
