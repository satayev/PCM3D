package pcm;

import pcm.model.Photon;
import pcm.model.SimpleModel;
import pcm.model.Statistics;
import pcm.model.geom.Hit;
import pcm.model.geom.Surface;
import pcm.model.geom.Wall;
import pcm.util.V;
import pcm.util.Vector;

public class AbsorptionSimulation {

  public final int size = 100;
  public SimpleModel model;
  public Statistics stats;

  public AbsorptionSimulation() {
    // TODO(satayev): implement Threads here
    model = new SimpleModel(size, size, size);
    stats = new Statistics();
  }

  // TODO(satayev): ISSOrbit should be generating photons?
  //                Implement PhotonFactory to efficiently reuse resources??
  private Photon resetPhoton() {
    Vector velocity = new Vector();
    double theta = Math.PI * 3 / 4 * (PCM3D.rnd.nextDouble() / 4 + 1), phi = Math.PI * 2 * PCM3D.rnd.nextDouble();
    velocity.x = Math.sin(theta) * Math.cos(phi);
    velocity.y = Math.sin(theta) * Math.sin(phi);
    velocity.z = Math.cos(theta);
    return new Photon(new Vector(size * (PCM3D.rnd.nextDouble() - 0.5), size * (PCM3D.rnd.nextDouble() - 0.5), size * 2 - 1), velocity);
  }

  @Deprecated
  public void run(int n) throws Exception {
    Photon photon = new Photon(new Vector(), new Vector());
    while (n-- > 0) {
      photon = resetPhoton();

      boolean done = false;
      while (!done) {
        Hit closest = null;

        // find the closest surface
        for (Surface s : model.surfaces) {
          Hit hit = s.getHit(photon, true);
          if (hit != null && hit.distance > V.EPS && hit.distance < Double.POSITIVE_INFINITY)
            if (closest == null || hit.distance < closest.distance)
              closest = hit;
        }

        if ((photon.p.z > size && photon.v.z > V.EPS) || closest == null || closest.distance == Double.POSITIVE_INFINITY)
          done = true;
        else {
          // travel to the surface
          photon.travel(closest.distance);
          if (closest.surface instanceof Wall)
            ((Wall) closest.surface).wrap(photon);
          else {
            if (closest.surface.absorb(photon)) {
              photon.absorbed = true;
              done = true;
            } else
              photon.bounce(closest.surface.normalAt(closest));
          }
        }
      }

      stats.update(photon);
    }
  }

  public void printStats() {
    System.out.println(stats);
  }
}
