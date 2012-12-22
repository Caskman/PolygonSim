package caskman.polygonsim.model;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import caskman.polygonsim.model.entities.Dot;
import caskman.polygonsim.model.entities.DynamicPolygon;
import caskman.polygonsim.model.entities.Explosion;
import caskman.polygonsim.model.entities.Mob;
import caskman.polygonsim.screens.InputEvent;
import caskman.polygonsim.screens.OptionsScreen;
import caskman.polygonsim.screens.ScreenManager;
import caskman.polygonsim.Parameters;


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
	private static float GRAVITY_INCREASE = 50F;
	private static float gravityRadius = 200F;
	private static float MAX_SUCTION_ACCEL = 50F;
	private static float FRICTION_CONSTANT = 20F;
	private static int MAP_MOVEMENT_SPEED = 15;
	private static int BACKGROUND_SPACING = 100;
	private static float BACKGROUND_OPACITY = 1F;
	private static float BACKGROUND_DEPTH = .6F;
	private static int NUM_GRAVITY_LINES = 6;
	private Vector cameraPosition;
	private Vector cameraVelocity;
	private Vector mousePosition;
	private Vector rightPressPosition;
	private Vector wheelPressPosition;
	private boolean leftMousePressed;
	private boolean mouseWheelPressed;
	private boolean rightMousePressed;
	private boolean isPaused;
	private ScreenManager manager;
	private float gravityAngle;
	private float gravityAngleSpeed;
	private final static float tpi = (float) (2*Math.PI);
	private Vector previousMousePosition;
	private boolean keyTyped;
	private char keyTypedChar;
	private boolean gravityDirection;
	private int gravityStrength;
	
	
	public GameModel(ScreenManager manager,Dimension screenDims) {
		this.screenDims = screenDims;
		this.manager = manager;
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
		keyTyped = false;
		keyTypedChar = 0;
		gravityStrength = 1;
		gravityDirection = true;
		gravityAngle = 0F;
		gravityAngleSpeed = tpi/256F;
		previousMousePosition = new Vector();
		r = new Random();
		leftMousePressed = false;
		mouseWheelPressed = false;
		rightMousePressed = false;
		isPaused = false;
//		int numBlocks = (int) (screenDims.width*screenDims.height*dotRatio);
		int numBlocks = 1000;
//		System.out.println(numBlocks);
		dots = new ArrayList<Mob>(numBlocks);
		mapDims = new Dimension(3000,3000);
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
		cameraPosition = new Vector((mapDims.width - screenDims.width)/2,(mapDims.height - screenDims.height)/2);
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
			
			previousMousePosition = mousePosition;
			mousePosition = e.getVector();
		} else if (e.isKeyInput()) {
			if (e.getType() == InputEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				manager.addScreen(new OptionsScreen(manager,false,this));
				isPaused = true;
			} else if (e.getType() == InputEvent.KEY_TYPED) {
				keyTyped = true;
				keyTypedChar = e.getKeyChar();
			}
		}
	}
	
	public void setPaused(boolean b) {
		isPaused = b;
	}
	
	private void processInput() {
		if (leftMousePressed) {
			applyGravityField(calcMapPosition(mousePosition));
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
		if (keyTyped) {
			keyTyped = false;
			switch (keyTypedChar) {
			case '`':
			case '~':
				gravityDirection = !gravityDirection;
				break;
			case '1':
				gravityStrength = 1;
				break;
			case '2':
				gravityStrength = 2;
				break;
			case '3':
				gravityStrength = 3;
				break;
			case '4':
				gravityStrength = 4;
				break;
			case '5':
				gravityStrength = 5;
				break;
			case '6':
				gravityStrength = 6;
				break;
			case '7':
				gravityStrength = 7;
				break;
			case '8':
				gravityStrength = 8;
				break;
			case '9':
				gravityStrength = 9;
				break;
			case '0':
				gravityStrength = 10;
				break;
			}
			gravityRadius = GRAVITY_INCREASE*gravityStrength;
		}
	}
	
	private Vector predictMousePosition(float interpol) {
		return Vector.add(Vector.scalar(interpol,Vector.subtract(previousMousePosition, mousePosition)),mousePosition);
	}
	
	private Vector calcMapPosition(Vector v) {
		return Vector.add(v,cameraPosition);
	}
	
	private void applyGravityField(Vector source) {
		List<Mob> affectedEntities = new ArrayList<Mob>();
		List<Float> mags = new ArrayList<Float>();
		List<Vector> disps = new ArrayList<Vector>();
		float mag;
		Vector disp;
		
		for (Mob m : allPhysicalEntities) {
			disp = Vector.displacement( m.getPosition(),source);
			mag = Vector.mag(disp);
			if (mag < gravityRadius) {
				affectedEntities.add(m);
				mags.add(mag);
				disps.add(disp);
			}
		}
		
		for (int i = 0; i < affectedEntities.size(); i++) {
			mag = mags.get(i);
			affectedEntities.get(i).applyAccel(Vector.scalar(((gravityDirection)?1F:-1F)*MAX_SUCTION_ACCEL/(mag),Vector.normalize(disps.get(i))));
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
	
	private void updateGravityAnimation() {
		gravityAngle = gravityAngle + gravityAngleSpeed;
		if (gravityAngle < 0 && gravityAngle < (0-tpi)) {
			gravityAngle += tpi;
		} else if (gravityAngle >= 0 && gravityAngle > tpi) {
			gravityAngle += (0-tpi);
		}
	}
	
	public void update() {
		if (isPaused)
			return;
		
		processInput();
		
		updateCamera();
		
		updateGravityAnimation();
		
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
	
	private void drawBackgroundPattern(Graphics2D g, float interpol, Vector offset) {
		Vector position = Vector.scalar(BACKGROUND_DEPTH,Vector.subtract(new Vector(), offset));
		Composite old = g.getComposite();
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, BACKGROUND_OPACITY));
		// draw vertical lines
		g.setColor(Parameters.BACKGROUND_COLOR);
		for (int mark = BACKGROUND_SPACING - ((int)position.x%BACKGROUND_SPACING); mark < screenDims.width; mark += BACKGROUND_SPACING) {
			g.drawLine(mark, 0, mark, screenDims.height-1);
		}
		
		// draw horizontal lines
		for (int mark = BACKGROUND_SPACING - ((int)position.y%BACKGROUND_SPACING); mark < screenDims.height; mark += BACKGROUND_SPACING) {
			g.drawLine(0, mark, screenDims.width-1, mark);
		}
		
		g.setComposite(old);
	}
	
	public void draw(Graphics2D g,float interpol) {
		if (isPaused)
			interpol = 0F;
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, screenDims.width, screenDims.height);
		
		
		Vector offset = calcMapScreenOffset(interpol);
		
		drawBackgroundPattern(g,interpol,offset);
		
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
		drawGravityField(g,interpol);
//		drawMouseInput(g,interpol);
//		q.draw(g);
	}
	
	private void drawGravityField(Graphics2D g,float interpol) {
		if (!leftMousePressed)
			return;
		Vector drawPosition = mousePosition;
		
		g.setColor(Color.WHITE);
		g.drawArc((int)(drawPosition.x - gravityRadius), (int)(drawPosition.y - gravityRadius), (int)gravityRadius*2, (int)gravityRadius*2, 0, 360);
		Composite old = g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,.1F));
		g.fillArc((int)(drawPosition.x - gravityRadius), (int)(drawPosition.y - gravityRadius), (int)gravityRadius*2, (int)gravityRadius*2, 0, 360);
		g.setComposite(old);
//		float interpolGravityAngle = gravityAngle + interpol*gravityAngleSpeed;
//		Vector[] points = new Vector[NUM_GRAVITY_LINES*2];
//		
//		float angleInterval = tpi/points.length;
//		int i = 0;
//		for (float point = interpolGravityAngle; i < points.length; point += angleInterval,i++) {
//			points[i] = new Vector(GRAVITY_RADIUS*(float)Math.cos(point),GRAVITY_RADIUS*(float)Math.sin(point));
//		}
//		
//		for (Vector point : points) {
//			g.drawLine((int)(point.x + drawPosition.x), (int)(point.y + drawPosition.y),(int) drawPosition.x,(int) drawPosition.y);
//		}
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
