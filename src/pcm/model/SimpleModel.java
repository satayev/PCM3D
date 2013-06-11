package pcm.model;

import java.util.ArrayList;
import java.util.List;

import pcm.model.geom.Floor;
import pcm.model.geom.Plane;
import pcm.model.geom.Sphere;
import pcm.model.geom.Surface;
import pcm.model.geom.Wall;
import pcm.util.Vector;

/**
 * @author Satayev
 */
public class SimpleModel {

  public Plane floor, ceiling;
  public List<Wall> walls;
  public List<Surface> surfaces;

  public SimpleModel(int height, int width, int length) {
    surfaces = new ArrayList<Surface>();
    surfaces.add(new Sphere(new Vector(0, 0, 0), height / 4));

    Vector[] v = new Vector[] { new Vector(25, 25, 0), new Vector(75, 25, 0), new Vector(75, 75, 0), new Vector(25, 75, 0), new Vector(25, 25, 100),
        new Vector(75, 25, 100), new Vector(75, 75, 100), new Vector(25, 75, 100), };

    //    surfaces.add(new Polygon(new Vector[] { v[0], v[1], v[5], v[4] }));
    //    surfaces.add(new Polygon(new Vector[] { v[1], v[2], v[6], v[5] }));
    //    surfaces.add(new Polygon(new Vector[] { v[2], v[3], v[7], v[6] }));
    //    surfaces.add(new Polygon(new Vector[] { v[3], v[0], v[4], v[7] }));
    //    surfaces.add(new Polygon(new Vector[] { v[4], v[5], v[6], v[7] }));

    floor = new Floor(new Vector(0, 0, 0), new Vector(0, 1, 0));
    // !!!FIXME!!! ceiling is twice the height of the tower
    ceiling = new Plane(new Vector(0, height * 2, 0), new Vector(0, -1, 0));

    walls = new ArrayList<Wall>(4);
    walls.add(new Wall(new Vector(length / 2, 0, 0), new Vector(-1, 0, 0), new Vector(-length, 0, 0)));
    walls.add(new Wall(new Vector(-length / 2, 0, 0), new Vector(1, 0, 0), new Vector(length, 0, 0)));
    walls.add(new Wall(new Vector(0, 0, width / 2), new Vector(0, 0, -1), new Vector(0, 0, -width)));
    walls.add(new Wall(new Vector(0, 0, -width / 2), new Vector(0, 0, 1), new Vector(0, 0, width)));

    surfaces.addAll(walls);
    surfaces.add(floor);
    surfaces.add(ceiling);
  }
}
