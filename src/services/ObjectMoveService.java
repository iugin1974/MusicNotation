package services;

import graphical.GraphicalClef;
import graphical.GraphicalNote;
import graphical.GraphicalObject;
import musicEvent.Note;
import musicEvent.NoteEvent;
import musicInterface.MusicObject;
import notation.Clef;
import notation.Score;
import notation.Tie;

/**
 * Service applicativo responsabile della gestione degli spostamenti
 * di oggetti grafici e della conseguente sincronizzazione con il modello musicale.
 *
 * Questa classe coordina l'aggiornamento dello stato del dominio quando
 * un oggetto grafico (nota, chiave, ecc.) viene spostato dall'utente.
 *
 * Non contiene logica musicale di basso livello né codice di rendering:
 * delega le operazioni specifiche ad altri service specializzati.
 */

public class ObjectMoveService {

	private final Score score;
	private final NotePitchService notePitchService;

	/**
	 * Crea un nuovo ObjectMoveService.
	 *
	 * @param score il modello dello spartito da aggiornare
	 * @param notePitchService service per la risoluzione del pitch delle note
	 * @param clefChangeService service per la gestione dei cambi di chiave
	 */

	public ObjectMoveService(Score score, NotePitchService notePitchService) {
		this.score = score;
		this.notePitchService = notePitchService;
	}

	/**
	 * Applica al modello musicale lo spostamento di un oggetto grafico.
	 *
	 * Il metodo:
	 * <ul>
	 *   <li>aggiorna la posizione temporale (tick) dell'oggetto nel modello</li>
	 *   <li>se l'oggetto è una nota, ricalcola il pitch e aggiorna eventuali legature</li>
	 *   <li>se l'oggetto è una chiave, applica il cambio di chiave</li>
	 * </ul>
	 *
	 * Se l'oggetto grafico non è associato ad alcun oggetto di modello,
	 * il metodo non ha effetto.
	 *
	 * @param obj l'oggetto grafico spostato dall'utente
	 */

	public void commitMove(GraphicalObject obj) {
		MusicObject mo = obj.getModelObject();
		if (mo == null)
			return;

		score.changeTick(mo, obj.getX());

		if (mo instanceof Note note && obj instanceof GraphicalNote gNote) {
			notePitchService.commitNotePitch(gNote);
			for (NoteEvent n : score.getConnectionGroup(note, Tie.class)) {
				n.setMidiNumber(note.getMidiNumber());
			}
		}
	}
}
