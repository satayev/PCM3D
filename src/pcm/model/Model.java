package pcm.model;

import java.util.List;

import pcm.model.geom.Plane;
import pcm.model.geom.solids.Solid;

public abstract class Model {

  // Size of the bounding box 
  public double X, Y, Z;

  public Plane floor, ceiling;
  // defines boundaries for the tile: walls, floor, and ceiling
  public List<Plane> bounds;
  // includes all CNTs in the tile
  public List<Solid> cnts;

}
