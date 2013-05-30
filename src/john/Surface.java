package john;

public abstract class Surface {

	/**
	 * Determines if the photon will collide with the surface and the distance to collision
	 * @param p The photon being checked
	 * @return The distance to collision, or positive infinity if not applicable
	 */
	public abstract double collisionDistance(Photon p);
	
	/**
	 * Moves the photon to the point of collision and reflects its velocity
	 * @param p The photon to operate on
	 * @return Returns true if the photon was absorbed
	 */
	public abstract boolean collision(Photon p);
}
