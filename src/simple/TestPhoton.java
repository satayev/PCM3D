package simple;

public class TestPhoton extends Photon {
	
	/**
	 * This method overrides photon with an exact placement
	 * The photon starts at the top with a downward trajectory
	 */
	public void reset() {
//		r.x = .65;
//		r.y = .95;
//		r.z = 1;
//		n.x = -.2;
//		n.y = -.1;
//		n.z = -.2;
		r.x = .50;
		r.y = .90;
		r.z = 1;
		n.x = 2;
		n.y = 0;
		n.z = -1;
		n.normalize();
		stat.newPhoton(r);
	}
	


	/**
	 * This method fixes the absorption chance
	 * @return false : not absorbed
	 */
	public boolean absorpsionChance() {
		return false;
	}
	
}
