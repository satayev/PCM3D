package pcm.model.geom;

import java.util.ArrayList;

import pcm.model.Photon;

/**
 * Walls define a boundary of the tile.
 * 
 * @author Satayev
 */
public class Wall extends Plane {

  private Vector differential;

  public Wall(Vector position, Vector normal, Vector differential) {
    super(position, normal);
    this.differential = differential;
  }

  public void wrap(Photon photon) {
    photon.move(differential);
  }

}
