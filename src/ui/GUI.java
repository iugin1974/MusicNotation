package ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
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
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import Measure.Bar;
import graphical.GraphicalBar;
import graphical.GraphicalClef;
import graphical.GraphicalNote;
import graphical.GraphicalObject;
import graphical.GraphicalRest;
import graphical.GraphicalScore;
import graphical.GraphicalStaff;
import graphical.LedgerLinesRenderer;
import graphical.MusicalSymbol;
import musicEvent.Note;
import musicInterface.MusicObject;
import notation.ScoreEvent;
import notation.ScoreListener;
import notation.Staff;
import scoreWriter.Controller;
import scoreWriter.ScoreWriter;
import scoreWriter.SymbolRegistry;

public class GUI extends JFrame implements ScoreListener {

	private final int DISTANCE_BETWEEN_STAVES = 50;
	private final int TOP_MARGIN = 50;
	private Controller controller;
	private Font musicFont, iconFont;
	private int mouseX, mouseY;
	private boolean insertMode = false;
	private MainPanel mainPanel;
	private ButtonGroup groupButtonsNotes;
	private ButtonGroup groupButtonsBars;
	private ButtonGroup groupButtonsClef;
	private MusicalSymbol objectToInsert;
	private JScrollPane scrollPane;
	private ButtonGroup groupButtonsRests;
	private LedgerLinesRenderer ledger;
	private Font fontLyric = new Font("SansSerif", Font.PLAIN, 12);
	protected GraphicalScore gScore;
	private MusicObject pendingObject;
	private int pendingX;
	private int pendingY;
	private int pendingVoice;
	private JToggleButton voice1;
	private JToggleButton voice2;

	private void initFont() {
		try {
			// font principale (dimensione 40)
			try (InputStream is1 = getClass().getResourceAsStream("/fonts/Bravura.otf")) {
				musicFont = Font.createFont(Font.TRUETYPE_FONT, is1).deriveFont(40f);
			}

			// font secondario (dimensione 20)
			try (InputStream is2 = getClass().getResourceAsStream("/fonts/Bravura.otf")) {
				iconFont = Font.createFont(Font.TRUETYPE_FONT, is2).deriveFont(20f);
			}

			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(musicFont);
			ge.registerFont(iconFont);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public GUI(Controller controller, GraphicalScore gScore) {
		this.controller = controller;
		this.gScore = gScore;
		ledger = new LedgerLinesRenderer();
		initFont();
		setTitle("Editor Musicale");
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		// Pannello centrale
		mainPanel = new MainPanel();
		mainPanel.setBackground(Color.WHITE);
		scrollPane = new JScrollPane(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, // niente scroll verticale
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

		JMenu lyricsMenu = new JMenu("Lyrics");
		JMenuItem addLyricsMenu = new JMenuItem("add Lyrics");

		addLyricsMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 'MainFrame.this' passa il JFrame principale alla dialog
				LyricsEditorDialog led = new LyricsEditorDialog(GUI.this, controller);
				led.setVisible(true); // mostra la finestra modale
				repaintPanel();
			}
		});

		lyricsMenu.add(addLyricsMenu);

		menuBar.add(fileMenu);
		menuBar.add(modificaMenu);
		menuBar.add(lyricsMenu);

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
		toolbarPanel.add(restToolbar());
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
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
		p.setBackground(new Color(230, 230, 230));

		MusicalSymbol[] notes = { SymbolRegistry.WHOLE_NOTE, SymbolRegistry.HALF_NOTE, SymbolRegistry.QUARTER_NOTE,
				SymbolRegistry.EIGHTH_NOTE, SymbolRegistry.SIXTEENTH_NOTE, SymbolRegistry.THIRTY_SECOND_NOTE,
				SymbolRegistry.SIXTY_FOURTH_NOTE };

		groupButtonsNotes = new ButtonGroup();

		for (MusicalSymbol noteSymbol : notes) {
			JToggleButton btn = createIconToggleButton(noteSymbol.getGlyphUp(), iconFont);
			btn.addActionListener(e -> {
				removeOtherSelections(groupButtonsNotes);
				objectToInsert = noteSymbol;
				insertMode = true;
				controller.setPointer(noteSymbol);
			});
			groupButtonsNotes.add(btn);
			p.add(btn);
		}

		return p;
	}

	private JPanel restToolbar() {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
		p.setBackground(new Color(230, 230, 230));

		MusicalSymbol[] rests = { SymbolRegistry.WHOLE_REST, SymbolRegistry.HALF_REST, SymbolRegistry.QUARTER_REST,
				SymbolRegistry.EIGHTH_REST, SymbolRegistry.SIXTEENTH_REST, SymbolRegistry.THIRTY_SECOND_REST,
				SymbolRegistry.SIXTY_FOURTH_REST };

		groupButtonsRests = new ButtonGroup();

		for (MusicalSymbol restSymbol : rests) {
			JToggleButton btn = createIconToggleButton(restSymbol.getGlyph(), iconFont);
			btn.addActionListener(e -> {
				removeOtherSelections(groupButtonsRests);
				insertMode = true;
				objectToInsert = restSymbol;
				controller.setPointer(restSymbol);
			});
			groupButtonsRests.add(btn);
			p.add(btn);
		}

		return p;
	}

	private JPanel clefToolbar() {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
		p.setBackground(new Color(230, 230, 230));

		MusicalSymbol[] clefs = { SymbolRegistry.CLEF_TREBLE, SymbolRegistry.CLEF_BASS, SymbolRegistry.CLEF_TREBLE_8 };

		groupButtonsClef = new ButtonGroup();

		for (MusicalSymbol clefSymbol : clefs) {
			JToggleButton btn = createIconImageToggle(clefSymbol.getIconPath());
			btn.addActionListener(e -> {
				removeOtherSelections(groupButtonsClef);
				insertMode = true;
				objectToInsert = clefSymbol;
				controller.setPointer(clefSymbol);
			});
			groupButtonsClef.add(btn);
			p.add(btn);
		}

		return p;
	}

	private JPanel barlineToolbar() {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
		p.setBackground(new Color(230, 230, 230));

		MusicalSymbol[] bars = { SymbolRegistry.BARLINE_SINGLE, SymbolRegistry.BARLINE_DOUBLE,
				SymbolRegistry.BARLINE_FINAL, SymbolRegistry.BARLINE_REPEAT_START, SymbolRegistry.BARLINE_REPEAT_END };

		groupButtonsBars = new ButtonGroup();

		for (MusicalSymbol s : bars) {
			JToggleButton btn = createIconImageToggle(s.getIconPath());
			btn.addActionListener(e -> {
				removeOtherSelections(groupButtonsBars);
				objectToInsert = s;
				insertMode = true;
				controller.setPointer(s);
			});
			groupButtonsBars.add(btn);
			p.add(btn);
		}

		return p;
	}

	private JPanel voiceToolbar() {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 4));
		p.setBackground(new Color(230, 230, 230));

		voice1 = new JToggleButton("Voce 1");
		voice2 = new JToggleButton("Voce 2");

		ButtonGroup g = new ButtonGroup();
		g.add(voice1);
		g.add(voice2);

		voice1.setSelected(true);

		ActionListener listener = e -> {
			controller.setCurrentVoice(voice1.isSelected() ? 1 : 2);
		};

		voice1.addActionListener(listener);
		voice2.addActionListener(listener);

		p.add(voice1);
		p.add(voice2);

		return p;
	}

	private JToggleButton createIconToggleButton(String textOrGlyph, Font font) {
		JToggleButton btn = new JToggleButton(textOrGlyph);
		btn.setFont(font);
		btn.setFocusPainted(false);
		btn.setContentAreaFilled(true);
		btn.setBackground(new Color(245, 245, 245));
		btn.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

		btn.addChangeListener(e -> {
			if (btn.isSelected()) {
				btn.setBackground(new Color(200, 220, 255));
				btn.setBorder(BorderFactory.createLineBorder(new Color(100, 140, 255), 2));
			} else {
				btn.setBackground(new Color(245, 245, 245));
				btn.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
			}
		});
		return btn;
	}

	private JToggleButton createIconImageToggle(String iconPath) {
		JToggleButton btn = new JToggleButton(new ImageIcon(getClass().getResource(iconPath)));
		btn.setFocusPainted(false);
		btn.setContentAreaFilled(true);
		btn.setBackground(new Color(245, 245, 245));
		btn.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

		btn.addChangeListener(e -> {
			if (btn.isSelected()) {
				btn.setBackground(new Color(200, 220, 255));
				btn.setBorder(BorderFactory.createLineBorder(new Color(100, 140, 255), 2));
			} else {
				btn.setBackground(new Color(245, 245, 245));
				btn.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
			}
		});
		return btn;
	}

	public void repaintPanel() {
		mainPanel.repaint();
	}

	public void resizePanel(int w, int h) {
		mainPanel.setPreferredSize(new Dimension(w, h));
		mainPanel.revalidate();

	}

	public JPanel getMainPanel() {
		return mainPanel;
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

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setFont(musicFont);
			gScore.draw(g);

			if (insertMode && mouseX > 0 && mouseY > 0) {
				Pointer pointer = controller.getPointer();
				pointer.draw(g);
				GraphicalStaff staff = getPointedStaff(mouseX, mouseY);
				if (staff == null)
					return;
				ledger.drawLedgerLines(g, pointer, staff);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// Richiede il focus al componente
			requestFocusInWindow();

			// --- Gestione click destro (popup menu) ---
			if (SwingUtilities.isRightMouseButton(e)) {
				GraphicalObject object = controller.getObjectAt(e.getX(), e.getY());

				if (object instanceof PopupLauncher) {
					PopupLauncher launcher = (PopupLauncher) object;
					JPopupMenu menu = launcher.getMenu(e.getX(), e.getY());
					menu.show(this, e.getX(), e.getY());
				}
			}

			// --- Modalità inserimento oggetto ---
			if (insertMode) {
				insertObject(e);
				return;
			}

			// Verifica se CTRL è premuto
			boolean ctrlPressed = e.isControlDown();
			// --- Modalità selezione ---
			controller.selectObjectAtPos(e.getX(), e.getY(), ctrlPressed);
			mainPanel.repaint();
		}

		/**
		 * Inserisce un oggetto usando il puntatore se esiste, altrimenti usa la
		 * posizione del mouse.
		 */
		private void insertObject(MouseEvent e) {
			int x;
			int y;

			if (controller.pointerExists()) {
				x = controller.getPointer().getX();
				y = controller.getPointer().getY();
			} else {
				x = e.getX();
				y = e.getY();
			}

			controller.insertObject(objectToInsert, x, y);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			controller.mousePressed(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			controller.mouseReleased(e);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			controller.moveObjects(e);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (gScore.hasStaves() && controller.pointerExists()) {
				mouseX = e.getX();
				mouseY = e.getY();
				// in quale Staff è il puntatore?
				GraphicalStaff s = getPointedStaff(mouseX, mouseY);
				if (s == null) {
					controller.movePointerTo(mouseX, mouseY);
				} else {
					int snapY = mouseY;
					snapY = getSnapY(s, mouseY);
					controller.movePointerTo(mouseX, snapY);
				}
				repaint();
			}
		}

		@Override
		public void componentResized(ComponentEvent e) {
			if (!gScore.hasStaves())
				return;
			int w = this.getWidth();
			int h = this.getHeight();
			for (GraphicalStaff s : gScore.getStaves()) {
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

	/**
	 * Ritorna l'indice dello staff alla posizione mouseX, mouseY oppure -1 se no vi
	 * è nessuno staff
	 * 
	 * @param mouseX
	 * @param mouseY
	 * @return
	 */
	public int getPointedStaffIndex(int mouseX, int mouseY) {
		int staffN = -1;
		List<GraphicalStaff> staffList = gScore.getStaves();
		for (int i = 0; i < staffList.size(); i++) {
			if (staffList.get(i).hitTest(mouseX, mouseY) != null) {
				staffN = i;
				break;
			}
		}
		return staffN;
	}

	public GraphicalStaff getPointedStaff(int mouseX, int mouseY) {
		for (GraphicalStaff s : gScore.getStaves()) {
			if (s.getBounds().contains(mouseX, mouseY))
				return s;
		}
		return null;
	}

	public int getSnapY(GraphicalStaff staff, int mouseY) {
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

	@Override
	public void scoreChanged(ScoreEvent e) {

		switch (e.getType()) {
		case STAFF_ADDED:
			gScore.createGraphicalStaff(0, e, getWidth());
			break;
		case OBJECT_ADDED:
			gScore.createGraphicalObject(e, pendingX, pendingY);
			break;
		case OBJECT_REMOVED:
			gScore.removeObject(e.getMusicObject());
			break;
		default:
			break;
		}
		clearPendingInsertion();
		mainPanel.repaint();
	}

	public void prepareGraphicalInsertion(int x, int y) {
		this.pendingX = x;
		this.pendingY = y;
	}

	private void clearPendingInsertion() {
		pendingX = pendingY = -1;
	}

}