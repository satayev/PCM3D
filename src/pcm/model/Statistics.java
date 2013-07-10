package pcm.model;

import java.util.ArrayList;
import java.util.List;

import pcm.model.geom.Vector;

public class Statistics {

  public int photonTotalCounter = 0;
  public int maxPhotonPaths = 0;
  public List<List<List<Vector>>> photonPaths = new ArrayList<List<List<Vector>>>();
  public int photonAbsorbedCounter = 0;
  public double photonPowerCounter = 0.;
  public int maxPhotonAbsorptionPoints = 0;
  public List<Vector> photonAbsorptionPoints = new ArrayList<Vector>();
  public int reflectionsTotalCounter = 0;

  public void update(Photon photon) {
    photonTotalCounter++;
    reflectionsTotalCounter += photon.reflectionCounter;
    if (photon.absorbed) {
      photonAbsorbedCounter++;
      photonPowerCounter += -photon.v0.z;
      if (photonAbsorptionPoints.size() < maxPhotonAbsorptionPoints) 
        photonAbsorptionPoints.add(photon.p);
    }
    if (photonPaths.size() < maxPhotonPaths) 
      photonPaths.add(photon.path);
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("# of photons:\t\t\t");
    sb.append(photonTotalCounter);
    sb.append("\n# of absorbed photons:\t\t");
    sb.append(photonAbsorbedCounter);
    sb.append("\nAverage reflections:\t\t");
    sb.append((double) reflectionsTotalCounter / photonTotalCounter);
    sb.append("\n% of absorbed photons:\t\t");
    sb.append((double) photonAbsorbedCounter / photonTotalCounter);
    return sb.toString();
  }

  public void setDetail(int details) {
    maxPhotonPaths = details;
    maxPhotonAbsorptionPoints = details;
  }
}
