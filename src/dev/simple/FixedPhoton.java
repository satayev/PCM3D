package dev.simple;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import pcm.PCM3D;
import pcm.model.geom.V;
import pcm.model.geom.Vector;

/**
 * This class restrains the photons to a specified entry vector.  
 * @author John Stewart
 */
public class FixedPhoton extends Photon {

  public FixedPhoton(Vector n0) {
    this.n0 = n0;
    if (blackBody == null) initialize();
  }

  public void reset() {
    n = n0.clone();
    r.x = X * PCM3D.rnd.nextDouble();
    r.y = Y * PCM3D.rnd.nextDouble();
    r.z = Z;
    r0 = r.clone();
    
    f = genFreq();
    w = 299792458/f;
    E = 1.986e-25/w;
    
    stat.newPhoton(r);
    stat.extendTail(n0);
  }

}
