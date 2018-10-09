package backend;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class AI {
    private Solver solver;
    private ArrayList<GameState> possibleMoves;
    private Block changedBlock;
    public AI (Solver solver) {
        this.solver = solver;
        this.possibleMoves = new ArrayList<>();
    }

    /**
     * Asks the AI to make a move. Uses a random number generator to check if its dumb or not. If its not dumb, it grabs
     * the move that will block the player's optimal path. If it is dumb it grabs any random block GameState
     * The AI is not allowed to move the player's previously moved block.
     * @param curGameState current Game state of the board
     * @param prevGameState previous Game State of the board
     * @return a gameState that the AI has decided to move.
     */
    public GameState makeMove (GameState curGameState, GameState prevGameState) {
        boolean smart = false;
        int randomNum = ThreadLocalRandom.current().nextInt(0, 10);
        findPossibleMoves(curGameState);
        if (randomNum < 3) smart = true;
        if (smart == true) {

            GameState MaxGameState = null;
            int findMaxMove = 0;
            this.changedBlock = findBlock(curGameState, prevGameState);
            for (GameState g : possibleMoves) {
                if (haveChangedBlock(curGameState, g)) {
                    continue;
                }
                int curMoves = solver.gridSolver(g);
                if (curMoves > findMaxMove) {
                    findMaxMove = curMoves;
                    MaxGameState = g;
                }
            }

            this.possibleMoves.clear();
            return MaxGameState;
        } else {
            this.changedBlock = findBlock(curGameState, prevGameState);
            for (int i = 0; i < possibleMoves.size(); i++) {
                if (haveChangedBlock(curGameState, possibleMoves.get(i))) {
                    possibleMoves.remove(i);
                }
            }

            int randomIndex = ThreadLocalRandom.current().nextInt(0, possibleMoves.size());
            GameState g = possibleMoves.get(randomIndex);
            this.possibleMoves.clear();
            return g;
        }
    }

    /**
     * Calculates between two gamestates, which block was moved and checks if the block is equal to the block that the player
     * previously moved. If it is, we return true else false
     * @param curGameState current Game State of the board
     * @param possibleGameState possible Game State of the possible movement of the board
     * @return true if block is same block as player's previously moved block else false
     */
    private boolean haveChangedBlock (GameState curGameState, GameState possibleGameState) {
        ArrayList<ArrayList<Block>> gameMap = curGameState.getGameState();
        ArrayList<ArrayList<Block>> possibleMap = possibleGameState.getGameState();
        for (int row = 0; row < gameMap.size(); row++) {
            for (int col = 0; col < gameMap.size(); col++) {
                if (gameMap.get(row).get(col) == null) continue;
                if (changedBlock == null) continue;
                if (gameMap.get(row).get(col) != possibleMap.get(row).get(col)) {
                    if (gameMap.get(row).get(col).getID() == changedBlock.getID()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Finds the block that was previously moved by the player, with respect to the most recent game state.
     *
     * @param curGameState current Game State of the board
     * @param nextGameState previous Game State of the board
     * @return the block object which was moved
     */
    private Block findBlock (GameState curGameState, GameState nextGameState) {
        ArrayList<ArrayList<Block>> gameMap = curGameState.getGameState();
        ArrayList<ArrayList<Block>> nextMap = nextGameState.getGameState();
        for (int row = 0; row < gameMap.size(); row++) {
            for (int col = 0; col < gameMap.size(); col++) {
                if (gameMap.get(row).get(col) != nextMap.get(row).get(col)) {
                    if (gameMap.get(row).get(col) != null) {
                        return gameMap.get(row).get(col);
                    } else {
                        return nextMap.get(row).get(col);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Initialises the possibleMoves array, such that the array includes all the possible movements of which a specific
     * game state can move towards. It uses the properties of lower bound and higherBound to find the distance.
     * @param curGameState current Game State of the board.
     */
    private void findPossibleMoves (GameState curGameState) {
        ArrayList<Block> drawnBlocks = new ArrayList<>();
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
                    if (i - lowerBound == 0) continue;
                    int direction = horizontal ? 3 : 0;
                    GameState possibleGameState = curGameState.cloneGameState(curGameState);
                    possibleGameState.moveBlock(row, col, direction, i - lowerBound);

                    possibleMoves.add(possibleGameState);
                }
                int length = horizontal ? block.getWidth() : block.getHeight();
                for (int i = lowestBlock + length; i < higherBound + 1; i++) {
                    if (higherBound + 1 - i == 0) continue;
                    int direction = horizontal ? 1 : 2;
                    GameState possibleGameState = curGameState.cloneGameState(curGameState);
                    possibleGameState.moveBlock(row, col, direction, higherBound + 1 - i);

                    possibleMoves.add(possibleGameState);
                }
                drawnBlocks.add(block);
            }
        }
    }

}
