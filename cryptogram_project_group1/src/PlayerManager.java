import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;

public class PlayerManager {

    /***
     * the standard constructor for PlayerManager, takes no arguments and performs no action
     */
    public PlayerManager(){

    }

    /***
     * Will check if the player exists, if not a new save file will be created for them
     * @param playername: the name which the file will have upon completion of the method
     * @throws IOException: if something goes wrong with the file creation
     * @throws FileAlreadyExistsException: if the player already exists and has data stored
     */
    protected void createNewPlayer(String playername) throws IOException, FileAlreadyExistsException {
        File player = new File("src\\Players\\"+playername+".txt");
        if (player.isFile()) {
            throw new FileAlreadyExistsException("This player already exists.");
        }
        player.createNewFile();
        Player currentPlayer = new Player(playername);
    }

    /***
     *
     * @param playername: the name of the player who's data is to be loaded
     * @return: the player object with the information of the loaded player
     * @throws FileNotFoundException: if the player does not have information saved
     * @throws IOException: if something goes wrong with file reading
     */
    protected Player loadPlayer(String playername) throws FileNotFoundException, IOException{
        File playerFile = new File("src\\Players\\"+playername+".txt");
        if (playerFile.isFile()) {
            Player currentPlayer = new Player(playername);
            return currentPlayer;
        }
        throw new FileNotFoundException("This player does not exist.");
    }


    private int findInsertLocation(double x, ArrayList<Double> list){
        for(int i = 0; i < list.size(); i++){
            if(x >= list.get(i)){
                list.add(i,x);
                return i;
            }
        }
        list.add(0, x);
        return 0;
    }

    private String removeSuffix(String fileName){
        int pos = fileName.lastIndexOf(".");
        return fileName.substring(0, pos);
    }

    protected ArrayList<String> getLeaderBoard() throws IOException {
        ArrayList<String> leaderBoard = new ArrayList<String>();
        ArrayList<Double> count = new ArrayList<Double>();
        File folder = new File("src\\Players");
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                Player player = new Player(removeSuffix(file.getName()));
                try {player.loadDetails();} catch(Exception e){};
                double score = player.getPercentCorrect();
                int i = findInsertLocation(score, count);
                leaderBoard.add(i, (player.getPlayerName() + ": " + String.valueOf(score)));
            }
        }
      return leaderBoard;
    }

    public static void main(String[] args) throws Exception {
        PlayerManager pm = new PlayerManager();
        System.out.println(pm.getLeaderBoard());
    }
}
