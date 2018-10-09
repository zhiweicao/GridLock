package backend;

public class State implements Comparable<State> {
    private State parent;
    private int depth;
    private GameState currentGameState;
    private int fScore;

    public State(GameState curGameState,int depth,State parent) {
        this.currentGameState = curGameState;
        this.depth = depth;
        this.parent = parent;
    }
    /** Returns the state associated with this node. */
    public GameState getGameState() {
        return currentGameState;
    }

    /** Returns this node's parent node. */
    public State getParent() {
        return parent;
    }

    /** Returns the depth of this node. */
    public int getDepth() {
        return depth;
    }

    public void setfScore(int fScore) {
        this.fScore = fScore;
    }
    /**
     * Create a compare function to allow a comparator for the priorityQueue used in A* search algorithm
     */
    @Override
    public int compareTo(State compareState) {
        int compareCost = this.fScore - compareState.fScore;
        return compareCost;
    }

}
