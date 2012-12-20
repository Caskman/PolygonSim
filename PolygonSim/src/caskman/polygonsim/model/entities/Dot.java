package caskman.polygonsim.model.entities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

import caskman.polygonsim.MainThread;
import caskman.polygonsim.model.Collidable;
import caskman.polygonsim.model.GameContext;
import caskman.polygonsim.model.GameModel;
import caskman.polygonsim.model.Rectangle;
import caskman.polygonsim.model.Vector;


public class Dot extends CollidableMob {
	
	public static Dimension dims = new Dimension(6,6);
	private Color color;
	private boolean isDead;
	static int MAX_GHOST_DURATION = (int) (MainThread.FPS*.5F);
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
		
		if (newPos.x < 0 || newPos.x > model.getMapDims().width - dims.width) {
			velocity.x = velocity.x * -1;
			position.x = (newPos.x < 0)?0:model.getMapDims().width - dims.width;
		}
		if (newPos.y < 0 || newPos.y > model.getMapDims().height - dims.height) {
			velocity.y = velocity.y * -1;
			position.y = (newPos.y < 0)?0:model.getMapDims().height - dims.height;
		}
		position = Vector.add(position, velocity);
		ghostDuration++;
	}

	@Override
	protected void draw(Graphics2D g, float interpol,Vector offset) {
		int x = (int) (getX() + getXVel()*interpol + offset.x);
		int y = (int) (getY() + getYVel()*interpol + offset.y);
		if (isGhost)
			g.setColor(Color.WHITE);
		else 
			g.setColor(color);
//		g.fillArc(x, y, dims.width, dims.height, 0, 360);
		g.fillRect(x, y, dims.width, dims.height);
	}


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

	public Dimension getDims() {
		return dims;
	}




}
