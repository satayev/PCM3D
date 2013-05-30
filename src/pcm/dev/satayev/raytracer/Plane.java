package pcm.dev.satayev.raytracer;

public class Plane {
  public Vec p,  n, color;
  int axis;
  double paxis;
  public Material m;
  public double pn;
  
  public Plane(Vec p, Vec n, Vec color, Material m) {
    this.p = p;
    this.n = Vec.norm(n);
    this.pn = Vec.dot(p, n);
    this.color = color;
    this.m = m;
  }
  
  public Vec norm() {
    return n;
  }
  public Intersection trace(Ray ray) {
    Vec s = ray.dir;
    double sn = Vec.dot(s, n);
    if (sn == 0) return null;
    Vec a = ray.from;
    double t = (pn - Vec.dot(a, n)) / sn;
    if (t < 1e-5) return null;
    Vec q = new Vec();
    for (int i = 0; i < 3; i++)
      q.a[i] = a.a[i] + t*s.a[i];
    return new Intersection(q, t);
  }
  
  public static Plane axisplane(Vec p, int axis, Vec color, Material material) {
    Plane r = new Plane(p, new Vec(axis == 0 ? 1 : 0, axis == 1 ? 1 : 0, axis == 2 ? 1 : 0), color, material);
    r.axis = axis;
    r.paxis = p.a[axis];
    return r;
  }
}
