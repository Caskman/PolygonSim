package caskman.polygonsim.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import caskman.polygonsim.RenderObject;
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
	
//	private Dimension getTextBounds(Graphics2D g,String s) {
//		FontMetrics m = g.getFontMetrics();
//		Dimension d = new Dimension(m.stringWidth(s) + 2,m.getHeight()+2);
//		return d;
//		Rect r = new Rect();
//		textColor.getTextBounds(text, 0, text.length(), r);
//		return r;
//	}
	
//	private Vector getStandardPosition(Graphics2D g,String s) {
//		FontMetrics m = g.getFontMetrics();
//		Vector space = new Vector(m.stringWidth(s) + 2,m.getHeight()+2);
//		Vector offset = Vector.scalar(.5F,space);
//		return Vector.subtract(centerPosition,offset);
//	}
	
	public void setTextSize(int size) {
		textSize = size;
	}
	
	private void initializePosition(Graphics2D g) {
		g.setFont(new Font(Font.SERIF,Font.PLAIN,textSize));
		FontMetrics m = g.getFontMetrics();
		dims = new Dimension(m.stringWidth(text) + 2,m.getHeight()+2);
		position = new Vector(centerPosition.x - dims.width/2,centerPosition.y - dims.height/2);
	}
	
//	public void draw(Graphics2D g,float interpol) {
////		Vector position = getStandardPosition(g,text);
//		if (position == null)
//			initializePosition(g);
//		if (isButton) {
////			Paint paint = new Paint();
////			paint.setColor(Color.BLACK);
//			g.setColor(Color.BLACK);
//			g.fillRect((int)position.x, (int)position.y, (int)(dims.width), (int)(dims.height));
//			g.setColor(borderColor);
//			g.drawRect((int)position.x, (int)position.y, (int)(dims.width), (int)(dims.height));
//		}
////		Rect rect = new Rect();
////		textColor.getTextBounds(text, 0, text.length(), rect);
////		Dimension textDims = getTextBounds(g,text);
////		float startOffsetX = (dims.width - textDims.width) / 2.0F;
////		float startOffsetY = dims.height - ((dims.height - textDims.height) / 2.0F);
//		g.setColor(textColor);
//		g.setFont(new Font(Font.SERIF,Font.PLAIN,textSize));
//		g.drawString(text,position.x + dims.width/32,position.y+3*dims.height/4);
//	}
	
	public void manageInput(InputEvent e) {
		if (e.getType() != InputEvent.MOUSE_CLICKED)
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
	
	public void getRenderObjects(List<RenderObject> renderList) {
		renderList.add(new RenderMenuObject(isButton,textColor,textSize,text,centerPosition));
	}
	
	private class RenderMenuObject extends RenderObject {

		private boolean _isButton;
		private Color _textColor;
		private int _textSize;
		private String _text;
		private Vector _centerPosition;
		
		public RenderMenuObject(boolean isButton, Color textColor,
				int textSize, String text, Vector centerPosition) {
			_isButton = isButton;
			_textColor = textColor;
			_textSize = textSize;
			_text = text;
			_centerPosition = centerPosition;
		}

		@Override
		public void render(Graphics2D g, float interpol) {
			Vector position;
			Dimension dims;
			
			g.setFont(new Font(Font.SERIF,Font.PLAIN,_textSize));
			FontMetrics m = g.getFontMetrics();
			dims = new Dimension(m.stringWidth(_text) + 2,m.getHeight()+2);
			position = new Vector(_centerPosition.x - dims.width/2,_centerPosition.y - dims.height/2);
			MenuItem.this.position = position;
			MenuItem.this.dims = dims;
			
			if (_isButton) {
				g.setColor(Color.BLACK);
				g.fillRect((int)position.x, (int)position.y, (int)(dims.width), (int)(dims.height));
				g.setColor(borderColor);
				g.drawRect((int)position.x, (int)position.y, (int)(dims.width), (int)(dims.height));
			}
			g.setColor(_textColor);
			g.drawString(_text,position.x + dims.width/32,position.y+3*dims.height/4);
		}
	}
}
