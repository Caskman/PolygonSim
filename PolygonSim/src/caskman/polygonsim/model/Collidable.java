package caskman.polygonsim.model;

import java.awt.Dimension;



public interface Collidable {
//	public void resolve(Collidable a);
	
	public void setTempNextPosition(float percent);
	
	public Vector getTempPosition();
	
	public Rectangle getAABB();
	
	public Vector getVelocity();
	
	public Dimension getDims();
}
