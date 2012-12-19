package caskman.polygonsim.model.entities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.List;

import caskman.polygonsim.RenderObject;
import caskman.polygonsim.UpdateThread;
import caskman.polygonsim.model.Collidable;
import caskman.polygonsim.model.GameContext;
import caskman.polygonsim.model.GameModel;
import caskman.polygonsim.model.Rectangle;
import caskman.polygonsim.model.Vector;


public class Dot extends CollidableMob {
	
	public static Dimension dims = new Dimension(6,6);
	private Color color;
	private boolean isDead;
	static int MAX_GHOST_DURATION = (int) (UpdateThread.FPS*.5F);
	boolean isGhost;
	int ghostDuration;

	public Dot(GameModel m,float xPos,float yPos,float xVel,float yVel,boolean startAsGhost) {
		super(m);
		
		position = new Vector(xPos,yPos);
		velocity = new Vector(xVel,yVel);
		color = Color.RED;
		isDead = false;
		isGhost = startAsGhost;
		ghostDuration = 0;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	@Override
	protected void update(GameContext g) {
		if (isDead)
			return;
		if (ghostDuration > MAX_GHOST_DURATION)
			isGhost = false;
		color = Color.RED;
		Vector newPos = Vector.add(position, velocity);
		
		if (newPos.x < 0 || newPos.x > model.getScreenDims().width - dims.width) {
			velocity.x = velocity.x * -1;
			position.x = (newPos.x < 0)?0:model.getScreenDims().width - dims.width;
		}
		if (newPos.y < 0 || newPos.y > model.getScreenDims().height - dims.height) {
			velocity.y = velocity.y * -1;
			position.y = (newPos.y < 0)?0:model.getScreenDims().height - dims.height;
		}
		position = Vector.add(position, velocity);
		ghostDuration++;
	}

//	@Override
//	protected void draw(Graphics2D g, float interpol) {
//		int x = (int) (getX() + getXVel()*interpol);
//		int y = (int) (getY() + getYVel()*interpol);
//		if (isGhost)
//			g.setColor(Color.WHITE);
//		else 
//			g.setColor(color);
////		g.fillArc(x, y, dims.width, dims.height, 0, 360);
//		g.fillRect(x, y, dims.width, dims.height);
//	}


	@Override
	public int getLargestDim() {
		return dims.width;
	}

	@Override
	public Rectangle getAABB() {
		return new Rectangle(position.x,position.y,dims.width,dims.height);
	}

	@Override
	public Rectangle getCollisionAABB() {
		return new Rectangle(collisionPosition.x,collisionPosition.y,dims.width,dims.height);
	}

	private void setDead(boolean b) {
		isDead = b;
	}
	
	@Override
	protected void resolveCollision(GameContext g,Collidable c, float percent) {
		if (isGhost)
			return;
		setCollisionPosition(percent);
		if (c instanceof Dot) {
			if (((Dot)c).isGhost)
				return;
			g.removals.add(this);
			setDead(true);
			g.removals.add((Mob)c);
			((Dot)c).setDead(true);
			Vector newVel = Vector.add(((Mob)c).velocity,velocity);
			g.additions.add(new DynamicPolygon(model,position.x,position.y,newVel.x,newVel.y,2));
			g.additions.add(new Explosion(model,collisionPosition.x,collisionPosition.y,Color.RED));
		}
	}

	@Override
	public void getRenderObjects(List<RenderObject> renderList) {
		renderList.add(new DotObject(isGhost,color,position,velocity));
	}


	private class DotObject extends RenderObject {

		private boolean isGhost;
		private Color color;
		private Vector position;
		private Vector velocity;
		
		public DotObject(boolean isGhost2, Color color2, Vector position2,
				Vector velocity2) {
			isGhost = isGhost2;
			color = color2;
			position = position2;
			velocity = velocity2;
		}

		@Override
		public void render(Graphics2D g, float interpol) {
			int x = (int) (position.x + velocity.x*interpol);
			int y = (int) (position.y + velocity.y*interpol);
			if (isGhost)
				g.setColor(Color.WHITE);
			else 
				g.setColor(color);
			g.fillRect(x, y, dims.width, dims.height);
		}
		
	}


}
