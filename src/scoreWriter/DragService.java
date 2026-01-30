package scoreWriter;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import graphical.GraphicalClef;
import graphical.GraphicalNote;
import graphical.GraphicalObject;
import graphical.GraphicalScore;
import graphical.GraphicalStaff;
import musicEvent.NoteEvent;
import notation.Score;
import notation.Tie;

public class DragService {

	private boolean dragging = false;
	private Point dragStart;
	private Map<GraphicalObject, Point> dragStartPositions;
	private final SelectionManager selectionManager;
	private final GraphicalScore graphicalScore;
	private Controller controller;
	private Score score;

	// TODO Elimina dipendenza dal controller
	public DragService(Controller controller, SelectionManager selectionManager, GraphicalScore graphicalScore) {
		this.controller = controller;
		this.selectionManager = selectionManager;
		this.graphicalScore = graphicalScore;
		score = controller.getScore();
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

	public List<GraphicalObject> mouseReleased(MouseEvent e) {
	    if (!dragging) {
	        return List.of();
	    }

	    dragging = false;

	    List<GraphicalObject> moved =
	        new ArrayList<>(selectionManager.getSelected());

	    dragStart = null;
	    dragStartPositions = null;

	    return moved;
	}

	/**
	 * Gestisce lo spostamento degli oggetti selezionati durante un'operazione di
	 * drag.
	 * <p>
	 * Se il drag non è attivo o non ci sono oggetti da spostare, il metodo non fa
	 * nulla. Lo spostamento orizzontale segue direttamente il mouse, mentre quello
	 * verticale può essere soggetto a snap in base allo staff dell'oggetto di
	 * riferimento.
	 * </p>
	 *
	 * @param e evento mouse che contiene la posizione corrente del cursore
	 */
	public void moveObjects(MouseEvent e) {
		if (!dragging) {
			return;
		}

		int dx = computeDx(e);
		int dy = computeDy(e);

		if (dragStartPositions.isEmpty()) {
			return;
		}

		SnapContext snap = computeSnapContext(dy);

		for (Map.Entry<GraphicalObject, Point> entry : dragStartPositions.entrySet()) {
			moveEntry(entry, dx, snap.snapDy);
		}

		controller.getGUI().repaintPanel();
	}

	/**
	 * Calcola lo spostamento orizzontale rispetto alla posizione iniziale del drag.
	 *
	 * @param e evento mouse corrente
	 * @return delta X del mouse rispetto all'inizio del drag
	 */
	private int computeDx(MouseEvent e) {
		return e.getX() - dragStart.x;
	}

	/**
	 * Calcola lo spostamento orizzontale rispetto alla posizione iniziale del drag.
	 *
	 * @param e evento mouse corrente
	 * @return delta X del mouse rispetto all'inizio del drag
	 */
	private int computeDy(MouseEvent e) {
		return e.getY() - dragStart.y;
	}

	/**
	 * Calcola il contesto di snap verticale a partire dallo spostamento Y del
	 * mouse.
	 * <p>
	 * Usa il primo oggetto selezionato come riferimento (anchor). Se l'anchor è una
	 * {@link GraphicalNote}, applica lo snap verticale allo staff corrispondente;
	 * altrimenti lo spostamento verticale rimane lineare.
	 * </p>
	 *
	 * @param dy spostamento verticale del mouse
	 * @return contesto contenente la delta Y finale da applicare agli oggetti
	 */
	private SnapContext computeSnapContext(int dy) {
		GraphicalObject anchor = dragStartPositions.keySet().iterator().next();
		Point anchorStart = dragStartPositions.get(anchor);

		int targetY = anchorStart.y + dy;
		int snappedY = targetY;

		if (anchor instanceof GraphicalNote note) {
			GraphicalStaff staff = graphicalScore.getStaff(note.getNote().getStaffIndex());
			if (staff != null) {
				snappedY = controller.getGUI().getSnapY(staff, targetY);
			}
		}

		int snapDy = snappedY - anchorStart.y;
		return new SnapContext(snapDy);
	}

	/**
	 * Contiene le informazioni necessarie per applicare lo snap verticale durante
	 * un'operazione di drag.
	 */
	private static class SnapContext {
		final int snapDy;

		SnapContext(int snapDy) {
			this.snapDy = snapDy;
		}
	}

	/**
	 * Sposta un singolo oggetto selezionato applicando le delta di movimento.
	 * <p>
	 * Le {@link GraphicalNote} vengono gestite con una logica specifica che
	 * aggiorna anche il modello musicale; tutti gli altri oggetti vengono spostati
	 * solo orizzontalmente.
	 * </p>
	 *
	 * @param entry  coppia oggetto/posizione iniziale
	 * @param dx     spostamento orizzontale
	 * @param snapDy spostamento verticale (eventualmente snappato)
	 */
	private void moveEntry(Map.Entry<GraphicalObject, Point> entry, int dx, int snapDy) {
		GraphicalObject o = entry.getKey();
		Point p = entry.getValue();

		if (o instanceof GraphicalNote gNote) {
			moveGraphicalNote(gNote, p, dx, snapDy);
		} else if (o instanceof GraphicalClef clef) {
			moveGraphicalClef(clef, p, dx);
		}

		else {
			o.moveTo(p.x + dx, p.y);
		}
	}

	private void moveGraphicalClef(GraphicalClef gClef, Point p, int dx) {
		gClef.moveTo(p.x + dx, p.y);
	}

	/**
	 * Sposta una {@link GraphicalNote} aggiornando sia la posizione grafica sia le
	 * informazioni musicali associate nel modello.
	 * <p>
	 * Dopo lo spostamento, vengono ricalcolati staff, posizione sul pentagramma e
	 * pitch MIDI. Tutte le altre note legate vengono poi allineate verticalmente.
	 * </p>
	 *
	 * @param gNote  nota grafica da spostare
	 * @param p      posizione iniziale della nota
	 * @param dx     spostamento orizzontale
	 * @param snapDy spostamento verticale (eventualmente snappato)
	 */
	private void moveGraphicalNote(GraphicalNote gNote, Point p, int dx, int snapDy) {
		NoteEvent modelNote = gNote.getModelObject();

		List<NoteEvent> tiedGroup = score.getConnectionGroup(modelNote, Tie.class);

		gNote.moveTo(p.x + dx, p.y + snapDy);

		int newY = gNote.getY();
		alignTiedNotes(modelNote, tiedGroup, newY);
	}

	/**
	 * Allinea verticalmente tutte le note legate a una nota di riferimento.
	 * <p>
	 * La nota di riferimento non viene modificata. L'allineamento è puramente
	 * grafico e non aggiorna il modello musicale.
	 * </p>
	 *
	 * @param modelNote nota di riferimento
	 * @param tiedGroup gruppo di note legate
	 * @param newY      coordinata Y da applicare alle note legate
	 */
	private void alignTiedNotes(NoteEvent modelNote, List<NoteEvent> tiedGroup, int newY) {
		for (NoteEvent note : tiedGroup) {
			if (note == modelNote) {
				continue;
			}

			GraphicalNote other = (GraphicalNote) graphicalScore.getGraphicalObject(note);

			other.setY(newY);
		}
	}
}
