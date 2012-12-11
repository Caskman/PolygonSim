package caskman.polygonsim.model;





public interface Collidable {
	
	
	public void setCollisionPosition(float percent);
	
	public Vector getVelocity();
	
	public void setVelocity(Vector v);
	
	public int getLargestDim();
	
	public Rectangle getAABB();
	
	public Rectangle getCollisionAABB();
	
	public void setResolved(boolean b);
	
	public boolean isResolved();
	
	public void setPosition(Vector v);
	
	public Vector getCollisionPosition();
	
}
