package scoreWriter;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import Measure.Bar;
import Measure.TimeSignature;
import graphical.GraphicalBar;
import graphical.GraphicalClef;
import graphical.GraphicalKeySignature;
import graphical.GraphicalNote;
import graphical.GraphicalObject;
import graphical.GraphicalRest;
import graphical.GraphicalStaff;
import graphical.GraphicalTimeSignature;
import graphical.MusicalSymbol;
import graphical.StaffActionListener;
import musicEvent.Modus;
import musicEvent.Note;
import musicEvent.Rest;
import musicInterface.MusicObject;
import notation.Clef;
import notation.CurvedConnection;
import notation.KeySignature;
import notation.Staff;
import notation.Voice;
import ui.GUI;
import ui.KeySignatureDialog;
import ui.Pointer;
import ui.TimeSignatureDialog;

public class ScoreWriter implements StaffActionListener {

	private notation.Score score;
	private SpatialGrid grid;
	private SelectionManager selectionManager;
	private GUI gui;
	private int voiceNumber = 1;
	private Pointer pointer;

	public ScoreWriter() {
		score = new notation.Score();
		selectionManager = new SelectionManager();
		grid = new SpatialGrid(20);
	}

	private void test() {
/*		addStaff();
		GraphicalClef c = new GraphicalClef(SymbolRegistry.CLEF_TREBLE);
		c.setXY(80, 80);
		score.addObject(c, 0, 0);

		GraphicalKeySignature k = new GraphicalKeySignature(120, gui.getStaff(0), new KeySignature(3, 1, Modus.MAJOR_SCALE));
		score.addObject(k, 0, 0);
		int[] xpos = {200,220,240,260,280,300,320};
		int[] ypos = {100,95,90,85,80,75,70,65};
		for (int i = 0; i<7; i++) {
		GraphicalNote n = new GraphicalNote(SymbolRegistry.QUARTER_NOTE, new Note());
		n.setXY(xpos[i], ypos[i]);
		n.setStaffPosition(-2+i);
		score.addObject(n, 0, 1);

		}
		export();*/
	//	System.exit(0);
	}

	public void addStaff() {
		score.addStaff();
		gui.repaint();
	}

	public int getVoiceType() {
		return voiceNumber;
	}

	public List<notation.Staff> getStaffList() {
		return score.getAllStaves();
	}

	public int getStaffCount() {
		return getStaffList().size();
	}

	/** restituisce una lista con tutti gli oggetti di tutti gli staves */
	public List<MusicObject> getAllObjects() {
		return score.getAllObjects();
	}

	public List<MusicObject> getVoice(int staffNumber, int voiceNumber) {
		return score.getObjects(staffNumber, voiceNumber);
	}

	public List<MusicObject> getVoices(int staffNumber) {
		return score.getObjects(staffNumber);
	}

	/**
	 * Restituisce l'oggetto grafico su cui si è cliccato o <i>null</i>
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public GraphicalObject getObjectAt(int x, int y) {

		// 1) Prima controlla gli oggetti normali
		for (GraphicalObject object : score.getAllObjects()) {
			if (object.hitTest(x, y)) {
				return object;
			}
		}

		// 2) Poi controlla se hai cliccato su uno staff
		int staffIndex = gui.getPointedStaffIndex(x, y);
		if (staffIndex != -1) {
			return gui.getStaff(staffIndex); // o ritorna uno StaffWrapper
		}

		return null;
	}

	public void selectObjectAtPos(int x, int y, boolean multipleSelection) {
		if (score.getStaffCount() == 0) {
			return;
		}
		if (!multipleSelection) {
			selectionManager.clearSelection();
		}
		GraphicalObject o = getObjectAt(x, y);
		if (o == null) {
			return;
		}
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

	private boolean insertNote(GraphicalNote n, int staffNumber, int voiceNumber) {
		if (voiceNumber == 0) {
			return false;
		}
		GraphicalNote newNote = (GraphicalNote) n.cloneObject();
		newNote.setVoice(voiceNumber);
		newNote.setStaffIndex(staffNumber);
		GraphicalStaff staff = gui.getStaff(staffNumber);
		int position = staff.getPosInStaff(newNote);
		newNote.setStaffPosition(position);
		score.addObject(newNote, staffNumber, voiceNumber);
		grid.add(newNote);
		// se necessario allunga i pentagrammi
		GraphicalStaff g = gui.getStaff(0);
		int x = newNote.getX();
		if (x > g.getWidth() - 100) {
			resizeStaves();
		}
		return true;
	}

	private boolean insertRest(GraphicalRest n, int staffNumber, int voiceNumber) {
		if (voiceNumber == 0) {
			return false;
		}
		GraphicalRest newNote = (GraphicalRest) n.cloneObject();
		score.addObject(newNote, staffNumber, voiceNumber);
		grid.add(newNote);
		// se necessario allunga i pentagrammi
		GraphicalStaff g = gui.getStaff(0);
		int x = newNote.getX();
		if (x > g.getWidth() - 100) {
			resizeStaves();
		}
		return true;
	}

	private void resizeStaves() {
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
		int firstLine = gui.getStaff(staffNumber).getYPosOfLine(1);
		b.setY(firstLine);
		score.addObject(b, staffNumber, 0);
	}

	private void insertClef(GraphicalClef clef, int staffNumber) {
		GraphicalClef c = (GraphicalClef) clef.cloneObject();
		int firstLine = 0;
		if (clef.getSymbol().equals(SymbolRegistry.CLEF_TREBLE)
				|| clef.getSymbol().equals(SymbolRegistry.CLEF_TREBLE_8)) {
			firstLine = gui.getStaff(staffNumber).getYPosOfLine(2);
		} else if (clef.getSymbol().equals(SymbolRegistry.CLEF_BASS)) {
			firstLine = gui.getStaff(staffNumber).getYPosOfLine(4);
		}
		c.setY(firstLine);
		score.addObject(c, staffNumber, 0);
	}

	public void insertObject(MusicalSymbol symbolToInsert, int x, int y) {
	    int staffNumber = checkInWichStaffIsPoint(x, y);
	    if (staffNumber == -1) {
			return;
		}

	    selectionManager.clearSelection();

	    // Creazione del GraphicalObject dal simbolo
	    GraphicalObject obj = createGraphicalObject(symbolToInsert, x, y, staffNumber);

	    // Impostazione posizione e selezione
	    selectionManager.select(obj, staffNumber);

	    // Inserimento nel modello (Score) e/o staff
	    // voice 0 per staff-wide
	    int v = 0;
	    if (obj instanceof GraphicalNote ||
	    		obj instanceof GraphicalRest) {
	    	v = voiceNumber;
	    }
	    // scrivo <= perché una voice (la 0) è staff-wide.
	    // quindi quando ci sono 2 voci, in realtà ce n'è una sola per note
	    if (score.getStaff(staffNumber).getNumberOfVoices() <= voiceNumber) {
	    	score.getStaff(staffNumber).addVoice();
	    }
	    score.addObject(obj, staffNumber, v);

	    // Aggiornamento GUI
	    gui.repaintPanel();
	}

	private KeySignature getPreviousKeySignature(GraphicalNote n) {

	    int noteX = n.getX();
	    int staffIndex = n.getStaffIndex();

	    Staff staff = score.getStaff(staffIndex);
	    Voice staffVoice = staff.getVoice(0); // staff-wide

	    List<GraphicalObject> objs = staffVoice.getObjects();

	    for (int i = objs.size() - 1; i >= 0; i--) {
	        GraphicalObject go = objs.get(i);

	        if (go.getX() <= noteX && go instanceof GraphicalKeySignature) {
	            return ((GraphicalKeySignature) go).getKeySignature();
	        }
	    }

	    return null; // nessuna armatura precedente → Do maggiore
	}

	private GraphicalObject createGraphicalObject(MusicalSymbol symbolToInsert, int x, int y, int staffNumber) {
	    switch (symbolToInsert.getType()) {
	        case NOTE: {
	            Note n = new Note();
	            n.setDuration(symbolToInsert.getDuration());
	            GraphicalNote gn = new GraphicalNote(symbolToInsert, n);
	            gn.setXY(x, y);
	            GraphicalStaff gs = gui.getStaff(staffNumber);
	            int position = gs.getPosInStaff(gn);
	    		gn.setStaffPosition(position);
	            return gn;
	        }

	        case REST: {
	            Rest r = new Rest(symbolToInsert.getDuration(), 0);
	            GraphicalRest gr = new GraphicalRest(symbolToInsert, r);
	            gr.setXY(x, y);
	            return gr;
	        }

	        case CLEF: {
	            GraphicalClef clef = new GraphicalClef(symbolToInsert);
	            int firstLine = 0;
	    		if (clef.getSymbol().equals(SymbolRegistry.CLEF_TREBLE)
	    				|| clef.getSymbol().equals(SymbolRegistry.CLEF_TREBLE_8)) {
					firstLine = gui.getStaff(staffNumber).getYPosOfLine(1);
				} else if (clef.getSymbol().equals(SymbolRegistry.CLEF_BASS)) {
					firstLine = gui.getStaff(staffNumber).getYPosOfLine(3);
				}
	    		clef.setXY(x, firstLine);
	            return clef;
	        }

	        case BARLINE: {
	            Bar b = getBar(symbolToInsert);
	            GraphicalBar gb = new GraphicalBar(symbolToInsert, b);
	            int firstLine = gui.getStaff(staffNumber).getYPosOfLine(0);
	            gb.setXY(x, firstLine);
	            return gb;
	        }

	        default:
	            throw new IllegalArgumentException("Tipo di simbolo non supportato: " + symbolToInsert.getType());
	    }
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
			if (gs.hitTest(x, y)) {
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
		for (int i = 0; i < selectionManager.getNumberOfStaves(); i++) {
			List<GraphicalNote> selectedGraphicalNotes = selectionManager.getSelectedNotesFromStaff(i);
			List<Note> selectedNotes = getNotesFromGraphicalList(selectedGraphicalNotes);
			if (selectedNotes.isEmpty()) {
				continue;
			}

			if (selectedNotes.size() == 1) {
				Note n1 = selectedNotes.get(0);
				Note n2 = score.getNextNote(n1);
				if (n2 == null) {
					continue;
				}

				if (n1.getY() == n2.getY()) {
					tie(n1, n2, i);
				} else {
					slur(n1, n2, i);
				}

				continue; // passa al prossimo staff
			}

			for (int j = 0; j < selectedNotes.size() - 1; j++) {
				GraphicalNote n1 = selectedNotes.get(j);
				GraphicalNote n2 = selectedNotes.get(j + 1);

				if (hasSameHeight(n1, n2) && score.areNotesConsecutive(n1, n2)) {
					tie(n1, n2, i); // usa i invece di j
				} else {
					slur(n1, n2, i);
				}
			}
		}
		gui.repaintPanel();
	}

	private boolean hasSameHeight(GraphicalNote n1, GraphicalNote n2) {
		return n1.getY() == n2.getY();
	}

	private void addCurve(CurvedConnection curve, GraphicalNote n1, GraphicalNote n2, int staffNumber) {
		curve.assignToNotes(n1, n2);
		int voiceNumber = getLayerOf(n1);
		if (voiceNumber != -1) {
			score.addObject(curve, staffNumber, voiceNumber);
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

	// TODO cancella anche tie e slur se cancellata la nota
	private void deleteSelectedObject() {
		List<GraphicalObject> selectedObjects = getSelectedObjects();
		if (selectedObjects.isEmpty()) {
			return;
		}

		// prima puliamo le note se l'oggetto è curva
		for (GraphicalObject obj : selectedObjects) {
			if (obj instanceof CurvedConnection) {
				((CurvedConnection) obj).removeFromNotes();
			}
		}

		// rimuovi dagli strati
		for (Staff staff : score.getAllStaves()) {
			for (Voice layer : staff.getVoices()) {
				layer.getObjects().removeAll(selectedObjects);
			}
		}

		// rimuovi dal grid solo le note
		for (GraphicalObject obj : selectedObjects) {
			if (obj instanceof GraphicalNote) {
				grid.remove(obj);
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
		} else if (o instanceof GraphicalKeySignature) {
			moveKeySignature((GraphicalKeySignature) o, x);
		} else {
			moveGenericObject(o, x, y);
		}

		updateStaffIfChanged(o, oldStaff, newStaff);
		gui.repaintPanel();
	}

	// ------------------------
	// Gestione movimento nota
	// ------------------------

	private void moveNote(GraphicalNote note, int x, int y) {

		int oldX = note.getX();

		CurvedConnection curve = note.getCurvedConnection();

		if (curve != null) {
			// nota di partenza della curva
			if (note.isCurveStart()) {
				GraphicalNote start = note;
				GraphicalNote end = curve instanceof Tie tie ? score.getNextNote(start)
						: curve instanceof Slur slur ? slur.getEndNote() : null;

				if (end != null && x > end.getX()) {
					x = end.getX();
				}

				start.moveTo(x, y);
				if (curve instanceof Tie) {
					// muovi la seconda nota solo verticalmente
					end.moveTo(end.getX(), y);
				}
				curve.setXY(start.getX(), start.getY());
				if (end != null) {
					curve.setX1Y1(end.getX(), end.getY());
				}
			}
			// nota di arrivo della curva
			else if (note.isCurveEnd()) {
				GraphicalNote end = note;
				GraphicalNote start = curve instanceof Tie tie ? score.getPrevNote(end)
						: curve instanceof Slur slur ? slur.getStartNote() : null;

				if (start != null && x < start.getX()) {
					x = start.getX();
				}

				end.moveTo(x, y);
				if (curve instanceof Tie) {
					// muovi la prima nota solo verticalmente
					start.moveTo(start.getX(), y);
				}
				if (start != null) {
					curve.setXY(start.getX(), start.getY());
				}
				curve.setX1Y1(end.getX(), end.getY());
			} else {
				// curva presente ma nota interna, solo movimento
				note.moveTo(x, y);
			}
		} else {
			// nota senza curva
			note.moveTo(x, y);
		}

		int newX = note.getX();
		updateGrid(note, oldX, newX);
		applyHorizontalSnap(note, y, newX);
		updateSlurIfNeeded(note, newX, y);
	}

	// ------------------------
	// Gestione movimento keysignature
	// ------------------------
	private void moveKeySignature(GraphicalKeySignature ks, int x) {
		ks.moveTo(x, ks.getY());
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
		if (!(o instanceof GraphicalNote)) {
			return;
		}
		GraphicalNote n = (GraphicalNote) o;
		Slur s = n.getSlur();
		if (s == null) {
			return;
		}

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
		if (o == null || oldStaff == newStaff || oldStaff < 0 || newStaff < 0) {
			return;
		}

		List<Staff> staves = score.getAllStaves();

		if (oldStaff >= staves.size() || newStaff >= staves.size()) {
			System.err.println("Staff index out of range!");
			return;
		}

		Staff oldS = staves.get(oldStaff);
		Staff newS = staves.get(newStaff);

		// trova il layer del vecchio staff
		Voice oldLayer = null;
		for (Voice layer : oldS.getVoices()) {
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
		Voice newLayer = newS.getVoice(oldLayer.getVoiceType());
		newLayer.addObject(o);

		System.out.println("Staff changed: " + oldStaff + " -> " + newStaff);
	}

	public HashMap<GraphicalObject, Integer> getStartXPositions(int mouseX, int mouseY) {
		// cerca l'oggetto alla posizione dopo di quella dove è il mouse
		int staffNumber = gui.getPointedStaffIndex(mouseX, mouseY);
		List<GraphicalObject> staff = score.getObjects(staffNumber);
		HashMap<GraphicalObject, Integer> map = new HashMap<>();
		for (GraphicalObject g : staff) {
			if (g.getX() >= mouseX - 10) { // il 10 è una tolleranza
				map.put(g, g.getX());
				System.out.println("GraphicalObject in Map saved: " + g.getX());
			}
		}
		return map;
	}

	public void shiftHorizontal(int mouseX, HashMap<GraphicalObject, Integer> startPositions, int staffNumber) {
		if (startPositions.isEmpty()) {
			return;
		}

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
		if (objects.isEmpty()) {
			return;
		}

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

		Exporter x = new Exporter();
		x.export(score);
		x.printScore();
	}

	public void setCurrentVoice(int i) {
		voiceNumber = i;
	}

	/**
	 * Restituisce il tipo di voce (int) del layer che contiene la nota n. Ritorna
	 * -1 se la nota non è presente in nessun layer.
	 */
	private int getLayerOf(GraphicalNote n) {
		if (n == null) {
			return -1;
		}

		for (Staff staff : score.getAllStaves()) {
			for (Voice layer : staff.getVoices()) {
				if (layer.getObjects().contains(n)) {
					return layer.getVoiceType();
				}
			}
		}

		return -1; // nota non trovata
	}

	public void setPointer(MusicalSymbol barlineSymbol) {
		pointer = new Pointer(this, barlineSymbol);
	}

	public Pointer getPointer() {
		return pointer;
	}

	public void movePointerTo(int mouseX, int snapY) {
		pointer.moveTo(mouseX, snapY);
		int index = checkInWichStaffIsPoint(mouseX, snapY);
		pointer.setStaffIndex(index);
	}

	public void destroyPointer() {
		pointer = null;
	}

	public boolean pointerExists() {
		return pointer != null;
	}


	public List<String> getLyricsFor(int staff, int voice, int stanza) {
		return score.getLyricsFor(staff, voice, stanza);
	}



	public void addLyrics(List<String> syllables, int staffIndex, int voiceNumber, int stanza) {
		// --- CONTROLLI ---
		if (score == null || score.getStaffCount() == 0) {
			JOptionPane.showMessageDialog(null, "Nessuno staff disponibile nel punteggio.", "Errore Lyrics",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		if (staffIndex < 0 || staffIndex >= score.getStaffCount()) {
			JOptionPane.showMessageDialog(null, "Staff selezionato non valido.", "Errore Lyrics",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		score.addLyrics(syllables, staffIndex, voiceNumber, stanza);

	}

	public void setKeySignature(int x, int y) {
		IntPair pair = KeySignatureDialog.showDialog(gui);
		if (pair == null) {
			return;
		}
		setKeySignature(pair.first, x, y);
	}

	private void setKeySignature(int chosenValue, int x, int y) {
		int type = 0;
		if (chosenValue < 0) {
			type = -1;
		} else if (chosenValue > 0) {
			type = 1;
		}
		int alterationsNumber = Math.abs(chosenValue);
		KeySignature ks = new KeySignature(alterationsNumber, type, Modus.MAJOR_SCALE); //TODO
		int staffIndex = gui.getPointedStaffIndex(x, y);
		GraphicalStaff staff = gui.getPointedStaff(x, y);

		GraphicalKeySignature gks = new GraphicalKeySignature(x, staff, ks);
		score.addObject(gks, staffIndex, 0);
		gui.repaintPanel();
	}

	public void setTimeSignature(int x, int y) {
		IntPair pair = TimeSignatureDialog.showDialog(gui);
		if (pair == null) {
			return;
		}
		setTimeSignature(pair.first, pair.second, x, y);
	}

	private void setTimeSignature(int n, int d, int x, int y) {
		TimeSignature ts = new TimeSignature(n, d);
		int staffIndex = gui.getPointedStaffIndex(x, y);
		GraphicalTimeSignature gts = new GraphicalTimeSignature(ts, gui.getStaff(staffIndex));
		gts.setXY(x, y);
		score.addObject(gts, staffIndex, 0);
		gui.repaintPanel();
	}

	public Clef createClef(MusicalSymbol clefSymbol) {
		if (clefSymbol.equals(SymbolRegistry.CLEF_TREBLE)) {
			return Clef.TREBLE;
		} else if (clefSymbol.equals(SymbolRegistry.CLEF_BASS)) {
			return Clef.BASS;
		} else if (clefSymbol.equals(SymbolRegistry.CLEF_TREBLE_8)) {
			return Clef.TREBLE_8;
		} else {
			throw new IllegalArgumentException("Clef non supportata");
		}
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

	private List<Note> getNotesFromGraphicalList(List<GraphicalNote> l) {
		List<Note> list = new ArrayList<>();
		for (GraphicalNote g : l) {
			list.add(g.getNote());
		}
		return list;
	}

	@Override
	public void requestKeySignature(Staff staff) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestTimeSignature(Staff staff) {
		// TODO Auto-generated method stub

	}

}
