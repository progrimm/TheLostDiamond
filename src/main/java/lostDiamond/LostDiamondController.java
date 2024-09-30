package lostDiamond;

import java.io.FileNotFoundException;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class LostDiamondController {

    // Henter ut fxml elementene vi skal bruke (kan gjøres finere tho)
    @FXML
    GridPane game_grid, historyTable;

    @FXML
    StackPane startPage, gamePage, historyPage;

    @FXML
    HBox bottom_hbox;

    @FXML
    Text score1, score2, message, wrongname;

    @FXML
    Button start_button;

    @FXML
    TextField player1_name_input, player2_name_input;


    private LostDiamond main_game;

    // Trigges når man trykker på playknappen
    @FXML
    public void onStartClick() {
        String name1 = player1_name_input.getText();
        String name2 = player2_name_input.getText();

        // Starter spillet dersom navnene er gyldige
        if ((isValidName(name1) && isValidName(name2)) && !name1.toLowerCase().equals(name2.toLowerCase())) {
            start_button.setDisable(true);
            startGame(name1, name2);
        } else {
            wrongname.setText("Ugyldig navn!");
        }
    }

    // Valideringsmetode
    public boolean isValidName(String name) {
        return ((name != null) && (!name.equals(""))
                && (name.matches("^[a-zA-Z]*$")));
    }

    // Metode for å starte spillet
    public void startGame(String name1, String name2) {
        startPage.setVisible(false);
        gamePage.setVisible(true);

        // Initialiserer spillklassen
        main_game = new LostDiamond(name1, name2);
        createBoard();
        score1.setText(main_game.getPlayer(1).getName());
        score2.setText(main_game.getPlayer(2).getName());

        showBricks();
        showLegalMoves();
    }

    // Fyller gamegrid med knapper
    private void createBoard() {
        int w = LostDiamond.GRID_WIDTH;
        int h = LostDiamond.GRID_HEIGHT;
        for (int x = 0; x < w; x++) {

            ColumnConstraints col = new ColumnConstraints(374 / w);
            col.setHalignment(HPos.CENTER);
            game_grid.getColumnConstraints().add(col);
            if (x < h) {
                game_grid.getRowConstraints().add(new RowConstraints(374 / h));
            }

            for (int y = 0; y < h; y++) {
                Button btn = new Button();
                btn.setId("" + x + y);
                // btn.setDisable(true);
                btn.setOnAction(evt -> {
                    onMoveClick(evt);
                });
                btn.setTextAlignment(TextAlignment.CENTER);
                btn.setStyle("-fx-background-color: grey");
                btn.setPrefSize(36, 36);
                btn.getStyleClass().add("brick");
                game_grid.add(btn, x, y);
            }
        }
        disableAllButtons();
    }

    private void disableAllButtons() {
        for (int x = 0; x < LostDiamond.GRID_WIDTH; x++) {
            for (int y = 0; y < LostDiamond.GRID_HEIGHT; y++) {
                Button button = (Button) game_grid.lookup("#" + x + y);
                button.setDisable(true);
            }
        }
    }

    private void showLegalMoves() {
        List<String> legalMoves = main_game.getLegalMoves(); // liste med id'ene til gyldige knapper
        for (String id : legalMoves) {
            Button button = (Button) game_grid.lookup("#" + id);
            button.setDisable(false);
        }
    }

    // Metode for å vise kun brikkene med status found + spillerne
    private void showBricks() {
        int w = LostDiamond.GRID_WIDTH;
        int h = LostDiamond.GRID_HEIGHT;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {

                String id = "" + x + y;
                Brick brick = main_game.getBrick(id);

                Player player1 = main_game.getPlayer(1);
                Player player2 = main_game.getPlayer(2);

                if (brick == player1) {
                    Button button = (Button) game_grid.lookup("#" + id);
                    button.setStyle("-fx-background-color: green");
                    button.setText("P1");
                    continue;
                }

                if (brick == player2) {
                    Button button = (Button) game_grid.lookup("#" + id);
                    button.setStyle("-fx-background-color: blue");
                    button.setText("P2");
                    continue;
                }

                if (((AbstractBrick) brick).getFoundStatus() == true) {
                    Button button = (Button) game_grid.lookup("#" + id);
                    button.setText("");
                    button.setStyle("-fx-background-color: " + ((AbstractBrick) brick).getColor());
                }
            }
        }
        disableAllButtons();
    }

    @FXML
    public void onMoveClick(ActionEvent evt) {
        Button clickedButton = (Button) evt.getTarget();
        String btn_id = clickedButton.getId();
        String msg = main_game.movePlayer(btn_id);
        message.setText(msg);
        showBricks();
        showLegalMoves();
        if (main_game.getGameOver() == true) {
            disableAllButtons();
            message.getStyleClass().add("clickable_message");
            message.setOnMouseClicked(e -> {
                gamePage.setVisible(false);
                historyPage.setVisible(true);
                showHistory();
            });
            FileManager fileManager = new FileManager();
            fileManager.writeToFile("history.txt", main_game);
        }
    }

    public void showHistory() {
        

        FileManager fileManager = new FileManager();

        try {
            List<String> games = fileManager.readFromFile("history.txt");
            for (int i = 0; i < games.size(); i++) {
                String[] gameRes = games.get(i).split(",");
                String winnerIndicator = gameRes[2];
                System.out.println(gameRes);
                for (int j = 0; j < 2; j++) {
                    Text resultat = new Text();
                    resultat.setText(gameRes[j]);
                    if (j == Integer.parseInt(winnerIndicator)) {
                        resultat.setStyle("-fx-fill: green; -fx-font-size: 16px;");
                    }
                    else {
                        resultat.setStyle("-fx-fill: red; -fx-font-size: 16px;");
                    }
                    historyTable.add(resultat, j, i+1);
                }
            }          
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
