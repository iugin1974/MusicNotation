package scoreWriter;

import java.awt.event.KeyEvent;

import graphical.MusicalSymbol;

public class KeyboardHandler {

	private final Controller controller;
	
	
	public KeyboardHandler(Controller controller) {
		this.controller = controller;
	}

	public void keyPressed(KeyEvent keyEvent) {
		if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
			controller.exitInsertMode();
		} else if (keyEvent.getKeyCode() == KeyEvent.VK_DELETE) {
			controller.deleteSelectedObject();
		} else if (keyEvent.getKeyChar() == 's') {
			controller.slurOrTie();
		} else if (keyEvent.getKeyChar() >= '1' && keyEvent.getKeyChar() <= '7') {
			controller.keyPressed((int)keyEvent.getKeyChar() - 48);
		} else if (keyEvent.getKeyChar() == 'n') {
			controller.setInsertType(MusicalSymbol.Type.NOTE);
		} else if (keyEvent.getKeyChar() == 'r') {
			controller.setInsertType(MusicalSymbol.Type.REST);
		} else if (keyEvent.getKeyChar() == 'b') {
			controller.setInsertType(MusicalSymbol.Type.BARLINE);
		} else if (keyEvent.getKeyChar() == 'c') {
			controller.setInsertType(MusicalSymbol.Type.CLEF);
		}
	}
}
