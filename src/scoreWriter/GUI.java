package scoreWriter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.InputStream;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import musicInterface.MusicObject;

public class GUI extends JFrame {

	private final int DISTANCE_BETWEEN_STAVES = 50;
	private ScoreWriter controller;
	private ArrayList<GraphicalStaff> staffList;
	private Font musicFont;
	private int mouseX, mouseY;
	private boolean insertNote = false;
	private GraphicalNote notePointer = null;
	private MainPanel mainPanel;
	private ButtonGroup group;

	private void initFont() {
		try (InputStream is = getClass().getResourceAsStream("/fonts/Bravura.otf")) {
			musicFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(40f);
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(musicFont);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public GUI(ScoreWriter controller) {
		this.controller = controller;
		initFont();
		setTitle("Editor Musicale");
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		// Pannello centrale
		mainPanel = new MainPanel();
		mainPanel.setBackground(Color.WHITE);
		add(mainPanel, BorderLayout.CENTER);

		add(noteToolbar(), BorderLayout.NORTH);

		// Barra menu
		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		JMenuItem nuovo = new JMenuItem("Nuovo");
		JMenuItem apri = new JMenuItem("Apri");
		JMenuItem salva = new JMenuItem("Salva");
		fileMenu.add(nuovo);
		fileMenu.add(apri);
		fileMenu.add(salva);

		JMenu modificaMenu = new JMenu("Modifica");
		JMenuItem addStaffMenu = new JMenuItem("add Staff");
		addStaffMenu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.addStaff();
				mainPanel.repaint();
			}
		});
		JMenuItem itemInsertNote = new JMenuItem("Insert Note");
		itemInsertNote.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				insertNote = true;
			}
		});
		modificaMenu.add(addStaffMenu);
		modificaMenu.add(itemInsertNote);

		menuBar.add(fileMenu);
		menuBar.add(modificaMenu);

		setJMenuBar(menuBar);

	}

	private JPanel noteToolbar() {
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		setBackground(Color.LIGHT_GRAY);

		String[] iconNames = { "nota_1.png", "nota_2.png", "nota_4.png", "nota_8.png", "nota_16.png", "nota_32.png",
				"nota_64.png" };

		group = new ButtonGroup();
		for (int i = 0; i < iconNames.length; i++) {
			JToggleButton button = new JToggleButton();
			button.setIcon(new ImageIcon(getClass().getResource("/icons/" + iconNames[i])));
			button.setActionCommand(String.valueOf(i));
			button.addActionListener(e -> {
				int index = Integer.parseInt(e.getActionCommand());
				insertNote = true;
				notePointer = new GraphicalNote(0); // crea la nota con MIDI = 0
				notePointer.setDuration(index);
			});
			group.add(button);
			p.add(button);
		}
		return p;
	}

	public ArrayList<GraphicalStaff> getStaffList() {
		return staffList;
	}

	public GraphicalStaff getStaff(int i) {
		return staffList.get(i);
	}

	private int calculateNextY() {
		int yPos = 0;
		for (GraphicalStaff s : staffList) {
			yPos += s.getHeight() + DISTANCE_BETWEEN_STAVES;
		}
		return yPos;
	}

	public GraphicalStaff addStaff() {
		if (staffList == null)
			staffList = new ArrayList<>();
		int y = calculateNextY();
		GraphicalStaff s = new GraphicalStaff(0, y, this.getWidth(), 5, 10, controller);
		staffList.add(s);
		return s;
	}

	private void drawNote() {

	}

	public void repaintPanel() {
		mainPanel.repaint();
	}

	class MainPanel extends JPanel implements MouseListener, MouseMotionListener {

		public MainPanel() {
			addMouseListener(this);
			addMouseMotionListener(this);
			addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					controller.keyPressed(e);
				}
			});

			setFocusable(true);
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setFont(musicFont);
			if (staffList != null) {
				for (int i = 0; i < staffList.size(); i++) {
					staffList.get(i).draw(g);
				}
			}
			if (insertNote && mouseX > 0 && mouseY > 0) {
				notePointer.draw(g);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (insertNote) {
				controller.insertNote(notePointer); // TODO scegli lo staff
			} else {
				controller.mouseClicked(e.getX(), e.getY());
			}
			repaint();
		}

		@Override
		public void mousePressed(MouseEvent e) {
			requestFocusInWindow();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			int mouseX = e.getX();
			int mouseY = e.getY();
			int snapY = mouseY;
			if (staffList != null && staffList.get(0).contains(mouseX, mouseY)) {
				ArrayList<Integer> snapPoints = staffList.get(0).getSnapPoints();
				int nearest = snapPoints.get(0);
				int minDist = Math.abs(mouseY - nearest);
				for (int y : snapPoints) {
					int d = Math.abs(mouseY - y);
					if (d < minDist) {
						minDist = d;
						nearest = y;
					}
				}
				snapY = nearest;
			}
			controller.mouseDragged(mouseX, snapY);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (insertNote && staffList != null) {
				mouseX = e.getX();
				mouseY = e.getY();
				// in quale Staff è il puntatore?
				int staffN = getPointedStaffIndex(mouseX, mouseY);
				if (staffN == -1)
					return;
				int snapY = mouseY;
				snapY = getSnapY(staffList.get(staffN), mouseY);
				notePointer.setXY(mouseX, snapY);
				repaint();
			}
		}
	}

	private int getPointedStaffIndex(int mouseX, int mouseY) {
		int staffN = -1;
		for (int i = 0; i < staffList.size(); i++) {
			if (staffList.get(i).contains(mouseX, mouseY)) {
				staffN = i;
				break;
			}
		}
		return staffN;
	}

	private int getSnapY(GraphicalStaff staff, int mouseY) {
		ArrayList<Integer> snapPoints = staff.getSnapPoints();
		int nearest = snapPoints.get(0);
		int minDist = Math.abs(mouseY - nearest);
		for (int y : snapPoints) {
			int d = Math.abs(mouseY - y);
			if (d < minDist) {
				minDist = d;
				nearest = y;
			}
		}
		return nearest;
	}

	public void exitInsertMode() {
		insertNote = false;
		group.clearSelection();
		repaint();
	}
}