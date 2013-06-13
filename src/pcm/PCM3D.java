package pcm;

import java.util.Random;

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
    AbsorptionSimulation simulation = new AbsorptionSimulation();
    simulation.run(1000);
    System.err.println(System.currentTimeMillis() - start);

    simulation.printStats();
  }

}
