package scoreWriter;

import java.util.ArrayList;
import java.util.List;

import graphical.MusicalSymbol;
import notation.SemitoneMap;

public class SymbolRegistry {


    private static final List<MusicalSymbol> ALL_SYMBOLS = new ArrayList<>();

    /**
     * Registra un {@link MusicalSymbol} nel registro globale dei simboli.
     * <p>
     * Questo metodo viene usato durante l'inizializzazione statica della classe
     * per raccogliere automaticamente tutti i simboli definiti come costanti.
     * In questo modo è possibile interrogare il registro (ad esempio per tipo)
     * senza dover ricorrere a reflection o mantenere manualmente una collezione separata.
     * </p>
     *
     * @param s il simbolo da registrare; non deve essere {@code null}
     * @return lo stesso simbolo passato come parametro, per consentire
     *         l'inizializzazione inline dei campi {@code static final}
     */
    private static MusicalSymbol register(MusicalSymbol s) {
        ALL_SYMBOLS.add(s);
        return s;
    }
    
    public static List<MusicalSymbol> getAllSymbols() {
    	return ALL_SYMBOLS;
    }
    
	// note e pause
	//	public MusicalSymbol(String name, String iconPath, String glyphUp, String glyphDown, Type type, int duration)
    public static final MusicalSymbol WHOLE_NOTE = register(new MusicalSymbol(
        "Whole Note",
        "/icons/nota_1.png",
        "\uE1D2", "\uE1D2",
        MusicalSymbol.Type.NOTE, 0
    ));

    public static final MusicalSymbol HALF_NOTE = register(new MusicalSymbol(
        "Half Note",
        "/icons/nota_2.png",
        "\uE1D3", "\uE1D4",
        MusicalSymbol.Type.NOTE, 1
    ));

    public static final MusicalSymbol QUARTER_NOTE = register(new MusicalSymbol(
        "Quarter Note",
        "/icons/nota_4.png",
        "\uE1D5", "\uE1D6",
        MusicalSymbol.Type.NOTE, 2
    ));

    public static final MusicalSymbol EIGHTH_NOTE = register(new MusicalSymbol(
        "Eighth Note",
        "/icons/nota_8.png",
        "\uE1D7", "\uE1D8",
        MusicalSymbol.Type.NOTE, 3
    ));

    public static final MusicalSymbol SIXTEENTH_NOTE = register(new MusicalSymbol(
        "Sixteenth Note",
        "/icons/nota_16.png",
        "\uE1D9", "\uE1DA",
        MusicalSymbol.Type.NOTE, 4
    ));

    public static final MusicalSymbol THIRTY_SECOND_NOTE = register(new MusicalSymbol(
        "Thirty-Second Note",
        "/icons/nota_32.png",
        "\uE1DB", "\uE1DC",
        MusicalSymbol.Type.NOTE, 5
    ));

    public static final MusicalSymbol SIXTY_FOURTH_NOTE = register(new MusicalSymbol(
        "Sixty-Fourth Note",
        "/icons/nota_64.png",
        "\uE1DD", "\uE1DE",
        MusicalSymbol.Type.NOTE, 6
    ));


    // Rest symbols
    public static final MusicalSymbol WHOLE_REST = register(new MusicalSymbol(
        "Whole Rest",
        "/icons/rest_1.png",
        "\uE4E3",
        MusicalSymbol.Type.REST, 0
    ));

    public static final MusicalSymbol HALF_REST = register(new MusicalSymbol(
        "Half Rest",
        "/icons/rest_2.png",
        "\uE4E4",
        MusicalSymbol.Type.REST, 1
    ));

    public static final MusicalSymbol QUARTER_REST = register(new MusicalSymbol(
        "Quarter Rest",
        "/icons/rest_4.png",
        "\uE4E5",
        MusicalSymbol.Type.REST, 2
    ));

    public static final MusicalSymbol EIGHTH_REST = register(new MusicalSymbol(
        "Eighth Rest",
        "/icons/rest_8.png",
        "\uE4E6",
        MusicalSymbol.Type.REST, 3
    ));

    public static final MusicalSymbol SIXTEENTH_REST = register(new MusicalSymbol(
        "Sixteenth Rest",
        "/icons/rest_16.png",
        "\uE4E7",
        MusicalSymbol.Type.REST, 4
    ));

    public static final MusicalSymbol THIRTY_SECOND_REST = register(new MusicalSymbol(
        "Thirty-Second Rest",
        "/icons/rest_32.png",
        "\uE4E8",
        MusicalSymbol.Type.REST, 5
    ));

    public static final MusicalSymbol SIXTY_FOURTH_REST = register(new MusicalSymbol(
        "Sixty-Fourth Rest",
        "/icons/rest_64.png",
        "\uE4E9",
        MusicalSymbol.Type.REST, 6
    ));



 // stanghette di battuta
 	// public MusicalSymbol(String name, String iconPath, String glyph, Type type)
    // Barline symbols
    public static final MusicalSymbol BARLINE_SINGLE = register(new MusicalSymbol(
        "Single Barline",
        "/icons/bar_single.png",
        "\uE030",
        MusicalSymbol.Type.BARLINE
    ));

    public static final MusicalSymbol BARLINE_DOUBLE = register(new MusicalSymbol(
        "Double Barline",
        "/icons/bar_double.png",
        "\uE031",
        MusicalSymbol.Type.BARLINE
    ));

    public static final MusicalSymbol BARLINE_FINAL = register(new MusicalSymbol(
        "Final Barline",
        "/icons/bar_end.png",
        "\uE032",
        MusicalSymbol.Type.BARLINE
    ));

    public static final MusicalSymbol BARLINE_REPEAT_START = register(new MusicalSymbol(
        "Repeat Start",
        "/icons/bar_startRepeat.png",
        "\uE040",
        MusicalSymbol.Type.BARLINE
    ));

    public static final MusicalSymbol BARLINE_REPEAT_END = register(new MusicalSymbol(
            "Repeat End",
            "/icons/bar_endRepeat.png",
            "\uE041",
            MusicalSymbol.Type.BARLINE
        ));

 // chiavi
 //	public MusicalSymbol(String name, String iconPath, String glyph, Type type, int midiOffset)
public static final MusicalSymbol CLEF_TREBLE = register(new MusicalSymbol(
        "Treble Clef",
        "/icons/treble.png",
        "\uE050",
        MusicalSymbol.Type.CLEF, 64,
        SemitoneMap.SEMITONE_MAP_TREBLE
    ));

public static final MusicalSymbol CLEF_BASS = register(new MusicalSymbol(
        "Bass Clef",
        "/icons/bass.png",
        "\uE062",
        MusicalSymbol.Type.CLEF, 43,
        SemitoneMap.SEMITONE_MAP_BASS
    ));

public static final MusicalSymbol CLEF_TREBLE_8 = register(new MusicalSymbol(
        "Treble 8 Clef",
        "/icons/treble8.png",
        "\uE052",
        MusicalSymbol.Type.CLEF, 52,
        SemitoneMap.SEMITONE_MAP_TREBLE_8
    ));
}
