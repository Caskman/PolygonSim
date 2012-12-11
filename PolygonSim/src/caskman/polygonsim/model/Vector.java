package caskman.polygonsim.model;



public class Vector {
	public float x;
	public float y;
	
	public Vector() {
		x = 0;
		y = 0;
	}
	
	public Vector(float x,float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector(Vector vector) {
		x = vector.x;
		y = vector.y;
	}
	
	public static Vector subtract(Vector u,Vector v) {
		return new Vector(u.x-v.x,u.y-v.y);
	}
	
	public static Vector add(Vector u, Vector v) {
		return new Vector(u.x+v.x,u.y+v.y);
	}
	
	public static Vector scalar(float scalar,Vector v) {
		return new Vector(scalar*v.x,scalar*v.y);
	}
	
	public static float dot(Vector u, Vector v) {
		return u.x*v.x+u.y*v.y;
	}
	
	public static Vector normalize(Vector v) {
		float mag = Vector.mag(v);
		return new Vector(v.x/mag,v.y/mag);
	}
	

	public static Vector displacement(Vector u,Vector v) {
		return new Vector(v.x-u.x,v.y-u.y);
	}

	public static float mag(Vector v) {
		return (float) Math.sqrt(v.x*v.x+v.y*v.y);
	}
}
