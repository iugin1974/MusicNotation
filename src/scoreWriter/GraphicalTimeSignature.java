package scoreWriter;

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

public class GraphicalTimeSignature implements GraphicalObject {

	private int numerator;
	private int denominator;
	private final GraphicalHelper helper = new GraphicalHelper();
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

	public GraphicalTimeSignature(int numerator, int denominator, GraphicalStaff staff) {
		this.numerator = numerator;
		this.denominator = denominator;
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
	    Graphics2D g2 = (Graphics2D) g;
	    g2.setColor(helper.isSelected() ? Color.RED : Color.BLACK);

	    Font oldFont = g2.getFont();
	    g2.setFont(numberFont);

	    int numY = staff.getYPosOfLine(4);
	    int denY = staff.getYPosOfLine(2);

	    drawDigits(g2, String.valueOf(numerator), helper.getX(), numY);
	    drawDigits(g2, String.valueOf(denominator), helper.getX(), denY);

	    // bounds più precisi
	    FontMetrics fm = g2.getFontMetrics();
	    int width = Math.max(fm.stringWidth(String.valueOf(numerator)), fm.stringWidth(String.valueOf(denominator)));
	    int height = (denY - numY) + fm.getHeight();
	    Rectangle bounds = new Rectangle(helper.getX() - width / 2, numY - fm.getAscent(), width, height);
	    helper.setBounds(bounds);

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
	public void setXY(int x, int y) {
		helper.setXY(x, y);
	}

	@Override
	public int getX() {
		return helper.getX();
	}

	@Override
	public void setX(int x) {
		helper.setX(x);
		;
	}

	@Override
	public int getY() {
		return helper.getY();
	}

	@Override
	public void setY(int y) {
		helper.setY(y);
	}

	@Override
	public boolean isSelected() {
		return helper.isSelected();
	}

	@Override
	public void select(boolean selected) {
		helper.select(selected);
	}

	@Override
	public boolean contains(int x, int y) {
		return helper.contains(x, y);
	}

	@Override
	public void moveTo(int x, int y) {
		helper.moveTo(x, y);

	}

	@Override
	public void moveBy(int dx, int dy) {
		helper.moveBy(dx, dy);
	}

	@Override
	public void setBounds(Rectangle bounds) {
		helper.setBounds(bounds);
	}

	@Override
	public Rectangle getBounds() {
		return helper.getBounds();
	}

	@Override
	public GraphicalObject cloneObject() {
		GraphicalTimeSignature timeSig = new GraphicalTimeSignature(numerator, denominator, staff);
		timeSig.setX(getX());
		timeSig.setY(getY());
		timeSig.setBounds(getBounds());
		return timeSig;
	}
	
	@Override
	public MusicalSymbol getSymbol() {
		return null;
	}
	
	@Override
	public String toString() {
		return null;
	}
}
