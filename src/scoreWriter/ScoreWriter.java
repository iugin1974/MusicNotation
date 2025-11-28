package scoreWriter;

import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.SwingUtilities;

import musicInterface.MusicObject;

public class ScoreWriter {

	private ObjectSaver saver;
	private SpatialGrid grid;
	private SelectionManager selectionManager;
	private int currentVoice = 1;
	// private ArrayList<GraphicalObject> graphicalObjects = new ArrayList<>();
	private GUI gui;

	public ScoreWriter() {
		saver = new ObjectSaver();
		selectionManager = new SelectionManager();
		grid = new SpatialGrid(20);
	}

	private void test() {
		addStaff();
		addStaff();
		GraphicalNote n1 = new GraphicalNote(SymbolRegistry.EIGHTH_NOTE);
		GraphicalNote n2 = new GraphicalNote(SymbolRegistry.EIGHTH_NOTE);
		GraphicalClef c = new GraphicalClef(SymbolRegistry.CLEF_TREBLE);
		c.setXY(50, 80);
		n1.setXY(100, 100);
		n2.setXY(200, 80);
		saver.addObject(0, c);
		saver.addObject(0, n1);
		saver.addObject(0, n2);
	}

	public void addStaff() {
		ArrayList<GraphicalObject> staffForSelectedObjects = new ArrayList<>();
		saver.addStaff();
		selectionManager.addStaff();
		gui.addStaff(saver.getStaffCount() - 1);
		gui.repaint();
	}

	public ArrayList<ArrayList<GraphicalObject>> getStaffList() {
		return saver.getAllStaffs();
	}

	public ArrayList<GraphicalObject> getStaff(int staffNumber) {
		return saver.getObjects(staffNumber);
	}

	private GraphicalObject getObjectAt(int x, int y) {
		for (int j = 0; j < saver.getStaffCount(); j++) {
			ArrayList<GraphicalObject> objects = saver.getObjects(j);

			for (int i = 0; i < objects.size(); i++) {
				GraphicalObject obj = objects.get(i);

				if (obj.contains(x, y))
					return obj;
			}
		}
		return null;
	}

	public void selectObjectAtPos(int x, int y, boolean multipleSelection) {
		if (saver.getStaffCount() == 0)
			return;
		if (!multipleSelection)
			selectionManager.deselectAll();
		GraphicalObject o = getObjectAt(x, y);
		if (o == null)
			return;
		int staffIndex = gui.getPointedStaffIndex(x, y);
		selectionManager.select(o, staffIndex);
	}

	private void go() {
		gui = new GUI(this);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				gui.setVisible(true);
				test();
			}
		});
	}

	public static void main(String[] args) {
		new ScoreWriter().go();
	}

	private void insertNote(GraphicalNote n, int staffNumber) {
		GraphicalNote newNote = (GraphicalNote) n.cloneObject();
		saver.addObject(staffNumber, newNote);
		grid.add(newNote);
		// se necessario allunga i pentagrammi
		GraphicalStaff g = gui.getStaff(0);
		int x = newNote.getX();
		if (x > g.getWidth() - 100)
			resizeStaves();
	}

	private void resizeStaves() {
		System.out.println("resize");
		int w = gui.getStaffList().get(0).getWidth();
		for (GraphicalStaff s : gui.getStaffList()) {
			s.setWidth(w + 200);
		}
		gui.resizePanel(w + 200, gui.getHeight()); // TODO l'altezza deve essere in base agli staves
		// anche nella guissa
		gui.repaintPanel();
	}

	private void insertBar(GraphicalBar bar, int staffNumber) {
		GraphicalBar b = (GraphicalBar) bar.cloneObject();
		int firstLine = gui.getStaff(staffNumber).getLineY(1);
		b.setY(firstLine);
		saver.addObject(staffNumber, b);
	}

	private void insertClef(GraphicalClef clef, int staffNumber) {
		GraphicalClef c = (GraphicalClef) clef.cloneObject();
		int firstLine = 0;
		if (clef.getSymbol().equals(SymbolRegistry.CLEF_TREBLE)
				|| clef.getSymbol().equals(SymbolRegistry.CLEF_TREBLE_8))
			firstLine = gui.getStaff(staffNumber).getLineY(2);
		else if (clef.getSymbol().equals(SymbolRegistry.CLEF_BASS))
			firstLine = gui.getStaff(staffNumber).getLineY(4);
		c.setY(firstLine);
		saver.addObject(staffNumber, c);
	}

	public void insertObject(Pointer pointer, GraphicalObject object) {
		int x = pointer.getX();
		int y = pointer.getY();
		int staffNumber = checkInWichStaffIsPoint(x, y);
		if (staffNumber == -1)
			return;
		object.setX(x);
		object.setY(y);
		selectionManager.deselectAll();
		selectionManager.select(object, staffNumber);
		if (object instanceof GraphicalNote)
			insertNote((GraphicalNote) object, staffNumber);
		else if (object instanceof GraphicalBar)
			insertBar((GraphicalBar) object, staffNumber);
		else if (object instanceof GraphicalClef)
			insertClef((GraphicalClef) object, staffNumber);
		saver.sort(staffNumber);
		gui.repaintPanel();
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @return il numero dello staff in cui si trova il punto passato come argomento
	 *         oppure -1
	 */
	private int checkInWichStaffIsPoint(int x, int y) {

		for (int i = 0; i < saver.getStaffCount(); i++) {
			GraphicalStaff gs = gui.getStaff(i);
			if (gs.contains(x, y)) {
				System.out.println("Note inside Staff " + i);
				return i;
			}
		}
		return -1;
	}

	public void keyPressed(KeyEvent keyEvent) {
		if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
			gui.exitInsertMode();
		} else if (keyEvent.getKeyCode() == KeyEvent.VK_DELETE) {
			deleteSelectedObject();
		} else if (keyEvent.getKeyChar() == 's') {
			slurOrTie();
		}
	}

	private void slurOrTie() {
		for (int i = 0; i < selectionManager.getStaffNumber(); i++) {
			ArrayList<GraphicalNote> selectedNotes = selectionManager.getSelectedNotes(i);
			// se è selezionata solo una nota fa la legatura con la successiva
			if (selectedNotes.size() == 1) {
				GraphicalNote n1 = selectedNotes.get(0);
				GraphicalNote n2 = saver.getNextNote(n1);
				if (n1.getY() == n2.getY()) tie(n1, n2, i);
				else slur(n1, n2, i);
				
				break;
			}
			// altrimenti tra le due selezionate
			for (int j = 0; j < selectedNotes.size() - 1; j++) {
				GraphicalNote n1 = selectedNotes.get(j);
				GraphicalNote n2 = selectedNotes.get(j + 1);
				// le note devono aver la stessa altezza ed essere consecutive
				if (hasSameHeight(n1, n2) && saver.areNotesConsecutive(n1, n2)) tie(n1, n2, j);
				else slur(n1, n2, j);
			}
		}
		gui.repaintPanel();
	}

	private boolean hasSameHeight(GraphicalNote n1, GraphicalNote n2) {
		return n1.getY() == n2.getY();
	}
	
	private void tie(GraphicalNote n1, GraphicalNote n2, int staffNumber) {
		System.out.println("Tie");
		Tie tie = new Tie();
		tie.setNotes(n1, n2);
		saver.addObject(staffNumber, tie);
		
	}

	private void slur(GraphicalNote n1, GraphicalNote n2, int staffNumber) {
		System.out.println("Slur");
				Slur slur = new Slur();
				slur.setNotes(n1, n2);
				saver.addObject(staffNumber, slur);
	}

	private void deleteSelectedObject() {
		ArrayList<GraphicalObject> selectedObjects = getSelectedObjects();
		if (selectedObjects.size() == 0)
			return;

		// TODO refractoring usando lo saver
		for (ArrayList<GraphicalObject> staff : saver.getAllStaffs()) {
			for (GraphicalObject object : selectedObjects) {
				if (staff.contains(object)) {
					staff.remove(object);
					if (object instanceof GraphicalNote)
						grid.remove((GraphicalNote) object);
				}
			}
		}
		gui.repaintPanel();
	}

	public void mousePressed(int x, int y) {
		// TODO deseleziona tutti gli altri oggetti
		selectObjectAtPos(x, y, false);
		gui.repaintPanel();

	}

	private void moveObject(GraphicalObject o, int x, int y) {

		int oldStaff = checkInWichStaffIsPoint(o.getX(), o.getY());
		int newStaff = checkInWichStaffIsPoint(x, y);

		if (o instanceof GraphicalNote) {
			moveNote((GraphicalNote) o, x, y);
		} else {
			moveGenericObject(o, x, y);
		}

		updateStaffIfChanged(o, oldStaff, newStaff);
		gui.repaintPanel();
	}

	// ------------------------
	// Gestione movimento nota
	// ------------------------

	private void moveNote(GraphicalNote n, int x, int y) {

		int oldX = n.getX();

		n.moveTo(x, y);
		// TODO: cosa fare con la tie o slur?
		
		int newX = n.getX();

		updateGrid(n, oldX, newX);
		applyHorizontalSnap(n, y, newX);
		updateSlurIfNeeded(n, newX, y);
	}

	private void updateGrid(GraphicalNote n, int oldX, int newX) {
		grid.updatePosition(n, oldX, newX);
	}

	private void applyHorizontalSnap(GraphicalNote n, int snapY, int newX) {
		final int SNAP_DISTANCE = 10;
		for (GraphicalNote other : grid.getNearby(newX)) {
			if (other != n && Math.abs(other.getX() - newX) < SNAP_DISTANCE) {
				n.moveTo(other.getX(), snapY);
				return;
			}
		}
	}

	private void updateSlurIfNeeded(GraphicalNote n, int x, int y) {
		Slur s = n.getSlur();
		if (s == null)
			return;

		if (n.isSlurStart()) {
			s.setX(x);
			s.setY(y);
		} else if (n.isSlurEnd()) {
			s.setX1(x);
			s.setY1(y);
		}
	}

	// ------------------------
	// Oggetti non nota
	// ------------------------

	private void moveGenericObject(GraphicalObject o, int x, int y) {
		o.moveTo(x, y);
	}

	// ------------------------
	// Staff update
	// ------------------------

	private void updateStaffIfChanged(GraphicalObject o, int oldStaff, int newStaff) {
		if (oldStaff == newStaff || oldStaff < 0 || newStaff < 0)
			return;

		saver.removeObject(oldStaff, o);
		saver.addObject(newStaff, o);

		System.out.println("Staff changed: " + oldStaff + " -> " + newStaff);
	}

	public HashMap<GraphicalObject, Integer> getStartXPositions(int mouseX, int mouseY) {
		// cerca l'oggetto alla posizione dopo di quella dove è il mouse
		int staffNumber = gui.getPointedStaffIndex(mouseX, mouseY);
		ArrayList<GraphicalObject> staff = saver.getObjects(staffNumber);
		HashMap<GraphicalObject, Integer> map = new HashMap<>();
		for (int i = 0; i < staff.size(); i++) {
			GraphicalObject g = staff.get(i);
			if (g.getX() >= mouseX - 10) { // il 10 è una tolleranza
				map.put(g, g.getX());
				System.out.println("GraphicalObject in Map saved: " + g.getX());
			}
		}
		return map;
	}

	public void shiftHorizontal(int mouseX, HashMap<GraphicalObject, Integer> startPositions, int staffNumber) {
		if (startPositions.isEmpty())
			return;

		// Prendi la prima nota della mappa come riferimento
		GraphicalObject first = startPositions.keySet().iterator().next();
		int startX = startPositions.get(first);

		int deltaX = mouseX - startX;

		// Sposta tutte le note nella mappa
		for (GraphicalObject o : startPositions.keySet()) {
			int originalX = startPositions.get(o);
			o.setX(originalX + deltaX);
		}

		// Aggiorna lunghezza dello staff se necessario
		ArrayList<GraphicalObject> staff = saver.getObjects(staffNumber);
		GraphicalStaff s = gui.getStaff(staffNumber);
		int maxX = staff.stream().mapToInt(GraphicalObject::getX).max().orElse(0);
		if (maxX > s.getWidth()) {
			s.setWidth(maxX + 50); // margine extra
		}

		gui.repaintPanel();
	}

	public void moveObjects(int mouseX, int mouseY) {

		ArrayList<GraphicalObject> objects = getSelectedObjects();
		if (objects.isEmpty())
			return;

		if (objects.size() == 1) {
			moveObject(objects.get(0), mouseX, mouseY);
			return;
		}

		HashMap<GraphicalObject, Point> startPositions = recordStartPositions(objects);

		Point ref = startPositions.get(objects.get(0));
		int dx = mouseX - ref.x;
		int dy = mouseY - ref.y;

		for (GraphicalObject o : objects) {
			moveMultipleObject(o, startPositions.get(o), dx, dy);
		}

		gui.repaintPanel();
	}

	// ------------------------------------------------------------
	// Salvataggio posizioni iniziali
	// ------------------------------------------------------------
	private HashMap<GraphicalObject, Point> recordStartPositions(List<GraphicalObject> objects) {
		HashMap<GraphicalObject, Point> map = new HashMap<>();
		for (GraphicalObject o : objects) {
			map.put(o, new Point(o.getX(), o.getY()));
		}
		return map;
	}

	// ------------------------------------------------------------
	// Movimento di un singolo oggetto nel gruppo
	// ------------------------------------------------------------
	private void moveMultipleObject(GraphicalObject o, Point start, int dx, int dy) {

		int newX = start.x + dx;
		int newY = start.y + dy;

		int oldStaff = checkInWichStaffIsPoint(start.x, start.y);
		int newStaff = checkInWichStaffIsPoint(newX, newY);

		o.moveTo(newX, newY);

		updateStaffIfChanged(o, oldStaff, newStaff);
	}

	public void mouseReleased(int x, int y) {
		int sn = checkInWichStaffIsPoint(x, y);
		saver.sort(sn);
	}

	private ArrayList<GraphicalObject> getSelectedObjects() {
		ArrayList<GraphicalObject> selectedObjects = new ArrayList<>();
		for (ArrayList<GraphicalObject> staff : saver.getAllStaffs()) {
			for (GraphicalObject object : staff) {
				if (object.isSelected()) {
					selectedObjects.add(object);
				}
			}
		}
		return selectedObjects;
	}

	public void export() {
		Exporter x = new Exporter(gui);
		x.setStaffs(saver.getAllStaffs());
		x.parse();
	}

	public void setCurrentVoice(int i) {
		currentVoice = i;
		
	}


}
