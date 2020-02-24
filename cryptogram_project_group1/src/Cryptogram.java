import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.io.File;
import java.lang.Character;

public abstract class Cryptogram<CryptogramType> {
    protected String phrasesPath;
    protected String phrase;
    protected int hotEncodedPhrase;
    protected String encryptedPhrase;
    protected String exceptionList = ",.!?() '`’";
    protected HashMap<Character, Integer> freqMap;
    protected HashMap<CryptogramType, Integer> encryptedFreqMap;
    protected HashMap<Character, CryptogramType> originalToCryptoMap;
    protected HashMap<CryptogramType, Character> cryptoToOriginalMap;
    protected HashMap<CryptogramType, Character> guessMap;
    protected HashMap<CryptogramType, Double> gameFrequency = new HashMap<>();
    protected HashMap<Character, Double> englishFrequency = new HashMap<>();
    protected Random randomSeed;

    /**
     * generateNewCryptogram will load a random phrase from a file and generate an encryption for it.
     *
     * @throws FileNotFoundException
     */
    protected void generateNewCryptogram() throws FileNotFoundException{
        loadPhrase();
        generateCipher();
        fillEnglishFrequency();
    }

    /**
     * loadPhrase will load a random line from a given file and set that to be the phrase for the cryptogram object.
     *
     * @throws FileNotFoundException
     */
    protected void loadPhrase() throws FileNotFoundException{
    File phraseFile = new File(phrasesPath);
        Scanner scanner = new Scanner(phraseFile);
        int i = 0;
        while (scanner.hasNext()) {
            if (i == hotEncodedPhrase) {
                phrase = scanner.nextLine();
                break;
            }
            scanner.nextLine();
            i++;
        }
        scanner.close();
    }

    /**
     * generateCipher will generate an encryption for the string stored in the phrase variable.
     */
    protected void generateCipher() {
        generateFreqMap();
        generateCryptoMappings();
        generateEncryptedFreqMap();
        fillGameFrequency();
    }

    /**
     * generateFreqMap will generate a frequency map of every character in the string stored in the phrase variable.
     */
    protected void generateFreqMap() {
        for (int i = 0; i < phrase.length(); i++) {
            char currentChar = Character.toLowerCase(phrase.charAt(i));
            if (exceptionList.indexOf(currentChar) != -1) {
                continue;
            } else if (freqMap.containsKey(currentChar)) {
                freqMap.put(currentChar, (freqMap.get(currentChar) + 1));
            } else {
                freqMap.put(currentChar, (1));
            }
        }
    }

    /**
     * generateFreqMap will generate a frequency map of every character in the encrypted phrase.
     */
    protected abstract void generateEncryptedFreqMap();

    /**
     * generateEncryptedChar recursively generates random elements of 'CryptogramType'
     * until a character that hasn't been mapped in the encryption before is generated.
     */
    protected abstract CryptogramType generateEncryptedChar();

    /**
     * generateCryptoMappings creates two HashMaps representing the conversion between a cryptogram Character/Integer
     * and its original letter. originalToCryptoMap will convert a given character from the original phrase into its
     * encrypted equivalent and cryptoToOriginalMap will convert an encrypted element into its original form.
     */
    protected abstract void generateCryptoMappings();

    /**
     * @return phrase
     */
    protected String getPhrase(){return phrase;}

    /**
     * @return returns the original version of an encrypted Character/Integer.
     */
    protected abstract CryptogramType getEncryptedChar(Character originalChar);

    /**
     * @return returns the encrypted version of a character.
     */
    protected abstract Character getOriginalChar(CryptogramType originalChar);

    /**
     * characetCount will loop through the phrase and count the amount each character appears in the phrase.
     */
    protected double characterCount() {
        double counter = 0.0;
        for (int i = 0; i < phrase.length(); i++) {
            if (exceptionList.indexOf(phrase.charAt(i)) != -1) {
                continue;
            } else {
                counter++;
            }
        }
        return counter;
    }

    /**
     * fillEnglishFrequency reads the average frequency of each letter in the alphabet based on a study
     *
     * @throws FileNotFoundException
     */
    protected void fillEnglishFrequency() throws FileNotFoundException{
        File freqFile = new File("src\\frequency.txt");
        if(freqFile.isFile()) {
            Scanner scanner = new Scanner(freqFile);
            char letter = 0;
            double frequency = 0;
            while (scanner.hasNext()) {
                letter = scanner.next().charAt(0);
                frequency = scanner.nextDouble();
                englishFrequency.put(letter, frequency);
            }
            scanner.close();
        }
        else{throw new FileNotFoundException("The file no longer exists");}
    }

    /**
     * fillGameFrequency will loop through encryptedFreqMap and fill the game frequency HashMap appropritely.
     * Each encrypted letter will be given a percentage of the amount they appear in the encrypted phrase rounded to
     * two decimal places.
     */
    protected abstract void fillGameFrequency();

    /**
     * returns englishFrequency HashMap
     */
    protected HashMap getEnglishFrequency(){return englishFrequency;}

    /**
     * returns gameFrequency HashMap
     */
    protected HashMap getGameFrequency(){return gameFrequency;}
}