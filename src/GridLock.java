import screen.GridLockFrame;

public class GridLock {

    private GridLockFrame gridLockFrame;

    public GridLock() {
        this.gridLockFrame = new GridLockFrame();
    }

    public static void main(String[] args) {
        GridLock gridLock = new GridLock();
        gridLock.start();
    }

    public void start() {
        gridLockFrame.setVisible();
    }
}
