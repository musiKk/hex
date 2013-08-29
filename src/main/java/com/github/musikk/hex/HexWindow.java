package com.github.musikk.hex;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class HexWindow extends JFrame {

	private final JTabbedPane tabbedPane = new JTabbedPane();
	private final Map<JComponent, File> tabFileMapping = new HashMap<>();

	{
		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				System.err.println("selected component: " + tabbedPane.getSelectedComponent());
				setTitle("hex - " + tabFileMapping.get(tabbedPane.getSelectedComponent()).getAbsolutePath());
			}
		});
	}

	public HexWindow() {
		super("hex");
		setSize(new Dimension(800, 600));

		setLayout(new BorderLayout());

		add(tabbedPane, BorderLayout.CENTER);

		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('f');

		fileMenu.add(new JMenuItem(new FileOpenAction()));
		fileMenu.add(new JMenuItem(new CloseTabAction()));

		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem(new QuitAction()));

		InputMap rootInputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap rootActionMap = getRootPane().getActionMap();

		rootInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, Event.CTRL_MASK), CloseTabAction.COMMAND_KEY);
		rootActionMap.put(CloseTabAction.COMMAND_KEY, new CloseTabAction());

		rootInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, Event.CTRL_MASK), "CYCLE_RIGHT");
		rootActionMap.put("CYCLE_RIGHT", new CycleTabAction(true));

		rootInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, Event.CTRL_MASK), "CYCLE_LEFT");
		rootActionMap.put("CYCLE_LEFT", new CycleTabAction(false));

		menuBar.add(fileMenu);

		add(menuBar, BorderLayout.NORTH);
	}

	private void addNewTab(File file) {
		try {
			ScrollableHexPanel hexPanel = new ScrollableHexPanel(new FileDataProvider(file));
			tabFileMapping.put(hexPanel, file);
			tabbedPane.addTab(file.getName(), hexPanel);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
	}

	private void closeTab(int index) {
		tabFileMapping.remove(tabbedPane.getSelectedComponent());
		tabbedPane.removeTabAt(index);
	}

	private class FileOpenAction extends AbstractAction {
		public FileOpenAction() {
			putValue(NAME, "Open");
			putValue(MNEMONIC_KEY, KeyEvent.VK_O);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

			int rc = fileChooser.showOpenDialog(HexWindow.this);
			switch (rc) {
			case JFileChooser.CANCEL_OPTION:
				return;
			case JFileChooser.APPROVE_OPTION:
				addNewTab(fileChooser.getSelectedFile());
				break;
			}
		}
	}

	private class CloseTabAction extends AbstractAction {
		public static final String COMMAND_KEY = "CLOSE_TAB";

		public CloseTabAction() {
			putValue(NAME, "Close Tab");
			putValue(MNEMONIC_KEY, KeyEvent.VK_C);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_W, Event.CTRL_MASK));
		}
		@Override
		public boolean isEnabled() {
			return tabbedPane.getTabCount() > 0;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			int selectedIndex = tabbedPane.getSelectedIndex();
			if (selectedIndex != -1) {
				closeTab(selectedIndex);
			}
		}
	}

	private class QuitAction extends AbstractAction {
		public QuitAction() {
			putValue(NAME, "Quit");
			putValue(MNEMONIC_KEY, KeyEvent.VK_Q);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK));
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			HexWindow.this.dispose();
		}
	}

	private class CycleTabAction extends AbstractAction {
		private final boolean cycleRight;
		public CycleTabAction(boolean cycleRight) {
			this.cycleRight = cycleRight;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int newIndex = tabbedPane.getSelectedIndex() + (cycleRight ? 1 : -1);
			tabbedPane.setSelectedIndex((newIndex + tabbedPane.getTabCount() % tabbedPane.getTabCount()) - 1);
		}
	}

	public enum CycleDirection {
		LEFT, RIGHT
	}

	public static void main(String[] args) throws Exception {
		JFrame f = new HexWindow();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(new Dimension(1024, 768));
		f.setVisible(true);
	}

}

