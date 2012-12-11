package caskman.polygonsim.model.entities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Random;

import caskman.polygonsim.MainThread;
import caskman.polygonsim.model.Collidable;
import caskman.polygonsim.model.GameContext;
import caskman.polygonsim.model.GameModel;
import caskman.polygonsim.model.Rectangle;
import caskman.polygonsim.model.Vector;

public class Line extends CollidableMob {
	
//	static float MAX_VELOCITY = 10F;
	float angleSpeed;
	float angle;
	static Dimension dims = new Dimension(30,30);
	static float tpi = 2F*3.14159F;
	Color color;
	static int dotSide = 6;
	boolean isDead;

	public Line(GameModel model,float xPos,float yPos,float xVel,float yVel) {
		super(model);
		
		position = new Vector(xPos,yPos);
		velocity = new Vector(xVel,yVel);
		angleSpeed = (tpi*model.getRandom().nextFloat())/(float)MainThread.FPS;
		angle = 0F;
		color = Color.GREEN;
		isDead = false;
	}
	
	@Override
	protected void update(GameContext g) {
		if (isDead)
			return;
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
		float interpolAngle = angle + angleSpeed*interpol;
		Vector interpolPosition = Vector.add(position,Vector.scalar(interpol,velocity));
		Vector interpolCenterPosition = new Vector(interpolPosition.x+(dims.width>>1),interpolPosition.y+(dims.height>>1));
		
		Vector lineDirection = new Vector((float)(radius*Math.cos(interpolAngle)),(float)(radius*Math.sin(interpolAngle)));
		Vector point1 = Vector.add(interpolCenterPosition,lineDirection);
		Vector point2 = Vector.add(interpolCenterPosition,Vector.scalar(-1F,lineDirection));
		g.setColor(color);
		g.drawLine((int)point1.x, (int)point1.y, (int)point2.x, (int)point2.y);
		g.setColor(Color.RED);
		int halfDotSide = dotSide >> 1;
		g.fillRect((int)(point1.x - halfDotSide) , (int)(point1.y - halfDotSide), dotSide, dotSide);
		g.fillRect((int)(point2.x - halfDotSide) , (int)(point2.y - halfDotSide), dotSide, dotSide);
//		g.setColor(Color.BLUE);
//		g.drawRect((int)interpolPosition.x,(int)interpolPosition.y,dims.width,dims.height);
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
	protected void resolveCollision(GameContext g, Collidable c, float percent) {
		if (isDead)
			return;
		if (c instanceof Line) {
			Line l = (Line)c;
			setDead(true);
			l.setDead(true);
			setCollisionPosition(percent);
			
			g.additions.add(new Explosion(model,collisionPosition.x,collisionPosition.y,Color.GREEN));
			g.removals.add(this);
			g.removals.add(l);
			
			Vector totalVelocity = Vector.add(velocity,l.velocity);
			g.additions.add(new DynamicPolygon(model,collisionPosition.x,collisionPosition.y,totalVelocity.x,totalVelocity.y,3));
			float xVel = model.dotMaxVel*model.getRandom().nextFloat() - (model.dotMaxVel/2F);
			float yVel = model.dotMaxVel*model.getRandom().nextFloat() - (model.dotMaxVel/2F);
			g.additions.add(new Dot(model,collisionPosition.x,collisionPosition.y,xVel,yVel,true));
//			Random r = model.getRandom();
//			float maxMag = model.dotMaxVel;
//			float halfMaxMag = maxMag / 2F;
//			Vector vel1 = new Vector(maxMag*r.nextFloat() - halfMaxMag,maxMag*r.nextFloat() - halfMaxMag);
//			Vector vel2 = new Vector(maxMag*r.nextFloat() - halfMaxMag,maxMag*r.nextFloat() - halfMaxMag);
//			Vector vel3 = new Vector(maxMag*r.nextFloat() - halfMaxMag,maxMag*r.nextFloat() - halfMaxMag);
//			Vector vel4 = new Vector(maxMag*r.nextFloat() - halfMaxMag,maxMag*r.nextFloat() - halfMaxMag);
//			
//			g.additions.add(new Dot(model,collisionPosition.x+2F*vel1.x,collisionPosition.y+2F*vel1.y,vel1.x,vel1.y,true));
//			g.additions.add(new Dot(model,collisionPosition.x+2F*vel2.x,collisionPosition.y+2F*vel2.y,vel2.x,vel2.y,true));
//			g.additions.add(new Dot(model,collisionPosition.x+2F*vel3.x,collisionPosition.y+2F*vel3.y,vel3.x,vel3.y,true));
//			g.additions.add(new Dot(model,collisionPosition.x+2F*vel4.x,collisionPosition.y+2F*vel4.y,vel4.x,vel4.y,true));
			
		}
	}
	
	private void setDead(boolean b) {
		isDead = b;
	}

}
