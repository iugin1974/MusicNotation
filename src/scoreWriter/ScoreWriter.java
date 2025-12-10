package scoreWriter;

import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import musicInterface.MusicObject;

public class ScoreWriter {

	private Score score;
	private SpatialGrid grid;
	private SelectionManager selectionManager;
	private GUI gui;
	private int voiceNumber = 1;
	private Pointer pointer;
	private Lyrics lyrics;

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
//		Syllable s1 = new Syllable("Hal");
//		Syllable s2 = new Syllable("lo");
//		Lyric l1 = new Lyric(s1, n1, 0, 1, 0);
//		Lyric l2 = new Lyric(s2, n2, 0, 1, 1);
//		lyrics = new Lyrics();
//		lyrics.addLyric(l1);
//		lyrics.addLyric(l2);
//		n1.addLyric(l1);
//		n2.addLyric(l2);
		c.setXY(50, 80);
		n1.setXY(100, 100);
		n2.setXY(200, 80);
		score.addObject(c, 0, 0);
		score.addObject(n1, 0, 1);
		score.addObject(n2, 0, 1);
		int[] pos = gui.getStaff(0).getYPosOfLinesAndSpacesExtended(0, 9);
		GraphicalKeySignature k = new GraphicalKeySignature(230, gui.getStaff(0), 7, -1);
		score.addObject(k, 0, 0);
		// export();
		// System.exit(0);
	}

	public void addStaff() {
		score.addStaff();
		selectionManager.addStaff();
		gui.addStaff(score.getStaffCount() - 1);
		gui.repaint();
	}

	public int getVoiceType() {
		return voiceNumber;
	}

	public List<Staff> getStaffList() {
		return score.getAllStaves();
	}

	public int getStaffCount() {
		return getStaffList().size();
	}

	/** restituisce una lista con tutti gli oggetti di tutti gli staves */
	public List<GraphicalObject> getAllObjects() {
		return score.getAllObjects();
	}

	public List<GraphicalObject> getVoice(int staffNumber, int voiceNumber) {
		return score.getObjects(staffNumber, voiceNumber);
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

	private boolean insertNote(GraphicalNote n, int staffNumber, int voiceNumber) {
		if (voiceNumber == 0)
			return false;
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
		if (x > g.getWidth() - 100)
			resizeStaves();
		return true;
	}

	private boolean insertRest(GraphicalRest n, int staffNumber, int voiceNumber) {
		if (voiceNumber == 0)
			return false;
		GraphicalRest newNote = (GraphicalRest) n.cloneObject();
		score.addObject(newNote, staffNumber, voiceNumber);
		grid.add(newNote);
		// se necessario allunga i pentagrammi
		GraphicalStaff g = gui.getStaff(0);
		int x = newNote.getX();
		if (x > g.getWidth() - 100)
			resizeStaves();
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
				|| clef.getSymbol().equals(SymbolRegistry.CLEF_TREBLE_8))
			firstLine = gui.getStaff(staffNumber).getYPosOfLine(2);
		else if (clef.getSymbol().equals(SymbolRegistry.CLEF_BASS))
			firstLine = gui.getStaff(staffNumber).getYPosOfLine(4);
		c.setY(firstLine);
		score.addObject(c, staffNumber, 0);
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
			insertNote((GraphicalNote) object, staffNumber, voiceNumber);
		else if (object instanceof GraphicalRest)
			insertRest((GraphicalRest) object, staffNumber, voiceNumber);
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
			ArrayList<GraphicalNote> selectedNotes = selectionManager.getSelectedNotesFromStaff(i);
			if (selectedNotes.isEmpty())
				continue;

			if (selectedNotes.size() == 1) {
				GraphicalNote n1 = selectedNotes.get(0);
				GraphicalNote n2 = score.getNextNote(n1);
				if (n2 == null)
					continue;

				if (n1.getY() == n2.getY())
					tie(n1, n2, i);
				else
					slur(n1, n2, i);

				continue; // passa al prossimo staff
			}

			for (int j = 0; j < selectedNotes.size() - 1; j++) {
				GraphicalNote n1 = selectedNotes.get(j);
				GraphicalNote n2 = selectedNotes.get(j + 1);

				if (hasSameHeight(n1, n2) && score.areNotesConsecutive(n1, n2))
					tie(n1, n2, i); // usa i invece di j
				else
					slur(n1, n2, i);
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
		if (selectedObjects.isEmpty())
			return;

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

		if (n instanceof GraphicalNote note) {
			CurvedConnection curve = note.getCurvedConnection();

			if (curve != null) {
				// nota di partenza della curva
				if (note.isCurveStart()) {
					GraphicalNote start = note;
					GraphicalNote end = curve instanceof Tie tie ? score.getNextNote(start)
							: curve instanceof Slur slur ? slur.getEndNote() : null;

					if (end != null && x > end.getX())
						x = end.getX();

					start.moveTo(x, y);
					if (curve instanceof Tie) {
						// muovi la seconda nota solo verticalmente
						end.moveTo(end.getX(), y);
					}
					curve.setXY(start.getX(), start.getY());
					if (end != null)
						curve.setX1Y1(end.getX(), end.getY());
				}
				// nota di arrivo della curva
				else if (note.isCurveEnd()) {
					GraphicalNote end = note;
					GraphicalNote start = curve instanceof Tie tie ? score.getPrevNote(end)
							: curve instanceof Slur slur ? slur.getStartNote() : null;

					if (start != null && x < start.getX())
						x = start.getX();

					end.moveTo(x, y);
					if (curve instanceof Tie) {
						// muovi la prima nota solo verticalmente
						start.moveTo(start.getX(), y);
					}
					if (start != null)
						curve.setXY(start.getX(), start.getY());
					curve.setX1Y1(end.getX(), end.getY());
				} else {
					// curva presente ma nota interna, solo movimento
					n.moveTo(x, y);
				}
			} else {
				// nota senza curva
				n.moveTo(x, y);
			}
		}

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
		if (o instanceof GraphicalNote == false)
			return;
		GraphicalNote n = (GraphicalNote) o;
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
		ScoreParser parser = new ScoreParser(score);
		List<ParsedStaff> parsedStaves = parser.parse();
		for (ParsedStaff parsedStaff : parsedStaves) {
			if (!parsedStaff.startsWithClef()) {
				System.out.println("Manca la chiave in uno staff");
				return;
			}
		}
		Exporter x = new Exporter();
		x.export(parsedStaves);
		x.printScore();
	}

	public void setCurrentVoice(int i) {
		switch (i) {
		case 1:
			voiceNumber = 1;
			break;
		case 2:
			voiceNumber = 2;
			break;
		}
	}

	/**
	 * Restituisce il tipo di voce (int) del layer che contiene la nota n. Ritorna
	 * -1 se la nota non è presente in nessun layer.
	 */
	private int getLayerOf(GraphicalNote n) {
		if (n == null)
			return -1;

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

	private void removeLyrics(int staffIndex, int voiceNumber, int stanza) {
	    Staff s = score.getStaff(staffIndex);
	    Voice v = s.getVoice(voiceNumber);
	    List<GraphicalNote> notes = v.getNotes();

	    for (GraphicalNote n : notes) {
	        n.removeLyric(stanza);
	    }
	    
	    // rimuovere anche dal contenitore globale
	    if (lyrics != null) {
	        lyrics.removeLyrics(staffIndex, voiceNumber, stanza);
	    }
	}

	public List<String> getLyricsFor(int staff, int voice, int stanza) {
		if (lyrics == null) return null;
		List<Lyric> l = lyrics.getLyrics(staff, voice, stanza);
	    List<String> list = new ArrayList<>();

	    // Scorre tutte le note del sistema
	    for (Lyric lyric : l) {
	            list.add(lyric.getSyllable().getText());
	    }
	    return list;
	}
	
	/**
	 * Determina se una nota deve ricevere la lyric.
	 * 
	 * @param note      La nota corrente
	 * @param connected Flag che indica se siamo dentro una curva
	 * @return true se la nota deve ricevere lyric, false altrimenti
	 */
	private boolean shouldAssignLyric(GraphicalNote note, boolean connected) {
		if (!connected) {
			// Nota singola o inizio curva → lyric sì
			return true;
		} else {
			// Dentro curva → skip
			return false;
		}
	}

	public void addLyrics(List<String> syllables, int staffIndex, int voiceNumber, int stanza) {
	    // --- CONTROLLI ---
	    if (score == null || score.getStaffCount() == 0) {
	        JOptionPane.showMessageDialog(null, 
	            "Nessuno staff disponibile nel punteggio.", 
	            "Errore Lyrics", 
	            JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    if (staffIndex < 0 || staffIndex >= score.getStaffCount()) {
	        JOptionPane.showMessageDialog(null, 
	            "Staff selezionato non valido.", 
	            "Errore Lyrics", 
	            JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    Staff s = score.getStaff(staffIndex);

	    if (voiceNumber < 0 || voiceNumber >= s.getVoices().size()) {
	        JOptionPane.showMessageDialog(null, 
	            "Voce selezionata non valida.", 
	            "Errore Lyrics", 
	            JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    if (stanza < 0 || stanza >= 10) { // supponendo massimo 10 strofe
	        JOptionPane.showMessageDialog(null, 
	            "Stanza selezionata non valida.", 
	            "Errore Lyrics", 
	            JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    // --- RIMOZIONE VECCHIE LYRICS ---
	    if (lyrics == null) {
	        lyrics = new Lyrics();
	    } else {
	        removeLyrics(staffIndex, voiceNumber, stanza);
	    }

	    Voice v = s.getVoice(voiceNumber);
	    List<GraphicalNote> notes = v.getNotes();

	    int syllableIndex = 0; // indice sillaba
	    int noteIndex = 0;     // indice nota
	    boolean connected = false;

	    while (syllableIndex < syllables.size() && noteIndex < notes.size()) {
	        GraphicalNote note = notes.get(noteIndex);
	        String syllable = syllables.get(syllableIndex);

	        // --- SILLABE SPECIALI ---
	        if ("_".equals(syllable)) {
	            syllableIndex++;
	            noteIndex++;
	            continue;
	        }
	        if ("--".equals(syllable) || "__".equals(syllable)) {
	            syllableIndex++;
	            continue;
	        }

	        // --- CONTROLLA CURVE ---
	        if (shouldAssignLyric(note, connected)) {
	            Syllable syl = new Syllable(syllable);
	            Lyric l = new Lyric(syl, note, staffIndex, voiceNumber, stanza);
	            lyrics.addLyric(l);
	            syllableIndex++;

	            if (note.isCurveStart())
	                connected = true;
	        } else {
	            // dentro curva → skip note
	            if (note.isCurveEnd())
	                connected = false;
	        }

	        noteIndex++;
	    }
	}
}
