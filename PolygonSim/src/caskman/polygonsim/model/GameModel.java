package caskman.polygonsim.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import caskman.polygonsim.model.entities.Dot;
import caskman.polygonsim.model.entities.Explosion;


public class GameModel {
	
	private Dimension screenDims;
	private Random r;
	private float dotRatio = .0001F;
	private float dotMaxVel = 20;
	private List<Mob> dots;
	private List<Mob> explosions;
	private QuadTree q;
	
	
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
		int numBlocks = (int) (screenDims.width*screenDims.height*dotRatio);
		dots = new ArrayList<Mob>(numBlocks);
		
		for (int i = 0; i < numBlocks; i++) {
			float xPos = r.nextFloat()*screenDims.width;
			float yPos = r.nextFloat()*screenDims.height;
			float xVel = dotMaxVel*r.nextFloat() - dotMaxVel/2.0F;
			float yVel = dotMaxVel*r.nextFloat() - dotMaxVel/2.0F;
			dots.add(new Dot(this,xPos,yPos,xVel,yVel));
		}
		
		explosions = new ArrayList<Mob>();
		q = new QuadTree(screenDims,5);
	}
	
	public void update() {
		
		updateQuadTree();
		
		GameContext g = getGameContext();
		
		for (Mob m : dots) {
			m.update(g);
		}
		for (Mob m : explosions) {
			m.update(g);
		}
		
		for (Mob m : g.removals) {
			explosions.remove(m);
		}
		
		if (r.nextFloat() < .1F)
			explosions.add(new Explosion(this,r.nextFloat()*(float)screenDims.width,r.nextFloat()*(float)screenDims.height));
		
	}
	
	private GameContext getGameContext() {
		GameContext g = new GameContext();
		g.removals = new ArrayList<Mob>();
		return g;
	}
	
	private void updateQuadTree() {
		q.clear();
		for (Mob m : dots) {
			q.insert((Collidable)m);
		}
		
	}
	
	public void draw(Graphics g,float interpol) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, screenDims.width, screenDims.height);
		for (Mob m : dots) {
			m.draw(g,interpol);
		}
		for (Mob m : explosions) {
			m.draw(g,interpol);
		}
		q.draw(g);
	}
}
