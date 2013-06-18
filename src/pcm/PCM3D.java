package pcm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pcm.model.AngledPhoton;
import pcm.model.FixedPhoton;
import pcm.model.Photon;
import pcm.model.geom.Polygon;
import pcm.model.geom.Prism;
import pcm.model.geom.Surface;
import pcm.util.V;
import pcm.util.Vector;

/**
 * Entry-point class for the 3D Photovoltaic Computer Modeling program.
 * 
 * @author Satayev
 * @version 1.0.0.0
 */
public final class PCM3D {

  public final static Random rnd = new Random(42);

  /**
   * @param args currently ignored
   */
  public static void main(String[] args) throws Exception {
    long start = System.currentTimeMillis();

    double size = 1;
    Vector[] boundary = { new Vector(size,0,0), new Vector(0,size,0) };
    double height = 2*size;
    
    List<Surface> surfaces = new ArrayList<Surface>();
    
//    surfaces.add(new Sphere(new Vector(0, 0, 0), height / 3));
//
//    Vector[] v = new Vector[] {
//        new Vector(25, 25, 0), new Vector(25, -25, 0), new Vector(-25, -25, 0), new Vector(-25, 25, 0),
//        new Vector(25, 25, 100), new Vector(25, -25, 100), new Vector(-25, -25, 100), new Vector(-25, 25, 100),
//    };
//    surfaces.add(new Polygon3D(new Vector[] { v[0], v[1], v[2], v[3] }));
//    surfaces.add(new Polygon3D(new Vector[] { v[0], v[1], v[5], v[4] }));
//    surfaces.add(new Polygon3D(new Vector[] { v[1], v[2], v[6], v[5] }));
//    surfaces.add(new Polygon3D(new Vector[] { v[2], v[3], v[7], v[6] }));
//    surfaces.add(new Polygon3D(new Vector[] { v[3], v[0], v[4], v[7] }));
//    surfaces.add(new Polygon3D(new Vector[] { v[4], v[5], v[6], v[7] }));
//
    surfaces.add(new Prism(new Vector(), 1, new Polygon(new Vector[] { new Vector(-.25, -.25, 0), new Vector(-.25, .25, 0),
        new Vector(.25, 25, 0), new Vector(.25, -.25, 0) })));
//
//    surfaces.add(new Prism(new Vector(), 100, new Curve(50)));

    Photon photon = new FixedPhoton(new Vector(), new Vector(), boundary, height, new Vector(.05,0,2), V.normalize(new Vector(.5,0,-2)));
    AbsorptionSimulation simulation = new AbsorptionSimulation(surfaces, boundary, height, photon);
    simulation.run(1000000);
    System.err.println(System.currentTimeMillis() - start);

    simulation.printStats();
  }

}
