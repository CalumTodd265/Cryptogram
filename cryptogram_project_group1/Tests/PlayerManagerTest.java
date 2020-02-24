import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import static org.junit.jupiter.api.Assertions.*;

class PlayerManagerTest {

    @Test
    void testCreateNewPlayer() throws IOException {
        PlayerManager playerManager = new PlayerManager();
        File player = new File("src\\Players\\testPlayer.txt");
        player.delete();
        playerManager.createNewPlayer("testPlayer");
        assertTrue(player.isFile());
    }

    @Test
    void testCreateNewPlayerThrowsException() throws IOException {
        PlayerManager playerManager = new PlayerManager();
        File player = new File("src\\Players\\testPlayer.txt");
        player.delete();
        playerManager.createNewPlayer("testPlayer");
        assertThrows(FileAlreadyExistsException.class,
                () -> playerManager.createNewPlayer("testPlayer"));
    }


    @Test
    void testLoadPlayer() throws FileNotFoundException, IOException {
        PlayerManager playerManager = new PlayerManager();
        try {playerManager.createNewPlayer("testPlayer");} catch(FileAlreadyExistsException e){}
        Player player = playerManager.loadPlayer("testPlayer");
        Assertions.assertEquals("testPlayer", player.getPlayerName());
    }

    @Test
    void testLoadPlayerThrowsFileNotFoundException() throws FileNotFoundException, IOException {
        PlayerManager playerManager = new PlayerManager();
        File player = new File("src\\Players\\nonExistentPlayer.txt");
        player.delete();
        assertThrows(FileNotFoundException.class,
                () -> playerManager.loadPlayer("nonExistentPlayer"));
    }
}
