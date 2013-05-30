package john;

public class Walls extends Surface {

	@Override
	public double collisionDistance(Photon p) {
		double d = Double.POSITIVE_INFINITY;
		double c = -p.rx / p.nx;
		if (c > p.e && c < d) d = c;
		c = -p.ry / p.ny;
		if (c > p.e && c < d) d = c;
		c = (Photon.X-p.rx) / p.nx;
		if (c > p.e && c < d) d = c;
		c = (Photon.Y-p.ry) / p.ny;
		if (c > p.e && c < d) d = c;
		return d;
	}

	@Override
	public boolean collision(Photon p) {
		double d = collisionDistance(p);
		p.move(d);
		if (p.rx < p.e || Photon.X - p.rx < p.e) p.move(Photon.X - p.rx,p.ry,p.rz);
		if (p.ry < p.e || Photon.Y - p.ry < p.e) p.move(p.rx,Photon.Y - p.ry,p.rz);
		return false;
	}

}
