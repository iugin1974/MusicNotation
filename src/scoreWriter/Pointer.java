package scoreWriter;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

import scoreWriter.VoiceLayer.VoiceType;

public class Pointer implements StaffInfo {

	private int x, y;
	private MusicalSymbol symbol;
	private ScoreWriter controller;
	private int staffIndex;
	
	Pointer(ScoreWriter controller, MusicalSymbol noteSymbol) {
		this.symbol = noteSymbol;
		this.controller = controller;
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
		VoiceType voiceType = controller.getVoiceType();
		// se il costruttore ha solo un glifo, prende quello.
		String glyph = null;
				// TODO riscrivi la funzione. Le pause hanno lo stesso glifo per one e two
		if (voiceType == VoiceType.VOICE_ONE)
			glyph = symbol.getGlyphUp();
		else if (voiceType == VoiceType.VOICE_TWO)
			glyph = symbol.getGlyphDown();
		if (glyph == null) glyph = symbol.getGlyph();
        g.drawString(glyph, x, y);
	}

	@Override
	public void setStaffIndex(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getStaffIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setStaffPosition(int p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getStaffPosition() {
		// TODO Auto-generated method stub
		return 0;
	}
}
