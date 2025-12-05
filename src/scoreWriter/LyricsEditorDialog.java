package scoreWriter;

import javax.swing.*;

import scoreWriter.VoiceLayer.VoiceType;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class LyricsEditorDialog extends JDialog {

    private JComboBox<Integer> staffCombo;
    private JComboBox<Integer> verseCombo;
    private JTextArea lyricsArea;
    private JButton pasteButton;
    private JButton cancelButton;

    private ScoreWriter controller; // riferimento al controller

    public LyricsEditorDialog(JFrame parent, ScoreWriter controller) {
        super(parent, "Inserisci Lyrics", true);
        this.controller = controller;

        initComponents();
        layoutComponents();
        attachListeners();

        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        // Combo box staff
        int numStaffs = controller.getStaffCount();
        Integer[] staffNumbers = new Integer[numStaffs];
        for (int i = 0; i < numStaffs; i++) {
            staffNumbers[i] = i + 1; // Staff 1, 2, ...
        }
        staffCombo = new JComboBox<>(staffNumbers);

     //  verseCombo = new JComboBox<>(new );

        // Area testo lyrics
        lyricsArea = new JTextArea(10, 40);
        lyricsArea.setLineWrap(true);
        lyricsArea.setWrapStyleWord(true);

        // Bottoni
        pasteButton = new JButton("Paste");
        cancelButton = new JButton("Cancel");
    }

    private void layoutComponents() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Staff:"));
        topPanel.add(staffCombo);
        topPanel.add(new JLabel("Strophe:"));
      //  topPanel.add(verseCombo);

        JScrollPane scrollPane = new JScrollPane(lyricsArea);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(pasteButton);
        bottomPanel.add(cancelButton);

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout(5,5));
        cp.add(topPanel, BorderLayout.NORTH);
        cp.add(scrollPane, BorderLayout.CENTER);
        cp.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void attachListeners() {
        pasteButton.addActionListener(e -> {
            String text = lyricsArea.getText().trim();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Inserisci almeno una sillaba.", "Errore", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Split su spazi per ottenere le sillabe
            java.util.List<String> syllables = Arrays.asList(text.split("\\s+"));

            int staffIndex = staffCombo.getSelectedIndex();
       //     VoiceType voiceType = (VoiceType) voiceCombo.getSelectedItem();

            controller.addLyrics(syllables, staffIndex, VoiceType.VOICE_ONE);
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());
    }

    // Test rapido
    public static void main(String[] args) {
        // Supponendo che ScoreWriter sia già creato e passato qui
        JFrame frame = new JFrame("Test Lyrics Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setVisible(true);
    }
}
