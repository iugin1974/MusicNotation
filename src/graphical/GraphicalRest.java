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
import musicEvent.Rest;

public class GraphicalRest extends GraphicalObject {

	private MusicalSymbol symbol;
	private final Rest rest;

	public GraphicalRest(MusicalSymbol symbol, Rest rest) {
		this.symbol = symbol;
		this.rest = rest;
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
	public void draw(Graphics g) {
		String glyph = symbol.getGlyph();
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

	
	@Override
	public GraphicalObject cloneObject() {
		GraphicalRest n = new GraphicalRest(symbol, rest);
		n.setX(getX());
		n.setY(getY());
		setBounds(getBounds());
		return n;
	}

	public MusicalSymbol getSymbol() {
		return symbol;
	}
	
	public Rest getRest() {
		return rest;
	}

}
