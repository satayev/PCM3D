package pcm.model.orbit;

import pcm.model.geom.Vector;

public class ISSOrbit {

  // these are from SGP4 Gravity constants
  private final static double radiusearthkm = 6378.135;
  private final static double vkmpersec = radiusearthkm * 0.0743669161331734132 / 60.0;

  public final static TLE tle = new TLE(
      "ISS (ZARYA)",
      "1 25544U 98067A   13174.89292319  .00010263  00000-0  18508-3 0   582",
      "2 25544  51.6496  75.1008 0009027  90.3531   1.0124 15.50356433835743"
  );

  public static double step = 30; // minutes
  public static Sgp4Unit sgp4 = new Sgp4Unit();

  public static Vector getISSPosition() {
    // ArrayList<Sgp4Data> results = sgp4.runSgp4(card1, card2, startYr, startDay, stopYr, stopDay, step);
    Sgp4Data data = sgp4.runSgp4(tle.epochYear, tle.epochDay);
    Vector p = data.pos;
    Vector v = data.vel;
    p.mult(radiusearthkm);
    v.mult(vkmpersec);
    return p;
  }

}
