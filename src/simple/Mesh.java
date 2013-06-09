package simple;

import java.util.ArrayList;
import java.util.List;

import pcm.geom.V;
import pcm.geom.Vector;

/**
 * This class models a three dimensional mesh model, generated with a set of points and using convex hull
 * @author John Stewart
 */
public class Mesh extends Solid {
	List<Triangle> surfaces = new ArrayList<Triangle>();
	
	public Mesh(List<Vector> LV) {
		Vector center = V.mean(LV);
		int n = LV.size();
		for (int i=0; i<n-2; i++) for (int j=i; j<n-1; j++) for (int k=j; k<n; k++) 
			surfaces.add(new Triangle(LV.get(i),LV.get(j),LV.get(k),center));
		// remove internal surfaces
		Photon p = new Photon();
		double L = Math.sqrt(p.X*p.X+p.Y*p.Y+p.Z*p.Z);
		for (int i=0; i<surfaces.size(); i++) {
			Triangle a = surfaces.get(i);
			p.move(V.add(V.add(a.p1,a.p2),a.p3).multiple(1./3));
			p.n = a.norm.clone();
			p.move(-L);
			double min = Double.POSITIVE_INFINITY;
			int index = -1;
			for (int j=0; j<surfaces.size(); j++)  {
				double d = surfaces.get(j).collisionDistance(p);
				if (d < Double.POSITIVE_INFINITY) {
					if (index < 0) {
						min = d;
						index = j;
					} else {
						int removed = j;
						if (d < min) {
							min = d;
							removed = index;
							index = j;
						} 
						surfaces.remove(removed);
						if (removed <= i) i--;
						if (removed <= j) j--;
					}
				} 
			}
		}
			
	}
	
}
