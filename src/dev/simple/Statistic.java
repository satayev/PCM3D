package dev.simple;

import java.util.ArrayList;
import java.util.List;

import pcm.model.geom.V;
import pcm.model.geom.Vector;

public class Statistic {

  // The cooridinates for the photon's collisions
  public List<List<List<Vector>>> rv = new ArrayList<List<List<Vector>>>();
  // The cooridinates for the photon's absorbsions
  public List<Vector> xv = new ArrayList<Vector>();
  public int n, N = 1, x, X = 1;

  public void newPhoton(Vector v) {
    n++;
    if (rv.size() < N) {
      rv.add(new ArrayList<List<Vector>>());
      newBranch(v);
    }
  }

  public void newBranch(Vector v) {
    if (!rv.isEmpty() && rv.size() <= N) {
      rv.get(rv.size() - 1).add(new ArrayList<Vector>());
      addPath(v);
    }
  }

  public void addPath(Vector v) {
    if (!rv.isEmpty() && rv.size() <= N) {
      List<List<Vector>> rv1 = rv.get(rv.size() - 1);
      rv1.get(rv1.size() - 1).add(v.clone());
    }
  }

  public void absorb(Vector v) {
    x++;
    if (!xv.isEmpty() && xv.size() < X) {
      xv.add(v.clone());
    }
  }

  public void printAll() {
    System.out.println();
    System.out.println("Photon Paths:");
    for (List<List<Vector>> i : rv) {
      for (List<Vector> j : i) {
        for (Vector k : j)
          System.out.printf("%1.3f %1.3f %1.3f | ", k.x, k.y, k.z);
        System.out.println();
      }
      System.out.println();
    }
    System.out.println();
    System.out.println("Absorbsion Points:");
    for (Vector i : xv)
      System.out.printf("%1.3f %1.3f %1.3f | ", i.x, i.y, i.z);
    System.out.println();
    System.out.println();
    System.out.println("Ratio:");
    System.out.printf("%d:%d | %1.2f | ", x, n, ((double) x) / n);
  }

  public void print() {
    System.out.println("Ratio:");
    System.out.printf("%d:%d | %1.2f | ", x, n, ((double) x) / n);
  }
  
  public String getRatio() {
    return String.format("%1.2f", ((double) x) / n);
  }

  public void extendTail(Vector n0) {
    if (!rv.isEmpty() && rv.size() <= N) {
      List<List<Vector>> rv1 = rv.get(rv.size() - 1);
      List<Vector> rv2 = rv1.get(0);
      Vector a = rv2.get(0);
      rv2.add(0,V.sub(a,n0));
    }
  }
  
  public void extendHead(Vector n) {
    if (!rv.isEmpty() && rv.size() <= N) {
      List<List<Vector>> rv1 = rv.get(rv.size() - 1);
      List<Vector> rv2 = rv1.get(rv1.size()-1);
      Vector a = rv2.get(rv2.size()-1);
      rv2.add(V.add(a,n));
    }
  }

}
