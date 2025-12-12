package scoreWriter;

import javax.swing.*;
import java.awt.*;

public class KeySignatureDialog {

    /**
     * Mostra un dialogo modale con slider e combo per selezionare tonalità.
     * Ritorna un IntPair con il numero di alterazioni (-7..7) se OK, null se annullato.
     */
    public static IntPair showDialog(Component parent) {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, -7, 7, 0);
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

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(slider, BorderLayout.CENTER);

        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        modePanel.add(new JLabel("Modalità:"));
        modePanel.add(modeCombo);

        panel.add(modePanel, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(parent, panel, "Scegli tonalità",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            int alterations = slider.getValue(); // -7..7
            int mode = modeCombo.getSelectedIndex();
            return new IntPair(alterations, mode);
        }

        return null;
    }
}
