package caskman.polygonsim.model;





public interface Collidable {
	
	
	public void setCollisionPosition(float percent);
	
	public Vector getVelocity();
	
	public int getLargestDim();
	
	public Rectangle getAABB();
	
	public Rectangle getCollisionAABB();
	
	public void setResolved(boolean b);
	
	public boolean isResolved();
	
}
