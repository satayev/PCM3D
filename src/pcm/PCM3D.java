package pcm;

import java.util.Random;

import pcm.model.RectangularPrismModel;
import pcm.model.orbit.ISSOrbit;

/**
 * Entry-point class for the 3D Photovoltaic Computer Modeling program.
 * 
 * @author Satayev
 * @version 1.0.0.0
 */
public final class PCM3D {

  public static Random rnd = new Random(40);

  /**
   * @param args currently ignored
   */
  public static void main(String[] args) throws Exception {
    long start = System.currentTimeMillis();
    ISSOrbit orbit = new ISSOrbit();
    AbsorptionSimulation simulation = new AbsorptionSimulation(new RectangularPrismModel(100, 100, 100), orbit);
    simulation.run(1000000);
    simulation.printStats();
    System.err.println(System.currentTimeMillis() - start);
  }
}
