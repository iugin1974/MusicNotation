package scoreWriter;

import java.awt.event.KeyEvent;

import ui.GUI;

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
			controller.selectDuration((int)keyEvent.getKeyChar() - 48);
		}
	}
}
