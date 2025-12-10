package scoreWriter;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class LyricsEditorDialog extends JDialog {

    private JComboBox<Integer> staffCombo;
    private JComboBox<Integer> voiceCombo;
    private JComboBox<Integer> stanzaCombo;
    private JTextArea lyricsArea;
    private JButton pasteButton;
    private JButton cancelButton;

    private final ScoreWriter controller;

    public LyricsEditorDialog(JFrame parent, ScoreWriter controller) {
        super(parent, "Inserisci Lyrics", true);
        this.controller = controller;

        initComponents();
        layoutComponents();
        attachListeners();

        loadLyricsIfExist();  // carica lyrics iniziali

        pack();
        setLocationRelativeTo(parent);
    }

    // -------------------------------------------------------------------------
    //  Caricamento delle lyrics esistenti
    // -------------------------------------------------------------------------
    private void loadLyricsIfExist() {
        int staffIndex = staffCombo.getSelectedIndex();
        int voiceNumber = voiceCombo.getSelectedIndex() + 1;
        int stanzaNumber = stanzaCombo.getSelectedIndex();

        List<String> syllables = controller.getLyricsFor(staffIndex, voiceNumber, stanzaNumber);
        if (syllables == null) return;
        if (syllables == null || syllables.isEmpty()) {
            lyricsArea.setText("");
            return;
        }

        // Ricostruisce la frase originale unendo tutte le sillabe
        StringBuilder sb = new StringBuilder();
        for (String s : syllables) {
            sb.append(s).append(" ");
        }

        lyricsArea.setText(sb.toString().trim());
    }

    // -------------------------------------------------------------------------
    //  Inizializzazione componenti
    // -------------------------------------------------------------------------
    private void initComponents() {

        // STAFF
        int numStaffs = controller.getStaffCount();
        Integer[] staffNumbers = new Integer[numStaffs];
        for (int i = 0; i < numStaffs; i++) staffNumbers[i] = i + 1;
        staffCombo = new JComboBox<>(staffNumbers);

        // VOICE (inizialmente per staff 0)
        // parte da 1 perché la voice 0 è staffwide
        int numVoices = controller.getStaffList().get(0).getVoices().size() - 1;
        Integer[] voiceNumbers = new Integer[numVoices];
        for (int i = 0; i < numVoices; i++) voiceNumbers[i] = i + 1;
        voiceCombo = new JComboBox<>(voiceNumbers);

        // STANZA 1–10
        Integer[] stanzaNumbers = new Integer[10];
        for (int i = 0; i < 10; i++) stanzaNumbers[i] = i + 1;
        stanzaCombo = new JComboBox<>(stanzaNumbers);

        // AREA TESTO
        lyricsArea = new JTextArea(10, 40);
        lyricsArea.setLineWrap(true);
        lyricsArea.setWrapStyleWord(true);

        // BOTTONI
        pasteButton = new JButton("Paste");
        cancelButton = new JButton("Cancel");
    }

    // -------------------------------------------------------------------------
    //  Layout
    // -------------------------------------------------------------------------
    private void layoutComponents() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Staff:"));
        topPanel.add(staffCombo);
        topPanel.add(new JLabel("Voice:"));
        topPanel.add(voiceCombo);
        topPanel.add(new JLabel("Stanza:"));
        topPanel.add(stanzaCombo);

        JScrollPane scrollPane = new JScrollPane(lyricsArea);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(pasteButton);
        bottomPanel.add(cancelButton);

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout(5, 5));
        cp.add(topPanel, BorderLayout.NORTH);
        cp.add(scrollPane, BorderLayout.CENTER);
        cp.add(bottomPanel, BorderLayout.SOUTH);
    }

    // -------------------------------------------------------------------------
    //  LISTENER
    // -------------------------------------------------------------------------
    private void attachListeners() {

        // Quando cambia lo staff → aggiorna le voci e ricarica eventuali lyrics
        staffCombo.addActionListener(e -> {
            int staffIndex = staffCombo.getSelectedIndex();
            int numVoices = controller.getStaffList().get(staffIndex).getVoices().size();

            voiceCombo.removeAllItems();
            for (int i = 0; i < numVoices; i++) voiceCombo.addItem(i + 1);

            loadLyricsIfExist();
        });

        // Voice e stanza cambiano solo il caricamento, mai salvataggio
        voiceCombo.addActionListener(e -> loadLyricsIfExist());
        stanzaCombo.addActionListener(e -> loadLyricsIfExist());

        // PASTE → salva le lyrics nella partitura
        pasteButton.addActionListener(e -> {
            String text = lyricsArea.getText().trim();

            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Inserisci almeno una sillaba.",
                        "Errore",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            List<String> syllables = Arrays.asList(text.split("\\s+"));

            int staffIndex = staffCombo.getSelectedIndex();
            int voiceNumber = voiceCombo.getSelectedIndex();
            int stanzaNumber = stanzaCombo.getSelectedIndex();

            controller.addLyrics(syllables, staffIndex, voiceNumber, stanzaNumber);

            dispose();
        });

        cancelButton.addActionListener(e -> dispose());
    }
}
