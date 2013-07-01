package pcm;

import pcm.model.Model;
import pcm.model.Photon;
import pcm.model.Statistics;
import pcm.model.geom.Hit;
import pcm.model.geom.Surface;
import pcm.model.geom.V;
import pcm.model.geom.Vector;
import pcm.model.geom.Wall;
import pcm.model.geom.solids.Solid;

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
  private Photon resetPhoton(Vector v0) {
    Vector velocity = v0.clone();
    if (velocity.dot(velocity) == 0) {
      double theta = Math.PI / 2 * (PCM3D.rnd.nextDouble() + 1), phi = Math.PI * 2 * PCM3D.rnd.nextDouble();
      velocity.x = Math.sin(theta) * Math.cos(phi);
      velocity.y = Math.sin(theta) * Math.sin(phi);
      velocity.z = Math.cos(theta);
    }
    return new Photon(new Vector(model.X * (PCM3D.rnd.nextDouble() - 0.5), model.Y * (PCM3D.rnd.nextDouble() - 0.5),
        model.Z), velocity);
  }

  public void run(int n, Vector v0) throws Exception {
    Photon photon = new Photon(new Vector(), new Vector());
    while (n-- > 0) {
      photon = resetPhoton(v0);

      boolean done = false;
      for (Solid s : model.cnts)
        if (s.contains(photon.p)) {
          photon.bounce(V.K);
          s.absorb(photon);
          photon.path.get(photon.path.size()-1).add(photon.p);
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

        // travel to the surface
        photon.travel(closest.distance);
        if (closest.surface instanceof Wall)
          ((Wall) closest.surface).wrap(photon);
        else {
          photon.bounce(closest.surface.normalAt(closest));

          if (closest.surface == model.ceiling)
            done = true;
          else
            done = closest.surface.absorb(photon);
        }
      }
      stats.update(photon);
    }
  }

  public void run(int n) throws Exception {
    run(n, new Vector(0, 0, 0));
  }

  public void printStats() {
    System.out.println(stats);
  }
  
}
