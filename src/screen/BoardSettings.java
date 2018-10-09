package screen;

import backend.Board;

public class BoardSettings {
    private int maxHints = 0;
    private Board board;
    private boolean aiMode = false;
    private int difficulty = 1;

    public BoardSettings(Board board) {
        this.board = board;
    }
    public void setDifficulty(int difficulty) {
        setAiMode(false);
        this.difficulty = difficulty;
        board.setNewMap(difficulty);
        switch (difficulty) {
            case 1:
                maxHints = 5;
                break;
            case 2:
                maxHints = 3;
                break;
            case 3:
                maxHints = 1;
                break;
        }
    }

    /**
     * Gets maxHints
     *
     * @return value of maxHints
     */
    public int getMaxHints() {
        return maxHints;
    }

    /**
     * Gets aiMode
     *
     * @return value of aiMode
     */
    public boolean isAiMode() {
        return aiMode;
    }

    public void setAiMode(boolean aiMode) {
        this.aiMode = aiMode;
    }

    /**
     * Gets difficulty
     *
     * @return value of difficulty
     */
    public int getDifficulty() {
        return difficulty;
    }
}
