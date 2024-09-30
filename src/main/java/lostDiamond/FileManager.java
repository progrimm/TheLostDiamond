package lostDiamond;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileManager {
    
    public void writeToFile(String filename, LostDiamond game) {

        
        try {
            List<String> pastGames = readFromFile(filename);

            String winnerIndicator;
            if (game.getWinner() == game.getPlayer(1)) {
                winnerIndicator = "0";
            }
            else {
                winnerIndicator = "1";
            }

            PrintWriter writer = new PrintWriter(filename);
            writer.println(game.getPlayer(1).getName() + "(" + game.getPlayer(1).getMoveCount() + ")" + "," + game.getPlayer(2).getName() + "(" + game.getPlayer(2).getMoveCount() + ")" + "," + winnerIndicator);


            if (pastGames.size() > 9) {
                pastGames.remove(pastGames.size() - 1);
            }

            for (String str : pastGames) {
                writer.println(str);
            }

            writer.flush();
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public List<String> readFromFile(String filename) throws FileNotFoundException {

        Scanner scanner = new Scanner(new File(filename));
        List<String> games = new ArrayList<>();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            
            games.add(line);
        }

        scanner.close();
        return games;
    }
}
