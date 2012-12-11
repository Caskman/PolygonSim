package caskman.polygonsim.model.entities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import caskman.polygonsim.model.GameContext;
import caskman.polygonsim.model.GameModel;
import caskman.polygonsim.model.Mob;
import caskman.polygonsim.model.Vector;


public class Dot extends Mob {
	
	private static Dimension dims = new Dimension(10,10);

	public Dot(GameModel m,float xPos,float yPos,float xVel,float yVel) {
		super(m);
		
		position = new Vector(xPos,yPos);
		velocity = new Vector(xVel,yVel);
		
	}
	
	@Override
	public void update(GameContext g) {
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
		g.setColor(Color.red);
		g.fillRect(x, y, dims.width, dims.height);
	}

}
