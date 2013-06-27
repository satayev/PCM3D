package dev.simple;

import java.util.ArrayList;
import java.util.List;

import pcm.model.geom.Vector;

/**
 * Controls the simulation
 * 
 * @author John Stewart
 */
public class SimpleModel {

  public List<Tower> LT = new ArrayList<Tower>();
  public List<Surface> LS = new ArrayList<Surface>();
  public Photon p;

  /**
   * Basic constructor
   * 
   * @param LT The list of the towers
   * @param p The photon to use
   */
  public SimpleModel(List<Tower> LT, Photon p) {
    this.LT.addAll(LT);
    for (Tower i : LT)
      LS.addAll(i.LS);
    this.p = p;
    LS.add(new Walls());
    LS.add(new Floor());
    LS.add(new Ceiling());
  }

  public void run(int n) {
    //System.out.println("LS.size() : "+LS.size());
    for (int i = 0; i < n; i++) {
      p.reset();
      //System.out.println("p.r : "+p.r.x+" "+p.r.y+" "+p.r.z);
      //System.out.println("p.n : "+p.n.x+" "+p.n.y+" "+p.n.z);
      boolean end = false;
      for (Tower j : LT)
        if (j.isInside(p)) {
          if (!p.bounce(new Vector(0, 0, 1))) 
            p.stat.extendHead(p.n);
          
          end = true;
        }
      while (!end) {
        double d = Double.POSITIVE_INFINITY;
        int l = -1;
        for (int k = 0; k < LS.size(); k++) {
          double c = LS.get(k).collisionDistance(p);
          if (c < d) {
            d = c;
            l = k;
          }
        }
        //System.out.println("l : "+l);
        if (l < 0 || LS.get(l).collision(p))
          end = true;
        //System.out.println("p.r : "+p.r.x+" "+p.r.y+" "+p.r.z);
        //System.out.println("p.n : "+p.n.x+" "+p.n.y+" "+p.n.z);
      }
    }
    //System.out.println();
  }

}
