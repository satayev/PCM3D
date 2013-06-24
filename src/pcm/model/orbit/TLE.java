package pcm.model.orbit;

/**
 * This class encapsulates the standard 2-line orbital element set for a satellite.
 * 
 * @author Satayev
 */
public class TLE {

  private static final double TO_DEGREES = 180.0 / Math.PI;
  private static final double TO_RADIANS = Math.PI / 180.0;

  // LINE 0
  /** Satellite name */
  public String name;

  // LINE 1
  /** SSC# [1, 99999] */
  public int satID;
  /** Classification: U, C, S, or T */
  public char satClass;
  /** International designator */
  public String intDesig;
  /** epoch year [0, 99] */
  public int epochYear;
  /** Epoch day [0.0, 366.0] */
  public double epochDay;
  /** First Time Derivative of the Mean Motion */
  public double nDot;
  /** Second Time Derivative of Mean Motion */
  public double nDotDot;
  /** SGP4 drag term */
  public double bstar;
  /** Orbit model to use [1=SGP, 2=SGP4, 3=SDP4, 4=SGP8, 5=SDP8] */
  public int ephemerisType;
  /** Element number [0, 9999] */
  public int elementNum;

  // LINE 2
  /** Orbital inclination (radians) [0.0, 180.0] */
  public double inclination;
  /** Right ascension of the ascending node (radians) [0.0, 360.0] */
  public double rightAscension;
  /** Orbit shape [0.0, 1.0) */
  public double eccentricity;
  /** Argument of Perigee */
  public double argPerigee;
  /** Mean Anomaly (radians) */
  public double meanAnomaly;
  /** Revolutions per day */
  public double meanMotion;
  /** Revolution number at epoch [Revs] */
  public int revolutionsNumber;

  public TLE(TLE that) {
    this.name = that.name == null ? null : new String(that.name);
    this.argPerigee = that.argPerigee;
    this.bstar = that.bstar;
    this.eccentricity = that.eccentricity;
    this.elementNum = that.elementNum;
    this.ephemerisType = that.ephemerisType;
    this.epochDay = that.epochDay;
    this.epochYear = that.epochYear;
    this.inclination = that.inclination;
    this.intDesig = that.intDesig;
    this.meanAnomaly = that.meanAnomaly;
    this.meanMotion = that.meanMotion;
    this.nDot = that.nDot;
    this.nDotDot = that.nDotDot;
    this.revolutionsNumber = that.revolutionsNumber;
    this.rightAscension = that.rightAscension;
    this.satClass = that.satClass;
    this.satID = that.satID;
  }

  public TLE(String line1, String line2) {
    this(null, line1, line2);
  }

  public TLE(String name, String line1, String line2) {
    this.name = name;

    parseLine1(line1);
    parseLine2(line2);

    if (!line1.regionMatches(2, line2, 2, 5))
      throw new IllegalArgumentException("Space station numbers of two lines do not match");
  }

  /** Parses and validates the data fields and checksum, if available, on the first element set line. */
  public void parseLine1(String line) {
    if (line == null)
      throw new IllegalArgumentException("TLE's Line1: null");
    if (line.length() < 68)
      throw new IllegalArgumentException("TLE's Line1: requires 68 characters");
    if (line.charAt(0) != '1')
      throw new IllegalArgumentException("TLE's Line1: must start with number 1");

    satID = Integer.parseInt(line.substring(2, 7).replace(' ', '0'));
    if (satID < 1 || satID > 99999)
      throw new IllegalArgumentException("TLE's Line1: satellite number is out of range " + satID);

    satClass = line.charAt(7);
    if (satClass != 'U' && satClass != 'C' && satClass != 'S' && satClass != 'T')
      throw new IllegalArgumentException("TLE's Line1: classification eror " + satClass);

    intDesig = line.substring(9, 17);

    epochYear = Integer.parseInt(line.substring(18, 20));
    if (epochYear < 0 || epochYear > 99)
      throw new IllegalArgumentException("TLE's Line1: epoch year is out of range " + epochYear);

    epochDay = Double.parseDouble(line.substring(20, 32));
    if (epochDay < 0.0 || epochDay > 367.0)
      throw new IllegalArgumentException("TLE's Line1: epoch day is out of range " + epochDay);

    nDot = Double.valueOf(line.substring(33, 43));

    String temp;
    if (line.charAt(50) == ' ')
      temp = line.substring(44, 45) + "." + line.substring(45, 50) + "E+" + line.substring(51, 52);
    else
      temp = line.substring(44, 45) + "." + line.substring(45, 50) + "E" + line.substring(50, 52);
    nDotDot = Double.parseDouble(temp);

    bstar = Double.parseDouble(line.substring(53, 54) + "." + line.substring(54, 59) + "E" + line.substring(59, 61).replace(' ', '0'));

    ephemerisType = Integer.parseInt(line.substring(62, 63));
    // Only allow for SGP or SGP4 
    if (ephemerisType != 0 && ephemerisType != 2)
      throw new IllegalArgumentException("TLE's Line1: ephemeris type is out of range");

    elementNum = Integer.parseInt(line.substring(65, 68).replace(' ', '0'));
    if (elementNum < 0 || elementNum > 999)
      throw new IllegalArgumentException("TLE's Line1: element number is out of range " + elementNum);

    if (line.length() > 68 && line.charAt(68) != ' ' && checkSum(line) != line.charAt(68) - '0')
      throw new IllegalArgumentException("TLE's Line1: check sum error, expecting " + checkSum(line) + ", actual value " + line.charAt(68));
  }

  /** Parses and validates the data fields and checksum, if available, on the first element set line. */
  public void parseLine2(String line) {
    if (line == null)
      throw new IllegalArgumentException("TLE's Line2: null");
    if (line.length() < 68)
      throw new IllegalArgumentException("TLE's Line2: requires 68 characters");
    if (line.charAt(0) != '2')
      throw new IllegalArgumentException("TLE's Line2: must start with number 2");

    satID = Integer.parseInt(line.substring(2, 7).replace(' ', '0'));
    if (satID < 1 || satID > 99999)
      throw new IllegalArgumentException("TLE's Line2: satellite number is out of range " + satID);

    inclination = Double.parseDouble(line.substring(8, 17));
    if (inclination < 0.0 || inclination > 180.0)
      throw new IllegalArgumentException("TLE's Line2: inclination is out of range " + inclination);
    inclination = inclination * TO_RADIANS;

    rightAscension = Double.parseDouble(line.substring(17, 26));
    if (rightAscension < 0.0 || rightAscension > 360.0)
      throw new IllegalArgumentException("TLE's Line2: right acension is out of range " + rightAscension);
    rightAscension = rightAscension * TO_RADIANS;

    argPerigee = Double.parseDouble(line.substring(34, 42));
    if (argPerigee < 0.0 || argPerigee > 360.0)
      throw new IllegalArgumentException("TLE's Line2: argument of perigee is out of range " + argPerigee);
    argPerigee = argPerigee * TO_RADIANS;

    eccentricity = Double.parseDouble("0." + line.substring(26, 33));
    if (eccentricity < 0.0 || eccentricity >= 1.0)
      throw new IllegalArgumentException("TLE's Line2: eccentricity is out of range " + eccentricity);

    meanAnomaly = Double.parseDouble(line.substring(43, 51));
    if (meanAnomaly < 0.0 || meanAnomaly > 360.0)
      throw new IllegalArgumentException("TLE's Line2: mean anomaly is out of range " + meanAnomaly);
    meanAnomaly = meanAnomaly * TO_RADIANS;

    meanMotion = Double.parseDouble(line.substring(52, 63));
    if (meanMotion > 17.0)
      throw new IllegalArgumentException("TLE's Line2: mean motion is out of range " + meanMotion);

    revolutionsNumber = Integer.parseInt(line.substring(63, 68).replace(' ', '0'));
    if (revolutionsNumber < 0 || revolutionsNumber > 99999)
      throw new IllegalArgumentException("TLE's Line2: revolutions number is out of range " + revolutionsNumber);

    if (line.length() > 68 && line.charAt(68) != ' ' && checkSum(line) != line.charAt(68) - '0')
      throw new IllegalArgumentException("TLE's Line1: check sum error, expecting " + checkSum(line) + ", actual value " + line.charAt(68));
  }

  private int checkSum(String line) {
    int checksum = 0;
    for (int i = 0; i < 68; i++) {
      char c = line.charAt(i);
      if (c == '-')
        checksum++;
      else if ('0' <= c && c <= '9')
        checksum += c - '0';
    }
    return checksum % 10;
  }

  public double getPeriod() {
    return meanMotion > 0.0 ? 1440.0 / meanMotion : 0.0;
  }

  public double getArgPerigeeDegrees() {
    return argPerigee * TO_DEGREES;
  }

  public double getInclinationDegrees() {
    return inclination * TO_DEGREES;
  }

  public double getMeanAnomalyDegrees() {
    return meanAnomaly * TO_DEGREES;
  }

  public double getRightAscensionDegrees() {
    return rightAscension * TO_DEGREES;
  }
}