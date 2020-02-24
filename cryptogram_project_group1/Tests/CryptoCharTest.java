import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class CryptoCharTest {

    @Test
    void testCrytoCharCreates() throws FileNotFoundException {
        CryptoChar cryptoChar = new CryptoChar();
        assertNotNull(cryptoChar);
    }

    @Test
    void testCrytoCharCreatesWithParameter() throws FileNotFoundException {
        CryptoChar cryptoChar = new CryptoChar(42, "src\\phrases.txt");
        assertNotNull(cryptoChar);
    }

    @Test
    void testLoadPhrase() throws FileNotFoundException {
        CryptoChar cryptoChar = new CryptoChar(42, "src\\phrases.txt");
        cryptoChar.loadPhrase();
        Assertions.assertEquals("Answer to the Ultimate Question of Life, the Universe, and Everything.",
                cryptoChar.phrase);
    }

    @Test
    void testGenerateFreqMap() throws FileNotFoundException {
        CryptoChar cryptoChar = new CryptoChar(42, "src\\phrases.txt");
        cryptoChar.loadPhrase();
        cryptoChar.generateFreqMap();
        char[] keys = {'a','d','e','f','g','h','i','l','m','n','o','q','r','s','t','u','v','w','l'};
        int[] values = {3,1,10,2,1,3,5,2,1,5,3,1,3,3,7,3,2,1,2};
        for(int i = 0; i < keys.length; i++){
            Assertions.assertTrue(cryptoChar.freqMap.containsKey(keys[i]));
            Assertions.assertEquals(values[i], cryptoChar.freqMap.get(keys[i]));
        }
    }

    @Test
    void testGenerateEncryptedChar() throws FileNotFoundException {
        CryptoChar cryptoChar = new CryptoChar(42, "src\\phrases.txt");
        cryptoChar.loadPhrase();
        cryptoChar.generateFreqMap();
        Assertions.assertEquals('h', cryptoChar.generateEncryptedChar());
    }


    @Test
    void testGenerateCryptoMappings() throws FileNotFoundException {
        CryptoChar cryptoChar = new CryptoChar(42, "src\\phrases.txt");
        cryptoChar.loadPhrase();
        cryptoChar.generateFreqMap();
        cryptoChar.generateCryptoMappings();
        char[] original = {'a','d','e','f','g','h','i','l','m','n','o','q','r','s','t','u','v','w','l'};
        char[] crypto = {'h','w','m','a','r','n','q','d','p','i','g','u','e','l','z','o','v','t','d'};
        for(int i = 0; i < original.length; i++){
            Assertions.assertTrue(cryptoChar.originalToCryptoMap.containsKey(original[i]));
            Assertions.assertTrue(cryptoChar.cryptoToOriginalMap.containsKey(crypto[i]));
            Assertions.assertEquals(crypto[i], cryptoChar.originalToCryptoMap.get(original[i]));
            Assertions.assertEquals(original[i], cryptoChar.cryptoToOriginalMap.get(crypto[i]));
        }
    }

    @Test
    void testGenerateEncryptedPhrase() throws FileNotFoundException {
        CryptoChar cryptoChar = new CryptoChar(42, "src\\phrases.txt");
        cryptoChar.loadPhrase();
        cryptoChar.generateFreqMap();
        cryptoChar.generateCryptoMappings();
        cryptoChar.generateEncryptedPhrase();
        Assertions.assertEquals("hiltme zg znm odzqphzm uomlzqgi ga dqam, znm oiqvmelm, hiw mvmeyznqir.",
                cryptoChar.encryptedPhrase);
    }

    @Test
    void testGenerateCipher() throws FileNotFoundException {
        CryptoChar cryptoChar = new CryptoChar(42, "src\\phrases.txt");
        cryptoChar.loadPhrase();
        cryptoChar.generateCipher();
        cryptoChar.generateEncryptedPhrase();
        Assertions.assertEquals("hiltme zg znm odzqphzm uomlzqgi ga dqam, znm oiqvmelm, hiw mvmeyznqir.",
                cryptoChar.encryptedPhrase);
    }

    @Test
    void testGenerateNewCryptogram() throws FileNotFoundException {
        CryptoChar cryptoChar = new CryptoChar(42, "src\\phrases.txt");
        cryptoChar.generateNewCryptogram();
    }

    @Test
    void testThrowsFileNotFoundException() throws FileNotFoundException {
        CryptoChar cryptoChar = new CryptoChar(42, "someWrongFilePath.txt");
        assertThrows(FileNotFoundException.class,
                () -> cryptoChar.generateNewCryptogram());
    }

    @Test
    void testDoesNotAlwaysThrowFileNotFoundException() throws FileNotFoundException {
        CryptoChar cryptoChar = new CryptoChar(42, "src\\phrases.txt");
        assertDoesNotThrow(() -> cryptoChar.generateNewCryptogram());
    }
}
