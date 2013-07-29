package pcm;

import java.util.ArrayList;
import java.util.List;

import pcm.model.Model;
import pcm.model.Photon;
import pcm.model.RectangularPrismModel;
import pcm.model.Statistics;
import pcm.model.geom.Hit;
import pcm.model.geom.Surface;
import pcm.model.geom.V;
import pcm.model.geom.Vector;
import pcm.model.geom.Wall;
import pcm.model.geom.solids.Solid;
import pcm.model.orbit.ISSOrbit;
import pcm.model.statics.Wavelength;
import pcm.model.statics.WavelengthAM0;

public class AbsorptionSimulation {

  public final Model model;
  public Statistics stats;
  public final ISSOrbit issOrbit;
  public final Wavelength wavelength;

  public AbsorptionSimulation(Model model, ISSOrbit issOrbit) {
    // TODO(satayev): implement Threads here
    this.model = model;
    this.issOrbit = issOrbit;
    this.wavelength = new WavelengthAM0();
    this.stats = new Statistics();
  }

  public AbsorptionSimulation(RectangularPrismModel model, Wavelength wavelength) {
    this.model = model;
    this.issOrbit = new ISSOrbit();
    this.wavelength = wavelength;
    this.stats = new Statistics();
  }

  private void resetPhoton(Photon photon, Vector v0) {
    photon.absorbed = false;
    photon.reflectionCounter = 0;
    
    photon.p.x = model.X * (PCM3D.rnd.nextDouble() - 0.5);
    photon.p.y = model.Y * (PCM3D.rnd.nextDouble() - 0.5);
    photon.p.z = model.Z;

    if (v0 == null) photon.v = new Vector(0,0,1);
    else if (v0.length() == 0) photon.v = issOrbit.getSunlightDirection(0);
    else photon.v = v0.clone();
    photon.v0 = photon.v.clone();
    
    photon.path = new ArrayList<List<Vector>>();
    List<Vector> list = new ArrayList<Vector>();
    list.add(V.sub(photon.p,photon.v));
    photon.path.add(list);
    
    photon.w = wavelength.genWavelength();
    photon.E = 1.99e-25 / photon.w;
  }

  public void run(int n, Vector v0) throws Exception {
    Photon photon = new Photon(new Vector(), new Vector());
    System.out.println("Start: "+ v0);
    while (n-- > 0) {
      resetPhoton(photon, v0);

      boolean done = false;

      if (photon.v0.z >= -V.EPS) {
        photon.absorbed = false;
        done = true;
      }

      if (!done) for (Solid s : model.cnts)
        if (s.contains(photon.p)) {
          photon.bounce(V.K);
          s.absorb(photon);
          photon.path.get(photon.path.size()-1).add(photon.p);
          done = true;
          break;
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

          if (closest.surface == model.ceiling) {
            photon.path.get(photon.path.size()-1).add(V.add(photon.p, photon.v));
            done = true;
          } else
            done = closest.surface.absorb(photon);
        }
      }
      stats.update(photon);
    }
    System.out.println("Finish");
  }

  public void run(int n) throws Exception {
    run(n, new Vector(0, 0, 0));
  }

  public void printStats() {
    System.out.println(stats);
  }
}
