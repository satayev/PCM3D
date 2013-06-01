package simple;

import java.util.ArrayList;
import java.util.List;

/**
 * Controls the simulation
 * @author John Stewart
 */
public class SimpleModel {
	
	public List<Tower> LT = new ArrayList<Tower>();
	public List<Surface> LS = new ArrayList<Surface>();
	public Photon p;
	
	/**
	 * Basic constructor
	 * @param LT The list of the towers
	 * @param p The photon to use
	 */
	public SimpleModel(List<Tower> LT, Photon p) {
		this.LT.addAll(LT);
		for (Tower i : LT) LS.addAll(i.LS);
		this.p = p;
		LS.add(new Walls());
		LS.add(new Floor());
		LS.add(new Ceiling());
	}
	
	public void run(int n) {
		Photon p = new Photon();
		for (int i = 0; i < n; i++) {
			p.reset();
			boolean end = false;
			for (Tower j : LT) if (j.isInside(p)){
				// collision
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
				if (l >= 0) if (LS.get(l).collision(p)) end = true;
			}
		}
	}
	
}
