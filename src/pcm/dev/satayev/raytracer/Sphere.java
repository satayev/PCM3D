package pcm.dev.satayev.raytracer;


public class Sphere {

  Vec c, color;
  double r, r2, ir;
  Material m;

  public Sphere(Vec c, double r, Vec color, Material m) {
    this.c = c;
    this.r = r;
    this.r2 = r * r;
    this.ir = 1 / r;
    this.m = m;
    this.color = color;
  }

  public Vec norm(Vec at) {
    Vec n = new Vec();
    for (int i = 0; i < 3; i++)
      n.a[i] = ir * (at.a[i] - c.a[i]);
    return n;
  }

  public Intersection trace(Ray ray) {
    Vec a = ray.from;
    Vec ac = Vec.sub(c, a);
    Vec s = ray.dir;
    double acs = Vec.dot(ac, s);
    if (acs < 0)
      return null;
    double ac2 = Vec.sqrlen(ac);
    double d2 = ac2 - r2;
    double D = acs * acs - d2;
    if (D < 0)
      return null;
    double sD = Math.sqrt(D);
    double t = acs - sD;
    if (t < 1e-5)
      t = acs + sD;
    if (t < 1e-5)
      return null;
    Vec p = new Vec();
    for (int i = 0; i < 3; i++)
      p.a[i] = a.a[i] + t * s.a[i];
    return new Intersection(p, t);
  }
}
