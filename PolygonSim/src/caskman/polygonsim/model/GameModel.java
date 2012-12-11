package caskman.polygonsim.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import caskman.polygonsim.InputListener;
import caskman.polygonsim.model.entities.Dot;
import caskman.polygonsim.model.entities.DynamicPolygon;
import caskman.polygonsim.model.entities.Explosion;
import caskman.polygonsim.model.entities.Line;
import caskman.polygonsim.model.entities.Mob;


public class GameModel {
	
	private Dimension screenDims;
	private Random r;
	private float dotRatio = .0005F;
	public float dotMaxVel = 15;
	private List<Mob> dots;
	private List<Mob> lines;
	private List<Mob> explosions;
	private List<Mob> polygons;
	private QuadTree q;
	private Vector mousePosition;
	
	
	public GameModel(Dimension screenDims,InputListener il) {
		this.screenDims = screenDims;
		
		initialize(il);
	}
	
	public Dimension getScreenDims() {
		return screenDims;
	}
	
	public Random getRandom() {
		return r;
	}
	
	private void initialize(InputListener il) {
		r = new Random();
		int numBlocks = (int) (screenDims.width*screenDims.height*dotRatio);
		dots = new ArrayList<Mob>(numBlocks);
		lines = new ArrayList<Mob>();
		
		for (int i = 0; i < numBlocks; i++) {
			float xPos = r.nextFloat()*screenDims.width;
			float yPos = r.nextFloat()*screenDims.height;
			float xVel = dotMaxVel*r.nextFloat() - dotMaxVel/2.0F;
			float yVel = dotMaxVel*r.nextFloat() - dotMaxVel/2.0F;
			dots.add(new Dot(this,xPos,yPos,xVel,yVel,false));
		}
		
		mousePosition = new Vector();
		
		il.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent arg0) {
			}
			public void mouseMoved(MouseEvent e) {
				mousePosition = new Vector(e.getX(),e.getY());
			}
		});
		
		explosions = new ArrayList<Mob>();
		polygons = new ArrayList<Mob>();
		q = new QuadTree(screenDims,5);
	}
	
	public void update() {
		
		updateQuadTree();
		
		GameContext g = getGameContext();
		
		// reset all resolved flags
		for (Mob m : dots) {
			((Collidable)m).setResolved(false);
		}
		for (Mob m : lines) {
			((Collidable)m).setResolved(false);
		}
		for (Mob m : polygons) {
			((Collidable)m).setResolved(false);
		}
		
		
		// UPDATE ALL ENTITIES
		for (Mob m : dots) {
			m.updateMob(g);
		}
		for (Mob m : lines) {
			m.updateMob(g);
		}
		for (Mob m : polygons) {
			m.updateMob(g);
		}
		for (Mob m : explosions) {
			m.updateMob(g);
		}
		
		for (Mob m : g.removals) {
			if (m instanceof Dot)
				dots.remove(m);
			else if (m instanceof Explosion)
				explosions.remove(m);
			else if (m instanceof Line)
				lines.remove(m);
			else if (m instanceof DynamicPolygon)
				polygons.remove(m);
		}
		
		for (Mob m : g.additions) {
			if (m instanceof Line)
				lines.add(m);
			else if (m instanceof Explosion)
				explosions.add(m);
			else if (m instanceof Dot)
				dots.add(m);
			else if (m instanceof DynamicPolygon)
				polygons.add(m);
		}
		
		
//		if (r.nextFloat() < .1F)
//			explosions.add(new Explosion(this,r.nextFloat()*(float)screenDims.width,r.nextFloat()*(float)screenDims.height));
		
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
		for (Mob m : lines) {
			q.insert((Collidable)m);
		}
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
		for (Mob m : lines) {
			m.drawMob(g, interpol);
		}
		for (Mob m : polygons) {
			m.drawMob(g, interpol);
		}
		for (Mob m : explosions) {
			m.drawMob(g,interpol);
		}
//		q.draw(g);
	}
}
