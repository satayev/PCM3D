package pcm.model.statics;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import pcm.PCM3D;

public class WavelengthAM0 extends Wavelength {
  
  public static double deltafAM0 = .001, maxfAM0 = 3;
  public static TreeMap<Double,Double> blackBodyAM0 = null;
  
  public double genFreq() {
    if (blackBodyAM0 == null) 
      init();
    double p = PCM3D.rnd.nextDouble();
    double p1 = blackBodyAM0.floorKey(p), p2 = blackBodyAM0.ceilingKey(p);
    double f1 = blackBodyAM0.get(p1), f2 = blackBodyAM0.get(p2);
    double f = f1+(p-p1)*(f2-f1)/(p2-p1); 
    return f*1e15;
  }

  public double genWavelength() {
    return 2.998e8 / genFreq();
  }

  private static void init() {
    blackBodyAM0 = new TreeMap<Double,Double>();
    List<Double> I = new ArrayList<Double>((int) (maxfAM0 / deltafAM0));
    I.add(1.474e-5 * deltafAM0 * deltafAM0 * deltafAM0 / (Math.exp(6.826 * deltafAM0) - 1));
    for (double f = 2 * deltafAM0; f <= maxfAM0; f += deltafAM0)
      I.add(1.474e-5 * f * f * f / (Math.exp(6.826 * f) - 1) + I.get(I.size() - 1));
    for (int i = 0; i < I.size(); i++)
      I.set(i, I.get(i) / I.get(I.size() - 1));
    for (int i = 0; i < I.size(); i++) 
      blackBodyAM0.put(I.get(i), (i + 1) * deltafAM0);
  }
  
}
