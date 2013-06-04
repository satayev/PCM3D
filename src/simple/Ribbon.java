package simple;

import pcm.geom.Vector;

/**
 * This basic class represents a vertical surface with infinite height.
 * It handles potential collisions with photons.
 * 
 * @author John Stewart
 */
public class Ribbon extends Surface {
  public double rx, ry, nx, ny, b;
  public double c; // Tangent distance from origin
  public Vector[] corners=new Vector[2];

  /**
   * This is the basic constructor
   * 
   * @param x1 X-coordinate of first corner
   * @param y1 Y-coordinate of first corner
   * @param x2 X-coordinate of second corner
   * @param y2 Y-coordinate of second corner
   */
  public Ribbon(double x1, double y1, double x2, double y2) {
    this.rx = x1;
    this.ry = y1;
    double dx = x2 - x1, dy = y2 - y1;
    this.b = Math.sqrt(dx * dx + dy * dy);
    this.nx = dy / b;
    this.ny = -dx / b;
    c = rx * nx + ry * ny;
    
    corners[0]=new Vector(x1,y1);corners[1]=new Vector(x2,y2);
  }

  /**
   * Detailed Constructor
   * 
   * @param rx X-coordinate of first corner
   * @param ry Y-coordinate of first corner
   * @param nx X-coordinate of surface normal vector
   * @param ny Y-coordinate of surface normal vector
   * @param b Distance to second corner
   */
  public Ribbon(double rx, double ry, double nx, double ny, double b) {
    this.rx = rx;
    this.ry = ry;
    this.nx = nx;
    this.ny = ny;
    this.b = b;
    c = rx * nx + ry * ny;
  }

  /**
   * Determines if the photon will collide with the surface and the distance to collision
   * 
   * @param p The photon being checked
   * @return The distance to collision, or positive infinity if not applicable
   */
  public double collisionDistance(Photon p) {
    // nx * (p.rx + d*p.nx) + ny * (p.ry + d*p.ny) = c
    double d = (c - nx * p.r.x - ny * p.r.y) / (nx * p.n.x + ny * p.n.y);
    if (d < p.e)
      return Double.MAX_VALUE;
    // project onto the surface
    double r = nx * (p.r.y + d * p.n.y - ry) - ny * (p.r.x + d * p.n.x - rx);
    if (r < 0 || r > b)
      return Double.MAX_VALUE;
    return d;
  }

  /**
   * Moves the photon to the point of collision and reflects its velocity
   * 
   * @param p The photon to operate on
   * @return Returns true if the photon was absorbed
   */
  public boolean collision(Photon p) {
    // calculate distance, move photon to surface
    double d = (c - nx * p.r.x - ny * p.r.y) / (nx * p.n.x + ny * p.n.y);
    p.move(d);
    return !p.bounce(new Vector(nx, ny, 0.));
  }

  /**
   * If the surface is directly in the x direction from the photon, this function returns true
   * 
   * @param p The photon to check
   * @return Returns true if the surface is directly along the x direction from the photon
   */
  public boolean checkRight(Photon p) {
    double d = (c - nx * p.r.x - ny * p.r.y) / nx;
    if (d < 0)
      return false;
    // project onto the surface
    double r = nx * (p.r.y - ry) - ny * (p.r.x + d - rx);
    if (r < 0 || r > b)
      return false;
    return true;
  }

}
