package scoreWriter;

import java.util.ArrayList;
import java.util.List;

public class VoiceLayer {

    /** Tipo di voce */
    public enum VoiceType {
        STAFF_WIDE,
        ONE_VOICE,
        VOICE_ONE,
        VOICE_TWO
    }

    private final VoiceType voiceType;
    private final List<GraphicalObject> objects;

    public VoiceLayer(VoiceType voiceType) {
        if (voiceType == null) {
            throw new IllegalArgumentException("voiceType non può essere null");
        }
        this.voiceType = voiceType;
        this.objects = new ArrayList<>();
    }

    /** Restituisce il tipo di voice */
    public VoiceType getVoiceType() {
        return voiceType;
    }

    /** Aggiunge un oggetto al layer */
    public void addObject(GraphicalObject o) {
        if (o != null) {
            objects.add(o);
        }
    }

    /** Rimuove un oggetto dal layer */
    public boolean removeObject(GraphicalObject o) {
        return objects.remove(o);
    }

    /** Restituisce tutti gli oggetti del layer (modificabile) */
    public List<GraphicalObject> getObjects() {
        return objects;
    }
    
    /** Restituisce solo le note **/
    public List<GraphicalNote> getNotes() {
    	List<GraphicalNote> notes = new ArrayList<>();
    	for (GraphicalObject object : objects) {
    		if (object instanceof GraphicalNote)
    			notes.add((GraphicalNote)object);
    	}
    	return notes;
    }

    /** Pulisce tutti gli oggetti dal layer */
    public void clear() {
        objects.clear();
    }
}
