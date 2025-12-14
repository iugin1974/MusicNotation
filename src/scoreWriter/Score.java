package scoreWriter;

import java.util.List;

import graphical.GraphicalNote;
import graphical.GraphicalObject;
import model.Voice;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Collection;

public class Score {

	private List<Staff> staffList;
	

	public Score() {
		staffList = new ArrayList<>();
	}
	
	public Staff getStaff(int n) {
		return staffList.get(n);
	}
	
	/** crea uno staff */
	public void addStaff() {
		staffList.add(new Staff());
	}

	/** Aggiunge un oggetto allo staff e alla voce indicata */
	public void addObject(GraphicalObject obj, int staffNumber, int voiceNumber) {
		staffList.get(staffNumber).getVoice(voiceNumber).addObject(obj);
	}

	/** Restituisce la lista degli oggetti di uno staff e voce specifici */
	public List<GraphicalObject> getObjects(int staffNumber, int voiceNumber) {
		if (staffNumber < 0 || staffNumber >= staffList.size()) {
			return null;
		}
		return staffList.get(staffNumber).getVoice(voiceNumber).getObjects();
	}
	
	/** Restituisce la lista degli oggetti di uno staff e voce specifici */
	public List<GraphicalObject> getObjects(Staff staff, int voiceNumber) {
		return staff.getVoice(voiceNumber).getObjects();
	}

	/**
	 * Restituisce tutti gli oggetti di uno staff, di tutti i layer.
	 * Restituisce una lista vuota se lo staffNumber non è valido.
	 */
	public List<GraphicalObject> getObjects(int staffNumber) {
	    if (staffNumber < 0 || staffNumber >= staffList.size()) {
	        return List.of(); // lista immutabile vuota
	    }

	    Staff staff = staffList.get(staffNumber);
	    List<GraphicalObject> all = new ArrayList<>();

	    for (Voice layer : staff.getVoices()) {
	        all.addAll(layer.getObjects());
	    }

	    return all;
	}
	
	public List<GraphicalObject> getStaffWideObjects(int staffNumber) {
		Staff staff = getStaff(staffNumber);
		return staff.getObjects(0);
	}
	
	public List<GraphicalObject> getStaffWideObjects(Staff staff) {
		return staff.getObjects(0);
	}

	/**
	 * Restituisce tutte le note di uno staff specifico e di una voce specifica.
	 * Per il layer STAFF_WIDE ritorna una lista vuota.
	 */
	public List<GraphicalNote> getNotes(int staffNumber, int voiceNumber) {
	    
	    // controlli di sicurezza
	    if (staffNumber < 0 || staffNumber >= staffList.size())
	        return List.of(); // lista vuota immutabile

	    Staff staff = staffList.get(staffNumber);

	    Voice layer = staff.getVoice(voiceNumber);
	    if (layer == null)
	        return List.of();

	    // STAFF_WIDE non contiene note → ritorna lista vuota
	    if (layer.getVoiceType() == 0)
	        return List.of();

	    List<GraphicalNote> notes = new ArrayList<>();
	    
	    for (GraphicalObject o : layer.getObjects()) {
	        if (o instanceof GraphicalNote note) {
	            notes.add(note);
	        }
	    }
	    
	    return notes;
	}
	
	/** Restituisce lo staffList completo */
	public List<Staff> getAllStaves() {
		return staffList;
	}

	/** restituisce una lista con tutti gli oggetti di tutti gli staves */
	public List<GraphicalObject> getAllObjects() {
	    List<GraphicalObject> all = new ArrayList<>();
	    for (Staff staff : staffList) {
	        for (Voice v : staff.getVoices()) {
	            all.addAll(v.getObjects());
	        }
	    }
	    return all;
	}
	
	/** Rimuove un oggetto dalla score */
	public boolean removeObject(GraphicalObject obj) {
	    if (obj == null) 
	        return false;

	    for (Staff staff : staffList) {
	        if (staff.removeObject(obj)) {
	            return true; // trovato e rimosso
	        }
	    }
	    return false; // non trovato in nessuno staff
	}


	/** Rimuove tutti gli oggetti da uno staff */
	public void clearVoice(int staffNumber, int voiceNumber) {
		if (staffNumber >= 0 && staffNumber < staffList.size()) {
			staffList.get(staffNumber).clearVoice(voiceNumber);
		}
	}

	/** Numero di staff presenti */
	public int getStaffCount() {
		return staffList.size();
	}
	
	/** Restituisce la nota successiva nella stessa voce dello stesso staff,
	 * oppure null se è l'ultima.
	 */
	public GraphicalNote getNextNote(GraphicalNote note) {
	    if (note == null) return null;

	    Voice layer = getVoiceOf(note);
	    if (layer == null) return null;
	    
	    List<GraphicalObject> objs = layer.getObjects();
	    int index = objs.indexOf(note);

	    for (int i = index + 1; i < objs.size(); i++) {
	        if (objs.get(i) instanceof GraphicalNote next)
	            return next;
	    }

	    return null;
	}

	/** Restituisce la nota precedente nella stessa voce dello stesso staff,
	 * oppure null se è la prima.
	 */
	public GraphicalNote getPrevNote(GraphicalNote note) {
	    if (note == null) return null;

	    Voice layer = getVoiceOf(note);
	    if (layer == null) return null;
	    
	    List<GraphicalObject> objs = layer.getObjects();
	    int index = objs.indexOf(note);

	    for (int i = index - 1; i >= 0; i--) {
	        if (objs.get(i) instanceof GraphicalNote prev)
	            return prev;
	    }

	    return null;
	}

	/** Controlla se n1 e n2 sono consecutive **/
	public boolean areNotesConsecutive(GraphicalNote n1, GraphicalNote n2) {
	    return getNextNote(n1) == n2;
	}

	/** Restituisce la VoiceLayer che contiene la nota, oppure null. */
	public Voice getVoiceOf(GraphicalNote note) {
	    if (note == null) return null;

	    for (Staff staff : staffList) {
	        for (Voice layer : staff.getVoices()) {
	            if (layer.getVoiceType() == 0)
	                continue;

	            if (layer.getObjects().contains(note)) {
	                return layer;
	            }
	        }
	    }
	    return null;
	}

	public void sort() {
	    for (Staff staff : getAllStaves()) {
	        staff.sort();
	    }
	}

}
