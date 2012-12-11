package caskman.polygonsim.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import caskman.polygonsim.model.entities.Dot;


public class GameModel {
	
	private Dimension screenDims;
	private Random r;
	private float dotRatio = .0001F;
	private float dotMaxVel = 30;
	private List<Mob> dots;
	private QuadTree q;
	
	public GameModel(Dimension screenDims) {
		this.screenDims = screenDims;
		
		initialize();
	}
	
	public Dimension getScreenDims() {
		return screenDims;
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
		
		q = new QuadTree(screenDims,4);
	}
	
	public void update() {
		
		updateQuadTree();
		
		for (Mob m : dots) {
			m.update(null);
		}
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
		q.draw(g);
	}
}
