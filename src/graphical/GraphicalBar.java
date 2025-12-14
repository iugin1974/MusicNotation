package graphical;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import Measure.Bar;
import model.MusicalSymbol;

public class GraphicalBar extends GraphicalObject {

	private MusicalSymbol symbol;
	private final Bar bar;

	public GraphicalBar(MusicalSymbol barlineSymbol, Bar bar) {
		this.symbol = barlineSymbol;
		this.bar = bar;
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
		GraphicalBar newBar = new GraphicalBar(symbol, bar);
		newBar.setX(getX());
		newBar.setY(getY());
		newBar.setBounds(getBounds());
		return newBar;
	}
	
	public MusicalSymbol getSymbol() {
		return symbol;
	}
	
	public Bar getBar() {
		return bar;
	}
}
