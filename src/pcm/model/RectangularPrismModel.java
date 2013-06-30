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
    this.height = height;
    this.width = width;
    this.length = length;

    cnts = new ArrayList<Solid>();

    cnts.add(new Prism(new Vector(), new Polygon(new Vector[] { new Vector(-25, -25, 0), new Vector(-25, 25, 0), new Vector(25, 25, 0),
        new Vector(25, -25, 0) })));

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
}
