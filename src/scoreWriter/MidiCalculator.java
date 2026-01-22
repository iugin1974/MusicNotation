package scoreWriter;

import musicEvent.Modus;
import musicEvent.Note;
import notation.Clef;
import notation.KeySignature;

public class MidiCalculator {

	public static boolean setMidiNumberAndAlteration(Note note, Clef clef, KeySignature ks) {
		if (clef == null) {
			System.out.println("Export ist not possible. No clef");
			return false;
		}
		int[] scale = clef.getSemitoneMap();
		int position = note.getStaffPosition();

		// posizione musicale della nota (indipendente dall’ottava)
		int notePosMod7 = Math.floorMod(position, 7); // 0..6
		int octaveShift = Math.floorDiv(position, 7); // può essere negativo

		if (ks == null)
			ks = new KeySignature(0, 0, Modus.MAJOR_SCALE);
		int typeOfAlterations = ks.getTypeOfAlterations();
		int[] keySignatureIndex;
		if (typeOfAlterations == 1)
			keySignatureIndex = ks.getSharpsIndex();
		else
			keySignatureIndex = ks.getFlatsIndex();
		int keyAlteration = 0;

		boolean found = false;
		int midiN = clef.getMidiOffset() + scale[notePosMod7] + (octaveShift * 12);

		for (int i = 0; i < ks.getNumberOfAlterations(); i++) {
		    int keyPosMod7 = Math.floorMod(keySignatureIndex[i], 7);

		    if (keyPosMod7 == notePosMod7) {
		        midiN += typeOfAlterations;
		        note.setAlteration(typeOfAlterations);
		        found = true;
		        break;
		    }
		}

		if (!found) {
		    note.setAlteration(0);
		}

		note.setMidiNumber(midiN);
		return true;

	}
}
