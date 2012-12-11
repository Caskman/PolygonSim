package caskman.polygonsim.model;

public class Rectangle {
	private int width;
	private int height;
	private int x;
	private int y;
	
	public Rectangle(int x,int y, int width,int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public int top() {
		return y;
	}
	
	public int left() {
		return x;
	}
	
	public int bottom() {
		return y + height;
	}
	
	public int right() {
		return x + width;
	}
	
	public int centerX() {
		return x + (width>>1);
	}
	
	public int centerY() {
		return y + (height>>1);
	}
	
	public int width() {
		return width;
	}
	
	public int height() {
		return height;
	}
}
