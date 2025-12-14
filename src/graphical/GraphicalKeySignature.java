package graphical;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import model.KeySignature;
import model.MusicalSymbol;

public class GraphicalKeySignature extends GraphicalObject {

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
	private final KeySignature keySignature;
	private ClefType clef;
	
	//private Font musicFont;
	
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
	// mode -1 = moll +1 = dur
	public GraphicalKeySignature(int x, GraphicalStaff staff, KeySignature keySignature) {

		setX(x);
		nextAlteration = x;
		this.staff = staff;
		this.keySignature = keySignature;
		computeLayout();
	}

	// -------------------------------------------------------------------
	// CALCOLO POSIZIONI DEI GLIFI
	// -------------------------------------------------------------------
	private void computeLayout() {
		int numberOfAlterations = keySignature.getNumberOfAlterations();
		int typeOfAlterations = keySignature.getTypeOfAlterations();
		int[] keySignatureSharpsIndex = keySignature.getSharpsIndex();
		int[] keySignatureFlatsIndex = keySignature.getFlatsIndex();
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
		if (isSelected())
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
		int width = nextAlteration - getX();
		int height = maxY - minY;

		Rectangle bounds = new Rectangle(getX(), minY, width, height);

		setBounds(bounds);
		//drawBounds(g2);

		// Ripristina posizione iniziale
		nextAlteration = getX();
	}

	private void drawGlyph(Graphics2D g2, int y) {
		int typeOfAlterations = keySignature.getTypeOfAlterations();
		String symbol = null;
		if (typeOfAlterations == -1)
			symbol = FLAT;
		else if (typeOfAlterations == 1)
			symbol = SHARP;
		g2.drawString(symbol, nextAlteration, y);
		nextAlteration += 10;
	}

	@Override
	public GraphicalObject cloneObject() {
		GraphicalKeySignature ks = new GraphicalKeySignature(getX(), staff, keySignature);
		ks.setX(getX());
		ks.setY(getY());
		setBounds(getBounds());
		return ks;
	}

	
	public MusicalSymbol getSymbol() {
		return null;
	}
	
	public KeySignature getKeySignature() {
		return keySignature;
	}

}
