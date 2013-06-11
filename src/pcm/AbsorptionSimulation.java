package pcm;

import pcm.model.Photon;
import pcm.model.SimpleModel;
import pcm.model.Statistics;
import pcm.model.geom.Surface;
import pcm.model.geom.Wall;
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
//    theta = Math.PI / 2 + Math.PI / 4;
//    phi = Math.PI / 4;
    velocity.x = Math.sin(theta) * Math.cos(phi);
    velocity.y = Math.cos(theta);
    velocity.z = Math.sin(theta) * Math.sin(phi);
    return new Photon(new Vector(size * (PCM3D.rnd.nextDouble() - 0.5), size * 2 - 1, size * (PCM3D.rnd.nextDouble() - 0.5)), velocity);
  }

  public void run(int n) throws Exception {
    Photon photon = new Photon(new Vector(), new Vector());

    while (n-- > 0) {
      photon = resetPhoton();
      //      if (n < 2035)
      //        System.out.println(n + " " + photon.v);

      boolean done = false;
      while (!done) {
        Surface closestSurface = null;
        double time = Double.POSITIVE_INFINITY;

        // find the closest surface
        for (Surface s : model.surfaces) {
          double t = s.travelTime(photon);
          if (t > 0 && t < time) {
            time = t;
            closestSurface = s;
          }
        }

        if (closestSurface == null || time == Double.POSITIVE_INFINITY)
          throw new Exception("Photon must always either be absorbed or hit a ceiling.");

        // travel to the surface
        photon.travel(time);

        // either escape through the ceiling, wrap around the walls, bounce from the surface, or get absorbed
        if (closestSurface == model.ceiling) {
          photon.absorbed = false;
          done = true;
        } else if (closestSurface instanceof Wall)
          ((Wall) closestSurface).wrap(photon);
        else {
          if (closestSurface.absorb(photon)) {
            photon.absorbed = true;
            done = true;
          } else
            photon.bounce(closestSurface.normal(photon.p));
        }

      }

      stats.update(photon);
    }
  }

  public void printStats() {
    System.out.println(stats);
  }
}
