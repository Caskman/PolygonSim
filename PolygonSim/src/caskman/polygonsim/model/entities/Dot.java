package caskman.polygonsim.model.entities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

import caskman.polygonsim.model.Collidable;
import caskman.polygonsim.model.GameContext;
import caskman.polygonsim.model.GameModel;
import caskman.polygonsim.model.Rectangle;
import caskman.polygonsim.model.Vector;


public class Dot extends CollidableMob {
	
	public static Dimension dims = new Dimension(6,6);
	private Color color;
	private boolean isResolved;
	private boolean isDead;

	public Dot(GameModel m,float xPos,float yPos,float xVel,float yVel) {
		super(m);
		
		position = new Vector(xPos,yPos);
		velocity = new Vector(xVel,yVel);
		color = Color.RED;
		isResolved = false;
		isDead = false;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	@Override
	protected void update(GameContext g) {
		if (isDead)
			return;
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
	}

	@Override
	protected void draw(Graphics2D g, float interpol) {
		int x = (int) (getX() + getXVel()*interpol);
		int y = (int) (getY() + getYVel()*interpol);
		g.setColor(color);
		g.fillArc(x, y, dims.width, dims.height, 0, 360);
//		g.fillRect(x, y, dims.width, dims.height);
	}

	@Override
	public void setCollisionPosition(float percent) {
		collisionPosition = Vector.add(Vector.scalar(percent, velocity),position);
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
		setCollisionPosition(percent);
		if (c instanceof Dot) {
			g.removals.add(this);
			setDead(true);
			g.removals.add((Mob)c);
			((Dot)c).setDead(true);
			Vector newVel = Vector.add(((Mob)c).velocity,velocity);
			g.additions.add(new Line(model,position.x,position.y,newVel.x,newVel.y));
			g.additions.add(new Explosion(model,collisionPosition.x,collisionPosition.y));
		}
	}

	@Override
	public void setResolved(boolean b) {
		isResolved = b;
	}

	@Override
	public boolean isResolved() {
		return isResolved;
	}




}
