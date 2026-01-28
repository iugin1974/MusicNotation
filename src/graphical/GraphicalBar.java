package graphical;

import java.awt.Color;
import java.awt.Graphics;

import Measure.Bar;
import Measure.Bar.Type;
import scoreWriter.SymbolRegistry;

public class GraphicalBar extends GraphicalObject {

	private MusicalSymbol symbol;
	private Bar bar;

	public GraphicalBar(Bar bar) {
        this.bar = bar;
        symbol = setSymbol();
    }

	@Override
	public void draw(Graphics g) {
		String glyph = symbol.getGlyph();
		setBounds(g, glyph);
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
		newBar.init(getGraphicalScore(), getGraphicalStaff(), getX(), getY());
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
