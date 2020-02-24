import java.io.*;
import java.util.InvalidPropertiesFormatException;

public class Player {
    private String playerName;
    private int noAttemptedGuesses;
    private int noSuccessfulGuesses;
    private int noPlayedCryptos;
    private int noCompletedCryptos;
    private double percentCorrect;
    private boolean savedCrypto;
    private boolean isCurrentSaveCompleted;
    private boolean isCurrentSaveLoaded;

    /**
     * The standard constructor for Player.
     * Sets the name of the current player and calls loadDetails method
     * @param name: The name of the player who is logged in.
     */
    public Player(String name) throws IOException, FileNotFoundException, NumberFormatException{
        playerName = name;
    }


    /**
     * @return Players name.
     */
    protected String getPlayerName(){
        return playerName;
    }

    /**
     * incrementsAttemptedGuesses increments the total number of attempted guesses of a player.
     */
    protected void incrementAttemptedGuesses(){
        noAttemptedGuesses++;
    }

    /**
     * incrementSuccessfulGuesses increments the total number of successful guesses of a player.
     */
    protected void incrementSuccessfulGuesses(){
        noSuccessfulGuesses++;
    }

    /**
     * @return the total number of attempted guesses of a player.
     */
    protected int getNoAttemptedGuesses(){
        return noAttemptedGuesses;
    }

    /**
     * @return the total number of successful guesses of a player.
     */
    protected int getNoSuccessfulGuesses(){
        return noSuccessfulGuesses;
    }

    /**
     * @return the total number of played cryptograms of a player.
     */
    protected int getNoPlayedCryptos(){
        return noPlayedCryptos;
    }

    /**
     * @return the total number of completed cryptograms of a player.
     */
    protected int getNoCompletedCryptos(){
        return noCompletedCryptos;
    }

    /**
     * getPercentCorrect Calculates the percent of correct of guesses made by a player.
     * @return the percent of correct guesses
     */
    protected double getPercentCorrect(){
        if(noSuccessfulGuesses == 0){
            percentCorrect = 0;
            return  percentCorrect;
        }
        else{
            percentCorrect = (noSuccessfulGuesses * 100.0f) / noAttemptedGuesses;
            return Math.round(percentCorrect);
        }

    }

    /**
     * savedCrypto: set to true.
     */
    public void setSavedCrypto(){
        savedCrypto = true;
    }

    /**
     * removedSavedCrypto savedCrypto: set to false.
     */
    public void removeSavedCrypto(){
        savedCrypto = false;
    }

    /**
     * @return if there is a cryptogram saved.
     */
    public boolean isSavedCrypto(){
        return savedCrypto;
    }

    /**
     * incrementPlayedCrypto increments the total number of cryptograms of a player.
     */
    protected void incrementPlayedCrypto(){
        noPlayedCryptos++;
    }

    /**
     * incrementCompletedCrypto increments the total number of completed cryptograms of a player.
     */
    protected void incrementCompletedCrypto(){
        noCompletedCryptos++;
    }

    /**
     * @return if the saved cryptogram has been completed before.
     */
    protected boolean getSaveCompleted(){
        return isCurrentSaveCompleted;
    }

    /**
     * isCurrentSaveCompleted: set to true.
     */
    protected void setSaveCompleted(){
        isCurrentSaveCompleted = true;
    }

    /**
     * isCurrentSaveCompleted: set to false.
     */
    protected void removeSaveCompleted(){
        isCurrentSaveCompleted = false;
    }

    /**
     * is the current saved cryptogram is loaded.
     */
    protected boolean getSaveLoaded(){
        return isCurrentSaveLoaded;
    }

    /**
     * isCurrentSaveLoaded: set to true.
     */
    protected void setSaveLoaded(){
        isCurrentSaveLoaded = true;
    }

    /**
     * isCurrentSaveLoaded: set to false.
     */
    protected void removeSaveLoaded(){
        isCurrentSaveLoaded = false;
    }

    public int compareTo(){
        return 0;
    }


    /***
     * saveDetails writes the statistics of the current logged in player to a file of the same name as player.
     *
     * @throws java.io.IOException
     */
    protected void saveDetails() throws IOException {
        File player = new File("src\\Players\\"+getPlayerName()+".txt");
        if(player.isFile()) {
            BufferedWriter writer = new BufferedWriter(new FileWriter("src\\Players\\" + getPlayerName() + ".txt"));
            writer.write(String.valueOf(getNoAttemptedGuesses()));
            writer.newLine();
            writer.write(String.valueOf(getNoSuccessfulGuesses()));
            writer.newLine();
            writer.write(String.valueOf(getNoPlayedCryptos()));
            writer.newLine();
            writer.write(String.valueOf(getNoCompletedCryptos()));
            writer.newLine();
            writer.write(String.valueOf(isSavedCrypto()));
            writer.newLine();
            writer.write(String.valueOf(getSaveCompleted()));
            writer.close();
            return;
        }
        throw new FileNotFoundException("This file does not exist");

    }

    /***
     *reads the statistics of the current logged in player from the players file.
     * If the player has a file saved and it is not empty the statistics are read from each line.
     * If the player does not have a file or it is empty then stats are default set to 0 or false.
     * @throws java.io.IOException
     * @throws java.io.FileNotFoundException
     */
    protected void loadDetails() throws IOException, FileNotFoundException{
        File player = new File("src\\Players\\"+getPlayerName()+".txt");
        BufferedReader br = new BufferedReader(new FileReader("src\\Players\\" + getPlayerName() + ".txt"));
        br.mark(1);
        boolean errorLoading = false;
        if(player.isFile() && br.readLine() != null) {
            try {
                br.reset();
                noAttemptedGuesses = Integer.parseInt(br.readLine());
                noSuccessfulGuesses = Integer.parseInt(br.readLine());
                noPlayedCryptos = Integer.parseInt(br.readLine());
                noCompletedCryptos = Integer.parseInt(br.readLine());
                savedCrypto = Boolean.parseBoolean(br.readLine());
                isCurrentSaveCompleted = Boolean.parseBoolean(br.readLine());
                br.close();
                isCurrentSaveLoaded = false;
                return;
            } catch (Exception e) { //Generic exception due to wide array of possible corrupt problems that will be handled the same way
                errorLoading = true;
            }
        }
        noAttemptedGuesses = 0;
        noSuccessfulGuesses = 0;
        noPlayedCryptos = 0;
        noCompletedCryptos = 0;
        savedCrypto = false;
        isCurrentSaveCompleted = false;
        if(errorLoading){
            throw(new InvalidPropertiesFormatException("File is corrupt"));
        }
        return;
    }
}
