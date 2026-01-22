package graphical;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.lang.ModuleLayer.Controller;

import musicInterface.MusicObject;

public abstract class GraphicalObject {
	
	private boolean selected = false;
	private int x = 0;
	private int y = 0;
	protected Rectangle bounds = null;
	protected GraphicalScore gScore;
	protected GraphicalStaff gStaff;
	
	// inizializzazione comune
    public final void init(
            GraphicalScore gScore,
            GraphicalStaff gStaff,
            int x,
            int y
    ) {
        this.gScore = gScore;
        this.gStaff = gStaff;
        this.x = x;
        this.y = y;
    }
    
    public final void init(
            GraphicalScore gScore,
            GraphicalStaff gStaff
    ) {
        this.gScore = gScore;
        this.gStaff = gStaff;
    }

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
	
	public int getWidth() {
		return bounds.width;
	}
	
	public int getHeight() {
		return bounds.height;
	}
	
	public GraphicalScore getGraphicalScore() {
		return gScore;
	}
	
	public GraphicalStaff getGraphicalStaff() {
		return gStaff;
	}
	
    public boolean contains(int x, int y) {
        return bounds != null && bounds.contains(x, y);
    }

    /** override se serve */
    public GraphicalObject hitTest(int x, int y) {
        return contains(x, y) ? this : null;
    }
	
	public void drawBounds(Graphics g) {
		if (!scoreWriter.Controller.TEST) return;
		
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
		
	public abstract void draw(Graphics g);
	public abstract GraphicalObject cloneObject();
	protected abstract MusicalSymbol setSymbol();
	public abstract MusicObject getModelObject();
}
