package pcm.model.statics;

import pcm.PCM3D;

public class ProbabilityOfAbsorption {
  
  public static boolean test(double wavelength) {
    return PCM3D.rnd.nextDouble() < prob(wavelength);
  }
  
  public static double prob(double wavelength) {
    return Alpha.getAlpha(wavelength) * DepthOfMaterial.getDepth();
  }
  
}
