package scoreWriter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import musicInterface.MusicObject;

class GraphicalStaff implements GraphicalObject {
	private int id = 0;
	private int x, y, width, distanceBetweenLines;
	boolean selected = false;
	private int lineNumber = 5;
	private final int MAX_LEDGER_LINES = 3; // i tagli addizionali
	private ScoreWriter controller;
	private final GraphicalHelper helper = new GraphicalHelper();

	GraphicalStaff(int id, int x, int y, int width, int lineNumber, int distanceBetweenLines, ScoreWriter controller) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.lineNumber = lineNumber;
		this.distanceBetweenLines = distanceBetweenLines;
		this.controller = controller;
		this.id = id;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getX2() {
		return x + width;
	}

	public int getY2() {
		return y + lineNumber * distanceBetweenLines;
	}

	public int getHeight() {
		return lineNumber * distanceBetweenLines;
	}

	public int getDistanceBetweenLines() {
		return distanceBetweenLines;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public int getNoteY(int semitoneFromC) {
		return getReferenceLine() - (distanceBetweenLines / 2) * semitoneFromC;
	}

	public int getReferenceLine() {
		return getYPosOfLine(0);
	}

	/**
	 * @param line
	 * @return la posizione verticale della linea <i>line</i>
	 */
	public int getYPosOfLine(int line) {
		// inverte la numerazione delle linee (la 5 viene considerata la 0)
		// quindi la 0 è la prima linea in basso
		// il do in chiave di violino sarebbe la linea -1
		// e così via.
		int l = lineNumber - line;
		return y + (l * distanceBetweenLines);
	}

	
	public int[] getYPosOfLines() {
		int pos[] = new int[lineNumber];
		for (int i = 1; i <= lineNumber; i++) {
			pos[i-1] = getYPosOfLine(i);
		}
		return pos;
	}

	/**
	 * @param space
	 * @return la posizione verticale dello centro dello spazio <i>space</i>
	 **/
	public int getYPosOfSpace(int space) {
		// inverte la numerazione delle linee (la 5 viene considerata la 0)
		int l = lineNumber - space - 1;
		return y + (l * distanceBetweenLines) + (distanceBetweenLines / 2);
	}
	
	public int[] getYPosOfSpaces() {
	    int pos[] = new int[lineNumber];
	    for (int s = 1; s <= lineNumber; s++) {
	        // lo spazio "s" è sopra la linea "s"
	        pos[s - 1] = getYPosOfSpace(s);
	    }
	    return pos;
	}
	
	/**
	 * @return array con le posizioni verticali di linee e spazi,
	 * alternati, partendo dalla linea più bassa.
	 */
	public int[] getYPosOfLinesAndSpaces() {
	    int total = 2 * lineNumber;
	    int[] pos = new int[total];

	    int idx = 0;
	    for (int line = 1; line <= lineNumber; line++) {
	        pos[idx++] = getYPosOfLine(line);  // linea

	        if (line < lineNumber) {            // spazio sopra la linea
	            pos[idx++] = getYPosOfSpace(line);
	        }
	    }

	    return pos;
	}
	
	/**
	 * Restituisce le posizioni verticali di tutte le linee e spazi del pentagramma,
	 * includendo eventuali linee/spazi extra sopra e sotto.
	 * 
	 * L’array risultante è ordinato dal basso verso l’alto (y più grande → basso, y più piccolo → alto):
	 * <ul>
	 *   <li>Gli indici 0..extraLinesBelow-1 corrispondono a spazi/linee sotto il pentagramma.</li>
	 *   <li>Gli indici successivi (centrali) corrispondono alle linee e spazi del pentagramma:</li>
	 *     <ul>
	 *       <li>Linea1 (più bassa) → indice = extraLinesBelow</li>
	 *       <li>Spazio1 sopra linea1 → indice = extraLinesBelow + 1</li>
	 *       <li>Linea2 → indice = extraLinesBelow + 2</li>
	 *       <li>… fino a Linea5 (più alta) → indice = extraLinesBelow + 8 (per pentagramma a 5 linee)</li>
	 *     </ul>
	 *   <li>Gli ultimi indici corrispondono a spazi/linee sopra il pentagramma (extraLinesAbove).</li>
	 * </ul>
	 * 
	 * Esempio in chiave di violino (5 linee, nessun extra sotto, 1 extra sopra):
	 *<br>
	 * indice 0: Linea1  → Mi4<br>
	 * indice 1: Spazio1 → Fa4<br>
	 * indice 2: Linea2  → Sol4<br>
	 * indice 3: Spazio2 → La4<br>
	 * indice 4: Linea3  → Si4<br>
	 * indice 5: Spazio3 → Do5<br>
	 * indice 6: Linea4  → Re5<br>
	 * indice 7: Spazio4 → Mi5<br>
	 * indice 8: Linea5  → Fa5<br>
	 * indice 9: Spazio sopra Linea5 → Sol5 (diesis in Re maggiore)<br>
	 * 
	 * Nota: il Do centrale (C4) in chiave di violino si troverebbe
	 * 2 spazi sotto la Linea1 e avrebbe un indice negativo se extraLinesBelow = 0.
	 * Per includere il Do centrale, impostare extraLinesBelow >= 2.
	 * 
	  * @param extraLinesBelow numero di spazi/linee aggiuntivi sotto il pentagramma 
	 * @param extraLinesAbove numero di spazi/linee aggiuntivi sopra il pentagramma
	 * @return array di interi con le coordinate Y di linee e spazi, ordinati dal basso verso l’alto
	 */
	public int[] getYPosOfLinesAndSpacesExtended(int extraLinesBelow, int extraLinesAbove) {
	    int total = 2 * lineNumber - 1 + 2 * (extraLinesAbove + extraLinesBelow);
	    int[] pos = new int[total];

	    int idx = 0;

	    // spazi/linee sotto il pentagramma
	    for (int i = extraLinesBelow; i > 0; i--) {
	        int delta = i * distanceBetweenLines;
	        pos[idx++] = y + distanceBetweenLines * lineNumber + delta; // spazio/linea sotto
	    }

	    // linee+spazi del pentagramma
	    for (int line = 1; line <= lineNumber; line++) {
	        pos[idx++] = getYPosOfLine(line);
	        if (line < lineNumber) {
	            pos[idx++] = getYPosOfSpace(line);
	        }
	    }

	    // spazi sopra il pentagramma
	    for (int i = 1; i <= extraLinesAbove; i++) {
	        pos[idx++] = getYPosOfLine(lineNumber) - i * distanceBetweenLines / 2; // spazio sopra
	    }

	    return pos;
	}


	public ArrayList<Integer> getSnapPoints() {
		ArrayList<Integer> snapPoints = new ArrayList<>();
		// da due tagli sotto a due tagli sopra
		int bottomLine = getYPosOfLine(0) + (distanceBetweenLines * 2);
		int topLine = getYPosOfLine(lineNumber) - (distanceBetweenLines * 2);
		for (int i = topLine; i <= bottomLine; i += distanceBetweenLines / 2) {
			snapPoints.add(i);
		}
		return snapPoints;
	}

	/**
	 * Indica la posizione della nota nel pentagramma, dove 0 è il do centrale in
	 * chiave di violino, 1 il re e così via
	 * 
	 * @param y
	 */
	public int getPosInStaff(GraphicalNote n) {
		ArrayList<Integer> snapPoints = getSnapPoints();
		// 14 è il do centrale nell'arraylist
		return -(snapPoints.indexOf(n.getY()) - 14);
	}

	@Override
	public void draw(Graphics g) {
		if (selected) {
			g.setColor(Color.RED);
		} else {
			g.setColor(Color.BLACK);
		}
		for (int i = 0; i < lineNumber; i++) {
			int yPos = y + i * distanceBetweenLines;
			g.drawLine(x, yPos, x + width, yPos);
		}
	}

		public int getId() {
		return id;
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
		helper.setX(x);;
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
	public boolean contains(int px, int py) {
		return px >= x && px <= (x + width) && py >= y - (distanceBetweenLines * MAX_LEDGER_LINES)
				&& py <= (y + (lineNumber * distanceBetweenLines) + (distanceBetweenLines * MAX_LEDGER_LINES));
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBounds(Rectangle bounds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Rectangle getBounds() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public MusicalSymbol getSymbol() {
		return null;
	}
	
}