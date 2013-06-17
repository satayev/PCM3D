package pcm.model;

import java.util.ArrayList;
import java.util.List;

import pcm.model.geom.Plane;
import pcm.model.geom.Wall;
import pcm.model.geom.curves.Polygon;
import pcm.model.geom.solids.Prism;
import pcm.model.geom.solids.Solid;
import pcm.util.Vector;

/**
 * @author Satayev
 */
public class SimpleModel {

  // Size of the bounding box 
  public int height, width, length;

  public Plane floor, ceiling;
  // defines boundaries for the tile: walls, floor, and ceiling
  public List<Plane> bounds;
  // includes all CNTs in the tile
  public List<Solid> cnts;

  public SimpleModel(int height, int width, int length) {
    this.height = height;
    this.width = width;
    this.length = length;

    cnts = new ArrayList<Solid>();

    cnts.add(new Prism(new Vector(), new Polygon(new Vector[] { new Vector(-25, -25, 0), new Vector(-25, 25, 0), new Vector(25, 25, 0),
        new Vector(25, -25, 0) })));

    // TODO(satayev): fix Circle curve;
    //    cnts.add(new Sphere(new Vector(0, 0, 0), height / 3));
    //    cnts.add(new Prism(new Vector(), new Circle(50)));
    //    cnts.add(new Cylinder(new Vector(), 50));

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
