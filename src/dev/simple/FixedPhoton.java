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
    
    if (degrees != -1) { // Temporary orbit interfacing with AppletModel
      double radius=1;
      double x=radius*Math.random()-radius*.5,y=radius*Math.random()-radius*.5;
      r.x = 1-degrees/180 + x; // 1-t so photon rises from east to west, y=1 to y=0
      r.y = .5 + y;
      r.z = Math.sin((1-degrees/180) * Math.PI);
      
      Vector tileCenter = new Vector(.5 + x,.5+ y,0);
      tileCenter.sub(r);
      tileCenter.normalize();
      n = tileCenter.clone();
      n0 = n.clone();
      
      r.sub(V.mult(3, n)); // start the photon beyond the model
      r0 = r.clone();
    }
    else {
      n = n0.clone();
      r.x = X * PCM3D.rnd.nextDouble();
      r.y = Y * PCM3D.rnd.nextDouble();
      r.z = Z;
      r0 = r.clone();
    }
    
    f = genFreq();
    w = 299792458/f;
    E = 1.986e-25/w;
    stat.newPhoton(r);
  }
  
}
