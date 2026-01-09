package graphical;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import musicInterface.MusicObject;
import notation.Staff;
import scoreWriter.ScoreWriter;
import ui.PopupLauncher;

public class GraphicalStaff extends GraphicalObject implements PopupLauncher {
	
	private Staff staff;
	private int id = 0;
	private int x, y, width, distanceBetweenLines;
	boolean selected = false;
	private int lineNumber = 5;
	private final int MAX_LEDGER_LINES = 3; // i tagli addizionali
	private StaffActionListener actionListener;

	public GraphicalStaff(Staff staff, int id, int x, int y, int width, int lineNumber, int distanceBetweenLines) {
		this.staff = staff;
		this.x = x;
		this.y = y;
		this.width = width;
		this.lineNumber = lineNumber;
		this.distanceBetweenLines = distanceBetweenLines;
		this.id = id;
		setBounds();
	}
	
	private void setBounds() {
		int rectX = x;
		int rectY = y - (distanceBetweenLines * MAX_LEDGER_LINES);
		int rectWidth = width;
		int rectHeight = (lineNumber * distanceBetweenLines) + (distanceBetweenLines * MAX_LEDGER_LINES * 2);
		bounds = new Rectangle(rectX, rectY, rectWidth, rectHeight);
	}

	public void setActionListener(StaffActionListener listener) {
	    this.actionListener = listener;
	}
	
	public void setWidth(int width) {
		this.width = width;
		setBounds();
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
	 * Restituisce la posizione verticale della linea i-esima del pentagramma.
	 * Convenzione:
	 * - 0 = prima linea in basso
	 * - 1 = seconda linea
	 * - ...
	 * - lineNumber-1 = prima linea in alto
	 * - valori negativi = linee/linee aggiuntive sotto il pentagramma
	 * 
	 * @param line indice della linea (0 = prima linea in basso)
	 * @return coordinata Y della linea
	 */
	public int getYPosOfLine(int line) {
	    // line 0 = prima linea in basso
	    // y aumenta verso il basso
	    int l = lineNumber - 1 - line; 
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
	 * inclusi eventuali spazi/linee extra sopra e sotto.
	 *
	 * Lo staffPosition 0 corrisponde alla prima linea in basso.
	 * Positivi → verso l’alto sul pentagramma, negativi → sotto la prima linea.
	 *
	 * @param extraBelow numero di spazi/linee aggiuntivi sotto il pentagramma
	 * @param extraAbove numero di spazi/linee aggiuntivi sopra il pentagramma
	 * @return array di coordinate Y ordinate dal basso verso l’alto
	 */
	public int[] getYPosOfLinesAndSpacesExtended(int extraBelow, int extraAbove) {
		int min = getYPosOfLine(-extraBelow);
		int max = getYPosOfLine(extraAbove + lineNumber);
		int step = distanceBetweenLines / 2;
		int dim = ((min - max) / 2) +2;
		int[] pos = new int[dim];
		int c = 0;
		for (int i = min; i > max; i -= step) {
			pos[c++] = i;
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
	 * Indica la posizione della nota nel pentagramma, dove 0 è la prima linea in basso
	 * 1 lo spazio successivo e così via
	 * 
	 * @param y
	 */
	public int getPosInStaff(GraphicalNote n) {
		int l = 2; // le ledges lines sopra e sotto
		 int[] pos = getYPosOfLinesAndSpacesExtended(l, l);

		    for (int i = 0; i < pos.length; i++) {
		        if (pos[i] == n.getY()) {
		        	// TODO commenta -> funziona, credimi
		            return i - (l * 2); // posizione pura nello staff
		        }
		    }
		    return -1;
	}

	
	/**
	 * Indica la posizione di Y pentagramma, dove 0 è la prima linea in basso
	 * 1 lo spazio successivo e così via
	 * 
	 * @param y
	 */
	public int getPosInStaff(int y) {
		int l = 2; // le ledges lines sopra e sotto
		 int[] pos = getYPosOfLinesAndSpacesExtended(l, l);

		    for (int i = 0; i < pos.length; i++) {
		        if (pos[i] == y) {
		        	// TODO commenta -> funziona, credimi
		            return i - (l * 2); // posizione pura nello staff
		        }
		    }
		    return -1;
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

	@Override
	public GraphicalObject cloneObject() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public MusicalSymbol getSymbol() {
		return null;
	}
	
	@Override
	public JPopupMenu getMenu(int x, int y) {
		JPopupMenu menu = new JPopupMenu();
		JMenuItem i1 = new JMenuItem("Key accidentals");
		JMenuItem i2 = new JMenuItem("Time signature");
        menu.add(i1);
        menu.add(i2);
        i1.addActionListener(e -> {
            if (actionListener != null) {
                actionListener.openKeySignatureDialog(x, y);
            }
        });

        i2.addActionListener(e -> {
            if (actionListener != null) {
                actionListener.openTimeSignatureDialog(x, y);
            }
        });

        
        return menu;
	}

	@Override
	protected MusicalSymbol setSymbol() {
		return null;
	}

	@Override
	public MusicObject getModelObject() {
		return null;
	}
}