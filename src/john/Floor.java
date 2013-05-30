package john;

public class Floor extends Surface {

	@Override
	public double collisionDistance(Photon p) {
		double d = -p.rz / p.nz;
		if (d < p.e) d = Double.POSITIVE_INFINITY;
		return d;
	}

	@Override
	public boolean collision(Photon p) {
		double d = collisionDistance(p);
		p.move(d);
		return !p.bounce(0, 0, 1);
	}

}
