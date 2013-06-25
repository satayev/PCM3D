package pcm.model;

import java.util.ArrayList;

import pcm.model.geom.Plane;
import pcm.model.geom.Vector;
import pcm.model.geom.Wall;
import pcm.model.geom.curves.Polygon;
import pcm.model.geom.solids.Prism;
import pcm.model.geom.solids.Solid;

/**
 * @author Satayev
 */
public class RectangularPrismModel extends Model {

  public RectangularPrismModel(int height, int width, int length) {
    this.X = length;
    this.Y = width;
    this.Z = height;

    cnts = new ArrayList<Solid>();

    cnts.add(new Prism(new Vector(), new Polygon(new Vector[] { new Vector(-25, -25, 0), new Vector(-25, 25, 0), new Vector(25, 25, 0),
        new Vector(25, -25, 0) })));

    // TODO(satayev): fix Circle curve;
    //    cnts.add(new Sphere(new Vector(0, 0, 0), height / 3));
    //    cnts.add(new Prism(new Vector(), new Circle(50)));
    //    cnts.add(new Cylinder(new Vector(), 50));

    //    double C0 = 25;
    //    Vector[] rectV = new Vector[] { new Vector(-C0, -C0, 0), new Vector(-C0, C0, 0), new Vector(C0, C0, 0), new Vector(C0, -C0, 0),
    //        new Vector(-C0, -C0, 100), new Vector(-C0, C0, 100), new Vector(C0, C0, 100), new Vector(C0, -C0, 100) };
    //    PolygonMesh mesh = new PolygonMesh(new Vector(), rectV, new int[][] { new int[] { 4, 5, 6, 7 }, new int[] { 4, 7, 3, 0 },
    //        new int[] { 5, 1, 2, 6 }, new int[] { 4, 0, 1, 5 }, new int[] { 7, 6, 2, 3 } });
    //    mesh.topFaceId = 0;
    //    cnts.add(mesh);

    bounds = new ArrayList<Plane>(6);
    bounds.add(new Wall(new Vector(length / 2, 0, 0), new Vector(-1, 0, 0), new Vector(-length, 0, 0)));
    bounds.add(new Wall(new Vector(-length / 2, 0, 0), new Vector(1, 0, 0), new Vector(length, 0, 0)));
    bounds.add(new Wall(new Vector(0, width / 2, 0), new Vector(0, -1, 0), new Vector(0, -width, 0)));
    bounds.add(new Wall(new Vector(0, -width / 2, 0), new Vector(0, 1, 0), new Vector(0, width, 0)));

    floor = new Plane(new Vector(0, 0, 0), new Vector(0, 0, 1));
    bounds.add(floor);
    ceiling = new Plane(new Vector(0, 0, height), new Vector(0, 0, -1));
    bounds.add(ceiling);
  }
  
  public RectangularPrismModel(double X, double Y, double Z) {
    this.X = X;
    this.Y = Y;
    this.Z = Z;

    cnts = new ArrayList<Solid>();

    bounds = new ArrayList<Plane>(6);
    bounds.add(new Wall(new Vector(X / 2, 0, 0), new Vector(-1, 0, 0), new Vector(-X, 0, 0)));
    bounds.add(new Wall(new Vector(-X / 2, 0, 0), new Vector(1, 0, 0), new Vector(X, 0, 0)));
    bounds.add(new Wall(new Vector(0, Y / 2, 0), new Vector(0, -1, 0), new Vector(0, -Y, 0)));
    bounds.add(new Wall(new Vector(0, -Y / 2, 0), new Vector(0, 1, 0), new Vector(0, Y, 0)));

    floor = new Plane(new Vector(0, 0, 0), new Vector(0, 0, 1));
    bounds.add(floor);
    ceiling = new Plane(new Vector(0, 0, Z), new Vector(0, 0, -1));
    bounds.add(ceiling);
  }
}
