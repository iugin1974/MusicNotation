package scoreWriter;

import musicEvent.Modus;
import notation.Clef;
import notation.KeySignature;

public class StaffMapper {

	private static final int[] SHARPS_ORDER = { 8, 5, 9, 6, 3, 7, 4 };
    private static final int[] FLATS_ORDER  = { 4, 7, 3, 6, 2, 5, 1 };
    
	public static int midiToStaffPosition(int pitch, Clef clef) {
		int delta = pitch - clef.getMidiOffset();
		int direction = Integer.signum(delta);

		int semitones = Math.abs(delta);
		int[] map = clef.getSemitoneMap();

		int octaveSize = 12;
		int diatonicStepsPerOctave = 7;

		int octaves = semitones / octaveSize;
		int remainder = semitones % octaveSize;

		int stepsInOctave = 0;
		while (stepsInOctave < map.length - 1 && map[stepsInOctave + 1] <= remainder) {
			stepsInOctave++;
		}

		int totalSteps = octaves * diatonicStepsPerOctave + stepsInOctave;

		return direction * totalSteps;
	}

	public static MidiPitch staffPositionToMidi(int staffPosition, Clef clef, KeySignature ks) {
		if (clef == null) {
			System.out.println("Export ist not possible. No clef");
			return null;
		}
		int[] scale = clef.getSemitoneMap();

		// posizione musicale della nota (indipendente dall’ottava)
		int notePosMod7 = Math.floorMod(staffPosition, 7); // 0..6
		int octaveShift = Math.floorDiv(staffPosition, 7); // può essere negativo
		int typeOfAlterations = ks.getTypeOfAlterations();
		int[] keySignatureIndex;
		if (typeOfAlterations == 1) {
			keySignatureIndex = SHARPS_ORDER;
		} else {
			keySignatureIndex = FLATS_ORDER;
		}
		int midiN = clef.getMidiOffset() + scale[notePosMod7] + (octaveShift * 12);

		for (int i = 0; i < ks.getNumberOfAlterations(); i++) {
			int keyPosMod7 = Math.floorMod(keySignatureIndex[i], 7);

			if (keyPosMod7 == notePosMod7) {
				midiN += typeOfAlterations;
				return new MidiPitch(midiN, typeOfAlterations);
			}
		}
		int alteration = ks.getAlteration(midiN);
		return new MidiPitch(midiN, alteration);
	}

	public static void main(String[] args) {
		for (int i = -2; i < 10; i++)
			System.out.println(staffPositionToMidi(i, Clef.treble(), new KeySignature(4, -1, Modus.MINOR_SCALE)));
	}

}
