package caskman.polygonsim.model.entities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import caskman.polygonsim.model.Collidable;
import caskman.polygonsim.model.GameContext;
import caskman.polygonsim.model.GameModel;
import caskman.polygonsim.model.Rectangle;
import caskman.polygonsim.model.Vector;


public class Dot extends CollidableMob {
	
	public static Dimension dims = new Dimension(10,10);
	private Color color;
	private boolean isResolved;

	public Dot(GameModel m,float xPos,float yPos,float xVel,float yVel) {
		super(m);
		
		position = new Vector(xPos,yPos);
		velocity = new Vector(xVel,yVel);
		color = Color.RED;
		isResolved = false;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	@Override
	protected void update(GameContext g) {
		color = Color.RED;
		
		if (isResolved())
			return;
		
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
	protected void draw(Graphics g, float interpol) {
		int x = (int) (getX() + getXVel()*interpol);
		int y = (int) (getY() + getYVel()*interpol);
		g.setColor(color);
		g.fillRect(x, y, dims.width, dims.height);
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

	@Override
	protected void resolveCollision(GameContext g,Collidable c, float percent) {
		setCollisionPosition(percent);
		c.setCollisionPosition(percent);
		
		Rectangle r1 = getCollisionAABB();
		Rectangle r2 = c.getCollisionAABB();
		
		Vector v1 = getVelocity();
		Vector v2 = c.getVelocity();
		
		boolean xCollision = r1.right() > r2.left() && r1.left() < r2.right();
		boolean yCollision = r1.bottom() > r2.top() && r1.top() < r2.bottom();
		float temp;
		
		if (xCollision) {
			temp = v1.x;
			v1.x = v2.x;
			v2.x = temp;
		}
		
		if (yCollision) {
			temp = v1.y;
			v1.y = v2.y;
			v2.y = temp;
		}
		
		setVelocity(v1);
		c.setVelocity(v2);
		
		Vector p1 = getCollisionPosition();
		Vector p2 = c.getCollisionPosition();
		
		setPosition(Vector.add(Vector.scalar(1F-percent, v1),p1));
		c.setPosition(Vector.add(Vector.scalar(1F-percent, v2),p2));
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
