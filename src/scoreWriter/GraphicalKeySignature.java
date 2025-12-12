package scoreWriter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GraphicalKeySignature implements GraphicalObject {

	// -------------------------------------------------------------------
	// SMuFL glyphs
	// -------------------------------------------------------------------
	private static final String SHARP = "\uE262"; // SMuFL accidentalSharp
	private static final String FLAT = "\uE260"; // SMuFL accidentalFlat

	// -------------------------------------------------------------------
	// CAMPI
	// -------------------------------------------------------------------
	private int nextAlteration;
	int pos[];
	private final GraphicalHelper helper = new GraphicalHelper();
	// Indici delle posizioni verticali per le alterazioni della tonalità
	// ordinati secondo l’ordine delle alterazioni nella chiave musicale.
	// I valori corrispondono agli indici dell’array restituito da
	// staff.getYPosOfLinesAndSpacesExtended(extraLinesAbove, extraLinesBelow).

	// Diesis: F#, C#, G#, D#, A#, E#, B# (ordine standard)
	private int[] keySignatureSharpsIndex = { 8, 5, 9, 6, 3, 7, 4 };

	// Bemolli: Bb, Eb, Ab, Db, Gb, Cb, Fb (ordine standard)
	// L’ordine dei bemolli nella chiave parte da Si♭ e segue l’ordine delle
	// tonalità
	private int[] keySignatureFlatsIndex = { 4, 7, 3, 6, 2, 5, 1 };
	private ClefType clef;
	
	//private Font musicFont;
	private int numberOfAlterations;
	private int typeOfAlterations;
	private GraphicalStaff staff;

	// -------------------------------------------------------------------
	// ENUM
	// -------------------------------------------------------------------

	public enum ClefType {
		TREBLE, BASS
	}

	// -------------------------------------------------------------------
	// COSTRUTTORE
	// -------------------------------------------------------------------
	// accidentals è un array con le posizioni delle linee e spazi del pentagramma
	// type 1 = sharp, -1 = flat, 0 = natural
	public GraphicalKeySignature(int x, GraphicalStaff staff, int numberOfAlterations, int typeOfAlterations) {

		setX(x);
		nextAlteration = x;
		this.numberOfAlterations = numberOfAlterations;
		this.typeOfAlterations = typeOfAlterations;
		this.staff = staff;

		computeLayout();
	}

	// -------------------------------------------------------------------
	// CALCOLO POSIZIONI DEI GLIFI
	// -------------------------------------------------------------------
	private void computeLayout() {
		// Array che conterrà le coordinate Y di tutte le alterazioni da disegnare
		pos = new int[numberOfAlterations];

		// Ricava tutte le posizioni possibili dal pentagramma,
		// inclusi eventuali spazi sopra o sotto (qui 0 linea del MI4, 9 spazio SOL5)
		int[] linePositions = staff.getYPosOfLinesAndSpacesExtended(0, 9);

		if (typeOfAlterations == 1) { // Diesis
			for (int i = 0; i < numberOfAlterations; i++) {
				// Prende l’indice dell’array corrispondente all’i-esimo diesis nella tonalità
				int line = keySignatureSharpsIndex[i];
				pos[i] = linePositions[line]; // assegna la coordinata Y corretta
			}

		} else if (typeOfAlterations == -1) { // Bemolli
			for (int i = 0; i < numberOfAlterations; i++) {
				// Prende l’indice dell’array corrispondente all’i-esimo bemolle nella tonalità
				int line = keySignatureFlatsIndex[i];
				pos[i] = linePositions[line]; // assegna la coordinata Y corretta
			}
		}

		// Ora 'pos' contiene tutte le coordinate Y delle alterazioni da disegnare,
		// nell’ordine corretto, pronte per il metodo draw(Graphics g)
	}

	// -------------------------------------------------------------------
	// DISEGNO
	// -------------------------------------------------------------------
	@Override
	public void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		//g2.setFont(musicFont);
		// Se selezionato, evidenzia
		if (helper.isSelected())
			g2.setColor(Color.RED);
		else
			g2.setColor(Color.BLACK);

		// Disegna le alterazioni
		for (int i = 0; i < pos.length; i++) {
			drawGlyph(g2, pos[i]);
		}

		// Calcola minY e maxY
		int minY = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;

		for (int p : pos) {
			if (p < minY)
				minY = p;
			if (p > maxY)
				maxY = p;
		}

		// Calcolo bounds corretti
		int width = nextAlteration - helper.getX();
		int height = maxY - minY;

		Rectangle bounds = new Rectangle(helper.getX(), minY, width, height);

		helper.setBounds(bounds);
		//helper.drawBounds(g2);

		// Ripristina posizione iniziale
		nextAlteration = helper.getX();
	}

	private void drawGlyph(Graphics2D g2, int y) {
		String symbol = null;
		if (typeOfAlterations == -1)
			symbol = FLAT;
		else if (typeOfAlterations == 1)
			symbol = SHARP;
		g2.drawString(symbol, nextAlteration, y);
		nextAlteration += 10;
	}

	// -------------------------------------------------------------------
	// INTERFACE IMPLEMENTATION
	// -------------------------------------------------------------------
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
	public GraphicalObject cloneObject() {
		GraphicalKeySignature ks = new GraphicalKeySignature(helper.getX(), staff, numberOfAlterations, typeOfAlterations);
		ks.setX(getX());
		ks.setY(getY());
		setBounds(getBounds());
		return ks;
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
	public MusicalSymbol getSymbol() {
		return null;
	}

	@Override
	public String toString() {
		return "KeySignature(" + typeOfAlterations + ", clef=" + clef + ")";
	}

	public int getNumberOfAlterations() {
		return numberOfAlterations;
	}

	public int getTypeOfAlterations() {
		return typeOfAlterations;
	}
}
