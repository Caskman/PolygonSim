package caskman.polygonsim.model;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;


/*
 * currently configured to work with squares, but not so much rectangles
 */

public class QuadTree {
	
	private List<Collidable> stuff;
	private List<QuadTree> nodes;
	private int level;
	private Rectangle bounds;
	private int MAX_LEVELS;
	private int MAX_OBJECTS = 10;
	
	public QuadTree(Dimension screenDims,int maxLevels) {
//		int dimTarget = ((largest.width < largest.height)?largest.height:largest.width)<<3;
//		int screenDim = (screenDims.height < screenDims.width)?screenDims.height:screenDims.width;
//		int i;
//		for (i = 0; screenDim > dimTarget; i++) {
//			screenDim = screenDim >> 1;
//		}
		MAX_LEVELS = maxLevels;
		initialize(0,new Rectangle(0,0,screenDims.width,screenDims.height));
	}
	
	private QuadTree(int level,Rectangle bounds,int maxLevels) {
		MAX_LEVELS = maxLevels;
		initialize(level,bounds);
	}
	
	private void initialize(int level,Rectangle bounds) {
		this.level = level;
		this.bounds = bounds;
		stuff = new ArrayList<Collidable>();
		nodes = new ArrayList<QuadTree>(4);
		for (int i = 0; i < 4; i++) {
			nodes.add(null);
		}
	}
	
	public void draw(Graphics g) {
		g.setColor(Color.BLUE);
		g.drawRect(bounds.left(),bounds.top(),bounds.width()-1,bounds.height()-1);
		if (nodes.get(0) != null) {
			for (int i = 0; i < 4; i++) {
				nodes.get(i).draw(g);
			}
		}
	}
	
	public void clear() {
		stuff.clear();
		for (int i = 0; i < 4; i++) {
			if (nodes.get(i) != null) {
				nodes.get(i).clear();
				nodes.set(i,null);
			}
		}
	}
	
	private void split() {
		int subWidth = bounds.width()>>1;
		int subHeight = bounds.height()>>1;
		int x = bounds.left();
		int y = bounds.top();
		nodes.set(0,new QuadTree(level + 1,new Rectangle(x+subWidth,y,subWidth,subHeight),MAX_LEVELS));
		nodes.set(1,new QuadTree(level + 1,new Rectangle(x,y,subWidth,subHeight),MAX_LEVELS));
		nodes.set(2,new QuadTree(level + 1,new Rectangle(x,y+subHeight,subWidth,subHeight),MAX_LEVELS));
		nodes.set(3,new QuadTree(level + 1,new Rectangle(x+subWidth,y+subHeight,subWidth,subHeight),MAX_LEVELS));
	}
	
	private int getIndex(Collidable c) {
		Rectangle r = c.getAABB();
		int index = -1;
		
		boolean topQuadrant = r.top() >= bounds.top() && r.bottom() <= bounds.centerY();
		boolean bottomQuadrant = r.top() >= bounds.centerY() && r.bottom() <= bounds.bottom();
		boolean leftQuadrant = r.left() >= bounds.left() && r.right() <= bounds.centerX();
		boolean rightQuadrant = r.left() >= bounds.centerX() && r.right() <= bounds.right();
		
		
		if (leftQuadrant) {
			if (topQuadrant) {
				index = 1;
			} else if (bottomQuadrant) {
				index = 2;
			}
		} else if (rightQuadrant) {
			if (topQuadrant) {
				index = 0;
			} else if (bottomQuadrant) {
				index = 3;
			}
		}
		return index;
	}
	
	public void insert(Collidable c) {
		if (nodes.get(0) != null) {
			int index = getIndex(c);
			if (index != -1) {
				nodes.get(index).insert(c);
				return;
			}
		}
		stuff.add(c);
		if (stuff.size() > MAX_OBJECTS && level < MAX_LEVELS) {
			if (nodes.get(0) == null) {
				split();
			}
			int i = 0;
			while (i < stuff.size()) {
				int index = getIndex(stuff.get(i));
				if (index != -1) {
					nodes.get(index).insert(stuff.remove(i));
				} else {
					i++;
				}
			}
		}
	}
	
	public List<Collidable> retrieve(List<Collidable> returnStuff,Collidable c) {
		int index = getIndex(c);
		if (index != -1 && nodes.get(0) != null) {
			nodes.get(index).retrieve(returnStuff, c);
		}
		returnStuff.addAll(stuff);
		return returnStuff;
	}
	
}