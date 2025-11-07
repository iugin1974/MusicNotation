package scoreWriter;

import java.awt.Graphics;
import java.awt.Rectangle;

public class GraphicalHelper {

	private boolean selected = false;
	private int x = 0;
	private int y = 0;
	private Rectangle bounds = null;
	
	public int getX() {
		return x;
	}

	
	public int getY() {
		return y;
	}

	
	public void moveTo(int x, int y) {
		this.x = x;
		this.y = y;
	}

	
	public void moveBy(int dx, int dy) {
		moveTo(this.x + dx, this.y + dy);
	}

	
	public void setX(int x) {
		this.x = x;		
	}

	
	public void setY(int y) {
		this.y = y;		
	}

	
	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	
	public boolean isSelected() {
		return selected;
	}

	
	public void select(boolean select) {
		selected = select;
	}
	
	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}
	
	public Rectangle getBounds() {
		return bounds;
	}
	
	public boolean contains(int x, int y) {
		return bounds != null && bounds.contains(x, y);
	}

}
