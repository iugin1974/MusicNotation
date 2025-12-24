package scoreWriter;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.swing.SwingUtilities;

import Measure.Bar;
import graphical.GraphicalBar;
import graphical.GraphicalNote;
import graphical.GraphicalObject;
import graphical.GraphicalRest;
import graphical.GraphicalScore;
import graphical.GraphicalStaff;
import graphical.MusicalSymbol;
import graphical.MusicalSymbol.Type;
import musicEvent.Note;
import notation.Score;
import ui.GUI;
import ui.Pointer;

public class Controller {
	
	public Score score = new Score();
	private GraphicalScore graphicalScore;
	private GUI gui;
	private Pointer pointer;
	private int currentVoice = 1;
	private SelectionManager selectionManager = new SelectionManager();
	private Point lastClick;

	private void test() {
		score.addStaff();
	}

	public static void main(String[] args) {
		new Controller().go();
	}

	private void registerListeners() {
		score.addListener(gui);
	}
	private void go() {
		graphicalScore = new GraphicalScore(score);
		gui = new GUI(this, graphicalScore);
		registerListeners();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				gui.setVisible(true);
				test();
			}
		});

	}
	
	public void setPointer(MusicalSymbol symbol) {
		pointer = new Pointer(this, symbol);
	}

	public void addStaff() {
		score.addStaff();
	}
	
	public void export() {
		
	}

	public void setCurrentVoice(int i) {
		currentVoice = i;
		
	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public Pointer getPointer() {
		return pointer;
	}

	public GraphicalObject getObjectAt(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean pointerExists() {
		return pointer != null;
	}

	public void insertObject(MusicalSymbol objectToInsert, int x, int y) {
		GraphicalStaff s = gui.getPointedStaff(x, y);
	    if (s == null) return;

	    selectionManager.deselectAll();
	    if (objectToInsert.getType() == Type.NOTE) insertNote(objectToInsert.getDuration(), s, x, y);
	    else if (objectToInsert.getType() == Type.BARLINE) insertBar(objectToInsert, s, x, y);
	   
	}

	private void insertBar(MusicalSymbol objectToInsert,  GraphicalStaff s, int x, int y) {
		Bar bar = getBar(objectToInsert);
		 int staffIndex = graphicalScore.getStaffIndex(s);
		 gui.prepareGraphicalInsertion(x, s.getYPosOfLine(0));
		 score.addObject(bar, staffIndex, currentVoice);
	}

	private void insertNote(int duration, GraphicalStaff s, int x, int y) {
	    Note n = createNote(duration);
	    int staffIndex = graphicalScore.getStaffIndex(s);
	    gui.prepareGraphicalInsertion(x, y);
	    score.addObject(n, staffIndex, currentVoice);
	}
	
	private Note createNote(int duration) {
		Note n = new Note();
		n.setDuration(duration);
		return n;
	}
	
	public Bar getBar(MusicalSymbol barSymbol) {
		Bar bar = new Bar();

		if (barSymbol.equals(SymbolRegistry.BARLINE_SINGLE)) {
			// default è già il singolo, non serve fare nulla
		} else if (barSymbol.equals(SymbolRegistry.BARLINE_DOUBLE)) {
			bar.setDoubleBar();
		} else if (barSymbol.equals(SymbolRegistry.BARLINE_REPEAT_START)) {
			bar.setBeginRepeatBar();
		} else if (barSymbol.equals(SymbolRegistry.BARLINE_REPEAT_END)) {
			bar.setEndRepeatBar();
		} else if (barSymbol.equals(SymbolRegistry.BARLINE_FINAL)) {
			bar.setEndBar();
		} else {
			throw new IllegalArgumentException("Barline non supportata: " + barSymbol.getName());
		}

		return bar;
	}
	
	
	public void selectObjectAtPos(int x, int y, boolean ctrl) {
		// TODO Auto-generated method stub
		
	}

	public HashMap<GraphicalObject, Integer> getStartXPositions(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	public void shiftHorizontal(int mouseX, HashMap<GraphicalObject, Integer> startPositions, int staffNumber) {
		// TODO Auto-generated method stub
		
	}

	public void moveObjects(int mouseX, int snapY) {
		// TODO Auto-generated method stub
		
	}

	public void movePointerTo(int mouseX, int snapY) {
		pointer.moveTo(mouseX, snapY);
	}

	public Point getLastClick() {
		return lastClick;
	}
}
