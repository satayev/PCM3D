package pcm.gui.graphics;

import pcm.model.geom.V;
import pcm.model.geom.Vector;
import processing.core.*;
import processing.opengl.*;

/**
 * A rotating Earth based on 3D textured sphere with simple rotation control by Mike Chang and revised by Aaron Koblin. 
 * Spins based on progression of ISS at 51.6-degree inclination to stationary ISS, as opposed to ISS rotating around it at 51.6-degree inclination to the equator.
 * latitude = 51.6 by sine of longitude (requires degrees-radians conversion)
 * 
 * @author Susan
 */ 
public class Earth {

	private Applet applet; // parent applet
	Vector position = new Vector(.5*AppletModel.magnif,.5*AppletModel.magnif,-600); // changed in Applet and AppletInterfacer
	float offset = 0; // in degrees longitude offset for sine function because ISS doesn't follow consistent path
	double ISSOrbitalPeriod = 92 * 60 + 50; // in seconds
	double ISSLag = 20; // degrees that Earth rotates past ISS in every orbit 
	double speed = (360 - ISSLag) / ISSOrbitalPeriod; // degrees per second, speed of Earth's spin
	
	PImage bg;
	PImage texmap;

	int sDetail = 35;  // Sphere detail setting
	float rotationX = 0; // corresponds with degrees latitude
	float rotationY = 0; // corresponds with degrees longitude
	float velocityX = 0;
	float velocityY = 0;
	float globeRadius = 1500;
	float pushBack = 0;

	float[] cx, cz, sphereX, sphereY, sphereZ;
	float sinLUT[];
	float cosLUT[];
	float SINCOS_PRECISION = 0.5f;
	int SINCOS_LENGTH = (int)(360.0 / SINCOS_PRECISION);

	public Earth() {
	}

	public Earth(Vector position, double radius) {
		  this.position = position.clone();
		  globeRadius = (float)radius;
	}
	
	/*
	 * Sets up sphere with Earth texture.
	 */
	public void load(Applet applet) {
	  this.applet = applet;
	  texmap = applet.loadImage("world32k.jpg");    
	  initializeSphere(sDetail);
	}

	/*
	 * Rotates Earth to latitude and longitude position with ISS over it
	 */
	void setISSPosition(double latitude, double longitude, boolean toEquator) {
		rotationX = (float)latitude;
		rotationY = (float)longitude;
		
		float x = (float)Math.asin(latitude/51.6);
		offset = applet.degrees(x) - (float)longitude;
		if (x > 0 && toEquator)
			offset += 90;
		else if (x < 0 && !toEquator)
			offset -= 90;		
	}
	
	/*
	 * Performs a rotation of Earth at speed of the ISS (slightly slower than Earth's rotation).
	 * 
	 * @param scalar with domain of [0, infinity) to slow down or speed up speed away from ~93 minutes ISS orbit in animation
	 */
	void spin(double scalar) {
		rotationY += (speed / applet.frameRate) * scalar;
		
		rotationX = (float)(51.6 * Math.sin(applet.radians(rotationY + offset)));
		//applet.println(rotationY+ " "+ rotationX);

	}
	
	void draw(boolean toSpin, double speed) {
		if (toSpin)
			spin(speed);
		renderGlobe();
	}
	
	void renderGlobe() {
	  applet.pushMatrix();
	  applet.translate((float)position.x, (float)position.y, (float)position.z);
	  applet.pushMatrix();
	  applet.noFill();
	  applet.stroke(255,200);
	  applet.strokeWeight(2);
	  applet.smooth();
	  applet.popMatrix();
	  applet.lights();    
	  applet.pushMatrix();
	  applet.rotateX( applet.radians(-rotationX) );  
	  applet.rotateY( applet.radians(270 - rotationY) );
	  applet.fill(200);
	  applet.noStroke();
	  applet.textureMode(applet.IMAGE);  
	  texturedSphere(globeRadius, texmap);
	  applet.popMatrix();  
	  applet.popMatrix();
	  rotationX += velocityX;
	  rotationY += velocityY;
	  velocityX *= 0.95;
	  velocityY *= 0.95;
	  
	}

	void initializeSphere(int res)
	{
	  sinLUT = new float[SINCOS_LENGTH];
	  cosLUT = new float[SINCOS_LENGTH];

	  for (int i = 0; i < SINCOS_LENGTH; i++) {
	    sinLUT[i] = (float) Math.sin(i * applet.DEG_TO_RAD * SINCOS_PRECISION);
	    cosLUT[i] = (float) Math.cos(i * applet.DEG_TO_RAD * SINCOS_PRECISION);
	  }

	  float delta = (float)SINCOS_LENGTH/res;
	  float[] cx = new float[res];
	  float[] cz = new float[res];
	  
	  // Calc unit circle in XZ plane
	  for (int i = 0; i < res; i++) {
	    cx[i] = -cosLUT[(int) (i*delta) % SINCOS_LENGTH];
	    cz[i] = sinLUT[(int) (i*delta) % SINCOS_LENGTH];
	  }
	  
	  // Computing vertexlist vertexlist starts at south pole
	  int vertCount = res * (res-1) + 2;
	  int currVert = 0;
	  
	  // Re-init arrays to store vertices
	  sphereX = new float[vertCount];
	  sphereY = new float[vertCount];
	  sphereZ = new float[vertCount];
	  float angle_step = (SINCOS_LENGTH*0.5f)/res;
	  float angle = angle_step;
	  
	  // Step along Y axis
	  for (int i = 1; i < res; i++) {
	    float curradius = sinLUT[(int) angle % SINCOS_LENGTH];
	    float currY = -cosLUT[(int) angle % SINCOS_LENGTH];
	    for (int j = 0; j < res; j++) {
	      sphereX[currVert] = cx[j] * curradius;
	      sphereY[currVert] = currY;
	      sphereZ[currVert++] = cz[j] * curradius;
	    }
	    angle += angle_step;
	  }
	  sDetail = res;
	}

	// Generic routine to draw textured sphere
	void texturedSphere(float r, PImage t) 
	{
	  int v1,v11,v2;
	  r = (r + 240) * 0.33f;
	  applet.beginShape(applet.TRIANGLE_STRIP);
	  applet.texture(t);
	  float iu=(float)(t.width-1)/(sDetail);
	  float iv=(float)(t.height-1)/(sDetail);
	  float u=0,v=iv;
	  for (int i = 0; i < sDetail; i++) {
		applet.vertex(0, -r, 0,u,0);
		applet.vertex(sphereX[i]*r, sphereY[i]*r, sphereZ[i]*r, u, v);
	    u+=iu;
	  }
	  applet.vertex(0, -r, 0,u,0);
	  applet.vertex(sphereX[0]*r, sphereY[0]*r, sphereZ[0]*r, u, v);
	  applet.endShape();   
	  
	  // Middle rings
	  int voff = 0;
	  for(int i = 2; i < sDetail; i++) {
	    v1=v11=voff;
	    voff += sDetail;
	    v2=voff;
	    u=0;
	    applet.beginShape(applet.TRIANGLE_STRIP);
	    applet.texture(t);
	    for (int j = 0; j < sDetail; j++) {
	    	applet.vertex(sphereX[v1]*r, sphereY[v1]*r, sphereZ[v1++]*r, u, v);
	    	applet.vertex(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2++]*r, u, v+iv);
	      u+=iu;
	    }
	  
	    // Close each ring
	    v1=v11;
	    v2=voff;
	    applet.vertex(sphereX[v1]*r, sphereY[v1]*r, sphereZ[v1]*r, u, v);
	    applet.vertex(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2]*r, u, v+iv);
	    applet.endShape();
	    v+=iv;
	  }
	  u=0;
	  
	  // Add the northern cap
	  applet.beginShape(applet.TRIANGLE_STRIP);
	  applet.texture(t);
	  for (int i = 0; i < sDetail; i++) {
	    v2 = voff + i;
	    applet.vertex(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2]*r, u, v);
	    applet.vertex(0, r, 0,u,v+iv);    
	    u+=iu;
	  }
	  applet.vertex(sphereX[voff]*r, sphereY[voff]*r, sphereZ[voff]*r, u, v);
	  applet.endShape();
	  
	}
}
