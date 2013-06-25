package pcm.model.orbit;

import java.util.Calendar;
import java.util.GregorianCalendar;

import pcm.model.Photon;
import pcm.model.geom.V;
import pcm.model.geom.Vector;
import pcm.model.geom.solids.Sphere;

public class ISSOrbit {

  public static Vector rotate(Vector p, Vector A, Vector dx, double theta) {
    Vector q = new Vector(0, 0, 0);
    dx.normalize();
    double x = p.x, y = p.y, z = p.z;
    double a = A.x, b = A.y, c = A.z;
    double u = dx.x, v = dx.y, w = dx.z;
    q.x = (a * (v * v + w * w) - u * (b * v + c * w - u * x - v * y - w * z)) * (1 - Math.cos(theta)) + x * Math.cos(theta)
        + (-c * v + b * w - w * y + v * z) * Math.sin(theta);
    q.y = (b * (u * u + w * w) - v * (a * u + c * w - u * x - v * y - w * z)) * (1 - Math.cos(theta)) + y * Math.cos(theta)
        + (c * u - a * w + w * x - u * z) * Math.sin(theta);
    q.z = (c * (u * u + v * v) - w * (a * u + b * v - u * x - v * y - w * z)) * (1 - Math.cos(theta)) + z * Math.cos(theta)
        + (-b * u + a * v - v * x + u * y) * Math.sin(theta);
    return q;
  }

  public static void main(String[] args) {
    Sphere earth = new Sphere(new Vector(), 6378.135);
    int orbitTotalMinutes = 93;
    int step = 1;
    int count = 0;
    double delta = 365.25 * 1440 / 4 * 0;
    for (double minutesSince = 0; minutesSince < orbitTotalMinutes; minutesSince += step) {
      Vector iss = getISSPosition(minutesSince + delta);
      Vector sun = getSunPosition(minutesSince + delta);

      Vector v = V.sub(iss, sun);
      //      System.out.print(V.normalize(v));

      Vector n = V.normalize(iss);

      Vector sunlight = rotate(v, new Vector(), new Vector(n.x, n.y, n.z + 1), Math.PI);

      double r = v.length();
      double theta = Math.acos(sunlight.z / r);
      double phi = Math.atan2(sunlight.y, sunlight.x);
      System.out.println(V.normalize(sunlight));
      System.out.printf("%.5f %.5f %.5f ", r, theta * 180 / Math.PI, phi * 180 / Math.PI);

      Photon p = new Photon(sun, v);
      if (earth.getHit(p) != null) {
        System.out.println(" light");
        count++;
      } else {
        System.out.println(" shadow");
      }
    }
    System.out.printf("%d out of %d minutes are in shadow.\n", orbitTotalMinutes - count, orbitTotalMinutes);
  }

  // these are from SGP4 Gravity constants
  private final static double radiusearthkm = 6378.135;
  private final static double vkmpersec = radiusearthkm * 0.0743669161331734132 / 60.0;

  public final static TLE tle = new TLE("ISS (ZARYA)", "1 25544U 98067A   13174.89292319  .00010263  00000-0  18508-3 0   582",
      "2 25544  51.6496  75.1008 0009027  90.3531   1.0124 15.50356433835743");

  public static double step = 30; // minutes
  public static Sgp4Unit sgp4 = new Sgp4Unit(tle);;

  public static Vector getISSPosition(double minutesSince) {
    // NORAD Spacetrack Report #3
    Sgp4Data data = sgp4.runSgp4(tle.epochYear, tle.epochDay, minutesSince);
    Vector p = data.pos;
    Vector v = data.vel;
    p.mult(radiusearthkm);
    v.mult(vkmpersec);
    return p;
  }

  public static Vector getSunPosition(double minutesSince) {
    // http://en.wikipedia.org/wiki/Position_of_the_Sun#Rectangular_equatorial_coordinates
    // http://www.fourmilab.ch/cgi-bin/Solar/action?sys=-Sf
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

    //    6:15 PM, PDT on March 22, 2009
    double jd = sgp4.julianday(year, calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), hrs, min, dsecs);
    double n = jd - 2451545.0 + minutesSince / 1440;
    double L = 280.460 + 0.9856474 * n;
    double g = 357.528 + 0.9856003 * n;

    L = L * Math.PI / 180;
    g = g * Math.PI / 180;
    L = sgp4.modfunc(L, Sgp4Unit.twopi);
    g = sgp4.modfunc(g, Sgp4Unit.twopi);

    double lambda = L + 1.915 * Math.sin(g) + 0.020 * Math.sin(2 * g);

    double R = 1.00014 - 0.01671 * Math.cos(g) - 0.00014 * Math.cos(2 * g);
    R *= 149597870.700 / 1000;

    double e = 23.439 - 0.0000004 * n;

    Vector pos = new Vector();
    pos.x = R * Math.cos(lambda);
    pos.y = R * Math.cos(e) * Math.sin(lambda);
    pos.z = R * Math.sin(e) * Math.sin(lambda);

    return pos;
  }

}
