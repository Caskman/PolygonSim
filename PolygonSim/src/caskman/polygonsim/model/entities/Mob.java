package caskman.polygonsim.model.entities;


import java.awt.Dimension;
import java.awt.Graphics2D;

import caskman.polygonsim.model.GameContext;
import caskman.polygonsim.model.GameModel;
import caskman.polygonsim.model.Vector;

public abstract class Mob {
	
	protected GameModel model;
	protected Vector position;
	protected Vector velocity;
	
	
	public Mob(GameModel m) {
		model = m;
	}
	
	public float getX() {
		return position.x;
	}
	
	public float getY() {
		return position.y;
	}
	
	public float getXVel() {
		return velocity.x;
	}
	
	public float getYVel() {
		return velocity.y;
	}
	
	public void updateMob(GameContext g) {
		update(g);
	}
	
	public void drawMob(Graphics2D g,float interpol,Vector offset) {
		draw(g,interpol,offset);
	}
	
	protected abstract void update(GameContext g);
	
	protected abstract void draw(Graphics2D g,float interpol,Vector offset);
	
	public Vector getVelocity() {
		return velocity;
	}
	
	public void applyAccel(Vector accel) {
		velocity = Vector.add(velocity,accel);
	}
	
	public Vector getPosition() {
		return position;
	}
	
	public abstract Dimension getDims();
	
}
