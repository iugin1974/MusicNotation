package services;

import graphical.GraphicalScore;
import graphical.GraphicalStaff;

public class InsertResult {

	private int lastX = 0;
	private int lastY = 0;
	private int duration = 0;
	private boolean firstMidiInsertion = true;
	private GraphicalScore score;
	private final int FACTOR = 10;

	public InsertResult(GraphicalScore s) {
		score = s;
	}
	
	public void update(int x, int y, int duration) {
		lastX = x;
		lastY = y;
		this.duration = duration;
		System.out.println("InsertResult: x = "+x);
	}

	public void firstMidiInsertion(boolean b) {
		firstMidiInsertion = b;
	}

	public boolean isFirstMidiInsertion() {
		return firstMidiInsertion;
	}

	/**
	 * Prossima posizione musicale logica. Oggi coincide ancora con pixel-based
	 * tick, ma semanticamente è già "tempo".
	 */
	public int getNextTick() {
		int d = 7 - duration;
		d *= FACTOR;
		return lastX + d;
	}
	
	public int getLastY() {
		return lastY;
	}
	
	public int getLastX() {
		return lastX;
	}
	public GraphicalStaff getGraphicalStaff() {
		return score.getStaffAtPos(lastX, lastY);
	}
}
