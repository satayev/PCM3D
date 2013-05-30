package pcm.dev.satayev.raytracer;

public class Material {
  public double lambert = 0.4, phong = 0.6, phongpower = 7, reflection = 0, refrcoeff = 1, transparency = 0;
  public double surface = 1 - reflection - transparency;
}
