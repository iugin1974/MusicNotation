package ui;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

import graphical.MusicalSymbol;
import scoreWriter.Controller;
import scoreWriter.ScoreWriter;
import scoreWriter.StaffInfo;

public class Pointer implements StaffInfo {

	private int x, y;
	private MusicalSymbol symbol;
	private Controller controller;
	
	public Pointer(Controller controller, MusicalSymbol noteSymbol) {
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
		int voiceNumber = controller.getCurrentVoice();
		// se il costruttore ha solo un glifo, prende quello.
		String glyph = null;
				// TODO riscrivi la funzione. Le pause hanno lo stesso glifo per one e two
		if (voiceNumber == 1)
			glyph = symbol.getGlyphUp();
		else if (voiceNumber == 2)
			glyph = symbol.getGlyphDown();
		if (glyph == null) glyph = symbol.getGlyph();
        g.drawString(glyph, x, y);
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
