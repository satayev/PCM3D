package pcm.model;

public class Statistics {

  public int photonTotalCounter = 0;
  public int photonAbsorbedCounter = 0;
  public int reflectionsTotalCounter = 0;

  public void update(Photon photon) {
    photonTotalCounter++;
    reflectionsTotalCounter += photon.reflectionCounter;
    if (photon.absorbed)
      photonAbsorbedCounter++;
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
}
