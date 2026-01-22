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

import musicEvent.Rest;
import musicInterface.MusicObject;
import scoreWriter.SymbolRegistry;

public class GraphicalRest extends GraphicalObject {

	private MusicalSymbol symbol;
	private final Rest rest;

	public GraphicalRest(Rest rest) {
		this.rest = rest;
		symbol = setSymbol();
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
		drawBounds(g);
	}

	
	@Override
	public GraphicalObject cloneObject() {
		GraphicalRest n = new GraphicalRest(rest);
		n.setX(getX());
		n.setY(getY());
		n.setBounds(getBounds());
		return n;
	}

	public MusicalSymbol getSymbol() {
		return symbol;
	}
	
	public Rest getRest() {
		return rest;
	}

	@Override
	protected MusicalSymbol setSymbol() {
		int dur = rest.getDuration(); // 0 = whole, 1 = half, 2 = quarter, ecc.

        return switch (dur) {
            case 0 -> SymbolRegistry.WHOLE_REST;
            case 1 -> SymbolRegistry.HALF_REST;
            case 2 -> SymbolRegistry.QUARTER_REST;
            case 3 -> SymbolRegistry.EIGHTH_REST;
            case 4 -> SymbolRegistry.SIXTEENTH_REST;
            case 5 -> SymbolRegistry.THIRTY_SECOND_REST;
            case 6 -> SymbolRegistry.SIXTY_FOURTH_REST;
            default -> SymbolRegistry.QUARTER_REST; // fallback
        };
	}

	@Override
	public Rest getModelObject() {
		return rest;
	}

}
