package pcm.model.orbit;

import pcm.model.geom.Vector;

public class Sgp4Data {

  public Vector pos = new Vector();
  public Vector vel = new Vector();

  public Sgp4Data(Vector pos, Vector vel) {
    this.pos = pos;
    this.vel = vel;
  }

}
