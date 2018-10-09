package backend;

public class Block {
    private int ID;
    private int height;
    private int width;
    private boolean isHorizontal;

    public Block(int ID, int height, int width, boolean isHorizontal) {
        this.ID = ID;
        this.height = height;
        this.width = width;
        this.isHorizontal = isHorizontal;
    }

    public int getID() {
        return ID;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }
}

