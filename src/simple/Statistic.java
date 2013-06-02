package simple;

import java.util.ArrayList;
import java.util.List;

import pcm.geom.Vector;

public class Statistic {

  // The cooridinates for the photon's collisions
  public List<List<Vector>> rv = new ArrayList<List<Vector>>();
  // The cooridinates for the photon's absorbsions
  public List<Vector> xv = new ArrayList<Vector>();
  public int n, N, x, X;

  public void newPhoton() {
    n++;
    if (n < N)
      rv.add(new ArrayList<Vector>());
  }

  public void addPath(Vector v) {
    if (n < N)
      rv.get(n - 1).add(v.clone());
  }

  public void absorb(Vector v) {
    if (x < X)
      xv.get(x - 1).add(v.clone());
  }

}
