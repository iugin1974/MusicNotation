package scoreWriter;

import java.util.ArrayList;
import java.util.List;

public class Voice {

    private final int voiceNumber;
    private final List<GraphicalObject> objects;

    public Voice(int voiceNumber) {
        this.voiceNumber = voiceNumber;
        this.objects = new ArrayList<>();
    }

    /** Restituisce il tipo di voice */
    public int getVoiceType() {
        return voiceNumber;
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
    	sort();
        return objects;
    }
    
    /** Restituisce solo le note **/
    public List<GraphicalNote> getNotes() {
    	List<GraphicalNote> notes = new ArrayList<>();
    	sort();
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
    
    public void sort() {
    	objects.sort(new CompareXPos());
    }
}
