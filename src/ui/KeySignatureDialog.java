package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

public class KeySignatureDialog {

    public static KeySignatureResult showDialog(Component parent) {
        JSlider slider = new JSlider(SwingConstants.HORIZONTAL, -7, 7, 0);
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);

        // Etichette personalizzate
        java.util.Hashtable<Integer, JLabel> labels = new java.util.Hashtable<>();
        labels.put(-7, new JLabel("7♭"));
        labels.put(0, new JLabel("C / a"));
        labels.put(7, new JLabel("7♯"));
        slider.setLabelTable(labels);
        slider.setPaintLabels(true);

        JComboBox<String> modeCombo = new JComboBox<>(new String[]{"Major", "Minor"});

        // Checkbox "All Staves", già selezionata
        JCheckBox allStavesCheckBox = new JCheckBox("All Staves", true);

        // Pannello principale
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(slider, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        southPanel.add(new JLabel("Modalità:"));
        southPanel.add(modeCombo);
        southPanel.add(allStavesCheckBox); // aggiunta della checkbox

        panel.add(southPanel, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(parent, panel, "Scegli tonalità",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            int alterations = slider.getValue(); // -7..7
            int mode = modeCombo.getSelectedIndex();
            boolean allStaves = allStavesCheckBox.isSelected();
            return new KeySignatureResult(alterations, mode, allStaves);
        }

        return null;
    }
}
