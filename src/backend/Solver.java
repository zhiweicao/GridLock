package backend;

import java.util.*;

public class Solver {

    private HashMap<String, GameState> open = new HashMap<>();
    private ArrayList<GameState> path;
    private ArrayList<Block> drawnBlocks;


    public Solver () {

        drawnBlocks = new ArrayList<>();
        path = new ArrayList<>();
    }


    public ArrayList<GameState> getPath () {
        return path;
    }

    /**
     * Solves the puzzle of the grid, with the current GameState of the board. Finds the minimum amount of moves via A*
     * search algorithm, with each node representing the possible moves a Gamestate could have to its parent GameState
     * Uses the class State to allow easier A* algorithm.
     * @param currentGameState the current GameState of the board in play
     * @return the number of moves required to solve the game
     */

    public int gridSolver (GameState currentGameState) {
        int NodeExpanded = 0;
        State newState = new State(currentGameState, 0, null);
        PriorityQueue<State> closedSet = new PriorityQueue<State>();
        closedSet.add(newState);
        String startKeyString = makeKey(currentGameState);
        open.put(startKeyString, currentGameState);
        while (!closedSet.isEmpty()) {
            State curState = closedSet.remove();
            NodeExpanded++;
            GameState curGameState = curState.getGameState();
            if (curGameState.validWin()) {
                initializePath(curState);
                open.clear();
                return curState.getDepth();
            }

            ArrayList<ArrayList<Block>> gameMap = curGameState.getGameState();
            for (int row = 0; row < gameMap.size(); row++) {
                for (int col = 0; col < gameMap.size(); col++) {
                    Block block = gameMap.get(row).get(col);
                    if (block == null) continue;
                    if (drawnBlocks.contains(block)) continue;
                    boolean horizontal = block.isHorizontal();
                    int lowestBlock = curGameState.findLowestBlockPoint(horizontal, row, col);
                    int lowerBound = curGameState.getLowerBound(col, row);
                    int higherBound = curGameState.getUpperBound(col, row);
                    for (int i = lowestBlock; i >= lowerBound; i--) {
                        int direction = horizontal ? 3 : 0;
                        GameState possibleGameState = curGameState.cloneGameState(curGameState);
                        possibleGameState.moveBlock(row, col, direction, i - lowerBound);
                        State neighbourState = new State(possibleGameState, curState.getDepth() + 1, curState);
                        neighbourState.setfScore(neighbourState.getDepth() + heuristic(possibleGameState));

                        String curKeyString = makeKey(possibleGameState);
                        if (!open.containsKey(curKeyString)) {
                            closedSet.add(neighbourState);
                            open.put(curKeyString, possibleGameState);

                        }
                    }
                    int length = horizontal ? block.getWidth() : block.getHeight();
                    for (int i = lowestBlock + length; i < higherBound + 1; i++) {

                        int direction = horizontal ? 1 : 2;
                        GameState possibleGameState = curGameState.cloneGameState(curGameState);
                        possibleGameState.moveBlock(row, col, direction, higherBound + 1 - i);
                        State neighbourState = new State(possibleGameState, curState.getDepth() + 1, curState);
                        neighbourState.setfScore(neighbourState.getDepth() + heuristic(possibleGameState));
                        String curKeyString = makeKey(possibleGameState);
                        if (!open.containsKey(curKeyString)) {
                            closedSet.add(neighbourState);
                            open.put(curKeyString, possibleGameState);
                            ;
                        }
                    }
                    drawnBlocks.add(block);
                }
            }
            drawnBlocks.clear();
        }
        return 0;
    }

    /**
     * Creates a path list of GameStates, using the state class, the class has a link to its parent GameState
     * @param curState
     */
    private void initializePath (State curState) {
        if (curState == null) {
            return;
        }
        initializePath(curState.getParent());
        path.add(curState.getGameState());
    }

    /**
     * Used for making a key for the closed set for A* search algorithm, converts the ID blocks of the gameMap into
     * a string.
     * @param curGameState current board of the game
     * @return a String that consists of the IDs in gameMap
     */
    private String makeKey(GameState curGameState) {
        String s = "";
        ArrayList<ArrayList<Block>> gameMap = curGameState.getGameState();
        for (int row = 0; row < gameMap.size(); row++) {
            for (int col = 0; col < gameMap.size(); col++) {
                Block b = gameMap.get(row).get(col);
                if (b == null) {
                    s = s + "0";
                } else {
                    s = s + Integer.toString(b.getID());
                }
            }
        }
        return s;
    }


    /**
     * A heuristic function used in A* search algorithm. The heuristic function uses a value to count how many
     * blocked cars there are infront on the player Car. A good indication of how close we are to the game state and is
     * an admissible heuristic.
     * @param curGameState current GameState of the board
     * @return a heuristic value of blocked cars.
     */
    private int heuristic(GameState curGameState) {
        ArrayList<ArrayList<Block>> gameMap = curGameState.getGameState();
        boolean firstTime = true;
        int lowestBlock = 0;
        int playerRow = curGameState.getPlayerRow();
        for (int col = 0; col < gameMap.size(); col++) {
            if (gameMap.get(playerRow).get(col) == null) continue;
            if (gameMap.get(playerRow).get(col).getID() == -1 && firstTime) {
                lowestBlock = col;
            }
        }
        Block block = gameMap.get(playerRow).get(lowestBlock);
        int blockCars = 0;
        for (int col = lowestBlock; col < gameMap.size(); col++) {

            if (gameMap.get(playerRow).get(col) != null && block != gameMap.get(2).get(col)) {
                blockCars += 1;

            }
        }

        return blockCars;
    }
}
