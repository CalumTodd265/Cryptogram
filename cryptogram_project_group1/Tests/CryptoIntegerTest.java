import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class CryptoIntegerTest {
    @Test
    void testCrytoIntCreates() throws FileNotFoundException {
        CryptoInteger cryptoInt = new CryptoInteger();
        assertNotNull(cryptoInt);
    }

    @Test
    void testCrytoIntCreatesWithParameter() throws FileNotFoundException {
        CryptoInteger cryptoInt = new CryptoInteger(42, "src\\phrases.txt");
        assertNotNull(cryptoInt);
    }

    @Test
    void testLoadPhrase() throws FileNotFoundException {
        CryptoInteger cryptoInt = new CryptoInteger(42, "src\\phrases.txt");
        cryptoInt.loadPhrase();
        Assertions.assertEquals("Answer to the Ultimate Question of Life, the Universe, and Everything.",
                cryptoInt.phrase);
    }

    @Test
    void testGenerateFreqMap() throws FileNotFoundException {
        CryptoInteger cryptoInt= new CryptoInteger(42, "src\\phrases.txt");
        cryptoInt.loadPhrase();
        cryptoInt.generateFreqMap();
        char[] keys = {'a','d','e','f','g','h','i','l','m','n','o','q','r','s','t','u','v','w','l'};
        int[] values = {3,1,10,2,1,3,5,2,1,5,3,1,3,3,7,3,2,1,2};
        for(int i = 0; i < keys.length; i++){
            Assertions.assertTrue(cryptoInt.freqMap.containsKey(keys[i]));
            Assertions.assertEquals(values[i], cryptoInt.freqMap.get(keys[i]));
        }
    }

    @Test
    void testGenerateEncryptedInt() throws FileNotFoundException {
        CryptoInteger cryptoInt = new CryptoInteger(42, "src\\phrases.txt");
        cryptoInt.loadPhrase();
        cryptoInt.generateFreqMap();
        Assertions.assertEquals(7, cryptoInt.generateEncryptedChar());
    }


    @Test
    void testGenerateCryptoMappings() throws FileNotFoundException {
        CryptoInteger cryptoInt = new CryptoInteger(42, "src\\phrases.txt");
        cryptoInt.loadPhrase();
        cryptoInt.generateFreqMap();
        cryptoInt.generateCryptoMappings();
        char[] original = {'a','d','e','f','g','h','i','l','m','n','o','q','r','s','t','u','v','w','y'};
        int[] crypto = {7,22,12,0,17,13,16,3,15,8,6,20,4,11,25,14,21,19,24};
        for(int i = 0; i < original.length; i++){
            Assertions.assertTrue(cryptoInt.originalToCryptoMap.containsKey(original[i]));
            Assertions.assertTrue(cryptoInt.cryptoToOriginalMap.containsKey(crypto[i]));
            Assertions.assertEquals(crypto[i], cryptoInt.originalToCryptoMap.get(original[i]));
            Assertions.assertEquals(original[i], cryptoInt.cryptoToOriginalMap.get(crypto[i]));
        }
    }

    @Test
    void testGenerateNewCryptogram() throws FileNotFoundException {
        CryptoInteger cryptoInt= new CryptoInteger(42, "src\\phrases.txt");
        cryptoInt.generateNewCryptogram();
    }

    @Test
    void testThrowsFileNotFoundException() throws FileNotFoundException {
        CryptoInteger cryptoInt = new CryptoInteger(42, "someWrongFilePath.txt");
        assertThrows(FileNotFoundException.class,
                () -> cryptoInt.generateNewCryptogram());
    }

    @Test
    void testDoesNotAlwaysThrowFileNotFoundException() throws FileNotFoundException {
        CryptoInteger cryptoInt = new CryptoInteger(42, "src\\phrases.txt");
        assertDoesNotThrow(() -> cryptoInt.generateNewCryptogram());
    }

}