package scoreWriter;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
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
	boolean mouseClicked(int x, int y) {
		if (staffList != null)
			return selectObject(x, y, staffList);
		return false;
	}

	boolean selectObject(int x, int y, ArrayList<ArrayList<GraphicalObject>> staffList) {
		boolean selected = false;
		for (int j = 0; j < staffList.size(); j++) {
			ArrayList<GraphicalObject> objects = staffList.get(j);
			for (int i = objects.size() - 1; i >= 0; i--) {
				GraphicalObject obj = objects.get(i);
				if (!selected && obj.contains(x, y)) {
					obj.select(true);
					selected = true;
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

	// TODO -> le note vengono solo inserite nello staff 0
	public void insertNote(GraphicalNote pointerNote) {
		int x = pointerNote.getX();
		int y = pointerNote.getY();
		int d = pointerNote.getDuration();
		int staffNumber = checkInWichStaffIsNote(x, y);
		if (staffNumber == -1)
			return;
		GraphicalNote n = new GraphicalNote(0);
		n.setX(x);
		n.setY(y);
		n.setDuration(d);
		n.select(false);
		// if (y > gs.getLineY(3))
		// n.setStemDirection(GraphicalNote.STEM_UP);
		// else
		// n.setStemDirection(GraphicalNote.STEM_DOWN);
		// int midi = calculateMidiNumber(gs, n);
		// n.setMidiNumber(midi);
		staffList.get(staffNumber).add(n);
		System.out.println("Staff " + staffNumber + " has now " + staffList.get(staffNumber).size() + " Notes.");
		gui.repaintPanel();
	}

	private int calculateMidiNumber(GraphicalStaff gs, GraphicalNote n) {

		int position = gs.getPosInStaff(n);
		int baseMidi = 60; // C4 (do centrale)
		int[] scale = { 0, 2, 4, 5, 7, 9, 11 }; // C D E F G A B

		int degree = position % 7;
		int octaveShift = position / 7;
		System.out.println(baseMidi + scale[degree] + (octaveShift * 12));
		return baseMidi + scale[degree] + (octaveShift * 12) + n.getAlteration();
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @return il numero dello staff in cui si trova il punto passato come argomento
	 *         oppure -1
	 */
	private int checkInWichStaffIsNote(int x, int y) {

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

	public void mouseDragged(int x, int y) {
		GraphicalObject o = getSelectedObject();
		if (o == null)
			return;
		if (o instanceof Movable) {
			((Movable) o).moveTo(x, y);
			gui.repaintPanel();
		}
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
	
}
