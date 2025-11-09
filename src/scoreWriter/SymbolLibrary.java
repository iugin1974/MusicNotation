package scoreWriter;

public class SymbolLibrary {

	public static final int STEM_DOWN = -1, STEM_UP = 1;

	public enum BarlineType {
		SINGLE, // singola stanghetta
		DOUBLE, // doppia stanghetta
		FINAL, // stanghetta finale (spessa)
		REPEAT_START, // inizio ripetizione
		REPEAT_END // fine ripetizione
	}

	public enum ClefType {
		TREBLE, BASS, TREBLE_8

	}

	public static String getNoteGlyph(int stem, int duration) {
		switch (stem) {
		case STEM_UP:
			switch (duration) {
			case 0:
				return "\uE1D2";
			case 1:
				return "\uE1D3";
			case 2:
				return "\uE1D5";
			case 3:
				return "\uE1D7";
			case 4:
				return "\uE1D9";
			case 5:
				return "\uE1DB";
			case 6:
				return "\uE1DD";
			default:
				return "";
			}

		case STEM_DOWN:
			switch (duration) {
			case 0:
				return "\uE1D2";
			case 1:
				return "\uE1D4";
			case 2:
				return "\uE1D6";
			case 3:
				return "\uE1D8";
			case 4:
				return "\uE1DA";
			case 5:
				return "\uE1DC";
			case 6:
				return "\uE1DE";
			default:
				return "";
			}

		default:
			return "";
		}
	}

	public static String getRestGlyph(int duration) {
		switch (duration) {
		case 0:
			return "\uE4E3";
		case 1:
			return "\uE4E4";
		case 2:
			return "\uE4E5";
		case 3:
			return "\uE4E6";
		case 4:
			return "\uE4E7";
		case 5:
			return "\uE4E8";
		case 6:
			return "\uE4E9";
		}
		return "";
	}

	public static String getBarlineGlyph(BarlineType type) {
		return switch (type) {
		case SINGLE -> "\uE030";
		case DOUBLE -> "\uE031";
		case FINAL -> "\uE032";
		case REPEAT_START -> "\uE040";
		case REPEAT_END -> "\uE041";
		};
	}

	public static String getClefGlyph(ClefType type) {
		return switch (type) {
		case TREBLE -> "\uE050";
		case BASS -> "\uE062";
		case TREBLE_8 -> "\uE052";
		};
	}
}