package pcm.model.statics;

public class Alpha {

  /**
   * Calculate alpha
   * @param wavelength The wavelength of the photon
   * @return Alpha, in units cm-1
   * Absorption Coefficient
   */
  public static double getAlpha(double wavelength) {
    return 4 * Math.PI * ExtinctionCoefficient.getK() / (wavelength *100);
  }

}
