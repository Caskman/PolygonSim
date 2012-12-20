package caskman.polygonsim.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import caskman.polygonsim.model.entities.Dot;
import caskman.polygonsim.model.entities.DynamicPolygon;
import caskman.polygonsim.model.entities.Explosion;
import caskman.polygonsim.model.entities.Mob;
import caskman.polygonsim.screens.InputEvent;
import caskman.polygonsim.screens.InputListener;


public class GameModel {
	
	private Dimension screenDims;
	private Dimension mapDims;
	private Random r;
	private float dotRatio = .0005F;
	public float dotMaxVel = 15;
	private List<Mob> dots;
//	private List<Mob> lines;
	private List<Mob> explosions;
	private List<Mob> polygons;
	private QuadTree q;
	private List<Mob> allPhysicalEntities;
	private static float SUCTION_RADIUS = 200F;
	private static float MAX_SUCTION_ACCEL = 50F;
	private static float FRICTION_CONSTANT = 20F;
	private static int MAP_MOVEMENT_SPEED = 15;
	private Vector cameraPosition;
	private Vector cameraVelocity;
	private Vector mousePosition;
	private Vector rightPressPosition;
	private Vector wheelPressPosition;
	private boolean leftMousePressed;
	private boolean mouseWheelPressed;
	private boolean rightMousePressed;
	
	
	public GameModel(Dimension screenDims) {
		this.screenDims = screenDims;
		
		initialize();
	}
	
	public Dimension getScreenDims() {
		return screenDims;
	}
	
	public Dimension getMapDims() {
		return mapDims;
	}
	
	public Random getRandom() {
		return r;
	}
	
	private void initialize() {
		r = new Random();
		leftMousePressed = false;
		mouseWheelPressed = false;
		rightMousePressed = false;
		int numBlocks = (int) (screenDims.width*screenDims.height*dotRatio);
		dots = new ArrayList<Mob>(numBlocks);
		mapDims = new Dimension(2000,2000);
//		lines = new ArrayList<Mob>();
		
		for (int i = 0; i < numBlocks; i++) {
			float xPos = r.nextFloat()*mapDims.width;
			float yPos = r.nextFloat()*mapDims.height;
//			float xVel = dotMaxVel*r.nextFloat() - dotMaxVel/2.0F;
//			float yVel = dotMaxVel*r.nextFloat() - dotMaxVel/2.0F;
			dots.add(new Dot(this,xPos,yPos,0F,0F,false));
		}
		
		clearCollisions();
		
		
		mousePosition = new Vector();
		rightPressPosition = new Vector();
		wheelPressPosition = new Vector();
		
//		initializeInputListeners(il);
		
		explosions = new ArrayList<Mob>();
		polygons = new ArrayList<Mob>();
		updatePhysicalEntityList();
		q = new QuadTree(mapDims,5);
		cameraPosition = new Vector();
		cameraVelocity = new Vector();
	}
	
	public void manageInput(InputEvent e) {
		if (e.isMouseInput()) {
			switch (e.getType()) {
			case InputEvent.MOUSE_PRESSED:
				switch (e.getButton()) {
				case 0:
					break;
				case 1:
					leftMousePressed = true;
					break;
				case 2:
					mouseWheelPressed = true;
					wheelPressPosition = e.getVector();
					break;
				case 3:
					rightMousePressed = true;
					rightPressPosition = calcMapPosition(e.getVector());
					break;
				}
				break;
			case InputEvent.MOUSE_RELEASED:
				switch (e.getButton()) {
				case 0:
					break;
				case 1:
					leftMousePressed = false;
					break;
				case 2:
					mouseWheelPressed = false;
					break;
				case 3:
					rightMousePressed = false;
					break;
				}
			case InputEvent.MOUSE_CLICKED:
			case InputEvent.MOUSE_DRAGGED:
			case InputEvent.MOUSE_ENTERED:
			case InputEvent.MOUSE_EXITED:
			case InputEvent.MOUSE_MOVED:
			}
			
			
			mousePosition = e.getVector();
		}
	}
	
	private void processInput() {
		if (leftMousePressed) {
			applySuctionField(calcMapPosition(mousePosition));
		}
		if (rightMousePressed) {
			Vector displacement = Vector.displacement(calcMapPosition(mousePosition),rightPressPosition);
			cameraVelocity = Vector.scalar(.5F,displacement);
		} else if (mouseWheelPressed)  {
			Vector displacement = Vector.displacement(wheelPressPosition,mousePosition);
			cameraVelocity = Vector.scalar(.1F,displacement);
		} else {
			if (mousePosition.x == 0) {
				cameraVelocity = Vector.add(new Vector(0-MAP_MOVEMENT_SPEED,0),cameraVelocity);
			}
			if (mousePosition.x == screenDims.width - 1) {
				cameraVelocity = Vector.add(new Vector(MAP_MOVEMENT_SPEED,0),cameraVelocity);
			}
			if (mousePosition.y == 0) {
				cameraVelocity = Vector.add(new Vector(0,0-MAP_MOVEMENT_SPEED),cameraVelocity);
			}
			if (mousePosition.y == screenDims.height - 1) {
				cameraVelocity = Vector.add(new Vector(0,MAP_MOVEMENT_SPEED),cameraVelocity);
			}
		}
	}
	
	private Vector calcMapPosition(Vector v) {
		return Vector.add(v,cameraPosition);
	}
	
	private void applySuctionField(Vector source) {
		List<Mob> affectedEntities = new ArrayList<Mob>();
		List<Float> mags = new ArrayList<Float>();
		List<Vector> disps = new ArrayList<Vector>();
		float mag;
		Vector disp;
		
		for (Mob m : allPhysicalEntities) {
			disp = Vector.displacement( m.getPosition(),source);
			mag = Vector.mag(disp);
			if (mag < SUCTION_RADIUS) {
				affectedEntities.add(m);
				mags.add(mag);
				disps.add(disp);
			}
		}
		
		for (int i = 0; i < affectedEntities.size(); i++) {
			mag = mags.get(i);
			affectedEntities.get(i).applyAccel(Vector.scalar(MAX_SUCTION_ACCEL/(mag),Vector.normalize(disps.get(i))));
		}
	}
	
	private void clearCollisions() {
		q = new QuadTree(mapDims,5);
		
		for (Mob m : dots) {
			q.insert((Collidable)m);
		}
		
		List<Mob> removals = new ArrayList<Mob>();
		for (Mob m : dots) {
			if (removals.contains(m))
				continue;
			List<Collidable> possibles = q.retrieve(new ArrayList<Collidable>(), (Collidable)m);
			for (Collidable c : possibles) {
				if (Collisions.detectCollision((Collidable)m, c) != -1F) {
					removals.add(m);
					break;
				}
			}
		}
		
		for (Mob m : removals) {
			dots.remove(m);
		}
	}
	
	private void updateCamera() {
		Vector temp = Vector.add(cameraPosition,cameraVelocity);
		if (temp.x < 0 || temp.x + screenDims.width > mapDims.width) {
			cameraVelocity.x = 0F;
			temp.x = (temp.x < 0)?0:mapDims.width - screenDims.width;
		}
		if (temp.y < 0 || temp.y + screenDims.height > mapDims.height) {
			cameraVelocity.y = 0F;
			temp.y = (temp.y < 0)?0:mapDims.height - screenDims.height;
		}
		cameraPosition = temp;
		cameraVelocity = Vector.scalar(.75F,cameraVelocity);
	}
	
	public void update() {
		
		processInput();
		
		updateCamera();
		
		updateQuadTree();
		
		GameContext g = getGameContext();
		
		// reset all resolved flags
		for (Mob m : dots) {
			((Collidable)m).setResolved(false);
		}
//		for (Mob m : lines) {
//			((Collidable)m).setResolved(false);
//		}
		for (Mob m : polygons) {
			((Collidable)m).setResolved(false);
		}
		
		updateEntities(g);
		
		resolvesEntityAdditionsAndRemovals(g);
		
		updatePhysicalEntityList();
		
		applyFriction();
		
		
//		if (r.nextFloat() < .1F)
//			explosions.add(new Explosion(this,r.nextFloat()*(float)screenDims.width,r.nextFloat()*(float)screenDims.height,Color.WHITE));
		
	}
	
	private void updatePhysicalEntityList() {
		allPhysicalEntities = new ArrayList<Mob>();
		
		for (Mob m : dots)
			allPhysicalEntities.add(m);
		for (Mob m : polygons)
			allPhysicalEntities.add(m);
	}
	
	private void applyFriction() {
		for (Mob m : allPhysicalEntities) {
			Vector accel = Vector.scalar(-1F/FRICTION_CONSTANT,m.getVelocity());
//			Vector directionOfFriction = Vector.normalize(Vector.scalar(-1F,m.getVelocity()));
			m.applyAccel(accel);
		}
	}
	
	private void resolvesEntityAdditionsAndRemovals(GameContext g) {
		for (Mob m : g.removals) {
			if (m instanceof Dot)
				dots.remove(m);
			else if (m instanceof Explosion)
				explosions.remove(m);
//			else if (m instanceof Line)
//				lines.remove(m);
			else if (m instanceof DynamicPolygon)
				polygons.remove(m);
		}
		
		for (Mob m : g.additions) {
//			if (m instanceof Line)
//				lines.add(m);
			if (m instanceof Explosion)
				explosions.add(m);
			else if (m instanceof Dot)
				dots.add(m);
			else if (m instanceof DynamicPolygon)
				polygons.add(m);
		}
	}
	
	private void updateEntities(GameContext g) {
		// UPDATE ALL ENTITIES
		for (Mob m : dots) {
			m.updateMob(g);
		}
//		for (Mob m : lines) {
//			m.updateMob(g);
//		}
		for (Mob m : polygons) {
			m.updateMob(g);
		}
		for (Mob m : explosions) {
			m.updateMob(g);
		}
	}
	
	private GameContext getGameContext() {
		GameContext g = new GameContext();
		g.additions = new ArrayList<Mob>();
		g.removals = new ArrayList<Mob>();
		g.quadTree = q;
		return g;
	}
	
	private void updateQuadTree() {
		q.clear();
		for (Mob m : dots) {
			q.insert((Collidable)m);
		}
//		for (Mob m : lines) {
//			q.insert((Collidable)m);
//		}
		for (Mob m : polygons) {
			q.insert((Collidable)m);
		}
		
	}
	
	public void draw(Graphics2D g,float interpol) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, screenDims.width, screenDims.height);
		Vector offset = calcMapScreenOffset(interpol);
		for (Mob m : dots) {
			if (isWithinScreen(m))
				m.drawMob(g,interpol,offset);
		}
		for (Mob m : polygons) {
			if (isWithinScreen(m))
				m.drawMob(g,interpol,offset);
		}
		for (Mob m : explosions) {
			if (isWithinScreen(m))
				m.drawMob(g,interpol,offset);
		}
		drawMouseInput(g,interpol);
//		q.draw(g);
	}
	
	private boolean isWithinScreen(Mob m) {
		if (cameraPosition.x < m.getX() + m.getDims().width && cameraPosition.x + screenDims.width > m.getX()
				&& cameraPosition.y < m.getY() + m.getDims().height && cameraPosition.y + screenDims.height > m.getY())
			return true;
		else return false;
	}
	
	private Vector calcMapScreenOffset(float interpol) {
		Vector interpolCamPos = Vector.add(cameraPosition,Vector.scalar(interpol,cameraVelocity));
		
		if (interpolCamPos.x < 0)
			interpolCamPos.x = 0;
		else if (interpolCamPos.x > mapDims.width - screenDims.width)
			interpolCamPos.x = mapDims.width - screenDims.width;
		if (interpolCamPos.y < 0)
			interpolCamPos.y = 0;
		else if (interpolCamPos.y > mapDims.height - screenDims.height)
			interpolCamPos.y = mapDims.height - screenDims.height;
		
		return Vector.subtract(new Vector(),interpolCamPos);
	}
	
	private void drawMouseInput(Graphics2D g,float interpol) {
		g.setColor(Color.WHITE);
		
		g.drawString("Mouse is "+((leftMousePressed)?"pressed":"not pressed"), 0, 80);
		g.drawString("Mouse wheel is "+((mouseWheelPressed)?"pressed.":"not pressed."),0,100);
		g.drawString("Mouse Press Position is "+rightPressPosition.toString(),0,120);
		g.drawString("Screen Position: "+mousePosition.toString(),0,140);
		g.drawString("Map Position: "+calcMapPosition(mousePosition),0,160);
	}


}
