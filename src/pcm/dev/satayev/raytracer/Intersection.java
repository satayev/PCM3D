package pcm.dev.satayev.raytracer;

public class Intersection {

  public Vec at;
  public double dist;
  public Object owner;
  
  public Intersection(Vec at, double d) {
    this.at = at;
    this.dist = d;
  }
}
