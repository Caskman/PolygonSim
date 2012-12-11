package caskman.polygonsim.model;

import java.awt.Graphics;

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
	
	public abstract void update(GameContext g);
	
	public abstract void draw(Graphics g,float interpol);
	
}
