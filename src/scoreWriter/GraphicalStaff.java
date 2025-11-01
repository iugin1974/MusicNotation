package scoreWriter;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import musicInterface.MusicObject;

class GraphicalStaff implements GraphicalObject {
	private int id = 0;
	private int x, y, width, distanceBetweenLines;
	boolean selected = false;
	private int lineNumber = 5;
	private final int MAX_ADDED_LINES = 2; // i tagli addizionali
	private ScoreWriter controller;

	GraphicalStaff(int id, int x, int y, int width, int lineNumber, int distanceBetweenLines, ScoreWriter controller) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.lineNumber = lineNumber;
		this.distanceBetweenLines = distanceBetweenLines;
		this.controller = controller;
		this.id = id;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
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

	public int getDistance() {
		return distanceBetweenLines;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public int getNoteY(int semitoneFromC) {
		return getReferenceLine() - (distanceBetweenLines / 2) * semitoneFromC;
	}

	public int getReferenceLine() {
		return getLineY(0);
	}

	/**
	 * @param line
	 * @return la posizione verticale della linea <i>line</i>
	 */
	public int getLineY(int line) {
		// inverte la numerazione delle linee (la 5 viene considerata la 0)
		int l = lineNumber - line;
		return y + (l * distanceBetweenLines);
	}

	/**
	 * @param space
	 * @return la posizione verticale dello centro dello spazio <i>space</i>
	 **/
	public int getSpaceY(int space) {
		// inverte la numerazione delle linee (la 5 viene considerata la 0)
		int l = lineNumber - space - 1;
		return y + (l * distanceBetweenLines) + (distanceBetweenLines / 2);
	}

	public boolean contains(int px, int py) {
		return px >= x && px <= (x + width) && py >= y - (distanceBetweenLines * MAX_ADDED_LINES)
				&& py <= (y + (lineNumber * distanceBetweenLines) + (distanceBetweenLines * MAX_ADDED_LINES));
	}

	public void select(boolean s) {
		selected = s;
	}

	public boolean isSelected() {
		return selected;
	}

	public ArrayList<Integer> getSnapPoints() {
		ArrayList<Integer> snapPoints = new ArrayList<>();
		// da due tagli sotto a due tagli sopra
		int bottomLine = getLineY(0) + (distanceBetweenLines * 2);
		int topLine = getLineY(lineNumber) - (distanceBetweenLines * 2);
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

	private void drawNote(GraphicalNote note, Graphics g) {
		if (note.getY() > getLineY(3))
			note.setStemDirection(GraphicalNote.STEM_UP);
		else
			note.setStemDirection(GraphicalNote.STEM_DOWN);
		note.draw(g);
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

		ArrayList<GraphicalObject> objects = controller.getStaff(id);
		for (GraphicalObject object : objects) {
			if (object instanceof GraphicalNote) {
				GraphicalNote note = (GraphicalNote) object;
				drawNote(note, g);
			}
		}
	}

	public int getId() {
		return id;
	}
}