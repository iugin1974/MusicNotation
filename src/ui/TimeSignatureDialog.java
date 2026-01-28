package ui;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class TimeSignatureDialog {

	// Valori possibili del denominatore
	private static final int[] DENOMS = { 1, 2, 4, 8, 16, 32, 64 };

	/**
	 * Mostra un dialogo modale con due slider: - numeratore (2..32) - denominatore
	 * (1,2,4,8,16,32,64)
	 *
	 * Ritorna null se l’utente preme Cancel.
	 */
	public static TimeSignatureResult showDialog(Component parent) {

		// Slider per il numeratore
		JSlider numSlider = new JSlider(1, 32, 4); // default 4/4
		numSlider.setMajorTickSpacing(4);
		numSlider.setMinorTickSpacing(1);
		numSlider.setPaintTicks(true);
		numSlider.setPaintLabels(true);

		// Slider per il denominatore
		JSlider denomSlider = new JSlider(0, DENOMS.length - 1, 2); // indice 2 → 4 (per 4/4)
		denomSlider.setMajorTickSpacing(1);
		denomSlider.setPaintTicks(true);

		// Etichette personalizzate per il denominatore
		java.util.Hashtable<Integer, JLabel> labels = new java.util.Hashtable<>();
		for (int i = 0; i < DENOMS.length; i++) {
			labels.put(i, new JLabel(String.valueOf(DENOMS[i])));
		}
		denomSlider.setLabelTable(labels);
		denomSlider.setPaintLabels(true);

		// Checkbox "All Staves", già selezionata
		JCheckBox allStavesCheckBox = new JCheckBox("All Staves", true);

		// Pannello contenente slider e checkbox
		JPanel panel = new JPanel(new GridLayout(5, 1, 5, 5));
		panel.add(new JLabel("Numeratore:"));
		panel.add(numSlider);
		panel.add(new JLabel("Denominatore:"));
		panel.add(denomSlider);
		panel.add(allStavesCheckBox);

		int result = JOptionPane.showConfirmDialog(parent, panel, "Time Signature", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			int numerator = numSlider.getValue();
			int denominator = DENOMS[denomSlider.getValue()];
			boolean allStaves = allStavesCheckBox.isSelected();
			return new TimeSignatureResult(numerator, denominator, allStaves);
		}

		return null;
	}

}
