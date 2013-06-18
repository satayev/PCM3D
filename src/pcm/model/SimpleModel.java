package pcm.model;

import java.util.ArrayList;
import java.util.List;

import pcm.model.geom.Plane;
import pcm.model.geom.Polygon;
import pcm.model.geom.Prism;
import pcm.model.geom.Sphere;
import pcm.model.geom.Surface;
import pcm.model.geom.Wall;
import pcm.util.V;
import pcm.util.Vector;

/**
 * @author Satayev
 */
public class SimpleModel {

  public Surface floor;
  public List<Wall> walls;
  public List<Surface> surfaces;

  public SimpleModel(List<Surface> s, Vector[] r) {
    surfaces = new ArrayList<Surface>();

    floor = new Plane(new Vector(0, 0, 0), new Vector(0, 0, 1));
    surfaces.add(floor);

    walls = new ArrayList<Wall>(2*r.length);
    Vector sum = new Vector();
    for (int i = 0; i < r.length; i++) sum.add(r[i]);
    sum.mult(.5);
    
    for (int i = r.length-1; i >= 0; i--) {
      Vector adj = r[i];
      Vector normal = new Vector(adj.y,-adj.x,0);
      normal.normalize();
      Vector position = V.mult(-V.dot(sum,normal),normal);
      walls.add(new Wall(sum, normal, V.add(V.mult(-2, sum),adj)));
      walls.add(new Wall(V.mult(-1,sum), V.mult(-1,normal), V.add(V.mult(2, sum),V.mult(-1,adj))));
      sum.add(V.mult(-1,adj));
      // TODO(john): not sure if this works
    }
    surfaces.addAll(walls);
    surfaces.addAll(s);

  }
}
