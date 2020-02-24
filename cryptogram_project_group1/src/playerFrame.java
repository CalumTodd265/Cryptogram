import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.InvalidPropertiesFormatException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * This is currently the first frame the user sees This frame is used to select
 * the type of cryptogram they want to play
 *
 */
public class playerFrame {
	private JFrame frame;
	private JButton letterCrypto, numberCrypto, quit;
	public GameFunctionality gameFunc;

	/**
	 * The standard constructor for FirstFrame makes a call to makeFrame()
	 */
	public playerFrame() {
		gameFunc = new GameFunctionality();
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

		letterCrypto = new JButton("Log In");
		letterCrypto.addActionListener(e -> {
			try {
				logIn(frame);
			} catch (FileNotFoundException e1) {
				JOptionPane.showMessageDialog(frame, "No player found with that name");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		toolbar.add(letterCrypto);

		numberCrypto = new JButton("Make Player");
		numberCrypto.addActionListener(e -> {
			try {
				addPlayer(frame);
			} catch (FileAlreadyExistsException e1) {
				JOptionPane.showMessageDialog(frame,
						"There is already an account with that name. Please enter another name");
			} catch (IOException exc) {
				exc.printStackTrace();
			}
		});
		toolbar.add(numberCrypto);

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
	 * @throws IOException 
	 */
	public void logIn(JFrame frame) throws IOException {
		String name;
		name = JOptionPane.showInputDialog(frame, "Please enter your name");
		try {
			gameFunc.loadPlayer(name);
		} catch (InvalidPropertiesFormatException e){
			JOptionPane.showMessageDialog(frame, "Player file is corrupted. Resetting stats to 0.");
		}
		frame.setVisible(false);
		typeFrame game = new typeFrame(gameFunc);
	}

	/**
	 * If the user selects to play an integer game, this sets up the GameFrame
	 * in the correct manner
	 *
	 * @param frame:
	 *            the FirstFrame that the user sees
	 * @throws IOException
	 * @throws FileAlreadyExistsException
	 */
	public void addPlayer(JFrame frame) throws FileAlreadyExistsException, IOException {
		String name;
		name = JOptionPane.showInputDialog(frame, "Please enter your name");
		if (name.length() > 30 || name.length() < 1) {
			JOptionPane.showMessageDialog(frame, "Name has a max. of 30 characters and a min. of 1 character." , "Error making account", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!name.matches("[a-zA-Z0-9]+")) {
			JOptionPane.showMessageDialog(frame, "Name can only contain alphanumeric characters" , "Error making account", JOptionPane.ERROR_MESSAGE);
			return;
		}
		gameFunc.createNewPlayer(name);
		JOptionPane.showMessageDialog(frame, "Account created. You can now log in with your account");
	}

	/**
	 * when user clicks quit a box will pop up asking if they really want toquit
	 * if user selects yes the system quits if user selects no the system stays
	 * open
	 */
	private void quit() {
		int quit = JOptionPane.showConfirmDialog(frame, "Are you sure you want to quit?.", "Quit.",
				JOptionPane.YES_NO_OPTION);
		if (quit == JOptionPane.YES_OPTION) {
			if (gameFunc.currentPlayer != null) {
				try {
					gameFunc.currentPlayer.saveDetails();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.exit(0);
		}
	}
}
