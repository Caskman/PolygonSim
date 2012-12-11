package caskman.polygonsim.model;


import java.awt.Dimension;



public interface Collidable {
	
	
	public void setCollisionPosition(float percent);
	
	public Vector getVelocity();
	
	public int getLargestDim();
	
	public Rectangle getAABB();
	
	public Rectangle getCollisionAABB();
	
}
