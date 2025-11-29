package scoreWriter;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.CubicCurve2D;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

import musicInterface.MusicObject;

public class GUI extends JFrame {

	private final int DISTANCE_BETWEEN_STAVES = 50;
	private final int TOP_MARGIN = 50;
	private ScoreWriter controller;
	private Font musicFont;
	private int mouseX, mouseY;
	private boolean insertMode = false;
	private MainPanel mainPanel;
	private ArrayList<GraphicalStaff> staffList;
	private ButtonGroup groupButtonsNotes;
	private ButtonGroup groupButtonsBars;
	private ButtonGroup groupButtonsClef;
	private GraphicalObject objectToInsert;
	private JScrollPane scrollPane;

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
	    scrollPane = new JScrollPane(
	            mainPanel,
	            JScrollPane.VERTICAL_SCROLLBAR_NEVER,      // niente scroll verticale
	            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED // scroll orizzontale se serve
	    );
	    add(scrollPane, BorderLayout.CENTER);

	    // 🔹 Toolbar principale (già esistente)
	    JPanel topBar = new JPanel(new BorderLayout());
	    topBar.add(mainToolbar(), BorderLayout.WEST);

	    // 🔹 Aggiungo la nuova voce toolbar 
	    topBar.add(voiceToolbar(), BorderLayout.EAST);

	    add(topBar, BorderLayout.NORTH);

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
	        }
	    });

	    JMenuItem itemInsertNote = new JMenuItem("Insert Note");
	    itemInsertNote.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            insertMode = true;
	        }
	    });

	    salva.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            controller.export();
	        }
	    });

	    modificaMenu.add(addStaffMenu);
	    modificaMenu.add(itemInsertNote);

	    menuBar.add(fileMenu);
	    menuBar.add(modificaMenu);

	    setJMenuBar(menuBar);
	}


	private JPanel mainToolbar() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBackground(Color.LIGHT_GRAY);

		// Pannello orizzontale per i pulsanti
		JPanel toolbarPanel = new JPanel();
		toolbarPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		toolbarPanel.setBackground(Color.LIGHT_GRAY);

		// Aggiungi le toolbar specifiche
		toolbarPanel.add(noteToolbar());
		toolbarPanel.add(barlineToolbar());
		toolbarPanel.add(clefToolbar());
		// Potrai aggiungere altre toolbar qui in futuro:
		// toolbarPanel.add(clefToolbar());
		// toolbarPanel.add(keySignatureToolbar());
		// toolbarPanel.add(timeSignatureToolbar());
		// ecc.

		// Inserisci la toolbar nella parte alta del main panel
		mainPanel.add(toolbarPanel, BorderLayout.NORTH);

		return mainPanel;
	}

	private JPanel noteToolbar() {
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		setBackground(Color.LIGHT_GRAY);

		MusicalSymbol[] notes = { SymbolRegistry.WHOLE_NOTE, SymbolRegistry.HALF_NOTE, SymbolRegistry.QUARTER_NOTE,
				SymbolRegistry.EIGHTH_NOTE, SymbolRegistry.SIXTEENTH_NOTE, SymbolRegistry.THIRTY_SECOND_NOTE,
				SymbolRegistry.SIXTY_FOURTH_NOTE };

		groupButtonsNotes = new ButtonGroup();

		for (MusicalSymbol noteSymbol : notes) {
			JToggleButton button = new JToggleButton();
			button.setIcon(new ImageIcon(getClass().getResource(noteSymbol.getIconPath())));
			button.setBorder(BorderFactory.createEmptyBorder());
			button.setMargin(new Insets(0, 0, 0, 0));
			button.setContentAreaFilled(false);
			button.setFocusPainted(false);
			button.addChangeListener(e -> {
				if (button.isSelected()) {
					button.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
				} else {
					button.setBorder(BorderFactory.createEmptyBorder());
				}
			});
			button.addActionListener(e -> {
				removeOtherSelections(groupButtonsNotes);
				objectToInsert = new GraphicalNote(noteSymbol); // passo direttamente il simbolo
				insertMode = true;
				controller.setPointer(noteSymbol); // pointer riceve il simbolo da mostrare
			});
			groupButtonsNotes.add(button);
			p.add(button);
		}
		return p;
	}

	private JPanel clefToolbar() {
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		setBackground(Color.LIGHT_GRAY);

		MusicalSymbol[] clefs = { SymbolRegistry.CLEF_TREBLE, SymbolRegistry.CLEF_BASS, SymbolRegistry.CLEF_TREBLE_8 };

		groupButtonsClef = new ButtonGroup();

		for (MusicalSymbol clefSymbol : clefs) {
			JToggleButton button = new JToggleButton();
			button.setIcon(new ImageIcon(getClass().getResource(clefSymbol.getIconPath())));
			button.setBorder(BorderFactory.createEmptyBorder());
			button.setMargin(new Insets(0, 0, 0, 0));
			button.setContentAreaFilled(false);
			button.setFocusPainted(false);
			button.addChangeListener(e -> {
				if (button.isSelected()) {
					button.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
				} else {
					button.setBorder(BorderFactory.createEmptyBorder());
				}
			});
			button.addActionListener(e -> {
				removeOtherSelections(groupButtonsNotes);
				objectToInsert = new GraphicalClef(clefSymbol); // passo direttamente il simbolo
				insertMode = true;
				controller.setPointer(clefSymbol); // pointer riceve il simbolo da mostrare
			});
			groupButtonsClef.add(button);
			p.add(button);
		}
		return p;
	}

	private JPanel barlineToolbar() {
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		p.setBackground(Color.LIGHT_GRAY);

		MusicalSymbol[] barlines = { SymbolRegistry.SINGLE_BARLINE, SymbolRegistry.DOUBLE_BARLINE,
				SymbolRegistry.FINAL_BARLINE, SymbolRegistry.REPEAT_START_BARLINE, SymbolRegistry.REPEAT_END_BARLINE };

		groupButtonsBars = new ButtonGroup();

		for (MusicalSymbol barlineSymbol : barlines) {
			JToggleButton button = new JToggleButton();
			button.setIcon(new ImageIcon(getClass().getResource(barlineSymbol.getIconPath())));
			button.setBorder(BorderFactory.createEmptyBorder());
			button.setMargin(new Insets(0, 0, 0, 0));
			button.setContentAreaFilled(false);
			button.setFocusPainted(false);

			button.addChangeListener(e -> {
				if (button.isSelected()) {
					button.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
				} else {
					button.setBorder(BorderFactory.createEmptyBorder());
				}
			});

			button.addActionListener(e -> {
				removeOtherSelections(groupButtonsBars);
				objectToInsert = new GraphicalBar(barlineSymbol); // passo direttamente il simbolo
				insertMode = true;
				controller.setPointer(barlineSymbol); // passa direttamente il simbolo
			});

			groupButtonsBars.add(button);
			p.add(button);
		}

		return p;
	}
	
	private JPanel voiceToolbar() {

	    JPanel voiceNumberToolbar = new JPanel();
	    voiceNumberToolbar.setLayout(new FlowLayout(FlowLayout.LEFT));

	    // Pulsanti toggle
	    JToggleButton voice1Button = new JToggleButton("1");
	    JToggleButton voice2Button = new JToggleButton("2");

	    // Gruppo (solo uno può essere attivo)
	    ButtonGroup group = new ButtonGroup();
	    group.add(voice1Button);
	    group.add(voice2Button);

	    // Selezione iniziale
	    voice1Button.setSelected(true);

	    // Listener condiviso
	    ActionListener voiceListener = e -> {
	        if (voice1Button.isSelected()) {
	            controller.setCurrentVoice(1);
	        } else if (voice2Button.isSelected()) {
	        	controller.setCurrentVoice(2);
	        }
	    };

	    voice1Button.addActionListener(voiceListener);
	    voice2Button.addActionListener(voiceListener);

	    // Aggiunta al pannello
	    voiceNumberToolbar.add(voice1Button);
	    voiceNumberToolbar.add(voice2Button);
	    
	    return voiceNumberToolbar;
	}

	private int calculateNextY() {
		int yPos = TOP_MARGIN;
		for (GraphicalStaff s : staffList) {
			yPos += s.getHeight() + DISTANCE_BETWEEN_STAVES;
		}
		return yPos;
	}

	public GraphicalStaff getStaff(int i) {
		return staffList.get(i);
	}

	public void addStaff(int id) {
		if (staffList == null)
			staffList = new ArrayList<>();
		int yPos = calculateNextY();
		GraphicalStaff gs = new GraphicalStaff(id, 0, yPos, mainPanel.getWidth(), 5, 10, controller);
		staffList.add(gs);
	}

	public void repaintPanel() {
		mainPanel.repaint();
	}

	public void resizePanel(int w, int h) {
		mainPanel.setPreferredSize(new Dimension(w, h));
		mainPanel.revalidate();

	}

	class MainPanel extends JPanel implements MouseListener, MouseMotionListener, ComponentListener {

		private HashMap<GraphicalObject, Integer> startPositions;

		public MainPanel() {
			addMouseListener(this);
			addComponentListener(this);
			addMouseMotionListener(this);
			addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					controller.keyPressed(e);
				}
			});

			setFocusable(true);
		}

		private void drawStaves(Graphics g) {
			for (GraphicalStaff gs : staffList) {
				gs.draw(g);
			}
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setFont(musicFont);
			if (staffList == null)
				return;
			drawStaves(g);
			for (GraphicalObject object: controller.getAllObjects()) {
				object.draw(g);
			}
			
			if (insertMode && mouseX > 0 && mouseY > 0) {
				controller.getPointer().draw(g);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			requestFocusInWindow();
			boolean ctrl = e.isControlDown();

			if (insertMode) {
				controller.insertObject(objectToInsert);
			} else {
				controller.selectObjectAtPos(e.getX(), e.getY(), ctrl);
			}

			repaint();
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.isControlDown())
				startPositions = controller.getStartXPositions(e.getX(), e.getY());
			else {
				controller.selectObjectAtPos(e.getX(), e.getY(), false);	
			}
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
			// 1) Trova lo staff sotto il mouse
			int staffNumber = getPointedStaffIndex(mouseX, mouseY);
			GraphicalStaff targetStaff = staffList.get(staffNumber);

			if (e.isControlDown()) {
				controller.shiftHorizontal(mouseX, startPositions, staffNumber);
			} else {
				int snapY = mouseY; // di default nessuno snap

				// 2) Se abbiamo trovato lo staff: snap verticale
				if (targetStaff != null) {
					ArrayList<Integer> snapPoints = targetStaff.getSnapPoints();

					// Trova lo snap point più vicino
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

				// 3) Muovi l'oggetto (qui avviene lo snap orizzontale tra pentagrammi)
				controller.moveObjects(mouseX, snapY);

			}
			repaint();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (staffList != null && controller.pointerExists()) {
				mouseX = e.getX();
				mouseY = e.getY();
				// in quale Staff è il puntatore?
				int staffN = getPointedStaffIndex(mouseX, mouseY);
				if (staffN == -1)
					return;
				int snapY = mouseY;
				snapY = getSnapY(staffList.get(staffN), mouseY);
				controller.movePointerTo(mouseX, snapY);
				repaint();
			}
		}

		@Override
		public void componentResized(ComponentEvent e) {
			if (staffList == null)
				return;
			int w = this.getWidth();
			int h = this.getHeight();
			for (GraphicalStaff s : staffList) {
				if (s.getWidth() > w)
					return; // setta la lunghezza solo se è inferiore a quella della finestra
				s.setWidth(w);
			}
			mainPanel.setPreferredSize(new Dimension(w, h));
			mainPanel.revalidate();
			mainPanel.repaint();
			repaint();

		}

		@Override
		public void componentMoved(ComponentEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void componentShown(ComponentEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void componentHidden(ComponentEvent e) {
			// TODO Auto-generated method stub

		}
	}

	public int getPointedStaffIndex(int mouseX, int mouseY) {
		int staffN = -1;
		for (int i = 0; i < staffList.size(); i++) {
			if (staffList.get(i).contains(mouseX, mouseY)) {
				staffN = i;
				break;
			}
		}
		return staffN;
	}

	public ArrayList<GraphicalStaff> getStaffList() {
		return staffList;
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

	private void removeOtherSelections(ButtonGroup clickedGroup) {
		if (clickedGroup != groupButtonsNotes) {
			groupButtonsNotes.clearSelection();
			insertMode = false;
			// TODO
		}
		if (clickedGroup != groupButtonsBars) {
			groupButtonsBars.clearSelection();
			insertMode = false;
			// TODO

		}
	}

	public void exitInsertMode() {
		insertMode = false;
		groupButtonsNotes.clearSelection();
		groupButtonsBars.clearSelection();
		groupButtonsClef.clearSelection();
		controller.destroyPointer();
		repaint();
	}

	public GraphicalObject getObjectToInsert() {
		return objectToInsert;
	}

	public void setObjectToInsert(GraphicalObject objectToInsert) {
		this.objectToInsert = objectToInsert;
	}
}