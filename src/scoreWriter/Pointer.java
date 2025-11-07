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
	private GUI gui;
	private MusicalSymbol symbol;
	public static enum GlyphType { NOTE, REST, BARLINE };
	
	Pointer(MusicalSymbol noteSymbol, GUI gui) {
		this.gui = gui;
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
//		String glyph = null;
//		if (glyphType == GlyphType.NOTE && y <= gui.getStaff(0).getLineY(3)) glyph = SymbolLibrary.getNoteGlyph(SymbolLibrary.STEM_DOWN, duration);
//		else if (glyphType == GlyphType.NOTE && y > gui.getStaff(0).getLineY(3)) glyph = SymbolLibrary.getNoteGlyph(SymbolLibrary.STEM_UP, duration);
//		else if (glyphType == GlyphType.REST) glyph = SymbolLibrary.getRestGlyph(duration);
//		else if (glyphType == GlyphType.BARLINE) glyph = SymbolLibrary.getBarlineGlyph(duration);
        g.drawString(symbol.getGlyphUp(), x, y);
		
	}
}
