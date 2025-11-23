package scoreWriter;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

public class Pointer {

	private int x, y;
	private MusicalSymbol symbol;
	
	Pointer(MusicalSymbol noteSymbol) {
		this.symbol = noteSymbol;
		init();
	}
	
	private void init() {
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
	
	public void moveTo(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}

	public void draw(Graphics g) {
		// se il costruttore ha solo un glifo, prende quello.
		String glyph = symbol.getGlyphUp();
		if (glyph == null) glyph = symbol.getGlyph();
        g.drawString(glyph, x, y);
		
	}
}
