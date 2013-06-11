package pcm.model.geom;

import pcm.model.Photon;
import pcm.util.V;
import pcm.util.Vector;

public class Floor extends Plane {

  public Floor(Vector position, Vector normal) {
    super(position, normal);
  }

  @Override
  public double travelTime(Photon photon) {
    double d = -photon.p.y / photon.v.y;
    if (photon.p.y < V.EPS || photon.v.y > -V.EPS)
      d = Double.POSITIVE_INFINITY;
    return d;
  }

}
