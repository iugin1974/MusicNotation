package scoreWriter;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class InfoPanel extends JPanel {
    private Map<String, JLabel> labels;

    public InfoPanel() {
        setLayout(new GridLayout(0, 1)); // una colonna, righe multiple
        labels = new HashMap<>();
    }

    // Aggiunge una nuova riga o aggiorna quella esistente
    public void setInfo(String key, String value) {
        JLabel label = labels.get(key);
        if (label == null) {
            // Se la riga non esiste, la creiamo
            label = new JLabel(key + ": " + value);
            labels.put(key, label);
            add(label);
            revalidate(); // aggiorna il layout
            repaint();
        } else {
            // Aggiorna solo il valore
            label.setText(key + ": " + value);
        }
    }

    // Esempio di utilizzo
    public static void main(String[] args) throws InterruptedException {
        JFrame frame = new JFrame("Info Panel Demo");
        InfoPanel panel = new InfoPanel();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.setSize(300, 200);
        frame.setVisible(true);

        // Simulazione aggiornamento dati
        for (int i = 0; i <= 10; i++) {
            panel.setInfo("Number Note", String.valueOf(i));
            panel.setInfo("Temperature", String.valueOf(20 + i));
            panel.setInfo("Speed", String.valueOf(100 - i * 5));
            Thread.sleep(500); // simula aggiornamento ogni mezzo secondo
        }
    }
}

