package scoreWriter;

import java.util.ArrayList;

public class ObjectSaver {

	private ArrayList<ArrayList<GraphicalObject>> staffList;

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
}
