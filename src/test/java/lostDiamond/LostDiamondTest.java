package lostDiamond;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals; 

import org.junit.jupiter.api.Test;

public class LostDiamondTest {
    
    @Test
    public void testConstructor() {
        LostDiamond lostDiamond = new LostDiamond(null, null);

        assertTrue(lostDiamond.getPlayer1Turn());
        
        assertEquals(0, lostDiamond.getPlayer(1).getPosX());
        assertEquals(0, lostDiamond.getPlayer(1).getPosY());
        assertEquals(6, lostDiamond.getPlayer(2).getPosX());
        assertEquals(6, lostDiamond.getPlayer(2).getPosY());

        assertEquals(0, lostDiamond.getPlayer(1).getMoveCount());
    }

    @Test
    public void writeToFileTest() {
        LostDiamond lostDiamond = new LostDiamond("Jenny", "Amund");
        lostDiamond.movePlayer("10");

        //Hvis spiller1 finner gullmynt
        if (lostDiamond.getPlayer1Turn()) {
            lostDiamond.changePlayersturn();
        }
        lostDiamond.movePlayer("56");

        //Hvis spiller2 finner gullmynt
        if (!lostDiamond.getPlayer1Turn()) {
            lostDiamond.changePlayersturn();
        }

        lostDiamond.movePlayer("20");
        lostDiamond.setWinner(lostDiamond.getPlayer(1));

        FileManager fileManager = new FileManager();
        fileManager.writeToFile("history.txt", lostDiamond);

        List<String> lines = null;

        try {
            lines = fileManager.readFromFile("history.txt");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //Sjekker om øverste linje er det siste spillet
        assertTrue(lines.get(0).equals("Jenny(2),Amund(1),0"));
        //Sjekker at det kun er de 10 siste spillene som vises
        assertTrue(lines.size() <= 10);

    }


    @Test
    public void endGameTest() {
        LostDiamond lostDiamond = new LostDiamond("Jenny", "Amund");
        Player player1 = lostDiamond.getPlayer(1);
        Player player2 = lostDiamond.getPlayer(2);

        lostDiamond.endGame(player1);

        assertEquals(player2, lostDiamond.getLoser());

    }

    @Test
    public void getLegalMovesTest() {
        LostDiamond lostDiamond = new LostDiamond(null, null);
        List<String> expected = Arrays.asList("10", "01");
        List<String> output = lostDiamond.getLegalMoves();

        assertTrue(expected.equals(output));

        lostDiamond.movePlayer("10");
        //Må gjøre for å teste samme spiller
        lostDiamond.changePlayersturn();
        List<String> expectedAfterMove = Arrays.asList("20","00","11");
        List<String> outputAfterMove = lostDiamond.getLegalMoves();

        assertTrue(expectedAfterMove.equals(outputAfterMove));

    }

    @Test
    public void getPlayerTest() {
        LostDiamond lostDiamond = new LostDiamond(null, null);

        assertThrows(IllegalArgumentException.class, () -> lostDiamond.getPlayer(3));
    }

    @Test
    public void idtoIntsTest() {
        LostDiamond lostDiamond = new LostDiamond(null, null);

        assertThrows(IllegalArgumentException.class, () -> lostDiamond.idToInts("123"));

        int[] expected = {1, 2};
        int[] output = lostDiamond.idToInts("12");

        assertArrayEquals(expected, output);
        
    }

}
