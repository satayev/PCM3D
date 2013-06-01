package simple;

import pcm.geom.Vector3D;

public class Floor extends Surface {

	@Override
	public double collisionDistance(Photon p) {
		double d = -p.r.z / p.n.z;
		if (d < p.e) d = Double.POSITIVE_INFINITY;
		return d;
	}

	@Override
	public boolean collision(Photon p) {
		double d = collisionDistance(p);
		p.move(d);
		return !p.bounce(new Vector3D(0, 0, 1));
	}

}
