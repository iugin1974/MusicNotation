package scoreWriter;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;

public class GraphicalClef implements GraphicalObject {

	private final GraphicalHelper helper = new GraphicalHelper();
	private MusicalSymbol symbol;
	
	public GraphicalClef(MusicalSymbol symbol) {
		this.symbol = symbol;
		setup();
	}
	
	private void setup() {
		InputStream is = getClass().getResourceAsStream("/fonts/Bravura.otf");
		Font font = null;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(40f);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		ge.registerFont(font);
	}
	
	@Override
	public void setXY(int x, int y) {
		helper.setXY(x, y);
	}

	@Override
	public int getX() {
		return helper.getX();
	}

	@Override
	public void setX(int x) {
		helper.setX(x);;
	}

	@Override
	public int getY() {
		return helper.getY();
	}

	@Override
	public void setY(int y) {
		helper.setY(y);
	}

	@Override
	public boolean isSelected() {
		return helper.isSelected();
	}

	@Override
	public void select(boolean selected) {
		helper.select(selected);
	}

	@Override
	public boolean contains(int x, int y) {
        return helper.contains(x, y);
    }

	@Override
	public void moveTo(int x, int y) {
		helper.moveTo(x, y);
		
	}

	@Override
	public void moveBy(int dx, int dy) {
		 helper.moveBy(dx, dy);
	}

	@Override
	public GraphicalObject cloneObject() {
		GraphicalClef c = new GraphicalClef(symbol);
		c.setX(getX());
		c.setY(getY());
		setBounds(getBounds());
		return c;
	}



	@Override
	public void setBounds(Rectangle bounds) {
	helper.setBounds(bounds);
	}

	@Override
	public Rectangle getBounds() {
		return helper.getBounds();
	}

	@Override
	public void draw(Graphics g) {
		String glyph = symbol.getGlyphUp();
		if (glyph == null) glyph = symbol.getGlyph();
        FontMetrics fm = g.getFontMetrics();
        int width = fm.stringWidth(glyph);
        int ascent = fm.getAscent();
        int descent = fm.getDescent();
        int height = ascent + descent;

        Rectangle bounds = new Rectangle(helper.getX(), helper.getY() - ascent, width, height);
        helper.setBounds(bounds);
        if (helper.isSelected()) {
			g.setColor(Color.RED);
		} else {
			g.setColor(Color.BLACK);
		}
        g.drawString(glyph, helper.getX(), helper.getY());
		
	}
	
	@Override
	public String toString() {
		return symbol.getName();
	}

	@Override
	public MusicalSymbol getSymbol() {
		return symbol;
	}
	
	 public int getMidiOffset() { 
		 return symbol.getMidiOffset();
	}

}
