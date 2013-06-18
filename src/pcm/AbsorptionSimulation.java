package pcm;

import pcm.model.Model;
import pcm.model.Photon;
import pcm.model.Statistics;
import pcm.model.geom.Hit;
import pcm.model.geom.Surface;
import pcm.model.geom.Wall;
import pcm.model.geom.solids.Solid;
import pcm.util.V;
import pcm.util.Vector;

public class AbsorptionSimulation {

  public final Model model;
  public final Statistics stats;

  public AbsorptionSimulation(Model model) {
    // TODO(satayev): implement Threads here
    this.model = model;
    this.stats = new Statistics();
  }

  // TODO(satayev): ISSOrbit should be generating photons?
  //                Implement PhotonFactory to efficiently reuse resources??
  private Photon resetPhoton() {
    Vector velocity = new Vector();
    double theta = Math.PI / 2 * (PCM3D.rnd.nextDouble() + 1), phi = Math.PI * 2 * PCM3D.rnd.nextDouble();
    velocity.x = Math.sin(theta) * Math.cos(phi);
    velocity.y = Math.sin(theta) * Math.sin(phi);
    velocity.z = Math.cos(theta);
    return new Photon(new Vector(model.length * (PCM3D.rnd.nextDouble() - 0.5), model.width * (PCM3D.rnd.nextDouble() - 0.5), model.height), velocity);
  }

  public void run(int n) throws Exception {
    Photon photon = new Photon(new Vector(), new Vector());
    while (n-- > 0) {
      photon = resetPhoton();

      boolean done = false;
      for (Solid s : model.cnts)
        if (s.contains(photon.p)) {
          photon.bounce(V.K);
          s.absorb(photon);
          done = true;
        }

      while (!done) {
        Hit closest = null;
        // find the closest boundary
        for (Surface s : model.bounds) {
          Hit hit = s.getHit(photon);
          if (hit != null && hit.distance < Double.POSITIVE_INFINITY)
            if (closest == null || hit.distance < closest.distance)
              closest = hit;
        }
        // find the closest CNT
        for (Surface s : model.cnts) {
          Hit hit = s.getHit(photon);
          if (hit != null && hit.distance < Double.POSITIVE_INFINITY)
            if (closest == null || hit.distance < closest.distance)
              closest = hit;
        }

        if (closest.surface == model.ceiling)
          done = true;
        else {
          // travel to the surface
          photon.travel(closest.distance);
          if (closest.surface instanceof Wall)
            ((Wall) closest.surface).wrap(photon);
          else {
            photon.bounce(closest.surface.normalAt(closest));
            done = closest.surface.absorb(photon);
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
