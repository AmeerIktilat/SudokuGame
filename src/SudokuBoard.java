import java.util.*;

public class SudokuBoard {
    private final int SIZE = 9;
    private int[][] board;
    private int[][] solution;

    public SudokuBoard() {
        board = new int[SIZE][SIZE];
    }

    public int[][] getBoard() {
        return board;
    }

    public int[][] getSolution() {
        return solution;
    }

    public void generatePuzzle(int clues) {
        fillBoard();
        solution = deepCopy(board); // Save full solution
        removeNumbers(81 - clues);  // Create puzzle
    }

    private boolean fillBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == 0) {
                    List<Integer> nums = new ArrayList<>();
                    for (int i = 1; i <= 9; i++) nums.add(i);
                    Collections.shuffle(nums);

                    for (int num : nums) {
                        if (isValidMove(row, col, num)) {
                            board[row][col] = num;
                            if (fillBoard()) return true;
                            board[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private void removeNumbers(int count) {
        Random rand = new Random();
        while (count > 0) {
            int row = rand.nextInt(SIZE);
            int col = rand.nextInt(SIZE);
            if (board[row][col] != 0) {
                board[row][col] = 0;
                count--;
            }
        }
    }

    private int[][] deepCopy(int[][] original) {
        int[][] copy = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, SIZE);
        }
        return copy;
    }

    public boolean isValidMove(int row, int col, int value) {
        for (int i = 0; i < SIZE; i++) {
            if (i != col && board[row][i] == value) return false;
            if (i != row && board[i][col] == value) return false;
        }

        int boxRowStart = (row / 3) * 3;
        int boxColStart = (col / 3) * 3;
        for (int i = boxRowStart; i < boxRowStart + 3; i++) {
            for (int j = boxColStart; j < boxColStart + 3; j++) {
                if ((i != row || j != col) && board[i][j] == value) return false;
            }
        }

        return true;
    }

    public void setValue(int row, int col, int value) {
        if (row >= 0 && row < SIZE && col >= 0 && col < SIZE && value >= 0 && value <= 9) {
            board[row][col] = value;
        }
    }

    public int getValue(int row, int col) {
        return board[row][col];
    }

    public boolean solve() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == 0) {
                    for (int num = 1; num <= 9; num++) {
                        board[row][col] = num;
                        if (isValidMove(row, col, num) && solve()) {
                            return true;
                        }
                        board[row][col] = 0;
                    }
                    return false;
                }
            }
        }
        return true;
    }
}