package graphical;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import Measure.Bar;
import Measure.Bar.Type;
import musicInterface.MusicObject;
import scoreWriter.SymbolRegistry;

public class GraphicalBar extends GraphicalObject {

	private MusicalSymbol symbol;
	private final Bar bar;

	public GraphicalBar(Bar bar) {
		this.bar = bar;
		symbol = setSymbol();
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
		GraphicalBar newBar = new GraphicalBar(bar);
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

	@Override
	protected MusicalSymbol setSymbol() {
		Type t = bar.getType();
		 switch (t) {
         case NORMAL:
             return SymbolRegistry.BARLINE_SINGLE;
         case DOUBLE:
             return SymbolRegistry.BARLINE_DOUBLE;
         case END:
             return SymbolRegistry.BARLINE_FINAL;
         case BEGIN_REPEAT:
             return SymbolRegistry.BARLINE_REPEAT_START;
         case END_REPEAT:
             return SymbolRegistry.BARLINE_REPEAT_END;
         default:
             return SymbolRegistry.BARLINE_SINGLE; // fallback
     }
	}


	@Override
	public Bar getModelObject() {
		return bar;
	}

}
