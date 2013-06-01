package simple;

import java.util.ArrayList;
import java.util.List;

import pcm.geom.Vector3D;

public class Statistic {
	
	// The cooridinates for the photon's collisions
	public List<List<Vector3D>> rv = new ArrayList<List<Vector3D>>(); 
	// The cooridinates for the photon's absorbsions
	public List<Vector3D> xv = new ArrayList<Vector3D>(); 
	public int n, N, x, X;
	
	public void newPhoton() {
		n++;
		if(n<N)
			rv.add(new ArrayList<Vector3D>());
	}
	
	public void addPath(Vector3D v) {
		if (n<N)
			rv.get(n-1).add(v.copy());
	}

	public void absorb(Vector3D v) {
		if (x<X)
			xv.get(x-1).add(v.copy());
	}

}
