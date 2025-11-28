package scoreWriter;

import java.util.ArrayList;
import java.util.Collections;

public class ObjectSaver {

	private ArrayList<ArrayList<GraphicalObject>> staffList;

	public ObjectSaver() {
		staffList = new ArrayList<>();
	}
	
	/** crea uno staff */
	public void addStaff() {
		staffList.add(new ArrayList<>());
	}

	/** Aggiunge un oggetto allo staff indicato */
	public void addObject(int staffNumber, GraphicalObject obj) {
		staffList.get(staffNumber).add(obj);
	}

	/** Restituisce la lista degli oggetti di uno staff specifico */
	public ArrayList<GraphicalObject> getObjects(int staffNumber) {
		if (staffNumber < 0 || staffNumber >= staffList.size()) {
			return null;
		}
		return staffList.get(staffNumber);
	}

	/** Restituisce tutte le note di uno staff specifico */
	public ArrayList<GraphicalNote> getNotes(int staffNumber) {
		ArrayList<GraphicalNote> notes = new ArrayList<>();
		for (GraphicalObject o : staffList.get(staffNumber)) {
			if (o instanceof GraphicalNote)
				notes.add((GraphicalNote)o);
		}
		return notes;
	}
	
	/** Restituisce lo staffList completo */
	public ArrayList<ArrayList<GraphicalObject>> getAllStaffs() {
		return staffList;
	}

	/** Rimuove un oggetto da uno staff */
	public boolean removeObject(int staffNumber, GraphicalObject obj) {
		if (staffNumber < 0 || staffNumber >= staffList.size())
			return false;
		return staffList.get(staffNumber).remove(obj);
	}

	/** Rimuove tutti gli oggetti da uno staff */
	public void clearStaff(int staffNumber) {
		if (staffNumber >= 0 && staffNumber < staffList.size()) {
			staffList.get(staffNumber).clear();
		}
	}

	/** Numero di staff presenti */
	public int getStaffCount() {
		return staffList.size();
	}
	
	/** Restituisce la nota successiva se esiste, oppure <i>null</i> */
	public GraphicalNote getNextNote(GraphicalNote n) {
		for (int i = 0; i < getStaffCount(); i++) {
			int index = getNotes(i).indexOf(n);
			// se c'è una nota successiva la ritorna
			if (index < getNotes(i).size()-1) return getNotes(i).get(index + 1);
		}
		return null;
	}
	
	/** Controlla se n1 e n2 sono consecutive **/
	public boolean areNotesConsecutive(GraphicalNote n1, GraphicalNote n2) {
	    return getNextNote(n1) == n2;
	}
	
	/** Ordina gli oggetti in base alla loro posizione orizzontale */
	public void sort(int staffNumber) {
		Collections.sort(staffList.get(staffNumber), new CompareXPos());
	}
	
	class CompareXPos implements java.util.Comparator<GraphicalObject> {
		@Override
		public int compare(GraphicalObject o1, GraphicalObject o2) {
			if (o1.getX() > o2.getX())
				return 1;
			if (o1.getX() < o2.getX())
				return -1;
			return 0;
		}
	}

}
