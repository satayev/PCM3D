package simple;

import pcm.geom.Vector3D;

public class Walls extends Surface {

	@Override
	public double collisionDistance(Photon p) {
		double d = Double.POSITIVE_INFINITY;
		double c = -p.r.x / p.n.x;
		if (c > p.e && c < d) d = c;
		c = -p.r.y / p.n.y;
		if (c > p.e && c < d) d = c;
		c = (Photon.X-p.r.x) / p.n.x;
		if (c > p.e && c < d) d = c;
		c = (Photon.Y-p.r.y) / p.n.y;
		if (c > p.e && c < d) d = c;
		return d;
	}

	@Override
	public boolean collision(Photon p) {
		double d = collisionDistance(p);
		p.move(d);
		if (p.r.x < p.e || Photon.X - p.r.x < p.e) p.move(new Vector3D(Photon.X - p.r.x,p.r.y,p.r.z));
		if (p.r.y < p.e || Photon.Y - p.r.y < p.e) p.move(new Vector3D(p.r.x,Photon.Y - p.r.y,p.r.z));
		return false;
	}

}
