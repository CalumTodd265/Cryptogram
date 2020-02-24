import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.InvalidPropertiesFormatException;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    @Test
    void testPlayerProfileCreates() throws IOException, FileNotFoundException {
        File playerFile = new File("src\\Players\\test.txt");
        playerFile.delete();
        Player player = new Player("test");
    }

    @Test
    void testPlayerLoadsDetails() throws IOException, FileNotFoundException {
        File playerFile = new File("src\\Players\\test.txt");
        playerFile.delete();
        Player player = new Player("test");
    }


    @Test
    void testPlayerHandlesCorruptPlayerProfile() throws FileNotFoundException, IOException {
        File playerFile = new File("src\\Players\\test.txt");
        playerFile.delete();
        playerFile.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter("src\\Players\\test.txt"));
        writer.write("iguahudfhgoisudfhgoisdfg");
        writer.newLine();
        writer.close();

        Player player = new Player("test");
        assertThrows(InvalidPropertiesFormatException.class,
                () -> player.loadDetails());
        assertEquals(0, player.getNoAttemptedGuesses());
    }

    @Test
    void testPlayerHandlesLoadingNonExistantPlayer() throws FileNotFoundException, IOException {
        File playerFile = new File("src\\Players\\test.txt");
        playerFile.delete();

        Player player = new Player("test");
        assertThrows(InvalidPropertiesFormatException.class,
                () ->  player.loadDetails());
        assertEquals(0, player.getNoAttemptedGuesses());

    }


    @Test
    void testSavePlayerDetailsSaves() throws FileNotFoundException, IOException {
        File playerFile = new File("src\\Players\\test.txt");
        playerFile.delete();

        Player player = new Player("test");
        try {player.loadDetails();} catch (Exception e){};
        player.incrementAttemptedGuesses();
        player.saveDetails();

        Player theSamePlayer = new Player("test");
        theSamePlayer.loadDetails();
        assertEquals(player.getNoAttemptedGuesses(), theSamePlayer.getNoAttemptedGuesses());
    }
}