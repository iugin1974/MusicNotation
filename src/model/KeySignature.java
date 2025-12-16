package model;

import musicEvent.Note;
import musicEvent.Modus;

public class KeySignature {

	private int numberOfAlterations;
	private int typeOfAlterations;
	private Modus modus = Modus.MAJOR_SCALE;
	// Indici delle posizioni verticali per le alterazioni della tonalità
	// ordinati secondo l’ordine delle alterazioni nella chiave musicale.
	// I valori corrispondono agli indici dell’array restituito da
	// staff.getYPosOfLinesAndSpacesExtended(extraLinesAbove, extraLinesBelow).

	// Diesis: F#, C#, G#, D#, A#, E#, B# (ordine standard)
	private int[] keySignatureSharpsIndex = { 8, 5, 9, 6, 3, 7, 4 };

	// Bemolli: Bb, Eb, Ab, Db, Gb, Cb, Fb (ordine standard)
	// L’ordine dei bemolli nella chiave parte da Si♭ e segue l’ordine delle
	// tonalità
	private int[] keySignatureFlatsIndex = { 4, 7, 3, 6, 2, 5, 1 };
//
	//
	// pitch class (0–11) delle toniche
	private static final int[] MAJOR_SHARP = { 0, 7, 2, 9, 4, 11, 6, 1 }; // C G D A E B F# C#
	private static final int[] MAJOR_FLAT = { 0, 5, 10, 3, 8, 1, 6, 11 }; // C F Bb Eb Ab Db Gb Cb

	private static final int[] MINOR_SHARP = { 9, 4, 11, 6, 1, 8, 3, 10 }; // A E B F# C# G# D# A#
	private static final int[] MINOR_FLAT = { 9, 2, 7, 0, 5, 10, 3, 8 }; // A D G C F Bb Eb Ab

	public KeySignature(int numberOfAlterations, int typeOfAlterations, Modus modus) {
		this.numberOfAlterations = numberOfAlterations;
		this.typeOfAlterations = typeOfAlterations;
		this.modus = modus;
	}

	/**
	 * Restituisce l'alterazione della tonalità per una data pitch class.
	 * -1 = bemolle, 0 = naturale, +1 = diesis
	 */
	public int getAlterationForPitchClass(int pitchClass) {
	    // Controllo per tonalità con diesis
	    if (typeOfAlterations == 1) { // diesis
	        int[] sharps = getSharpsIndex(); // ordini standard: F# C# G# D# A# E# B#
	        for (int i = 0; i < numberOfAlterations; i++) {
	            if (pitchClass == sharps[i]) return +1;
	        }
	    } else if (typeOfAlterations == -1) { // bemolle
	        int[] flats = getFlatsIndex(); // ordini standard: Bb Eb Ab Db Gb Cb Fb
	        for (int i = 0; i < numberOfAlterations; i++) {
	            if (pitchClass == flats[i]) return -1;
	        }
	    }
	    // Se non è alterata dalla tonalità
	    return 0;
	}
//	public Note getTonic() {
//
//		int pitchClass;
//
//		if (typeOfAlterations == 1) { // diesis
//			if (modus == Modus.MAJOR_SCALE) {
//				pitchClass = MAJOR_SHARP[numberOfAlterations];
//			} else { // MINOR o DORIAN
//				pitchClass = MINOR_SHARP[numberOfAlterations];
//			}
//		} else { // bemolli
//			if (modus == Modus.MAJOR_SCALE) {
//				pitchClass = MAJOR_FLAT[numberOfAlterations];
//			} else { // MINOR o DORIAN
//				pitchClass = MINOR_FLAT[numberOfAlterations];
//			}
//		}
//
//		int midi = 60 + pitchClass; // Do centrale come riferimento
//		int alteration = typeOfAlterations;
//
//		return new Note(midi, alteration);
//	}

	public int getNumberOfAlterations() {
		return numberOfAlterations;
	}

	public int getTypeOfAlterations() {
		return typeOfAlterations;
	}

	public Modus getModus() {
		return modus;
	}

	public int[] getSharpsIndex() {
		return keySignatureSharpsIndex;
	}

	public int[] getFlatsIndex() {
		return keySignatureFlatsIndex;
	}
}
