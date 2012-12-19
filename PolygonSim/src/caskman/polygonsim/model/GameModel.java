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
//import caskman.polygonsim.model.entities.Line;


public class GameModel {
	
	private Dimension screenDims;
	private Random r;
	private float dotRatio = .0005F;
	public float dotMaxVel = 15;
	private List<Mob> dots;
//	private List<Mob> lines;
	private List<Mob> explosions;
	private List<Mob> polygons;
	private QuadTree q;
	private boolean isMousePressed;
	private List<Mob> allPhysicalEntities;
	private static float SUCTION_RADIUS = 200F;
	private static float MAX_SUCTION_ACCEL = 50F;
	private static float FRICTION_CONSTANT = 20F;
	
	private Vector mousePosition;
	
	
	public GameModel(Dimension screenDims) {
		this.screenDims = screenDims;
		
		initialize();
	}
	
	public Dimension getScreenDims() {
		return screenDims;
	}
	
	public Random getRandom() {
		return r;
	}
	
	private void initialize() {
		r = new Random();
		isMousePressed = false;
		int numBlocks = (int) (screenDims.width*screenDims.height*dotRatio);
		dots = new ArrayList<Mob>(numBlocks);
//		lines = new ArrayList<Mob>();
		
		for (int i = 0; i < numBlocks; i++) {
			float xPos = r.nextFloat()*screenDims.width;
			float yPos = r.nextFloat()*screenDims.height;
//			float xVel = dotMaxVel*r.nextFloat() - dotMaxVel/2.0F;
//			float yVel = dotMaxVel*r.nextFloat() - dotMaxVel/2.0F;
			dots.add(new Dot(this,xPos,yPos,0F,0F,false));
		}
		
		clearCollisions();
		
		mousePosition = new Vector();
		
//		initializeInputListeners(il);
		
		explosions = new ArrayList<Mob>();
		polygons = new ArrayList<Mob>();
		updatePhysicalEntityList();
		q = new QuadTree(screenDims,5);
	}
	
	public void manageInput(InputEvent e) {
		if (e.getType() == InputEvent.MOUSE_PRESSED || e.getType() == InputEvent.MOUSE_DRAGGED) {
			isMousePressed = true;
			mousePosition = e.getVector();
		} else if (e.getType() == InputEvent.MOUSE_RELEASED) {
			isMousePressed = false;
		}
	}
	
	private void processInput() {
		if (isMousePressed) {
			
			applySuctionField(mousePosition);
		}
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
		q = new QuadTree(screenDims,5);
		
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
	
	public void update() {
		
		processInput();
		
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
		for (Mob m : dots) {
			m.drawMob(g,interpol);
		}
//		for (Mob m : lines) {
//			m.drawMob(g, interpol);
//		}
		for (Mob m : polygons) {
			m.drawMob(g, interpol);
		}
		for (Mob m : explosions) {
			m.drawMob(g,interpol);
		}
		drawMouseInput(g,interpol);
//		q.draw(g);
	}
	
	private void drawMouseInput(Graphics2D g,float interpol) {
		g.setColor(Color.WHITE);
		
		g.drawString("Mouse is "+((isMousePressed)?"pressed":"not pressed"), 0, 80);
		g.drawString(mousePosition.toString(),0,100);
	}

//	private void initializeInputListeners(InputListener il) {
//		
//		MouseInputListener ml = new MouseInputListener() {
//		@Override
//		public void mouseClicked(MouseEvent arg0) {
//			
//		}
//		@Override
//		public void mouseEntered(MouseEvent arg0) {
//			
//		}
//		@Override
//		public void mouseExited(MouseEvent arg0) {
//			
//		}
//		@Override
//		public void mousePressed(MouseEvent e) {
//			isMousePressed = true;
//			mousePosition = new Vector(e.getX(),e.getY());
//		}
//		@Override
//		public void mouseReleased(MouseEvent arg0) {
//			isMousePressed = false;
//		}
//		@Override
//		public void mouseDragged(MouseEvent e) {
//			isMousePressed = true;
//			mousePosition = new Vector(e.getX(),e.getY());
//		}
//		@Override
//		public void mouseMoved(MouseEvent arg0) {
//			
//		}
//	};
//		il.addMouseListener(ml);
//		il.addMouseMotionListener(ml);
//	}

}
