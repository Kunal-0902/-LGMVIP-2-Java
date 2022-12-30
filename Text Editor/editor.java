import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.text.*;
import java.text.*;
import java.util.*;

public class editor extends JFrame {
	JTextArea area = new JTextArea(0, 0);
	JScrollPane scroller = new JScrollPane(area);

	JMenu menu = new JMenu("Edit");
	JMenuItem menuItem = new JMenuItem("copy");
	JMenuItem menucut = new JMenuItem("cut");
	JMenuItem menuclear = new JMenuItem("clear");
	JMenuItem menupaste = new JMenuItem("paste");

	JMenuBar menuBar = new JMenuBar();

	JMenu FILE = new JMenu("File");
	JMenu EDIT = new JMenu("Edit");
	JMenu FORMAT = new JMenu("Format");
	JMenu VIEW = new JMenu("View");
	JMenu HELP = new JMenu("Help");

	JMenuItem NEWFILE = new JMenuItem("New");
	JMenuItem OPENFILE = new JMenuItem("Open");
	JMenuItem SAVEFILE = new JMenuItem("Save");
	JMenuItem SAVEASFILE = new JMenuItem("Save As...");
	JMenuItem PRINTFILE = new JMenuItem("Print...");
	JMenuItem EXITFILE = new JMenuItem("Exit");

	JMenuItem COPYEDIT = new JMenuItem("copy");
	JMenuItem CUTEDIT = new JMenuItem("cut");
	JMenuItem PASTEDIT = new JMenuItem("paste");
	JMenuItem TIMEDIT = new JMenuItem("Time/Date");

	JMenuItem FONT = new JMenuItem("Font");

	JMenuItem ABOUT = new JMenuItem("About");

	String file = null;
	String fileN;

	boolean opened = false;

	JLabel statusLabel;

	JPanel aboutPanel = new JPanel();

	int ind = 0;

	StringBuffer sbufer;
	String findString;

	fontSelector fontS = new fontSelector();

	public editor() {
		super("Text Editor");
		this.setSize(800, 600);
		this.getContentPane().setLayout(new BorderLayout());
		area.setLineWrap(true);
		area.requestFocus(true);
		this.getContentPane().add(scroller, BorderLayout.CENTER);

		area.setDragEnabled(true);

		// SETS THE MENUBAR
		FILE.add(OPENFILE);
		FILE.add(SAVEFILE);
		FILE.addSeparator();
		FILE.add(PRINTFILE);
		FILE.addSeparator();
		FILE.add(EXITFILE);

		EDIT.add(CUTEDIT);
		EDIT.add(COPYEDIT);
		EDIT.add(PASTEDIT);
		EDIT.addSeparator();
		EDIT.add(TIMEDIT);

		FORMAT.add(FONT);

		HELP.add(ABOUT);

		menuBar.add(FILE);
		menuBar.add(EDIT);
		menuBar.add(FORMAT);
		menuBar.add(HELP);

		this.setJMenuBar(menuBar);

		// ACTIONLISTENER FOR ITEMS IN THE MENUBAR
		// OPEN A NEW FILE
		NEWFILE.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		NEWFILE.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				opened = false;
				int confirm = JOptionPane.showConfirmDialog(null,
						"Would you like to save?",
						"New File",
						JOptionPane.YES_NO_CANCEL_OPTION);

				if (confirm == JOptionPane.YES_OPTION) {
					saveFile();
					area.setText(null);

				} else if (confirm == JOptionPane.CANCEL_OPTION) {
				} else {
					area.setText(null);

				}
			}
		});

		// SAVE OPTION. HAS A VALIDATION CHECK THAT CHECKS WETHER ITS AN OPENED FILE OR
		// NEW FILE
		SAVEFILE.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		SAVEFILE.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveFile();
			}
		});

		// OPTION THAT WILL BRING UP A DIALOG WHICH SAVES THE FILE WITH A NAME AND
		// FORMAT DESIRED
		SAVEASFILE.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				opened = false;
				saveFile();
			}
		});

		// PRINTS WHATEVER IS IN THE TEXT AREA
		PRINTFILE.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		PRINTFILE.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (area.getText().equals(""))
					JOptionPane.showMessageDialog(null, "Text Area is empty.");
				else
					print(createBuffer());

			}
		});

		// ACTION FOR OPEN BUTTON
		OPENFILE.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		OPENFILE.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFile();
			}
		});

		// ACTION FOR CUT BUTTON
		CUTEDIT.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		CUTEDIT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				area.cut();
			}
		});

		// ACTION FOR COPY BUTTON
		COPYEDIT.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		COPYEDIT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				area.copy();
			}
		});

		// ACTION FOR PASTE BUTTON
		PASTEDIT.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		PASTEDIT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				area.paste();
			}
		});

		// FONT SELECTOR OPTION
		FONT.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		FONT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fontS.setVisible(true);
				fontS.okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						Font selectedFont = fontS.returnFont();
						area.setFont(selectedFont);
						fontS.setVisible(false);
					}
				});

				fontS.cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						fontS.setVisible(false);
					}
				});
			}
		});

		// PRINTS THE SYSTEM DATE AND TIME IN THE EDITOR
		TIMEDIT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Date currentDate;
				SimpleDateFormat formatter;
				String dd;
				formatter = new SimpleDateFormat("KK:mm aa MMMMMMMMM dd yyyy", Locale.getDefault());
				currentDate = new java.util.Date();
				dd = formatter.format(currentDate);
				area.insert(dd, area.getCaretPosition());
			}
		});

		// EXITS THE APPLICATION AND CHECKS FOR ANY CHANGES MADE
		EXITFILE.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		EXITFILE.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int confirm = JOptionPane.showConfirmDialog(null,
						"Would you like to save?",
						"Exit Application",
						JOptionPane.YES_NO_CANCEL_OPTION);

				if (confirm == JOptionPane.YES_OPTION) {
					saveFile();
					dispose();
					System.exit(0);
				} else if (confirm == JOptionPane.CANCEL_OPTION) {
				} else {
					dispose();
					System.exit(0);
				}
			}
		});

		// CLOSES THE WINDOW WHEN THE CLOSE BUTTON IS PRESSED
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int confirm = JOptionPane.showConfirmDialog(null,
						"Would you like to save?",
						"Exit Application",
						JOptionPane.YES_NO_CANCEL_OPTION);

				if (confirm == JOptionPane.YES_OPTION) {
					saveFile();
					dispose();
					System.exit(0);
				} else if (confirm == JOptionPane.CANCEL_OPTION) {

				} else {
					dispose();
					System.exit(0);
				}
			}
		});

	}

	// MAIN FUNCTION.
	public static void main(String args[]) {
		editor l = new editor();
		l.setVisible(true);
	}

	// FUNCTION CALLED BY THE SAVE BUTTON
	public void saveFile() {
		String line = area.getText();
		if (opened == true) {
			try {
				FileWriter output = new FileWriter(file);
				BufferedWriter bufout = new BufferedWriter(output);
				bufout.write(line, 0, line.length());
				JOptionPane.showMessageDialog(null, "Save Successful");
				bufout.close();
				output.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} else {
			JFileChooser fc = new JFileChooser();
			int result = fc.showSaveDialog(new JPanel());

			if (result == JFileChooser.APPROVE_OPTION) {
				fileN = String.valueOf(fc.getSelectedFile());

				try {
					FileWriter output = new FileWriter(fileN);
					BufferedWriter bufout = new BufferedWriter(output);
					bufout.write(line, 0, line.length());
					JOptionPane.showMessageDialog(null, "Save Successful");
					bufout.close();
					output.close();
					opened = true;
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}

	// PRINT FUNCTION
	public String createBuffer() {
		String buffer;
		buffer = area.getText();
		return buffer;
	}

	private void print(String s) {
		StringReader sr = new StringReader(s);
		LineNumberReader lnr = new LineNumberReader(sr);
		Font typeface = new Font("Monospaced", Font.PLAIN, 12);
		Properties p = new Properties();
		PrintJob pjob = getToolkit().getPrintJob(this, "Print report", p);

		if (pjob != null) {
			Graphics pg = pjob.getGraphics();
			if (pg != null) {
				FontMetrics fm = pg.getFontMetrics(typeface);
				int margin = 20;
				int pageHeight = pjob.getPageDimension().height - margin;
				int fontHeight = fm.getHeight();
				int fontDescent = fm.getDescent();
				int curHeight = margin;

				String nextLine;
				pg.setFont(area.getFont());

				try {
					do {
						nextLine = lnr.readLine();
						if (nextLine != null) {
							if ((curHeight + fontHeight) > pageHeight) { // New Page
								pg.dispose();
								pg = pjob.getGraphics();
								curHeight = margin;
							}

							curHeight += fontHeight;

							if (pg != null) {
								pg.setFont(typeface);
								pg.drawString(nextLine, margin, curHeight - fontDescent);
							}
						}
					} while (nextLine != null);

				} catch (EOFException eof) {
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			pg.dispose();
		}
		if (pjob != null)
			pjob.end();
	}

	// FUNCTION TO OPEN THE FILE
	public void openFile() {
		area.setText(null);
		JFileChooser fc = new JFileChooser();
		int result = fc.showOpenDialog(new JPanel());

		if (result == JFileChooser.APPROVE_OPTION) {
			String file = String.valueOf(fc.getSelectedFile());
			// String dirn = fc.getDirectory();

			File fil = new File(file);

			// START THIS THREAD WHILE READING FILE
			Thread loader = new FileLoader(fil, area.getDocument());
			loader.start();
		} else {
		}
	}

	// Thread to load a file into the text storage model

	class FileLoader extends Thread {

		JLabel state;

		FileLoader(File f, Document doc) {
			setPriority(4);
			this.f = f;
			this.doc = doc;
		}

		public void run() {
			try {
				// initialize the statusbar

				JProgressBar progress = new JProgressBar();
				progress.setMinimum(0);
				progress.setMaximum((int) f.length());

				// try to start reading
				Reader in = new FileReader(f);
				char[] buff = new char[4096];
				int nch;
				while ((nch = in.read(buff, 0, buff.length)) != -1) {
					doc.insertString(doc.getLength(), new String(buff, 0, nch), null);
					progress.setValue(progress.getValue() + nch);
				}

			} catch (IOException e) {
				System.err.println(e.toString());
			} catch (BadLocationException e) {
				System.err.println(e.getMessage());
			}

		}

		Document doc;
		File f;
	}

}