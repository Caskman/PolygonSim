package caskman.polygonsim.model.entities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import caskman.polygonsim.MainThread;
import caskman.polygonsim.model.GameContext;
import caskman.polygonsim.model.GameModel;
import caskman.polygonsim.model.Vector;

public class DynamicPolygon extends Mob {
	
	static Dimension dims = new Dimension(30,30);
	int vertices;
	float angle;
	static float tpi = 2*3.14159F;
	static float angleSpeed = tpi/MainThread.FPS;
	static int dotSide = 6;

	public DynamicPolygon(GameModel m,float xPos,float yPos,float xVel,float yVel,int numVertices) {
		super(m);
		position = new Vector(xPos,yPos);
		velocity = new Vector(xVel,yVel);
		vertices = numVertices;
		angle = 0F;
	}

	@Override
	protected void update(GameContext g) {
		angle = ((angle += angleSpeed) > tpi)?angle - tpi:angle;
		
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
		List<Vector> points = new ArrayList<Vector>(vertices);
		
		float pointAngleInterval = tpi/vertices;
		float pointAngle = angle + interpol*angleSpeed;
		int radius = dims.width>>1;
		Vector interpolPosition = Vector.add(position,Vector.scalar(interpol, velocity));
		Vector interpolCenterPosition = Vector.add(interpolPosition,new Vector(dims.width,dims.height));
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
		
		
		
	}

}
