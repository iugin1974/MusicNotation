package scoreWriter;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import Measure.Bar;
import Measure.TimeSignature;
import graphical.GraphicalBar;
import graphical.GraphicalKeySignature;
import graphical.GraphicalNote;
import graphical.GraphicalObject;
import graphical.GraphicalRest;
import graphical.GraphicalScore;
import graphical.GraphicalStaff;
import graphical.GraphicalTimeSignature;
import graphical.MusicalSymbol;
import graphical.MusicalSymbol.Type;
import graphical.StaffActionListener;
import musicEvent.Modus;
import musicEvent.MusicEvent;
import musicEvent.Note;
import musicEvent.Rest;
import musicInterface.MusicObject;
import notation.Clef;
import notation.CurvedConnection;
import notation.KeySignature;
import notation.Score;
import notation.SemitoneMap;
import notation.Staff;
import notation.Tie;
import ui.GUI;
import ui.KeySignatureDialog;
import ui.Pointer;
import ui.TimeSignatureDialog;

public class Controller implements StaffActionListener {

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

	private void test() {
		score.addStaff();
		/*
		 * Note n1 = new Note(); Note n2 = new Note(); n1.setDuration(2);
		 * n2.setDuration(3); score.addObject(n1, 0, 1); score.addObject(n2, 0, 1);
		 * TimeSignature ts = new TimeSignature(4, 4); score.addObject(ts, 0, 0);
		 * KeySignature ks = new KeySignature(1, 1, Modus.MAJOR_SCALE);
		 * score.addObject(ks, 0, 0); Note n3 = new Note(); Note n4 = new Note();
		 * n3.setDuration(4); n4.setDuration(4); score.addObject(n3, 0, 2);
		 * score.addObject(n4, 0, 2); Clef c = Clef.treble(); score.addObject(c, 0, 0);
		 * Bar bar = new Bar(); bar.setEndBar(); score.addObject(bar, 0, 0);
		 * c.setTick(10); ks.setTick(20); ts.setTick(30); n1.setTick(40);
		 * n2.setTick(50); n3.setTick(40); n4.setTick(50); bar.setTick(60);
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

		// TODO elimina solo la slur se selezionata
		// prima puliamo le note se l'oggetto è curva
		/*
		 * for (GraphicalObject obj : selectedObjects) { if (obj instanceof
		 * CurvedConnection) { ((CurvedConnection) obj).removeFromNotes(); } }
		 */

		for (GraphicalObject o : selectedObjects) {
			MusicObject mo = o.getModelObject();
			score.removeObject(mo);
		}
		// rimuovi dal grid solo le note
		for (GraphicalObject obj : selectedObjects) {
			if (obj instanceof GraphicalNote)
				;
			// grid.remove((GraphicalNote) obj);
		}

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
		selectionManager.deselectAll();
		if (objectToInsert.getType() == Type.NOTE)
			insertNote(objectToInsert.getDuration(), s, x, y);
		else if (objectToInsert.getType() == Type.REST)
			insertRest(objectToInsert.getDuration(), s, x, y);
		else if (objectToInsert.getType() == Type.BARLINE)
			insertBar(objectToInsert, s, x, y);
		else if (objectToInsert.getType() == Type.CLEF)
			insertClef(objectToInsert, s, x, y);

		resizeStavesIfNeeded(x);

	}

	private void insertBar(MusicalSymbol objectToInsert, GraphicalStaff s, int x, int y) {
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
	
	private void insertClef(MusicalSymbol clefSymbol, GraphicalStaff s, int x, int y) {
		Clef c = createClef(clefSymbol);
		if (c == null)
			return;
		c.setTick(x);
		int staffIndex = graphicalScore.getStaffIndex(s);
		gui.prepareGraphicalInsertion(x, y);
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

	public void selectObjectAtPos(int x, int y, boolean multipleSelection) {
		if (score.getStaffCount() == 0)
			return;
		if (!multipleSelection)
			selectionManager.deselectAll();
		GraphicalObject o = getObjectAt(x, y);
		System.out.println(o);
		if (o == null)
			return;
		selectionManager.select(o);
	}

	private void resizeStavesIfNeeded(int x) {
		if (x < gui.getWidth() - 100)
			return;
		List<GraphicalStaff> staves = graphicalScore.getStaves();
		int w = staves.get(0).getWidth();
		// int w = gui.getStaffList().get(0).getWidth();
		for (GraphicalStaff s : staves) {
			s.setWidth(w + 200);
		}
		gui.resizePanel(w + 200, gui.getHeight()); // TODO l'altezza deve essere in base agli staves
		// anche nella guissa
		gui.repaintPanel();
	}

	public void mousePressed(MouseEvent e) {
		if (!SwingUtilities.isLeftMouseButton(e))
			return;

		// controlla se si è cliccato su un oggetto
		GraphicalObject o = getObjectAt(e.getX(), e.getY());
		if (o == null || !selectionManager.hasSelectedObjects())
			return;

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
			// Altri oggetti solo in orizzontale
			if (o instanceof GraphicalNote) {
				o.moveTo(p.x + dx, p.y + snapDy); // X libera, Y snap
			} else {
				o.moveTo(p.x + dx, p.y);
			}
		}

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
				note.setStaffPosition(gNote.getStaffPosition());

				// aggiorna tie/slur se presente
				// CurvedConnection curve = note.getCurvedConnection();
				// if (curve != null) {
				// curve.updateFromGraphical(); // metodo che aggiorna curve dal grafico
				// }
			}
		}
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

	private void slurOrTie() {
		List<GraphicalObject> l = selectionManager.getSelected();
		List<Note> selectedNotes = new ArrayList<>();
		for (GraphicalObject g : l) {
			if (g instanceof GraphicalNote) {
				selectedNotes.add(((GraphicalNote) g).getModelObject());
			}
		}
		if (selectedNotes.isEmpty())
			return;
		if (selectedNotes.size() == 1) {
			Note n1 = selectedNotes.get(0);
			Note n2 = (Note) score.getNextNote(n1);
			
			if (n2 != null) {
				if (n1.getStaffPosition() == n2.getStaffPosition()) {
					n1.isTiedStart();
					n2.isTiedEnd();
				} else {
					n1.isSlurStart();
					n2.isSlurEnd();
				}
			}
		}

		/*
		 * for (int i = 0; i < selectionManager.getSelected().size() - 1; i++) { if
		 * (selectedNotes.size() == 1) { Note n1 = selectedNotes.get(0); Note n2;
		 * MusicEvent e = score.getNextNote(n1); if (e instanceof Note) { n2 = (Note)e;
		 * } else continue; if (n1.getStaffPosition() == n2.getStaffPosition()) tie(n1,
		 * n2, i); else slur(n1, n2, i);
		 * 
		 * continue; // passa al prossimo staff }
		 * 
		 * for (int j = 0; j < selectedNotes.size() - 1; j++) { GraphicalNote n1 =
		 * selectedNotes.get(j); GraphicalNote n2 = selectedNotes.get(j + 1);
		 * 
		 * if (hasSameHeight(n1, n2) && score.areNotesConsecutive(n1, n2)) tie(n1, n2,
		 * i); // usa i invece di j else slur(n1, n2, i); } } gui.repaintPanel();
		 */
	}

	@Override
	public void openKeySignatureDialog(int x, int y) {
		IntPair pair = KeySignatureDialog.showDialog(gui);
		if (pair == null)
			return;
		setKeySignature(pair.first, x, y);
	}

	private void setKeySignature(int chosenValue, int x, int y) {
		int type = 0;
		if (chosenValue < 0)
			type = -1;
		else if (chosenValue > 0)
			type = 1;
		int alterationsNumber = Math.abs(chosenValue);
		KeySignature ks = new KeySignature(alterationsNumber, type, Modus.MAJOR_SCALE); // TODO
		ks.setTick(x);
		int staffIndex = gui.getPointedStaffIndex(x, y);
		gui.prepareGraphicalInsertion(x, y);
		score.addObject(ks, staffIndex, 0);
	}

	@Override
	public void openTimeSignatureDialog(int x, int y) {
		IntPair pair = TimeSignatureDialog.showDialog(gui);
		if (pair == null)
			return;
		setTimeSignature(pair.first, pair.second, x, y);
	}

	private void setTimeSignature(int n, int d, int x, int y) {
		TimeSignature ts = new TimeSignature(n, d);
		ts.setTick(x);
		int staffIndex = gui.getPointedStaffIndex(x, y);
		gui.prepareGraphicalInsertion(x, y);
		score.addObject(ts, staffIndex, 0);
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
}
