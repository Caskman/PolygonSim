package caskman.polygonsim.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import caskman.polygonsim.model.Vector;


public class MenuItem {
	
	Vector position;
	Vector centerPosition;
	Dimension dims;
	Color borderColor;
	Color textColor;
	String text;
	boolean isButton;
	private List<MenuItemListener> listeners;
	int textSize;
	
	public MenuItem() {
		listeners = new ArrayList<MenuItemListener>();
		borderColor = Color.WHITE;
//		borderColor.setStyle(Style.STROKE);
//		borderColor.setColor(Color.WHITE);
		textColor = Color.WHITE;
//		textColor.setColor(Color.WHITE);
//		textColor.setAntiAlias(true);
		isButton = false;
		textSize = 12;
	}
	
	public void update() {
		
	}
	
	public void setTextColor(Color color) {
		textColor = color;
	}
	
	public void setTextSize(int size) {
		textSize = size;
	}
	
	private void initializePosition(Graphics2D g) {
//		g.setFont(new Font(Font.SERIF,Font.PLAIN,textSize));
		FontMetrics m = g.getFontMetrics();
		dims = new Dimension(m.stringWidth(text) + 2,m.getHeight()+2);
		position = new Vector(centerPosition.x - dims.width/2,centerPosition.y - dims.height/2);
	}
	
	public void draw(Graphics2D g,float interpol) {
		g.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,textSize));
		if (position == null)
			initializePosition(g);
		if (isButton) {
			g.setColor(Color.BLACK);
			g.fillRect((int)position.x, (int)position.y, (int)(dims.width), (int)(dims.height));
			g.setColor(borderColor);
			g.drawRect((int)position.x, (int)position.y, (int)(dims.width), (int)(dims.height));
		}
		g.setColor(textColor);
		g.drawString(text,position.x + dims.width/32,position.y+3*dims.height/4);
	}
	
	public void manageInput(InputEvent e) {
		if (e.getType() != InputEvent.MOUSE_PRESSED)
			return;
		Vector v = e.getVector();
		if (v.x > position.x && v.x < (position.x+dims.width) && v.y > position.y && v.y < (position.y + dims.height)) {
			notifyListeners();
		}
	}
	
	public void addMenuItemListener(MenuItemListener listener) {
		listeners.add(listener);
	}
	
	private void notifyListeners() {
		for (MenuItemListener l : listeners) {
			l.itemActivated();
		}
	}
}
