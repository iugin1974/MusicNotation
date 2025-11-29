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