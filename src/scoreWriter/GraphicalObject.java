package scoreWriter;

import java.awt.Graphics;

public interface GraphicalObject {

	public int getX();
	public int getY();
	public void setX(int x);
	public void setY(int y);
	public boolean isSelected();
	public void select(boolean select);
	public boolean contains(int x, int y);
	public void draw(Graphics g);
}
