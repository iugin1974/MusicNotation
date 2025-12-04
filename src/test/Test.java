package test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import scoreWriter.*;

public class Test {

	public static void main(String[] args) {
		
        
        // preparo lo score
        Score score = new Score();
        score.addStaff();
        score.addStaff(); // serve per simulare ambiente più reale, anche se usiamo solo staff 0

        // oggetti da inserire
        GraphicalObject n1 = new GraphicalNote(null); // VOICE_ONE
        n1.setX(100);

        GraphicalObject n2 = new GraphicalNote(null); // VOICE_ONE
        n2.setX(8);

        GraphicalObject w1 = new GraphicalBar(null);  // STAFF_WIDE
        w1.setX(35);

        GraphicalObject n3 = new GraphicalNote(null); // VOICE_TWO
        n3.setX(200);

        GraphicalObject n4 = new GraphicalNote(null); // VOICE_TWO
        n4.setX(112);

        // aggiungo gli oggetti allo score
        score.addObject(n1, 0, VoiceLayer.VoiceType.VOICE_ONE);
        score.addObject(n2, 0, VoiceLayer.VoiceType.VOICE_ONE);
        score.addObject(w1, 0, VoiceLayer.VoiceType.STAFF_WIDE);
        score.addObject(n3, 0, VoiceLayer.VoiceType.VOICE_TWO);
        score.addObject(n4, 0, VoiceLayer.VoiceType.VOICE_TWO);

        // creo il mixer
        LayerMixer lm = new LayerMixer(score);

        // mixiamo lo staff 0
        List<List<GraphicalObject>> result = lm.mixStaff(0);

        // estraiamo le due liste
        List<GraphicalObject> voiceOneList = result.get(0);
        List<GraphicalObject> voiceTwoList = result.get(1);

        // ---------------------------------------
        // TEST SU VOICE ONE + WIDE
        // ---------------------------------------
        assertEquals(3, voiceOneList.size(), "VoiceOne deve contenere 3 elementi");

        // ordine atteso (per X crescente): n2 (8), w1 (35), n1 (100)
        assertEquals(n2, voiceOneList.get(0));
        assertEquals(w1, voiceOneList.get(1));
        assertEquals(n1, voiceOneList.get(2));

        // ---------------------------------------
        // TEST SU VOICE TWO + WIDE
        // ---------------------------------------
 //       assertEquals(3, voiceTwoList.size(), "VoiceTwo deve contenere 3 elementi");

        // ordine atteso: w1 (35), n4 (112), n3 (200)
        assertEquals(w1, voiceTwoList.get(0));
        assertEquals(n4, voiceTwoList.get(1));
        assertEquals(n3, voiceTwoList.get(2));
    }
}
