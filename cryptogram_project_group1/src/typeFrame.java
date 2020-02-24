import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * This is currently the first frame the user sees This frame is used to select
 * the type of cryptogram they want to play
 *
 */
public class typeFrame {
	private JFrame frame;
	private JButton letterCrypto, numberCrypto, quit, back;
	public GameFunctionality gf;

	/**
	 * The standard constructor for FirstFrame makes a call to makeFrame()
	 */
	public typeFrame(GameFunctionality gameFunc) {
		gf = gameFunc;
		makeFrame();
	}

	/**
	 * Adds buttons to the contentPane of the frame, sets up the frame
	 * dimensions, and gives the buttons action listeners.
	 */
	protected void makeFrame() {
		frame = new JFrame("Cryptogram");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				quit();
			}
		});
		JPanel contentPane = (JPanel) frame.getContentPane();
		contentPane.setBorder(new EmptyBorder(12, 12, 12, 12));

		JPanel toolbar = new JPanel();

		letterCrypto = new JButton("Letter Crypto");
		letterCrypto.addActionListener(e -> {
			try {
				letterGame(frame);
			} catch (FileNotFoundException e1) {
				JOptionPane.showMessageDialog(frame, "File not found", "File Error", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
		});
		toolbar.add(letterCrypto);

		numberCrypto = new JButton("Number Crypto");
		numberCrypto.addActionListener(e -> {
			try {
				numberGame(frame);
			} catch (FileNotFoundException exc) {
				JOptionPane.showMessageDialog(frame, "File not found", "File Error", JOptionPane.ERROR_MESSAGE);
				System.exit(1);

			}
		});
		toolbar.add(numberCrypto);

		back = new JButton("Back");
		back.addActionListener(e -> back());
		toolbar.add(back);

		quit = new JButton("Quit");
		quit.addActionListener(e -> quit());
		toolbar.add(quit);

		JPanel flow = new JPanel();
		flow.add(toolbar);

		frame.getContentPane().add(flow);
		frame.pack();
		frame.setSize(1000, 400);

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(d.width / 2 - frame.getWidth() / 2, d.height / 2 - frame.getHeight() / 2);
		frame.setVisible(true);
	}

	/**
	 * If the user selects to play a letter game, this sets up the GameFrame in
	 * the correct manner
	 *
	 * @param frame:
	 *            the FirstFrame that the user sees
	 * @throws FileNotFoundException
	 */
	public void letterGame(JFrame frame) throws FileNotFoundException {
		frame.setVisible(false);
		GameFrame game = new GameFrame(0, gf);
	}

	/**
	 * If the user selects to play an integer game, this sets up the GameFrame
	 * in the correct manner
	 *
	 * @param frame:
	 *            the FirstFrame that the user sees
	 * @throws FileNotFoundException
	 */
	public void numberGame(JFrame frame) throws FileNotFoundException {
		frame.setVisible(false);
		GameFrame game = new GameFrame(1, gf);

	}

	/**
	 * when user clicks quit a box will pop up asking if they really want to quit
	 * if user selects yes the system quits if user selects no the system stays
	 * open
	 */
	private void quit() {
		int quit = JOptionPane.showConfirmDialog(frame, "Are you sure you want to quit?.", "Quit.",
				JOptionPane.YES_NO_OPTION);
		if (quit == JOptionPane.YES_OPTION) {
			if (gf.currentPlayer != null) {
				try {
					gf.currentPlayer.saveDetails();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
		}
	}

	/***
	 * Allows the user to go back to the previous screen when clicked
	 */
	private void back() {
		frame.setVisible(false);
		playerFrame first = new playerFrame();
	}
}
