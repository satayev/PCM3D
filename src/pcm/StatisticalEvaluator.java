package pcm;

import java.util.ArrayList;
import java.util.List;

import pcm.model.RectangularPrismModel;
import pcm.model.Statistics;
import pcm.model.geom.V;
import pcm.model.geom.Vector;
import pcm.model.geom.curves.Polygon;
import pcm.model.geom.solids.Prism;
import pcm.model.orbit.ISSOrbit;
import pcm.model.statics.Alpha;
import pcm.model.statics.Wavelength;
import pcm.model.statics.WavelengthAM0;

public class StatisticalEvaluator {
  
  public RectangularPrismModel model = new RectangularPrismModel(1., 1., 1.);
  
  public List<Vector> vectors;
  
  public List<Statistics> points;

  public List<Double> wavelengthValue;
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
      model.cnts.add(new Prism(new Vector(), new Polygon(i.toArray(new Vector[i.size()]))));
  }
  
  /**
   * Method runs all simulations and stores data
   * @param vectors The directions of the incoming sunlight
   * @param wavelength The model for generating each photons wavelength
   * @param details The memory for each photon, such as the paths
   * @param precision The number of trials to run
   */
  public void runSimulation(List<Vector> vectors, Wavelength wavelength, int details, int precision) {
    this.vectors = vectors;
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
    wavelengthValue = new ArrayList<Double>(steps);
    wavelengthIntensity = new ArrayList<Double>(steps);
    wavelengthAlpha = new ArrayList<Double>(steps);
    int[] wavelengthCount = new int[steps];
    double stepSize = maxWavelength / steps;
    for (int i = 0; i < steps; i++) wavelengthValue.add((i+1) * stepSize);
    for (int i = 0; i < detail;) {
      double w = wavelength.genWavelength();
      int w0 = (int) (w / stepSize) - 1;
      if (w0 >= 0 && w0 < steps) {
        wavelengthCount[w0]++;
        i++;
      }
    }
    for (int i = 0; i < steps; i++) wavelengthIntensity.add(((double) wavelengthCount[i]) / detail); 
    for (int i = 0; i < steps; i++) wavelengthAlpha.add(Alpha.getAlpha(wavelengthValue.get(i))); 
  }
  
  public List<Vector> generateVectors(double zenith, double azimuth, double angle, int precision) {
    Vector entry = angleToVector(zenith, azimuth);
    return generateVectors(entry, angle, precision);
  }
  
  public List<Vector> generateVectors(Vector entry, double angle, int size) {
    Vector rotation = generateRotationVector(entry, angle);
    double degree = 2*Math.PI / size;
    return generateVectors(entry, rotation, degree, size);
  }
  
  public List<Vector> generateVectors(Vector entry, Vector rotation, double angle, int size) {
    List<Vector> list = new ArrayList<Vector>();
    while (size-- > 0) {
      list.add(entry);
      entry = V.rotateAbout(entry, rotation, angle);
    }
    return list;
  }
  
  public Vector angleToVector(double zenith, double azimuth) {
    double zenith0 = Math.PI * zenith / 180, 
        azimuth0 = Math.PI * azimuth / 180 + Math.PI;
    return new Vector(Math.cos(azimuth0) * Math.sin(zenith0), Math.sin(azimuth0) * Math.sin(zenith0), -Math.cos(zenith0));
  }
  
  public double vectorToZenith(Vector vector) {
    if (vector == null) return Double.NaN;
    return Math.acos(vector.z) * 180 / Math.PI;
  }
  
  public double vectorToAzimuth(Vector vector) {
    if (vector == null) return Double.NaN;
    Vector v0 = vector.clone();
    v0.z = 0;
    v0.normalize();
    return Math.acos(v0.x) * 180 / Math.PI;
  }

  public Vector generateRotationVector(Vector entry, double angle) {
    Vector y = V.cross(entry, new Vector(0,0,1)), x = V.cross(y, entry);
    y.normalize();
    x.normalize();
    double angle0 = Math.PI*angle / 180;
    Vector rotation = V.add(V.mult(Math.cos(angle0), x), V.mult(Math.sin(angle0), y));
    rotation = V.cross(rotation, entry);
    rotation.normalize();
    return rotation;
  }
  
}
