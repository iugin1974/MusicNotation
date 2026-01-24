/*
 *  TODO
 *  - lyrics: -- e __ non vengono esportati (ha a che fare col
 *  fatto che vengono salvati nelle note?
 *  - salvataggio
 *  - mousePressed e Mouseclicked fanno praticamente la stessa cosa
 *  
*/
package scoreWriter;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import Measure.Bar;
import Measure.TimeSignature;
import graphical.GraphicalCurvedConnection;
import graphical.GraphicalNote;
import graphical.GraphicalObject;
import graphical.GraphicalScore;
import graphical.GraphicalStaff;
import graphical.MusicalSymbol;
import graphical.MusicalSymbol.Type;
import graphical.StaffActionListener;
import musicEvent.Modus;
import musicEvent.MusicEvent;
import musicEvent.Note;
import musicEvent.NoteEvent;
import musicEvent.Rest;
import musicInterface.MusicObject;
import notation.Clef;
import notation.CurvedConnection;
import notation.KeySignature;
import notation.Score;
import notation.Slur;
import notation.Staff;
import notation.Tie;
import ui.GUI;
import ui.KeySignatureDialog;
import ui.KeySignatureResult;
import ui.Pointer;
import ui.TimeSignatureDialog;
import ui.TimeSignatureResult;

public class Controller implements StaffActionListener {

	public final static boolean TEST = true;

	public Score score = new Score();
	private GraphicalScore graphicalScore;
	private GUI gui;
	private Pointer pointer;
	private int currentVoice = 1;
	private SelectionManager selectionManager = new SelectionManager();
	private Point lastClick;
	private boolean dragging = false;
	private Point dragStart;
	private Map<GraphicalObject, Point> dragStartPositions;

	private static final int X_SCALE = 3;

	private void setGraphicalPosition(MusicObject obj) {
		GraphicalObject gObj = graphicalScore.getObject(obj);
		if (gObj == null)
			return;

		gObj.setX(obj.getTick() * X_SCALE);

		if (obj instanceof NoteEvent note) {
			int y = 90 - note.getStaffPosition() * 10;
			gObj.setY(y);
		} else {
			gObj.setY(90);
		}
	}

	private void test() {
		createChoirSATBOrganTemplate();
		/*
		 * score.addStaff();
		 * 
		 * // ===== STAFF OBJECTS (VOICE 0) ===== Clef clef = Clef.treble();
		 * clef.setTick(0); score.addObject(clef, 0, 0); setGraphicalPosition(clef);
		 * 
		 * TimeSignature ts = new TimeSignature(4, 4); ts.setTick(10);
		 * score.addObject(ts, 0, 0); setGraphicalPosition(ts);
		 * 
		 * KeySignature ks = new KeySignature(1, 1, Modus.MAJOR_SCALE); ks.setTick(20);
		 * score.addObject(ks, 0, 0); setGraphicalPosition(ks);
		 * 
		 * // ===== VOICE 1 ===== Note v1n1 = new Note(); v1n1.setDuration(4);
		 * v1n1.setStaffPosition(2); v1n1.setTick(40); score.addObject(v1n1, 0, 1);
		 * setGraphicalPosition(v1n1);
		 * 
		 * Note v1n2 = new Note(); v1n2.setDuration(4); v1n2.setStaffPosition(2);
		 * v1n2.setTick(60); score.addObject(v1n2, 0, 1); setGraphicalPosition(v1n2);
		 * 
		 * Note v1n3 = new Note(); v1n3.setDuration(4); v1n3.setStaffPosition(2);
		 * v1n3.setTick(80); score.addObject(v1n3, 0, 1); setGraphicalPosition(v1n3);
		 * 
		 * Note v1n4 = new Note(); v1n4.setDuration(4); v1n4.setStaffPosition(2);
		 * v1n4.setTick(100); score.addObject(v1n4, 0, 1); setGraphicalPosition(v1n4);
		 * 
		 * 
		 * // ===== VOICE 2 ===== Note v2n1 = new Note(); v2n1.setDuration(2);
		 * v2n1.setStaffPosition(-2); v2n1.setTick(40); score.addObject(v2n1, 0, 2);
		 * setGraphicalPosition(v2n1);
		 * 
		 * Note v2n2 = new Note(); v2n2.setDuration(2); v2n2.setStaffPosition(-1);
		 * v2n2.setTick(60); score.addObject(v2n2, 0, 2); setGraphicalPosition(v2n2);
		 * 
		 * // ===== BAR ===== Bar bar = new Bar(); bar.setEndBar(); bar.setTick(120);
		 * score.addObject(bar, 0, 0); setGraphicalPosition(bar);
		 * 
		 * // ===== LYRICS ===== score.addLyrics(List.of("la", "_", "__", "so"), 0, 1,
		 * 0); score.addLyrics(List.of("do", "_", "_", "re"), 0, 1, 1);
		 * 
		 * // ===== TIE CHAIN ===== Tie t1 = Tie.createIfValid(score, v1n1, v1n2); if
		 * (t1 != null) { t1.setStaff(0); score.addCurvedConnection(t1); }
		 * 
		 * Tie t2 = Tie.createIfValid(score, v1n2, v1n3); if (t2 != null) {
		 * t2.setStaff(0); score.addCurvedConnection(t2); }
		 * 
		 * Tie t3 = Tie.createIfValid(score, v1n3, v1n4); if (t3 != null) {
		 * t3.setStaff(0); score.addCurvedConnection(t3); }
		 * 
		 * // ===== SLUR ===== Slur slur = new Slur(v1n1, v1n4); slur.setStaff(0);
		 * score.addCurvedConnection(slur); export();
		 */
	}

	public static void main(String[] args) {
		new Controller().go();
	}

	private void registerListeners() {
		score.addListener(gui);
	}

	private void go() {
		graphicalScore = new GraphicalScore(score, this);
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
		Exporter x = new Exporter();
		x.export(score);
		x.printScore();
	}

	public void setCurrentVoice(int i) {
		currentVoice = i;

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

	private void deleteSelectedObject() {

		List<GraphicalObject> selectedObjects = selectionManager.getSelected();
		if (selectedObjects.isEmpty())
			return;

		// 1. Rimuovi dal MODELLO
		for (GraphicalObject o : selectedObjects) {

			MusicObject mo = o.getModelObject();
			if (mo == null)
				continue;

			// Delegazione totale allo Score
			score.removeObject(mo);
		}

		// 2. Pulisci la selezione (opzionale ma consigliato)
		selectionManager.clearSelection();
	}

	public Pointer getPointer() {
		return pointer;
	}

	public GraphicalObject getObjectAt(int x, int y) {
		return graphicalScore.hitTest(x, y);
	}

	public boolean pointerExists() {
		return pointer != null;
	}

	public void insertObject(MusicalSymbol objectToInsert, int x, int y) {
		GraphicalStaff s = gui.getPointedStaff(x, y);
		if (s == null)
			return;
		selectionManager.clearSelection();
		if (objectToInsert.getType() == Type.NOTE)
			insertNote(objectToInsert.getDuration(), s, x, y);
		else if (objectToInsert.getType() == Type.REST)
			insertRest(objectToInsert.getDuration(), s, x, y);
		else if (objectToInsert.getType() == Type.BARLINE)
			insertBar(objectToInsert, s, x);
		else if (objectToInsert.getType() == Type.CLEF)
			insertClef(objectToInsert, s, x);

		resizeStavesIfNeeded();

	}

	private void insertBar(MusicalSymbol objectToInsert, GraphicalStaff s, int x) {
		Bar bar = getBar(objectToInsert);
		bar.setTick(x);
		int staffIndex = graphicalScore.getStaffIndex(s);
		gui.prepareGraphicalInsertion(x, s.getYPosOfLine(0));
		score.addObject(bar, staffIndex, currentVoice);
	}

	private void insertNote(int duration, GraphicalStaff s, int x, int y) {
		Note n = createNote(duration);
		n.setTick(x);
		int staffPosition = s.getPosInStaff(y);
		System.out.println("Insert note at " + staffPosition + ". staff position");
		n.setStaffPosition(staffPosition);
		int staffIndex = graphicalScore.getStaffIndex(s);
		gui.prepareGraphicalInsertion(x, y);
		score.addObject(n, staffIndex, currentVoice);
	}

	private void insertRest(int duration, GraphicalStaff s, int x, int y) {
		Rest r = createRest(duration);
		r.setTick(x);
		int staffIndex = graphicalScore.getStaffIndex(s);
		gui.prepareGraphicalInsertion(x, y);
		score.addObject(r, staffIndex, currentVoice);
	}

	private void insertClef(MusicalSymbol clefSymbol, GraphicalStaff s, int x) {
		Clef c = createClef(clefSymbol);
		if (c == null)
			return;
		c.setTick(x);
		int staffIndex = graphicalScore.getStaffIndex(s);
		gui.prepareGraphicalInsertion(x, s.getYPosOfLine(1));
		score.addObject(c, staffIndex, 0);
	}

	private Note createNote(int duration) {
		Note n = new Note();
		n.setDuration(duration);
		return n;
	}

	private Rest createRest(int duration) {
		Rest r = new Rest(duration, 0);
		return r;
	}

	private Clef createClef(MusicalSymbol symbol) {
		if (symbol.equals(SymbolRegistry.CLEF_TREBLE)) {
			return Clef.treble();
		}
		if (symbol.equals(SymbolRegistry.CLEF_TREBLE_8))
			return Clef.treble8();

		if (symbol.equals(SymbolRegistry.CLEF_BASS))
			return Clef.bass();

		/*
		 * if (symbol.equals(SymbolRegistry.CLEF_ALTO)) return Clef.ALTO;
		 * 
		 * if (symbol.equals(SymbolRegistry.CLEF_TENOR)) return Clef.TENOR;
		 * 
		 * if (symbol.equals(SymbolRegistry.CLEF_SOPRANO)) return Clef.SOPRANO;
		 * 
		 * if (symbol.equals(SymbolRegistry.CLEF_MEZZO_SOPRANO)) return
		 * Clef.MEZZO_SOPRANO;
		 * 
		 * if (symbol.equals(SymbolRegistry.CLEF_BARITONE)) return Clef.BARITONE;
		 * 
		 * if (symbol.equals(SymbolRegistry.CLEF_PERCUSSION)) return Clef.PERCUSSION;
		 */

		return null;
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

	public void removeSelection() {
		selectionManager.clearSelection();
	}

	public void addClickedObjectToSelection(int x, int y) {
		GraphicalObject o = getObjectAt(x, y);
		addObjectToSelection(o);
	}

	public void addObjectToSelection(GraphicalObject o) {
		if (score.getStaffCount() == 0 || o == null)
			return;
		if (o instanceof GraphicalStaff)
			return;
		selectionManager.select(o, true);
		gui.repaintPanel();
	}

	public void selectObjectAtPos(int x, int y) {
		GraphicalObject o = getObjectAt(x, y);
		selectObject(o);
	}

	public void selectObject(GraphicalObject o) {
		if (score.getStaffCount() == 0 || o == null)
			return;
		if (o instanceof GraphicalStaff)
			return;
		selectionManager.select(o, false);
		gui.repaintPanel();
	}

	private MusicObject getLastElement() {
		List<MusicObject> list = score.getAllObjects();
		if (list.size() == 0)
			return null;
		return list.get(list.size() - 1);
	}

	private void resizeStavesIfNeeded() {
		MusicObject mo = getLastElement();
		if (mo == null)
			return;
		GraphicalObject go = graphicalScore.getObject(mo);
		int lastObjectX = go.getX();
		List<GraphicalStaff> staves = graphicalScore.getStaves();
		int w = staves.get(0).getWidth();
		for (GraphicalStaff ss : staves) {
			System.out.println("    " + ss.getWidth());
		}
		System.out.println(lastObjectX);
		if (lastObjectX < w - 100)
			return;
		System.out.println("Resize staves");

		for (GraphicalStaff s : staves) {
			s.setWidth(w + 200);
		}
		gui.resizePanel(w + 200, gui.getHeight()); // TODO l'altezza deve essere in base agli staves
		gui.repaintPanel();
	}

	public void mousePressed(MouseEvent e) {
		if (!SwingUtilities.isLeftMouseButton(e)) {
			return;
		}
		// se vi sono oggetti selezionati inizia il trascinamento
		// salva le poisizioni del mouse e di tutti gli oggetti.
		dragging = true;
		dragStart = e.getPoint();
		dragStartPositions = new HashMap<>();

		for (GraphicalObject obj : selectionManager.getSelected()) {
			dragStartPositions.put(obj, new Point(obj.getX(), obj.getY()));
		}
	}

	public void moveObjects(MouseEvent e) {
		if (!dragging)
			return;

		int dx = e.getX() - dragStart.x;
		int dy = e.getY() - dragStart.y;

		// Oggetto di riferimento per lo snap verticale

		if (dragStartPositions.isEmpty()) {
			return; // nessun oggetto da trascinare
		}

		GraphicalObject anchor = dragStartPositions.keySet().iterator().next();
		Point anchorStart = dragStartPositions.get(anchor);

		int targetY = anchorStart.y + dy;
		int snappedY = targetY;

		if (anchor instanceof GraphicalNote note) {
			GraphicalStaff staff = graphicalScore.getStaff(note.getNote().getStaffIndex());
			if (staff != null) {
				snappedY = gui.getSnapY(staff, targetY); // allineamento verticale
			}
		}

		int snapDy = snappedY - anchorStart.y;
		// Applica la stessa delta a tutti gli oggetti selezionati
		for (Map.Entry<GraphicalObject, Point> entry : dragStartPositions.entrySet()) {
			GraphicalObject o = entry.getKey();
			Point p = entry.getValue();

			// solo le note si possono muovere in tutte le direzioni.
			// Altri oggetti solo in orizzontales
			if (o instanceof GraphicalNote gNote) {

				NoteEvent modelNote = gNote.getModelObject();

				// 1. Recupera tutte le note legate
				List<NoteEvent> tiedGroup = score.getConnectionGroup(modelNote, Tie.class);

				// 2. Muovi la nota trascinata
				gNote.moveTo(p.x + dx, p.y + snapDy);

				int newY = gNote.getY();

				// 3. Allinea verticalmente tutte le altre note del gruppo
				for (NoteEvent note : tiedGroup) {
					if (note == modelNote)
						continue;

					GraphicalNote other = (GraphicalNote) graphicalScore.getObject(note);

					other.setY(newY);
					// TODO La tie deve essere aggiornata in tutte le note
				}
			} else {
				o.moveTo(p.x + dx, p.y);
			}
		}
		// problema con le voci e le legature
		// metto tie, la tolgo, la rimetto e viene slur
		// export totalmente a puttane
		gui.repaintPanel();
	}

	public void mouseReleased(MouseEvent e) {
		if (!dragging)
			return;

		dragging = false;

		for (GraphicalObject obj : selectionManager.getSelected()) {
			MusicObject mo = obj.getModelObject();
			if (mo == null)
				continue;
			score.changeTick(mo, obj.getX());

			if (mo instanceof Note note && obj instanceof GraphicalNote gNote) {
				GraphicalStaff s = graphicalScore.getStaffAtPos(e.getX(), e.getY());
				if (s == null)
					return;
				int snapY = gui.getSnapY(s, gNote.getY());
				int p = s.getPosInStaff(snapY);
				note.setStaffPosition(p);
			}
		}
		resizeStavesIfNeeded();
		dragStart = null;
		dragStartPositions = null;
	}

	public void movePointerTo(int mouseX, int snapY) {
		pointer.moveTo(mouseX, snapY);
	}

	public Point getLastClick() {
		return lastClick;
	}

	public void destroyPointer() {
		pointer = null;
	}

	public List<CurvedConnection> getCurveList() {
		return score.getCurveList();
	}

	/**
	 * Crea una Tie o una Slur a partire dalla selezione corrente.
	 *
	 * Regole: - Se è selezionata UNA sola nota, viene collegata alla nota
	 * successiva nello score - Se sono selezionate PIÙ note, ciascuna nota viene
	 * collegata alla successiva selezionata - L'ultima nota non viene mai collegata
	 * a note esterne alla selezione
	 *
	 * Per ogni coppia: - se la relazione è musicalmente valida → Tie - altrimenti →
	 * Slur
	 *
	 * La semantica musicale è demandata al modello (Tie.createIfValid), la GUI non
	 * prende decisioni musicali.
	 */
	private void slurOrTie() {

		// Recupera tutti gli oggetti grafici selezionati
		List<GraphicalObject> l = selectionManager.getSelected();

		// Estrae solo le note (oggetti grafici → modello)
		List<Note> selectedNotes = new ArrayList<>();
		for (GraphicalObject g : l) {
			if (g instanceof GraphicalNote) {
				selectedNotes.add(((GraphicalNote) g).getModelObject());
			}
		}

		// Nessuna nota selezionata → nessuna operazione
		if (selectedNotes.isEmpty())
			return;

		// Lista finale di note da collegare in sequenza
		List<Note> notesToProcess = new ArrayList<>();

		if (selectedNotes.size() == 1) {
			// Caso speciale: una sola nota selezionata
			// La si collega automaticamente alla nota successiva nello score
			Note n1 = selectedNotes.get(0);
			Note n2 = (Note) score.getNextNote(n1);

			if (n2 != null) {
				notesToProcess.add(n1);
				notesToProcess.add(n2);
			}
		} else {
			// Caso generale: più note selezionate
			// Si collegano solo tra loro, in ordine
			notesToProcess.addAll(selectedNotes);
		}

		// Crea una connessione per ogni coppia consecutiva
		// (l'ultima nota non guarda oltre)
		for (int i = 0; i < notesToProcess.size() - 1; i++) {

			Note n1 = notesToProcess.get(i);
			Note n2 = notesToProcess.get(i + 1);

			CurvedConnection curve;

			System.out.println("Slur or tie? Compare " + n1.getStaffPosition() + " with " + n2.getStaffPosition());
			// Il modello decide se la relazione è una Tie valida
			Tie tie = Tie.createIfValid(score, n1, n2);
			if (tie != null) {
				curve = tie;
			} else {
				// Se non è una Tie, la relazione è una Slur
				curve = new Slur(n1, n2);
			}

			int staffIndex = n1.getStaffIndex();
			GraphicalObject go = graphicalScore.getObject(n1);
			int x = go.getX();
			int y = go.getY();
			curve.setStaff(staffIndex);
			// Registrazione separata:
			// - GraphicalScore per la visualizzazione
			// - Score per il modello e gli eventi
//	        graphicalScore.addCurvedConnection(curve);
			gui.prepareGraphicalInsertion(x, y);
			score.addCurvedConnection(curve);
		}
	}

	@Override
	public void openKeySignatureDialog(int x, int y) {
		KeySignatureResult result = KeySignatureDialog.showDialog(gui);
		if (result == null)
			return;
		setKeySignature(result.getAlterations(), result.getMode(), result.isAllStaves(), x, y);
	}

	private void setKeySignature(int alterations, int mode, boolean allStaves, int x, int y) {
	    int type = 0;
	    if (alterations < 0)
	        type = -1;
	    else if (alterations > 0)
	        type = 1;

	    int alterationsNumber = Math.abs(alterations);
	    Modus modus = Modus.MAJOR_SCALE;
	    if (mode > 0) modus = Modus.MINOR_SCALE; // attenzione: qui controlla il tuo indice combo

	    if (allStaves) {
	    	for (Staff s : score.getAllStaves()) {
	    		KeySignature ks = new KeySignature(alterationsNumber, type, modus);
	    		ks.setTick(x);
	        	int staffIndex = score.getStaffIndex(s);
	        	GraphicalStaff gs = graphicalScore.getStaff(staffIndex);
	            gui.prepareGraphicalInsertion(x, gs, 0);
	            score.addObject(ks, staffIndex, 0); // clonare per avere oggetti separati
	        }
	    } else {
	        KeySignature ks = new KeySignature(alterationsNumber, type, modus);
	        ks.setTick(x);
	        int staffIndex = gui.getPointedStaffIndex(x, y);
	        gui.prepareGraphicalInsertion(x, y);
	        score.addObject(ks, staffIndex, 0);
	    }
	}


	@Override
	public void openTimeSignatureDialog(int x, int y) {
		TimeSignatureResult result = TimeSignatureDialog.showDialog(gui);
		if (result == null)
			return;
		setTimeSignature(result.getNumberator(), result.getDenumerator(), result.isAllStaves(), x, y);
	}

	private void setTimeSignature(int n, int d, boolean allStaves, int x, int y) {
	    if (allStaves) {
	        // Inserisce la time signature su tutti gli staff
	        for (Staff s : score.getAllStaves()) {
	        	TimeSignature ts = new TimeSignature(n, d);
	    	    ts.setTick(x);

	        	int staffIndex = score.getStaffIndex(s);
	        	GraphicalStaff gs = graphicalScore.getStaff(staffIndex);
	            gui.prepareGraphicalInsertion(x, gs, 0);
	            score.addObject(ts, staffIndex, 0); // clonare per avere oggetti separati
	        }
	    } else {
	        // Inserisce solo sullo staff puntato
	    	TimeSignature ts = new TimeSignature(n, d);
    	    ts.setTick(x);
	        int staffIndex = gui.getPointedStaffIndex(x, y);
	        gui.prepareGraphicalInsertion(x, y);
	        score.addObject(ts, staffIndex, 0);
	    }
	}


	@Override
	public void shitObjectsRight(int x, int y) {
		// cerca tutti gli oggetti a destra di mouseX
		GraphicalStaff s = graphicalScore.getStaffAtPos(x, y);
		int staffIndex = graphicalScore.getStaffIndex(s);
		List<MusicObject> objs = score.getObjects(staffIndex);
		for (MusicObject o : objs) {
			if (o.getTick() >= x) {
				o.setTick(o.getTick() + 10);
				GraphicalObject go = graphicalScore.getObject(o);
				go.moveTo(go.getX() + 10, go.getY());
			}
		}
		gui.repaintPanel();
	}

	public List<String> getLyricsFor(int staff, int voice, int stanza) {
		return score.getLyricsFor(staff, voice, stanza);
	}

	public int getStaffCount() {
		return graphicalScore.getStaffCount();
	}

	public Score getScore() {
		return score;
	}

	public void addLyrics(List<String> syllables, int staffIndex, int voiceNumber, int stanza) {
		// --- CONTROLLI ---
		if (score == null || score.getStaffCount() == 0) {
			JOptionPane.showMessageDialog(null, "Nessuno staff disponibile nella score.", "Errore Lyrics",
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

	public int getCurrentVoice() {
		return currentVoice;
	}

	public void createPianoTemplate() {
		Staff[] staffs = { score.addStaff(), score.addStaff() };
		Clef[] clefs = { Clef.treble(), Clef.bass() };

		for (int i = 0; i < 2; i++) {
			GraphicalStaff gs = graphicalScore.getStaff(score.getStaffIndex(staffs[i]));
			gui.prepareGraphicalInsertion(10, gs, clefs[i].getPosInStaff());
			score.addObject(clefs[i], score.getStaffIndex(staffs[i]), 0);
		}
	}

	public void createOrganTemplate() {
		Staff[] staffs = { score.addStaff(), score.addStaff(), score.addStaff() };
		Clef[] clefs = { Clef.treble(), Clef.bass(), Clef.bass() };

		for (int i = 0; i < 3; i++) {
			GraphicalStaff gs = graphicalScore.getStaff(score.getStaffIndex(staffs[i]));
			gui.prepareGraphicalInsertion(10, gs, clefs[i].getPosInStaff());
			clefs[i].setTick(10);
			score.addObject(clefs[i], score.getStaffIndex(staffs[i]), 0);
		}

	}

	public void createChoirSATBTemplate() {
		Staff[] staffs = { score.addStaff(), score.addStaff(), score.addStaff(), score.addStaff() };
		Clef[] clefs = { Clef.treble(), Clef.treble(), Clef.treble8(), Clef.bass() };

		for (int i = 0; i < 4; i++) {
			GraphicalStaff gs = graphicalScore.getStaff(score.getStaffIndex(staffs[i]));
			gui.prepareGraphicalInsertion(10, gs, clefs[i].getPosInStaff());
			clefs[i].setTick(10);
			score.addObject(clefs[i], score.getStaffIndex(staffs[i]), 0);
		}
	}

	public void createChoirSATBOrganTemplate() {
		createChoirSATBTemplate();
		createOrganTemplate();
	}
}
