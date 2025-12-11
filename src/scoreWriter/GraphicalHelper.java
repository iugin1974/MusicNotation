package scoreWriter;

import java.awt.Color;
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
	
	public void drawBounds(Graphics g) {
	    if (bounds == null) {
	        return;
	    }

	    Color old = g.getColor();
	    g.setColor(selected ? Color.RED : Color.GRAY);

	    int x = bounds.x;
	    int y = bounds.y;
	    int w = bounds.width;
	    int h = bounds.height;

	    // Disegno tratteggiato
	    final int dash = 4; // lunghezza dei segmenti

	    // Lati orizzontali
	    for (int i = x; i < x + w; i += dash * 2) {
	        g.drawLine(i, y, i + dash, y);         // alto
	        g.drawLine(i, y + h, i + dash, y + h); // basso
	    }

	    // Lati verticali
	    for (int i = y; i < y + h; i += dash * 2) {
	        g.drawLine(x, i, x, i + dash);         // sinistra
	        g.drawLine(x + w, i, x + w, i + dash); // destra
	    }

	    g.setColor(old);
	}


}
