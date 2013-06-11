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
    return "# of photons:\t\t\t" + photonTotalCounter + "\n# of absorbed photons:\t\t" + photonAbsorbedCounter + "\nAverage reflections:\t\t"
        + (double) reflectionsTotalCounter / photonTotalCounter;
  }
}
