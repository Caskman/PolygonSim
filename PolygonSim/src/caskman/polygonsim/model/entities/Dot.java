package caskman.polygonsim.model.entities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import caskman.polygonsim.model.Collidable;
import caskman.polygonsim.model.GameContext;
import caskman.polygonsim.model.GameModel;
import caskman.polygonsim.model.Mob;
import caskman.polygonsim.model.Rectangle;
import caskman.polygonsim.model.Vector;


public class Dot extends Mob implements Collidable {
	
	public static Dimension dims = new Dimension(10,10);
	private Color color;

	public Dot(GameModel m,float xPos,float yPos,float xVel,float yVel) {
		super(m);
		
		position = new Vector(xPos,yPos);
		velocity = new Vector(xVel,yVel);
		color = Color.RED;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	@Override
	public void update(GameContext g) {
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
	public void draw(Graphics g, float interpol) {
		int x = (int) (getX() + getXVel()*interpol);
		int y = (int) (getY() + getYVel()*interpol);
		g.setColor(color);
		g.fillRect(x, y, dims.width, dims.height);
	}

	@Override
	public void setTempNextPosition(float percent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Vector getTempPosition() {
		//TODO
		return null;
	}

	@Override
	public Rectangle getAABB() {
		return new Rectangle(position.x,position.y,dims.width,dims.height);
	}

	@Override
	public Vector getVelocity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dimension getDims() {
		// TODO Auto-generated method stub
		return null;
	}

}
