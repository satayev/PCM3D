package pcm;

import java.util.ArrayList;
import java.util.List;

import pcm.model.RectangularPrismModel;
import pcm.model.Statistics;
import pcm.model.geom.Vector;
import pcm.model.geom.curves.Polygon;
import pcm.model.geom.solids.Prism;
import pcm.model.orbit.ISSOrbit;
import pcm.model.statics.Alpha;
import pcm.model.statics.Wavelength;

public class StatisticalEvaluator {
  
  public RectangularPrismModel model = new RectangularPrismModel(1., 1., 1.);
  
  public List<Statistics> points;

  public List<Double> wavelengthIntensity;
  public List<Double> wavelengthAlpha;
  
  /**
   * Parameter for creating a new model
   * @param X The X-bound
   * @param Y The Y-bound
   * @param Z The Z-bound, and the height of the towers
   * @param list The list of the polygons, which is a list of the vectors
   */
  public void AssignModel(double X, double Y, double Z, List<List<Vector>> list) {
    model = new RectangularPrismModel(X,Y,Z);
    for (List<Vector> i : list)
      model.cnts.add(new Prism(new Vector(), new Polygon((Vector[]) i.toArray())));
  }
  
  /**
   * Method runs all simulations and stores data
   * @param vectors The directions of the incoming sunlight
   * @param wavelength The model for generating each photons wavelength
   * @param details The memory for each photon, such as the paths
   * @param precision The number of trials to run
   */
  public void runSimulation(List<Vector> vectors, Wavelength wavelength, int details, int precision) {
    if (precision < details) precision = details;
    int N = vectors.size();
    points = new ArrayList<Statistics>(N);
    for (int i = 0; i < N; i++) {
      AbsorptionSimulation alpha = new AbsorptionSimulation(model, wavelength);
      alpha.stats.setDetail(details);
      try {
        alpha.run(precision, vectors.get(i));
      } catch (Exception e) {
        e.printStackTrace();
      }
      points.add(alpha.stats);
    }
  }
  
  /**
   * This method analyzes the form of the wavelength
   * @param wavelength The wavelength distribution to analyze
   * @param steps The number of data points to take
   * @param maxWavelength The upper bound on the range, lower bound is zero
   * @param detail The number of trials to run for the intensity measurement
   */
  public void analyzeWavelength(Wavelength wavelength, int steps, double maxWavelength, double detail) {
    wavelengthIntensity = new ArrayList<Double>(steps);
    wavelengthAlpha = new ArrayList<Double>(steps);
    int[] wavelengthCount = new int[steps];
    double stepSize = maxWavelength / steps;
    for (int i = 0; i < detail;) {
      double w = wavelength.genWavelength();
      int w0 = (int) (w / stepSize) - 1;
      if (w0 >= 0 && w0 < steps) {
        wavelengthCount[w0]++;
        i++;
      }
    }
    for (int i = 0; i < detail; i++) wavelengthIntensity.add((int) wavelengthCount[i] / detail); 
    for (int i = 0; i < detail; i++) wavelengthAlpha.add(Alpha.getAlpha((i+1) * stepSize)); 
  }
  
}
