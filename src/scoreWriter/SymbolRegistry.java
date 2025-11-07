package scoreWriter;

public class SymbolRegistry {

	/*
	 * public MusicalSymbol(String name, String iconPath,
                         String glyphUp, String glyphDown,
                         boolean hasStem, boolean isRest, boolean isBarline,
                         int duration)
	 */
	
    // Note symbols
    public static final MusicalSymbol WHOLE_NOTE = new MusicalSymbol(
        "Whole Note",
        "/icons/nota_1.png",
        "\uE1D2", "\uE1D2",
        false, false, false, 0
    );

    public static final MusicalSymbol HALF_NOTE = new MusicalSymbol(
        "Half Note",
        "/icons/nota_2.png",
        "\uE1D3", "\uE1D4",
        true, false, false, 1
    );

    public static final MusicalSymbol QUARTER_NOTE = new MusicalSymbol(
        "Quarter Note",
        "/icons/nota_4.png",
        "\uE1D5", "\uE1D6",
        true, false, false, 2
    );

    public static final MusicalSymbol EIGHTH_NOTE = new MusicalSymbol(
        "Eighth Note",
        "/icons/nota_8.png",
        "\uE1D7", "\uE1D8",
        true, false, false, 3
    );

    public static final MusicalSymbol SIXTEENTH_NOTE = new MusicalSymbol(
        "Sixteenth Note",
        "/icons/nota_16.png",
        "\uE1D9", "\uE1DA",
        true, false, false, 4
    );

    public static final MusicalSymbol THIRTY_SECOND_NOTE = new MusicalSymbol(
        "Thirty-Second Note",
        "/icons/nota_32.png",
        "\uE1DB", "\uE1DC",
        true, false, false, 5
    );

    public static final MusicalSymbol SIXTY_FOURTH_NOTE = new MusicalSymbol(
        "Sixty-Fourth Note",
        "/icons/nota_64.png",
        "\uE1DD", "\uE1DE",
        true, false, false, 6
    );


    // Rest symbols
    public static final MusicalSymbol WHOLE_REST = new MusicalSymbol(
        "Whole Rest",
        "/icons/whole_rest.png",
        "\uE4E3", null,
        false, true, false, 0
    );

    public static final MusicalSymbol HALF_REST = new MusicalSymbol(
        "Half Rest",
        "/icons/half_rest.png",
        "\uE4E4", null,
        false, true, false, 1
    );

    public static final MusicalSymbol QUARTER_REST = new MusicalSymbol(
        "Quarter Rest",
        "/icons/quarter_rest.png",
        "\uE4E5", null,
        false, true, false, 2
    );

    public static final MusicalSymbol EIGHTH_REST = new MusicalSymbol(
        "Eighth Rest",
        "/icons/eighth_rest.png",
        "\uE4E6", null,
        false, true, false, 3
    );

    public static final MusicalSymbol SIXTEENTH_REST = new MusicalSymbol(
        "Sixteenth Rest",
        "/icons/sixteenth_rest.png",
        "\uE4E7", null,
        false, true, false, 4
    );

    public static final MusicalSymbol THIRTY_SECOND_REST = new MusicalSymbol(
        "Thirty-Second Rest",
        "/icons/thirtysecond_rest.png",
        "\uE4E8", null,
        false, true, false, 5
    );

    public static final MusicalSymbol SIXTY_FOURTH_REST = new MusicalSymbol(
        "Sixty-Fourth Rest",
        "/icons/sixtyfourth_rest.png",
        "\uE4E9", null,
        false, true, false, 6
    );


    // Barline symbols
    public static final MusicalSymbol SINGLE_BARLINE = new MusicalSymbol(
        "Single Barline",
        "/icons/bar_single.png",
        "\uE030", null,
        false, false, true, -1
    );

    public static final MusicalSymbol DOUBLE_BARLINE = new MusicalSymbol(
        "Double Barline",
        "/icons/bar_double.png",
        "\uE031", null,
        false, false, true, -1
    );

    public static final MusicalSymbol FINAL_BARLINE = new MusicalSymbol(
        "Final Barline",
        "/icons/bar_end.png",
        "\uE032", null,
        false, false, true, -1
    );

    public static final MusicalSymbol REPEAT_START_BARLINE = new MusicalSymbol(
        "Repeat Start",
        "/icons/bar_startRepeat.png",
        "\uE040", null,
        false, false, true, -1
    );

    public static final MusicalSymbol REPEAT_END_BARLINE = new MusicalSymbol(
        "Repeat End",
        "/icons/bar_endRepeat.png",
        "\uE041", null,
        false, false, true, -1
    );
}

