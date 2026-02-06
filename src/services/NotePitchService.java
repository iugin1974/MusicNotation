package services;

import graphical.GraphicalNote;
import graphical.GraphicalScore;
import graphical.GraphicalStaff;
import musicEvent.Note;
import notation.Clef;
import notation.KeySignature;
import notation.Score;
import notation.MidiPitch;
import notation.StaffMapper;

/**
 * Service applicativo responsabile della risoluzione del pitch di una nota
 * a partire dalla sua posizione grafica sul pentagramma.
 *
 * Questa classe funge da ponte tra:
 * <ul>
 *   <li>la rappresentazione grafica (GraphicalNote, GraphicalScore)</li>
 *   <li>il modello musicale (Note, Clef, KeySignature)</li>
 * </ul>
 *
 * Non contiene logica di rendering né regole musicali di basso livello:
 * coordina l'interazione tra view e model e aggiorna lo stato del dominio.
 */

public class NotePitchService {

	private final Score score;
	private final GraphicalScore graphicalScore;

	/**
	 * Crea un nuovo NotePitchService.
	 *
	 * @param score il modello musicale contenente chiavi, armature e note
	 * @param graphicalScore la rappresentazione grafica del pentagramma
	 */
	public NotePitchService(Score score, GraphicalScore graphicalScore) {
		this.score = score;
		this.graphicalScore = graphicalScore;
	}
	
	/**
	 * Aggiorna il pitch (MIDI) di una nota in base alla sua posizione grafica
	 * sul pentagramma.
	 *
	 * Il metodo:
	 * <ol>
	 *   <li>determina lo staff grafico su cui si trova la nota</li>
	 *   <li>calcola la posizione musicale (staffPosition)</li>
	 *   <li>recupera la chiave e la tonalità attive in quel punto dello spartito</li>
	 *   <li>risolve il pitch MIDI corrispondente</li>
	 *   <li>aggiorna il modello della nota</li>
	 * </ol>
	 *
	 * Se la nota non si trova su alcuno staff valido, il metodo non ha effetto.
	 *
	 * @param gNote la nota grafica la cui posizione determina il pitch musicale
	 */

	public void commitNotePitch(GraphicalNote gNote) {
		Note note = gNote.getModelObject();

		GraphicalStaff s = graphicalScore.getStaffAtPos(gNote.getX(), gNote.getY());
		if (s == null) {
			return;
		}

		int staffPosition = s.getPosInStaff(gNote);
		note.setStaffPosition(staffPosition);

		int staffIndex = graphicalScore.getStaffIndex(s);
		int tick = note.getTick();

		Clef clef = score.getPreviousObjectOfType(staffIndex, tick, Clef.class);
		KeySignature ks = score.getPreviousObjectOfType(staffIndex, tick, KeySignature.class);

		MidiPitch midi = StaffMapper.staffPositionToMidi(gNote.getStaffPosition(), clef, ks);

		note.setMidiNumber(midi.getMidiNumber());
	}
}
