package scoreWriter;

import scoreWriter.MusicalSymbol.Type;

public class SymbolRegistry {

	// note e pause
	//	public MusicalSymbol(String name, String iconPath, String glyphUp, String glyphDown, Type type, int duration)
    public static final MusicalSymbol WHOLE_NOTE = new MusicalSymbol(
        "Whole Note",
        "/icons/nota_1.png",
        "\uE1D2", "\uE1D2",
        MusicalSymbol.Type.NOTE, 0
    );

    public static final MusicalSymbol HALF_NOTE = new MusicalSymbol(
        "Half Note",
        "/icons/nota_2.png",
        "\uE1D3", "\uE1D4",
        MusicalSymbol.Type.NOTE, 1
    );

    public static final MusicalSymbol QUARTER_NOTE = new MusicalSymbol(
        "Quarter Note",
        "/icons/nota_4.png",
        "\uE1D5", "\uE1D6",
        MusicalSymbol.Type.NOTE, 2
    );

    public static final MusicalSymbol EIGHTH_NOTE = new MusicalSymbol(
        "Eighth Note",
        "/icons/nota_8.png",
        "\uE1D7", "\uE1D8",
        MusicalSymbol.Type.NOTE, 3
    );

    public static final MusicalSymbol SIXTEENTH_NOTE = new MusicalSymbol(
        "Sixteenth Note",
        "/icons/nota_16.png",
        "\uE1D9", "\uE1DA",
        MusicalSymbol.Type.NOTE, 4
    );

    public static final MusicalSymbol THIRTY_SECOND_NOTE = new MusicalSymbol(
        "Thirty-Second Note",
        "/icons/nota_32.png",
        "\uE1DB", "\uE1DC",
        MusicalSymbol.Type.NOTE, 5
    );

    public static final MusicalSymbol SIXTY_FOURTH_NOTE = new MusicalSymbol(
        "Sixty-Fourth Note",
        "/icons/nota_64.png",
        "\uE1DD", "\uE1DE",
        MusicalSymbol.Type.NOTE, 6
    );


    // Rest symbols
    public static final MusicalSymbol WHOLE_REST = new MusicalSymbol(
        "Whole Rest",
        "/icons/whole_rest.png",
        "\uE4E3", null,
        MusicalSymbol.Type.REST, 0
    );

    public static final MusicalSymbol HALF_REST = new MusicalSymbol(
        "Half Rest",
        "/icons/half_rest.png",
        "\uE4E4", null,
        MusicalSymbol.Type.REST, 1
    );

    public static final MusicalSymbol QUARTER_REST = new MusicalSymbol(
        "Quarter Rest",
        "/icons/quarter_rest.png",
        "\uE4E5", null,
        MusicalSymbol.Type.REST, 2
    );

    public static final MusicalSymbol EIGHTH_REST = new MusicalSymbol(
        "Eighth Rest",
        "/icons/eighth_rest.png",
        "\uE4E6", null,
        MusicalSymbol.Type.REST, 3
    );

    public static final MusicalSymbol SIXTEENTH_REST = new MusicalSymbol(
        "Sixteenth Rest",
        "/icons/sixteenth_rest.png",
        "\uE4E7", null,
        MusicalSymbol.Type.REST, 4
    );

    public static final MusicalSymbol THIRTY_SECOND_REST = new MusicalSymbol(
        "Thirty-Second Rest",
        "/icons/thirtysecond_rest.png",
        "\uE4E8", null,
        MusicalSymbol.Type.REST, 5
    );

    public static final MusicalSymbol SIXTY_FOURTH_REST = new MusicalSymbol(
        "Sixty-Fourth Rest",
        "/icons/sixtyfourth_rest.png",
        "\uE4E9", null,
        MusicalSymbol.Type.REST, 6
    );


 // stanghette di battuta
 	// public MusicalSymbol(String name, String iconPath, String glyph, Type type)
    // Barline symbols
    public static final MusicalSymbol SINGLE_BARLINE = new MusicalSymbol(
        "Single Barline",
        "/icons/bar_single.png",
        "\uE030",
        MusicalSymbol.Type.BARLINE
    );

    public static final MusicalSymbol DOUBLE_BARLINE = new MusicalSymbol(
        "Double Barline",
        "/icons/bar_double.png",
        "\uE031",
        MusicalSymbol.Type.BARLINE
    );

    public static final MusicalSymbol FINAL_BARLINE = new MusicalSymbol(
        "Final Barline",
        "/icons/bar_end.png",
        "\uE032",
        MusicalSymbol.Type.BARLINE
    );

    public static final MusicalSymbol REPEAT_START_BARLINE = new MusicalSymbol(
        "Repeat Start",
        "/icons/bar_startRepeat.png",
        "\uE040",
        MusicalSymbol.Type.BARLINE
    );

    public static final MusicalSymbol REPEAT_END_BARLINE = new MusicalSymbol(
            "Repeat End",
            "/icons/bar_endRepeat.png",
            "\uE041",
            MusicalSymbol.Type.BARLINE
        );

 // chiavi
 //	public MusicalSymbol(String name, String iconPath, String glyph, Type type, int midiOffset)
public static final MusicalSymbol CLEF_TREBLE = new MusicalSymbol(
        "Treble Clef",
        "/icons/treble.png",
        "\uE050",
        MusicalSymbol.Type.CLEF, 60,
        SemitoneMap.SEMITONE_MAP_TREBLE
    );

public static final MusicalSymbol CLEF_BASS = new MusicalSymbol(
        "Bass Clef",
        "/icons/bass.png",
        "\uE062",
        MusicalSymbol.Type.CLEF, 40,
        SemitoneMap.SEMITONE_MAP_BASS
    );

public static final MusicalSymbol CLEF_TREBLE_8 = new MusicalSymbol(
        "Treble 8 Clef",
        "/icons/treble8.png",
        "\uE052",
        MusicalSymbol.Type.CLEF, 48,
        SemitoneMap.SEMITONE_MAP_TREBLE_8
    );
};
