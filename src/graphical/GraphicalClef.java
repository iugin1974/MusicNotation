package graphical;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;

import model.MusicalSymbol;

public class GraphicalClef extends GraphicalObject {

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
	public GraphicalObject cloneObject() {
		GraphicalClef c = new GraphicalClef(getSymbol());
		c.setX(getX());
		c.setY(getY());
		c.setBounds(getBounds());
		return c;
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

        Rectangle bounds = new Rectangle(getX(), getY() - ascent, width, height);
        setBounds(bounds);
        if (isSelected()) {
			g.setColor(Color.RED);
		} else {
			g.setColor(Color.BLACK);
		}
        g.drawString(glyph, getX(), getY());
		
	}
	
	public MusicalSymbol getSymbol() {
		return symbol;
	}
	
	 public int[] getSemitoneMap() { 
		 return symbol.getSemitoneMap();
	}
	 
	 public int getMidiOffset() {
		 return symbol.getMidiOffset();
	 }
	 
	}
