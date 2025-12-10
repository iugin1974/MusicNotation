package test;
import javax.swing.*;

import scoreWriter.GraphicalKeySignature;

import java.awt.*;
import java.awt.event.*;

public class TestKeySignature extends JPanel {

    GraphicalKeySignature ksSharp;
    GraphicalKeySignature ksFlat;

    public TestKeySignature() {

        int lineSpacing = 20;
        int staffY = 150;

        ksSharp = new GraphicalKeySignature(120, staffY, lineSpacing,
                GraphicalKeySignature.KeySigType.SHARP_4,
                GraphicalKeySignature.ClefType.TREBLE);

        ksFlat = new GraphicalKeySignature(120, staffY + 150, lineSpacing,
                GraphicalKeySignature.KeySigType.FLAT_3,
                GraphicalKeySignature.ClefType.BASS);

        MouseAdapter ma = new MouseAdapter() {

            GraphicalKeySignature selected = null;
            int lastX, lastY;

            @Override
            public void mousePressed(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();

                if (ksSharp.contains(lastX, lastY)) {
                    selected = ksSharp;
                } else if (ksFlat.contains(lastX, lastY)) {
                    selected = ksFlat;
                } else {
                    selected = null;
                }

                ksSharp.select(selected == ksSharp);
                ksFlat.select(selected == ksFlat);
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (selected != null) {
                    int dx = e.getX() - lastX;
                    int dy = e.getY() - lastY;
                    selected.moveBy(dx, dy);
                    lastX = e.getX();
                    lastY = e.getY();
                    repaint();
                }
            }
        };

        addMouseListener(ma);
        addMouseMotionListener(ma);

        setPreferredSize(new Dimension(800, 500));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawStaff(g, 100, 150, 20);     // Treble
        drawStaff(g, 100, 300, 20);     // Bass

        ksSharp.draw(g);
        ksFlat.draw(g);
    }

    private void drawStaff(Graphics g, int x, int y, int lineSpacing) {
        for (int i = 0; i < 5; i++) {
            int yy = y + i * lineSpacing;
            g.drawLine(x, yy, x + 600, yy);
        }
    }

    // ----------------------------------------------------------------------
    // MAIN
    // ----------------------------------------------------------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test Key Signature");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new TestKeySignature());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
