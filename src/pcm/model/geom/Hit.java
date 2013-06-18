package pcm.model.geom;

import pcm.util.Vector;

public class Hit {

  public Surface surface;
  public double distance;

  public int i;
  public double d;
  public Vector v;

  public Hit(double distance, Surface surface) {
    this.distance = distance;
    this.surface = surface;
  }

  public Hit(double distance, Surface surface, int position) {
    this.distance = distance;
    this.surface = surface;
    this.i = position;
  }

  public Hit(double distance, Surface surface, double position) {
    this.distance = distance;
    this.surface = surface;
    this.d = position;
  }

  public Hit(double distance, Surface surface, Vector position) {
    this.distance = distance;
    this.surface = surface;
    this.v = position;
  }

}
