package pcm.model;

import java.util.ArrayList;
import java.util.List;

import pcm.model.geom.Plane;
import pcm.model.geom.Polygon;
import pcm.model.geom.Prism;
import pcm.model.geom.Sphere;
import pcm.model.geom.Surface;
import pcm.model.geom.Wall;
import pcm.util.Vector;

/**
 * @author Satayev
 */
public class SimpleModel {

  public Surface floor;
  public List<Wall> walls;
  public List<Surface> surfaces;

  public SimpleModel(int height, int width, int length) {
    surfaces = new ArrayList<Surface>();
    //    surfaces.add(new Sphere(new Vector(0, 0, 0), height / 3));

    surfaces.add(new Prism(new Vector(), 100, new Polygon(new Vector[] { new Vector(-25, -25, 0), new Vector(-25, 25, 0), new Vector(25, 25, 0),
        new Vector(25, -25, 0) })));

    floor = new Plane(new Vector(0, 0, 0), new Vector(0, 0, 1));
    surfaces.add(floor);

    walls = new ArrayList<Wall>(4);
    walls.add(new Wall(new Vector(length / 2, 0, 0), new Vector(-1, 0, 0), new Vector(-length, 0, 0)));
    walls.add(new Wall(new Vector(-length / 2, 0, 0), new Vector(1, 0, 0), new Vector(length, 0, 0)));
    walls.add(new Wall(new Vector(0, width / 2, 0), new Vector(0, -1, 0), new Vector(0, -width, 0)));
    walls.add(new Wall(new Vector(0, -width / 2, 0), new Vector(0, 1, 0), new Vector(0, width, 0)));
    surfaces.addAll(walls);

  }
}
