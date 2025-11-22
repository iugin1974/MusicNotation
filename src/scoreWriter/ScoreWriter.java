package scoreWriter;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.swing.SwingUtilities;

import musicInterface.MusicObject;

public class ScoreWriter {

	private ArrayList<ArrayList<GraphicalObject>> staffList;
	// private ArrayList<GraphicalObject> graphicalObjects = new ArrayList<>();
	private GUI gui;

	private void test() {
		addStaff();
		addStaff();
	}

	public void addStaff() {
		ArrayList<GraphicalObject> staff = new ArrayList<>();
		if (staffList == null)
			staffList = new ArrayList<>();
		staffList.add(staff);
		gui.addStaff(getStaffNumber() - 1);
		gui.repaint();
	}

	public ArrayList<GraphicalObject> getStaff(int i) {
		return staffList.get(i);
	}

	/**
	 * ritorna <i>true</i> se l'utente ha cliccato su un elemento
	 * 
	 * @param args
	 */
	public boolean selectObjectAtPos(int x, int y) {
		if (staffList != null)
			return selectObject(x, y, staffList);
		return false;
	}

	private boolean selectObject(int x, int y, ArrayList<ArrayList<GraphicalObject>> staffList) {
		boolean selected = false;
		for (int j = 0; j < staffList.size(); j++) {
			ArrayList<GraphicalObject> objects = staffList.get(j);
			for (int i = objects.size() - 1; i >= 0; i--) {
				GraphicalObject obj = objects.get(i);
				if (!selected && obj.contains(x, y)) {
					obj.select(true);
					selected = true;
					break;
				} else {
					obj.select(false);
				}
			}
		}
		return selected;
	}

	public int getStaffNumber() {
		if (staffList == null)
			return 0;
		return staffList.size();
	}

	public ArrayList<ArrayList<GraphicalObject>> getStaffList() {
		return staffList;
	}

	private void go() {
		gui = new GUI(this);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				gui.setVisible(true);
				test();
			}
		});
	}

	public static void main(String[] args) {
		new ScoreWriter().go();
	}

	private void insertNote(GraphicalNote n, int staffNumber) {
		GraphicalNote newNote = (GraphicalNote) n.cloneObject();
		staffList.get(staffNumber).add(newNote);
	}

	private void insertBar(GraphicalBar bar, int staffNumber) {
		GraphicalBar b = (GraphicalBar) bar.cloneObject();
		int firstLine = gui.getStaff(staffNumber).getLineY(1);
		b.setY(firstLine);
		staffList.get(staffNumber).add(b);
	}

	private void insertClef(GraphicalClef clef, int staffNumber) {
		GraphicalClef c = (GraphicalClef) clef.cloneObject();
		int firstLine = 0;
		if (clef.getSymbol().equals(SymbolRegistry.CLEF_TREBLE)
				|| clef.getSymbol().equals(SymbolRegistry.CLEF_TREBLE_8))
			firstLine = gui.getStaff(staffNumber).getLineY(2);
		else if (clef.getSymbol().equals(SymbolRegistry.CLEF_BASS))
			firstLine = gui.getStaff(staffNumber).getLineY(4);
			c.setY(firstLine);
		staffList.get(staffNumber).add(c);
	}

	public void insertObject(Pointer pointer, GraphicalObject object) {
		int x = pointer.getX();
		int y = pointer.getY();
		int staffNumber = checkInWichStaffIsPoint(x, y);
		if (staffNumber == -1)
			return;
		object.setX(x);
		object.setY(y);
		object.select(false);
		if (object instanceof GraphicalNote)
			insertNote((GraphicalNote) object, staffNumber);
		else if (object instanceof GraphicalBar)
			insertBar((GraphicalBar) object, staffNumber);
		else if (object instanceof GraphicalClef)
			insertClef((GraphicalClef) object, staffNumber);
		sortObjectsInStaff(staffNumber);
		gui.repaintPanel();
	}

	private void sortObjectsInStaff(int staffNumber) {
		Collections.sort(staffList.get(staffNumber), new CompareXPos());
	}

	
	/**
	 * 
	 * @param x
	 * @param y
	 * @return il numero dello staff in cui si trova il punto passato come argomento
	 *         oppure -1
	 */
	private int checkInWichStaffIsPoint(int x, int y) {

		for (int i = 0; i < getStaffNumber(); i++) {
			GraphicalStaff gs = gui.getStaff(i);
			if (gs.contains(x, y)) {
				System.out.println("Note inside Staff " + i);
				return i;
			}
		}
		return -1;
	}

	public void keyPressed(KeyEvent keyEvent) {
		if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
			gui.exitInsertMode();
		} else if (keyEvent.getKeyCode() == KeyEvent.VK_DELETE) {
			deleteSelectedObject();
		}
	}

	private void deleteSelectedObject() {
		GraphicalObject object = getSelectedObject();
		if (object == null)
			return;
		for (ArrayList<GraphicalObject> staff : staffList) {
			if (staff.contains(object)) {
				staff.remove(object);
				break;
			}
		}
		gui.repaintPanel();
	}

	public void mousePressed(int x, int y) {
		selectObjectAtPos(x, y);
		GraphicalObject o = getSelectedObject();
		if (o == null)
			return;
		o.moveTo(x, y);
		gui.repaintPanel();
	
	}
	public void mouseDragged(int x, int y) {
		GraphicalObject o = getSelectedObject();
		if (o == null)
			return;
		o.moveTo(x, y);
		gui.repaintPanel();
	}

	public void mouseReleased(int x, int y) {
			int sn = checkInWichStaffIsPoint(x, y);
			sortObjectsInStaff(sn);
	}

	private GraphicalObject getSelectedObject() {
		for (ArrayList<GraphicalObject> staff : staffList) {
			for (GraphicalObject object : staff) {
				if (object.isSelected()) {
					return object;
				}
			}
		}
		return null;
	}

	public void export() {
		Exporter x = new Exporter(gui);
		x.setStaffs(staffList);
		x.parse();
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
