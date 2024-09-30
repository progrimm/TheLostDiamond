package lostDiamond;

public abstract class AbstractBrick implements Brick {
    private boolean found = false;
    private final String color;
    
    public AbstractBrick(String color) {
        this.color = color;
    }
    public boolean getFoundStatus() {
        return found;
    }

    public void setFound() {
        this.found = true;
    }

    protected String getColor() {
        return color;
    }
}
