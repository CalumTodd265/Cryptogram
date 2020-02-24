import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class GameFrame {
	private JFrame frame;
	private JButton getHint, showSolution, checkSolution, newGame, undoButton, saveCrypto, loadCrypto, showStats,
			showScoreboard, showFreqs, quit, back;
	protected String phrase;
	protected int no_of_guesses;
	protected int line3position;
	protected int line2position;
	protected GameFunctionality gameFunc;
	protected JTextField[] texts;
	protected JLabel[] labels;
	protected int phraseLength;

	/**
	 * The standard constructor for GameFrame. Adds buttons to the contentPane
	 * of the frame, sets up the frame dimensions, sets GridBagLayout
	 * constraints and gives the buttons action listeners
	 *
	 * @param gameType:
	 *            Keeps track of what kind of cryptogram the user is playing
	 *            (e.g. char or integer)
	 */
	public GameFrame(int gameType, GameFunctionality gf) throws FileNotFoundException {
		frame = new JFrame("Cryptogram");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				quit();
			}
		});
		JPanel contentPane = (JPanel) frame.getContentPane();
		contentPane.setBorder(new EmptyBorder(12, 12, 12, 12));

		JPanel toolbar = new JPanel();
		toolbar.setLayout(new GridLayout(0, 1));

		JPanel crypto = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		crypto.setLayout(layout);
		crypto.setSize(1200, 500);

		frame.getContentPane().add(crypto);

		gameFunc = gf;

		newGame = new JButton("Generate Cryptogram");
		newGame.addActionListener(e -> {
			try {
				newGame(crypto, c, gameType);
			} catch (FileNotFoundException exc) {
				JOptionPane.showMessageDialog(frame, "File could not be found.", "File Error",
						JOptionPane.ERROR_MESSAGE);
			}
		});
		toolbar.add(newGame);

		undoButton = new JButton("Undo");
		toolbar.add(undoButton);
		undoButton.addActionListener(e -> undo(crypto, c, gameType));
		undoButton.setEnabled(false);

		showFreqs = new JButton("Show Frequencies");
		toolbar.add(showFreqs);
		showFreqs.addActionListener(e -> showFrequencies());
		showFreqs.setEnabled(false);

		getHint = new JButton("Get Hint");
		toolbar.add(getHint);
		getHint.addActionListener(e -> getHint(crypto, c, gameType));
		getHint.setEnabled(false);

		checkSolution = new JButton("Check Solution");
		checkSolution.addActionListener(e -> checkSolution(crypto, c, gameType, 0));
		checkSolution.setEnabled(false);
		toolbar.add(checkSolution);

		showSolution = new JButton("Show Solution");
		showSolution.addActionListener(e -> showSolution(crypto, c, gameType));
		showSolution.setEnabled(false);
		toolbar.add(showSolution);

		loadCrypto = new JButton("Load Cryptogram");
		loadCrypto.addActionListener(e -> loadCrypto(crypto, c, gameType));
		toolbar.add(loadCrypto);

		saveCrypto = new JButton("Save Cryptogram");
		saveCrypto.addActionListener(e -> saveCrypto());
		toolbar.add(saveCrypto);

		showStats = new JButton("Show Statistics");
		showStats.addActionListener(e -> showStats());
		toolbar.add(showStats);
		
		showScoreboard = new JButton("Show Scoreboard");
		showScoreboard.addActionListener(e -> showScoreboard());
		toolbar.add(showScoreboard);

		quit = new JButton("Quit");
		quit.addActionListener(e -> quit());
		toolbar.add(quit);

		back = new JButton("Back");
		back.addActionListener(e -> back());
		toolbar.add(back);

		JLabel playerName = new JLabel("Welcome " + gf.currentPlayer.getPlayerName());
		toolbar.add(playerName);

		JPanel flow = new JPanel();
		flow.add(toolbar);

		frame.getContentPane().add(flow, BorderLayout.WEST);
		frame.pack();
		frame.setSize(1200, 600);

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(d.width / 2 - frame.getWidth() / 2, d.height / 2 - frame.getHeight() / 2);
		frame.setVisible(true);
	}

	/**
	 * When user clicks quit a box will pop up asking if they really want to
	 * quit if user selects yes the system quits if user selects no the system
	 * stays open
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

	/**
	 * The action listener for the newGame button. Calls the correct
	 * GameFunctionality method depending on what type of cryptogram the user
	 * has chosen to play
	 *
	 * @param crypto:
	 *            The JPanel which the game contents are in
	 * @param c:
	 *            the previously set GridBagConstraints
	 * @param gameType:
	 *            Keeps track of what kind of cryptogram the user is playing
	 *            (e.g. char or integer)
	 * @throws FileNotFoundException
	 */
	private void newGame(JPanel crypto, GridBagConstraints c, int gameType) throws FileNotFoundException {
		undoButton.setEnabled(false);
		no_of_guesses = 0;
		gameFunc.cryptogram = null;

		if (gameType == 0) {
			gameFunc.newLetterGame();
		} else {
			gameFunc.newIntegerGame();
		}
		displayCrypto(crypto, c, gameType);
		getHint.setEnabled(true);
		showSolution.setEnabled(true);
		showFreqs.setEnabled(true);

	}

	/**
	 * Updates the GUI according to the user's guess choices
	 *
	 * @param phraseChar:
	 *            the encrypted version of the letter the user is making a guess
	 *            for
	 * @param guess:
	 *            the guess they have inputted into a JTextField
	 * @param crypto:
	 *            The JPanel which the game contents are in
	 * @param c:
	 *            the previously set GridBagConstraints
	 * @param gameType:
	 *            Keeps track of what kind of cryptogram the user is playing
	 *            (e.g. char or integer)
	 * @throws StringIndexOutOfBoundsException
	 */
	private void makeGuess(Object phraseChar, char guess, JPanel crypto, GridBagConstraints c, int gameType)
			throws StringIndexOutOfBoundsException {

		if (Character.isDigit(guess) || !Character.isLetter(guess)) {
			JOptionPane.showMessageDialog(frame, "You can only guess letters in the English alphabet.", "Guess Error",
					JOptionPane.ERROR_MESSAGE);
			Character ch = (Character) gameFunc.cryptogram.guessMap.get(phraseChar);
			resetChar(phraseChar, crypto, c);
			return;
		}

		guess = Character.toLowerCase(guess);
		int result = gameFunc.makeGuess(phraseChar, guess, false);

		if (result == 1) {
			displayGuess(crypto, c, phraseChar, guess);
			checkSolution(crypto, c, gameType, result);
			return;
		} else if (result == 2) {
			displayGuess(crypto, c, phraseChar, guess);
			checkSolution(crypto, c, gameType, result);
			return;
		} else if (result == 0) {
			displayGuess(crypto, c, phraseChar, guess);
			return;
		} else if (result == -3) {
			JOptionPane.showMessageDialog(frame,
					"You have guessed this letter for another character. Please undo the previous"
							+ "character and try again.",
					"Guess Error", JOptionPane.ERROR_MESSAGE);
			resetChar(phraseChar, crypto, c);
			return;
		} else if (result == -2) {
			int overwrite = JOptionPane.showConfirmDialog(frame,
					"You have already made a guess for this letter. Do you want " + "to overwrite your guess?",
					"Overwrite", JOptionPane.YES_NO_OPTION);
			if (overwrite == JOptionPane.YES_OPTION) {
				gameFunc.undo(phraseChar);
				result = gameFunc.makeGuess(phraseChar, guess, true);
				displayGuess(crypto, c, phraseChar, guess);
				if (result == 1 || result == 2) {
					checkSolution(crypto, c, gameType, result);
				}
				return;
			} else if (overwrite == JOptionPane.NO_OPTION) {
				resetChar(phraseChar, crypto, c);
				return;
			}
		}
	}

	/**
	 * Updates the GUI according to the user's undo choice
	 *
	 * @param crypto:
	 *            The JPanel which the game contents are in
	 * @param c:
	 *            the previously set GridBagConstraints
	 * @param gameType:
	 *            Keeps track of what kind of cryptogram the user is playing
	 *            (e.g. char or integer)
	 */
	private void undo(JPanel crypto, GridBagConstraints c, int gameType) {
		String undo;
		undo = JOptionPane.showInputDialog(frame, "Please enter the character you want to remove your guess for");
		Object undoChar;
		
		if (Character.isDigit(undo.charAt(0)) || !Character.isLetter(undo.charAt(0))) {
			JOptionPane.showMessageDialog(frame, "You can only undo guesses for letters in the cryptogram", "Undo Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (gameType == 0) {
			try {
				undoChar = undo.charAt(0);
			} catch (NullPointerException exc) {
				JOptionPane.showMessageDialog(frame, "Please enter a letter");
				return;
			} catch (StringIndexOutOfBoundsException exc) {
				JOptionPane.showMessageDialog(frame, "Please enter a letter");
				return;
			}
		} else {
			undoChar = Integer.valueOf(undo);
		}
		if (gameFunc.undo(undoChar) == 1) {
			gameFunc.undo(undoChar);
			for (int i = 0; i < phraseLength; i++) {
				if (gameFunc.cryptogram.originalToCryptoMap.get(phrase.charAt(i)) == undoChar) {
					if (i > phrase.lastIndexOf(' ', 60) && phraseLength > 60) {
						textOver60(c, " ", crypto, i);
					} else if (i > phrase.lastIndexOf(' ', 30)) {
						textOver30(c, " ", crypto, i);
					} else {
						text(c, " ", crypto, i);
					}
				}
			}
			JOptionPane.showMessageDialog(frame, "Guess successfully removed");
			no_of_guesses--;
			if (no_of_guesses < 1) {
				undoButton.setEnabled(false);
			}
		} else if (gameFunc.undo(undo) == 0) {
			JOptionPane.showMessageDialog(frame, "That character is not in the cryptogram");
			return;
		} else {
			JOptionPane.showMessageDialog(frame, "You have not yet made a guess for that character");
			return;
		}

	}

	/**
	 * If the user has entered a valid guess, this overwrites the TextField on
	 * the GUI with that guess
	 *
	 * @param phraseChar:
	 *            the encrypted version of the letter the user is making a guess
	 *            for
	 * @param guess:
	 *            the guess they have inputted into a JTextField
	 * @param crypto:
	 *            The JPanel which the game contents are in
	 * @param c:
	 *            the previously set GridBagConstraints
	 */
	private void displayGuess(JPanel crypto, GridBagConstraints c, Object phraseChar, char guess) {
		for (int i = 0; i < phraseLength; i++) {
			if (gameFunc.cryptogram.originalToCryptoMap.get(phrase.charAt(i)) == phraseChar) {
				String input = Character.toString(guess);
				if (i > phrase.lastIndexOf(' ', 60) && phraseLength > 60) {
					textOver60(c, input, crypto, i);
				} else if (i > phrase.lastIndexOf(' ', 30)) {
					textOver30(c, input, crypto, i);

				} else {
					text(c, input, crypto, i);
				}
			}
		}
		JOptionPane.showMessageDialog(frame, "Guess Success");
		no_of_guesses++;
		undoButton.setEnabled(true);
	}

	/**
	 * When the user hits back, they are taken back to the frame with the choice
	 * of a number or letter cryptogram.
	 */
	private void back() {
		frame.setVisible(false);
		try {
			gameFunc.currentPlayer.saveDetails();
		} catch (IOException e) {
			e.printStackTrace();
		}
		typeFrame first = new typeFrame(gameFunc);
	}

	/**
	 * the action listener for checkSolution, when the user hits the button,
	 * they are given an appropriate message on their current completion of the
	 * cryptogram
	 *
	 * @param crypto:
	 *            The JPanel which the game contents are in
	 * @param c:
	 *            the previously set GridBagConstraints
	 * @param gameType:
	 *            Keeps track of what kind of cryptogram the user is playing
	 *            (e.g. char or integer)
	 */
	private void checkSolution(JPanel crypto, GridBagConstraints c, int gameType, int completionStatus) {
		if (completionStatus == 0) {
			completionStatus = gameFunc.checkComplete();
		}
		if (completionStatus == 0) {
			JOptionPane.showMessageDialog(frame, "You have not completed the cryptogram");
			return;
		} else if (completionStatus == 1) {
			JOptionPane.showMessageDialog(frame, "Your answer is not correct");
			return;
		} else if (completionStatus == 2) {
			JOptionPane.showMessageDialog(frame, "Well done! You have correctly completed the cryptogram");
			try {
				newGame(crypto, c, gameType);
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(frame, "File not found", "File Error", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
			return;
		}
	}

	/**
	 * textOver60 sets the value of a JTextField when it is on the 3rd line
	 *
	 * @param c:
	 *            the previously set GridBagConstraints
	 * @param input:
	 *            the String that is to be put in the JTextField
	 * @param crypto:
	 *            The JPanel which the game contents are in
	 * @param i:
	 *            the position of the textfield on the grid
	 */
	private void textOver60(GridBagConstraints c, String input, JPanel crypto, int i) {
		c.gridx = i;
		c.gridy = 5;
		texts[i].setText(input);
		crypto.add(texts[i], c);
	}

	/**
	 * textOver30 sets the value of a JTextField when it is on the 2nd line
	 *
	 * @param c:
	 *            the previously set GridBagConstraints
	 * @param input:
	 *            the String that is to be put in the JTextField
	 * @param crypto:
	 *            The JPanel which the game contents are in
	 * @param i:
	 *            the position of the textfield on the grid
	 */
	private void textOver30(GridBagConstraints c, String input, JPanel crypto, int i) {
		c.gridx = i;
		c.gridy = 3;
		texts[i].setText(input);
		crypto.add(texts[i], c);
	}

	/**
	 * text sets the value of a JTextField when it is on the 1st line
	 *
	 * @param c:
	 *            the previously set GridBagConstraints
	 * @param input:
	 *            the String that is to be put in the JTextField
	 * @param crypto:
	 *            The JPanel which the game contents are in
	 * @param i:
	 *            the position of the textfield on the grid
	 */
	private void text(GridBagConstraints c, String input, JPanel crypto, int i) {
		c.gridx = i;
		c.gridy = 1;
		texts[i].setText(input);
		crypto.add(texts[i], c);
	}

	/**
	 * If the user enters an invalid guess, or doesn't want to overwrite their
	 * guess this function sets the JTextField back to it's previous String
	 *
	 * @param phraseChar:
	 *            the encrypted version of the letter the user is making a guess
	 *            for
	 * @param crypto:
	 *            The JPanel which the game contents are in
	 * @param c:
	 *            the previously set GridBagConstraints
	 */
	private void resetChar(Object phraseChar, JPanel crypto, GridBagConstraints c) {
		for (int i = 0; i < phraseLength; i++) {
			line2position = 0;
			line3position = 0;
			if (gameFunc.cryptogram.guessMap.containsKey(phraseChar)
					&& gameFunc.cryptogram.originalToCryptoMap.get(phrase.charAt(i)) == phraseChar) {
				Character ch = (Character) gameFunc.cryptogram.guessMap.get(phraseChar);
				String input = Character.toString(ch);
				if (i > phrase.lastIndexOf(' ', 60) && phraseLength > 60) {

					textOver60(c, input, crypto, i);
					line3position++;

				} else if (i > phrase.lastIndexOf(' ', 30)) {

					textOver30(c, input, crypto, i);
					line2position++;
				} else {
					text(c, input, crypto, i);
				}
			} else if (!gameFunc.cryptogram.guessMap.containsKey(phraseChar)
					&& gameFunc.cryptogram.originalToCryptoMap.get(phrase.charAt(i)) == phraseChar) {
				String input = " ";
				if (i > phrase.lastIndexOf(' ', 60) && phraseLength > 60) {
					textOver60(c, input, crypto, i);
					line3position++;
				} else if (i > phrase.lastIndexOf(' ', 30)) {
					textOver30(c, input, crypto, i);
					line2position++;
				} else {
					text(c, input, crypto, i);
				}
			}
		}
	}

	/**
	 * Method used to display the current cryptogram, both the encrypted text
	 * and put the user's guesses in the textboxes
	 *
	 * @param crypto:
	 *            The JPanel which the game contents are in
	 * @param c:
	 *            the previously set GridBagConstraints
	 * @param gameType:
	 *            Keeps track of what kind of cryptogram the user is playing
	 *            (e.g. char or integer)
	 */

	private void displayCrypto(JPanel crypto, GridBagConstraints c, int gameType) {
		checkSolution.setEnabled(true);
		showSolution.setEnabled(true);
		crypto.removeAll();
		phrase = gameFunc.cryptogram.getPhrase();
		phraseLength = phrase.length();
		phrase = phrase.toLowerCase();
		texts = new JTextField[phraseLength];
		labels = new JLabel[phraseLength];
		line2position = 0;
		line3position = 0;

		for (int j = 0; j < phraseLength; j++) {
			c.insets = new Insets(4, 4, 4, 4);
			if (phrase.charAt(j) != ' ' && phrase.charAt(j) != '.' && phrase.charAt(j) != '?'
					&& phrase.charAt(j) != ',') {
				JTextField text = new JTextField(1);
				texts[j] = text;
				JLabel label = new JLabel("" + gameFunc.cryptogram.originalToCryptoMap.get(phrase.charAt(j)));
				labels[j] = label;

				Object phraseChar = gameFunc.cryptogram.originalToCryptoMap.get(phrase.charAt(j));
				String input = null;
				if (gameFunc.cryptogram.guessMap.containsKey(phraseChar)) {
					Character ch = (Character) gameFunc.cryptogram.guessMap.get(phraseChar);
					input = Character.toString(ch);
				}
				text.setText(input);
				text.addKeyListener(new KeyAdapter() {
					public void keyTyped(KeyEvent e) {
						if (text.getText().length() >= 1) // limit textfield to
															// 1 character
							e.consume();
					}
				});
				text.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							makeGuess(phraseChar, text.getText().charAt(0), crypto, c, gameType);
						} catch (StringIndexOutOfBoundsException exc) {
							resetChar(phraseChar, crypto, c);
						}
						return;
					}
				});
				text.setBounds(3, 3, 3, 3);
				if (j > phrase.lastIndexOf(' ', 60) && phraseLength > 60) {
					c.gridx = line3position;
					c.gridy = 4;
					label.setLabelFor(text);
					crypto.add(label, c);
					c.gridy = 5;
					crypto.add(text, c);
					line3position++;
				} else if (j > phrase.lastIndexOf(' ', 30)) {
					c.gridx = line2position;
					c.gridy = 2;
					label.setLabelFor(text);
					crypto.add(label, c);
					c.gridy = 3;
					crypto.add(text, c);
					line2position++;
				} else {
					c.gridx = j;
					c.gridy = 0;
					label.setLabelFor(text);
					crypto.add(label, c);
					c.gridy = 1;
					crypto.add(text, c);
				}
			} else {
				JLabel label = new JLabel("" + phrase.charAt(j));
				labels[j] = label;
				if (phrase.charAt(j) == ' ') {
					c.insets = new Insets(4, 8, 4, 8);
				}
				if (j > phrase.lastIndexOf(' ', 60) && phraseLength > 60) {
					c.gridx = line3position;
					c.gridy = 4;
					crypto.add(label, c);
					line3position++;
				} else if (j > phrase.lastIndexOf(' ', 30)) {
					c.gridx = line2position;
					c.gridy = 2;
					crypto.add(label, c);
					line2position++;
				} else {
					c.gridx = j;
					c.gridy = 0;
					crypto.add(label, c);
				}
			}
		}
		frame.getContentPane().add(crypto);
		frame.pack();
		frame.setSize(1200, 600);

	}

	/**
	 * Shows the user their current statistics such as their number of completed
	 * cryptograms, number of attempted cryptograms, number of correct guesses,
	 * number of total guesses and their guess accuracy
	 * 
	 **/
	private void showStats() {
		JOptionPane.showMessageDialog(frame,
				"Number of Cryptograms Completed: " + gameFunc.currentPlayer.getNoCompletedCryptos() + "\n"
						+ "Number of Cryptograms Attempted: " + gameFunc.currentPlayer.getNoPlayedCryptos() + "\n"
						+ "Number of Correct Guesses: " + gameFunc.currentPlayer.getNoSuccessfulGuesses() + "\n"
						+ "Number of Guesses: " + gameFunc.currentPlayer.getNoAttemptedGuesses() + "\n"
						+ "Guess Accuracy: " + gameFunc.currentPlayer.getPercentCorrect() + "% \n", "Statistics", JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Loads the user's saved cryptogram onto the GUI
	 *
	 * @param crypto:
	 *            The JPanel which the game contents are in
	 * @param c:
	 *            the previously set GridBagConstraints
	 * @param gameType:
	 *            Keeps track of what kind of cryptogram the user is playing
	 *            (e.g. char or integer)
	 */

	private void loadCrypto(JPanel crypto, GridBagConstraints c, int gameType) {
		int action;
		gameFunc.currentPlayer.setSaveLoaded();
		if (gameFunc.hasCryptoBeenCompletedBefore()) {
			action = JOptionPane.showConfirmDialog(frame,
					"You have already completed your saved cryptogram. \n Completing this again"
							+ " will not increase your number of wins, however your number of cryptograms played will increase. \n"
							+ " Do you want to continue?",
					"Warning", JOptionPane.YES_NO_OPTION);
			if (action == JOptionPane.YES_OPTION) {
				try {
					gameFunc.loadCryptogram();
					getHint.setEnabled(true);
					showFreqs.setEnabled(true);
					displayCrypto(crypto, c, gameType);
				} catch (InvalidPropertiesFormatException e) {
					JOptionPane.showMessageDialog(frame, "Save file is corrupt");
				} catch (FileNotFoundException e) {
					JOptionPane.showMessageDialog(frame, "You do not have a cryptogram saved yet.");
				} catch (IOException e) {
					JOptionPane.showMessageDialog(frame, "Could not load cryptogram.");
				}
			} else {
				gameFunc.currentPlayer.removeSaveLoaded();
			}
		} else {
			try {
				gameFunc.loadCryptogram();
				displayCrypto(crypto, c, gameType);
				getHint.setEnabled(true);
				showFreqs.setEnabled(true);
			} catch (InvalidPropertiesFormatException e) {
				JOptionPane.showMessageDialog(frame, "Save file is corrupt");
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(frame, "You do not have a cryptogram saved yet.");
			} catch (IOException e) {
				JOptionPane.showMessageDialog(frame, "Could not load cryptogram.");
			}

		}

	}

	/**
	 * Saves the user's current cryptogram to a file that can later be loaded
	 * from
	 * 
	 **/

	private void saveCrypto() {
		if (gameFunc.currentPlayer.isSavedCrypto()) {
			int save = JOptionPane.showConfirmDialog(frame,
					"You have already saved a cryptogram. Do you want" + " to overwrite this save?", "Save",
					JOptionPane.YES_NO_OPTION);
			if (save == JOptionPane.YES_OPTION) {
				try {
					gameFunc.saveCryptogram();
					JOptionPane.showMessageDialog(frame, "Cryptogram Saved");
				} catch (IOException e) {
					JOptionPane.showMessageDialog(frame, "Could not save cryptogram.");
				}
			}
		} else {
			try {
				gameFunc.saveCryptogram();
				JOptionPane.showMessageDialog(frame, "Cryptogram Saved");
			} catch (IOException e) {
				JOptionPane.showMessageDialog(frame, "Could not save cryptogram.");
			}

		}

	}

	private void showSolution(JPanel crypto, GridBagConstraints c, int gameType) {

		gameFunc.showSolution();

		for (Object phraseChar : gameFunc.cryptogram.cryptoToOriginalMap.keySet()) {
			resetChar(phraseChar, crypto, c);
		}
		checkSolution.setEnabled(false);
		undoButton.setEnabled(false);
		getHint.setEnabled(false);
	}

	private void getHint(JPanel crypto, GridBagConstraints c, int gameType) {
		int overWrite;
		overWrite = gameFunc.getHint();
		Object hint = gameFunc.getHintChar();

		resetChar(hint, crypto, c);
		if (overWrite == 1) {
			JOptionPane.showMessageDialog(frame, "Guess has been overwritten for " + gameFunc.getHintChar());
		}
		JOptionPane.showMessageDialog(frame,
				"Hint for " + gameFunc.getHintChar() + " successfully entered the cryptogram");
	if (gameFunc.checkCompleteHint() == 1) {
		JOptionPane.showMessageDialog(frame, "The cryptogram is complete. Your wins will not increase");
		try {
			newGame(crypto, c, gameType);
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(frame, "File not found", "File Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
		
	}
}

	private void showFrequencies() {
		String freqs = new String();
		String english = new String();

		for (Object phraseChar : gameFunc.cryptogram.gameFrequency.keySet()) {
			freqs = freqs.concat(phraseChar + " = " + gameFunc.cryptogram.gameFrequency.get(phraseChar) + "% \n");
		}
		JOptionPane.showMessageDialog(frame, freqs,"Game Frequencies", JOptionPane.PLAIN_MESSAGE);
		for (Object englishChar: gameFunc.cryptogram.englishFrequency.keySet()) {
			english = english.concat(englishChar + " = " + gameFunc.cryptogram.englishFrequency.get(englishChar) + "% \n");
		}
		JOptionPane.showMessageDialog(frame, english, "English Frequencies", JOptionPane.PLAIN_MESSAGE);

	}
	
	private void showScoreboard() {
		ArrayList<String> names = new ArrayList<>();
		try {
			names = gameFunc.getLeaderBoard();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, "Could not find scoreboard");
		}
		
		String scoreboard = new String();
		int i = 1;
		
		for (String name: names ){
			scoreboard = scoreboard.concat(i + " : " + name + " \n");
			i++;
		}
		JOptionPane.showMessageDialog(frame, scoreboard);
	}
}
