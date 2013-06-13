package pcm.model.geom;

import pcm.model.Photon;
import pcm.util.V;
import pcm.util.Vector;

/**
 * @author Satayev
 */
public class Prism extends Surface {

  public double height;
  public Base base;

  public Prism(Vector position, double height, Base base) {
    super(position, null);
    this.height = height;
    this.base = base;
  }

  @Override
  public Vector normalAt(Hit hit) {
    if (hit.v != null)
      return hit.v;
    Vector v = base.normalAt(hit);
    v.z = hit.v.z;
    return v;
  }

  @Override
  public Hit getHit(Photon photon, boolean computePosition) {
    if (height + V.EPS < photon.p.z && photon.v.z + V.EPS < 0) {
      double distance = (height - photon.p.z) / photon.v.z;
      Vector at = V.scaleAdd(photon.p, distance, photon.v);
      if (base.inside(at))
        return new Hit(distance, this, new Vector(0, 0, 1));
      else
        return null;
    }

    Hit hit = base.getHit(photon, true);
    if (hit == null)
      return null;
    double z = photon.p.z + photon.v.z * hit.distance;
    if (z + V.EPS < 0 || z > height + V.EPS)
      return null;

    hit.surface = this;
    hit.v = new Vector(photon.p.x + photon.v.x * hit.distance, photon.p.y + photon.v.y * hit.distance);
    return hit;
  }
}
