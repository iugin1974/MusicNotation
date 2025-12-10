package scoreWriter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GraphicalKeySignature implements GraphicalObject {

	
    // -------------------------------------------------------------------
    //  FONT SMuFL (Bravura)
    // -------------------------------------------------------------------
    private static Font bravuraFont;

    private static synchronized Font loadBravuraFont(float size) {
        if (bravuraFont == null) {
            try {
                InputStream is = GraphicalKeySignature.class.getResourceAsStream("/fonts/Bravura.otf");
                Font base = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(40f);
                bravuraFont = base.deriveFont(size);
            } catch (Exception ex) {
                ex.printStackTrace();
                // fallback
                bravuraFont = new Font("Serif", Font.PLAIN, (int) size);
            }
        } else {
            bravuraFont = bravuraFont.deriveFont(size);
        }
        return bravuraFont;
    }

    // -------------------------------------------------------------------
    //  SMuFL glyphs
    // -------------------------------------------------------------------
    private static final String SHARP  = "\uE262";  // SMuFL accidentalSharp
    private static final String FLAT   = "\uE260";  // SMuFL accidentalFlat

    // -------------------------------------------------------------------
    //  CAMPI
    // -------------------------------------------------------------------
    private int x, y;
    int pos[];
    private boolean selected = false;
 // Indici delle posizioni verticali per le alterazioni della tonalità
 // ordinati secondo l’ordine delle alterazioni nella chiave musicale.
 // I valori corrispondono agli indici dell’array restituito da
 // staff.getYPosOfLinesAndSpacesExtended(extraLinesAbove, extraLinesBelow).

 // Diesis: F#, C#, G#, D#, A#, E#, B# (ordine standard)
 private int[] keySignatureSharpsIndex = { 8, 5, 9, 6, 3, 7, 4 };

 // Bemolli: Bb, Eb, Ab, Db, Gb, Cb, Fb (ordine standard)
 // L’ordine dei bemolli nella chiave parte da Si♭ e segue l’ordine delle tonalità
 private int[] keySignatureFlatsIndex = { 4, 7, 3, 6, 2, 5, 1 };
    private Rectangle bounds = new Rectangle();
    private ClefType clef;

    private Font musicFont;
	private int numberOfAlterations;
	private int typeOfAlterations;
	private GraphicalStaff staff;

    // -------------------------------------------------------------------
    //  ENUM
    // -------------------------------------------------------------------

    public enum ClefType {
        TREBLE,
        BASS
    }

  
    // -------------------------------------------------------------------
    //  COSTRUTTORE
    // -------------------------------------------------------------------
    // accidentals è un array con le posizioni delle linee e spazi del pentagramma
    // type 1 = sharp, -1 = flat, 0 = natural
    public GraphicalKeySignature(int x, GraphicalStaff staff, int numberOfAlterations,
                                 int typeOfAlterations) {

        this.x = x;
        this.numberOfAlterations = numberOfAlterations;
        this.typeOfAlterations = typeOfAlterations;
        this.staff = staff;
        
        computeLayout();
    }

    // -------------------------------------------------------------------
    //  CALCOLO POSIZIONI DEI GLIFI
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
        g2.setFont(musicFont);

        for (int i = 0; i< pos.length; i++) {
        	int y = pos[i];
            drawGlyph(g2, y);
        }

        if (selected) {
            g2.setColor(Color.RED);
            g2.draw(bounds);
            g2.setColor(Color.BLACK);
        }
    }

    private void drawGlyph(Graphics2D g2, int y) {
        String symbol = null;
        if (typeOfAlterations == -1) symbol = FLAT;
        else if (typeOfAlterations == 1) symbol = SHARP;
        g2.drawString(symbol, x, y);
        x+=10;
    }

    // -------------------------------------------------------------------
    // INTERFACE IMPLEMENTATION
    // -------------------------------------------------------------------
    @Override
    public int getX() { return x; }

    @Override
    public int getY() { return y; }

    @Override
    public void moveTo(int newX, int newY) {
        this.x = newX;
        this.y = newY;
        computeLayout();
    }

    @Override
    public void moveBy(int dx, int dy) {
        this.x += dx;
        this.y += dy;
        computeLayout();
    }

    @Override
    public void setX(int x) { moveTo(x, this.y); }

    @Override
    public void setY(int y) { moveTo(this.x, y); }

    @Override
    public void setXY(int x, int y) { moveTo(x, y); }

    @Override
    public boolean isSelected() { return selected; }

    @Override
    public void select(boolean sel) { this.selected = sel; }

    @Override
    public boolean contains(int px, int py) {
        return bounds.contains(px, py);
    }

    @Override
    public void setBounds(Rectangle r) { this.bounds = r; }

    @Override
    public Rectangle getBounds() { return bounds; }

    @Override
    public GraphicalObject cloneObject() {
        return null;
    }

    @Override
    public MusicalSymbol getSymbol() {
        return null;
    }

    @Override
    public String toString() {
        return "KeySignature(" + typeOfAlterations + ", clef=" + clef + ")";
    }
    
    
}
