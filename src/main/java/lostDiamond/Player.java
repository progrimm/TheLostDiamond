package lostDiamond;

public class Player implements Brick {
    
    private int moveCount = 0;
    private String name;
    private int posX;
    private int posY;
    private Brick brickUnder;
    private boolean hasDiamond = false;

    public Player(String name, int posX, int posY) {
        setName(name);
        this.posX = posX;
        this.posY = posY;
        this.brickUnder = new NeutralBrick();
        ((AbstractBrick) brickUnder).setFound();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public int getMoveCount() {
        return this.moveCount;
    }

    public void incrementMoveCount() {
        moveCount += 1;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosX(int x) {
        posX = x;
    }

    public void setPosY(int y) {
        posY = y;
    }

    public Brick getBrickUnder() {
        return brickUnder;
    }

    public void setBrickUnder(Brick b) {
        this.brickUnder = b;
    }

    public boolean getHasDiamond() {
        return hasDiamond;
    }

    public void setHasDiamond() {
        hasDiamond = true;
    }
}
