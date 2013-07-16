package dev.simple;

import java.util.List;

import pcm.model.geom.Vector;

public class SimpleFixedModel extends SimpleModel {
  
  double theta = 0;
  public FixedPhoton p0;
  
  /*
   * Warning: Do not use
   */
  public SimpleFixedModel(List<Tower> LT, Photon p) {
    super(LT, p);
    this.p0 = (FixedPhoton) p;
  }
  
  public SimpleFixedModel(double X, double Y, double Z, double theta, List<Tower> LT, FixedPhoton p) {
    super(LT, p);
    p.X = X;
    p.Y = Y;
    p.Z = Z;
    this.theta = theta;
    this.p0 = p;
  }
  
  public void setEntry(Vector n0) {
    n0.rotate(theta);
    p0.n0 = n0;
  }

}
