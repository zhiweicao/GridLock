package backend;

import java.util.Random;

public class MapGenerator {

    public MapGenerator() {

    }

    /**
     * Creates a randomly generated map with the given difficulty and board size. Difficulty is
     * determined primarily by the minimum number of moves required to solve the puzzle.
     * @param diff difficuly of the puzzle: 1 = easy, 2 = medium, 3 = hard.
     * @param size the size of the map as size x size.
     * @return The newly generated GameState.
     */
    public GameState createRandomMap(int diff, int size) {
        Random rand = new Random();
        GameState currGameState = new GameState();
        int playerRow = 2;
        int playerPos = rand.nextInt(2);
        currGameState.setEmptyMap(size);
        currGameState.addPlayer(playerRow, playerPos);

        int totalBlocks;
        int minMoveCount;
        int maxMoves;

        switch (diff) {
            default:
            case 1:
                totalBlocks = rand.nextInt(3) + 8;
                maxMoves = 12;
                minMoveCount = rand.nextInt(3) + 9;
                break;
            case 2:
                totalBlocks = rand.nextInt(3) + 12;
                maxMoves = 20;
                minMoveCount = rand.nextInt(2) + 13;
                break;
            case 3:
                totalBlocks = rand.nextInt(3) + 20;
                maxMoves = 1000;
                minMoveCount = 20;
                break;
        }
        /*
        // The number of obstacles between player and exit.
        int blockPlayerCount = rand.nextInt(size - (playerPos + 2)) + 1 2;

        // The number of obstacles blocking the obstacles between player and exit.
        int blockObstacleCount = blockPlayerCount + 6;
        */

        //ArrayList<Integer> inPlayerPath = new ArrayList<>();

        int moves = 0;
        int currBlockCount = 0;
        int outerLoop = 0;

        // generate random blocks until we have desired amount
        while (moves < minMoveCount /*&& currBlockCount < totalBlocks*/) {
            outerLoop++;
            //System.out.println(outerLoop);
            if (outerLoop > 10) {
                return createRandomMap(diff, 6);
            } //break;
            GameState newGameState = new GameState();

            /*
            // shuffle random block
            if (currBlockCount > 3 && diff == 3 && rand.nextInt(3) == 0) {
                currGameState = shuffle(currGameState);
            } //else System.out.println("Skip shuffle");
            */

            // Generate random block
            int loops = 0;
            while (currBlockCount < 7) {

                loops++;

                if (loops > 30) {
                    // Make sure we don't get stuck in general.

                    //System.out.println("NO MORE SPACE TO PLACE BLOCKS! " + moves);
                    if (moves < minMoveCount) return createRandomMap(diff, size);
                       /* for (int i = 0; i < rand.nextInt(20) + 5; i++) {
                            shuffle(currGameState);
                        }*/
                    else return currGameState;
                } /*else if (loops > 30) {
                    newGameState = shuffle(currGameState);

                    break;
                }*/

                //System.out.println("Calculating new values");
                int randomRow = rand.nextInt(6);
                int randomCol = rand.nextInt(6);
                int randomHorizontal = rand.nextInt(2);
                int randomLength = rand.nextInt(2) + 2;

                boolean invalid = false;

                //Check if random block is valid
                if (randomHorizontal == 1) {
                    if (randomCol + randomLength > size) continue;
                    if (randomRow == 2) continue;
                    for (int i = 0; i < randomLength; i++) {
                        if (currGameState.validBlock(randomRow, randomCol + i)) invalid = true;
                    }
                } else {
                    if (randomRow + randomLength > size) continue;
                    for (int i = 0; i < randomLength; i++) {
                        if (currGameState.validBlock(randomRow + i, randomCol)) invalid = true;
                    }
                }

                // if valid create block object and exit loop;
                if (invalid == false) {
                    Block newBlock;
                    if (randomHorizontal == 1) {
                        newBlock = new Block(currBlockCount + 1, 1, randomLength, true);
                    } else {
                        newBlock = new Block(currBlockCount + 1, randomLength, 1, false);
                    }

                    newGameState = newGameState.cloneGameState(currGameState);

                    newGameState.setBlock(randomRow, randomCol, newBlock);

                    //  System.out.println("===== ATTEMPTING ADD NEW BLOCK =====");
                    // System.out.println("At ROW:" + randomRow + " COL:" + randomCol + " LEN:" + randomLength + " HORIZ:" + randomHorizontal);
                    break;
                }
            }

            // DETERMINISTICALLY GENERATE BLOCKS
            if (currBlockCount > 6) {
                //System.out.println("Adding...");
                int optRow = 0;
                int optCol = 0;
                int optHoriz = 0;
                int optLength = 0;
                int optMoves = 0;
                Block newBlock;
                for (int dRow = 0; dRow < size; dRow++) {
                    for (int dCol = 0; dCol < size; dCol++) {
                        for (int dHoriz = 0; dHoriz < 2; dHoriz++) {
                            for (int dLength = 2; dLength < 4; dLength++) {

                                boolean invalid = false;
                                if (dHoriz == 1) {
                                    if (dCol + dLength > size) continue;
                                    if (dRow == 2) continue;
                                    for (int i = 0; i < dLength; i++) {
                                        if (currGameState.validBlock(dRow, dCol + i)) invalid = true;
                                    }
                                } else {
                                    if (dRow + dLength > size) continue;
                                    for (int i = 0; i < dLength; i++) {
                                        if (currGameState.validBlock(dRow + i, dCol)) invalid = true;
                                    }
                                }
                                if (invalid) continue;

                                if (dHoriz == 1) {
                                    newBlock = new Block(currBlockCount + 1, 1, dLength, true);
                                } else {
                                    newBlock = new Block(currBlockCount + 1, dLength, 1, false);
                                }
                                newGameState = newGameState.cloneGameState(currGameState);
                                newGameState.setBlock(dRow, dCol, newBlock);
                                Solver solve1 = new Solver();
                                int newMoves = solve1.gridSolver(newGameState);
                                if (newMoves > optMoves && newMoves <= maxMoves) {
                                    optMoves = newMoves;
                                    optCol = dCol;
                                    optRow = dRow;
                                    optHoriz = dHoriz;
                                    optLength = dLength;
                                }
                            }
                        }
                    }
                }

                //System.out.println("!!!!!!!" + optMoves);
                if (optMoves == 0 /*|| optMoves < moves*/) return createRandomMap(diff, size);
                if (optHoriz == 1) {
                    newBlock = new Block(currBlockCount + 1, 1, optLength, true);
                } else {
                    newBlock = new Block(currBlockCount + 1, optLength, 1, false);
                }
                //System.out.println("Deterministically added block at Row:" + optRow + " Col:" + optCol + " Horiz:" + optHoriz + " Length:" + optLength);
                newGameState = newGameState.cloneGameState(currGameState);
                newGameState.setBlock(optRow, optCol, newBlock);
                currGameState = newGameState;
                moves = optMoves;
                currBlockCount++;
            }

            //System.out.println("Checking Solvability");

            // if not solvable discard newGameState and try again. If solvable but doesn't add
            // any moves to puzzle, 2 in 3 chance to discard and try again.


            Solver solve = new Solver();
            int newMoves = solve.gridSolver(newGameState);
            //System.out.println(newMoves);
            if (newMoves == 0 || newMoves > maxMoves) continue;
            boolean tryAgain = false;
            switch (diff) {
                default:
                case 1:
                    if (newMoves <= moves && rand.nextInt(5) != 0) tryAgain = true;
                    break;
                case 2:
                    if (newMoves <= moves + 1 && rand.nextInt(2) != 0) tryAgain = true;
                    break;
                case 3:
                    if (newMoves <= moves) tryAgain = true;
                    break;
            }
            if (tryAgain == true) continue;

            moves = newMoves;
            currGameState = newGameState;
            currBlockCount += 1;
            outerLoop = 0;
            //System.out.println("Added Block " + moves);
            //currGameState.printMap();

        }

        if (moves < minMoveCount) return createRandomMap(diff, size);
        return currGameState;
    }

    /**
     * Randomly shuffle blocks around on the current GameState.
     * @param currGameState The GameState to shuffle
     * @return The shuffled GameState.
     */
    private GameState shuffle(GameState currGameState) {
        int loop = 0;
        Random rand = new Random();
        GameState newGameState;
        Solver solver = new Solver();
        int moves = solver.gridSolver(currGameState);
        //System.out.println("MOVES: " + moves);
        while (true) {

            newGameState = currGameState.cloneGameState(currGameState);
            if (loop > rand.nextInt(10) + 10) return newGameState;
            loop++;
            //System.out.println("Shuffling " + loop);
            Block blockToMove = null;
            int row = 0;
            int col = 0;
            // Pick random block
            while (blockToMove == null) {
                row = rand.nextInt(6);
                col = rand.nextInt(6);
                blockToMove = newGameState.getBlock(row, col);
            }

            // Move up/left
            if (rand.nextBoolean()) {

                //currGameState.printMap();
                int lowerBound = newGameState.getLowerBound(col, row);
                //int pos = blockToMove.isHorizontal() ? col : row;
                int pos = currGameState.findLowestBlockPoint(blockToMove.isHorizontal(), row, col);
                int maxMove = pos - lowerBound;
                //System.out.println("Col: " + lowestPos + " lowerBound: " + lowerBound + "   " + maxMove);

                int distance;
                if (maxMove == 0) continue;
                else if (maxMove == 1) distance = 1;
                else distance = rand.nextInt(maxMove - 1) + 1;
                int direction = blockToMove.isHorizontal() ? 4 : 0;
                //System.out.println("Move by " + distance + " toward " + direction);
                newGameState.moveBlock(row, col, direction, distance);

                // Move down/right
            } else {
                int upperBound = newGameState.getUpperBound(col, row);
                int length = newGameState.getBlockLength(row, col);
                //int pos = blockToMove.isHorizontal() ? col : row;
                int pos = currGameState.findLowestBlockPoint(blockToMove.isHorizontal(), row, col);
                int maxMove = upperBound - (pos + length - 1);
                //System.out.println("Row: " + lowestPos + " length " + length + " upperBound: " + upperBound + "   " + maxMove);

                int distance;
                if (maxMove == 0) continue;
                else if (maxMove == 1) distance = 1;
                else distance = rand.nextInt(maxMove - 1) + 1;
                int direction = blockToMove.isHorizontal() ? 1 : 2;
                //System.out.println("Move by " + distance + " toward " + direction);
                newGameState.moveBlock(row, col, direction, distance);
            }
            solver = new Solver();
            //newGameState.printMap();

            if (solver.gridSolver(newGameState) < moves) continue;
            return newGameState;
            //currGameState.printMap();
        }

    }
}