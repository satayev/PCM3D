package dev.simple;

import pcm.model.geom.Vector;

public class Floor extends Surface {

  @Override
  public double collisionDistance(Photon p) {
    double d = -p.r.z / p.n.z;
    if (d < 0 || p.n.z > 0)
      d = Double.POSITIVE_INFINITY;
    return d;
  }

  @Override
  public boolean collision(Photon p) {
    double d = collisionDistance(p);
    p.move(d);
    return p.bounce(new Vector(0, 0, 1));
  }

}
