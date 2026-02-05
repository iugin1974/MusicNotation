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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import Measure.Bar;
import Measure.TimeSignature;
import graphical.GraphicalClef;
import graphical.GraphicalNote;
import graphical.GraphicalObject;
import graphical.GraphicalScore;
import graphical.GraphicalStaff;
import graphical.MusicalSymbol;
import graphical.MusicalSymbol.Type;
import graphical.StaffActionListener;
import midi.MidiInput;
import midi.MidiListener;
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
import services.ClefChangeService;
import services.InsertResult;
import services.InsertService;
import services.NotePitchService;
import services.ObjectMoveService;
import services.ScoreTemplateService;
import ui.GUI;
import ui.KeySignatureDialog;
import ui.KeySignatureResult;
import ui.MidiDeviceChooser;
import ui.MidiDeviceSelectionListener;
import ui.Pointer;
import ui.TimeSignatureDialog;
import ui.TimeSignatureResult;

public class Controller implements StaffActionListener, MidiListener, MidiDeviceSelectionListener {

	public final static boolean TEST = true;

	public Score score = new Score();
	private GraphicalScore graphicalScore;
	private GUI gui;
	private Pointer pointer;
	private int currentVoice = 1;
	private SelectionManager selectionManager = new SelectionManager();
	private ObjectMoveService objectMoveService;
	private KeyboardHandler keyboardHandler;
	private ClefChangeService clefChangeService;
	private NotePitchService notePitchService;
	private InsertResult insertResult;
	private Point lastClick;
	private MidiInput midiInput;
	protected boolean applyOnAllStaves = true;
	private DragService dragService;
	private InsertService insertService;
	private MusicalSymbol.Type insertType;

	private void test() {

		score.addStaff();

		// ===== STAFF OBJECTS (VOICE 0) =====
		Clef clef = Clef.treble();
		clef.setTick(0);
		score.addObject(clef, 0, 0);
//
//		TimeSignature ts = new TimeSignature(18, 7);
//		ts.setTick(50);
//		score.addObject(ts, 0, 0);
//
		KeySignature ks = new KeySignature(2, 1, Modus.MAJOR_SCALE);
		ks.setTick(100);
		score.addObject(ks, 0, 0);
		
//		int tick = 150;
//		for (int i = 62; i < 74; i++) {
//			Note n = new Note(i);
//			n.setTick(tick+=30);
//			score.addObject(n, 0, 1);
//		}
//
//		// ===== VOICE 1 =====
//		Note v1n1 = new Note(60, 0, 2, 0);
//		v1n1.setTick(150);
//		score.addObject(v1n1, 0, 1);

		/*
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
		 * score.addCurvedConnection(slur);
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
		dragService = new DragService(this, selectionManager, graphicalScore);
		notePitchService = new NotePitchService(score, graphicalScore);
		clefChangeService = new ClefChangeService(score);
		objectMoveService = new ObjectMoveService(score, notePitchService, clefChangeService);
		keyboardHandler = new KeyboardHandler(this);
		insertService = new InsertService(score, graphicalScore, clefChangeService);
		insertResult = new InsertResult(graphicalScore);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				gui.setVisible(true);
				// selectMidiDevice();
				// testMidi();
				 test();
				// load();
				// save();
				// System.exit(0);
			}
		});

	}

	private void testMidi() {
		addStaff();
		MusicalSymbol symbol = SymbolRegistry.QUARTER_NOTE;
		pointer = new Pointer(this, symbol);
		Clef clef = Clef.treble();
		score.addObject(clef, 0, 0);
		gui.selectSymbolToInsert(symbol);
		pointer.moveTo(700, 100);
		noteOn(60);
	}

	private void selectMidiDevice() {
		midiInput = new MidiInput(this);
		List<MidiDevice.Info> devices = midiInput.findInputDevices();

		MidiDeviceChooser chooser = new MidiDeviceChooser(devices, this);

		chooser.setVisible(true);
	}

	@Override
	public void deviceSelected(MidiDevice.Info info) {
		System.out.println("selected " + info.getDescription());
		try {
			MidiDevice device = MidiSystem.getMidiDevice(info);
			if (device != null) {
				device.open();
				midiInput.setTransmitter(device);
			}
		} catch (MidiUnavailableException e) {
			JOptionPane.showMessageDialog(null, "Impossibile aprire il device: " + info.getName(), "Errore MIDI",
					JOptionPane.ERROR_MESSAGE);
		}

	}

	@Override
	public void noteOn(int pitch) {
		if (pointer == null)  {
			System.out.println("Cannot write: Pointer inactive");
			return;
		}
		int x = 0;
		int y = 0;
		if (insertResult.isFirstMidiInsertion()) {
		x = pointer.getX();
		y = pointer.getY();
		insertResult.firstMidiInsertion(false);
		} else {
			x = insertResult.getNextTick();
			y = insertResult.getLastY();
		}
		MusicalSymbol symbol = gui.getObjectToInsert();
		if (symbol == null) {
			return;
		}
		int duration = symbol.getDuration();
		GraphicalStaff staff = graphicalScore.getStaffAtPos(x, y);
		if (staff == null) {
			return;
		}
		insertService.insertFromMidi(duration, pitch, x, currentVoice, staff);
		insertResult.update(x, y, duration);
		resizeStavesIfNeeded(insertResult.getNextTick());
	}

	public void resetMidiInsertion() {
		insertResult.firstMidiInsertion(true);
	}
	
	/**
	 * 
	 * Inserisce un oggetto nella score.
	 * @param symbol
	 * @param x
	 * @param y
	 */
	public void insertObject(MusicalSymbol symbol, int x, int y) {
		selectionManager.clearSelection();
		GraphicalStaff s = graphicalScore.getStaffAtPos(x, y);
		MusicObject mo = insertService.insertObject(symbol, s, x, y, currentVoice, applyOnAllStaves);
		selectionManager.select(graphicalScore.getGraphicalObject(mo), false);
		if (symbol.getType().equals(MusicalSymbol.Type.NOTE)
				|| symbol.getType().equals(MusicalSymbol.Type.REST)) {
		insertResult.update(x, y, symbol.getDuration());
		}
		resizeStavesIfNeeded();
		scrollLeftIfNeeded();
	}

	public void setPointer(MusicalSymbol symbol) {
		int x = 0;
		int y = 0;
		if (pointer != null) {
			x = pointer.getX();
			y = pointer.getY();
		}
		pointer = new Pointer(this, symbol);
		pointer.moveTo(x, y);
		gui.repaintPanel();
	}

	private void updatePointerAfterScroll(int scrollWidth) {
		System.out.println("scroll width " +scrollWidth);
		pointer.moveTo(pointer.getX() + scrollWidth/2, pointer.getY());
		System.out.println("pointer: " + pointer.getX() + " "+pointer.getY());
		System.out.println("Pointer updated");
	}
	
	public Staff addStaff() {
		return score.addStaff();
	}

	public void export() {
		Exporter x = new Exporter();
		x.export(score);
		x.printScore();

		save(); // TODO -> è qui solo per test
	}

	public void save() {
		score.save();
	}

	public void load() {
		score.load();
	}

	public void setCurrentVoice(int i) {
		currentVoice = i;

	}

	public void keyPressed(KeyEvent keyEvent) {
		keyboardHandler.keyPressed(keyEvent);
	}

	protected void deleteSelectedObject() {

		List<GraphicalObject> selectedObjects = selectionManager.getSelected();
		if (selectedObjects.isEmpty()) {
			return;
		}

		// 1. Rimuovi dal MODELLO
		for (GraphicalObject o : selectedObjects) {

			MusicObject mo = o.getModelObject();
			if (mo == null) {
				continue;
			}

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

	public void removeSelection() {
		selectionManager.clearSelection();
	}

	public void addClickedObjectToSelection(int x, int y) {
		GraphicalObject o = getObjectAt(x, y);
		addObjectToSelection(o);
	}

	public void addObjectToSelection(GraphicalObject o) {
		if (score.getStaffCount() == 0 || o == null || (o instanceof GraphicalStaff)) {
			return;
		}
		selectionManager.select(o, true);
		gui.repaintPanel();
	}

	public void selectObjectAtPos(int x, int y) {
		GraphicalObject o = getObjectAt(x, y);
		selectObject(o);
	}

	public void selectObject(GraphicalObject o) {
		if (score.getStaffCount() == 0 || o == null || (o instanceof GraphicalStaff)) {
			return;
		}
		selectionManager.select(o, false);
		gui.repaintPanel();
	}

	private MusicObject getLastElement() {
		List<MusicObject> list = score.getAllObjects();
		if (list.size() == 0) {
			return null;
		}
		return list.get(list.size() - 1);
	}

	private void resizePanelIfNeeded() {
		int h = score.getStaffCount() * graphicalScore.getStaff(0).getHeight();
		if (h > gui.getMainPanel().getHeight()) {
			gui.resizePanel(gui.getWidth(), h + 200);
			gui.repaintPanel();
		}
	}

	private void resizeStavesIfNeeded(int x) {
		int requiredWidth = x + gui.MARGIN;
		int currentWidth = gui.getScoreWidth();
		if (requiredWidth  < currentWidth) {
			return;
		}

		 gui.resizePanel(requiredWidth, gui.getHeight());
		gui.repaintPanel();
	}
	
	public void resizeStavesIfNeeded() {
		MusicObject mo = getLastElement();
		if (mo == null) {
			return;
		}
		GraphicalObject go = graphicalScore.getGraphicalObject(mo);
		int lastObjectX = go.getX();
		resizeStavesIfNeeded(lastObjectX);
	}

	private int scrollLeftIfNeeded() {
		int viewWidth = gui.getViewWidth(); // la parte visibile della score
		if (viewWidth > insertResult.getLastX() + gui.MARGIN) return 0;
		SwingUtilities.invokeLater(() -> {
			System.out.println("scrollLeftIfNeeded: x = " + insertResult.getLastX());
		gui.scrollLeft(insertResult.getLastX(), gui.MARGIN);
		updatePointerAfterScroll(gui.MARGIN);
		});
		return gui.MARGIN;
	}
	
	public void mouseDragged(MouseEvent e) {
		dragService.moveObjects(e);
	}

	public void mousePressed(MouseEvent e) {
		dragService.mousePressed(e);
	}

	public void mouseReleased(MouseEvent e) {
		List<GraphicalObject> moved = dragService.mouseReleased(e);

		if (moved == null || moved.isEmpty()) {
			return;
		}

		for (GraphicalObject obj : moved) {
			objectMoveService.commitMove(obj);
		}

		resizeStavesIfNeeded();
		gui.repaintPanel();
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
	protected void slurOrTie() {

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
		if (selectedNotes.isEmpty()) {
			return;
		}

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
			curve.setStaff(staffIndex);
			// Registrazione separata:
			// - GraphicalScore per la visualizzazione
			// - Score per il modello e gli eventi
			score.addCurvedConnection(curve);
		}
	}

	@Override
	public void openKeySignatureDialog(int x, int y) {
		KeySignatureResult result = KeySignatureDialog.showDialog(gui);
		if (result == null) {
			return;
		}
		setKeySignature(result.getAlterations(), result.getMode(), x, y);
	}

	private void setKeySignature(int alterations, int mode, int x, int y) {
		int type = 0;
		if (alterations < 0) {
			type = -1;
		} else if (alterations > 0) {
			type = 1;
		}

		int alterationsNumber = Math.abs(alterations);
		Modus modus = Modus.MAJOR_SCALE;
		if (mode > 0) {
			modus = Modus.MINOR_SCALE; // attenzione: qui controlla il tuo indice combo
		}

		if (applyOnAllStaves) {
			for (Staff s : score.getAllStaves()) {
				KeySignature ks = new KeySignature(alterationsNumber, type, modus);
				ks.setTick(x);
				int staffIndex = score.getStaffIndex(s);
				score.addObject(ks, staffIndex, 0); // clonare per avere oggetti separati
			}
		} else {
			KeySignature ks = new KeySignature(alterationsNumber, type, modus);
			ks.setTick(x);
			int staffIndex = gui.getPointedStaffIndex(x, y);
			score.addObject(ks, staffIndex, 0);
		}
	}

	@Override
	public void openTimeSignatureDialog(int x, int y) {
		TimeSignatureResult result = TimeSignatureDialog.showDialog(gui);
		if (result == null) {
			return;
		}
		setTimeSignature(result.getNumberator(), result.getDenumerator(), x, y);
	}

	private void setTimeSignature(int n, int d, int x, int y) {
		if (applyOnAllStaves) {
			// Inserisce la time signature su tutti gli staff
			for (Staff s : score.getAllStaves()) {
				TimeSignature ts = new TimeSignature(n, d);
				ts.setTick(x);

				int staffIndex = score.getStaffIndex(s);
				score.addObject(ts, staffIndex, 0); // clonare per avere oggetti separati
			}
		} else {
			// Inserisce solo sullo staff puntato
			TimeSignature ts = new TimeSignature(n, d);
			ts.setTick(x);
			int staffIndex = gui.getPointedStaffIndex(x, y);
			score.addObject(ts, staffIndex, 0);
		}
	}

	@Override
	public void shiftObjectsRight(int x, int y) {
		// cerca tutti gli oggetti a destra di mouseX
		GraphicalStaff s = graphicalScore.getStaffAtPos(x, y);
		int staffIndex = graphicalScore.getStaffIndex(s);
		List<MusicObject> objs = score.getObjects(staffIndex);
		for (MusicObject o : objs) {
			if (o.getTick() >= x) {
				o.setTick(o.getTick() + 100);
				GraphicalObject go = graphicalScore.getGraphicalObject(o);
				go.moveTo(go.getX() + 100, go.getY());
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
		ScoreTemplateService templateService = new ScoreTemplateService(score);
		templateService.createPianoTemplate();
		resizePanelIfNeeded();
	}

	public void createOrganTemplate() {
		ScoreTemplateService templateService = new ScoreTemplateService(score);
		templateService.createOrganTemplate();
		resizePanelIfNeeded();
	}

	public void createChoirSATBTemplate() {
		ScoreTemplateService templateService = new ScoreTemplateService(score);
		templateService.createChoirSATBTemplate();
		resizePanelIfNeeded();
	}

	public void createChoirSATBOrganTemplate() {
		createChoirSATBTemplate();
		createOrganTemplate();
	}

	public void applyOnAllStaves(boolean s) {
		applyOnAllStaves = s;
	}

	public void exitInsertMode() {
		gui.exitInsertMode();
	}

	public GUI getGUI() {
		return gui;
	}

	public void setInsertType(MusicalSymbol.Type insertType) {
		this.insertType = insertType;
	}

	private void selectSymbol(MusicalSymbol.Type type, int indexOrDuration) {
		MusicalSymbol symbol = null;

		if (type == MusicalSymbol.Type.NOTE || type == MusicalSymbol.Type.REST) {
			int duration = 7 - indexOrDuration;
			symbol = MusicalSymbol.getByDuration(type, duration);
		} else if (indexOrDuration > 0){ // TODO necessita un controllo?
			// scrivo -1 altrimenti l'utente dovrebbe premere 0
			symbol = MusicalSymbol.getByType(type).get(indexOrDuration - 1);
		}

		if (symbol == null)
			return;

		gui.clearSelection();
		gui.selectSymbolToInsert(symbol);
		gui.selectButtonForSymbol(symbol);
		gui.enterInsertMode();

		setPointer(symbol);
	}

	public void keyPressed(int i) {
		gui.clearSelection();
		selectSymbol(insertType, i);
	}

	public void addAccidental(int i) {
		List<GraphicalObject> selected = selectionManager.getSelected();
		if (selected == null || selected.isEmpty()) return;
		for (GraphicalObject go : selected) {
			MusicObject mo = go.getModelObject();
			if (mo instanceof NoteEvent && ((NoteEvent)mo).getAlteration() <=2) {
				((NoteEvent)mo).addSharp();
			}
		}
		
	}

}
