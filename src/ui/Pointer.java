package ui;

import java.awt.Color;
import java.awt.Graphics;
import graphical.MusicalSymbol;
import scoreWriter.Controller;
import scoreWriter.StaffInfo;

public class Pointer implements StaffInfo {

	private int x, y;
	private MusicalSymbol symbol;
	private Controller controller;

	public Pointer(Controller controller, MusicalSymbol noteSymbol) {
		this.symbol = noteSymbol;
		this.controller = controller;
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
		if (voiceNumber == 1) {
			glyph = symbol.getGlyphUp();
		} else if (voiceNumber == 2) {
			glyph = symbol.getGlyphDown();
		}
		if (glyph == null) {
			glyph = symbol.getGlyph();
		}
		g.setColor(Color.GRAY);
        g.drawString(glyph, x, y);
        g.setColor(Color.BLACK);
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
