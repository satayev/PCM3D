package pcm;

import java.util.Random;

import pcm.model.RectangularPrismModel;

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
    AbsorptionSimulation simulation = new AbsorptionSimulation(new RectangularPrismModel(100, 100, 100));
    simulation.run(1000000);
    simulation.printStats();
    System.err.println(System.currentTimeMillis() - start);
  }
}
