import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;

import static org.junit.jupiter.api.Assertions.*;

class GameFunctionalityTest {
    @Test
    void testNewLetterGame() throws FileNotFoundException, IOException {
        GameFunctionality gameFunc = new GameFunctionality();

        File playerFile = new File("src\\Players\\test.txt");
        playerFile.delete();
        try { gameFunc.createNewPlayer("test");} catch (Exception e){};
        gameFunc.loadPlayer("test");

        gameFunc.newLetterGame(42, "src\\phrases.txt");
        Assertions.assertEquals("Answer to the Ultimate Question of Life, the Universe, and Everything.",
                gameFunc.cryptogram.phrase);
        char[] original = {'a','d','e','f','g','h','i','l','m','n','o','q','r','s','t','u','v','w','y'};
        char[] crypto = {'h','w','m','a','r','n','q','d','p','i','g','u','e','l','z','o','v','t','y'};
        for(int i = 0; i < original.length; i++){
            Assertions.assertTrue(gameFunc.cryptogram.originalToCryptoMap.containsKey(original[i]));
            Assertions.assertTrue(gameFunc.cryptogram.cryptoToOriginalMap.containsKey(crypto[i]));
            Assertions.assertEquals(crypto[i], gameFunc.cryptogram.originalToCryptoMap.get(original[i]));
            Assertions.assertEquals(original[i], gameFunc.cryptogram.cryptoToOriginalMap.get(crypto[i]));
        }
    }

    @Test
    void testNewIntegerGame() throws FileNotFoundException, IOException {
        GameFunctionality gameFunc = new GameFunctionality();
        try{ gameFunc.createNewPlayer("test"); } catch (FileAlreadyExistsException e){}
        gameFunc.loadPlayer("test");

        gameFunc.newIntegerGame(42, "src\\phrases.txt");
        Assertions.assertEquals("Answer to the Ultimate Question of Life, the Universe, and Everything.",
                gameFunc.cryptogram.phrase);
        char[] original = {'a','d','e','f','g','h','i','l','m','n','o','q','r','s','t','u','v','w','y'};
        int[] crypto = {7,22,12,0,17,13,16,3,15,8,6,20,4,11,25,14,21,19,24};
        for(int i = 0; i < original.length; i++){
            Assertions.assertTrue(gameFunc.cryptogram.originalToCryptoMap.containsKey(original[i]));
            Assertions.assertTrue(gameFunc.cryptogram.cryptoToOriginalMap.containsKey(crypto[i]));
            Assertions.assertEquals(crypto[i], gameFunc.cryptogram.originalToCryptoMap.get(original[i]));
            Assertions.assertEquals(original[i], gameFunc.cryptogram.cryptoToOriginalMap.get(crypto[i]));
        }
    }

    @Test
    void testNewLetterGameHandlesException() {
        GameFunctionality gameFunc = new GameFunctionality();
        assertThrows(FileNotFoundException.class,
                () -> gameFunc.newLetterGame(42, "someWrongPath"));
    }

    @Test
    void testNewIntegerGameHandlesException() {
        GameFunctionality gameFunc = new GameFunctionality();
        assertThrows(FileNotFoundException.class,
                () -> gameFunc.newIntegerGame(42, "someWrongPath"));
    }

    @Test
    void testPlayerCanMakeGuess() throws FileNotFoundException, IOException {
        GameFunctionality gameFunc = new GameFunctionality();
        try{ gameFunc.createNewPlayer("test"); } catch (FileAlreadyExistsException e){}
        gameFunc.loadPlayer("test");

        gameFunc.newIntegerGame(42, "src\\phrases.txt");
        gameFunc.makeGuess(0,'z', true);
        Assertions.assertEquals('z', gameFunc.cryptogram.guessMap.get(0));

        gameFunc.newLetterGame(42, "src\\phrases.txt");
        gameFunc.makeGuess('a','z', false);
        Assertions.assertEquals('z', gameFunc.cryptogram.guessMap.get('a'));
    }

    @Test
    void testPromptPlayerToConfirmGuessRewrite() throws FileNotFoundException, IOException {
        GameFunctionality gameFunc = new GameFunctionality();
        try{ gameFunc.createNewPlayer("test"); } catch (FileAlreadyExistsException e){}
        gameFunc.loadPlayer("test");

        gameFunc.newIntegerGame(42, "src\\phrases.txt");
        gameFunc.makeGuess(0,'z', false);
        Assertions.assertEquals(-2, gameFunc.makeGuess(0,'y', false));

        gameFunc.newLetterGame(42, "src\\phrases.txt");
        gameFunc.makeGuess('a','z', false);
        Assertions.assertEquals(-2, gameFunc.makeGuess('a','y', false));
    }

    @Test
    void testPlayerConfirmsGuessRewrite() throws FileNotFoundException, IOException {
        GameFunctionality gameFunc = new GameFunctionality();
        try{ gameFunc.createNewPlayer("test"); } catch (FileAlreadyExistsException e){}
        gameFunc.loadPlayer("test");

        gameFunc.newIntegerGame(42, "src\\phrases.txt");
        gameFunc.makeGuess(0,'z', false);
        Assertions.assertEquals(0, gameFunc.makeGuess(0,'y', true));

        gameFunc.newLetterGame(42, "src\\phrases.txt");
        gameFunc.makeGuess('a','z', false);
        Assertions.assertEquals(0, gameFunc.makeGuess('a','y', true));
    }

    @Test
    void testPlayerTriesToEnterAlreadyMapped() throws FileNotFoundException, IOException {
        GameFunctionality gameFunc = new GameFunctionality();
        try{ gameFunc.createNewPlayer("test"); } catch (FileAlreadyExistsException e){}
        gameFunc.loadPlayer("test");

        gameFunc.newIntegerGame(42, "src\\phrases.txt");
        gameFunc.makeGuess(0,'z', false);
        Assertions.assertEquals(-3, gameFunc.makeGuess(24,'z', false));

        gameFunc.newLetterGame(42, "src\\phrases.txt");
        gameFunc.makeGuess('a','z', false);
        Assertions.assertEquals(-3, gameFunc.makeGuess('y','z', false));
    }

    @Test
    void testPlayerTriesToLetterNotInCryptogram() throws FileNotFoundException, IOException {
        GameFunctionality gameFunc = new GameFunctionality();
        try{ gameFunc.createNewPlayer("test"); } catch (FileAlreadyExistsException e){}
        gameFunc.loadPlayer("test");

        gameFunc.newIntegerGame(42, "src\\phrases.txt");
        Assertions.assertEquals(-1, gameFunc.makeGuess(1,'z', false));

        gameFunc.newLetterGame(42, "src\\phrases.txt");
        Assertions.assertEquals(-1, gameFunc.makeGuess('b','z', false));
    }

    @Test
    void testPlayerEntersLastValueAndIsCorrect() throws FileNotFoundException, IOException {
        GameFunctionality gameFunc = new GameFunctionality();
        try{ gameFunc.createNewPlayer("test"); } catch (FileAlreadyExistsException e){}
        gameFunc.loadPlayer("test");

        gameFunc.newIntegerGame(42, "src\\phrases.txt");
        char[] original = {'a','d','e','f','g','h','i','l','m','n','o','q','r','s','t','u','v','w'};
        int[] crypto = {7,22,12,0,17,13,16,3,15,8,6,20,4,11,25,14,21,19};
        for(int i = 0; i < original.length; i++){
            gameFunc.makeGuess(crypto[i], original[i], false);
        }
        Assertions.assertEquals(2, gameFunc.makeGuess(24, 'y', false));

        gameFunc.newLetterGame(42, "src\\phrases.txt");
        char[] charCrypto = {'h','w','m','a','r','n','q','d','p','i','g','u','e','l','z','o','v','t'};
        for(int i = 0; i < original.length; i++){
            gameFunc.makeGuess(charCrypto[i], original[i], false);
        }
        Assertions.assertEquals(2, gameFunc.makeGuess('y', 'y', false));
    }

    @Test
    void testPlayerEntersLastValueAndIsIncorrect() throws FileNotFoundException, IOException {
        GameFunctionality gameFunc = new GameFunctionality();
        try{ gameFunc.createNewPlayer("test"); } catch (FileAlreadyExistsException e){}
        gameFunc.loadPlayer("test");

        gameFunc.newIntegerGame(42, "src\\phrases.txt");
        char[] original = {'a','d','e','f','g','h','i','l','m','n','o','q','r','s','t','u','v','w'};
        int[] intCrypto = {7,22,12,0,17,13,16,3,15,8,6,20,4,11,25,14,21,19};
        for(int i = 0; i < original.length; i++){
            gameFunc.makeGuess(intCrypto[i], original[i], false);
        }
       Assertions.assertEquals(1, gameFunc.makeGuess(24, 'z', false));

        gameFunc.newLetterGame(42, "src\\phrases.txt");
        char[] charCrypto = {'h','w','m','a','r','n','q','d','p','i','g','u','e','l','z','o','v','t'};
        for(int i = 0; i < original.length; i++){
            gameFunc.makeGuess(charCrypto[i], original[i], false);
        }
        Assertions.assertEquals(1, gameFunc.makeGuess('y', 'z', false));
    }

    @Test
    void testUndoMappedLetter() throws FileNotFoundException, IOException {
        GameFunctionality gameFunc = new GameFunctionality();
        try{ gameFunc.createNewPlayer("test"); } catch (FileAlreadyExistsException e){}
        gameFunc.loadPlayer("test");

        gameFunc.newIntegerGame(42, "src\\phrases.txt");
        gameFunc.makeGuess(0,'z', false);
        Assertions.assertEquals('z', gameFunc.cryptogram.guessMap.get(0));
        Assertions.assertEquals(1, gameFunc.undo(0));
        assertNull(gameFunc.cryptogram.guessMap.get(0));

        gameFunc.newLetterGame(42, "src\\phrases.txt");
        gameFunc.makeGuess('a','z', false);
        Assertions.assertEquals('z', gameFunc.cryptogram.guessMap.get('a'));
        Assertions.assertEquals(1, gameFunc.undo('a'));
        assertNull(gameFunc.cryptogram.guessMap.get('a'));

    }

    @Test
    void testUndoUnmappedLetter() throws FileNotFoundException, IOException {
        GameFunctionality gameFunc = new GameFunctionality();
        try{ gameFunc.createNewPlayer("test"); } catch (FileAlreadyExistsException e){}
        gameFunc.loadPlayer("test");

        gameFunc.newIntegerGame(42, "src\\phrases.txt");
        Assertions.assertEquals(-1, gameFunc.undo(0));

        gameFunc.newLetterGame(42, "src\\phrases.txt");
        Assertions.assertEquals(-1, gameFunc.undo('a'));
    }

    @Test
    void testUndoLetterNotInCryptogram() throws FileNotFoundException , IOException{
        GameFunctionality gameFunc = new GameFunctionality();
        try{ gameFunc.createNewPlayer("test"); } catch (FileAlreadyExistsException e){}
        gameFunc.loadPlayer("test");

        gameFunc.newIntegerGame(42, "src\\phrases.txt");
        Assertions.assertEquals(0, gameFunc.undo(26));


        gameFunc.newLetterGame(42, "src\\phrases.txt");
        Assertions.assertEquals(0, gameFunc.undo('A'));
    }

    @Test
    void testCryptogramsSuccessfullyCompletedIncrementsUponSuccess() throws IOException, FileNotFoundException {
        GameFunctionality gameFunc = new GameFunctionality();
        File playerFile = new File("src\\Players\\test.txt");
        playerFile.delete();
        try{ gameFunc.createNewPlayer("test"); } catch (FileAlreadyExistsException e){}
        gameFunc.loadPlayer("test");

        assertEquals(0, gameFunc.currentPlayer.getNoCompletedCryptos());
        gameFunc.newIntegerGame(42, "src\\phrases.txt");
        char[] original = {'a','d','e','f','g','h','i','l','m','n','o','q','r','s','t','u','v','w'};
        int[] crypto = {7,22,12,0,17,13,16,3,15,8,6,20,4,11,25,14,21,19};
        for(int i = 0; i < original.length; i++){
            gameFunc.makeGuess(crypto[i], original[i], false);
        }
        assertEquals(2, gameFunc.makeGuess(24, 'y', false));
        assertEquals(1, gameFunc.currentPlayer.getNoCompletedCryptos());
    }

    @Test
    void testCryptogramsSuccessfullyCompletedDoesNotIncrementOnFailure() throws IOException, FileNotFoundException {
        GameFunctionality gameFunc = new GameFunctionality();
        File playerFile = new File("src\\Players\\test.txt");
        playerFile.delete();
        try{ gameFunc.createNewPlayer("test"); } catch (FileAlreadyExistsException e){}
        gameFunc.loadPlayer("test");

        assertEquals(0, gameFunc.currentPlayer.getNoCompletedCryptos());
        gameFunc.newIntegerGame(42, "src\\phrases.txt");
        char[] original = {'a','d','e','f','g','h','i','l','m','n','o','q','r','s','t','u','v','w'};
        int[] crypto = {7,22,12,0,17,13,16,3,15,8,6,20,4,11,25,14,21,26};
        for(int i = 0; i < original.length; i++){
            gameFunc.makeGuess(crypto[i], original[i], false);
        }
        assertEquals(0, gameFunc.makeGuess(24, 'y', false));
        assertEquals(0, gameFunc.currentPlayer.getNoCompletedCryptos());
    }


    @Test
    void testCryptogramsPlayedIncrementsWhenNewCryptogramPlayed() throws IOException, FileNotFoundException {
        GameFunctionality gameFunc = new GameFunctionality();
        File playerFile = new File("src\\Players\\test.txt");
        playerFile.delete();
        try{ gameFunc.createNewPlayer("test"); } catch (FileAlreadyExistsException e){}
        gameFunc.loadPlayer("test");

        assertEquals(0, gameFunc.currentPlayer.getNoPlayedCryptos());
        gameFunc.newIntegerGame(42, "src\\phrases.txt");
        assertEquals(1, gameFunc.currentPlayer.getNoPlayedCryptos());
    }

    @Test
    void testCryptogramLoadedDoesNotIncrementCryptogramsPlayed() throws IOException, FileNotFoundException {
        GameFunctionality gameFunc = new GameFunctionality();
        File playerFile = new File("src\\Players\\test.txt");
        playerFile.delete();
        try{ gameFunc.createNewPlayer("test"); } catch (FileAlreadyExistsException e){}
        gameFunc.loadPlayer("test");

        assertEquals(0, gameFunc.currentPlayer.getNoPlayedCryptos());
        gameFunc.newIntegerGame(42, "src\\phrases.txt");
        assertEquals(1, gameFunc.currentPlayer.getNoPlayedCryptos());
        gameFunc.saveCryptogram();
        gameFunc.loadCryptogram();
        assertEquals(1, gameFunc.currentPlayer.getNoPlayedCryptos());
    }

    @Test
    void testSavedGameIsRecordedInPlayerStats() throws FileNotFoundException, IOException {
        GameFunctionality gameFunc = new GameFunctionality();
        File playerFile = new File("src\\Players\\test.txt");
        playerFile.delete();
        try{ gameFunc.createNewPlayer("test"); } catch (FileAlreadyExistsException e){}
        gameFunc.loadPlayer("test");

        gameFunc.newIntegerGame(42, "src\\phrases.txt");
        assertEquals(false, gameFunc.currentPlayer.getSaveLoaded());
        gameFunc.saveCryptogram();
        assertEquals(true, gameFunc.currentPlayer.getSaveLoaded());
    }

    @Test
    void testLoadCryptogramLoads() throws FileNotFoundException, IOException {
        GameFunctionality gameFunc = new GameFunctionality();
        File playerFile = new File("src\\Players\\test.txt");
        playerFile.delete();
        try{ gameFunc.createNewPlayer("test"); } catch (FileAlreadyExistsException e){}
        gameFunc.loadPlayer("test");

        assertEquals(0, gameFunc.currentPlayer.getNoCompletedCryptos());
        gameFunc.newIntegerGame(42, "src\\phrases.txt");
        char[] original = {'a','d','e','f','g','h','i','l','m','n','o','q','r','s','t','u','v','w'};
        int[] crypto = {7,22,12,0,17,13,16,3,15,8,6,20,4,11,25,14,21,19};
        for(int i = 0; i < original.length; i++){
            gameFunc.makeGuess(crypto[i], original[i], false);
        }
        gameFunc.saveCryptogram();
        gameFunc.newIntegerGame();
        gameFunc.loadCryptogram();
        assertEquals(2, gameFunc.makeGuess(24, 'y', false));
        assertEquals(1, gameFunc.currentPlayer.getNoCompletedCryptos());
    }


    @Test
    void testLoadCryptogramThrowsErrorIfNoFileFound() throws FileNotFoundException, IOException {
        GameFunctionality gameFunc = new GameFunctionality();
        File playerFile = new File("src\\Players\\test.txt");
        playerFile.delete();
        try{ gameFunc.createNewPlayer("test"); } catch (FileAlreadyExistsException e){}
        gameFunc.loadPlayer("test");

        File saveFile = new File("src\\Saves\\test.txt");
        saveFile.delete();
        assertThrows(FileNotFoundException.class,
                () -> gameFunc.loadCryptogram());
    }


    @Test
    void testLoadFileThrowsErrorIfFileCorrupted() throws FileNotFoundException, IOException {
        GameFunctionality gameFunc = new GameFunctionality();
        File playerFile = new File("src\\Players\\test.txt");
        playerFile.delete();
        try{ gameFunc.createNewPlayer("test"); } catch (FileAlreadyExistsException e){}
        gameFunc.loadPlayer("test");

        gameFunc.newIntegerGame(42, "src\\phrases.txt");
        assertEquals(false, gameFunc.currentPlayer.getSaveLoaded());
        gameFunc.saveCryptogram();

        BufferedWriter writer = new BufferedWriter(new FileWriter("src\\Saves\\test.txt"));
        writer.write("ThisIsUnreadableGarbage||iuawehgof83wyufpa8w3ufo9aw3faw3\ndsad");
        writer.close();
        assertThrows(InvalidPropertiesFormatException.class,
                () -> gameFunc.loadCryptogram());
    }

    @Test
    void testGeneratesHint() throws IOException{
        GameFunctionality gameFunc = new GameFunctionality();
        File playerFile = new File("src\\Players\\test.txt");
        playerFile.delete();
        try{ gameFunc.createNewPlayer("test"); } catch (FileAlreadyExistsException e){}
        gameFunc.loadPlayer("test");
        gameFunc.newIntegerGame(42, "src\\phrases.txt");
        assertEquals(0, gameFunc.getHint());
        assertEquals(gameFunc.cryptogram.guessMap.get(gameFunc.getHintChar()), gameFunc.cryptogram.cryptoToOriginalMap.get(gameFunc.getHintChar()));
    }
/*
    @Test
    void testGeneratedHintOverwritesUserGuess() throws IOException{
        GameFunctionality gameFunc = new GameFunctionality();
        File playerFile = new File("src\\Players\\test.txt");
        playerFile.delete();
        try{ gameFunc.createNewPlayer("test"); } catch (FileAlreadyExistsException e){}
        gameFunc.loadPlayer("test");
        gameFunc.newIntegerGame(42, "src\\phrases.txt");
        char[] original = {'a','d','e','f','g','h','i','l','m','n','o','q','r','s','t','u','v','w'};
        int[] crypto = {7,22,12,0,17,13,16,3,15,8,6,20,4,11,25,14,21,19};
        for(int i = 0; i < original.length; i++){
            gameFunc.makeGuess(crypto[i], original[i], false);
        }
        assertEquals(1, gameFunc.getHint());
        assertEquals(gameFunc.cryptogram.guessMap.get(gameFunc.getHintChar()), gameFunc.cryptogram.cryptoToOriginalMap.get(gameFunc.getHintChar()));
    }
*/
}