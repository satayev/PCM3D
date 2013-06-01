package pcm.dev.satayev.raytracer;

import java.util.List;

import pcm.geom.Intersection;
import pcm.model.shape.Plane;
import pcm.model.shape.Sphere;

public class Raytracer {
  static int min;
  static List<Object> objects;

  public static Intersection trace(Ray ray) {
     Intersection p = null;
     for (Object o : objects) {
       if (o instanceof Sphere) {
         Sphere sp = (Sphere)o;
         Intersection ep = sp.trace(ray);
         if (ep == null)
           continue;
         double d = ep.dist;
         if (p == null || (d < p.dist && d > 1e-5)) {
           if (d < min)
             return null;
           p = ep;
           p.owner = o;
         }
       }
       if (o instanceof Plane) {
         Plane sp = (Plane)o;
         Intersection ep = sp.trace(ray);
         if (ep == null)
           continue;
         double d = ep.dist;
         if (p == null || (d < p.dist && d > 1e-5)) {
           if (d < min)
             return null;
           p = ep;
           p.owner = o;
         }
       }
     }
     return p;
   }
      
  public static Vec color(Ray ray) {
    Intersection hit = trace(ray);
    if (hit == null)
      return new Vec();
  }

  rt.color = function(r)
  {
      var hit = rt.trace(r)   
      if (!hit) return rt.bgcolor 
      
      hit.norm = hit.norm || hit.owner.norm(hit.at)
          
      var surfcol = rt.diffuse(r, hit) || [0, 0, 0]
      var reflcol = rt.reflection(r, hit) || [0, 0, 0]    
      var refrcol = rt.refraction(r, hit) || [0, 0, 0]    
      
      var m = hit.owner.mat
      
      var surf = m.surface
      var refl = m.reflection
      var refr = m.transparency
      
      var col = new Array(3)
          
      for (var i = 0; i < 3; i++)
          col[i] = surf * surfcol[i] + refl * reflcol[i] + refr * refrcol[i]
      
      return col
  }

  rt.diffuse = function(r, hit)
  {   
      var obj = hit.owner 
      var m = obj.mat
      var sumlight = 0    
          
      for (var j in rt.lights)
      {
          var light = rt.lights[j]        
          var dir = vec.sub(hit.at, light.at)     
          var dist = vec.len(dir)
          
          dir = vec.mul(1/dist, dir)
          
          var ray = {}
          
          ray.from = light.at
          ray.dir = dir       
          
          var q = rt.trace(ray, dist - math.eps)
          
          if (!q || vec.sqrdist(q.at, hit.at) > math.eps)
              continue        
          
          if (m.phong > 0)
          {
              var lr = vec.reflect(dir, hit.norm)
              var vcos = -vec.dot(lr, r.dir)
              
              if (vcos > 0)
              {           
                  var phong = Math.pow(vcos, m.phongpower)
                  sumlight += light.power * m.phong * phong
              }           
          }
          
          if (m.lambert > 0)
          {
              var cos = -vec.dot(dir, hit.norm)
              
              if (cos > 0)
                  sumlight += light.power * m.lambert * cos
          }
      }   
      
      return vec.mul(sumlight, typeof obj.color == 'function' ? obj.color(hit.at) : obj.color)
  }

  rt.reflection = function(r, hit)
  {   
      var k = hit.owner.mat.reflection

      if (k * r.power < math.eps)
          return
              
      var q = {}
              
      q.dir = vec.reflect(r.dir, hit.norm)        
      q.from = hit.at
      q.power = k * r.power       
                      
      return rt.color(q)
  }

  public static Vec refraction(Ray ray, Intersection hit) {
    
  }
  
  rt.refraction = function(r, hit)
  {
      var m = hit.owner.mat
      var t = m.transparency
      
      if (t * r.power < math.eps)
          return
      
      var dir = vec.refract(r.dir, hit.norm, m.refrcoeff) 
      if (!dir) return
          
      var q = {}

      q.dir = dir
      q.from = hit.at
      q.power = t * r.power   
          
      return rt.color(q)
  }
}
