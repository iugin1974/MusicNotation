package scoreWriter;

import java.awt.Graphics;
import java.awt.Rectangle;

public interface GraphicalObject {

	public int getX();
	public int getY();
	public void moveTo(int x, int y);
	public void moveBy(int dx, int dy);
	public void setX(int x);
	public void setY(int y);
	public void setXY(int x, int y);
	public boolean isSelected();
	public void select(boolean select);
	public boolean contains(int x, int y);
	public void setBounds(Rectangle bounds);
	public Rectangle getBounds();
	public void draw(Graphics g);
	public GraphicalObject cloneObject();
}
