package backend;

import java.util.*;

public class Board {

    private GameState currentGameState;
    private GameState originalGameState;
    private Stack<GameState> trackedGameState;
    private Solver solve;
    private AI computer;
    private Integer minMoves;
    private MapGenerator generator;

    /**
     * Constructor for Board.
     */
    public Board() {
        this.trackedGameState = new Stack<>();
        this.generator = new MapGenerator();
        this.originalGameState = new GameState();
        this.currentGameState = new GameState();
        this.solve = new Solver();
        this.computer = new AI(solve);
        this.minMoves = 0;
        this.originalGameState.setEmptyMap(6);
        this.currentGameState.setEmptyMap(6);
    }

    /*

        Getters and Setters

    */

    /**
     * Retrieves the upper (bottom-most or right-most for this block, depending on plane of movement)
     * boundary of movement for this block. Returns the boundary as an integer representing the
     * uppermost column or row coordinate this block may move to. Coordinates start from 0 in top-left
     *
     * @param col column coordinate of the block to retrieve bounds for.
     * @param row row coordinate of the block to retrieve bounds for.
     * @return uppermost position (col or row coordinate) this block may move to.
     */
    public int getUpperBound(int col, int row) {
        return currentGameState.getUpperBound(col, row);
    }

    /**
     * Retrieves the lower (top-most or leftmost for this block, depending on plane of movement)
     * boundary of movement for this block. Returns the boundary as an integer representing the
     * uppermost column or row coordinate this block may move to. Coordinates start from 0 in top
     * left
     *
     * @param col column coordinate of the block to retrieve bounds for.
     * @param row row coordinate of the block to retrieve bounds for.
     * @return lowermost position (col or row coordinate) this block may move to.
     */
    public int getLowerBound(int col, int row) {
        return currentGameState.getLowerBound(col, row);
    }

    /**
     * Retrieves the horizontal nature of  the specific block located at location row and col
     * @param row y coordinate of the block, starting from 0 at the top
     * @param col x coordinate of the block, starting from 0 at the left
     * @return true if block is horizontal or false
     */
    public boolean getBlockHorizontal(int row, int col) { return currentGameState.getBlockHorizontal(row, col); }

    /**
     * trackGameState
     * assists in adding the newGameState to the tracking list
     * removes the oldest element if there are more than 10 moves. Change for difficulty?
     * @param newGameState
     */
    private void trackGameState(GameState newGameState) {
        this.trackedGameState.push(newGameState);
    }

    /**
     * Retrieves the block length and from the specific block located at location row and col
     * @pre Assuming the row and col is not NULL
     * @param row y coordinate of the block, starting from 0 at the top
     * @param col x coordinate of the block, starting from 0 at the left
     * @return the specific block length at the the location x and y
     */
    public int getBlockLength(int row, int col) {
        return currentGameState.getBlockLength(row, col);
    }

    /**
     * Retrieves the dimensions of the currentGameState
     * @return dimensions of the board
     */
    public int getDimensions() {
        return currentGameState.getDimensions();
    }

    /**
     * Checks to see if the game has been won yet. If the player is in the victory position it will
     * return true, otherwise the player has not won yet and will return false;
     *
     * @return true if player has won, false if not.
     */
    public boolean validWin() {
        return currentGameState.validWin();
    }

    /**
     * validBlock
     * Checks to see if the location holds a block
     * @param row
     * @param col
     * @return true if block occupied, false if empty
     */
    public boolean validBlock(int row, int col) {
        return currentGameState.validBlock(row,col);
    }

    /**
     * @return number of moves made
     */
    public int moveCounter() {
        return trackedGameState.size();
    }

    /**
     * getMinMoves
     * @return int mininum moves to solve board
     */
    public int getMinMoves() {
        return this.minMoves;
    }

    /*

        Logic & Functions

     */

    /**
     * Will move a block that exists at the given x and y co-ordinates (in board spaces) in the
     * given direction and distance. The direction is passed as an int where: 0 = up, 1 = right,
     * 2 = down, 3 = left.
     *
     * @param srcCol x co-location of the initial block in square spaces on the board
     * @param srcRow y co-location of the initial block in square spaces on the board
     * @param desCol x co-location of the final block in the square spaces on the board
     * @param desRow y co-location of the final block in the square spaces on the board
     */
    public boolean moveBlock(int srcRow, int srcCol, int desRow, int desCol) {
        if (srcCol == desCol && desRow == srcRow) return false;
        GameState newGameState = new GameState();
        boolean isHorizontal = false;
        int distance = 0;
        int direction = 0;
        if (srcRow == desRow) isHorizontal = true;
        if (isHorizontal) {
            if (srcCol > desCol) {
                distance = srcCol - desCol;
                direction = 3;
            } else {
                distance = desCol - srcCol;
                direction = 1;
            }
        } else {
            if (srcRow > desRow) {
                distance = srcRow - desRow;
                direction = 0;
            } else {
                distance = desRow - srcRow;
                direction = 2;
            }
        }

        newGameState = newGameState.cloneGameState(this.currentGameState);
        newGameState.moveBlock(srcRow, srcCol, direction, distance);
        trackGameState(currentGameState);
        currentGameState = newGameState;
        return true;
    }

    /**
     * AIMoveBlock
     * Performs the AI's move
     */
    public void AIMoveBlock() {
        currentGameState = this.computer.makeMove(currentGameState, trackedGameState.peek());
    }

    /**
     * Checks is the block at given column and row coordinates has already been drawn, and if not it will
     * set it as having been drawn. If passed the coordinate of the very bottom right block, after
     * the usual check it will wipe the drawn status of all blocks, ready to be drawn in again.
     *
     * @param col column coordinate of the block, starting from 0 at the left.
     * @param row row coordinate of the block, starting from 0 at the top.
     * @return true if the block has already been drawn, false if not.
     */
    public boolean isSeen(int row, int col) {
        return currentGameState.isSeen(col, row);
    }
    
    /**
     * Checks if the block at the given coordinates is the player.
     *
     * @param row the row of the block to check, starting from 0 at the top.
     * @param col The column of the block to check, starting from 0 on the left.
     * @return True if it's the player, false otherwise.
     */
    public boolean isPlayer(int row, int col) {
        return currentGameState.isPlayer(row, col);
    }

    /**
     * Updates GameStates on Board to perform Undo Action
     */
    public boolean doUndo() {
        if (trackedGameState.empty()) {
            return false;
        } else {
            GameState oldGameState = trackedGameState.pop();
            currentGameState = oldGameState;
            return true;
        }
    }

    /**
     * resets the board completely to the original initial map
     */
    public void resetBoard() {
        trackedGameState.clear();
        this.currentGameState = originalGameState.cloneGameState(originalGameState);
        solveMinMoves();
    }

    /**
     * Loads predefined maps for 1st stage of the Tutorial
     */
    public void loadTutorialMapIntro() {
        String intro = "maps/tutorial1.txt";
        this.currentGameState.readTutorialMap(intro);
    }

    /**
     * Loads predefined maps for 2nd stage of the Tutorial
     */
    public void loadTutorialMapEnd() {
        String intro = "maps/tutorial2.txt";
        this.currentGameState.readTutorialMap(intro);
    }

    /**
     * doHint
     * modifies the current Board instance to the next most optimal move
     * @return bool true/false
     */
    public boolean doHint() {
        Solver toSolve = new Solver();
        toSolve.gridSolver(this.currentGameState);
        ArrayList<GameState> solvedPath = toSolve.getPath();
        if (solvedPath.size() > 1) {
            this.currentGameState = solvedPath.get(1);
            this.trackedGameState.push(solvedPath.get(1));
            return true;
        } else {
            return false;
        }
    }

    /**
     * solveMinMoves
     * finds the minimum amount of moves for the board
     */
    public void solveMinMoves() {
        Solver toSolve = new Solver();
        toSolve.gridSolver(this.currentGameState);
        int size = toSolve.getPath().size();
        this.minMoves = size-1;
    }


    /**
     * Sets the map of this board to be a newly random generated map if the given difficulty
     * @param difficulty
     */
    public void setNewMap(int difficulty) {
        trackedGameState.clear();
        currentGameState = generator.createRandomMap(difficulty, 6);
        originalGameState = currentGameState.cloneGameState(currentGameState);
        solveMinMoves();
    }

}
