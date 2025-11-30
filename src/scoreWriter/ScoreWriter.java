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
import scoreWriter.VoiceLayer.VoiceType;

public class ScoreWriter {

	private Score score;
	private SpatialGrid grid;
	private SelectionManager selectionManager;
	private GUI gui;
	private VoiceType voiceType = VoiceType.VOICE_ONE;
	private Pointer pointer;

	public ScoreWriter() {
		score = new Score();
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
		score.addObject(c, 0, VoiceType.STAFF_WIDE);
		score.addObject(n1, 0, VoiceType.VOICE_ONE);
		score.addObject(n2, 0, VoiceType.VOICE_ONE);
	}

	public void addStaff() {
		List<GraphicalObject> staffForSelectedObjects = new ArrayList<>();
		score.addStaff();
		selectionManager.addStaff();
		gui.addStaff(score.getStaffCount() - 1);
		gui.repaint();
	}

	public VoiceType getVoiceType() {
		return voiceType;
	}

	public List<Staff> getStaffList() {
		return score.getAllStaves();
	}

	/** restituisce una lista con tutti gli oggetti di tutti gli staves */
	public List<GraphicalObject> getAllObjects() {
		return score.getAllObjects();
	}

	public List<GraphicalObject> getVoice(int staffNumber, VoiceType voiceType) {
		return score.getObjects(staffNumber, voiceType);
	}

	public List<GraphicalObject> getVoices(int staffNumber) {
		return score.getObjects(staffNumber);
	}

	private GraphicalObject getObjectAt(int x, int y) {
		for (GraphicalObject object : score.getAllObjects()) {
			if (object.contains(x, y))
				return object;
		}
		return null;
	}

	public void selectObjectAtPos(int x, int y, boolean multipleSelection) {
		if (score.getStaffCount() == 0)
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

	private boolean insertNote(GraphicalNote n, int staffNumber, VoiceType voiceType) {
		if (voiceType == VoiceType.STAFF_WIDE)
			return false;
		GraphicalNote newNote = (GraphicalNote) n.cloneObject();
		if (voiceType == VoiceType.VOICE_ONE)
			newNote.setStemDirection(GraphicalNote.STEM_UP);
		else if (voiceType == VoiceType.VOICE_TWO)
			newNote.setStemDirection(GraphicalNote.STEM_DOWN);
		score.addObject(newNote, staffNumber, voiceType);
		grid.add(newNote);
		// se necessario allunga i pentagrammi
		GraphicalStaff g = gui.getStaff(0);
		int x = newNote.getX();
		if (x > g.getWidth() - 100)
			resizeStaves();
		return true;
	}
	
	private boolean insertRest(GraphicalRest n, int staffNumber, VoiceType voiceType) {
		if (voiceType == VoiceType.STAFF_WIDE)
			return false;
		GraphicalRest newNote = (GraphicalRest) n.cloneObject();
		score.addObject(newNote, staffNumber, voiceType);
		grid.add(newNote);
		// se necessario allunga i pentagrammi
		GraphicalStaff g = gui.getStaff(0);
		int x = newNote.getX();
		if (x > g.getWidth() - 100)
			resizeStaves();
		return true;
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
		score.addObject(b, staffNumber, VoiceType.STAFF_WIDE);
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
		score.addObject(c, staffNumber, VoiceType.STAFF_WIDE);
	}

	public void insertObject(GraphicalObject object) {
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
			insertNote((GraphicalNote) object, staffNumber, voiceType);
		else if (object instanceof GraphicalRest)
			insertRest((GraphicalRest) object, staffNumber, voiceType);
		else if (object instanceof GraphicalBar)
			insertBar((GraphicalBar) object, staffNumber);
		else if (object instanceof GraphicalClef)
			insertClef((GraphicalClef) object, staffNumber);
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

		for (int i = 0; i < score.getStaffCount(); i++) {
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
				GraphicalNote n2 = score.getNextNote(n1);
				if (n2 == null)
					return;
				if (n1.getY() == n2.getY())
					tie(n1, n2, i);
				else
					slur(n1, n2, i);

				break;
			}
			// altrimenti tra le due selezionate
			for (int j = 0; j < selectedNotes.size() - 1; j++) {
				GraphicalNote n1 = selectedNotes.get(j);
				GraphicalNote n2 = selectedNotes.get(j + 1);
				// le note devono aver la stessa altezza ed essere consecutive
				if (hasSameHeight(n1, n2) && score.areNotesConsecutive(n1, n2))
					tie(n1, n2, j);
				else
					slur(n1, n2, j);
			}
		}
		gui.repaintPanel();
	}

	private boolean hasSameHeight(GraphicalNote n1, GraphicalNote n2) {
		return n1.getY() == n2.getY();
	}

	private void addCurve(CurvedConnection curve, GraphicalNote n1, GraphicalNote n2, int staffNumber) {
		curve.setNotes(n1, n2);
		VoiceType voiceType = getLayerOf(n1);
		if (voiceType != null) {
			score.addObject(curve, staffNumber, voiceType);
		} else {
			System.err.println("Errore: nota iniziale non trovata in nessun layer!");
		}
	}

	private void tie(GraphicalNote n1, GraphicalNote n2, int staffNumber) {
		addCurve(new Tie(), n1, n2, staffNumber);
	}

	private void slur(GraphicalNote n1, GraphicalNote n2, int staffNumber) {
		addCurve(new Slur(), n1, n2, staffNumber);
	}

	private void deleteSelectedObject() {
		List<GraphicalObject> selectedObjects = getSelectedObjects();
		if (selectedObjects.isEmpty())
			return;

		for (Staff staff : score.getAllStaves()) {
			for (VoiceLayer layer : staff.getVoices()) {
				layer.getObjects().removeAll(selectedObjects);
			}
		}

		// rimuovi anche dal grid solo le note
		for (GraphicalObject obj : selectedObjects) {
			if (obj instanceof GraphicalNote)
				grid.remove((GraphicalNote) obj);
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

	private void moveNote(GraphicalObject n, int x, int y) {

		int oldX = n.getX();

		n.moveTo(x, y);
		// TODO: cosa fare con la tie o slur?

		int newX = n.getX();

		updateGrid(n, oldX, newX);
		applyHorizontalSnap(n, y, newX);
		updateSlurIfNeeded(n, newX, y);
	}

	private void updateGrid(GraphicalObject n, int oldX, int newX) {
		grid.updatePosition(n, oldX, newX);
	}

	private void applyHorizontalSnap(GraphicalObject n, int snapY, int newX) {
		final int SNAP_DISTANCE = 10;
		for (GraphicalObject other : grid.getNearby(newX)) {
			if (other != n && Math.abs(other.getX() - newX) < SNAP_DISTANCE) {
				n.moveTo(other.getX(), snapY);
				return;
			}
		}
	}

	private void updateSlurIfNeeded(GraphicalObject o, int x, int y) {
		if (o instanceof GraphicalNote == false) return;
		GraphicalNote n = (GraphicalNote)o;
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

	/**
	 * Sposta un oggetto da uno staff a un altro, mantenendo il layer originale.
	 */
	private void updateStaffIfChanged(GraphicalObject o, int oldStaff, int newStaff) {
		if (o == null || oldStaff == newStaff || oldStaff < 0 || newStaff < 0)
			return;

		List<Staff> staves = score.getAllStaves();

		if (oldStaff >= staves.size() || newStaff >= staves.size()) {
			System.err.println("Staff index out of range!");
			return;
		}

		Staff oldS = staves.get(oldStaff);
		Staff newS = staves.get(newStaff);

		// trova il layer del vecchio staff
		VoiceLayer oldLayer = null;
		for (VoiceLayer layer : oldS.getVoices()) {
			if (layer.getObjects().contains(o)) {
				oldLayer = layer;
				break;
			}
		}

		if (oldLayer == null) {
			System.err.println("Oggetto non trovato nello staff vecchio!");
			return;
		}

		// rimuovi dall’oldLayer
		oldLayer.removeObject(o);

		// aggiungi nello stesso tipo di layer nel nuovo staff
		VoiceLayer newLayer = newS.getVoice(oldLayer.getVoiceType());
		newLayer.addObject(o);

		System.out.println("Staff changed: " + oldStaff + " -> " + newStaff);
	}

	public HashMap<GraphicalObject, Integer> getStartXPositions(int mouseX, int mouseY) {
		// cerca l'oggetto alla posizione dopo di quella dove è il mouse
		int staffNumber = gui.getPointedStaffIndex(mouseX, mouseY);
		List<GraphicalObject> staff = score.getObjects(staffNumber);
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
		List<GraphicalObject> staff = score.getObjects(staffNumber);
		GraphicalStaff s = gui.getStaff(staffNumber);
		int maxX = staff.stream().mapToInt(GraphicalObject::getX).max().orElse(0);
		if (maxX > s.getWidth()) {
			s.setWidth(maxX + 50); // margine extra
		}

		gui.repaintPanel();
	}

	public void moveObjects(int mouseX, int mouseY) {

		List<GraphicalObject> objects = getSelectedObjects();
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

	private List<GraphicalObject> getSelectedObjects() {
		List<GraphicalObject> selectedObjects = new ArrayList<>();
		for (GraphicalObject object : score.getAllObjects()) {
			if (object.isSelected()) {
				selectedObjects.add(object);
			}
		}
		return selectedObjects;
	}

	public void export() {
		Exporter x = new Exporter(gui);
		x.setScore(score);
		x.parse();
		x.printScore();
	}

	public void setCurrentVoice(int i) {
		switch (i) {
		case 1:
			voiceType = VoiceType.VOICE_ONE;
			break;
		case 2:
			voiceType = VoiceType.VOICE_TWO;
			break;
		}
	}

	/**
	 * Restituisce il tipo di voce (VoiceType) del layer che contiene la nota n.
	 * Ritorna null se la nota non è presente in nessun layer.
	 */
	private VoiceType getLayerOf(GraphicalNote n) {
		if (n == null)
			return null;

		for (Staff staff : score.getAllStaves()) {
			for (VoiceLayer layer : staff.getVoices()) {
				if (layer.getObjects().contains(n)) {
					return layer.getVoiceType();
				}
			}
		}

		return null; // nota non trovata
	}

	public void setPointer(MusicalSymbol barlineSymbol) {
		pointer = new Pointer(this, barlineSymbol);
	}

	public Pointer getPointer() {
		return pointer;
	}

	public void movePointerTo(int mouseX, int snapY) {
		pointer.moveTo(mouseX, snapY);
	}

	public void destroyPointer() {
		pointer = null;
	}

	public boolean pointerExists() {
		return pointer != null;
	}
}
