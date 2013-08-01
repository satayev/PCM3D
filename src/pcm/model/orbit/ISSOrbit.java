package pcm.model.orbit;

import java.util.Calendar;
import java.util.GregorianCalendar;

import pcm.model.Photon;
import pcm.model.geom.V;
import pcm.model.geom.Vector;
import pcm.model.geom.solids.Sphere;

public class ISSOrbit {

  // these are from SGP4 Gravity constants
  private final static double RADIUS_EARTH_KM = 6378.135;
  private final static double SPEED_KMperSEC = RADIUS_EARTH_KM * 0.0743669161331734132 / 60.0;
  private static final double TO_RADIANS = Math.PI / 180;
  private static final double ASTRONOMICAL_UNIT = 149597870.700;

  private final static Sphere Earth = new Sphere(new Vector(), RADIUS_EARTH_KM);

  public TLE tle;
  public Sgp4Unit sgp4;

  public ISSOrbit() {
    tle = new TLE("ISS (ZARYA)", "1 25544U 98067A   13174.89292319  .00010263  00000-0  18508-3 0   582",
        "2 25544  51.6496  75.1008 0009027  90.3531   1.0124 15.50356433835743");
    initSgp4();
  }

  public ISSOrbit(TLE tle) {
    this.tle = tle;
    initSgp4();
  }

  public ISSOrbit(String name, String line1, String line2) {
    this.tle = new TLE(name, line1, line2);
    initSgp4();
  }

  private void initSgp4() {
    sgp4 = new Sgp4Unit(tle);
  }

  public Vector getSunlightDirection(double minutesSince) {
    Vector iss = getISSPosition(minutesSince);
    Vector sun = getSunPosition(minutesSince);

    // since ISS is closer to Earth, check if photons from ISS reach Sun
    Vector v = V.normalize(V.sub(sun, iss));
    Photon p = new Photon(iss, v);
    if (Earth.getHit(p) != null)
      return null; // in shadow

    // --- find photons direction onto the solar panel ---

    // assume the panel is always tangential to the surface
    Vector normal = V.normalize(iss);
    // rotate vectors, such that the panel's normal is (0,0,1)
    Vector sunlight = V.rotate(v, new Vector(), new Vector(normal.x, normal.y, normal.z + 1), Math.PI);
    // photons are coming from Sun to ISS
    sunlight.mult(-1);
    sunlight.normalize();
    return sunlight;
  }

  public Vector getISSPosition(double minutesSince) {
    // NORAD Spacetrack Report #3
    Sgp4Data data = sgp4.runSgp4(tle.epochYear, tle.epochDay, minutesSince);
    Vector p = data.pos;
    Vector v = data.vel;
    p.mult(RADIUS_EARTH_KM);
    v.mult(SPEED_KMperSEC);
    return p;
  }
  
  public Vector getISSVelocity(double minutesSince) {
    // NORAD Spacetrack Report #3
    Sgp4Data data = sgp4.runSgp4(tle.epochYear, tle.epochDay, minutesSince);
    Vector p = data.pos;
    Vector v = data.vel;
    p.mult(RADIUS_EARTH_KM);
    v.mult(SPEED_KMperSEC);
    return v;
  }

  private Vector getSunPosition(double minutesSince) {
    int year;
    if (sgp4.satrec.epochyr < 50)
      year = sgp4.satrec.epochyr + 2000;
    else
      year = sgp4.satrec.epochyr + 1900;

    int jDays = (int) Math.floor(sgp4.satrec.epochdays);
    double remainder = (sgp4.satrec.epochdays - jDays);
    int hrs = (int) Math.floor(remainder * 24.0);
    remainder = remainder * 24.0 - (double) hrs;
    int min = (int) Math.floor(remainder * 60.0);
    remainder = remainder * 60.0 - (double) min;
    double dsecs = remainder * 60.0;

    GregorianCalendar calendar = new GregorianCalendar(Sgp4Unit.GMT_TZ);
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.DAY_OF_YEAR, jDays);
    calendar.set(Calendar.HOUR_OF_DAY, hrs);
    calendar.set(Calendar.MINUTE, min);

    double jd = sgp4.julianday(year, calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), hrs, min, dsecs);
    // TODO(satayev): should delta minutes be added to epochdays instead?
    double n = jd - 2451545.0 + minutesSince / 1440;
    double L = 280.460 + 0.9856474 * n;
    double g = 357.528 + 0.9856003 * n;

    L = L * TO_RADIANS;
    g = g * TO_RADIANS;
    L = sgp4.modfunc(L, Sgp4Unit.twopi);
    g = sgp4.modfunc(g, Sgp4Unit.twopi);

    double lambda = L + 1.915 * Math.sin(g) + 0.020 * Math.sin(2 * g);

    double R = 1.00014 - 0.01671 * Math.cos(g) - 0.00014 * Math.cos(2 * g);
    R *= ASTRONOMICAL_UNIT;

    double e = 23.439 - 0.0000004 * n;
    e = e * TO_RADIANS;

    Vector pos = new Vector();
    pos.x = R * Math.cos(lambda);
    pos.y = R * Math.cos(e) * Math.sin(lambda);
    pos.z = R * Math.sin(e) * Math.sin(lambda);

    return pos;
  }

}
