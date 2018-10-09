package backend;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class GameState {

    private ArrayList<ArrayList<Block>> gameMap;
    private ArrayList<Block> drawnBlocks;

    private int playerRow;
    private Block player;

    /**
     * Constructor
     */
    public GameState () {
        this.gameMap = new ArrayList<>();
        this.drawnBlocks = new ArrayList<>();
        this.playerRow = 2;
        this.player = null;
    }

    /*

        Getters and Setters

     */

    /**
     * getGameState
     * returns the map
     * @return Integer[][]
     */
    public ArrayList<ArrayList<Block>> getGameState () {
        return this.gameMap;
    }


    /**
     * gets the row that the Player is situated in
     * @return int representing playerRow
     */
    public int getPlayerRow() {
        return playerRow;
    }

    /**
     * Retrieves the dimensions of the board
     * @return dimensions of board
     */
    public int getDimensions() {
        return gameMap.size();
    }

    /**
     * Retrieves the Block object at the given x,y coordinates.
     *
     * @param col x coordinate of the block to retrieve.
     * @param row y coordinate of the block to retrieve.
     * @return Block object at this location.
     */
    public Block getBlock(int row, int col) {
        return gameMap.get(row).get(col);
    }


    /**
     * Sets a type of block in a specified location
     * @param row
     * @param col
     * @param b
     */
    public void setBlock(int row, int col, Block b) {
        this.alignBlocks(this.gameMap, row, col, b);
    }

    /**
     * Checks for valid block in location specified
     * @param row
     * @param col
     * @return true if object in location, false if empty
     */
    public boolean validBlock(int row, int col) {
        return gameMap.get(row).get(col) != null;
    }

    /**
     * setGameState
     * sets GameState to a newMap as needed.
     * @param newGameMap
     */
    private void setGameState (ArrayList<ArrayList<Block>> newGameMap) {
        this.gameMap = newGameMap;
    }

    /*

        Logic and Functions

     */
    /**
     * scans pre-defined Map file, and creates a GameState from the map, that builds Block instances
     * and the gameState contains the references to these block instances.
     */
    public void readTutorialMap (String path) {
        Scanner sc = null;
        ArrayList<ArrayList<Integer>> inputMap = new ArrayList<>();
        int i = 0;
        try
        {
            sc = new Scanner(new File(path));    // args[0] is the first command line argument
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] input = line.split("\\s+");
                ArrayList<Integer> row = new ArrayList<>();
                for (String col : input) {
                    System.out.println(col);
                    row.add(Integer.parseInt(col));
                }
                inputMap.add(i, row);
                i++;
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.println(e.getMessage());
        }
        finally
        {
            if (sc != null) sc.close();
        }
        int j;
        ArrayList<ArrayList<Block>> initMap = new ArrayList<>();
        for (i = 0; i < inputMap.size(); i++) {
            ArrayList<Block> b = new ArrayList<>();
            initMap.add(b);
            for (j = 0; j < inputMap.get(i).size(); j++) {
                initMap.get(i).add(null);
            }
        }
        for (i = 0; i < inputMap.size(); i++) {
            for (j = 0; j < inputMap.get(i).size(); j++) {
                if (initMap.get(i).get(j) != null) continue;
                int blockID = inputMap.get(i).get(j);
                if (blockID == -1) {
                    Block p = new Block(blockID, 1, 2, true);
                    this.playerRow = i;
                    this.player = p;
                    alignBlocks(initMap, i, j, p);
                } else if (blockID > 0) {
                    int width = findWidth(inputMap, i, j);
                    int height = findHeight(inputMap, i, j);
                    boolean isHorizontal = false;
                    if (width > height) isHorizontal = true;
                    Block o = new Block(blockID, height, width, isHorizontal);
                    alignBlocks(initMap, i, j, o);
                }
            }
        }
        this.gameMap = initMap;
    }

    /**
     * Align and initialize all pointer references to the specific block b, inside the initGameState map.
     * Only need to cover positive cases as everytime we encounter a block, it must be a new block from map
     * and as we start from upper-left most corner of array, will only encounter blocks that hasn't been initialized
     * yet
     * @param initMap initMap, the inital GameState map to be initalised with references to blocks
     * @param col x co-ordinate of the start of block
     * @param row y co-ordinate of the start of block
     * @param b the block that needs to be referenced from initMap
     */
    private void alignBlocks (ArrayList<ArrayList<Block>> initMap, int row, int col, Block b) {
        boolean horizontal = false;
        if (b.isHorizontal()) horizontal = true;
        int boundary = horizontal ? b.getWidth() : b.getHeight();
        for (int i = 0; i < boundary; i++) {
            if (horizontal) {
                initMap.get(row).set(col + i, b);
            } else {
                initMap.get(row + i).set(col, b);
            }
        }
    }

    /**
     * Finds the width of the to-be block from the specific location from the file.
     * Only need to check positive sides because every time we encounter a block, it must be a new block from the
     * map, and as we start from upper-left most corner of array, we will only encounter blocks in which go down.
     * Else it would of been taken care from a previous iteration.
     * @param inputMap map consisted of integers to be instantiated as blocks
     * @param col x co-ordinate used to find the length of block via width
     * @param row y co-ordinate of the to-be block
     * @return return the width of  the to-b block from inputMap
     */
    private int findWidth(ArrayList<ArrayList<Integer>> inputMap, int row, int col) {
        int width = 0;
        for (int i = col; i < inputMap.size() && inputMap.get(row).get(i) == inputMap.get(row).get(col); i++) {
            width++;
        }
        return width;
    }

    /**
     * Finds the height of the to-be block from the specific location from the file.
     * Only need to check positive sides because every time we encounter a block, it must be a new block from the
     * map, and as we start from upper-left most corner of array, we will only encounter blocks in which go right.
     * Else it would of been taken care from a previous iteration.
     * @param inputMap map consisted of integers to be instantiated as blocks
     * @param col x co-ordinate of the to-be block
     * @param row y co-ordinate used to find the length of block via height
     * @return the height of the to-be block from inputMap
     */
    private int findHeight(ArrayList<ArrayList<Integer>> inputMap, int row, int col) {
        int height = 0;
        for (int i = row; i < inputMap.get(row).size() && inputMap.get(i).get(col) == inputMap.get(row).get(col); i++) {
            height++;
        }
        return height;
    }

    /**
     * copyGameMap
     * replicates and returns a provided GameMap
     * @param oldGameMap
     * @return Integer[][]
     */
    public ArrayList<ArrayList<Block>> copyGameMap ( ArrayList<ArrayList<Block>> oldGameMap) {
        if (oldGameMap.isEmpty()) {
            return null;
        }
        ArrayList<ArrayList<Block>> newGameMap = new ArrayList<>(oldGameMap.size());
        for (int outer = 0; outer < oldGameMap.size(); outer++) {
            newGameMap.add(outer, new ArrayList<Block>());
            for (int inner = 0; inner < oldGameMap.get(outer).size(); inner++) {
                newGameMap.get(outer).add(inner, oldGameMap.get(outer).get(inner));
            }
        }

        return newGameMap;
    }

    /**
     * cloneGameState
     * replicates input GameState
     * @param oldGameState
     * @return GameState
     */
    public GameState cloneGameState ( GameState oldGameState) {
        if (oldGameState == null) {
            return null;
        }
        GameState newGameState = new GameState();
        newGameState.setGameState(copyGameMap(oldGameState.getGameState()));
        newGameState.playerRow = oldGameState.playerRow;
        newGameState.player = oldGameState.player;
        return newGameState;
    }

    /**
     * With the given direction, distance and the original position of block, we move the block in the distance
     * of the given direction in the current GameState.
     * @param col x location of the block in square spaces on the board
     * @param row y location of the block in square spaces on the board
     * @param direction the direction to move it: 0 = up, 1 = right, 2 = down, 3 = left.
     * @param distance the distance in spaces to move it.
     */
    public void moveBlock(int row, int col, int direction, int distance) {
         if (distance == 0) return;
         Block b = getBlock(row, col);

        boolean horizontal = false;
        if (b.isHorizontal()) horizontal = true;
        int lowestBlock = findLowestBlockPoint(horizontal, row, col);
        int boundary = horizontal ? b.getWidth() : b.getHeight();
        for (int i = lowestBlock; i < boundary + lowestBlock; i++) {
            if (horizontal) {
                if (direction == 1) {
                    int index = boundary + lowestBlock - (i - lowestBlock) - 1;
                    gameMap.get(row).set(index + distance, b);
                    gameMap.get(row).set(index, null);
                } else if (direction == 3) {
                    gameMap.get(row).set(i - distance, b);
                    gameMap.get(row).set(i, null);
                }
            } else {
                if (direction == 2) {
                    int index = boundary + lowestBlock - (i - lowestBlock) - 1;
                    gameMap.get(index + distance).set(col, b);
                    gameMap.get(index).set(col, null);
                } else if (direction == 0) {
                    gameMap.get(i - distance).set(col, b);
                    gameMap.get(i).set(col, null);
                }
            }
        }
    }

    /**
     * Finds the lowest point of the specific block, with the given row and col and orientation.
     * The lowest point here denotes the closest point in which the specific block is equal towards the side of 0
     * @param horizontal The orientation of the block
     * @param row The row of the block
     * @param col The col of the block
     * @return lowest point of either row or col (based on orientation)
     */
    public int findLowestBlockPoint(boolean horizontal, int row, int col) {
        int lowestBlock = 0;
        for (int i = horizontal ? col : row; i >= 0; i--) {
            if (horizontal) {
                if (gameMap.get(row).get(i) != gameMap.get(row).get(col)) break;
                lowestBlock = i;
            } else {
                if (gameMap.get(i).get(col) != gameMap.get(row).get(col)) break;
                lowestBlock = i;
            }
        }
        return lowestBlock;
    }

    /**
     * Retrieves the upper (topmost or rightmost for this block, depending on plane of movement)
     * boundary of movement for this block. Returns the boundary as an integer representing the
     * uppermost x or y coordinate this block may move to.
     *
     * @param col x coordinate of the block to retrieve bounds for.
     * @param row y coordinate of the block to retrieve bounds for.
     * @return uppermost position (x or y coordinate) this block may move to.
     */
    public int getUpperBound(int col, int row) {
        Block block = gameMap.get(row).get(col);
        if (block == null) System.out.println("hihih");
        // Find dimension of movement.
        boolean horizontal = false;
        if (block.isHorizontal()) horizontal = true;

        int boundary = horizontal ? gameMap.size() : gameMap.get(0).size();
        for (int i = horizontal ? col : row; i < boundary; i++) {
            if (horizontal) {
                if (gameMap.get(row).get(i) != null && gameMap.get(row).get(i) != block) {
                    return i - 1;
                }
            } else {
                if (gameMap.get(i).get(col) != null && gameMap.get(i).get(col) != block) {

                    return i - 1;
                }
            }

        }

        return boundary - 1;
    }

    /**
     * Retrieves the lower (bottom-most or leftmost for this block, depending on plane of movement)
     * boundary of movement for this block. Returns the boundary as an integer representing the
     * uppermost column or row coordinate this block may move to. coordinates start from 0 in top-left.
     *
     * @param col column coordinate of the block to retrieve bounds for.
     * @param row row coordinate of the block to retrieve bounds for.
     * @return lowermost position (col or row coordinate) this block may move to.
     */
    public int getLowerBound(int col, int row) {
        Block block = gameMap.get(row).get(col);

        boolean horizontal = false;
        if (block.isHorizontal()) horizontal = true;

        for (int i = horizontal ? col : row; i >= 0; i--) {
            if (horizontal) {
                if (gameMap.get(row).get(i) != null && gameMap.get(row).get(i) != block) {
                    return i + 1;
                }
            } else {
                if (gameMap.get(i).get(col) != null && gameMap.get(i).get(col) != block) {
                    return i + 1;
                }
            }

        }
        return 0;
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
    public boolean isSeen(int col, int row) {
        boolean isSeen = true;
        Block block = gameMap.get(row).get(col);
        if (block != null) {
            System.out.println(drawnBlocks.toString());
            if (drawnBlocks.contains(block)) {
                isSeen = true;
            } else {
                drawnBlocks.add(block);
                isSeen = false;
            }
        }
        if (row == gameMap.size() - 1 && col == gameMap.get(0).size() - 1) {
            drawnBlocks.clear();
        }

        return isSeen;
    }

    /**
     * Retrieves the horizontal state of the particular block located at x and y in gameMap
     * @param col x coordinate of the block to retrieve information from
     * @param row y coordinate of the block to retrieve information from
     * @return horizontal nature of block
     */
    public Boolean getBlockHorizontal(int row, int col) {
        return this.gameMap.get(row).get(col).isHorizontal();
    }

    /**
     * Retrieves the block length of the particular block located at x and y in gameMap
     * @param col x coordinate of the block to retrieve information from
     * @param row y coodrinate of the block to retrieve information from
     * @return length of the block
     */
    public int getBlockLength(int row, int col) {
        Block block = gameMap.get(row).get(col);
        if (block == null) {
            return -1;
        }
        if (gameMap.get(row).get(col).isHorizontal()) {
            return gameMap.get(row).get(col).getWidth();
        } else {
            return gameMap.get(row).get(col).getHeight();
        }
    }

    /**
     * Checks to see if this is a victory gameState, i.e. if the player is in the winning position
     *
     * @return true if player has won, false if not.
     */
    public boolean validWin() {
        int lastCol = gameMap.get(0).size() - 1;
        if (player == null)return false;
        Block endBlock = gameMap.get(playerRow).get(lastCol);
        if (endBlock == null)  return false;

        return endBlock.equals(player);
    }

    /**
     * Checks if the block at the given coordinates is the player.
     *
     * @param row the row of the block to check, starting from 0 at the top.
     * @param col The column of the block to check, starting from 0 on the left.
     * @return True if it's the player, false otherwise.
     */
    public boolean isPlayer(int row, int col) {
        if (gameMap.get(row).get(col) == null) return false;
        if (gameMap.get(row).get(col).equals(player)) return true;
        return false;
    }

    /**
     * Generates a clear map based on an inputted size of (size x size)
     * @param size
     */
    public void setEmptyMap(int size) {
        this.gameMap = new ArrayList<>();
        ArrayList<Block> emptyRow = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            emptyRow.add(null);
        }
        for (int i = 0; i < size; i++) {
            ArrayList<Block> newRow = new ArrayList<>(emptyRow);
            gameMap.add(newRow);
        }
    }


    /**
     * Add player at indicated location
     * @param row
     * @param pos
     */
    public void addPlayer(int row, int pos) {
        Block player = new Block(-1, 1, 2, true);
        this.player = player;
        this.playerRow = row;
        alignBlocks(this.gameMap, row, pos, player);
    }

    /*
        Debuggers
     */

    /**
     * Used for debugging
     * prints map in console
     * currently switched off the print function
     */
    public void printMap () {
        for (int i = 0; i < gameMap.size(); i++) {
            for (int j = 0; j < gameMap.get(i).size(); j++) {
                if (gameMap.get(i).get(j) == null) {
//                    System.out.print("0");
                    continue;
                }
//                System.out.print(gameMap.get(i).get(j).getID());
            }
//            System.out.println("");
        }
    }


}


