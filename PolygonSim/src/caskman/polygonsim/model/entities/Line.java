package caskman.polygonsim.model.entities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

import caskman.polygonsim.MainThread;
import caskman.polygonsim.model.GameContext;
import caskman.polygonsim.model.GameModel;
import caskman.polygonsim.model.Vector;

public class Line extends Mob {
	
	static float MAX_VELOCITY = 10F;
	float angleSpeed;
	float angle;
	static Dimension dims = new Dimension(6,6);
	static float tpi = 2F*3.14159F;
	Color color;

	public Line(GameModel model,float xPos,float yPos,float xVelPercent,float yVelPercent) {
		super(model);
		
		position = new Vector(xPos,yPos);
		velocity = new Vector(MAX_VELOCITY*xVelPercent,MAX_VELOCITY*yVelPercent);
		angleSpeed = tpi/(float)MainThread.FPS;
		angle = 0F;
		color = Color.GREEN;
	}
	
	@Override
	protected void update(GameContext g) {
		angle = ((angle += angleSpeed) > tpi)?angle - tpi:angle;
		color = Color.GREEN;
		
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
		float radius = dims.width>>1;
		Vector lineDirection = new Vector((float)(radius*Math.cos(angle)),(float)(radius*Math.sin(angle)));
		Vector p1 = Vector.add(position,lineDirection);
		Vector p2 = Vector.add(position,Vector.scalar(-1F,lineDirection));
		g.setColor(color);
		g.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
	}

}
