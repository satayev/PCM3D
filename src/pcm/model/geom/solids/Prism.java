package pcm.model.geom.solids;

import pcm.model.Photon;
import pcm.model.geom.Hit;
import pcm.model.geom.V;
import pcm.model.geom.Vector;
import pcm.model.geom.curves.Curve;

/**
 * Vertical prism with infinite height. Base of the prism is defined by a {@link Curve}.
 * 
 * @author Satayev
 */
public class Prism extends Solid {

  public Curve base;

  public Prism(Vector position, Curve base) {
    super(position);
    this.base = base;
  }

  @Override
  public Vector normalAt(Hit hit) {
    return base.normalAt(hit);
  }

  @Override
  public Hit getHit(Photon photon) {
    Hit hit = base.getHit(photon);
    if (hit == null)
      return null;
    hit.surface = this;
    hit.v = V.scaleAdd(photon.p, hit.distance, photon.v);
    return hit;
  }

  @Override
  public boolean contains(Vector p) {
    return base.contains(p);
  }
}
