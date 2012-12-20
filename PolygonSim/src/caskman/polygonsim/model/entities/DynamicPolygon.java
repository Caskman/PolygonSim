package caskman.polygonsim.model.entities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import caskman.polygonsim.MainThread;
import caskman.polygonsim.model.Collidable;
import caskman.polygonsim.model.GameContext;
import caskman.polygonsim.model.GameModel;
import caskman.polygonsim.model.Rectangle;
import caskman.polygonsim.model.Vector;

public class DynamicPolygon extends CollidableMob {
	
	static Dimension dims = new Dimension(30,30);
	int vertices;
	float angle;
	static float tpi = 2*3.14159F;
	float angleSpeed;
	static int dotSide = 8;

	public DynamicPolygon(GameModel m,float xPos,float yPos,float xVel,float yVel,int numVertices) {
		super(m);
		position = new Vector(xPos,yPos);
		velocity = new Vector(xVel,yVel);
		vertices = numVertices;
		angle = 0F;
		angleSpeed = (tpi*model.getRandom().nextFloat())/(float)MainThread.FPS;
	}

	@Override
	protected void update(GameContext g) {
		angle = ((angle += angleSpeed) > tpi)?angle - tpi:angle;
		
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
	}

	@Override
	protected void draw(Graphics2D g, float interpol,Vector offset) {
		List<Vector> points = new ArrayList<Vector>(vertices);
		
		float pointAngleInterval = tpi/vertices;
		float pointAngle = angle + interpol*angleSpeed;
		int radius = dims.width>>1;
		Vector interpolPosition = Vector.add(Vector.add(position,offset),Vector.scalar(interpol, velocity));
		Vector interpolCenterPosition = Vector.add(interpolPosition,new Vector(dims.width>>1,dims.height>>1));
		for (int i = 0; i < vertices; i++) {
			Vector pointOffset = new Vector((float)(radius*Math.cos(pointAngle)),(float)(radius*Math.sin(pointAngle)));
			points.add(Vector.add(pointOffset,interpolCenterPosition));
			
			pointAngle += pointAngleInterval;
		}
		
		// draw connecting lines
		
		g.setColor(Color.GREEN);
		for (int i = 0; i < vertices; i++) {
			Vector p1 = points.get(i);
			Vector p2 = points.get((i+1)%vertices);
			g.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
		}
		
		
		//draw points
		int halfDotSide = dotSide>>1;
		g.setColor(Color.RED);
		for (Vector point : points) {
			g.fillRect((int)point.x - halfDotSide, (int)point.y - halfDotSide, dotSide, dotSide);
		}
		
		// draw Axis Aligned Bounding Box
//		g.setColor(Color.BLUE);
//		g.drawRect((int)interpolPosition.x, (int)interpolPosition.y, dims.width, dims.height);
		
		// draw number of vertices
		g.setColor(Color.YELLOW);
		g.drawString(""+vertices, interpolCenterPosition.x - 3, interpolCenterPosition.y + 2);
		
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
//		if (isDead) {
//			return;
//		}
		if (c instanceof DynamicPolygon) {
			DynamicPolygon d = (DynamicPolygon)c;
//			if (d.isDead) {
//				return;
//			}
			setCollisionPosition(percent);
			
			if (d.vertices != vertices)
				return;
			
			// kill both polygons
//			setDead(true);
//			d.setDead(true);
			g.removals.add(this);
			g.removals.add(d);
			
			// create new polygon
			int newVertices = vertices+1;
			Vector newVelocity = Vector.scalar(.75F,Vector.add(velocity,d.velocity));
			g.additions.add(new DynamicPolygon(model,collisionPosition.x,collisionPosition.y,newVelocity.x,newVelocity.y,newVertices));
			
			// create remainder dots
			int numDots = vertices - 2;
			for (int i = 0; i < numDots; i++) {
				float xVel = model.dotMaxVel*model.getRandom().nextFloat() - (model.dotMaxVel/2F);
				float yVel = model.dotMaxVel*model.getRandom().nextFloat() - (model.dotMaxVel/2F);
				g.additions.add(new Dot(model,collisionPosition.x,collisionPosition.y,xVel,yVel,true));
			}
			
			g.additions.add(new Explosion(model,collisionPosition.x,collisionPosition.y,Color.GREEN));
		}
	}
	
	public Dimension getDims() {
		return dims;
	}

}
