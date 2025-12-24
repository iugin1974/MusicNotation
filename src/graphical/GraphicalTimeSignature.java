package graphical;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.io.IOException;
import java.io.InputStream;

import Measure.TimeSignature;

public class GraphicalTimeSignature extends GraphicalObject {

	private GraphicalStaff staff;
	private static final char[] SMUFL_DIGITS = {
		    '\uE080', // 0
		    '\uE081', // 1
		    '\uE082', // 2
		    '\uE083', // 3
		    '\uE084', // 4
		    '\uE085', // 5
		    '\uE086', // 6
		    '\uE087', // 7
		    '\uE088', // 8
		    '\uE089'  // 9
		};
	private Font numberFont;
	private final TimeSignature timeSignature;

	public GraphicalTimeSignature(TimeSignature timeSignature, GraphicalStaff staff) {
		this.timeSignature = timeSignature;
		this.staff = staff;
		
		try (InputStream is1 = getClass().getResourceAsStream("/fonts/BravuraText.otf")) {
			numberFont = Font.createFont(Font.TRUETYPE_FONT, is1).deriveFont(50f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FontFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void drawDigits(Graphics2D g2, String number, int x, int y) {
	    int cursorX = x;
	    FontRenderContext frc = g2.getFontRenderContext();

	    for (int i = 0; i < number.length(); i++) {
	        String glyph = toSMUFLNumber(number.substring(i, i + 1));
	        GlyphVector gv = numberFont.createGlyphVector(frc, glyph);
	        g2.drawGlyphVector(gv, cursorX, y);
	        cursorX += gv.getVisualBounds().getWidth(); // larghezza reale del glifo
	    }
	}


	@Override
	public void draw(Graphics g) {
		int numerator = timeSignature.getNumerator();
		int denominator = timeSignature.getDenominator();
	    Graphics2D g2 = (Graphics2D) g;
	    g2.setColor(isSelected() ? Color.RED : Color.BLACK);

	    Font oldFont = g2.getFont();
	    g2.setFont(numberFont);

	    int numY = staff.getYPosOfLine(4);
	    int denY = staff.getYPosOfLine(2);

	    drawDigits(g2, String.valueOf(numerator), getX(), numY);
	    drawDigits(g2, String.valueOf(denominator), getX(), denY);

	    // bounds più precisi
	    FontMetrics fm = g2.getFontMetrics();
	    int width = Math.max(fm.stringWidth(String.valueOf(numerator)), fm.stringWidth(String.valueOf(denominator)));
	    int height = (denY - numY) + fm.getHeight();
	    Rectangle bounds = new Rectangle(getX() - width / 2, numY - fm.getAscent(), width, height);
	    setBounds(bounds);

	    g2.setFont(oldFont);
	}

	private String toSMUFLNumber(String s) {
	    StringBuilder sb = new StringBuilder();
	    for (char c : s.toCharArray()) {
	        int digit = c - '0';
	        sb.append(SMUFL_DIGITS[digit]);
	    }
	    return sb.toString();
	}
	
	@Override
	public GraphicalObject cloneObject() {
		GraphicalTimeSignature timeSig = new GraphicalTimeSignature(timeSignature, staff);
		timeSig.setX(getX());
		timeSig.setY(getY());
		timeSig.setBounds(getBounds());
		return timeSig;
	}
	
	public MusicalSymbol getSymbol() {
		return null;
	}
	
	public TimeSignature getTimeSignature() {
		return timeSignature;
	}

	@Override
	protected MusicalSymbol setSymbol() {
		return null;
	}
	
}
