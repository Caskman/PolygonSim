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
//import caskman.polygonsim.model.entities.Line;
import caskman.polygonsim.model.entities.Mob;


public class GameModel {
	
	private Dimension screenDims;
	private Random r;
	private float dotRatio = .0009F;
	public float dotMaxVel = 15;
	private List<Mob> dots;
//	private List<Mob> lines;
	private List<Mob> explosions;
	private List<Mob> polygons;
	private QuadTree q;
	private Vector mousePosition;
	private int[] censusData;
	
	
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
//		lines = new ArrayList<Mob>();
		
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
		censusData = new int[0];
	}
	
	public void update() {
		
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
		
		updateCensus();
		
		
//		if (r.nextFloat() < .1F)
//			explosions.add(new Explosion(this,r.nextFloat()*(float)screenDims.width,r.nextFloat()*(float)screenDims.height));
		
	}
	
	private void updateCensus() {
		censusData = new int[findLargestPolygon()];
		
		for (Mob m : polygons) {
			censusData[((DynamicPolygon)m).getVertexCount() - 1]++;
		}
		censusData[0] = dots.size();
		censusData[1] = lines.size();
	}
	
	private int findLargestPolygon() {
		if (polygons.size() == 0) {
			if (lines.size() == 0)
				return 1;
			return 2;
		}
		int max = 2;
		for (Mob m : polygons) {
			if (((DynamicPolygon)m).getVertexCount() > max)
				max = ((DynamicPolygon)m).getVertexCount();
		}
		return max;
			
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
		
		drawCensusData(g,interpol);
//		q.draw(g);
	}
	
	private void drawCensusData(Graphics2D g, float interpol) {
		g.setColor(Color.WHITE);
		
		
		g.drawString("Total Dots: "+calcTotalDots(), 0, 80);
		for (int i = 0; i < censusData.length; i++) {
			g.drawString(getPolygonName(i+1)+": "+censusData[i], 0, 100 + 20*i);
		}
	}
	
	private int calcTotalDots() {
		int sum = 0;
		for (Mob m : polygons) {
			sum += ((DynamicPolygon)m).getVertexCount();
		}
		sum += dots.size();
		sum += lines.size()<<1;
		return sum;
	}
	
	private String getPolygonName(int n) {
		switch (n) {
		case 1:
			return "Dot";
		case 2:
			return "Line";
		case 3: 
			return "Triangle";
		case 4:
			return "Square";
		case 5:
			return "Pentagon";
		case 6:
			return "Hexagon";
		case 7:
			return "Septagon";
		case 8:
			return "Octagon";
		case 9:
			return "Nonagon";
		case 10:
			return "Dodecagon";
			default:
				return n+"-gon";
		}
	}
}
