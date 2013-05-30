package pcm.dev.satayev.raytracer;

public class Ray {

  Vec from, to, dir;
  double power;
  
  public Ray(Vec to, Vec from, double power) {
    this.from = from;
    this.to = to;
    this.power = power;
    this.dir = Vec.norm(Vec.sub(to, from));
  }
  
}
