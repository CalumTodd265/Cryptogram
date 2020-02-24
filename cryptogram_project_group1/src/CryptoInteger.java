import java.io.FileNotFoundException;

import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.io.File;

/**
 * CryptoInteger is an Integer based implementation of the Cryptogram abstract class.
 * It creates a cryptogram for a given phrase where the letters mapped to the original phrase are Integers.
 */
public class CryptoInteger extends Cryptogram<Integer> {

    /**
     * The standard constructor for CryptoInteger.
     * Will set object to a random seed and sets the phrases text file to "src\\phrases.txt".
     */
    public CryptoInteger() throws FileNotFoundException {
        phrasesPath = "src\\phrases.txt";
        cryptoToOriginalMap = new HashMap<>();
        originalToCryptoMap = new HashMap<>();
        guessMap = new HashMap<>();
        freqMap = new HashMap<>();
        encryptedFreqMap = new HashMap<>();
        randomSeed = new Random();
        hotEncodedPhrase = randomSeed.nextInt(19);
        generateNewCryptogram();
    }

    /**
     * Testing constructor for CryptoInteger
     * This should not be called outside of test files.
     *
     * @param seed: The random generator seed you wish to use.
     * @param path: The directory path of the file you wish to load phrases from.
     *
     */
    public CryptoInteger(Integer seed, String path) {
        phrasesPath = path;
        cryptoToOriginalMap = new HashMap<>();
        originalToCryptoMap = new HashMap<>();
        guessMap = new HashMap<>();
        freqMap = new HashMap<>();
        randomSeed = new Random(seed);
        hotEncodedPhrase = randomSeed.nextInt(19);
        encryptedFreqMap = new HashMap<>();
        System.out.println(hotEncodedPhrase);
    }

    /***
     * another constructor to be used for the loading of a saved cryptogram
     * @param hep: the hotcode value which is used to load the original phrase from the phrases.txt file
     * @param ctoMap: The mapping of encrypted to original to original characters
     * @param otcMap: The mapping of original to encrypted numbers
     * @param guesses: The mapping of the users guesses to the encrypted numbers
     * @param frequencies: The mapping of the characters in the cryptogram to the frequency with which they appear
     * @throws FileNotFoundException :if the phrases.txt file cannot be found
     */
    public CryptoInteger(int hep, HashMap<Integer, Character> ctoMap, HashMap<Character, Integer> otcMap,
                      HashMap<Integer, Character> guesses, HashMap<Character, Integer> frequencies, HashMap<Integer, Double> gFreq,
                         HashMap<Character, Double> engFreq) throws FileNotFoundException {
        phrasesPath = "src\\phrases.txt";
        cryptoToOriginalMap = ctoMap;
        originalToCryptoMap = otcMap;
        guessMap = guesses;
        freqMap = frequencies;
        randomSeed = new Random();
        hotEncodedPhrase = hep;
        gameFrequency = gFreq;
        englishFrequency = engFreq;
        loadPhrase();
    }

    /**
     * generateEncryptedChar recursively generates random Integers until a Integer that hasn't been mapped in the
     * encryption before is generated.
     */
    protected Integer generateEncryptedChar() {
        Integer possibleCryptoInt = (randomSeed.nextInt(26));
        if (cryptoToOriginalMap.containsKey(possibleCryptoInt)){
            return generateEncryptedChar();
        }
        return possibleCryptoInt;
    }

    /**
     * generateCryptoMappings creates two HashMaps representing the conversion between a cryptogram Integer and
     * the original letter. originalToCryptoMap will convert a given character from the original phrase into its
     * encrypted equivalent and cryptoToOriginalMap will convert an encrypted Integer into its original form.
     */
    protected void generateCryptoMappings() {
        for (Character currentChar : freqMap.keySet()) {
            int generatedInt = generateEncryptedChar();
            cryptoToOriginalMap.put(generatedInt, currentChar);
            originalToCryptoMap.put(currentChar, generatedInt);
        }
    }

    /**
     * @return returns the original version of an encrypted integer.
     */
    protected Character getOriginalChar(Integer cryptoChar){
        return cryptoToOriginalMap.get(cryptoChar);
    }

    /**
     * @return returns the encrypted version of a character.
     */
    protected Integer getEncryptedChar(Character originalChar){
        return originalToCryptoMap.get(originalChar);
    }

    /**
     * generateFreqMap will generate a frequency map of every character in the encrypted phrase.
     */
    protected void generateEncryptedFreqMap(){
        for(int currentChar : cryptoToOriginalMap.keySet()){
            for(int i = 0; i < freqMap.get(cryptoToOriginalMap.get(currentChar)); i++) {
                if(encryptedFreqMap.containsKey(currentChar)) {
                    encryptedFreqMap.put(currentChar, encryptedFreqMap.get(currentChar) + 1);
                }
                else{
                    encryptedFreqMap.put(currentChar,1);
                }
            }
        }
    }

    /**
     * fillGameFrequency will loop through encryptedFreqMap and fill the game frequency HashMap appropritely.
     * Each encrypted letter will be given a percentage of the amount they appear in the encrypted phrase rounded to
     * two decimal places.
     */
    protected void fillGameFrequency(){
        for(int c : encryptedFreqMap.keySet()){
            double frequency = (double)encryptedFreqMap.get(c) / characterCount();
            frequency *= 100;
            frequency = Math.round(frequency * 100.0) / 100.0;
            gameFrequency.put(c , frequency);
        }
    }

}


