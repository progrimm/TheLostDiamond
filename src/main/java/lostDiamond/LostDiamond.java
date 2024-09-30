package lostDiamond;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LostDiamond {


    public static final int NEUTRAL_AMOUNT = 36;
    public static final int NEWMOVE_AMOUNT = 5;
    public static final int VISA_AMOUNT = 5;
    public static final int GRID_WIDTH = 7;
    public static final int GRID_HEIGHT = 7;

    private Player player1;
    private Player player2;
    private Player winner;
    private Player loser;
    private boolean player1Turn = true;
    private boolean gameOver;

    private Brick[][] gameGrid;

    public LostDiamond(String name1, String name2) {
        this.player1 = new Player(name1, 0, 0);
        this.player2 = new Player(name2, GRID_WIDTH - 1, GRID_HEIGHT - 1);
        this.gameGrid = new Brick[GRID_WIDTH][GRID_HEIGHT];
        setGameOver(false);
        generateRandomBoard();
    }

    public Brick getBrick(String id) {
        int x = idToInts(id)[0];
        int y = idToInts(id)[1];
        // Kloner for å bevare innkapsling
        Brick[][] gameGridCopy = Arrays.stream(gameGrid).map(Brick[]::clone).toArray(Brick[][]::new);
        return gameGridCopy[x][y];
    }

    public void setBrick(int x, int y, Brick brick) {
        gameGrid[x][y] = brick;
    }

    public int[] idToInts(String id) {
        if (id.length() != 2) {
            throw new IllegalArgumentException("Ugyldig id");
        }

        int[] res = new int[2];
        String[] xy = id.split("");
        res[0] = Integer.parseInt(xy[0]);
        res[1] = Integer.parseInt(xy[1]);
        return res;
    }

    public String movePlayer(String id_destination) {
        Player player = player1Turn ? getPlayer(1) : getPlayer(2);
        Brick oldBrickUnder = player.getBrickUnder();
        setBrick(player.getPosX(), player.getPosY(), oldBrickUnder);
        Brick newBrickUnder = getBrick(id_destination);
        player.setBrickUnder(newBrickUnder);
        setBrick(idToInts(id_destination)[0], idToInts(id_destination)[1], player);
        player.setPosX(idToInts(id_destination)[0]);
        player.setPosY(idToInts(id_destination)[1]);
        player.incrementMoveCount();
        changePlayersturn();

        // Powerup på noen av brikkene dersom de ikke er tatt fra før + melding
        if (((AbstractBrick) newBrickUnder).getFoundStatus() == false) {
            ((AbstractBrick) newBrickUnder).setFound();
            if (((AbstractBrick) newBrickUnder) instanceof NewMoveBrick) {
                changePlayersturn();
                return "" + player.getName() + " fant en gullmynt og får et ekstra flytt!";
            }

            if (((AbstractBrick) newBrickUnder) instanceof NeutralBrick) {
                return "Her var det ikke mye spennende for " + player.getName() + "...";
            }

            if (((AbstractBrick) newBrickUnder) instanceof Diamond) {
                player.setHasDiamond();
                return player.getName() + " fant diamanten!";
            }

            if (((AbstractBrick) newBrickUnder) instanceof VisaBrick) {
                Player opponentPlayer = player1Turn ? getPlayer(1) : getPlayer(2); // ??? det funker
                if (opponentPlayer.getHasDiamond() == true) {
                    player.setHasDiamond();
                    return player.getName() + " fant et visum, og kan vinne ved å komme først!";
                }
                return player.getName() + " fant et visum! Men det gjør ingenting...";
            }
        }

        // Sjekker om spillet er ferdig!
        if (checkGameOver(player) == true) {
            endGame(player);            
            return player.getName() + " kom først og vant. Klikk på teksten for å se historikk.";
        }
        return "";
    }

    public void endGame(Player player) {
        winner = player;
        loser = winner == player2 ? getPlayer(1) : getPlayer(2);

    }


    public boolean checkGameOver(Player player) {
        if (player.getHasDiamond() && ((player.getPosX() == 0 && player.getPosY() == 0) || (player.getPosX() == GRID_WIDTH-1 && player.getPosY() == GRID_HEIGHT-1))) {
            setGameOver(true);
            return true;
        }
        return false;
    }

    public boolean getGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean b) {
        gameOver = b;
    }


    public Player getPlayer(int i) {
        switch (i) {
            case 1:
                return this.player1;
            case 2:
                return this.player2;
            default:
                break;
        }
        throw new IllegalArgumentException("Only player 1 and player 2 exist");
    }

    public Player getWinner() {
        return this.winner;
    }

    //Trenger for testing
    public void setWinner(Player player) {
        this.winner = player;
    }

    public Player getLoser() {
        return this.loser;
    }

    public void changePlayersturn() {
        player1Turn = !player1Turn;
    }

    //Trenger for testing
    public boolean getPlayer1Turn() {
        return this.player1Turn;
    }

    // Lager et tilfeldig oppsett av brettet
    private void generateRandomBoard() {
        List<Brick> bricks = new ArrayList<>();
        for (int i = 0; i < NEUTRAL_AMOUNT; i++) {
            bricks.add(new NeutralBrick());
        }
        for (int i = 0; i < NEWMOVE_AMOUNT; i++) {
            bricks.add(new NewMoveBrick());
        }
        for (int i = 0; i < VISA_AMOUNT; i++) {
            bricks.add(new VisaBrick());
        }
        bricks.add(new Diamond());
        Collections.shuffle(bricks);
        gameGrid[0][0] = player1;

        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                if (x == 0 && y == 0 || x == GRID_WIDTH - 1 && y == GRID_HEIGHT - 1) {
                    continue;
                }
                gameGrid[x][y] = bricks.remove(0);
            }
        }
        gameGrid[GRID_WIDTH - 1][GRID_HEIGHT - 1] = player2;
    }

    public List<String> getLegalMoves() {
        Player player = player1Turn ? getPlayer(1) : getPlayer(2);
        int x = player.getPosX();
        int y = player.getPosY();
        List<String> possible_moves = new ArrayList<>(
                Arrays.asList("" + (x + 1) + y, "" + (x - 1) + y, "" + x + (y + 1), "" + x + (y - 1)));
        List<String> legal_moves = new ArrayList<>(possible_moves);

        // Sjekker om getBrick utløser error, dvs at det ligger utenfor brettet, eller
        // om det er motspilleren
        for (String id : possible_moves) {
            try {
                Brick brick = getBrick(id);
                if (brick instanceof Player) {
                    throw new Exception();
                }
            } catch (Exception e) {
                legal_moves.remove(id);
            }
        }
        return legal_moves;
    }
}
