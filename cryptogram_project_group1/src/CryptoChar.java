import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.lang.Character;

/**
 * CryptoChar is a Character based implementation of the Cryptogram abstract class.
 * It creates a cryptogram for a given phrase where the letters mapped to the original phrase are characters.
 */
public class CryptoChar extends Cryptogram<Character> {
    /**
     * The standard constructor for CryptoChar.
     * Will set object to a random seed and sets the phrases text file to "src\\phrases.txt".
     */
    public CryptoChar() throws FileNotFoundException {
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

    /***
     * Second constructor to be used for the loading of a saved cryptogram
     * @param hep: the hotcode value which is used to load the original phrase from the phrases.txt file
     * @param ctoMap: The mapping of encrypted to original to original characters
     * @param otcMap: The mapping of original to encrypted charqcters
     * @param guesses: The mapping of the users guesses to the encrypted characters
     * @param frequencies: The mapping of the characters in the cryptogram to the frequency with which they appear
     * @throws FileNotFoundException :if the phrases.txt file cannot be found
     */
    public CryptoChar(int hep, HashMap<Character, Character> ctoMap, HashMap<Character, Character> otcMap,
                      HashMap<Character, Character> guesses, HashMap<Character, Integer> frequencies, HashMap<Character, Double> gFreq,
                      HashMap<Character,Double> engFreq) throws FileNotFoundException {
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
        generateEncryptedPhrase();
    }

    /**
     * Testing constructor for CryptoChar
     * This should not be called outside of test files.
     *
     * @param seed: The random generator seed you wish to use.
     * @param path: The directory path of the file you wish to load phrases from.
     *
     */
    public CryptoChar(int seed, String path) {
        phrasesPath = path;
        cryptoToOriginalMap = new HashMap<>();
        originalToCryptoMap = new HashMap<>();
        guessMap = new HashMap<>();
        freqMap = new HashMap<>();
        randomSeed = new Random(seed);
        hotEncodedPhrase = randomSeed.nextInt(19);
        encryptedFreqMap = new HashMap<>();
    }

    /**
     * generateEncryptedChar recursively generates random characters until a character that hasn't been mapped in the
     * encryption before is generated.
     */
    protected Character generateEncryptedChar() {
        char possibleCryptoChar = (char) (randomSeed.nextInt(26) + 'a');
        if (cryptoToOriginalMap.containsKey(possibleCryptoChar)) {
            return generateEncryptedChar();
        } else {
            return possibleCryptoChar;
        }
    }

    /**
     * generateCryptoMappings creates two HashMaps representing the conversion between a cryptogram letter and
     * the original letter. originalToCryptoMap will convert a given character from the original phrase into its
     * encrypted equivalent and cryptoToOriginalMap will convert an encrypted character into its original form.
     */
    protected void generateCryptoMappings() {
        for (Character currentChar : freqMap.keySet()) {
            char generatedChar = generateEncryptedChar();
            cryptoToOriginalMap.put(generatedChar, currentChar);
            originalToCryptoMap.put(currentChar, generatedChar);
        }
    }

    /**
     * generateEncryptedPhrase converts the entire phrase into its encrypted version.
     */
    protected void generateEncryptedPhrase() {
        encryptedPhrase = String.valueOf(originalToCryptoMap.get(Character.toLowerCase(phrase.charAt(0))));
        for (int i = 1; i < phrase.length(); i++) {
            if (exceptionList.indexOf(phrase.charAt(i)) != -1) {
                encryptedPhrase = encryptedPhrase + phrase.charAt(i);
            } else {
                encryptedPhrase = encryptedPhrase + originalToCryptoMap.get(Character.toLowerCase(phrase.charAt(i)));
            }
        }
    }

    /**
     * getOriginalChar handles the conversion of both upper and lower case letters when getting the original version of
     * an encrypted character.
     *
     * @return returns the original version of an encrypted Character
     */
    protected Character getEncryptedChar(Character originalChar) {
        boolean isUpperCase = false;
        if (Character.isUpperCase(originalChar)) {
            originalChar = Character.toLowerCase(originalChar);
            isUpperCase = true;
        }
        Character returnChar = cryptoToOriginalMap.get(originalChar);
        if (isUpperCase) {
            Character.toUpperCase(returnChar);
        }
        return returnChar;
    }

    /**
     * getOriginalChar handles the conversion of both upper and lower case letters when getting the encrypted version
     * of a given character.
     *
     * @return returns the encrypted version of a character.
     */
    protected Character getOriginalChar(Character cyptoChar) {
        boolean isUpperCase = false;
        if (Character.isUpperCase(cyptoChar)) {
            cyptoChar = Character.toLowerCase(cyptoChar);
            isUpperCase = true;
        }
        Character returnChar = originalToCryptoMap.get(cyptoChar);
        if (isUpperCase) {
            Character.toUpperCase(returnChar);
        }
        return returnChar;
    }

    /**
     * generateFreqMap will generate a frequency map of every character in the encrypted phrase.
     */
    protected void generateEncryptedFreqMap(){
        for(char currentChar : cryptoToOriginalMap.keySet()){
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
        for(char c : encryptedFreqMap.keySet()){
            double frequency = (double)encryptedFreqMap.get(c) / characterCount();
            frequency *= 100;
            frequency = Math.round(frequency * 100.0) / 100.0;
            gameFrequency.put(c , frequency);
        }
    }
}
