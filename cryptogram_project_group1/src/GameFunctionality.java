import java.io.*;
import java.util.*;

/**
 * GameFunctionality handles the main game-logic of the cryptogram game.
 */
public class GameFunctionality {
    protected Cryptogram cryptogram;
    protected Player currentPlayer;
    protected PlayerManager playerManager;
    protected int type;
    protected boolean isPlayingSaved;
    private char hintChar;
    private Object cryptChar;

    /**
     * Constructor for GameFunctionality. Currently empty but will be used in future sprints.
     */
    public GameFunctionality() {
        playerManager = new PlayerManager();
    }

    /**
     * Creates a new Character implementation of the cryptogram class.
     *
     * @throws FileNotFoundException
     */
    protected void newLetterGame() throws FileNotFoundException {
        cryptogram = new CryptoChar();
        currentPlayer.incrementPlayedCrypto();
        type = 0;
        isPlayingSaved = false;
        currentPlayer.removeSaveLoaded();
    }

    /**
     * Testing function for the Character implementation of the Cryptogram class.
     * This should not be called outside of test files.
     *
     * @param seed: The random generator seed you wish to use.
     * @param path: The directory path of the file you wish to load phrases from.
     */
    protected void newLetterGame(Integer seed, String path) throws FileNotFoundException {
        cryptogram = new CryptoChar(seed, path);
        cryptogram.generateNewCryptogram();
        currentPlayer.incrementPlayedCrypto();
        type = 0;
        isPlayingSaved = false;
        currentPlayer.removeSaveLoaded();
    }

    /**
     * Creates a new Character implementation of the cryptogram class.
     *
     * @throws FileNotFoundException
     */
    protected void newIntegerGame() throws FileNotFoundException {
        cryptogram = new CryptoInteger();
        currentPlayer.incrementPlayedCrypto();
        type = 1;
        isPlayingSaved = false;
        currentPlayer.removeSaveLoaded();
    }

    /**
     * Testing function for the Integer implementation of the Cryptogram class.
     * This should not be called outside of test files.
     *
     * @param seed: The random generator seed you wish to use.
     * @param path: The directory path of the file you wish to load phrases from.
     */
    protected void newIntegerGame(Integer seed, String path) throws FileNotFoundException {
        cryptogram = new CryptoInteger(seed, path);
        cryptogram.generateNewCryptogram();
        currentPlayer.incrementPlayedCrypto();
        type = 1;
        isPlayingSaved = false;
        currentPlayer.removeSaveLoaded();
    }

    /**
     * makeGuess allows the player to try and guess the original character a cryptogram element represents.
     * It will also check if the cryptogram is complete after every successful guess.
     *
     * @param cryptoChar The encrypted element the player would like to map a guess to.
     * @param guessChar  The guess the player has made.
     * @param Confirmed  A boolean denoting if the player has confirmed their choice.
     * @return A hot encoded integer representing both the success status of the guess and the completion status of the
     * cyrptogram if the guess is successful.
     * Positive values are denoted in the checkComplete function.
     * A 0 represents that the map was successful or that number was mapped already.
     * If the guess is unsuccessful a non-negative number is returned.
     * A -1 represents that the element is not in the encryption. It is handled with an error to the user.
     * A -3 represents that the guessMap already contains that guess. It is handled with an error to the user.
     * A -2 represents that the key the player is attempting to map to already has a guess assigned to it.
     * The player will be asked to confirm their choice at which point the guess will be removed and makeGuess
     * will be recalled.
     */
    protected int makeGuess(Object cryptoChar, Object guessChar, boolean Confirmed) {
        if (guessChar == cryptogram.guessMap.get(cryptoChar)) {
            return 0;
        } else if (!cryptogram.cryptoToOriginalMap.containsKey(cryptoChar)) {
            return -1;
        } else if (cryptogram.guessMap.containsValue(guessChar)) {
            return -3;
        } else if (cryptogram.guessMap.containsKey(cryptoChar) && !Confirmed) {
            return -2;
        }
        cryptogram.guessMap.put(cryptoChar, guessChar);
        if (cryptogram.cryptoToOriginalMap.get(cryptoChar) == cryptogram.guessMap.get(cryptoChar)) {
            currentPlayer.incrementSuccessfulGuesses();
        }
        currentPlayer.incrementAttemptedGuesses();
        return checkComplete();
    }

    /***
     * Updates the guess map to be equal to the crypto to original map so the correct solution can be shown to the user
     */
    protected void showSolution() {
        for (Object currentChar : cryptogram.cryptoToOriginalMap.keySet()) {
            Object solutionChar = cryptogram.cryptoToOriginalMap.get(currentChar);
            cryptogram.guessMap.put(currentChar, solutionChar);
        }
    }

    /**
     * checkComplete checks if the cryptogram is complete.
     *
     * @return An hot encoded integer representing the completion state of the cryptogram.
     * Returns 0 for incomplete,  Returns 1 for complete but incorrect and returns 2 for complete and correct.
     */
    protected int checkComplete() {
        boolean complete = true;
        if (cryptogram == null) {
            return 0;
        }
        for (Object c : cryptogram.cryptoToOriginalMap.keySet()) {
            if (cryptogram.cryptoToOriginalMap.get(c) != cryptogram.guessMap.get(c)) {
                complete = false;
            }
        }
        if (cryptogram.guessMap.size() < cryptogram.originalToCryptoMap.size()) {
            return 0;
        } else if (complete) {
            if (!currentPlayer.getSaveLoaded()) {
                currentPlayer.incrementCompletedCrypto();
            } else if (currentPlayer.getSaveLoaded() && !currentPlayer.getSaveCompleted()) {
                currentPlayer.incrementCompletedCrypto();
                currentPlayer.setSaveCompleted();
            }
            return 2;
        } else {
            return 1;
        }
    }

    /***
     * Used to check if a hint has been used to complete the cryptogram
     * @return
     * 1 if the cryptogram is complete and correct
     * 0 if the cryptogram is complete and incorrect
     * -1 if the cryptogram is incomplete
     */
    protected int checkCompleteHint(){
        boolean complete = true;
        if(cryptogram.guessMap.size() > cryptogram.cryptoToOriginalMap.size()){
            return -1;
        }
        for (Object c : cryptogram.cryptoToOriginalMap.keySet()) {
            if (cryptogram.cryptoToOriginalMap.get(c) != cryptogram.guessMap.get(c)) {
                complete = false;
            }
        }
        if(complete){
            return 1;
        }
        return 0;
    }

    /***
     * @return
     * 1 if the character will be overwriting a current user guess
     * 0 if not
     */
    private int isOverwrite(){
        if(cryptogram.guessMap.get(cryptChar) == null){
            return 0;
        }
        return 1;
    }

    /***
     * Will generate a hint for the user and place it into the guess map
     * @return
     * 1 if the hint will be overwriting an incorrect user guess
     * 0 if not
     */
    protected int getHint() {
    Random rand = new Random();
    int lookup = rand.nextInt(cryptogram.phrase.length());
    hintChar = Character.toLowerCase(cryptogram.phrase.charAt(lookup));
    cryptChar = cryptogram.originalToCryptoMap.get(Character.toLowerCase(hintChar));
    while(cryptogram.guessMap.get(cryptChar) == cryptogram.cryptoToOriginalMap.get(cryptChar) || exceptionCheck()){
        lookup = rand.nextInt(cryptogram.phrase.length());
        hintChar = Character.toLowerCase(cryptogram.phrase.charAt(lookup));
        cryptChar = cryptogram.originalToCryptoMap.get(hintChar);
    }
    int over = isOverwrite();
    cryptogram.guessMap.put(cryptChar,hintChar);
    return over;
    }

    /***
     * checks if the current value of hintChar is part of the cryptogram exception list
     * @return
     * true - if the value of hintChar was found in the exception list
     * False - if not
     */
    private boolean exceptionCheck(){
        for(int i = 0; i < cryptogram.exceptionList.length(); i++){
            if(cryptogram.exceptionList.charAt(i) == hintChar){
                return true;
            }
        }
        return false;
    }

    /***
     * getter for the cryptChar field
     * @return
     * The current value for the cryptChar field
     *
     */
    public Object getHintChar(){
        return cryptChar;
    }

    /**
     * undo allows the player to undo a guess for a particular cryptogram element.
     *
     * @param cryptoChar: the character in the cryptogram the player wishes to unmap a guess from.
     * @return A hot encoded integer representing the success status of the undo.
     * A 0 represents that the element is not in the encryption.
     * A -1 represents that a guess has not been made for the given encrypted element.
     * A 1 represents a successful undo
     */
    protected int undo(Object cryptoChar) {
        if (!cryptogram.cryptoToOriginalMap.containsKey(cryptoChar)) {
            return 0;
        }
        if (!cryptogram.guessMap.containsKey(cryptoChar)) {
            return -1;
        }
        cryptogram.guessMap.remove(cryptoChar);
        return 1;
    }


    /***
     * calls the player manager to create a new player with the specified playerName
     * @param name: the name of the new player to be created
     * @throws IOException: if the file creation or writing fails
     */
    protected void createNewPlayer(String name) throws IOException {
        playerManager.createNewPlayer(name);
    }

    /***
     * calls the player manager to load the details of the player specified
     * @param name: the name of the player who's information should be loaded
     * @throws NumberFormatException if the players save file is corrupt
     * @throws IOException if the players save file could not be found
     */
    protected void loadPlayer(String name) throws NumberFormatException, IOException {
        currentPlayer = playerManager.loadPlayer(name);
        currentPlayer.loadDetails();
    }

    /***
     * will throw an exception if the file does not exists. Will do nothing if it exists.
     * @param in The file who's existance is being checked
     * @throws java.io.IOException
     */
    protected void fileCheck(File in) throws java.io.IOException {
        if (!in.isFile()) {
            throw new FileNotFoundException(("This player does not have a cryptogram saved"));
        }
    }


    /***
     * will return the type of cryptogram whos information is currently stored
     * @return: the type of Cryptogram which is saved
     * @throws java.io.IOException: if the file that they are trying to check doesn't exist
     */
    protected String getType(File in) throws java.io.IOException {
        fileCheck(in);
        FileInputStream fis = new FileInputStream(in);
        Scanner scan = new Scanner(fis);
        String type = scan.nextLine();
        fis.close();
        return type;
    }

    /***
     * will decide what kind of cryptogram is saved and will call the appropriate one to load the information
     * @throws java.io.IOException
     */
    protected void loadCryptogram() throws InvalidPropertiesFormatException, java.io.IOException {
        File in = new File("src\\Saves\\" + currentPlayer.getPlayerName() + ".txt");
        if (getType(in).equals("char")) {
            isPlayingSaved = true;
            loadCryptogramchar(in);
        } else {
            isPlayingSaved = true;
            loadCryptogramInt(in);
        }
        currentPlayer.setSaveLoaded();
    }

    /***
     * will load the information for a CryptoChar data type from a file
     * @param in: The file which the information in being loaded from
     * @throws java.io.IOException
     */
    protected void loadCryptogramchar(File in) throws java.io.IOException {
        Boolean corruption = false;
        HashMap<Character, Integer> freq = new HashMap<>();
        HashMap<Character, Character> org = new HashMap<>();
        HashMap<Character, Character> crypt = new HashMap<>();
        HashMap<Character, Character> guess = new HashMap<>();
        HashMap<Character, Double> gFreq = new HashMap<>();
        HashMap<Character, Double> engFreq = new HashMap<>();
        String currentLine = "";
        FileInputStream fis = new FileInputStream(in);
        Scanner scan = new Scanner(fis);
        try {
            scan.nextLine();
            currentLine = scan.nextLine();
            int hot = Integer.parseInt(currentLine);
            currentLine = scan.nextLine();

            while (!currentLine.equals("############################")) {
                StringTokenizer str = new StringTokenizer(currentLine, "=", false);
                crypt.put(str.nextToken().charAt(0), str.nextToken().charAt(0));
                currentLine = scan.nextLine();
            }
            currentLine = scan.nextLine();
            while (!currentLine.equals("############################")) {
                StringTokenizer str = new StringTokenizer(currentLine, "=", false);
                org.put(str.nextToken().charAt(0), str.nextToken().charAt(0));
                currentLine = scan.nextLine();
            }
            currentLine = scan.nextLine();
            while (!currentLine.equals("############################")) {
                StringTokenizer str = new StringTokenizer(currentLine, "=", false);
                guess.put(str.nextToken().charAt(0), str.nextToken().charAt(0));
                currentLine = scan.nextLine();
            }
            currentLine = scan.nextLine();
            while (!currentLine.equals("############################")) {
                StringTokenizer str = new StringTokenizer(currentLine, "=", false);
                freq.put(str.nextToken().charAt(0), Integer.parseInt(str.nextToken()));
                currentLine = scan.nextLine();
            }
            currentLine = scan.nextLine();
            while (!currentLine.equals("############################")) {
                StringTokenizer str = new StringTokenizer(currentLine, "=", false);
                gFreq.put(str.nextToken().charAt(0), Double.parseDouble(str.nextToken()));
                currentLine = scan.nextLine();
            }
            currentLine = scan.nextLine();
            while (!currentLine.equals("############################")) {
                StringTokenizer str = new StringTokenizer(currentLine, "=", false);
                engFreq.put(str.nextToken().charAt(0),Double.parseDouble(str.nextToken()));
                currentLine = scan.nextLine();
            }
            fis.close();
            this.cryptogram = new CryptoChar(hot, crypt, org, guess, freq, gFreq, engFreq);
            return;
        } catch (Exception e) {
            corruption = true;
        }
        if (corruption) {
            throw (new InvalidPropertiesFormatException("File is corrupt"));
        }
    }

    /***
     * Loads the information for a CryptoInteger data type from a file
     * @param in: The file which the information  will be loaded from
     * @throws java.io.IOException
     */
    protected void loadCryptogramInt(File in) throws java.io.IOException {
        Boolean corruption = false;
        HashMap<Character, Integer> freq = new HashMap<>();
        HashMap<Character, Integer> org = new HashMap<>();
        HashMap<Integer, Character> crypt = new HashMap<>();
        HashMap<Integer, Character> guess = new HashMap<>();
        HashMap<Integer, Double> gFreq = new HashMap<>();
        HashMap<Character, Double> engFreq = new HashMap<>();
        String currentLine = "";
        FileInputStream fis = new FileInputStream(in);
        Scanner scan = new Scanner(fis);
        try {
            scan.nextLine();
            currentLine = scan.nextLine();
            int hot = Integer.parseInt(currentLine);
            currentLine = scan.nextLine();

            while (!currentLine.equals("############################")) {
                StringTokenizer str = new StringTokenizer(currentLine, "=", false);
                crypt.put(Integer.parseInt(str.nextToken()), str.nextToken().charAt(0));
                currentLine = scan.nextLine();
            }
            currentLine = scan.nextLine();
            while (!currentLine.equals("############################")) {
                StringTokenizer str = new StringTokenizer(currentLine, "=", false);
                org.put(str.nextToken().charAt(0), Integer.parseInt(str.nextToken()));
                currentLine = scan.nextLine();
            }
            currentLine = scan.nextLine();
            while (!currentLine.equals("############################")) {
                StringTokenizer str = new StringTokenizer(currentLine, "=", false);
                guess.put(Integer.parseInt(str.nextToken()), str.nextToken().charAt(0));
                currentLine = scan.nextLine();
            }
            currentLine = scan.nextLine();
            while (!currentLine.equals("############################")) {
                StringTokenizer str = new StringTokenizer(currentLine, "=", false);
                freq.put(str.nextToken().charAt(0), Integer.parseInt(str.nextToken()));
                currentLine = scan.nextLine();
            }
            currentLine = scan.nextLine();
            while (!currentLine.equals("############################")) {
                StringTokenizer str = new StringTokenizer(currentLine, "=", false);
                gFreq.put(Integer.parseInt(str.nextToken()), Double.parseDouble(str.nextToken()));
                currentLine = scan.nextLine();
            }
            currentLine = scan.nextLine();
            while (!currentLine.equals("############################")) {
                StringTokenizer str = new StringTokenizer(currentLine, "=", false);
                engFreq.put(str.nextToken().charAt(0), Double.parseDouble(str.nextToken()));
                currentLine = scan.nextLine();
            }
            fis.close();
            this.cryptogram = new CryptoInteger(hot, crypt, org, guess, freq, gFreq, engFreq);
            return;
        } catch (Exception e) {
            corruption = true;
        }
        if (corruption) {
            throw (new InvalidPropertiesFormatException("File is corrupt"));
        }
    }

    /***
     * saves the information of a CryptoChar data type to a file
     * @param saved: The file which the cryptogram information will be writen to
     * @throws java.io.IOException
     */
    protected void saveCryptogramChar(File saved) throws java.io.IOException {
        String write = String.valueOf(cryptogram.hotEncodedPhrase);
        HashMap<Character, Integer> freq = cryptogram.freqMap;
        HashMap<Character, Character> org = cryptogram.originalToCryptoMap;
        HashMap<Character, Character> crypt = cryptogram.cryptoToOriginalMap;
        HashMap<Character, Character> guess = cryptogram.guessMap;
        HashMap<Character, Double> gFreq = cryptogram.gameFrequency;
        HashMap<Character, Double> engFreq = cryptogram.englishFrequency;

        FileOutputStream out = new FileOutputStream(saved);
        PrintWriter print = new PrintWriter(out);
        print.println("char");
        print.println(write);

        for (Map.Entry<Character, Character> m : crypt.entrySet()) {
            print.println((m.getKey() + "=" + m.getValue()));
        }
        print.println("############################");

        for (Map.Entry<Character, Character> m : org.entrySet()) {
            print.println((m.getKey() + "=" + m.getValue()));
        }

        print.println("############################");

        for (Map.Entry<Character, Character> m : guess.entrySet()) {
            print.println((m.getKey() + "=" + m.getValue()));
        }

        print.println("############################");

        for (Map.Entry<Character, Integer> m : freq.entrySet()) {
            print.println((m.getKey() + "=" + m.getValue()));
        }

        print.println("############################");

        for (Map.Entry<Character, Double> m : gFreq.entrySet()){
            print.println(m.getKey() + "=" + m.getValue());
        }

        print.println("############################");

        for(Map.Entry<Character, Double> m : engFreq.entrySet()){
            print.println(m.getKey() + "=" + m.getValue());
        }

        print.println("############################");
        print.flush();
        print.close();
        out.close();
    }

    /***
     * Saves the details of a CryptoInteger data type to a file
     * @param saved: The file to which the information is being saved
     * @throws java.io.IOException
     */
    protected void saveCryptogramInt(File saved) throws java.io.IOException {
        String write = String.valueOf(cryptogram.hotEncodedPhrase);
        HashMap<Character, Integer> freq = cryptogram.freqMap;
        HashMap<Character, Integer> org = cryptogram.originalToCryptoMap;
        HashMap<Integer, Character> crypt = cryptogram.cryptoToOriginalMap;
        HashMap<Character, Integer> guess = cryptogram.guessMap;
        HashMap<Integer, Double> gFreq = cryptogram.gameFrequency;
        HashMap<Character, Double> engFreq = cryptogram.englishFrequency;

        FileOutputStream out = new FileOutputStream(saved);
        PrintWriter print = new PrintWriter(out);
        print.println("int");
        print.println(write);

        for (Map.Entry<Integer, Character> m : crypt.entrySet()) {
            print.println((m.getKey() + "=" + m.getValue()));
        }
        print.println("############################");

        for (Map.Entry<Character, Integer> m : org.entrySet()) {
            print.println((m.getKey() + "=" + m.getValue()));
        }

        print.println("############################");

        for (Map.Entry<Character, Integer> m : guess.entrySet()) {
            print.println((m.getKey() + "=" + m.getValue()));
        }

        print.println("############################");


        for (Map.Entry<Character, Integer> m : freq.entrySet()) {
            print.println((m.getKey() + "=" + m.getValue()));
        }

        print.println("############################");

        for (Map.Entry<Integer, Double> m : gFreq.entrySet()){
            print.println(m.getKey() + "=" + m.getValue());
        }

        print.println("############################");

        for(Map.Entry<Character, Double> m : engFreq.entrySet()){
            print.println(m.getKey() + "=" + m.getValue());
        }

        print.println("############################");
        print.flush();
        print.close();
        out.close();
    }


    /***
     * Wrapper function to decide which version of saveCryptogram will be used. creates the File object which will be used in those methods
     * @throws java.io.IOException
     */
    public void saveCryptogram() throws java.io.IOException {
        File saved = new File("src\\Saves\\" + currentPlayer.getPlayerName() + ".txt");
        if (type == 0) {
            saveCryptogramChar(saved);
        } else {
            saveCryptogramInt(saved);
        }
        currentPlayer.setSavedCrypto();
        currentPlayer.setSaveLoaded();
        currentPlayer.removeSaveCompleted();
    }

    /***
     * to prevent stat abuse through the save and load functionality
     * @return a boolean which indicates if the user has completed the current cryptogram before
     */
    public boolean hasCryptoBeenCompletedBefore() {
        if (currentPlayer.getSaveLoaded() && currentPlayer.getSaveCompleted()) {
            return true;
        }
        return false;
    }


    protected ArrayList<String> getLeaderBoard() throws IOException {
        return playerManager.getLeaderBoard();
    }
}


